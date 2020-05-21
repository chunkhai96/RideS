package com.example.chunkhai.rides.Util.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyContactListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "EmergencyContactListAda";

    Context context;
    ArrayList<String> phones;
    int listContainerLayoutResourceId; //R.layout.layout_id

    public EmergencyContactListAdapter(Context context, int listContainerLayoutResourceId,
                                       ArrayList<String> phones) {
        super(context, listContainerLayoutResourceId, phones);

        Log.d(TAG, "EmergencyContactListAdapter: instantiate");

        this.context = context;
        this.listContainerLayoutResourceId = listContainerLayoutResourceId;
        this.phones = phones;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for TextWithValueListAdapter");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(listContainerLayoutResourceId, parent, false);
        TextView textViewTitle = (TextView) row.findViewById(R.id.tvListTitle);
        final ImageView btnCall = (ImageView) row.findViewById(R.id.btnCall);
        final String telNum = phones.get(position);
        textViewTitle.setText(telNum);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + telNum));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(callIntent);
            }
        });

        return row;
    }
}