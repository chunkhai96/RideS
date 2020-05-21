package com.example.chunkhai.rides.Util.ListAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.Notification;
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NotificationListAdapter extends ArrayAdapter<Notification> {
    private static final String TAG = "NotificationListAdapter";

    Context context;
    ArrayList<Notification> notifications;
    int listContainerLayoutResourceId; //R.layout.layout_id

    public NotificationListAdapter(Context context, int listContainerLayoutResourceId, ArrayList<Notification> notifications) {
        super(context, listContainerLayoutResourceId, notifications);

        Log.d(TAG, "NotificationListAdapter: instantiate");
        this.context = context;
        this.listContainerLayoutResourceId = listContainerLayoutResourceId;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for NotificationList");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(listContainerLayoutResourceId, parent, false);
        ImageView listImageView = (ImageView) row.findViewById(R.id.ivListImage);
        TextView listCaption = (TextView) row.findViewById(R.id.tvListCaption);
        TextView tvTime = (TextView) row.findViewById(R.id.tvTime);
        ImageView unarchivedSign = (ImageView) row.findViewById(R.id.unarchivedSign);

        listCaption.setText(Html.fromHtml(notifications.get(position).getNotif_message()));

        //notification type handling
        if(notifications.get(position).getNotif_type() == context.getResources().getInteger(R.integer.notification_type_newRideRequest)){
            listImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_new_request));
        }
        else if(notifications.get(position).getNotif_type() == context.getResources().getInteger(R.integer.notification_type_cancelRequest)){
            listImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_cancel_request));
        }
        else if(notifications.get(position).getNotif_type() == context.getResources().getInteger(R.integer.notification_type_cancelRide)){
            listImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_cancel_ride));
        }
        else if(notifications.get(position).getNotif_type() == context.getResources().getInteger(R.integer.notification_type_acceptRequest)){
            listImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_accept_request));
        }
        else if(notifications.get(position).getNotif_type() == context.getResources().getInteger(R.integer.notification_type_declineRequest)){
            listImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_decline_request));
        }

        //notification status handling
        if(notifications.get(position).getNotif_status() == context.getResources().getInteger(R.integer.notification_status_new)){
            listCaption.setTextColor(context.getResources().getColor(R.color.colorBlack));
            unarchivedSign.setVisibility(View.VISIBLE);
        }
        else if(notifications.get(position).getNotif_status() == context.getResources().getInteger(R.integer.notification_status_archived)){
            listCaption.setTextColor(context.getResources().getColor(R.color.colorDefaultText));
            unarchivedSign.setVisibility(View.GONE);
        }

        //notification time handling
        long dif = System.currentTimeMillis() - notifications.get(position).getNotif_createdTimestamp();
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

        tvTime.setText(timeDiff);

        Log.d(TAG, "getView: time: " + tvTime.getText().toString());

        return row;
    }

}
