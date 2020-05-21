package com.example.chunkhai.rides;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.RequesterRVAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PendingRequestActivity extends AppCompatActivity {
    private static final String TAG = "PendingRequestActivity";
    private final String TITLE = "Pending Request";

    //widgets
    RelativeLayout relLayout;
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RecyclerView rvPendingRequest;
    TextView tvNoRequester;
    ProgressBar progressBar;

    //vars
    String mPr_id,mPr_providerUid, mCurrentUserProfileName, mVehicleId;
    int listContainerLayoutResourceId;
    ArrayList<SharedRide> mSharedRideRequests;
    Vehicle mVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request);

        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        tvNoRequester = (TextView) findViewById(R.id.tvNoRequester);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvPendingRequest = (RecyclerView) findViewById(R.id.rvPendingRequest);

        mPr_id = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID);
        mPr_providerUid = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID);
        mCurrentUserProfileName = getIntent().getStringExtra(DatabaseHelper.TABLE_USER_ATTR_PROFILE_NAME);
        mVehicleId = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_VEHICLE_ID);
        listContainerLayoutResourceId = R.layout.card_row_requester;
        init();
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

        getVehicleDetail(mVehicleId);
    }

    private  void getVehicleDetail(String vehicleId){
        Log.d(TAG, "getVehicleDetail: getting vehicle information");

        mVehicle = new Vehicle();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_VEHICLE)
                .child(vehicleId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mVehicle = dataSnapshot.getValue(Vehicle.class);
                mVehicle.setVehicle_plateNo(dataSnapshot.getKey());

                getRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to getting vehicle detail -> " + databaseError);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRequest(){
        showProgressBar();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                .equalTo(mPr_id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: accepted shared ride " + dataSnapshot.getChildrenCount());
                mSharedRideRequests = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SharedRide sr = ds.getValue(SharedRide.class);
                    sr.setRs_id(ds.getKey());
                    if (sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_requested)) {
                        mSharedRideRequests.add(sr);
                    }
                }
                initRequesterCardList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on retrieving shared ride list");
            }
        });
    }

    private void initRequesterCardList(){
        Log.d(TAG, "initRequesterCardList: initializing requester card list");
        if(mSharedRideRequests.size() == 0){
            tvNoRequester.setVisibility(View.VISIBLE);
        }
        else {
            tvNoRequester.setVisibility(View.GONE);
        }
        RequesterRVAdapter adapter = new RequesterRVAdapter(this, mSharedRideRequests
                , mPr_id, mPr_providerUid, mCurrentUserProfileName, mVehicle.getVehicle_capacity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvPendingRequest.setLayoutManager(layoutManager);
        rvPendingRequest.setNestedScrollingEnabled(false);
        rvPendingRequest.setItemAnimator(new DefaultItemAnimator());
        rvPendingRequest.setAdapter(adapter);
        hideProgressBar();
    }

    private void showProgressBar(){
        relLayout.setBackgroundColor(getResources().getColor(R.color.colorGreyWithTransparency));
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideProgressBar(){
        relLayout.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
