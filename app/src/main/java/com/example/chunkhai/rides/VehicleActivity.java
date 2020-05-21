package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.ProvidedRide;
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.ListAdapter.VehicleListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class VehicleActivity extends AppCompatActivity{

    private static final String TAG = "VehicleActivity";
    private static final String TITLE = "Vehicle";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnAdd;
    ImageView ivVehicleImage;
    TextView tvNoVehicle;
    ListView lvVehicle;
    ProgressBar progressBar;

    //vars
    Context mContext;
    int listContainerLayoutResourceId;
    int ivListId;
    int tvListTitleId;
    FirebaseAuth mAuth;
    ArrayList <Vehicle> mVehicles;
    boolean vehicleInActiveRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnAdd = (RelativeLayout) findViewById(R.id.btnAdd);
        tvNoVehicle = (TextView) findViewById(R.id.tvNoVehicle);
        lvVehicle = (ListView) findViewById(R.id.lvVehicle);
        listContainerLayoutResourceId = R.layout.list_row_vehicle;
        ivListId = R.id.ivListImage;
        tvListTitleId = R.id.tvListTitle;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        getUserVehicle();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.lvVehicle) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mVehicles.get(info.position).getVehicle_plateNo());
            String[] menuItems = getResources().getStringArray(R.array.vehicleContextMenuItems);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String selectedPlateNo = mVehicles.get(info.position).getVehicle_plateNo();

        if(menuItemIndex == 0){
            Intent intent = new Intent(VehicleActivity.this, EditVehicleActivity.class);
            intent.putExtra(DatabaseHelper.TABLE_VEHICLE_ATTR_PLATE_NO, selectedPlateNo);
            startActivity(intent);
        }
        else if(menuItemIndex == 1){
            deleteConfirm(selectedPlateNo);
        }

        return true;
    }

    public void init(){
        Log.d(TAG, "init: initializing toolbar and other widgets");

        //Init Toolbar

        if(!mVehicles.isEmpty()) {
            int totalVehicle = mVehicles.size();
            tvToolbarTitle.setText(TITLE + " (" + totalVehicle + ")");
            tvNoVehicle.setVisibility(View.GONE);
        }
        else{
            tvToolbarTitle.setText(TITLE);
            tvNoVehicle.setVisibility(View.VISIBLE);
        }

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
                if(mVehicles.size() < 3) {
                    Intent intent = new Intent(mContext, AddVehicleActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(VehicleActivity.this, "Maximum 3 vehicles for each mUser", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initVehicleList(ListView list, ArrayList<Vehicle> vehicles){

        Log.d(TAG, "initEmergencyContactList: initializing " + vehicles.size());

            VehicleListAdapter listAdapter = new VehicleListAdapter(mContext,
                    listContainerLayoutResourceId, ivListId, tvListTitleId, vehicles);

            list.setAdapter(listAdapter);

            registerForContextMenu(list);
    }

    private void getUserVehicle() {
        Log.d(TAG, "getUserVehicle: getting mUser vehicle list");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_VEHICLE)
                .orderByChild(DatabaseHelper.TABLE_VEHICLE_ATTR_UID)
                .equalTo(mAuth.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mVehicles = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Vehicle vehicle = ds.getValue(Vehicle.class);
                    vehicle.setVehicle_plateNo(ds.getKey());
                    mVehicles.add(vehicle);
                }
                init();
                initVehicleList(lvVehicle, mVehicles);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving mUser vehicle");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteVehicle(final String vehiclePlateNo){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child(DatabaseHelper.TABLE_PROVIDEDRIDE)
                .orderByChild(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_VEHICLE_ID)
                .equalTo(vehiclePlateNo);

        vehicleInActiveRide = false;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ProvidedRide pr = ds.getValue(ProvidedRide.class);
                    pr.setPr_id(ds.getKey());
                    if(pr.getPr_status() == getResources().getInteger(R.integer.providedride_status_open)){
                        vehicleInActiveRide = true;
                        break;
                    }
                }

                if(!vehicleInActiveRide){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                            .child(DatabaseHelper.TABLE_VEHICLE)
                            .child(vehiclePlateNo);

                    ref.removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete: Vehicle data deleted");
                                }
                            });

                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                            .child(DatabaseHelper.FOLDER_VEHICLE_PIC + "/" + vehiclePlateNo);
                    storageReference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Vehicle image deleted");
                                    Toast.makeText(VehicleActivity.this, "Successfully deleted"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "onFailure: Error on deleting vehicle image");
                            Toast.makeText(VehicleActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    new AlertDialog.Builder(VehicleActivity.this)
                            .setTitle("Couldn't delete")
                            .setMessage("This vehicle is used in a active ride.")
                            .setNegativeButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteConfirm(final String plateNo){
        new AlertDialog.Builder(this)
                .setTitle(plateNo)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteVehicle(plateNo);
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
