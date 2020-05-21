package com.example.chunkhai.rides;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
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

public class FragmentScheduleProvidedRide extends Fragment{
    private static final String TAG = "FragmentScheduleProvide";

    //widgets
    RecyclerView rvProvidedRide;
    TextView tvNoRide;
    ProgressBar progressBar;
    FloatingActionButton fabAddRide;

    //vars
    FirebaseAuth mAuth;
    ArrayList<ProvidedRide> mProvidedRides;
    User mCurrentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_provided_ride, container, false);

        tvNoRide = (TextView) view.findViewById(R.id.tvNoRide);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        rvProvidedRide = (RecyclerView) view.findViewById(R.id.rvProvidedRide);

        fabAddRide =(FloatingActionButton) view.findViewById(R.id.fabAddRide);

        mAuth = FirebaseAuth.getInstance();

        getCurrentUserDetail();

        return view;
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

        if(mProvidedRides.isEmpty()){
            tvNoRide.setVisibility(View.VISIBLE);
        }
        else{
            tvNoRide.setVisibility(View.GONE);
        }

        fabAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddProvideRideActivity.class);
                startActivity(intent);
            }
        });
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
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    mProvidedRides = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ProvidedRide ride = ds.getValue(ProvidedRide.class);
                        if (ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                            ride.setPr_id(ds.getKey());
                            mProvidedRides.add(ride);
                        }
                    }

                    Collections.sort(mProvidedRides);
                    init();
                    initProvidedRidesCardList();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retriving mUser provided ride");
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initProvidedRidesCardList(){
        Log.d(TAG, "initProvidedRidesCardList: initializing provided ride card list");
        ScheduleProvidedRideRVAdapter adapter = new ScheduleProvidedRideRVAdapter(getContext(), mProvidedRides);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        rvProvidedRide.setLayoutManager(layoutManager);
        rvProvidedRide.setNestedScrollingEnabled(false);
        rvProvidedRide.setItemAnimator(new DefaultItemAnimator());
        rvProvidedRide.setAdapter(adapter);
    }

    private void cancelRideConfirm(final ProvidedRide ride){
        new AlertDialog.Builder(getContext())
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
                        Toast.makeText(getContext(), "Ride canceled", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
