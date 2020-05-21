package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class RatingReviewRVAdapter extends RecyclerView.Adapter<RatingReviewRVAdapter.MyViewHolder>{
    private static final String TAG = "RatingReviewRVAdapter";

    private Context mContext;
    private List<RatingReview> ratingReviews;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvRaterProfileName, tvDateOfRate, tvReview, tvToTitle;
        public ImageView ivRaterProfileImg;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);

            tvRaterProfileName = (TextView) view.findViewById(R.id.tvRaterProfileName);
            tvDateOfRate = (TextView) view.findViewById(R.id.tvDateOfRate);
            tvReview = (TextView) view.findViewById(R.id.tvReview);
            ivRaterProfileImg = (ImageView) view.findViewById(R.id.ivRaterProfileImg);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        }
    }


    public RatingReviewRVAdapter(Context context, List<RatingReview> ratingReviews) {
        this.mContext = context;
        this.ratingReviews = ratingReviews;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_row_rate_review, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final RatingReview ratingReview = this.ratingReviews.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ratingReview.getRr_createdTimeStamp());
        String dateOfRate = getDatetimeFromTimestamp(calendar);
        holder.tvDateOfRate.setText(dateOfRate);
        holder.tvReview.setText(ratingReview.getRr_review());
        holder.ratingBar.setRating(ratingReview.getRr_rating());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(ratingReview.getRr_raterUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User rater = dataSnapshot.getValue(User.class);
                    rater.setUser_uid(dataSnapshot.getKey());
                    holder.tvRaterProfileName.setText(rater.getUser_profileName());
                    if(rater.getUser_profilePicSrc() == null){
                        holder.ivRaterProfileImg.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic));
                    }
                    else {
                        if(rater.getUser_profilePicSrc().matches("")){
                            holder.ivRaterProfileImg.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic));
                        }
                        Glide.with(mContext)
                                .load(rater.getUser_profilePicSrc())
                                .apply(new RequestOptions().optionalCenterCrop())
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.ivRaterProfileImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingReviews.size();
    }

    private String getDatetimeFromTimestamp(Calendar calendar) {
        String format = "d/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(calendar.getTime());
    }
}
