package com.example.chunkhai.rides;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RotateBitmap;
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
import java.util.HashMap;
import java.util.Map;

import static com.example.chunkhai.rides.PersonalInfoActivity.getBytesFromBitmap;

public class VerifyLicenseActivity extends AppCompatActivity {

    private static final String TAG = "VerifyLicenseActivity";
    private static final String TITLE = "License Verification";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnSave;
    EditText etLicenseNo;
    ImageView ivLicenseImg;
    TextView tvLicenseVerifMsg, warningMsgLicenseNo, warningMsgLicenseImg;
    RelativeLayout relLayout;
    ProgressBar progressBar;

    //vars
    Context mContext;
    User mUser;
    String mLicenseNo;
    private byte[] mUploadBytes;
    private double mProgress = 0;
    private boolean licenseUsed = false;
    private Uri mSelectedUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_license);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnSave = (RelativeLayout) findViewById(R.id.btnSave);
        etLicenseNo = (EditText) findViewById(R.id.etLicenseNo);
        ivLicenseImg = (ImageView) findViewById(R.id.ivLicenseImg);
        tvLicenseVerifMsg = (TextView) findViewById(R.id.tvLicenseVerifMsg);
        warningMsgLicenseNo = (TextView) findViewById(R.id.warningMsgLicenseNo);
        warningMsgLicenseImg = (TextView) findViewById(R.id.warningMsgPic);
        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mUser = new User();

        init();
        initCroperino();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), VerifyLicenseActivity.this, true, 8, 5, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, VerifyLicenseActivity.this);
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), VerifyLicenseActivity.this, true, 8, 5, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedUri = Uri.fromFile(CroperinoFileUtil.getTempFile());
                    ivLicenseImg.setImageURI(mSelectedUri);
                }
                break;
            default:
                break;
        }
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

        etLicenseNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                if(mUser.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)) {
                    checkLicenseVerification();
                }
                else {
                    new AlertDialog.Builder(VerifyLicenseActivity.this)
                            .setTitle("Verify your identity")
                            .setMessage("Please complete your identity verification to proceed.")
                            .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(VerifyLicenseActivity.this, SettingActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving mUser information -> " + databaseError);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initCroperino(){
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/RideS/Pictures", "/sdcard/RideS/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(VerifyLicenseActivity.this);
        CroperinoFileUtil.setupDirectory(VerifyLicenseActivity.this);
    }

    private void checkLicenseVerification(){
        Log.d(TAG, "checkMatrixVerification: checking mUser verification information");
        if(mUser.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_new)){
            tvLicenseVerifMsg.setVisibility(View.VISIBLE);
            tvLicenseVerifMsg.setText("Your verification is pending. Please be patient.");
            Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_pending );
            etLicenseNo.setEnabled(false);
            etLicenseNo.setText(mUser.getUser_licenseNo());
            etLicenseNo.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
            Glide.with(getApplicationContext())
                    .load(mUser.getUser_verifMatrixSrc())
                    .apply(new RequestOptions().optionalCenterCrop())
                    .into(ivLicenseImg);
        }
        else if(mUser.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_verified)){
            tvLicenseVerifMsg.setVisibility(View.VISIBLE);
            tvLicenseVerifMsg.setText("Congratulation! Your license is verified.");
            Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_green_check );
            etLicenseNo.setEnabled(false);
            etLicenseNo.setText(mUser.getUser_licenseNo());
            etLicenseNo.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
            Glide.with(getApplicationContext())
                    .load(mUser.getUser_verifLicenseSrc())
                    .apply(new RequestOptions().optionalCenterCrop())
                    .into(ivLicenseImg);
        }
        else if(mUser.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_unverified)){
            tvLicenseVerifMsg.setVisibility(View.VISIBLE);
            tvLicenseVerifMsg.setText("Your identity verification request is rejected. Please ensure your driving license is correct and provide a clear photo of your driving license.");
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: save clicked");
                    if(filledCorrect()){
                        checkLicenseRegistered(mLicenseNo);
                        showProgressBar();
                    }
                }
            });
            ivLicenseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectPhotoDialog();
                }
            });
        }
        else {
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: save clicked");
                    if(filledCorrect()){
                        checkLicenseRegistered(mLicenseNo);
                        showProgressBar();
                    }
                }
            });
            ivLicenseImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectPhotoDialog();
                }
            });
        }
    }

    private boolean filledCorrect(){
        Log.d(TAG, "filledCorrect: checking mUser entered data");
        warningMsgLicenseNo.setVisibility(View.GONE);
        warningMsgLicenseImg.setVisibility(View.GONE);

        if(etLicenseNo.getText().toString().matches("")){
            warningMsgLicenseNo.setVisibility(View.VISIBLE);
            warningMsgLicenseNo.setText("Cannot be empty");
            return false;
        }
        else if(etLicenseNo.getText().toString().length() < 12) {
            warningMsgLicenseNo.setVisibility(View.VISIBLE);
            warningMsgLicenseNo.setText("Invalid format");
            return false;
        }
        else if(mSelectedUri == null){
            warningMsgLicenseImg.setVisibility(View.VISIBLE);
            return false;
        }

        mLicenseNo = etLicenseNo.getText().toString();
        return true;
    }

    private void showSelectPhotoDialog(){
        Log.d(TAG, "showSelectPhotoDialog: showing modal");

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
                        Croperino.prepareGallery(VerifyLicenseActivity.this);
                    }
                });

                dialogOpenCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Croperino.prepareCamera(VerifyLicenseActivity.this);
                    }
                });
            }
        });

        dialog.show();
    }

    private void checkLicenseRegistered(String licenseNo){
        Log.d(TAG, "checkLicenseRegistered: checking is the license number register by other user");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER);
        Query query = ref.orderByChild(DatabaseHelper.TABLE_USER_ATTR_LICENSE_NO).equalTo(licenseNo);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        if(user.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_new)
                                || user.getUser_verifLicenseStatus() == getResources().getInteger(R.integer.user_verification_status_verified)){
                            licenseUsed = true;
                            break;
                        }
                    }
                }
                if(!licenseUsed){
                    uploadNewPhoto(mSelectedUri);
                }
                else {
                    Toast.makeText(VerifyLicenseActivity.this, "This license number is registered", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on checking registered 'license -> " + databaseError);
                Toast.makeText(VerifyLicenseActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadingNewPhoto: uploading a new image uri to storage.");
        VerifyLicenseActivity.BackgroundImageResize resize = new VerifyLicenseActivity.BackgroundImageResize(null);
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

        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started.");
            if(mBitmap == null){
                try{
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(VerifyLicenseActivity.this,uris[0]);
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
        Toast.makeText(VerifyLicenseActivity.this, "Uploading image", Toast.LENGTH_SHORT).show();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(DatabaseHelper.FOLDER_VERIFICATION + "/" + DatabaseHelper.FOLDER_VERIFICATION_LICENSE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        UploadTask uploadTask = storageReference.putBytes(mUploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(VerifyLicenseActivity.this, "Upload completed", Toast.LENGTH_SHORT).show();

                //insert the download uri into the firebase database
                Task<Uri> urlTask = storageReference.getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri firebaseUri = urlTask.getResult();
                String imageSrc = firebaseUri.toString();
                Log.d(TAG, "onSuccesss: firebase download url: " + imageSrc);
                addLicenseVerification(mLicenseNo, imageSrc);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifyLicenseActivity.this, "could not upload photo", Toast.LENGTH_SHORT).show();
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

    private void addLicenseVerification(String licenseNo, String licenseImgSrc){

        Map<String, Object> verifInfo = new HashMap<>();
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_LICENSE_NO, licenseNo);
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_VERIF_LICENSE_SRC, licenseImgSrc);
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_VERIF_LICENSE_STATUS,
                getResources().getInteger(R.integer.user_verification_status_new));
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_VERIF_MATRIX_TIMESTAMP, System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(verifInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: successfully inserted mUser license verification");
                        Toast.makeText(VerifyLicenseActivity.this, "Verification sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: failed to insert mUser license verification -> " + e);
                        Toast.makeText(VerifyLicenseActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                });
    }

    private void showProgressBar(){
        relLayout.setForeground(getResources().getDrawable(R.drawable.modal_screen));
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideProgressBar(){
        relLayout.setForeground(getResources().getDrawable(R.drawable.modal_screen));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
