package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.RatingReview;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.RideDetailActivity;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EmergencyContactRVAdapter extends RecyclerView.Adapter<EmergencyContactRVAdapter.MyViewHolder> {
    private static final String TAG = "EmergencyContactRVAdapt";

    private Context mContext;
    private List<String> contacts;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListTitle;

        public MyViewHolder(View view) {
            super(view);
            tvListTitle = (TextView) view.findViewById(R.id.tvListTitle);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: " + contacts.get(getAdapterPosition()));
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:123456789"));
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    }
                    mContext.startActivity(callIntent);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d(TAG, "onLongClick: "+ contacts.get(getAdapterPosition()));
                    return true;
                }
            });
        }
    }


    public EmergencyContactRVAdapter(Context context, List<String> contacts) {
        this.mContext = context;
        this.contacts = contacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_text_only, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String contact = this.contacts.get(position);
        holder.tvListTitle.setText(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}
