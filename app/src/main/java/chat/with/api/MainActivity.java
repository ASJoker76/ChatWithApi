package chat.with.api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/*import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;*/
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import chat.with.api.adapter.KontakDataAdapter;
import chat.with.api.connection.API;
import chat.with.api.model.res.ResDetailChat;
import chat.with.api.model.res.ResUser;
import chat.with.api.model.res.ResUserDetail;
import chat.with.api.utils.GridSpacingItemDecoration;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv_list_kontak;
    private KontakDataAdapter adapters;
    private List<ResUserDetail> resListProjects;
    private ArrayList<ResUserDetail> resListProjectArrayList;
    TextView txt_username_pengirim;
    String username_pengirim;
    MaterialButton btn_logout;

    private static final String TAG = "MainActivity";

    //private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        loaddata();
        populatelist();
        SharedPreferences prefs = getBaseContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        username_pengirim = prefs.getString("user","");
        txt_username_pengirim = findViewById(R.id.txt_username_pengirim);
        txt_username_pengirim.setText(username_pengirim);
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("login", 0);
                preferences.edit().remove("user").commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void populatelist() {
        Call<ResUser> getListCall = API.service().userRequest();
        getListCall.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                Log.d("res code",response.code()+"");
                if (response.code() == 200) {
                    ResUser resUser = response.body();
                    resListProjects = resUser.getDataUser();
                    resListProjectArrayList.addAll(resListProjects);
                    adapters.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                t.printStackTrace();
                //progress.setVisibility(View.GONE);
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Tidak Ada Koneksi"+ "")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                populatelist();
                            }
                        })
                        .show();
            }
        });
    }

    private void loaddata() {
        // tampil data recycler view
        int numberOfColumns = 1;
        rv_list_kontak = (RecyclerView) findViewById(R.id.rv_list_kontak);
        //recyclerViewInvenTory.setHasFixedSize(true);
        resListProjectArrayList = new ArrayList<>();
        adapters = new KontakDataAdapter(MainActivity.this, resListProjectArrayList);
        rv_list_kontak.setAdapter(adapters);
        rv_list_kontak.setLayoutManager(new GridLayoutManager(MainActivity.this, numberOfColumns,GridLayoutManager.VERTICAL, false));
        rv_list_kontak.addItemDecoration(new GridSpacingItemDecoration(2, 2,true,2));
    }
}