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
import com.example.chunkhai.rides.Util.RecycleViewAdapter.SchedulePendingRequestRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentSchedulePendingRequest extends Fragment{
    private static final String TAG = "FragmentSchedulePending";

    //widgets
    TextView tvNoYourRequest;
    RecyclerView rvPendingRequest;
    ProgressBar progressBar;

    //vars
    FirebaseAuth mAuth;
    int yourRequestListContainerLayoutResourceId;
    ArrayList<ProvidedRide> mYourRequests = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_pending_request, container, false);

        tvNoYourRequest = (TextView) view.findViewById(R.id.tvNoYourRequest);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        rvPendingRequest = (RecyclerView) view.findViewById(R.id.rvPendingRequest);

        mAuth = FirebaseAuth.getInstance();

        getUserYourRequests();

        return view;
    }

    private void getUserYourRequests() {
        Log.d(TAG, "getUserYourRequests: getting mUser pending requests");

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_CARPOOLER_UID)
                .equalTo(mAuth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> requestedRideIds = new ArrayList<>();
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sharedRide = ds.getValue(SharedRide.class);
                        if (sharedRide.getRs_status() == getResources().getInteger(R.integer.sharedride_status_requested)) {
                            requestedRideIds.add(sharedRide.getRs_providedRideId());
                        }
                    }


                    if (!requestedRideIds.isEmpty()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query query = ref.child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                                .orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_DEPART_TIMESTAMP)
                                .startAt(System.currentTimeMillis());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Activity activity = getActivity();
                                if (activity != null && isAdded()) {
                                    mYourRequests = new ArrayList<>();
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            ProvidedRide ride = ds.getValue(ProvidedRide.class);
                                            ride.setPr_id(ds.getKey());
                                            for (String rideId : requestedRideIds) {
                                                if (ride.getPr_id().matches(rideId) &&
                                                        ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                                                    mYourRequests.add(ride);
                                                }
                                            }
                                        }
                                    }
                                    Collections.sort(mYourRequests);
                                    initProvidedRidesCardList();
                                    checkEmptyList();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: error: " + databaseError);
                            }

                        });
                    } else {
                        mYourRequests = new ArrayList<>();
                        initProvidedRidesCardList();
                        checkEmptyList();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on getting mUser shared ride");
            }
        });
    }

    private void initProvidedRidesCardList(){
        Log.d(TAG, "initProvidedRidesCardList: initializing provided ride card list");
        SchedulePendingRequestRVAdapter adapter = new SchedulePendingRequestRVAdapter(getContext(), mYourRequests);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        rvPendingRequest.setLayoutManager(layoutManager);
        rvPendingRequest.setNestedScrollingEnabled(false);
        rvPendingRequest.setItemAnimator(new DefaultItemAnimator());
        rvPendingRequest.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    private void checkEmptyList(){
        if(mYourRequests.isEmpty()){
            tvNoYourRequest.setVisibility(View.VISIBLE);
        }
        else {
            tvNoYourRequest.setVisibility(View.GONE);
        }
    }
}
