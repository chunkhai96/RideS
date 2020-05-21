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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.R;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;

public class ProfileTopListAdapter extends ArrayAdapter<String> {

    Context context;
    String imageSrc;
    String[] name;
    String[] id;
    ImageView ivVerify, getIvVerify2;

    public ProfileTopListAdapter(Context c, String[] profileName, String profileImageSrc, String[] userId) {
        super(c, R.layout.list_row_profile_top, R.id.tvProfileName, profileName);
        this.context = c;
        this.imageSrc = profileImageSrc;
        this.name = profileName;
        this.id = userId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_row_profile_top, parent, false);
        ImageView profileImage = (ImageView) row.findViewById(R.id.profile_image);
        TextView tvProfileName = (TextView) row.findViewById(R.id.tvProfileName);
        final ImageView ivVerify = (ImageView) row.findViewById(R.id.ivVerify);
        final ImageView ivVerify2 = (ImageView) row.findViewById(R.id.ivVerify2);

        if(imageSrc == null || imageSrc.equals("")) {
            profileImage.setImageDrawable(context.getResources().getDrawable(R.mipmap.img_default_profile_pic));
        }
        else{
            Glide.with(context)
                    .load(imageSrc)
                    .apply(new RequestOptions().optionalCenterCrop())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
        tvProfileName.setText(name[position]);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userDetail = dataSnapshot.getValue(User.class);
                if (userDetail.getUser_verifMatrixStatus() == context.getResources().getInteger(R.integer.user_verification_status_verified)){
                    if(userDetail.getUser_verifLicenseStatus() == context.getResources().getInteger(R.integer.user_verification_status_verified)){
                        ivVerify2.setVisibility(View.VISIBLE);
                    }
                    else {
                        ivVerify.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on retreiving user information -> " + databaseError);
                Toast.makeText(context,"Error", Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }
}