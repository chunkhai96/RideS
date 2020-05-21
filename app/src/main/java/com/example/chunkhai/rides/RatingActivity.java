package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.chunkhai.rides.Object.RatingReview;
import com.example.chunkhai.rides.Object.ParcelUser;
import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;

public class RatingActivity extends AppCompatActivity implements FragmentRating.OnGetFromUserClickListener {

    private static final String TAG = "RatingActivity";

    @Override
    public void clickSubmit(float rating, String review, int position) {

        long currentTimestamp = System.currentTimeMillis();

        RatingReview cRate = new RatingReview(
                FirebaseAuth.getInstance().getUid()
                , parcelUsers.get(position).getUser_uid()
                , rating
                , review
                , currentTimestamp);
        ratingReviews.add(cRate);

        if (position == parcelUsers.size()-1){
            insertRateReview();
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        else {
            mPager.setCurrentItem(position+1);
        }
    }

    @Override
    public void clickSkip() {
        showSkipRatingModel();
    }

    //widgets
    RelativeLayout rlFace;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private SpringDotsIndicator dotsIndicator;
    ProgressBar progressBar;

    //vars
    String pr_id, provider_uid;
    ArrayList<ParcelUser> parcelUsers;
    ArrayList<SharedRide> sharedRides;
    long totalToRate;
    int globalCounter;
    ArrayList<RatingReview> ratingReviews;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        pr_id = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID);
        provider_uid = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID);

        dotsIndicator = (SpringDotsIndicator) findViewById(R.id.dots_indicator);
        mPager = (ViewPager) findViewById(R.id.pager);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ratingReviews = new ArrayList<>();

        Log.d(TAG, "onCreate: mUser: " + FirebaseAuth.getInstance().getUid());
        getUsersToRate();


    }

    @Override
    public void onBackPressed() {
        showSkipRatingModel();
    }

    private void initViewPager(){
        Log.d(TAG, "initViewPager: initializing" + parcelUsers.size());
        if(parcelUsers.size() > 0) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), parcelUsers);
            mPager.setAdapter(mPagerAdapter);
            dotsIndicator.setViewPager(mPager);
            progressBar.setVisibility(View.GONE);
        }
        else {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    private void getUsersToRate(){
        progressBar.setVisibility(View.VISIBLE);
        if(provider_uid.matches(FirebaseAuth.getInstance().getUid())){
            parcelUsers = new ArrayList<>();
            sharedRides = new ArrayList<>();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                    .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                    .equalTo(pr_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: getting " + dataSnapshot.getChildrenCount() + " shared ride");
                    totalToRate = dataSnapshot.getChildrenCount();
                    globalCounter = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        SharedRide sr = ds.getValue(SharedRide.class);
                        sr.setRs_id(ds.getKey());
                        if(sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_accept)
                                || sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_complete)) {
                            sharedRides.add(sr);
                        }
                        globalCounter++;
                    }
                    if (!(globalCounter < totalToRate)) {
                        getCarpoolersDetail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Error on getting users");
                }
            });
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(DatabaseHelper.TABLE_USER)
                    .child(provider_uid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    parcelUsers = new ArrayList<>();
                    ParcelUser user = dataSnapshot.getValue(ParcelUser.class);
                    user.setUser_uid(dataSnapshot.getKey());
                    parcelUsers.add(user);

                    initViewPager();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Error on getting mUser info");
                }
            });
        }
    }

    private void getCarpoolersDetail(){
        for(globalCounter = 0; globalCounter<sharedRides.size(); globalCounter++){
            SharedRide sr = sharedRides.get(globalCounter);
            String id = sr.getRs_carpoolerUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(DatabaseHelper.TABLE_USER)
                    .child(id);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: get mUser detail #" + dataSnapshot.getKey());
                    ParcelUser user = dataSnapshot.getValue(ParcelUser.class);
                    user.setUser_uid(dataSnapshot.getKey());
                    parcelUsers.add(user);
                    Log.d(TAG, "onDataChange: parcelUsers " + user.getUser_profileName());
                    if(globalCounter == sharedRides.size()) {
                        initViewPager();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: Error on getting mUser info");
                }
            });
        }
    }

    private void showSkipRatingModel(){
        new AlertDialog.Builder(this)
                .setTitle("Rate")
                .setMessage("Are you sure you want to skip rate the mUser?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void insertRateReview(){
        Log.d(TAG, "insertRateReview: ride provider rate and review carpooler");
        for(int i = 0; i < ratingReviews.size(); i++) {
            RatingReview cRate = ratingReviews.get(i);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(DatabaseHelper.TABLE_RATINGREVIEW)
                    .push()
                    .setValue(cRate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: successfully added");

                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.d(TAG, "onCanceled: Error on inserting");

                        }
                    });
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        ArrayList<ParcelUser> pUsers;

        public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<ParcelUser> pUsers) {
            super(fm);
            this.pUsers = pUsers;
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentRating.newInstance(pUsers.get(position), position, pUsers.size());
        }

        @Override
        public int getCount() {
            return pUsers.size();
        }
    }

}

