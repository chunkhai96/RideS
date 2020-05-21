package com.example.chunkhai.rides;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.ScheduleOngoingRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentScheduleOngoingRide extends Fragment{
    private static final String TAG = "FragmentScheduleOngoing";

    //widgets
    TextView tvNoRide;
    RecyclerView rvOngoingRide;
    ProgressBar progressBar;

    //vars
    ArrayList<ProvidedRide> mOngoingRides = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_ongoing_ride, container, false);

        tvNoRide = (TextView) view.findViewById(R.id.tvNoRide);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        rvOngoingRide = (RecyclerView) view.findViewById(R.id.rvOngoingRide);

        getUserOngoingRide();

        return view;
    }

    private void getUserOngoingRide(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_CARPOOLER_UID)
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    final ArrayList<String> requestedRideIds = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sharedRide = ds.getValue(SharedRide.class);
                        if (sharedRide.getRs_status() == getResources().getInteger(R.integer.sharedride_status_accept)) {
                            requestedRideIds.add(sharedRide.getRs_providedRideId());
                        }
                    }

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DatabaseHelper.TABLE_PROVIDEDRIDE);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Activity activity = getActivity();
                            if(activity != null && isAdded()) {
                                mOngoingRides = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ProvidedRide ride = ds.getValue(ProvidedRide.class);
                                    ride.setPr_id(ds.getKey());

                                    if (ride.getPr_providerUid().matches(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        if (ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                                            mOngoingRides.add(ride);
                                        }
                                    } else {
                                        for (String rideId : requestedRideIds) {
                                            if (ride.getPr_id().matches(rideId)) {
                                                if (ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)
                                                        || ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_complete))
                                                    mOngoingRides.add(ride);
                                            }
                                        }
                                    }
                                }
                                Collections.sort(mOngoingRides);
                                initProvidedRidesCardList();
                                progressBar.setVisibility(View.GONE);

                                checkEmptyList();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: error: " + databaseError);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error: " + databaseError);
            }
        });
    }

    private void initProvidedRidesCardList(){
        Log.d(TAG, "initProvidedRidesCardList: initializing provided ride card list");
        ScheduleOngoingRVAdapter adapter = new ScheduleOngoingRVAdapter(getContext(), mOngoingRides);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        rvOngoingRide.setLayoutManager(layoutManager);
        rvOngoingRide.setNestedScrollingEnabled(false);
        rvOngoingRide.setItemAnimator(new DefaultItemAnimator());
        rvOngoingRide.setAdapter(adapter);
    }

    private void checkEmptyList(){
        if(mOngoingRides.isEmpty()){
            tvNoRide.setVisibility(View.VISIBLE);
        }
        else {
            tvNoRide.setVisibility(View.GONE);
        }
    }
}
