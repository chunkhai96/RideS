package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Calendar;
import java.util.Locale;

public class AddProvideRideActivity extends AppCompatActivity {
    private static final String TAG = "AddProvideRide";
    private final String TITLE = "Provide a Ride";
    private static final int FROM_PLACE_PICKER_REQUEST = 1;
    private static final int TO_PLACE_PICKER_REQUEST = 2;

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnPublish;
    EditText etFrom, etTo, etDepartureDateTime, etShareFare;

    Spinner spinnerVehicle;
    TextView warningMsgFrom, warningMsgTo, warningMsgDepartureDateTime, warningMsgVehicle, warningMsgShareFare;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    LinearLayout alertNoVehicle;
    TextView tvRegisterVehicle;

    //vars
    User mCurrentUser;
    ArrayList<String> vehicles;
    ArrayList<String> vehicleIds;
    GoogleApiClient mGoogleApiClient;
    LatLng fromLatLng = null;
    String fromPlace;
    String fromAddress;
    LatLng toLatLng = null;
    String toPlace;
    String toAddress;
    long departTimestamp;
    String selectedVehicleId;
    double shareFare;
    Calendar mCalendar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_provide_ride);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnPublish = (RelativeLayout) findViewById(R.id.btnPublish);
        etFrom = (EditText) findViewById(R.id.etFrom);
        etTo = (EditText) findViewById(R.id.etTo);
        etDepartureDateTime = (EditText) findViewById(R.id.etDepartureDateTime);
        etShareFare = (EditText) findViewById(R.id.etShareFare);
        spinnerVehicle = findViewById(R.id.spinnerVehicle);

        warningMsgFrom = (TextView) findViewById(R.id.warningMsgFrom);
        warningMsgTo = (TextView) findViewById(R.id.warningMsgTo);
        warningMsgDepartureDateTime = (TextView) findViewById(R.id.warningMsgDepartureDateTime);
        warningMsgVehicle = (TextView) findViewById(R.id.warningMsgVehicle);
        warningMsgShareFare = (TextView) findViewById(R.id.warningMsgShareFare);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);

        alertNoVehicle = (LinearLayout) findViewById(R.id.alertNoVehicle);
        tvRegisterVehicle = (TextView) findViewById(R.id.tvRegisterVehicle);

        mCurrentUser = new User();
        mCalendar = Calendar.getInstance();
        mAuth = FirebaseAuth.getInstance();
        getCurrentUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == FROM_PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Log.d(TAG, "onActivityResult: From place selected: " + place.getLatLng().toString());
                fromLatLng = place.getLatLng();
                fromPlace = place.getName().toString();
                fromAddress = place.getAddress().toString();
                String address = place.getName() + ", " + place.getAddress();
                etFrom.setText(address);
            }
        }
        else if (requestCode == TO_PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Log.d(TAG, "onActivityResult: To place selected: " + place.getLatLng().toString());
                toLatLng = place.getLatLng();
                toPlace = place.getName().toString();
                toAddress = place.getAddress().toString();
                String address = place.getName() + ", " + place.getAddress();
                etTo.setText(address);
            }
        }
    }

    private void getCurrentUserInfo(){
        Log.d(TAG, "getCurrentUserInfo: getting user info from database");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                mCurrentUser.setUser_uid(dataSnapshot.getKey());
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on getting user info -> " + databaseError);
                Toast.makeText(AddProvideRideActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
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

        btnPublish.setVisibility(View.VISIBLE);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: save clicked");
                if(mCurrentUser.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_verified)) {
                    if(fillCorrect()){
                        departTimestamp = mCalendar.getTimeInMillis();
                        selectedVehicleId = vehicleIds.get(spinnerVehicle.getSelectedItemPosition());
                        double fare = Double.parseDouble(etShareFare.getText().toString());
                        double rounded = Math.round(fare*10.00)/10.00;
                        shareFare = rounded;
                        addNewRide(fromLatLng, toLatLng, departTimestamp, selectedVehicleId, shareFare);
                    }
                }
                else {
                    new AlertDialog.Builder(AddProvideRideActivity.this)
                            .setTitle("Verify your license")
                            .setMessage("You must verify your license for providing ride")
                            .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(AddProvideRideActivity.this, SettingActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("Not now", null)
                            .show();
                }
            }
        });

        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker(FROM_PLACE_PICKER_REQUEST);
            }
        });

        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker(TO_PLACE_PICKER_REQUEST);
            }
        });

        etDepartureDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: departure date clicked");
                showDataTimePicker();

            }
        });

        getUserVehicleList();

    }

    private void showPlacePicker(int requestCode){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(AddProvideRideActivity.this), requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
        }
    }

    private void showDataTimePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddProvideRideActivity.this
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                view.setMinDate(System.currentTimeMillis() - 1000);
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(AddProvideRideActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        mCalendar.set(Calendar.MINUTE, minute);
                        updateDateTimeLabel();
                    }
                }
                , mCalendar.get(Calendar.HOUR_OF_DAY)
                , mCalendar.get(Calendar.MINUTE)
                , false)
                .show();
            }

        }
        , mCalendar.get(Calendar.YEAR)
        , mCalendar.get(Calendar.MONTH)
        , mCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateTimeLabel() {
        String format = "dd MMM yyyy (E) - hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        etDepartureDateTime.setText(sdf.format(mCalendar.getTime()));
    }

    private void getUserVehicleList() {
        Log.d(TAG, "getUserVehicleList: getting mUser vehicle list");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_VEHICLE)
                .orderByChild(DatabaseHelper.TABLE_VEHICLE_ATTR_UID)
                .equalTo(mAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicles = new ArrayList<>();
                vehicleIds = new ArrayList<>();
                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Vehicle vehicle = ds.getValue(Vehicle.class);
                    vehicle.setVehicle_plateNo(ds.getKey());
                    String listItem = vehicle.getVehicle_plateNo() + " - " + vehicle.getVehicle_model();
                    vehicles.add(listItem);
                    vehicleIds.add(ds.getKey());
                }
                initVehicleDropdown();
                checkNoVehicle();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on loading mUser vehicle list");
            }
        });
    }

    private void initVehicleDropdown(){
        if(vehicles.size() != 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehicles);
            spinnerVehicle.setAdapter(adapter);
        }
        else{
            String[] item = {"No available vehicle"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, item);
            spinnerVehicle.setAdapter(adapter);
            spinnerVehicle.setEnabled(false);
        }
    }

    private void checkNoVehicle(){
        if(vehicles.size() == 0) {
            alertNoVehicle.setVisibility(View.VISIBLE);

            String string = new String("Register now");
            SpannableString content = new SpannableString(string);
            content.setSpan(new UnderlineSpan(), 0, string.length(), 0);
            tvRegisterVehicle.setText(content);

            tvRegisterVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(AddProvideRideActivity.this, VehicleActivity.class));
                    startActivity(new Intent(AddProvideRideActivity.this, AddVehicleActivity.class));
                    finish();
                }
            });
        }
    }

    private boolean fillCorrect(){

        String dateTime = etDepartureDateTime.getText().toString();
        String shareFare = etShareFare.getText().toString();

        warningMsgFrom.setVisibility(View.GONE);
        warningMsgTo.setVisibility(View.GONE);
        warningMsgDepartureDateTime.setVisibility(View.GONE);
        warningMsgVehicle.setVisibility(View.GONE);
        warningMsgShareFare.setVisibility(View.GONE);
        warningMsgDepartureDateTime.setVisibility(View.GONE);

        if(fromLatLng == null){
            warningMsgFrom.setVisibility(View.VISIBLE);
            return false;
        }
        else if(toLatLng == null){
            warningMsgTo.setVisibility(View.VISIBLE);
            return false;
        }
        else if(dateTime.matches("")){
            warningMsgDepartureDateTime.setText("Cannot be empty");
            warningMsgDepartureDateTime.setVisibility(View.VISIBLE);
            return false;
        }
        else if(mCalendar.getTimeInMillis() < System.currentTimeMillis()){
            Log.d(TAG, "fillCorrect:  " + mCalendar.getTimeInMillis() + " < " + System.currentTimeMillis());
            warningMsgDepartureDateTime.setText("Datetime is invalid");
            warningMsgDepartureDateTime.setVisibility(View.VISIBLE);
            return false;
        }
        else if(shareFare.matches("")){
            warningMsgShareFare.setVisibility(View.VISIBLE);
            warningMsgShareFare.setText("Cannot be empty");
            return false;
        }
        else if(!(Float.parseFloat(shareFare) < 100)){
            warningMsgShareFare.setVisibility(View.VISIBLE);
            warningMsgShareFare.setText("Maximum is RM 99.99");
            return false;
        }
        else if(vehicles.size() == 0){
            warningMsgVehicle.setVisibility(View.VISIBLE);
            return false;
        }

        return true;

    }

    private void addNewRide(LatLng fromLatLng, LatLng toLatLng, long departTimestamp, String selectedVehicleId, double shareFare){
        showProgressBar();

        String userId = mAuth.getCurrentUser().getUid();
        long currentTimestamp = System.currentTimeMillis();

        ProvidedRide newRide = new ProvidedRide(userId, currentTimestamp
                , fromLatLng.latitude, fromLatLng.longitude, fromPlace, fromAddress
                , toLatLng.latitude, toLatLng.longitude, toPlace, toAddress
                , departTimestamp, selectedVehicleId
                , shareFare
                , getResources().getInteger(R.integer.providedride_status_open));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .push()
                .setValue(newRide)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddProvideRideActivity.this, "Ride published"
                                , Toast.LENGTH_SHORT).show();

                        hideProgressBar();

                        Intent i = new Intent(AddProvideRideActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setAction("OPEN_TAB_SCHEDULE");
                        startActivity(i);

                        finish();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toast.makeText(AddProvideRideActivity.this, "Error on adding new vehicle"
                                , Toast.LENGTH_SHORT).show();

                        hideProgressBar();
                    }
                });
    }

    private void showProgressBar(){
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorGreyWithTransparency));
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideProgressBar(){
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
