package com.example.chunkhai.rides;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.androidadvance.topsnackbar.TSnackbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.chunkhai.rides.Object.LocationLatLng;
import com.example.chunkhai.rides.Object.Notification;
import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.SharedRide;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.os.Looper.getMainLooper;

public class FragmentHome extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        if(!isInternetAvailable()){
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
        mMap = googleMap;
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right top
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 968, 60, 0);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(20000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        getAllRide();
        //getUnarchivedNotif();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            progressBar.setVisibility(View.GONE);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.d("LocationCallback", "Location: " + location.getLatitude() + " " + location.getLongitude());
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                //move map camera
                if(deviceLocationGranted == false) {
                    moveCamera(latLng, DEFAULT_ZOOM, MY_LOCATION);
                    deviceLocationGranted = true;
                }

                if(haveActiveRide){
                    Map<String, Double> currentLatLng = new HashMap<>();
                    currentLatLng.put("latitude", location.getLatitude());
                    currentLatLng.put("longitude", location.getLongitude());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("Location Tracking")
                            .child(activeRidePrId)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(String.valueOf(System.currentTimeMillis()))
                            .setValue(currentLatLng);
                }
            }
        }
    };

    private static final String TAG = "FragmentHome";

    public static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String CALL_PHONE= Manifest.permission.CALL_PHONE;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1111;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -100), new LatLng(71, 136));
    private static final String MY_LOCATION = "My Location";

    //vars
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean deviceLocationGranted = false;
    private Marker mMarker;
    private Boolean mLocationPermissionsGranted = false;
    private SupportMapFragment mapFragment;
    private ArrayList<ProvidedRide> mRides;
    private ArrayList<SharedRide> userSharedRides;
    private boolean haveActiveRide;
    private boolean haveRideViewStarted = false;
    private HashMap<String,Bitmap> hashmapUserIdProfileImg;
    private String activeRidePrId;
    Handler timeHandler;
    private showcaseCallbackListener mShowcaseCallbackListener;

    //widgets
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private TextView tvSearchRide;
    private  EditText etSearchFrom, etSearchTo;
    private  Button btnSearch;
    private FloatingActionButton fabAddRide;
    private RelativeLayout relLayoutSearch;
    private RelativeLayout relLayoutHaveRideBottomView;
    private TextView tvHaveRideViewDestination;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        tvSearchRide = (TextView) view.findViewById(R.id.tvSearchRide);
        etSearchFrom = (EditText) view.findViewById(R.id.etSearchFrom);
        etSearchTo = (EditText) view.findViewById(R.id.etSearchTo);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        fabAddRide = (FloatingActionButton) view.findViewById(R.id.fabAddRide);
        relLayoutSearch = (RelativeLayout) view.findViewById(R.id.relLayoutSearch);
        relLayoutHaveRideBottomView = (RelativeLayout) view.findViewById(R.id.relLayoutHaveRideBottomView);
        tvHaveRideViewDestination = (TextView) view.findViewById(R.id.tvHaveRideViewDestination);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        timeHandler = new Handler(getMainLooper());
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserCurrentRide();
                timeHandler.postDelayed(this, 60000);
            }
        }, 10);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (isServicesOK()) {
            init();
            initMap();
        }

        startShowcase();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: fragment home stopped");
        timeHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public interface showcaseCallbackListener {
        void homeDone();
    }

    public void setshowcaseCallbackListener(showcaseCallbackListener cal){
        this.mShowcaseCallbackListener = cal;
    }

    private void init(){

        tvSearchRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetBehavior.from(nestedScrollView)
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRide();
            }
        });

        fabAddRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddProvideRideActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServiceOK: checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and mUser can make map service request
            Log.d(TAG, "isServicesOK: Google Play Service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error but can be fixed
            Log.d(TAG, "isServicesOK: an error occurred but you can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            //error cant be resolved
            Toast.makeText(getContext(), "You cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initMap() {
        Log.d(TAG, "initMap: map start");
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals(MY_LOCATION)) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMarker = mMap.addMarker(options);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient: building google api client");
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (cm.getActiveNetworkInfo() != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void getUserCurrentRide(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_SHAREDRIDE);

        Query query = ref.orderByChild(DatabaseHelper.TABLE_SHAREDRIDE_ATTR_CARPOOLER_UID)
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    userSharedRides = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        SharedRide sr = ds.getValue(SharedRide.class);
                        sr.setRs_id(ds.getKey());
                        if (sr.getRs_status() == getResources().getInteger(R.integer.sharedride_status_accept)) {
                            userSharedRides.add(sr);
                        }
                    }


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(DatabaseHelper.TABLE_PROVIDEDRIDE);

                    Query query = ref.orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_DEPART_TIMESTAMP)
                            .endAt(System.currentTimeMillis());

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: query data change");
                            Activity activity = getActivity();
                            if (activity != null && isAdded()) {
                                haveActiveRide = false;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    ProvidedRide pr = ds.getValue(ProvidedRide.class);
                                    pr.setPr_id(ds.getKey());
                                    if (pr.getPr_providerUid().matches(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            && pr.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                                        showSnackbarRideStarted(pr.getPr_id());
                                        haveActiveRide = true;
                                        activeRidePrId = pr.getPr_id();
                                        if(!haveRideViewStarted) {
                                            startHaveRideView();
                                            haveRideViewStarted = true;
                                        }
                                        break;
                                    } else {
                                        for (SharedRide sr : userSharedRides) {
                                            if (sr.getRs_providedRideId().matches(pr.getPr_id())) {
                                                showSnackbarRideStarted(pr.getPr_id());
                                                haveActiveRide = true;
                                                activeRidePrId = pr.getPr_id();
                                                if(!haveRideViewStarted) {
                                                    startHaveRideView();
                                                    haveRideViewStarted = true;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: Error on getting active provided ride -> " + databaseError);
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on getting user shared ride -> " + databaseError);
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showSnackbarRideStarted(final String pr_id){
        TSnackbar snackbar = TSnackbar.make(coordinatorLayout, "A ride is started! ", TSnackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorOrange));
        snackbar.setAction("View Detail", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RideDetailActivity.class);
                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, pr_id);
                getContext().startActivity(intent);
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();


    }

    private void searchRide(){
        String placeFrom = etSearchFrom.getText().toString();
        String placeTo = etSearchTo.getText().toString();
        Intent intent = new Intent(getContext(), SearchRideActivity.class);
        intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_FROM_ADDRESS, placeFrom);
        intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_TO_ADDRESS, placeTo);
        startActivity(intent);
    }

    private void getAllRide() {
        Log.d(TAG, "getAllRide: getting ride");

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_DEPART_TIMESTAMP)
                .startAt(System.currentTimeMillis());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRides = new ArrayList<>();
                Activity activity = getActivity();
                if(activity != null && isAdded()) {
                    mMap.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ProvidedRide ride = ds.getValue(ProvidedRide.class);
                        if (ride.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)) {
                            ride.setPr_id(ds.getKey());
                            mRides.add(ride);
                        }
                    }
                    setMarkerForAllRides(mRides);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retriving search ride result");
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMarkerForAllRides(ArrayList<ProvidedRide> rides){
        for(ProvidedRide ride : rides){
            LatLng latLng = new LatLng(ride.getPr_fromLatitude(), ride.getPr_fromLongitude());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ride.getPr_departTimestamp());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(ride.getPr_id())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_silver));
            mMap.addMarker(markerOptions);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(getContext(), RideDetailActivity.class);
                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, marker.getTitle());
                startActivity(intent);
                return true;
            }
        });
    }

    private String getDatetimeFromTimestamp(Calendar calendar) {
        String format = "dd MMM yyyy (E) - hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(calendar.getTime());
    }

    private void startShowcase(){
        Log.d(TAG, "startShowcase: showcase started");

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setEnabled(false);
        
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "HOME");

        sequence.setConfig(config);

        sequence.addSequenceItem(fabAddRide, "Publish Ride",
                "Press + button to publish a ride", "GOT IT");

        sequence.addSequenceItem(tvSearchRide, "Search Ride",
                "Swipe up to show search panel", "GOT IT");

        sequence.addSequenceItem(relLayoutSearch, "Search Ride",
                "Enter the keyword in the box", "GOT IT");

        sequence.addSequenceItem(btnSearch, "Search Ride",
                "Press to search", "GOT IT");

        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
            @Override
            public void onDismiss(MaterialShowcaseView itemView, int position) {
                if(position == 1){
                    BottomSheetBehavior.from(nestedScrollView)
                            .setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if(position == 3){
                    mShowcaseCallbackListener.homeDone();
                }
            }
        });

        sequence.start();
    }

    private void startHaveRideView(){
        Log.d(TAG, "startHaveRideView: starting real time tracking view");
        //new features
        hashmapUserIdProfileImg = new HashMap<>();
        mMap.clear();
        fabAddRide.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.GONE);
        relLayoutHaveRideBottomView.setVisibility(View.VISIBLE);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(DatabaseHelper.TABLE_PROVIDEDRIDE).child(activeRidePrId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: Successfully retrieve active ride detail");
                        ProvidedRide pr = dataSnapshot.getValue(ProvidedRide.class);
                        tvHaveRideViewDestination.setText(pr.getPr_toPlace());
                        LatLng destination = new LatLng(pr.getPr_toLatitude(), pr.getPr_toLongitude());
                        tvHaveRideViewDestination.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!tvHaveRideViewDestination.isSelected()) {
                                    tvHaveRideViewDestination.setSelected(true);
                                } else {
                                    tvHaveRideViewDestination.setSelected(false);
                                }
                            }
                        });
                        mMap.addMarker(new MarkerOptions()
                                .position(destination)
                                .title("Destination"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: Failed to retrieve active ride detail -> " + databaseError);
                    }
                });


        final HashMap<String,Marker> hashMapMarker = new HashMap<>();
        DatabaseReference ltref = ref.child("Location Tracking").child(activeRidePrId);
        ltref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: get passenger location tracking list ("
                        + dataSnapshot.getChildrenCount() + "passenger)");
                //String passengerExcludeSelf[] = new String[(int)dataSnapshot.getChildrenCount()-1];
                int i = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    //passengerExcludeSelf[i] = ds.getKey();
                    final String passengerId = ds.getKey();
                    if(passengerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        continue;
                    }
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(DatabaseHelper.TABLE_USER)
                            .child(passengerId);
                    //.child(passengerExcludeSelf[i]);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: get user profile name");
                            User user = dataSnapshot.getValue(User.class);
                            final String passengerProfileName = user.getUser_profileName();
                            final String profilePicSrc = user.getUser_profilePicSrc();
                            //hashmapUserIdProfileImg.put(passengerId, preloadCustomMarkerImg(getContext(), profilePicSrc));
                            //final Bitmap profileImg = loadBitmap(profilePicSrc);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                    .child("Location Tracking")
                                    .child(activeRidePrId)
                                    .child(passengerId);
                            //.child(passengerExcludeSelf[i]);
                            Query query = ref.orderByKey().limitToLast(1);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "onDataChange: get user latest location (user id: " + passengerId + ")");
                                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                        LocationLatLng location = ds.getValue(LocationLatLng.class);
                                        location.setLocation_id(ds.getKey());
                                        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                        Marker marker = hashMapMarker.get(passengerId);
                                        if (marker != null) {
                                            marker.setPosition(latLng);

                                        }
                                        else {
                                            final View markerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom_layout, null);
                                            if(profilePicSrc != null) {
                                                Log.d(TAG, "createCustomMarker: set custom marker with user profile image");
                                                Glide.with(getContext())
                                                        .asBitmap()
                                                        .load(profilePicSrc)
                                                        .apply(new RequestOptions().optionalCenterCrop())
                                                        .apply(RequestOptions.circleCropTransform())
                                                        .into(new SimpleTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                final float scale = getContext().getResources().getDisplayMetrics().density;
                                                                int pixels = (int) (40 * scale + 0.5f);
                                                                Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);
                                                                MarkerOptions markerOptions = new MarkerOptions()
                                                                        .position(latLng)
                                                                        .title(passengerProfileName)
                                                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                                                Marker newMarker = mMap.addMarker(markerOptions);
                                                                hashMapMarker.put(passengerId, newMarker);
                                                            }
                                                        });


                                                Log.d(TAG, "preloadCustomMarkerImg: " + profilePicSrc);
                                            }
                                            else {
                                                Log.d(TAG, "createCustomMarker: set custom marker with default profile image");
                                            }
//                                            -----
//                                            MarkerOptions markerOptions = new MarkerOptions()
//                                                    .position(latLng)
//                                                    .title(passengerProfileName)
//                                                    .icon(BitmapDescriptorFactory.fromBitmap(preloadCustomMarkerImg(getContext(), profilePicSrc)));
//                                            Marker newMarker = mMap.addMarker(markerOptions);
//                                            hashMapMarker.put(passengerId, newMarker);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(TAG, "onCancelled: failed to get user latest location -> " + databaseError);
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: failed to get user profile name -> " + databaseError);
                        }
                    });
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Failed to get passenger location tracking list -> " + databaseError);
            }
        });

        //end of new features
    }

    private Bitmap preloadCustomMarkerImg(final Context context, String profileImg) {
        final View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom_layout, null);
        if(profileImg != null) {
            Log.d(TAG, "createCustomMarker: set custom marker with user profile image");
            final ImageView markerImage = (ImageView) markerView.findViewById(R.id.user_dp);
            Glide.with(context)
                    .load(profileImg)
                    .apply(new RequestOptions().optionalCenterCrop())
                    .apply(RequestOptions.circleCropTransform())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    })
                    .into(markerImage);


            Log.d(TAG, "preloadCustomMarkerImg: " + profileImg);
        }
        else {
            Log.d(TAG, "createCustomMarker: set custom marker with default profile image");
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        markerView.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);
        return bitmap;
    }
}

