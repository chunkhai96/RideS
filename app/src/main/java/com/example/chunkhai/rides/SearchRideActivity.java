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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.SearchRideRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SearchRideActivity extends AppCompatActivity {
    private static final String TAG = "SearchRideActivity";
    private final String TITLE = "Search Ride";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    TextView tvNoRide;
    RecyclerView rvSearchRideResult;
    ProgressBar progressBar;

    //vars
    FirebaseAuth mAuth;
    ArrayList<ProvidedRide> mSearchRideResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        tvNoRide = (TextView) findViewById(R.id.tvNoRide);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        rvSearchRideResult = (RecyclerView) findViewById(R.id.rvSearchResult);

        mAuth = FirebaseAuth.getInstance();

        String placeFrom = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_FROM_ADDRESS);
        String placeTo = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_TO_ADDRESS);

        init();
        searchRide(placeFrom, placeTo);
    }

    private void init(){
        Log.d(TAG, "init: initializing toolbar and other widgets");

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void searchRide(final String placeFrom, final String placeTo) {
        Log.d(TAG, "getUserProvidedRide: getting mUser provided ride list");

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_DEPART_TIMESTAMP)
                .startAt(System.currentTimeMillis());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSearchRideResults = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProvidedRide ride = ds.getValue(ProvidedRide.class);
                    if(ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)
                            && (ride.getPr_fromAddress().toLowerCase().contains(placeFrom.toLowerCase())
                                || ride.getPr_fromPlace().toLowerCase().contains(placeFrom.toLowerCase()))
                            && (ride.getPr_toAddress().toLowerCase().contains(placeTo.toLowerCase())
                                || ride.getPr_toPlace().toLowerCase().contains(placeTo.toLowerCase()))) {
                        ride.setPr_id(ds.getKey());
                        mSearchRideResults.add(ride);
                    }
                }
                Collections.sort(mSearchRideResults);
                initProvidedRidesCardList();
                if(mSearchRideResults.isEmpty()){
                    tvNoRide.setVisibility(View.VISIBLE);
                }
                else{
                    tvNoRide.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retriving search ride result");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initProvidedRidesCardList(){
        Log.d(TAG, "initProvidedRidesCardList: initializing provided ride card list");
        SearchRideRVAdapter adapter = new SearchRideRVAdapter(this, mSearchRideResults);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvSearchRideResult.setLayoutManager(layoutManager);
        rvSearchRideResult.setNestedScrollingEnabled(false);
        rvSearchRideResult.setItemAnimator(new DefaultItemAnimator());
        rvSearchRideResult.setAdapter(adapter);
    }
}
