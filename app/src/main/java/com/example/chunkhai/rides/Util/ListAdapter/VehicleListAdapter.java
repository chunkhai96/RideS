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
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.R;

import java.util.ArrayList;

public class VehicleListAdapter extends ArrayAdapter<Vehicle> {
    private static final String TAG = "DrawableTextListAdapter";

    Context context;
    ArrayList<Vehicle> vehicles;
    int listContainerLayoutResourceId; //R.layout.layout_id
    int imageViewId; //R.id.imageview_id
    int textViewId; //R.id.textview_id

    public VehicleListAdapter(Context context, int listContainerLayoutResourceId, int imageViewId, int textViewId,
                              ArrayList<Vehicle> vehicles) {
        super(context, listContainerLayoutResourceId, textViewId, vehicles);

        Log.d(TAG, "VehicleListAdapter: instantiate");
        this.context = context;
        this.listContainerLayoutResourceId = listContainerLayoutResourceId;
        this.textViewId = textViewId;
        this.imageViewId = imageViewId;
        this.vehicles = vehicles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for DrawableTextList");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(listContainerLayoutResourceId, parent, false);
        ImageView listImageView = (ImageView) row.findViewById(imageViewId);
        TextView listTextView = (TextView) row.findViewById(textViewId);
        TextView listCaption = (TextView) row.findViewById(R.id.tvListCaption);
        View divider = (View) row.findViewById(R.id.divider);

        listTextView.setText(vehicles.get(position).getVehicle_plateNo());
        String caption = vehicles.get(position).getVehicle_model()+ " &bull; " + vehicles.get(position).getVehicle_color() + " &bull; "
                + vehicles.get(position).getVehicle_capacity();
        listCaption.setText(Html.fromHtml(caption));

        String imageUrl = vehicles.get(position).getVehicle_imageSrc();

        if(imageUrl == null || imageUrl.equals("")){
            listImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.img_default_vehicle_pic));
        }
        else {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().optionalCenterCrop())
                    .into(listImageView);
        }

        //remove last row divider
        if(position == vehicles.size()-1){
            divider.setVisibility(View.GONE);
        }

        return row;
    }
}