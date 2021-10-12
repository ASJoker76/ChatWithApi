package chat.with.api.connection;

import static android.provider.Settings.System.getUriFor;
import static chat.with.api.utils.AESEncyption.decrypt;

import android.app.Application;
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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;

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

import chat.with.api.ChatActivity;
import chat.with.api.MainActivity;
import chat.with.api.R;

public class CPusher extends Application {

    public static final String TAG = CPusher.class.getSimpleName();
    private static CPusher mInstance;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    //there can be multiple notifications so it can be used as notification identity
    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;
    private Vibrator vibrator;
    public static final String NOTIFICATION_REPLY = "NotificationReply";
    String dec;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        PusherOptions options = new PusherOptions();
        options.setCluster(getString(R.string.cluster));

        //Pusher pusher = new Pusher(getString(R.string.key), options);
        Pusher pusher = new Pusher("1503f658e5c89d8da00e", options);

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
                //showNotification();
                //NOTIFICATION
                String data = event.getData();
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
//                    try {
//                        String dec = decrypt(pesan);
//                        remoteCollapsedViews.setTextViewText(R.id.txt_isi_pesan, dec);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    remoteCollapsedViews.setTextViewText(R.id.txt_isi_pesan, dec);

                    remoteExpandedViews.setTextViewText(R.id.txt_judul, nama_pengirim);
//                    try {
//                        String dec = decrypt(pesan);
//                        remoteExpandedViews.setTextViewText(R.id.txt_isi_pesan, dec);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    remoteExpandedViews.setTextViewText(R.id.txt_isi_pesan, dec);
                    //start this(MainActivity) on by Tapping notification
                    Intent mainIntent = new Intent(CPusher.this, ChatActivity.class);
                    mainIntent.putExtra("username_penerima", nama_pengirim);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent mainPIntent = PendingIntent.getActivity(CPusher.this, 0,
                            mainIntent, PendingIntent.FLAG_ONE_SHOT);

                    //creating notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(CPusher.this, CHANNEL_ID);
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
                    /* Set Light */
                    builder.setLights(Color.RED, 500,500);
                    /* Vibrate pattern */
                    //long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
                    //builder.setStyle(new NotificationCompat.InboxStyle());
//                  builder.setContentTitle(data);
//                  builder.setContentText("Test");
                    //builder.setAutoCancel(true);
                    //builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                    //builder.setDefaults(Notification.DEFAULT_SOUND);
                    //notification manager
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(CPusher.this);
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

                    RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY)
                            .setLabel("Please enter your name")
                            .build();

                    NotificationCompat.Action action =
                            new NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
                                    "Reply Now...", mainPIntent)
                                    .addRemoteInput(remoteInput)
                                    .build();

                    //init Android Vibrator API
                    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(1000);
                    }
                }
            }
        });
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
