package com.example.chunkhai.rides;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.Vehicle;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RotateBitmap;
import com.google.android.gms.tasks.OnCanceledListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.chunkhai.rides.PersonalInfoActivity.getBytesFromBitmap;

public class EditVehicleActivity extends AppCompatActivity implements SelectVehiclePhotoDialog.OnPhotoSelectedListener{

    private static final String TAG = "EditVehicleActivity";
    private final String TITLE = "Edit";

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to imageview");
        Glide.with(EditVehicleActivity.this)
                .load(imagePath.toString())
                .apply(new RequestOptions().optionalCenterCrop())
                .into(ivVehicleImage);
        //assign to global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        Glide.with(this)
                .load(bitmap)
                .apply(new RequestOptions().optionalCenterCrop())
                .into(ivVehicleImage);
        //assign to global variable
        mSelectedBitmap = bitmap;
        mSelectedUri = null;
    }

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnSave;
    ImageView ivVehicleImage;
    EditText etModel, etColor, etPlateNo, etCapacity;
    TextView tvWarningMsgImage, tvWarningMsgPlateNo, tvWarningMsgModel, tvWarningMsgColor, tvWarningMsgCapacity;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;

    //vars
    Context mContext;
    FirebaseAuth mAuth;
    String plateNo, model, color, imageSrc;
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri = null;
    private byte[] mUploadBytes;
    private double mProgress = 0;
    String cPlateNo;
    Vehicle mVehicle = new Vehicle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        cPlateNo = getIntent().getStringExtra(DatabaseHelper.TABLE_VEHICLE_ATTR_PLATE_NO);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnSave = (RelativeLayout) findViewById(R.id.btnSave);
        ivVehicleImage = (ImageView) findViewById(R.id.ivVehicleImage);
        etPlateNo = (EditText) findViewById(R.id.etPlateNo);
        etModel = (EditText) findViewById(R.id.etModel);
        etColor = (EditText) findViewById(R.id.etColor);
        etCapacity = (EditText)findViewById(R.id.etCapacity);
        tvWarningMsgImage = (TextView) findViewById(R.id.warningMsgImage);
        tvWarningMsgPlateNo = (TextView) findViewById(R.id.warningMsgPlateNo);
        tvWarningMsgModel = (TextView) findViewById(R.id.warningMsgModel);
        tvWarningMsgColor = (TextView) findViewById(R.id.warningMsgColor);
        tvWarningMsgCapacity = (TextView) findViewById(R.id.warningMsgCapacity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);

        mAuth= FirebaseAuth.getInstance();

        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), EditVehicleActivity.this, true, 16, 9, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, EditVehicleActivity.this);
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), EditVehicleActivity.this, true, 16, 9, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedUri = Uri.fromFile(CroperinoFileUtil.getTempFile());
                    ivVehicleImage.setImageURI(mSelectedUri);
                }
                break;
            default:
                break;
        }
    }

    private void init(){
        Log.d(TAG, "init: initializing toolbar and other widgets");

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setVisibility(View.VISIBLE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "setOnClickListener: save button clicked");

                if(fillCorrect()){
                    showProgressBar();

                    String model = etModel.getText().toString();
                    String color = etColor.getText().toString();
                    int capacity = Integer.parseInt(etCapacity.getText().toString());
                    Log.d(TAG, "onClick: capacity: " + capacity);

                    mVehicle.setVehicle_model(model);
                    mVehicle.setVehicle_color(color);
                    mVehicle.setVehicle_capacity(capacity);

                    if(mSelectedUri != null){
                        uploadNewPhoto(mSelectedUri);
                    }
                    else {
                        editVehicle(mVehicle);
                    }
                }
            }
        });

        ivVehicleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening dialog to choose new photo");
                showSelectPhotoDialog();
            }
        });

        etModel.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etColor.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        initCroperino();
        getOldData();
    }

    private void initCroperino(){
        //Initialize on every usage
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/RideS/Pictures", "/sdcard/RideS/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(EditVehicleActivity.this);
        CroperinoFileUtil.setupDirectory(EditVehicleActivity.this);
    }

    private void showSelectPhotoDialog(){
        Log.d(TAG, "showSelectPhotoDialog: showing modal");

        if (ContextCompat.checkSelfPermission(EditVehicleActivity.this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(EditVehicleActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_selectvehiclephoto)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                final TextView dialogChoosePhoto = (TextView) dialog.findViewById(R.id.dialogChoosePhoto);
                final TextView dialogOpenCamera = (TextView) dialog.findViewById(R.id.dialogOpenCamera);

                dialogChoosePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Croperino.prepareGallery(EditVehicleActivity.this);
                    }
                });

                dialogOpenCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        try {
                            Croperino.prepareCamera(EditVehicleActivity.this);
                        }
                        catch (Exception e){
                            Log.d(TAG, "onClick: failed to prepare camera -> " + e);
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void getOldData(){
        Log.d(TAG, "getOldData: getting old data for vehicle: " + cPlateNo);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_VEHICLE)
                .child(cPlateNo);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: " + dataSnapshot.getKey());
                mVehicle = dataSnapshot.getValue(Vehicle.class);
                mVehicle.setVehicle_plateNo(dataSnapshot.getKey());

                Glide.with(EditVehicleActivity.this)
                        .load(mVehicle.getVehicle_imageSrc())
                        .apply(new RequestOptions().optionalCenterCrop())
                        .into(ivVehicleImage);
                etPlateNo.setText(mVehicle.getVehicle_plateNo());
                etModel.setText(mVehicle.getVehicle_model());
                etColor.setText(mVehicle.getVehicle_color());
                etCapacity.setText(String.valueOf(mVehicle.getVehicle_capacity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadingNewPhoto: uploading a new image uri to storage.");
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imagePath);
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap){
            if(bitmap != null){
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: compressing image");
            //mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started.");
            if(mBitmap == null){
                try{
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(EditVehicleActivity.this,uris[0]);
                }catch(IOException e){
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            Log.d(TAG, "doInBackground: megabytes before compression: " + mBitmap.getByteCount() / 1000000);
            bytes = getBytesFromBitmap(mBitmap, 100);
            Log.d(TAG, "doInBackground: megabytes after compression: " + bytes.length / 1000000);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes;

            //excecute the upload task
            executeUploadTask();
        }
    }

    private void executeUploadTask(){
        Log.d(TAG, "executeUploadTask: uploading image");
        Toast.makeText(EditVehicleActivity.this, "Uploading image", Toast.LENGTH_SHORT).show();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(DatabaseHelper.FOLDER_VEHICLE_PIC + "/" + mVehicle.getVehicle_plateNo());

        UploadTask uploadTask = storageReference.putBytes(mUploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditVehicleActivity.this, "Upload completed", Toast.LENGTH_SHORT).show();

                //insert the download uri into the firebase database
                Task<Uri> urlTask = storageReference.getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri firebaseUri = urlTask.getResult();
                mVehicle.setVehicle_imageSrc(firebaseUri.toString());
                Log.d(TAG, "onSuccesss: firebase download url: " + mVehicle.getVehicle_imageSrc());
                editVehicle(mVehicle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditVehicleActivity.this, "could not upload photo", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if(currentProgress > (mProgress + 15)){
                    mProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: upload is " + mProgress + "% done");
                }
            }
        });
    }

    private boolean fillCorrect(){
        String plateNo = etPlateNo.getText().toString();
        String model = etModel.getText().toString();
        String color = etColor.getText().toString();
        String capacity = etCapacity.getText().toString();

        tvWarningMsgPlateNo.setVisibility(View.GONE);
        tvWarningMsgPlateNo.setVisibility(View.GONE);
        tvWarningMsgPlateNo.setVisibility(View.GONE);
        tvWarningMsgCapacity.setVisibility(View.GONE);

        if(plateNo.matches("")){
            tvWarningMsgPlateNo.setVisibility(View.VISIBLE);
            return false;
        }
        else if(model.matches("")){
            tvWarningMsgModel.setVisibility(View.VISIBLE);
            return false;
        }
        else if(color.matches("")){
            tvWarningMsgColor.setVisibility(View.VISIBLE);
            return false;
        }
        else if(capacity.matches("")){
            tvWarningMsgCapacity.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            return true;
        }
    }

    private void editVehicle(final Vehicle vehicle){

        Vehicle newVehicle = vehicle;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_VEHICLE)
                .child(vehicle.getVehicle_plateNo());

        ref.setValue(newVehicle)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditVehicleActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
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
