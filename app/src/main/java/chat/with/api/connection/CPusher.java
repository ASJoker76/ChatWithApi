package chat.with.api.connection;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
//                Channel channel = pusher.subscribe("my-channel");
//                channel.bind("my-event", new SubscriptionEventListener() {
//                    @Override
//                    public void onEvent(PusherEvent event) {
//
//                    }
//
//                    @Override
//                    public void onError(String message, Exception e) {
//
//                    }
//                });
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
                String nama="";
                String pesan="";
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    nama= jsonObject.getString("nama_pengirim");
                    pesan= jsonObject.getString("pesan");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                createNotificationChannel();
                //inflating the views (custom_normal.xml and custom_expanded.xml)
                RemoteViews remoteCollapsedViews = new RemoteViews(getPackageName(), R.layout.custom_normal);
                RemoteViews remoteExpandedViews = new RemoteViews(getPackageName(), R.layout.custom_expanded);

                remoteCollapsedViews.setTextViewText(R.id.txt_judul, nama);
                remoteCollapsedViews.setTextViewText(R.id.txt_isi_pesan, pesan);

                remoteExpandedViews.setTextViewText(R.id.txt_judul, nama);
                remoteExpandedViews.setTextViewText(R.id.txt_isi_pesan, pesan);
                //start this(MainActivity) on by Tapping notification
                Intent mainIntent = new Intent(CPusher.this, ChatActivity.class);
                mainIntent.putExtra("username_penerima", nama);
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
//                builder.setContentTitle(data);
//                builder.setContentText("Test");
                builder.setAutoCancel(false);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                //notification manager
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(CPusher.this);
                notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

                //init Android Vibrator API
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(2000);
                }
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//                {
//                    int importance = NotificationManager.IMPORTANCE_HIGH;
//                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
//                    notificationChannel.enableLights(true);
//                    notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.enableVibration(true);
//                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//                    assert mNotificationManager != null;
//                    mBuilder.setChannelId(CHANNEL_ID);
//                    mNotificationManager.createNotificationChannel(notificationChannel);
//                }
//                assert mNotificationManager != null;
//                mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
//                //END NOTIF
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
