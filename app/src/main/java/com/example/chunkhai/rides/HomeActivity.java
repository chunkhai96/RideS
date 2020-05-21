package com.example.chunkhai.rides;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.Notification;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.SectionPagerAdapter;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class HomeActivity extends AppCompatActivity implements FragmentHome.showcaseCallbackListener {
    private static final String TAG = "HomeActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String TUTORIAL_PREFERENCE = "Tutorial";
    public static final String TUTORIAL_SHOWED = "showed";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    public void homeDone() {
        tvNotificationCount.setText("3");
        tvNotificationCount.setVisibility(View.VISIBLE);
        new MaterialShowcaseView.Builder(this)
                .setTarget(btnNotification)
                .setDismissText("GOT IT")
                .setTitleText("Notification")
                .setContentText("Red box beside the bell icon mean you have unread notification")
                .setDelay(300)
                .singleUse("HOME_ACTIVITY")
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView showcaseView) {
                        getUnarchivedNotif();
                        tabLayout.setEnabled(true);
                    }
                })
                .show();
    }

    //widgets
    RelativeLayout btnNotification;
    TextView tvNotificationCount;
    TabLayout tabLayout;

    //vars
    private long mUnarchivedNotifCount;
    private boolean mLocationPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTheme(R.style.AppTheme);
        Log.d(TAG,"onCreate:starting");

        btnNotification = (RelativeLayout) findViewById(R.id.btnNotification);
        tvNotificationCount = (TextView) findViewById(R.id.tvNotificationCount);

        if(!directToLoginPageIfNoLoggedIn()) {
            if(!showTutorial()) {
                setupViewPager();
                getLocationPermission();
            }
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: activity stopped");
        super.onStop();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof FragmentHome) {
            FragmentHome fragmentHome = (FragmentHome) fragment;
            fragmentHome.setshowcaseCallbackListener(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: check permission");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission not granted");
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    init();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: get permission");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this,
                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this,
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: success grant permission");
                mLocationPermissionsGranted = true;
                init();
            } else {
                Log.d(TAG, "getLocationPermission: fail grant permission (COURSE_LOCATION)");
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "getLocationPermission: fail grant permission (FINE_LOCATION)");
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void init(){
        Log.d(TAG, "init: initializing toolbar and other widgets");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        checkDeviceToken(token);
                    }
                });

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            }
        });

        getUnarchivedNotif();
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Message
     */
    private void setupViewPager(){
        Log.d(TAG, "setupViewPager:setup view page with tab fragment");
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentSchedule());
        adapter.addFragment(new FragmentHome());
        adapter.addFragment((new FragmentProfile()));
        ViewPager viewPager= (ViewPager) findViewById(R.id.vpContentContainer);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_schedule);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_ride);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_person);

        final Context context = this.getApplicationContext();
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(context, R.color.colorSecondaryDark);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(context, R.color.colorDefaultText);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                }
        );

        int[][] state = new int[][] {
                new int[] {-android.R.attr.state_selected},
                new int[] {android.R.attr.state_selected}
        };

        int[] color = new int[] {
                Color.parseColor("#808080"),
                getResources().getColor(R.color.colorSecondaryDark)
        };
        ColorStateList colorStateList = new ColorStateList(state,color);

        if ("OPEN_TAB_SCHEDULE".equals(getIntent().getAction())) {
            tabLayout.getTabAt(0).select();
        }
        else {
            tabLayout.getTabAt(1).select();
        }
    }

    private boolean directToLoginPageIfNoLoggedIn(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d(TAG, "directToLoginPageIfNoLoggedIn: No logged in, redirecting to login page");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean showTutorial(){
        SharedPreferences sharedPreferences = getSharedPreferences(TUTORIAL_PREFERENCE,
                Context.MODE_PRIVATE);
        if (sharedPreferences.getInt(TUTORIAL_SHOWED, -1) == -1 || sharedPreferences.getInt(TUTORIAL_SHOWED, -1) == 0) {
            Intent intent = new Intent(this, Tutorial.class);
            startActivity(intent);
            finish();
            return true;
        }
        else {
            return false;
        }
    }

    private void checkDeviceToken(final String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getUid())
                .child(DatabaseHelper.TABLE_USER_ATTR_DEVICE_TOKEN);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String recordedToken = dataSnapshot.getValue(String.class);
                if(recordedToken != null) {
                    if (!recordedToken.matches(token)) {
                        updateToken(token);
                    }
                }
                else {
                    updateToken(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getUid())
                .child(DatabaseHelper.TABLE_USER_ATTR_DEVICE_TOKEN);
        ref.setValue(token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: device token updated");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled: Error");
                    }
                });
    }

    private void getUnarchivedNotif(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(DatabaseHelper.TABLE_NOTIFICATION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: notification count " + dataSnapshot.getChildrenCount());
                mUnarchivedNotifCount = 0 ;
                String recipientUid = dataSnapshot.getKey();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Notification notif = ds.getValue(Notification.class);
                    notif.setNotif_reciever_uid(recipientUid);
                    notif.setNotif_id(ds.getKey());
                    if(notif.getNotif_status() == getResources().getInteger(R.integer.notification_status_new)){
                        mUnarchivedNotifCount++;
                    }
                }
                if(mUnarchivedNotifCount > 0){
                    tvNotificationCount.setVisibility(View.VISIBLE);
                    String notifCount;
                    if(mUnarchivedNotifCount <= 99) {
                        notifCount = Long.toString(mUnarchivedNotifCount);
                    }
                    else {
                        notifCount = "99";
                    }
                    tvNotificationCount.setText(notifCount);
                }
                else {
                    tvNotificationCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving notification");
            }
        });

    }
}
