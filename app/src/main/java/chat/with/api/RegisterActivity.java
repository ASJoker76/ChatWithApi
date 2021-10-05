package chat.with.api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

import chat.with.api.connection.API;
import chat.with.api.model.req.ReqRegister;
import chat.with.api.model.res.ResUtama;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText txt_username,txt_password,txt_nama_lengkap;
    MaterialButton btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);
        txt_nama_lengkap = findViewById(R.id.txt_nama_lengkap);

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //register(txt_username.getText().toString(),txt_password.getText().toString(),txt_nama_lengkap.getText().toString());
                pindah();
            }
        });
    }

    private void pindah() {
        Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
        finish();
        startActivity(intent);
    }

    private void register(String username,String password,String namalengkap) {

//        ReqRegister reqRegister = new ReqRegister();
//        reqRegister.setUsername(username);
//        reqRegister.setPassword(password);
//        reqRegister.setNama_lengkap(nama_lengkap);
        Call<ResUtama> callRegister = API.service().registerRequest(username,password,namalengkap);
        callRegister.enqueue(new Callback<ResUtama>() {
            @Override
            public void onResponse(Call<ResUtama> call, Response<ResUtama> response) {
                Log.d("Log Chat", response.code() + "");
                if (response.code() == 200) {

                    //ResUtama resChat = response.body();
                    //messagesWindow.sendMessage(chat);
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(response.code()+ "")
                            .show();
                }
                else{

                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(response.code()+ "")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResUtama> call, Throwable t) {
                t.printStackTrace();
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ada Kesalahan Sistem")
                        .show();
            }
        });
    }
}