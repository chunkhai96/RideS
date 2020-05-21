package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.Notification;
import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.CarpoolerRVAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cdflynn.android.library.checkview.CheckView;

public class RideDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map ready");
        mMap = googleMap;

        getRideDetail(pr_id);
    }

    private static final String TAG = "RideDetailActivity";
    private final String TITLE = "Ride Detail";
    private final int CAMERA_PADDING = 300;
    private final int SMALL_SCREEN_CAMERA_PADDING = 100;

    //widgets
    AppBarLayout appBarLayout;
    RelativeLayout rlAppBar;
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnRequest;
    RelativeLayout btnDiscussion;
    TextView tvRequestCount;
    CoordinatorLayout coordinatorLayout;
    ProgressBar progressBar;
    ImageView ivSteering, ivPassenger, ivDone;
    RelativeLayout rlCancelled;
    LinearLayout linLayoutProvider;
    ImageView ivProviderProfileImage;
    TextView tvProviderName, tvProviderMatrixNo, tvProviderPhoneNo;
    ImageView ivMale, ivFemale;
    Button btnViewProfile;
    TextView tvDateTitle, tvDepartureDatetime, tvFromTitle, tvFromCaption, tvToTitle, tvToCaption, tvShareRm, tvShareSen;
    ImageView ivVehicleImage;
    TextView tvVehiclePlateNo, tvVehicleInfo;
    private RecyclerView recyclerView;
    private CarpoolerRVAdapter adapter;
    TextView tvEmptyCarpooler;
    Button btnJoin, btnCancelRequest, btnCancelRide, btnComplete;
    CheckView checkAnimation;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    NestedScrollView nestedScrollView;
    ImageView ivMinus;

    //vars
    String pr_id;
    ProvidedRide mRide;
    User mProvider;
    User mCurrentUser;
    Vehicle mVehicle;
    FirebaseAuth mAuth;
    long globalCounter;
    String mNote;
    ArrayList<SharedRide> mAgreedSharedRides;
    ArrayList<User> mCarpoolers = new ArrayList<>();
    long mSharedRidesSize;
    long mRequestersSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        rlAppBar = (RelativeLayout) findViewById(R.id.relLayout1);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnRequest = (RelativeLayout) findViewById(R.id.btnRequest);
        btnDiscussion = (RelativeLayout) findViewById(R.id.btnDiscussion);
        tvRequestCount = (TextView) findViewById(R.id.tvRequestCount);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        nestedScrollView= (NestedScrollView) findViewById(R.id.nestedScrollView);
        ivMinus = (ImageView) findViewById(R.id.ivMinus);

        ivSteering = (ImageView) findViewById(R.id.ivSteering);
        ivPassenger = (ImageView) findViewById(R.id.ivPassenger);
        ivDone = (ImageView) findViewById(R.id.ivDone);
        rlCancelled = (RelativeLayout) findViewById(R.id.rlCancelled);

        linLayoutProvider = (LinearLayout) findViewById(R.id.linLayoutProvider);
        ivProviderProfileImage = (ImageView) findViewById(R.id.ivProviderProfileImage);
        tvProviderName = (TextView) findViewById(R.id.tvProviderName);
        tvProviderMatrixNo = (TextView) findViewById(R.id.tvProviderMatrixNo);
        tvProviderPhoneNo = (TextView) findViewById(R.id.tvProviderPhoneNo);
        ivMale = (ImageView) findViewById(R.id.ivMale);
        ivFemale = (ImageView) findViewById(R.id.ivFemale);

        tvDateTitle = (TextView) findViewById(R.id.tvDateTitle);
        tvFromTitle = (TextView) findViewById(R.id.tvFromTitle);
        tvFromCaption = (TextView) findViewById(R.id.tvFromCaption);
        tvToTitle = (TextView) findViewById(R.id.tvToTitle);
        tvToCaption = (TextView) findViewById(R.id.tvToCaption);
        tvDepartureDatetime = (TextView) findViewById(R.id.tvDepartureDateTime);
        tvShareRm = (TextView) findViewById(R.id.tvShareRm);
        tvShareSen = (TextView) findViewById(R.id.tvShareSen);
        btnViewProfile = (Button) findViewById(R.id.btnViewProfile);

        ivVehicleImage = (ImageView) findViewById(R.id.ivVehicleImage);
        tvVehiclePlateNo = (TextView) findViewById(R.id.tvVehiclePlateNo);
        tvVehicleInfo = (TextView) findViewById(R.id.tvVehicleInfo);

        tvEmptyCarpooler = (TextView) findViewById(R.id.tvEmptyCarpooler);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnCancelRequest = (Button) findViewById(R.id.btnCancelRequest);
        btnCancelRide = (Button) findViewById(R.id.btnCancelRide);
        btnComplete = (Button) findViewById(R.id.btnComplete);

        checkAnimation = (CheckView) findViewById(R.id.checkAnimation);

        mAuth = FirebaseAuth.getInstance();

        pr_id = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID);

        init();
        initMap();
    }

    private void init(){
        Log.d(TAG, "init: initializing toolbar and other widgets");

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(nestedScrollView);

        appBarLayout.setBackground(getResources().getDrawable(R.drawable.transparent_toolbar));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorBlackWithTransparency));
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if(i == BottomSheetBehavior.STATE_EXPANDED){
                    appBarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                }
                if(i == BottomSheetBehavior.STATE_COLLAPSED){
                    appBarLayout.setBackground(getResources().getDrawable(R.drawable.transparent_toolbar));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(getResources().getColor(R.color.colorBlackWithTransparency));
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        showProgressBar();

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDiscussion.setVisibility(View.VISIBLE);
        btnDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RideDetailActivity.this, RideDiscussionActivity.class);
                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, mRide.getPr_id());
                startActivity(intent);
            }
        });

    }

    private void initMap() {
        Log.d(TAG, "initMap: map start");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void getRideDetail(final String pr_id) {
        Log.d(TAG, "getUserProvidedRide: getting detail information");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .child(pr_id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRide = dataSnapshot.getValue(ProvidedRide.class);
                mRide.setPr_id(dataSnapshot.getKey());

                getProviderInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retriving search ride result");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProviderInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(mRide.getPr_providerUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProvider = dataSnapshot.getValue(User.class);
                mProvider.setUser_uid(dataSnapshot.getKey());

                getVehicleDetail(mRide.getPr_vehicleId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving provider info");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVehicleDetail(final String plate_no) {
        Log.d(TAG, "getVehicleDetail: getting detail information");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_VEHICLE)
                .child(plate_no);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mVehicle = dataSnapshot.getValue(Vehicle.class);
                mVehicle.setVehicle_plateNo(dataSnapshot.getKey());
                getCurrentUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving search ride result");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentUserInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                mCurrentUser.setUser_uid(dataSnapshot.getKey());


                updatePageInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving current mUser info");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAcceptedSharedRide(){
        mSharedRidesSize = 0;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                        .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                        .equalTo(mRide.getPr_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: accepted shared ride " + dataSnapshot.getChildrenCount());
                mSharedRidesSize = dataSnapshot.getChildrenCount();
                mAgreedSharedRides = new ArrayList<>();
                mCarpoolers = new ArrayList<>();
                mRequestersSize = 0;
                globalCounter = 0;
                if(mSharedRidesSize > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sr = ds.getValue(SharedRide.class);
                        sr.setRs_id(ds.getKey());
                        if (sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_accept)
                            || sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_complete)) {
                            mAgreedSharedRides.add(sr);
                        }
                        if(sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_requested)){
                            mRequestersSize++;
                        }
                        globalCounter++;
                        if(globalCounter == mSharedRidesSize){
                            getCarpoolerDetail();
                        }
                    }
                }
                else{
                    getCarpoolerDetail();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on retrieving shared ride list");
            }
        });

    }

    private void getCarpoolerDetail(){
        Log.d(TAG, "getCarpoolerDetail: getting carpooler detail from accepted shared ride");
        globalCounter = 0;
        if(mAgreedSharedRides.size() != 0) {
            for (int i = 0; i < mAgreedSharedRides.size(); i++) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(DatabaseHelper.TABLE_USER)
                        .child(mAgreedSharedRides.get(i).getRs_carpoolerUid());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: retrieving carpooler info " + dataSnapshot.getKey());
                        User carpooler = dataSnapshot.getValue(User.class);
                        carpooler.setUser_uid(dataSnapshot.getKey());
                        mCarpoolers.add(carpooler);

                        globalCounter++;
                        if (globalCounter == mAgreedSharedRides.size()) {
                            initCarpoolerCard();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: Error on retrieving mUser info");
                    }
                });
            }
        }
        else {
            initCarpoolerCard();
        }
    }

    private void updatePageInfo(){

        getAcceptedSharedRide();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mRide.getPr_departTimestamp());
        tvDateTitle.setText(getDatetimeFromTimestamp(calendar, 2));
        tvDepartureDatetime.setText(getDatetimeFromTimestamp(calendar, 1));
        tvFromTitle.setText(mRide.getPr_fromPlace());
        tvFromCaption.setText(mRide.getPr_fromAddress());
        tvToTitle.setText(mRide.getPr_toPlace());
        tvToCaption.setText(mRide.getPr_toAddress());
        initMarquee();
        String sharedFare = Double.toString(mRide.getPr_sharedFare());
        List<String> split = Arrays.asList(sharedFare.split("\\."));
        tvShareRm.setText(split.get(0));
        tvShareSen.setText(" " + split.get(1) + "0");

        Glide.with(getApplicationContext())
                .load(mVehicle.getVehicle_imageSrc())
                .apply(new RequestOptions().optionalCenterCrop())
                .into(ivVehicleImage);
        tvVehiclePlateNo.setText(mVehicle.getVehicle_plateNo());
        String vehicleInfo = mVehicle.getVehicle_model() + " &bull; " + mVehicle.getVehicle_color() + " &bull; " + mVehicle.getVehicle_capacity();
        tvVehicleInfo.setText(Html.fromHtml(vehicleInfo));

        LatLng fromLatLng = new LatLng(mRide.getPr_fromLatitude(), mRide.getPr_fromLongitude());
        LatLng toLatLng = new LatLng(mRide.getPr_toLatitude(), mRide.getPr_toLongitude());
        setMarker(fromLatLng, toLatLng);

        if(mRide.getPr_providerUid().matches(FirebaseAuth.getInstance().getUid())) {
            linLayoutProvider.setVisibility(View.GONE);
            btnRequest.setVisibility(View.VISIBLE);
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RideDetailActivity.this, PendingRequestActivity.class);
                    intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, mRide.getPr_id());
                    intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID, mRide.getPr_providerUid());
                    intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_VEHICLE_ID, mRide.getPr_vehicleId());
                    intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_PROFILE_NAME, mCurrentUser.getUser_profileName());
                    startActivity(intent);
                }
            });
            ivSteering.setVisibility(View.VISIBLE);

            if (mRide.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                if (mRide.getPr_departTimestamp() > System.currentTimeMillis()) {
                    showCancelRideButton();
                } else {
                    showCompleteButton();
                }
            }
            else if(mRide.getPr_status() == getResources().getInteger(R.integer.providedride_status_complete)){
                showDone();
            }
            else if(mRide.getPr_status() == getResources().getInteger(R.integer.providedride_status_cancel)){
                showCancelled();
            }
        }
        else {
            if(mProvider.getUser_profilePicSrc() == null || mProvider.getUser_profilePicSrc().equals("")){
                ivProviderProfileImage.setImageDrawable(getResources().getDrawable(R.mipmap.img_default_vehicle_pic));
            }
            else {
                Glide.with(getApplicationContext())
                        .load(mProvider.getUser_profilePicSrc())
                        .apply(new RequestOptions().optionalCenterCrop())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProviderProfileImage);
            }
            tvProviderName.setText(mProvider.getUser_profileName());
            tvProviderMatrixNo.setText(mProvider.getUser_matrixNo());
            tvProviderPhoneNo.setText(mProvider.getUser_phoneNo());
            if(mProvider.getUser_gender().matches(getResources().getString(R.string.male))){
                ivMale.setVisibility(View.VISIBLE);
                ivFemale.setVisibility(View.GONE);
            }
            else {
                ivFemale.setVisibility(View.VISIBLE);
                ivMale.setVisibility(View.GONE);
            }

            btnViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RideDetailActivity.this, ViewProfile.class);
                    intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, mProvider.getUser_uid());
                    startActivity(intent);
                }
            });
            if(mRide.getPr_status() != getResources().getInteger(R.integer.providedride_status_cancel)) {
                checkRequestStatus();
            }
            else {
                showCancelled();
                hideProgressBar();
            }
        }
    }

    private void checkRequesterNum(){
        if (mRequestersSize > 0) {
            tvRequestCount.setVisibility(View.VISIBLE);
            tvRequestCount.setText(Long.toString(mRequestersSize));
        }
        else {
            tvRequestCount.setVisibility(View.GONE);
        }
    }

    private String getDatetimeFromTimestamp(Calendar calendar, int type /*1=full datetime, 2=date month*/) {
        String format = "";
        if(type == 1) {
            format = "dd MMM yyyy (E) - hh:mm a";
        }
        else if(type == 2){
            format = "dd, MMM";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(calendar.getTime());
    }

    private void setMarker(LatLng fromLatLng, LatLng toLatLng){
        MarkerOptions fromMarkerOption = new MarkerOptions()
                .position(fromLatLng)
                .title("Drive from")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_steering));
        MarkerOptions toMarkerOption = new MarkerOptions()
                .position(toLatLng)
                .title("Destination");
        Marker fromMarker = mMap.addMarker(fromMarkerOption);
        Marker toMarker = mMap.addMarker(toMarkerOption);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(fromMarker.getPosition());
        builder.include(toMarker.getPosition());
        LatLngBounds bounds = builder.build();

        try {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_PADDING);
            mMap.animateCamera(cu);
        }
        catch (Exception e){
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,SMALL_SCREEN_CAMERA_PADDING);
            mMap.animateCamera(cu);
        }


    }

    private void checkRequestStatus(){
        Log.d(TAG, "checkRequestStatus: check request status");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                .equalTo(mRide.getPr_id());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide ride = ds.getValue(SharedRide.class);
                        ride.setRs_id(ds.getKey());
                        if (ride.getRs_carpoolerUid().matches(FirebaseAuth.getInstance().getUid())) {
                            Log.d(TAG, "onDataChange: ");
                            if (ride.getRs_status() == getResources().getInteger(R.integer.sharedride_status_requested)) {
                                showCancelRequestButton();
                            }
                            else if (ride.getRs_status() == getResources().getInteger(R.integer.sharedride_status_cancel)){
                                showJoinButton();
                            }
                            else if (ride.getRs_status() == getResources().getInteger(R.integer.sharedride_status_accept)) {
                                ivPassenger.setVisibility(View.VISIBLE);
                                if(mRide.getPr_departTimestamp() > System.currentTimeMillis()) {
                                    showCancelRequestButton();
                                }
                                else {
                                    showCompleteButton();
                                }
                            }
                            else if (ride.getRs_status() == getResources().getInteger(R.integer.sharedride_status_decline)){
                                showJoinButton();
                            }
                            else if (ride.getRs_status() == getResources().getInteger(R.integer.sharedride_status_complete)){
                                ivPassenger.setVisibility(View.VISIBLE);
                                showDone();
                            }
                            break;
                        }
                        else {
                            showJoinButton();
                        }
                    }
                }
                else {
                    showJoinButton();
                }

                hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving mUser provided ride");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showJoinButton(){
        btnJoin.setVisibility(View.VISIBLE);
        btnCancelRequest.setVisibility(View.GONE);
        btnCancelRide.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);

        ivDone.setVisibility(View.GONE);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJoinRideNoteDialog();
            }
        });
    }

    private void showCancelRequestButton(){
        btnCancelRequest.setVisibility(View.VISIBLE);
        btnJoin.setVisibility(View.GONE);
        btnCancelRide.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);

        ivDone.setVisibility(View.GONE);

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequest();
            }
        });
    }

    private void showCancelRideButton(){
        btnCancelRide.setVisibility(View.VISIBLE);
        btnJoin.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);

        ivDone.setVisibility(View.GONE);

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRide();
            }
        });
    }

    private void showCompleteButton(){
        btnComplete.setVisibility(View.VISIBLE);
        btnJoin.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.GONE);
        btnCancelRide.setVisibility(View.GONE);

        ivDone.setVisibility(View.GONE);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeRide();
            }
        });
    }

    private void showDone(){
        ivDone.setVisibility(View.VISIBLE);
        btnJoin.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.GONE);
        btnCancelRide.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);
    }

    private void showCancelled(){
        rlCancelled.setVisibility(View.VISIBLE);
        btnJoin.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.GONE);
        btnCancelRide.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);
    }

    private void joinRide(){
        Log.d(TAG, "joinRide: add new request");
        if(mCurrentUser.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)) {
            showProgressBar();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                    .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                    .equalTo(mRide.getPr_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean joined = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sr = ds.getValue(SharedRide.class);
                        sr.setRs_id(ds.getKey());
                        if (sr.getRs_carpoolerUid().matches(FirebaseAuth.getInstance().getUid())) {
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference ref = database.getReference().child(DatabaseHelper.TABLE_SHAREDRIDE)
                                    .child(sr.getRs_id());
                            try {
                                ref.child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_STATUS)
                                        .setValue(getResources().getInteger(R.integer.sharedride_status_requested));
                                ref.child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_NOTE)
                                        .setValue(mNote);
                                ref.child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_CREATED_TIMESTAMP)
                                        .setValue(System.currentTimeMillis());
                                joined = true;
                                Toast.makeText(RideDetailActivity.this, "Request is sent"
                                        , Toast.LENGTH_SHORT).show();

                                sendNewRideRequestNotification();
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (!joined) {
                        String carpoolerUid = mAuth.getUid();
                        long currentTimestamp = System.currentTimeMillis();
                        SharedRide sharedRide = new SharedRide(mRide.getPr_id(), carpoolerUid, mNote
                                , currentTimestamp, getResources().getInteger(R.integer.sharedride_status_requested));
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child(DatabaseHelper.TABLE_SHAREDRIDE)
                                .push()
                                .setValue(sharedRide)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(RideDetailActivity.this, "Request is sent"
                                                , Toast.LENGTH_SHORT).show();

                                        sendNewRideRequestNotification();
                                    }
                                })
                                .addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {
                                        Toast.makeText(RideDetailActivity.this, "Error on requesting ride"
                                                , Toast.LENGTH_SHORT).show();

                                        hideProgressBar();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            new AlertDialog.Builder(RideDetailActivity.this)
                    .setTitle("Verify your identity")
                    .setMessage("You must verify your identity to request share ride")
                    .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(RideDetailActivity.this, SettingActivity.class));
                        }
                    })
                    .setNegativeButton("Not now", null)
                    .show();
        }
    }

    private void cancelRequest(){
        if(mRide.getPr_departTimestamp() > System.currentTimeMillis()) {
            showProgressBar();
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Request")
                    .setMessage("Are you sure you want to proceed?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d(TAG, "cancelRequest: canceling ride request");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                                    .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                                    .equalTo(mRide.getPr_id());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        SharedRide sr = ds.getValue(SharedRide.class);
                                        sr.setRs_id(ds.getKey());
                                        if (sr.getRs_carpoolerUid().matches(FirebaseAuth.getInstance().getUid())) {
                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            final DatabaseReference ref = database.getReference().child(DatabaseHelper.TABLE_SHAREDRIDE)
                                                    .child(sr.getRs_id())
                                                    .child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_STATUS);
                                            ref.setValue(getResources().getInteger(R.integer.sharedride_status_cancel))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(RideDetailActivity.this, "Request canceled", Toast.LENGTH_SHORT).show();
                                                            sendCancelRequestNotification();
                                                        }
                                                    });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(RideDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
        else {
            showCompleteButton();
            hideProgressBar();
            Toast.makeText(RideDetailActivity.this, "This ride is started", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelRide(){
        if(mRide.getPr_departTimestamp() > System.currentTimeMillis()) {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Ride")
                    .setMessage("Cancel this ride might affect the mUser who already request for this ride. \n" +
                            "Are you sure you want to proceed?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.d(TAG, "cancelRide: canceling");
                            showProgressBar();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                    .child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                                    .child(mRide.getPr_id())
                                    .child(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_STATUS);
                            ref.setValue(getResources().getInteger(R.integer.providedride_status_cancel))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RideDetailActivity.this, "Ride canceled", Toast.LENGTH_SHORT).show();
                                            sendCancelRideNotification();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
        else {
            showCompleteButton();
            hideProgressBar();
            Toast.makeText(RideDetailActivity.this, "This ride is started", Toast.LENGTH_SHORT).show();
        }

    }

    private void completeRide(){
        Log.d(TAG, "completeRide: completing the ride");
        showProgressBar();

        if (mRide.getPr_providerUid().matches(FirebaseAuth.getInstance().getUid())) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                    .child(mRide.getPr_id())
                    .child(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_STATUS);
            ref.setValue(getResources().getInteger(R.integer.providedride_status_complete))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RideDetailActivity.this, "Ride completed", Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                            showCheckAnimation();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                                    .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                                    .equalTo(pr_id);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            SharedRide sr = ds.getValue(SharedRide.class);
                                            sr.setRs_id(ds.getKey());
                                            if (sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_accept)
                                                    || sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_complete)) {
                                                Intent intent = new Intent(RideDetailActivity.this, RatingActivity.class);
                                                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, mRide.getPr_id());
                                                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID, mRide.getPr_providerUid());
                                                startActivity(intent);
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                    .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                    .equalTo(mRide.getPr_id());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sr = ds.getValue(SharedRide.class);
                        sr.setRs_id(ds.getKey());
                        if (sr.getRs_carpoolerUid().matches(FirebaseAuth.getInstance().getUid())) {
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference ref = database.getReference().child(DatabaseHelper.TABLE_SHAREDRIDE)
                                    .child(sr.getRs_id());
                            try {
                                ref.child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_STATUS)
                                        .setValue(getResources().getInteger(R.integer.sharedride_status_complete));
                                Toast.makeText(RideDetailActivity.this, "Ride completed", Toast.LENGTH_SHORT).show();
                                hideProgressBar();
                                showCheckAnimation();
                                Intent intent = new Intent(RideDetailActivity.this, RatingActivity.class);
                                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, mRide.getPr_id());
                                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_PROVIDER_UID, mRide.getPr_providerUid());
                                startActivity(intent);


                            } catch (Exception e) {
                                e.printStackTrace();
                                hideProgressBar();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    hideProgressBar();
                    Toast.makeText(RideDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendNewRideRequestNotification(){
        Notification notif = new Notification();
        notif.setNotif_type(getResources().getInteger(R.integer.notification_type_newRideRequest));
        notif.setNotif_createdTimestamp(System.currentTimeMillis());
        notif.setNotif_sender_uid(mCurrentUser.getUser_uid());
        notif.setNotif_status(getResources().getInteger(R.integer.notification_status_new));
        notif.setNotif_ref_providedRideId(mRide.getPr_id());
        String message = "<b>" + mCurrentUser.getUser_profileName() + "</b> sent you a carpool request";
        notif.setNotif_message(message);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DatabaseHelper.TABLE_NOTIFICATION).child(mProvider.getUser_uid())
                .push()
                .setValue(notif)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressBar();
                        //send notification
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled: Fail to send notification");
                    }
                });
    }

    private void sendCancelRideNotification(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(DatabaseHelper.TABLE_SHAREDRIDE)
                            .orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_PROVIDED_RIDE_ID)
                            .equalTo(mRide.getPr_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: retrieving shared ride list " + dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sr = ds.getValue(SharedRide.class);
                        sr.setRs_id(ds.getKey());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(DatabaseHelper.TABLE_SHAREDRIDE).child(sr.getRs_id())
                                .child(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_STATUS)
                                .setValue(getResources().getInteger(R.integer.sharedride_status_cancel));
                        Notification notif = new Notification();
                        notif.setNotif_type(getResources().getInteger(R.integer.notification_type_cancelRide));
                        notif.setNotif_createdTimestamp(System.currentTimeMillis());
                        notif.setNotif_sender_uid(mCurrentUser.getUser_uid());
                        notif.setNotif_status(getResources().getInteger(R.integer.notification_status_new));
                        notif.setNotif_ref_providedRideId(sr.getRs_providedRideId());
                        String message = "<b>" + mCurrentUser.getUser_profileName() + "</b> canceled a ride you requested.";
                        notif.setNotif_message(message);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child(DatabaseHelper.TABLE_NOTIFICATION).child(sr.getRs_carpoolerUid())
                                .push()
                                .setValue(notif)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        showCancelled();
                                        hideProgressBar();
                                        //send notification
                                    }
                                })
                                .addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {
                                        Log.d(TAG, "onCanceled: Fail to send notification");
                                    }
                                });
                    }
                }
                else {
                    showCancelled();
                    hideProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on retrieving shared ride list");
            }
        });
    }

    private void sendCancelRequestNotification(){
        Notification notif = new Notification();
        notif.setNotif_type(getResources().getInteger(R.integer.notification_type_cancelRequest));
        notif.setNotif_createdTimestamp(System.currentTimeMillis());
        notif.setNotif_sender_uid(mCurrentUser.getUser_uid());
        notif.setNotif_status(getResources().getInteger(R.integer.notification_status_new));
        notif.setNotif_ref_providedRideId(mRide.getPr_id());
        String message = "<b>" + mCurrentUser.getUser_profileName() + "</b> cancelled a carpool request";
        notif.setNotif_message(message);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DatabaseHelper.TABLE_NOTIFICATION).child(mProvider.getUser_uid())
                .push()
                .setValue(notif)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressBar();
                        //send notification
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled: Fail to send notification");
                    }
                });
    }

    private void showJoinRideNoteDialog(){
        Log.d(TAG, "showJoinRideNoteDialog: showing modal");

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_enter_join_ride_note)
                .setPositiveButton("JOIN", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                final EditText etNote = (EditText) dialog.findViewById(R.id.etNote);
                etNote.setText(getResources().getString(R.string.default_join_ride_note));

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");
                        if(etNote.getText().toString().matches("")){
                            mNote = " ";
                        }
                        else {
                            mNote = etNote.getText().toString();
                        }

                        joinRide();
                        dialog.dismiss();
                    }
                });

                Button btnNegative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                btnNegative.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog negative button clicked");
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    private void initMarquee(){

        tvDateTitle.setSelected(true); //prevent marquee run by default

        tvFromTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tvFromTitle.isSelected()) {
                    tvFromTitle.setSelected(true);
                }
                else {
                    tvFromTitle.setSelected(false);
                }
                tvFromCaption.setSelected(false);
                tvToTitle.setSelected(false);
                tvToCaption.setSelected(false);
            }
        });
        tvFromCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFromTitle.setSelected(false);
                if(!tvFromCaption.isSelected()) {
                    tvFromCaption.setSelected(true);
                }
                else {
                    tvFromCaption.setSelected(false);
                }
                tvToTitle.setSelected(false);
                tvToCaption.setSelected(false);
            }
        });
        tvToTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFromTitle.setSelected(false);
                tvFromCaption.setSelected(false);
                if(!tvToTitle.isSelected()) {
                    tvToTitle.setSelected(true);
                }
                else {
                    tvToTitle.setSelected(false);
                }
                tvToCaption.setSelected(false);
            }
        });
        tvToCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvFromTitle.setSelected(false);
                tvFromCaption.setSelected(false);
                tvToTitle.setSelected(false);
                if(!tvToCaption.isSelected()) {
                    tvToCaption.setSelected(true);
                }
                else {
                    tvToCaption.setSelected(false);
                }
            }
        });
    }

    private void initCarpoolerCard(){

        if(mCarpoolers.size() > 0){
            tvEmptyCarpooler.setVisibility(View.GONE);
        }
        else {
            tvEmptyCarpooler.setVisibility(View.VISIBLE);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        adapter = new CarpoolerRVAdapter(this, mCarpoolers);

        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        checkRequesterNum();

        hideProgressBar();
    }

    private void showCheckAnimation(){
        checkAnimation.check();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAnimation.uncheck();
            }
        }, 2000);
    }

    private void showProgressBar(){
        coordinatorLayout.setBackgroundColor(getResources().getColor(R.color.colorGreyWithTransparency));
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideProgressBar(){
        coordinatorLayout.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}