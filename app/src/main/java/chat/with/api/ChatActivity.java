package chat.with.api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
//import chat.with.api.interfaces.OnAskToLoadMoreCallback;
//import chat.with.api.views.MessagesWindow;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;

//import com.google.firebase.messaging.RemoteMessage;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import com.samsao.messageui.interfaces.OnAskToLoadMoreCallback;
import com.samsao.messageui.views.MessagesWindow;
import com.samsao.messageui.models.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import chat.with.api.connection.API;

import chat.with.api.model.req.ReqKirimChat;
import chat.with.api.model.res.ResChat;
import chat.with.api.model.res.ResDetailChat;
import chat.with.api.model.res.ResRoom;
import chat.with.api.model.res.ResUtama;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final int MESSAGE_COUNT = 5;
    private MessagesWindow messagesWindow;
    String username_penerima,username_pengirim;
    String id_room;
    TextView txt_username_penerima;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    //there can be multiple notifications so it can be used as notification identity
    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences prefs = getBaseContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        username_pengirim = prefs.getString("user","");
        username_penerima = getIntent().getStringExtra("username_penerima");

        messagesWindow = (MessagesWindow) findViewById(R.id.customized_messages_window);
        final EditText message = messagesWindow.getWritingMessageView().findViewById(R.id.message_box_text_field);

        txt_username_penerima = findViewById(R.id.txt_username_penerima);
        txt_username_penerima.setText(username_penerima);

        //pusher();

        cekroom(username_pengirim,username_penerima);
        //onload(username_pengirim,username_penerima);

//        List<Message> messages = new ArrayList<>();
//        Message messaged = new Message(":)", Message.THIS_SIDE);
//        messaged.setTime("7:20 pm");
//        messages.add(messaged);
//        messagesWindow.loadMessages(messages);
//        messagesWindow.setOnAskToLoadMoreCallback(new OnAskToLoadMoreCallback() {
//            @Override
//            public void onAskToLoadMore() {
//                List<Message> messages = getMessages("");
//                if (messages.isEmpty()) {
//                    messagesWindow.removeOnAskToLoadMoreCallback();
//                } else {
//                    messagesWindow.loadOldMessages(messages);
//                }
//            }
//        });

        message.setHint("Type Here.....");
        messagesWindow.setBackgroundResource(R.drawable.gray_button_background);
        messagesWindow.getWritingMessageView().findViewById(R.id.message_box_button).setPadding(5,5,5,5);
        messagesWindow.getWritingMessageView().findViewById(R.id.message_box_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                //messagesWindow.sendMessage(message.getText().toString());
                kirimpesan(id_room,message.getText().toString(),username_pengirim,currentTime,username_penerima);
//code python instant
                //messagesWindow.receiveMessage(message.getText().toString());
                //messagesWindow.setTimestampMode(MessagesWindow.ALWAYS_AFTER_BALLOON);
//                messagesWindow.setTimestampMode(MessagesWindow.INSIDE_BALLOON_TOP);
//                messagesWindow.setTimestampInsideBalloonGravity(MessagesWindow.TIMESTAMP_INSIDE_BALLOON_GRAVITY_CORNER);
                message.setText("");
            }
        });


    }

    private void pusher() {
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
                    nama= jsonObject.getString("name");
                    pesan= jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mBuilder = new NotificationCompat.Builder(ChatActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle(event.getData())
                        .setContentText("Test")
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                mNotificationManager = (NotificationManager) ChatActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    assert mNotificationManager != null;
                    mBuilder.setChannelId(CHANNEL_ID);
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }
                assert mNotificationManager != null;
                mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
                //END NOTIF
            }
        });
    }

    private void cekroom(String username_pengirim, String username_penerima) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<ResRoom> callChat = API.service().cekroom(username_pengirim,username_penerima);
        callChat.enqueue(new Callback<ResRoom>() {
            @Override
            public void onResponse(Call<ResRoom> call, Response<ResRoom> response) {
                pDialog.dismissWithAnimation();
                Log.d("Log Chat", response.code() + "");
                ResRoom resRoom = response.body();
                id_room = resRoom.getId_room();
                onload(id_room);
            }

            @Override
            public void onFailure(Call<ResRoom> call, Throwable t) {
                pDialog.dismissWithAnimation();
                t.printStackTrace();
                new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Tidak Ada Koneksi"+ "")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                cekroom(username_pengirim,username_penerima);
                            }
                        })
                        .show();
            }
        });
    }

    private void kirimpesan(String id_room_,String chat,String pengirim,String waktu,String penerima) {
        Call<ResUtama> callKirimChat = API.service().kirimPesanRequest(id_room_,chat,pengirim,waktu,penerima);
        callKirimChat.enqueue(new Callback<ResUtama>() {
            @Override
            public void onResponse(Call<ResUtama> call, Response<ResUtama> response) {
                Log.d("Log Chat", response.code() + "");
                if (response.code() == 200) {

                    //ResUtama resChat = response.body();
                    messagesWindow.sendMessage(chat +"\n" + waktu);
                    //showNotification();
                }
                else{

                    new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(response.code()+ "")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResUtama> call, Throwable t) {
                t.printStackTrace();
                new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Pesan Tidak Terkirim, Periksa Koneksi Internet")
                        .show();
            }
        });
    }

    private void showNotification() {

        createNotificationChannel();

        //inflating the views (custom_normal.xml and custom_expanded.xml)
        RemoteViews remoteCollapsedViews = new RemoteViews(getPackageName(), R.layout.custom_normal);
        RemoteViews remoteExpandedViews = new RemoteViews(getPackageName(), R.layout.custom_expanded);

        //start this(MainActivity) on by Tapping notification
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPIntent = PendingIntent.getActivity(this, 0,
                mainIntent, PendingIntent.FLAG_ONE_SHOT);

        //Click Like button to start LikeActivity
        Intent likeIntent = new Intent(this, ChatActivity.class);
        likeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent likePIntent = PendingIntent.getActivity(this, 0, likeIntent, PendingIntent.FLAG_ONE_SHOT);

        //creating notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        //icon
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
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
        builder.addAction(R.drawable.ic_sent, "Kirim", likePIntent);

        //notification manager
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "My Notification";
            String description = "My notification description";
            //importance of your notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;


            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void onload(String id_room) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<ResChat> callChat = API.service().ambilpesanbynama(id_room);
        callChat.enqueue(new Callback<ResChat>() {
            @Override
            public void onResponse(Call<ResChat> call, Response<ResChat> response) {
                Log.d("Log Chat", response.code() + "");
                if (response.code() == 200) {
                    pDialog.dismissWithAnimation();
                    ResChat resChat = response.body();
                    Log.d("isi",resChat.getDataChat()+"");
                    if(resChat.getKode()==200){
                        for(int i=0;i<resChat.getDataChat().size();i++){
                            ResDetailChat resDetailChat = resChat.getDataChat().get(i);
                            if(resDetailChat.getUsr_pengirim().equals(username_pengirim)||resDetailChat.getUsr_penerima().equals(username_penerima)){
                                messagesWindow.sendMessage(resDetailChat.getChat()+"\n" + resDetailChat.getWaktu_chat());
                            }
                            else if (resDetailChat.getUsr_pengirim().equals(username_penerima)||resDetailChat.getUsr_penerima().equals(username_pengirim)){
                                messagesWindow.receiveMessage(resDetailChat.getChat()+"\n" + resDetailChat.getWaktu_chat());
                            }
                        }
                    }
                    //pusher();
                }
                else{
                    pDialog.dismissWithAnimation();
                    new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(response.code()+ "")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResChat> call, Throwable t) {
                t.printStackTrace();
                pDialog.dismissWithAnimation();
                new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Tidak Ada Koneksi"+ "")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                onload(id_room);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}