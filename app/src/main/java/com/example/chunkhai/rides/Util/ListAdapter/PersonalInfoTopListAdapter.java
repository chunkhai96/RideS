package com.example.chunkhai.rides.Util.ListAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.R;

public class PersonalInfoTopListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "PersonalInfoTopListAdap";

    Context context;
    String imageUrl;
    String[] listTitle;

    public PersonalInfoTopListAdapter(Context c, String[] listTitle, String imageUrl) {
        super(c, R.layout.list_row_personal_info_top, R.id.tvListTitle, listTitle);
        this.context = c;
        this.imageUrl = imageUrl;
        this.listTitle = listTitle;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, "getView: creating view for PersonalInfoTopListAdapter");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row_personal_info_top, parent, false);
        TextView tvProfileName = (TextView) row.findViewById(R.id.tvListTitle);
        ImageView profileImage = (ImageView) row.findViewById(R.id.personal_info_profile_image);

        tvProfileName.setText(listTitle[position]);
        if(imageUrl == null || imageUrl.equals("")){
            profileImage.setImageDrawable(context.getResources().getDrawable(R.mipmap.img_default_profile_pic));
        }
        else {
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().optionalCenterCrop())
                    .into(profileImage);
        }

        return row;
    }

}