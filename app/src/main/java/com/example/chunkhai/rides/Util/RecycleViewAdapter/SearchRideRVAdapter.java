package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchRideRVAdapter extends RecyclerView.Adapter<SearchRideRVAdapter.MyViewHolder>{
    private static final String TAG = "ScheduleProvidedRideRVAdapter";

    private Context mContext;
    private List<ProvidedRide> providedRides;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDateTitle, tvDepartureDateTime, tvFromTitle, tvToTitle, tvShareRm, tvShareSen;

        public MyViewHolder(View view) {
            super(view);

            tvDateTitle = (TextView) view.findViewById(R.id.tvDateTitle);
            tvDepartureDateTime = (TextView) view.findViewById(R.id.tvDepartureDateTime);
            tvFromTitle = (TextView) view.findViewById(R.id.tvFromTitle);
            tvToTitle = (TextView) view.findViewById(R.id.tvToTitle);
            tvShareRm = (TextView) view.findViewById(R.id.tvShareRm);
            tvShareSen = (TextView) view.findViewById(R.id.tvShareSen);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RideDetailActivity.class);
                    intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, providedRides.get(getAdapterPosition()).getPr_id());
                    mContext.startActivity(intent);
                }
            });
        }
    }


    public SearchRideRVAdapter(Context mContext, List<ProvidedRide> providedRides) {
        this.mContext = mContext;
        this.providedRides = providedRides;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_search_ride, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ProvidedRide providedRide = this.providedRides.get(position);
        Calendar departTime = Calendar.getInstance();
        departTime.setTimeInMillis(providedRide.getPr_departTimestamp());
        holder.tvDateTitle.setText(getDatetimeFromTimestamp(departTime, 2));
        holder.tvDepartureDateTime.setText(getDatetimeFromTimestamp(departTime, 1));
        holder.tvFromTitle.setText(providedRide.getPr_fromPlace());
        holder.tvToTitle.setText(providedRide.getPr_toPlace());
        String sharedFare = Double.toString(providedRide.getPr_sharedFare());
        List<String> split = Arrays.asList(sharedFare.split("\\."));
        holder.tvShareRm.setText(split.get(0));
        holder.tvShareSen.setText(" " + split.get(1) + "0");
    }

    @Override
    public int getItemCount() {
        return providedRides.size();
    }

    private String getDatetimeFromTimestamp(Calendar calendar, int type /*1=full datetime, 2=date month*/) {
        String format = "";
        if(type == 1) {
            format = "dd MMM yyyy (E) - hh:mm a";
        }
        else if(type == 2){
            format = "dd, MMM";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(calendar.getTime());
    }
}
