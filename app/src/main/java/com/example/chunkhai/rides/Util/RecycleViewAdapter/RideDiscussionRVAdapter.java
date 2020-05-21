package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.Discussion;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RideDiscussionRVAdapter extends RecyclerView.Adapter<RideDiscussionRVAdapter.MyViewHolder>{
    private static final String TAG = "RideDiscussionRVAdapter";

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Discussion> chats;
    private FirebaseUser mUser;
    private String latestDate = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvUserName, tvMessage, tvTime;
        public ImageView ivUserProfileImg;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            ivUserProfileImg = (ImageView) view.findViewById(R.id.ivUserProfileImg);
        }
    }


    public RideDiscussionRVAdapter(Context context, List<Discussion> chats) {
        this.mContext = context;
        this.chats = chats;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_row_message_right, parent, false);
            return new MyViewHolder(itemView);
        }
        else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_row_message_left, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Discussion chat = chats.get(position);
        String msg = chat.getDisc_content() + "<font size='5dp'> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; </font>";
        holder.tvMessage.setText(Html.fromHtml(msg));
        Calendar calendarCurr = Calendar.getInstance();
        calendarCurr.setTimeInMillis(chat.getDisc_timestamp());
        holder.tvTime.setText(getDatetimeFromTimestamp(calendarCurr));

        if(position > 0){
            Calendar calendarPrevious = Calendar.getInstance();
            calendarPrevious.setTimeInMillis(chats.get(position-1).getDisc_timestamp());
            if(!getDateFromTimestamp(calendarCurr).equals(getDateFromTimestamp(calendarPrevious))){
                holder.tvDate.setText(getDateFromTimestamp(calendarCurr));
                holder.tvDate.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.tvDate.setText(getDateFromTimestamp(calendarCurr));
            holder.tvDate.setVisibility(View.VISIBLE);
        }


        if(chat.getDisc_sender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.ivUserProfileImg.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic));
            holder.tvUserName.setText("You");
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(DatabaseHelper.TABLE_USER)
                    .child(chat.getDisc_sender());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.tvUserName.setText(user.getUser_profileName());
                    if(user.getUser_profilePicSrc() == null ||user.getUser_profilePicSrc().equals("")){
                        holder.ivUserProfileImg.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic));
                    }
                    else {
                        Glide.with(mContext)
                                .load(user.getUser_profilePicSrc())
                                .apply(new RequestOptions().optionalCenterCrop())
                                .apply(RequestOptions.circleCropTransform())
                                .apply(new RequestOptions().placeholder(R.drawable.ic_loading_image))
                                .into(holder.ivUserProfileImg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    holder.ivUserProfileImg.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic));
                    holder.tvUserName.setText("Anonymous");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getDisc_sender().equals(mUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    private String getDatetimeFromTimestamp(Calendar calendar) {
        String format = "k:m";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(calendar.getTime());
    }

    private String getDateFromTimestamp(Calendar calendar){
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        return sdf.format(calendar.getTime());
    }
}
