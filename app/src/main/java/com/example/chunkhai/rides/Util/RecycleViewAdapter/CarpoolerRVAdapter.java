package com.example.chunkhai.rides.Util.RecycleViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.ViewProfile;

import java.util.List;

public class CarpoolerRVAdapter extends RecyclerView.Adapter<CarpoolerRVAdapter.MyViewHolder>{
    private Context mContext;
    private List<User> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCarpoolerImg;

        public MyViewHolder(View view) {
            super(view);
            ivCarpoolerImg = (ImageView) view.findViewById(R.id.ivCarpoolerImg);
        }
    }


    public CarpoolerRVAdapter(Context mContext, List<User> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_row_carpooler, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final User user = userList.get(position);

        if(user.getUser_profilePicSrc() != null) {
            if(!user.getUser_profilePicSrc().matches("")){
                Glide.with(mContext)
                        .load(user.getUser_profilePicSrc())
                        .apply(new RequestOptions().optionalCenterCrop())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.ivCarpoolerImg);
            }
            else {
                Glide.with(mContext)
                        .load(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic))
                        .apply(new RequestOptions().optionalCenterCrop())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.ivCarpoolerImg);
            }

        }
        else {
            Glide.with(mContext)
                    .load(mContext.getResources().getDrawable(R.mipmap.img_default_profile_pic))
                    .apply(new RequestOptions().optionalCenterCrop())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivCarpoolerImg);
        }

        holder.ivCarpoolerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewProfile.class);
                intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, user.getUser_uid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
