package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.ScheduleProvidedRideRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ProvideRideActivity extends AppCompatActivity {
    private static final String TAG = "ProvideRideActivity";
    private final String TITLE = "Provide Ride";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnAdd;
    RecyclerView rvProvidedRide;
    TextView tvNoRide;
    ProgressBar progressBar;

    //vars
    FirebaseAuth mAuth;
    ArrayList<ProvidedRide> mProvidedRides;
    User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_schedule_provided_ride);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnAdd = (RelativeLayout) findViewById(R.id.btnAdd);
        tvNoRide = (TextView) findViewById(R.id.tvNoRide);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        rvProvidedRide = (RecyclerView) findViewById(R.id.rvProvidedRide);

        mAuth = FirebaseAuth.getInstance();

        getCurrentUserDetail();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if(menuItemIndex == 0){
            cancelRideConfirm(mProvidedRides.get(info.position));
        }

        return true;
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

        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentUser.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_verified)) {
                    startActivity(new Intent(ProvideRideActivity.this, AddProvideRideActivity.class));
                }
                else {
                    if(mCurrentUser.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)) {
                        new AlertDialog.Builder(ProvideRideActivity.this)
                                .setTitle("Verify your license")
                                .setMessage("Please complete your license verification to proceed.")
                                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(ProvideRideActivity.this, VerifyLicenseActivity.class));
                                    }
                                })
                                .setNegativeButton("Not now", null)
                                .show();
                    }
                    else {
                        new AlertDialog.Builder(ProvideRideActivity.this)
                                .setTitle("Verify your identity")
                                .setMessage("Please complete your identity and license verification to proceed.")
                                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(ProvideRideActivity.this, VerifyMatrixActivity.class));
                                    }
                                })
                                .setNegativeButton("Not now", null)
                                .show();
                    }
                }
            }
        });

        if(mProvidedRides.isEmpty()){
            tvNoRide.setVisibility(View.VISIBLE);
        }
        else{
            tvNoRide.setVisibility(View.GONE);
        }
    }

    private void getUserProvidedRide() {
        Log.d(TAG, "getUserProvidedRide: getting mUser provided ride list");

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID)
                .equalTo(mAuth.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProvidedRides = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProvidedRide ride = ds.getValue(ProvidedRide.class);
                    if(ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                        ride.setPr_id(ds.getKey());
                        mProvidedRides.add(ride);
                    }
                }

                Collections.sort(mProvidedRides);
                init();
                initProvidedRidesCardList();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retriving mUser provided ride");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initProvidedRidesCardList(){
        Log.d(TAG, "initProvidedRidesCardList: initializing provided ride card list");
        ScheduleProvidedRideRVAdapter adapter = new ScheduleProvidedRideRVAdapter(this, mProvidedRides);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvProvidedRide.setLayoutManager(layoutManager);
        rvProvidedRide.setNestedScrollingEnabled(false);
        rvProvidedRide.setItemAnimator(new DefaultItemAnimator());
        rvProvidedRide.setAdapter(adapter);
    }

    private void cancelRideConfirm(final ProvidedRide ride){
        new AlertDialog.Builder(this)
                .setTitle("Cancel Ride")
                .setMessage("Cancel this ride might affect the mUser who already request for this ride. \n" +
                        "Are you sure you want to proceed?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        cancelRide(ride.getPr_id());
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void cancelRide(final String pr_id){

        Log.d(TAG, "cancelRide: canceling");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .child(pr_id);
        ref.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProvideRideActivity.this, "Ride canceled", Toast.LENGTH_SHORT).show();
                        //hideProgressBar();
                    }
                });

    }

    private void getCurrentUserDetail(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(mAuth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                getUserProvidedRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on retreiving mUser information -> " + databaseError);
                Toast.makeText(ProvideRideActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
