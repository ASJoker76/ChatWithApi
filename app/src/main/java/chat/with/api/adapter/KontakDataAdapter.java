package chat.with.api.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import chat.with.api.ChatActivity;
import chat.with.api.LoginActivity;
import chat.with.api.MainActivity;
import chat.with.api.R;
import chat.with.api.model.res.ResUserDetail;

public class KontakDataAdapter extends RecyclerView.Adapter<KontakDataAdapter.MyViewHolder> {

    private Activity myFragment;
    private LayoutInflater inflater;
    private ArrayList<ResUserDetail> imageModelArrayList;

    public KontakDataAdapter(Activity fragment, ArrayList<ResUserDetail> imageModelArrayList) {

        this.myFragment = fragment;
        this.imageModelArrayList = imageModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kontak, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_username.setText(imageModelArrayList.get(position).getUsername());
        holder.layar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myFragment, ChatActivity.class);
                intent.putExtra("username_penerima", imageModelArrayList.get(position).getUsername());
                myFragment.finish();
                myFragment.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layar;
        TextView txt_username;

        public MyViewHolder(View itemView) {
            super(itemView);
            layar        = (LinearLayout) itemView.findViewById(R.id.ly_id);
            txt_username = (TextView) itemView.findViewById(R.id.txt_username);
        }

    }
}
