package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.RideDetailActivity;
import com.example.chunkhai.rides.Util.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SchedulePendingRequestRVAdapter extends RecyclerView.Adapter<SchedulePendingRequestRVAdapter.MyViewHolder>{
    private static final String TAG = "SchedulePendingRequestR";

    private Context mContext;
    private List<ProvidedRide> requestedRides;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvMonth, tvDay, tvFromTitle, tvToTitle, tvTime;

        public MyViewHolder(View view) {
            super(view);

            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvMonth = (TextView) view.findViewById(R.id.tvMonth);
            tvDay = (TextView) view.findViewById(R.id.tvDay);
            tvFromTitle = (TextView) view.findViewById(R.id.tvFromTitle);
            tvToTitle = (TextView) view.findViewById(R.id.tvToTitle);
            tvTime = (TextView) view.findViewById(R.id.tvTime);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RideDetailActivity.class);
                    intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, requestedRides.get(getAdapterPosition()).getPr_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }


    public SchedulePendingRequestRVAdapter(Context mContext, List<ProvidedRide> requestedRides) {
        this.mContext = mContext;
        this.requestedRides = requestedRides;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_schedule_ongoing_ride, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ProvidedRide providedRide = this.requestedRides.get(position);
        Calendar departTime = Calendar.getInstance();
        departTime.setTimeInMillis(providedRide.getPr_departTimestamp());
        holder.tvDate.setText(getDatetimeFromTimestamp(departTime, 1));
        holder.tvMonth.setText(getDatetimeFromTimestamp(departTime, 2));
        holder.tvDay.setText(getDatetimeFromTimestamp(departTime, 3));
        holder.tvTime.setText(getDatetimeFromTimestamp(departTime, 4));
        holder.tvFromTitle.setText(providedRide.getPr_fromPlace());
        holder.tvToTitle.setText(providedRide.getPr_toPlace());

    }

    @Override
    public int getItemCount() {
        return requestedRides.size();
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

}
