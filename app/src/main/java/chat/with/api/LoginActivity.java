package chat.with.api;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

import chat.with.api.connection.API;
import chat.with.api.model.res.ResLogin;
import chat.with.api.model.res.ResUtama;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText txt_username,txt_password;
    MaterialButton btn_register,btn_login;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ceksession();

        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();
                login(username,password);
            }
        });

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pindah();
            }
        });
    }

    private void ceksession() {
        SharedPreferences prefs = getBaseContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        String usr = prefs.getString("user","");
        if(usr.equals("")){

        }
        else{
            pindahkehome();
        }
    }

    private void login(String username,String password) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<ResLogin> callRegister = API.service().loginRequest(username,password);
        callRegister.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                Log.d("Log Chat", response.code() + "");
                ResLogin resResUtama = response.body();
                if (response.code() == 200) {
                    pDialog.dismissWithAnimation();

                    SharedPreferences.Editor editor = getBaseContext().getSharedPreferences("login",Context.MODE_PRIVATE).edit();
                    editor.putString("user",resResUtama.getUsername());
                    editor.apply();

                    //messagesWindow.sendMessage(chat);
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(resResUtama.getMessage()+ "")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    pindahkehome();
                                }
                            })
                            .show();
                }
                else{
                    pDialog.dismissWithAnimation();
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(resResUtama.getMessage()+ "")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                t.printStackTrace();
                pDialog.dismissWithAnimation();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ada Kesalahan Sistem")
                        .show();
            }
        });
    }

    private void pindah() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void pindahkehome(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}