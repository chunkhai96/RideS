package com.example.chunkhai.rides;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowProfilePicActivity extends AppCompatActivity {
    private static final String TAG = "ShowProfilePicActivity";
    private final  String title = "Profile Picture";

    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile_pic);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        profileImg = (ImageView) findViewById(R.id.full_profile_image);


        //Init Toolbar
        tvToolbarTitle.setText(title);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference().child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Data retrieved");
                if(dataSnapshot.child(DatabaseHelper.TABLE_USER_ATTR_PROFILE_PIC_SRC).getValue(String.class) != null){
                    String imageUrl = dataSnapshot.child(DatabaseHelper.TABLE_USER_ATTR_PROFILE_PIC_SRC).getValue(String.class);
                    Glide.with(getBaseContext())
                            .load(imageUrl)
                            .apply(new RequestOptions().fitCenter())
                            .into(profileImg);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: The read failed: " + databaseError.getCode());
            }

        });
    }
}
