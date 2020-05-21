package com.example.chunkhai.rides;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.RatingReview;
import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ViewProfile extends AppCompatActivity {
    private static final String TAG = "ViewProfile";

    //widgets
    ImageButton btnBack, btnEmergencyContact, btnCall;
    ImageView ivProfileImg, getIvProfileImgBackground, ivBigProfileImg, ivVerification;
    TextView tvProfileName, tvSignature, tvCompletedRide, tvRating, tvFullName, tvMatrixNo, tvPhoneNo;
    RelativeLayout relLayout2;
    LinearLayout linLayoutRating;
    ProgressBar progressBar;

    //vars
    String uid;
    User mUser;
    int mCompletedRide = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnEmergencyContact = (ImageButton) findViewById(R.id.btnEmergencyContact);
        btnCall = (ImageButton) findViewById(R.id.btnCall);
        ivProfileImg = (ImageView) findViewById(R.id.ivProfileImg);
        getIvProfileImgBackground = (ImageView) findViewById(R.id.ivProfileImgBackground);
        ivBigProfileImg = (ImageView) findViewById(R.id.ivBigProfileImg);
        ivVerification = (ImageView) findViewById(R.id.ivVerification);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvSignature = (TextView) findViewById(R.id.tvSignature);
        tvCompletedRide = (TextView) findViewById(R.id.tvCompletedRide);
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvMatrixNo = (TextView) findViewById(R.id.tvMatrixNo);
        tvPhoneNo = (TextView) findViewById(R.id.tvPhoneNo);
        relLayout2 = (RelativeLayout) findViewById(R.id.relLayout2);
        linLayoutRating = (LinearLayout) findViewById(R.id.linLayoutRating);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        uid = getIntent().getStringExtra(DatabaseHelper.TABLE_USER_ATTR_UID);
        init();

    }

    private void init(){
        showProgressBar();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfile.this, EmergencyContactActivity.class);
                intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, uid);
                startActivity(intent);
            }
        });

        linLayoutRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfile.this, RatingReviewActivity.class);
                intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, uid);
                startActivity(intent);
            }
        });

        getUserDetail(uid);

    }

    private void getUserDetail(String userId){
        Log.d(TAG, "getUserDetail: getting user profile detail");
        
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference().child(DatabaseHelper.TABLE_USER)
                .child(userId);

        mUser = new User();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Read info from firebase database");
                mUser = dataSnapshot.getValue(User.class);

                String imgUrl = mUser.getUser_profilePicSrc();

                Glide.with(getApplicationContext())
                        .load(R.mipmap.ic_profile_image_circle)
                        .apply(new RequestOptions().optionalCenterCrop())
                        .apply(RequestOptions.circleCropTransform())
                        .into(getIvProfileImgBackground);
                if(imgUrl == null || imgUrl.equals("")){
                    Glide.with(getApplicationContext())
                            .load(getResources().getString(R.string.default_view_profile_background_img_url))
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d(TAG, "onLoadFailed: Unable to load image -> " + e);
                                    Toast.makeText(getApplicationContext(), "Unable to load image", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    hideProgressBar();
                                    return false;
                                }
                            })
                            .apply(new RequestOptions().bitmapTransform(new BlurTransformation(50)))
                            .into(ivBigProfileImg);

                    Glide.with(getApplicationContext())
                            .load(getDrawable(R.mipmap.img_default_profile_pic))
                            .apply(new RequestOptions().optionalCenterCrop())
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivProfileImg);
                }
                else {
                    Glide.with(getApplicationContext())
                            .load(imgUrl)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.d(TAG, "onLoadFailed: Unable to load image -> " + e);
                                    Toast.makeText(getApplicationContext(), "Unable to load image", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    hideProgressBar();
                                    return false;
                                }
                            })
                            .apply(new RequestOptions().bitmapTransform(new BlurTransformation(50)))
                            .into(ivBigProfileImg);
                    Glide.with(getApplicationContext())
                            .load(imgUrl)
                            .apply(new RequestOptions().optionalCenterCrop())
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivProfileImg);
                }

                if(mUser.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)
                        && mUser.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_verified)){
                    ivVerification.setImageResource(R.mipmap.ic_verified2);
                }
                else if(mUser.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)){
                    ivVerification.setImageResource(R.mipmap.ic_verified);
                }

                String profileName = (mUser.getUser_profileName() == null) ? "" : mUser.getUser_profileName();
                tvProfileName.setText(profileName);
                String gender = (mUser.getUser_gender() == null) ? "" : mUser.getUser_gender();
                if(gender.equals(getResources().getString(R.string.male))){
                    tvProfileName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_male, 0);
                }
                else {
                    tvProfileName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_female, 0);
                }
                String signature = (mUser.getUser_signature() == null) ? "Let's RideS" : mUser.getUser_signature();
                tvSignature.setText(signature);
                String fullName = "";
                String matrixNo = "";
                if(mUser.getUser_verifMatrixStatus() ==
                        getResources().getInteger(R.integer.user_verification_status_verified)){
                    fullName = mUser.getUser_fullName();
                    matrixNo = mUser.getUser_matrixNo();
                }
                else{
                    fullName = "Unverified";
                    matrixNo = "Unverified";
                }
                tvFullName.setText(fullName);
                tvMatrixNo.setText(matrixNo);
                final String phone = (mUser.getUser_phoneNo() == null) ? "" : mUser.getUser_phoneNo();
                tvPhoneNo.setText(phone);
                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + phone));
                        startActivity(callIntent);
                    }
                });

                getUserCompletedProvidedRide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: The read failed: " + databaseError);
            }

        });
    }

    private void getUserCompletedProvidedRide(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_PROVIDEDRIDE);

        Query query = ref.orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID)
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ProvidedRide pr = ds.getValue(ProvidedRide.class);
                    if(pr.getPr_status() == getResources().getInteger(R.integer.providedride_status_complete)){
                        mCompletedRide ++;
                    }
                }
                getUserCompletedSharedRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on getting user provided ride info -> " + databaseError);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserCompletedSharedRide(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_SHAREDRIDE);

        Query query = ref.orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_CARPOOLER_UID)
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    SharedRide sr = ds.getValue(SharedRide.class);
                    if(sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_complete)){
                        mCompletedRide ++;
                    }
                }
                tvCompletedRide.setText(String.valueOf(mCompletedRide));
                getUserRating();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on getting user shared ride info -> " + databaseError);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserRating(){
        Log.d(TAG, "getUserRatingReview: getting mUser rate and review from database");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(DatabaseHelper.TABLE_RATINGREVIEW)
                .orderByChild(DatabaseHelper.TABLE_RATINGREVIEW_ATTR_RECEIVER_UID)
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: mUser rate review accessed (" + dataSnapshot.getChildrenCount() + ")");
                long numberOfRate = dataSnapshot.getChildrenCount();
                if(numberOfRate > 0) {
                    double totalRatingPoint = 0.0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        RatingReview rr = ds.getValue(RatingReview.class);
                        totalRatingPoint += rr.getRr_rating();
                    }
                    DecimalFormat df = new DecimalFormat(".#");
                    double overallRating = totalRatingPoint / numberOfRate;
                    tvRating.setText(df.format(overallRating));
                }
                else {
                    tvRating.setText("0.0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed on accessing mUser rate review ->" + databaseError);
            }
        });
    }

    private void showProgressBar(){
        relLayout2.setBackground(getResources().getDrawable(R.color.colorWhite));
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideProgressBar(){
        relLayout2.setBackground(getResources().getDrawable(R.color.colorTransparent));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
