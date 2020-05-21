package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.Notification;
import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.RideDetailActivity;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.ViewProfile;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RequesterRVAdapter extends RecyclerView.Adapter<RequesterRVAdapter.MyViewHolder> {
    private static final String TAG = "RequesterRVAdapter";

    private Context mContext;
    private List<SharedRide> requestedSharedRides;
    private String mPr_id, mPr_providerUid, mCurrentUserProfileName;
    private int mCapacity;
    private List<SharedRide> mAcceptedSharedRide;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvRequesterName, tvNote, tvTime;
        public ImageView ivRequesterImg, btnAccept, btnReject;

        public MyViewHolder(View view) {
            super(view);
            tvRequesterName = (TextView) view.findViewById(R.id.tvRequesterName);
            tvNote = (TextView) view.findViewById(R.id.tvNote);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            ivRequesterImg = (ImageView) view.findViewById(R.id.ivRequesterImg);
            btnAccept = (ImageView) view.findViewById(R.id.btnAccept);
            btnReject = (ImageView) view.findViewById(R.id.btnReject);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }


    public RequesterRVAdapter(Context context, List<SharedRide> requestedSharedRides, String pr_id, String pr_providerUid, String currentUserProfileName, int capacity){
        this.mContext = context;
        this.requestedSharedRides = requestedSharedRides;
        this.mPr_id = pr_id;
        this.mPr_providerUid = pr_providerUid;
        this.mCurrentUserProfileName = currentUserProfileName;
        this.mCapacity = capacity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_requester, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SharedRide sr = this.requestedSharedRides.get(position);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(sr.getRs_carpoolerUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                user.setUser_uid(dataSnapshot.getKey());
                holder.tvRequesterName.setText(user.getUser_profileName());
                if(user.getUser_profilePicSrc() == null){
                    Glide.with(mContext)
                            .load(mContext.getDrawable(R.mipmap.img_default_profile_pic))
                            .apply(new RequestOptions().optionalCenterCrop())
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.ivRequesterImg);
                }
                else {
                    if(user.getUser_profilePicSrc().matches("")) {
                        Glide.with(mContext)
                                .load(mContext.getDrawable(R.mipmap.img_default_profile_pic))
                                .apply(new RequestOptions().optionalCenterCrop())
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.ivRequesterImg);
                    }
                    Glide.with(mContext)
                            .load(user.getUser_profilePicSrc())
                            .apply(new RequestOptions().optionalCenterCrop())
                            .apply(RequestOptions.circleCropTransform())
                            .into(holder.ivRequesterImg);

                }
                holder.ivRequesterImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewProfile.class);
                        intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, user.getUser_uid());
                        mContext.startActivity(intent);
                    }
                });
                holder.tvTime.setText(calculateDateDiff(sr.getRs_createdTimestamp()));
                holder.tvNote.setText(sr.getRs_note());
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: accept button clicked");
                        getAcceptedSharedRide(sr.getRs_id(), sr.getRs_carpoolerUid());
                    }
                });

                holder.btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: decline button clicked");
                        declineRequest(sr.getRs_id(), sr.getRs_carpoolerUid());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to get user detail -> " + databaseError);
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return requestedSharedRides.size();
    }

    private void getAcceptedSharedRide(final String rs_id, final String requesterId){
        Log.d(TAG, "getAcceptedSharedRide: getting accepted shared ride detail");

        mAcceptedSharedRide = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_SHAREDRIDE);
        Query query = ref.orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID).equalTo(mPr_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    SharedRide sr = ds.getValue(SharedRide.class);
                    sr.setRs_id(ds.getKey());
                    if (sr.getRs_status() == mContext.getResources().getInteger(R.integer.sharedride_status_accept)){
                        mAcceptedSharedRide.add(sr);
                    }
                }
                if(mAcceptedSharedRide.size() < mCapacity) {
                    acceptRequest(rs_id, requesterId);
                }
                else {
                    Toast.makeText(mContext, "Your vehicle is out of capacity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on getting accepted shared ride detail -> " + databaseError);
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptRequest(final String rs_id, final String requesterId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_SHAREDRIDE)
                .child(rs_id)
                .child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_STATUS);
        ref.setValue(mContext.getResources().getInteger(R.integer.sharedride_status_accept))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Request accepted", Toast.LENGTH_SHORT).show();
                        sendAcceptNotification(requesterId);
                    }
                });
    }

    private void declineRequest(final String rs_id, final String requesterId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_SHAREDRIDE)
                .child(rs_id)
                .child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_STATUS);
        ref.setValue(mContext.getResources().getInteger(R.integer.sharedride_status_decline))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "Request declined", Toast.LENGTH_SHORT).show();
                        sendDeclineNotification(requesterId);
                    }
                });
    }

    private void sendAcceptNotification(String requesterId){
        Notification notif = new Notification();
        notif.setNotif_type(mContext.getResources().getInteger(R.integer.notification_type_acceptRequest));
        notif.setNotif_createdTimestamp(System.currentTimeMillis());
        notif.setNotif_sender_uid(mPr_providerUid);
        notif.setNotif_status(mContext.getResources().getInteger(R.integer.notification_status_new));
        notif.setNotif_ref_providedRideId(mPr_id);
        String message = "<b>" + mCurrentUserProfileName + "</b> accepted your carpool request";
        notif.setNotif_message(message);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DatabaseHelper.TABLE_NOTIFICATION).child(requesterId)
                .push()
                .setValue(notif)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //send notification
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled: Fail to send notification");
                    }
                });
    }

    private void sendDeclineNotification(String requesterId){
        Notification notif = new Notification();
        notif.setNotif_type(mContext.getResources().getInteger(R.integer.notification_type_declineRequest));
        notif.setNotif_createdTimestamp(System.currentTimeMillis());
        notif.setNotif_sender_uid(mPr_providerUid);
        notif.setNotif_status(mContext.getResources().getInteger(R.integer.notification_status_new));
        notif.setNotif_ref_providedRideId(mPr_id);
        String message = "<b>" + mCurrentUserProfileName + "</b> declined your carpool request";
        notif.setNotif_message(message);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DatabaseHelper.TABLE_NOTIFICATION).child(requesterId)
                .push()
                .setValue(notif)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //send notification
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled: Fail to send notification");
                    }
                });
    }

    private String calculateDateDiff(long timestamp){
        long dif = System.currentTimeMillis() - timestamp;
        long difDay = TimeUnit.MILLISECONDS.toDays(dif);
        long difHour = TimeUnit.MILLISECONDS.toHours(dif);
        long difMinute = TimeUnit.MILLISECONDS.toMinutes(dif);
        String timeDiff;
        if(difDay < 1){
            if(difHour < 1){
                if(difMinute == 1) {
                    timeDiff = difMinute + " minute ago";
                }
                else {
                    timeDiff = difMinute + " minutes ago";
                }
            }
            else {
                if(difHour == 1) {
                    timeDiff = difHour + " hour ago";
                }
                else {
                    timeDiff = difHour + " hours ago";
                }
            }
        }
        else {
            if(difDay == 1) {
                timeDiff = difDay + " day ago";
            }
            else {
                timeDiff = difDay + " days ago";
            }
        }
        return timeDiff;
    }

}
