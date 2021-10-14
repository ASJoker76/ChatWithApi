package chat.with.api.connection;

import static chat.with.api.utils.AESEncyption.decrypt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.app.Service;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import chat.with.api.ChatActivity;
import chat.with.api.R;

public class ForeBackground extends Service {

    Timer timer;
    TimerTask timerTask;
    String TAG = "Timers";
    int Your_X_SECS = 5;
    //there can be multiple notifications so it can be used as notification identity
    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;
    private Vibrator vibrator;
    public static final String NOTIFICATION_REPLY = "NotificationReply";
    String dec;
    private Pusher pusher;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        createNotificationChannel();
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stoptimertask();
        super.onDestroy();
    }

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, Your_X_SECS * 1000); //
        //timer.schedule(timerTask, 5000,1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {

                        //TODO CALL NOTIFICATION FUNC
                        pusher();

                    }
                });
            }
        };
    }

    public void pusher(){
        try {
            PusherOptions options = new PusherOptions();
            options.setCluster(getString(R.string.cluster));

            //Pusher pusher = new Pusher(getString(R.string.key), options);
            pusher = new Pusher("1503f658e5c89d8da00e", options);

            pusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    Log.i("Pusher", "State changed from " + change.getPreviousState() +
                            " to " + change.getCurrentState());
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    Log.i("Pusher", "There was a problem connecting! " +
                            "\ncode: " + code +
                            "\nmessage: " + message +
                            "\nException: " + e
                    );
                }
            }, ConnectionState.ALL);

            Channel channel = pusher.subscribe("my-channel");

            channel.bind("Chat With Api", new SubscriptionEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    Log.i("Pusher", "Received event with data: " + event.toString());

//                showNotification();
                    //NOTIFICATION
                    String data = event.getData();
                    String waktu_dapet="";
                    String nama_pengirim="";
                    String nama_penerima="";
                    String pesan="";
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        nama_penerima= jsonObject.getString("nama_penerima");
                        nama_pengirim= jsonObject.getString("nama_pengirim");
                        pesan= jsonObject.getString("pesan");
                        try {
                            dec = decrypt(pesan);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences prefs = getBaseContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                    String usr = prefs.getString("user","");

                    if(nama_penerima.equals(usr)){
                        createNotificationChannel();
                        //inflating the views (custom_normal.xml and custom_expanded.xml)
                        RemoteViews remoteCollapsedViews = new RemoteViews(getPackageName(), R.layout.custom_normal);
                        RemoteViews remoteExpandedViews = new RemoteViews(getPackageName(), R.layout.custom_expanded);

                        remoteCollapsedViews.setTextViewText(R.id.txt_judul, nama_pengirim);
                        remoteCollapsedViews.setTextViewText(R.id.txt_isi_pesan, dec);

                        remoteExpandedViews.setTextViewText(R.id.txt_judul, nama_pengirim);
                        remoteExpandedViews.setTextViewText(R.id.txt_isi_pesan, dec);
                        //start this(MainActivity) on by Tapping notification
                        Intent mainIntent = new Intent(ForeBackground.this, ChatActivity.class);
                        mainIntent.putExtra("username_penerima", nama_pengirim);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent mainPIntent = PendingIntent.getActivity(ForeBackground.this, 0,
                                mainIntent, PendingIntent.FLAG_ONE_SHOT);

                        //creating notification
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ForeBackground.this, CHANNEL_ID);
                        //icon
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        //set priority
                        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        //dismiss on tap
                        builder.setAutoCancel(true);
                        //start intent on notification tap (MainActivity)
                        builder.setContentIntent(mainPIntent);
                        //custom style
                        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
                        builder.setCustomContentView(remoteCollapsedViews);
                        builder.setCustomBigContentView(remoteExpandedViews);
                        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);


                        /* Set Light */
                        builder.setLights(Color.RED, 500,500);
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ForeBackground.this);
                        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        if(alarmSound == null){
                            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                            if(alarmSound == null){
                                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            }
                        }
                        builder.setSound(alarmSound);
                        builder.setDefaults(Notification.DEFAULT_SOUND);

//                        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY)
//                                .setLabel("Please enter your name")
//                                .build();
//
//                        NotificationCompat.Action action =
//                                new NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
//                                        "Reply Now...", mainPIntent)
//                                        .addRemoteInput(remoteInput)
//                                        .build();

                        //init Android Vibrator API
                        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(1000);
                        }

                        final Notification notification = builder.build();
                        // notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
                        // notification.flags |= Notification.FLAG_NO_CLEAR;
                        // notification.flags |= Notification.FLAG_ONGOING_EVENT;

                        startForeground(NOTIFICATION_ID, notification);
                    }
                }
            });
        }
        catch (Exception e){

        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence name = "My Notification";
            String description = "My notification description";
            //importance of your notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
