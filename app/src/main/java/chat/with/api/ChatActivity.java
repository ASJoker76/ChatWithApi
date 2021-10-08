package chat.with.api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
//import chat.with.api.interfaces.OnAskToLoadMoreCallback;
//import chat.with.api.views.MessagesWindow;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

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
    int id_room;
    TextView txt_username_penerima;
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

                //messagesWindow.sendMessage(message.getText().toString());
                kirimpesan(id_room,message.getText().toString(),username_pengirim,"12:00",username_penerima);
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

        Pusher pusher = new Pusher(getString(R.string.key), options);

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

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.i("Pusher", "Received event with data: " + event.toString());
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
                onload(id_room,username_pengirim,username_penerima);
            }

            @Override
            public void onFailure(Call<ResRoom> call, Throwable t) {
                pDialog.dismissWithAnimation();
                t.printStackTrace();
                new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ada Kesalahan Sistem")
                        .show();
            }
        });
    }

    private void kirimpesan(int id_room_,String chat,String pengirim,String waktu,String penerima) {
//        ReqKirimChat reqKirimChat = new ReqKirimChat();
//        reqKirimChat.setChat(chat);
//        reqKirimChat.setUsr_pengirim(pengirim);
//        reqKirimChat.setWaktu_chat(waktu);
//        reqKirimChat.setUsr_penerima(penerima);
        Call<ResUtama> callKirimChat = API.service().kirimPesanRequest(id_room_,chat,pengirim,waktu,penerima);
        callKirimChat.enqueue(new Callback<ResUtama>() {
            @Override
            public void onResponse(Call<ResUtama> call, Response<ResUtama> response) {
                Log.d("Log Chat", response.code() + "");
                if (response.code() == 200) {

                    //ResUtama resChat = response.body();
                    messagesWindow.sendMessage(chat);
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
                        .setTitleText("Ada Kesalahan Sistem")
                        .show();
            }
        });
    }

    private void onload(int id_room,String username_pengirim_,String username_penerima_) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<ResChat> callChat = API.service().ambilpesanbynama(id_room,username_pengirim_,username_penerima_);
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
                                messagesWindow.sendMessage(resDetailChat.getChat());
                            }
                            else if (resDetailChat.getUsr_pengirim().equals(username_penerima)||resDetailChat.getUsr_penerima().equals(username_pengirim)){
                                messagesWindow.receiveMessage(resDetailChat.getChat());
                            }
                        }
                    }
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
                        .setTitleText("Ada Kesalahan Sistem")
                        .show();
            }
        });
    }

    private List<Message> getMessages(String identification) {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            Message message = new Message(identification + "message " + i, i % 3 == 0 ? Message.THIS_SIDE : Message.THAT_SIDE);
            message.setTime("7:20 pm");
            messages.add(message);
        }
        return messages;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}