package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.RideDetailActivity;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleOngoingRVAdapter extends RecyclerView.Adapter<ScheduleOngoingRVAdapter.MyViewHolder>{
    private static final String TAG = "ScheduleOngoingRVAdapte";

    private Context mContext;
    private List<ProvidedRide> ongoingRides;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvMonth, tvDay, tvFromTitle, tvToTitle, tvTime;
        public ImageView ivSitting, ivDriving;

        public MyViewHolder(View view) {
            super(view);

            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvMonth = (TextView) view.findViewById(R.id.tvMonth);
            tvDay = (TextView) view.findViewById(R.id.tvDay);
            tvFromTitle = (TextView) view.findViewById(R.id.tvFromTitle);
            tvToTitle = (TextView) view.findViewById(R.id.tvToTitle);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            ivSitting = (ImageView) view.findViewById(R.id.pic_sitting);
            ivDriving = (ImageView) view.findViewById(R.id.pic_driving);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RideDetailActivity.class);
                    intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, ongoingRides.get(getAdapterPosition()).getPr_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }


    public ScheduleOngoingRVAdapter(Context mContext, List<ProvidedRide> ongoingRides) {
        this.mContext = mContext;
        this.ongoingRides = ongoingRides;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_schedule_ongoing_ride, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ProvidedRide providedRide = this.ongoingRides.get(position);
        Calendar departTime = Calendar.getInstance();
        departTime.setTimeInMillis(providedRide.getPr_departTimestamp());
        holder.tvDate.setText(getDatetimeFromTimestamp(departTime, 1));
        holder.tvMonth.setText(getDatetimeFromTimestamp(departTime, 2));
        holder.tvDay.setText(getDatetimeFromTimestamp(departTime, 3));
        holder.tvTime.setText(getDatetimeFromTimestamp(departTime, 4));
        holder.tvFromTitle.setText(providedRide.getPr_fromPlace());
        holder.tvToTitle.setText(providedRide.getPr_toPlace());

        if(providedRide.getPr_providerUid().matches(FirebaseAuth.getInstance().getUid())){
            holder.ivDriving.setVisibility(View.VISIBLE);
            holder.ivSitting.setVisibility(View.GONE);
        }
        else {
            holder.ivDriving.setVisibility(View.GONE);
            holder.ivSitting.setVisibility(View.VISIBLE);
        }

        if(isToday(departTime) == 0){
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryDark));
            holder.tvDay.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryDark));
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryDark));
        }
        else if(isToday(departTime) < 0){
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            holder.tvDay.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
        }

    }

    @Override
    public int getItemCount() {
        return ongoingRides.size();
    }

    private String getDatetimeFromTimestamp(Calendar calendar, int type /*1=full datetime, 2=date month*/) {
        String format = "";

        if(type == 1) {
            format = "dd";
        }
        else if(type == 2){
            format = "MMM";
        }
        else if(type == 3){
            format = "E";
        }
        else if(type == 4){
            format = "hh:mma";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(calendar.getTime());
    }

    private int isToday(Calendar calendar) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
        Date today = new Date();
        Date departDate = new Date();
        try {
            today = sdf.parse(now.get(Calendar.YEAR) + " " + now.get(Calendar.MONTH) + " " + now.get(Calendar.DAY_OF_MONTH));
            departDate = sdf.parse(calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DAY_OF_MONTH));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "isToday: " + departDate);
        Log.d(TAG, "isToday: " + today);


        if (departDate.before(today)) {
            return -1;
        } else if (departDate.after(today)) {
            return 1;
        } else {
            return 0;
        }
    }
}
