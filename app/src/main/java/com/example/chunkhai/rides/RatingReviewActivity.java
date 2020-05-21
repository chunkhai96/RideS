package com.example.chunkhai.rides;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chunkhai.rides.Object.RatingReview;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.RatingReviewRVAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class RatingReviewActivity extends AppCompatActivity {
    private static final String TAG = "RatingReviewActivity";
    private final String TITLE = "Rating & Review";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    TextView tvOverallRate, tvTotalRater;
    RatingBar ratingBarOverall;
    ProgressBar progressBarFiveStar, progressBarFourStar, progressBarThreeStar, progressBarTwoStar, progressBarOneStar;
    RecyclerView rvRating;

    //vars
    ArrayList<RatingReview> ratingReviews;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_review);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        tvOverallRate = (TextView) findViewById(R.id.tvOverallRate);
        ratingBarOverall = (RatingBar) findViewById(R.id.ratingBarOverall);
        tvTotalRater = (TextView) findViewById(R.id.tvTotalRater);
        progressBarFiveStar = (ProgressBar) findViewById(R.id.progressBarFiveStar);
        progressBarFourStar = (ProgressBar) findViewById(R.id.progressBarFourStar);
        progressBarThreeStar = (ProgressBar) findViewById(R.id.progressBarThreeStar);
        progressBarTwoStar = (ProgressBar) findViewById(R.id.progressBarTwoStar);
        progressBarOneStar = (ProgressBar) findViewById(R.id.progressBarOneStar);

        rvRating = (RecyclerView) findViewById(R.id.rvRating);

        uid = getIntent().getStringExtra(DatabaseHelper.TABLE_USER_ATTR_UID);

        init();
    }

    public void init(){
        Log.d(TAG, "init: initializing toolbar and other widgets");

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserRatingReview(uid);
    }

    private void initOverallRatingSummary(){
        Log.d(TAG, "initOverallRatingSummary: setting view");

        float sumOfRates = 0;
        int numberOfStar[] = new int[5];
        numberOfStar[0] = 0; //1 star
        numberOfStar[1] = 0; //2 star
        numberOfStar[2] = 0; //3 star
        numberOfStar[3] = 0; //4 star
        numberOfStar[4] = 0; //5 star
        for(RatingReview rr : ratingReviews){
            float rate = rr.getRr_rating();

            sumOfRates += rate;
            if(rate == 1){
                numberOfStar[0]++;
            }
            else if(rate == 2){
                numberOfStar[1]++;
            }
            else if(rate == 3){
                numberOfStar[2]++;
            }
            else if(rate == 4){
                numberOfStar[3]++;
            }
            else if(rate == 5){
                numberOfStar[4]++;
            }
        }

        DecimalFormat df = new DecimalFormat("0.#");
        int totalRaters = ratingReviews.size();
        float overallRateAverage = (float)0.0;
        if(totalRaters > 0){
            overallRateAverage = sumOfRates/totalRaters;
        }
        int progress1star = Math.round(((float)numberOfStar[0]/totalRaters)*100);
        int progress2star = Math.round(((float)numberOfStar[1]/totalRaters)*100);
        int progress3star = Math.round(((float)numberOfStar[2]/totalRaters)*100);
        int progress4star = Math.round(((float)numberOfStar[3]/totalRaters)*100);
        int progress5star = Math.round(((float)numberOfStar[4]/totalRaters)*100);

        tvOverallRate.setText(String.format("%.1f", overallRateAverage));
        ratingBarOverall.setRating(overallRateAverage);
        tvTotalRater.setText("("+totalRaters+")");
        progressBarOneStar.setProgress(progress1star);
        progressBarTwoStar.setProgress(progress2star);
        progressBarThreeStar.setProgress(progress3star);
        progressBarFourStar.setProgress(progress4star);
        progressBarFiveStar.setProgress(progress5star);
    }

    private void initRatingReviewRecycleView(){
        Log.d(TAG, "initRatingReviewRecycleView: initializing rating and review list");
        RatingReviewRVAdapter adapter = new RatingReviewRVAdapter(this, ratingReviews);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvRating.setLayoutManager(layoutManager);
        rvRating.setNestedScrollingEnabled(false);
        rvRating.setItemAnimator(new DefaultItemAnimator());
        rvRating.setAdapter(adapter);
    }

    private void getUserRatingReview(String uid){
        Log.d(TAG, "getUserRatingReview: getting mUser rate and review from database");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(DatabaseHelper.TABLE_RATINGREVIEW)
                .orderByChild(DatabaseHelper.TABLE_RATINGREVIEW_ATTR_RECEIVER_UID)
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: mUser rate review accessed (" + dataSnapshot.getChildrenCount() + ")");
                ratingReviews = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    RatingReview rr = ds.getValue(RatingReview.class);
                    rr.setRr_id(dataSnapshot.getKey());
                    ratingReviews.add(rr);
                }
                Collections.reverse(ratingReviews);
                initOverallRatingSummary();
                initRatingReviewRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed on accessing mUser rate review ->" + databaseError);
            }
        });
    }
}
