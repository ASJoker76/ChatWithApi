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
    MaterialButton btn_register,btn_login;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txt_username.getText().toString();
                password = txt_password.getText().toString();
                register(username,password);
                //pindah();
            }
        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pindah();
            }
        });
    }

    private void pindah() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void register(String username,String password) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<ResUtama> callRegister = API.service().registerRequest(username,password);
        callRegister.enqueue(new Callback<ResUtama>() {
            @Override
            public void onResponse(Call<ResUtama> call, Response<ResUtama> response) {
                Log.d("Log Chat", response.code() + "");
                ResUtama resResUtama = response.body();
                if (response.code() == 200) {
                    pDialog.dismissWithAnimation();

                    //messagesWindow.sendMessage(chat);
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(resResUtama.getMessage()+ "")
                            .show();

                    clear();
                }
                else{
                    pDialog.dismissWithAnimation();
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(resResUtama.getMessage()+ "")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResUtama> call, Throwable t) {
                t.printStackTrace();
                pDialog.dismissWithAnimation();
                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ada Kesalahan Sistem")
                        .show();
            }
        });
    }

    private void clear() {
        txt_username.setText("");
        txt_password.setText("");
    }
}