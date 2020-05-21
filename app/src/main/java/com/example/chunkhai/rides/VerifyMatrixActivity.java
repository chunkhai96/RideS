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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Object.Vehicle;
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

public class VerifyMatrixActivity extends AppCompatActivity {
    private static final String TAG = "VerifyMatrixActivity";
    private static final String TITLE = "Identity Verification";

    //widgets
    private TextView tvToolbarTitle;
    private RelativeLayout btnBack;
    private RelativeLayout btnSave;
    private RelativeLayout relLayout;
    private EditText etFullName, etMatrixNo;
    private TextView tvMatrixVerifMsg, warningMsgFullName, warningMsgMatrixNo, warningMsgPic;
    private ImageView ivMatrixImg;
    private ProgressBar progressBar;

    //vars
    private Context mContext;
    private String mFullName, mMatrixNo;
    private Uri mSelectedUri = null;
    private byte[] mUploadBytes;
    private double mProgress = 0;
    private boolean matrixUsed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_matrix);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnSave = (RelativeLayout) findViewById(R.id.btnSave);
        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        ivMatrixImg = (ImageView) findViewById(R.id.ivMatrixImg);
        etFullName = (EditText) findViewById(R.id.etFullName);
        etMatrixNo = (EditText) findViewById(R.id.etMatrixNo);
        tvMatrixVerifMsg = (TextView) findViewById(R.id.tvMatrixVerifMsg);
        warningMsgFullName = (TextView) findViewById(R.id.warningMsgFullName);
        warningMsgMatrixNo = (TextView) findViewById(R.id.warningMsgMatrixNo);
        warningMsgPic = (TextView) findViewById(R.id.warningMsgPic);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        init();
        initCroperino();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), VerifyMatrixActivity.this, true, 8, 5, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, VerifyMatrixActivity.this);
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), VerifyMatrixActivity.this, true, 8, 5, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    mSelectedUri = Uri.fromFile(CroperinoFileUtil.getTempFile());
                    ivMatrixImg.setImageURI(mSelectedUri);
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

        etFullName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etMatrixNo.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(8)});

        checkMatrixVerification();

    }

    public void initCroperino(){
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/RideS/Pictures", "/sdcard/RideS/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(VerifyMatrixActivity.this);
        CroperinoFileUtil.setupDirectory(VerifyMatrixActivity.this);
    }

    private void checkMatrixVerification(){
        Log.d(TAG, "checkMatrixVerification: getting mUser verification information");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getUser_verifMatrixStatus() != 0){
                    if(user.getUser_verifMatrixStatus() == 1){
                        tvMatrixVerifMsg.setVisibility(View.VISIBLE);
                        tvMatrixVerifMsg.setText("Your verification is pending. Please be patient.");
                        Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_pending );
                        etFullName.setEnabled(false);
                        etFullName.setText(user.getUser_fullName());
                        etFullName.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
                        etMatrixNo.setEnabled(false);
                        etMatrixNo.setText(user.getUser_matrixNo());
                        etMatrixNo.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
                        Glide.with(getApplicationContext())
                                .load(user.getUser_verifMatrixSrc())
                                .apply(new RequestOptions().optionalCenterCrop())
                                .into(ivMatrixImg);
                    }
                    else if(user.getUser_verifMatrixStatus() == 2){
                        tvMatrixVerifMsg.setVisibility(View.VISIBLE);
                        tvMatrixVerifMsg.setText("Congratulation! Your identity is verified.");
                        Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.ic_green_check );
                        etFullName.setEnabled(false);
                        etFullName.setText(user.getUser_fullName());
                        etFullName.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
                        etMatrixNo.setEnabled(false);
                        etMatrixNo.setText(user.getUser_matrixNo());
                        etMatrixNo.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
                        Glide.with(getApplicationContext())
                                .load(user.getUser_verifMatrixSrc())
                                .apply(new RequestOptions().optionalCenterCrop())
                                .into(ivMatrixImg);
                    }
                    else if(user.getUser_verifMatrixStatus() == 3){
                        tvMatrixVerifMsg.setVisibility(View.VISIBLE);
                        tvMatrixVerifMsg.setText("Your identity verification request is rejected. Please ensure your full name and matrix card number is correct and provide a clear photo of your matrix card.");
                        btnSave.setVisibility(View.VISIBLE);
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "onClick: save clicked");
                                if(filledCorrect()){
                                    checkMatrixRegistered(mMatrixNo);
                                    showProgressBar();
                                }
                            }
                        });
                        ivMatrixImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSelectPhotoDialog();
                            }
                        });
                    }
                }
                else {
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: save clicked");
                            if(filledCorrect()){
                                checkMatrixRegistered(mMatrixNo);
                                showProgressBar();
                            }
                        }
                    });
                    ivMatrixImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showSelectPhotoDialog();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving mUser information -> " + databaseError);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean filledCorrect(){
        Log.d(TAG, "filledCorrect: checking mUser entered data");
        warningMsgFullName.setVisibility(View.GONE);
        warningMsgMatrixNo.setVisibility(View.GONE);
        warningMsgPic.setVisibility(View.GONE);

        if(etFullName.getText().toString().matches("")){
            warningMsgFullName.setVisibility(View.VISIBLE);
            return false;
        }
        else if(etMatrixNo.getText().toString().matches("")){
            warningMsgMatrixNo.setVisibility(View.VISIBLE);
            return false;
        }
        else if(mSelectedUri == null){
            warningMsgPic.setVisibility(View.VISIBLE);
            return false;
        }

        mFullName = etFullName.getText().toString().toUpperCase();
        mMatrixNo = etMatrixNo.getText().toString().toUpperCase();
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
                        Croperino.prepareGallery(VerifyMatrixActivity.this);
                    }
                });

                dialogOpenCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Croperino.prepareCamera(VerifyMatrixActivity.this);
                    }
                });
            }
        });

        dialog.show();
    }

    private void checkMatrixRegistered(String matrixNo){
        Log.d(TAG, "checkMatrixRegistered: checking is the matrix number register by other user");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER);
        Query query = ref.orderByChild(DatabaseHelper.TABLE_USER_ATTR_MATRIX_NO).equalTo(matrixNo);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        if(user.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_new)
                                || user.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)){
                            matrixUsed = true;
                            break;
                        }
                    }
                }
                if(!matrixUsed){
                    uploadNewPhoto(Uri.fromFile(CroperinoFileUtil.getTempFile()));
                }
                else {
                    Toast.makeText(VerifyMatrixActivity.this, "This matrix number is registered", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error on checking registered matrix -> " + databaseError);
                Toast.makeText(VerifyMatrixActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started.");
            if(mBitmap == null){
                try{
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(VerifyMatrixActivity.this,uris[0]);
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
        Toast.makeText(VerifyMatrixActivity.this, "Uploading image", Toast.LENGTH_SHORT).show();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(DatabaseHelper.FOLDER_VERIFICATION + "/" + DatabaseHelper.FOLDER_VERIFICATION_MATRIX + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        UploadTask uploadTask = storageReference.putBytes(mUploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(VerifyMatrixActivity.this, "Upload completed", Toast.LENGTH_SHORT).show();

                //insert the download uri into the firebase database
                Task<Uri> urlTask = storageReference.getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri firebaseUri = urlTask.getResult();
                String imageSrc = firebaseUri.toString();
                Log.d(TAG, "onSuccesss: firebase download url: " + imageSrc);
                addMatrixVerification(mFullName, mMatrixNo, imageSrc);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifyMatrixActivity.this, "could not upload photo", Toast.LENGTH_SHORT).show();
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

    private void addMatrixVerification(String fullName, String matrixNo, String matrixImgSrc){
        Map<String, Object> verifInfo = new HashMap<>();
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_FULLNAME, fullName);
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_MATRIX_NO, matrixNo);
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_VERIF_MATRIX_SRC, matrixImgSrc);
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_VERIF_MATRIX_STATUS, getResources().getInteger(R.integer.user_verification_status_new));
        verifInfo.put(DatabaseHelper.TABLE_USER_ATTR_VERIF_MATRIX_TIMESTAMP, System.currentTimeMillis());
        FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(verifInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: successfully inserted mUser matrix verification");
                        Toast.makeText(VerifyMatrixActivity.this, "Verification sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onSuccess: failed to insert mUser matrix verification -> " + e);
                        Toast.makeText(VerifyMatrixActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
        relLayout.setForeground(getResources().getDrawable(R.color.colorTransparent));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
