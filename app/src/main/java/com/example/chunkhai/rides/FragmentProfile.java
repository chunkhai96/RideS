package com.example.chunkhai.rides;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.ListAdapter.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentProfile extends Fragment{
    private static final String TAG = "FragmentProfile";

    //widgets
    ListView lvProfileTop;
    ListView lvProfileMiddle;
    ProgressBar progressBar;


    //vars
    String[] profileName;
    String[] userId;

    int[] menuIcons = {R.mipmap.ic_colorful_car, R.mipmap.ic_colorful_rate, R.mipmap.ic_colorful_setting};
    String[] menuTexts = {"Vehicle", "Rating & Review", "Setting"};

    int listContainerLayoutResourceId;
    int listTextId;
    int listIconId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: fragment profile");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        lvProfileTop = (ListView) view.findViewById(R.id.lvProfileTop);
        lvProfileMiddle = (ListView) view.findViewById(R.id.lvProfileMiddle);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        listContainerLayoutResourceId = R.layout.list_row_profile_middle;
        listTextId = R.id.tvProfileMiddleListItemText;
        listIconId = R.id.tvProfileMiddleListItemIcon;

        progressBar.setVisibility(View.VISIBLE);
        getUserProfileInfo();

        return view;
    }

    public void initProfileTopList(ListView list, final String[] profileName, String profileImageSrc){
        Log.d(TAG, "initProfileTopList: initializing");

        ProfileTopListAdapter listAdapter = new ProfileTopListAdapter(getContext(), profileName, profileImageSrc, userId);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initProfileMiddleList(ListView list, int[] drawables, String[] texts){
        Log.d(TAG, "initProfileMiddleList: initializing");
        DrawableTextListAdapter listAdapter = new DrawableTextListAdapter(getContext(),
                listContainerLayoutResourceId, listIconId, listTextId, drawables, texts);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent;

                switch (position){
                    case 0:
                        Log.d(TAG, "onItemClick: Vehicle clicked");
                        intent = new Intent(getActivity(), VehicleActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Log.d(TAG, "onItemClick: Rating & Review clicked");
                        intent = new Intent(getActivity(), RatingReviewActivity.class);
                        intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                    case 2:
                        Log.d(TAG, "onItemClick: Setting clicked");
                        intent = new Intent(getActivity(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void getUserProfileInfo(){
        Log.d(TAG, "getUserProfileInfo: getting data from database");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference().child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    String profile_name = dataSnapshot.child(DatabaseHelper.TABLE_USER_ATTR_PROFILE_NAME).getValue(String.class);
                    profileName = new String[]{profile_name};
                    String profileImageSrc = dataSnapshot.child(DatabaseHelper.TABLE_USER_ATTR_PROFILE_PIC_SRC).getValue(String.class);

                    initProfileTopList(lvProfileTop, profileName, profileImageSrc);
                    initProfileMiddleList(lvProfileMiddle, menuIcons, menuTexts);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error: " + databaseError);
            }
        });
    }
}
