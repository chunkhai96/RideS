package com.example.chunkhai.rides;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.ListAdapter.PersonalInfoTopListAdapter;
import com.example.chunkhai.rides.Util.ListAdapter.TextWithValueListAdapter;
import com.example.chunkhai.rides.Util.RotateBitmap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PersonalInfoActivity extends AppCompatActivity implements SelectPhotoDialog.OnPhotoSelectedListener{

    private static final String TAG = "PersonalInfoActivity";
    private static final String TITLE_OWN_PROFILE = "Personal Info";
    private static final String TITLE_OTHER_PROFILE = "Profile";

    final String[] listTitleProfilePic = {"Profile Picture"};
    final String[] listTitleMiddle = {"Profile Name", "Gender", "Phone Number", "Signature"};
    final String[] listTitleBottom = {"Full Name", "Matrix Number", "License Number"};

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnReview;
    RelativeLayout btnEmergencyContact;
    ImageView profileImg;
    ListView lvPersonalInfoTop;
    ListView lvPersonalInfoMiddle;
    ListView lvPersonalInfoBottom;
    ProgressBar progressBar;
    ProgressBar imgProgressBar;
    private ProgressBar mProgressBar;

    //vars
    Context mContext;
    String otherUserId;
    int listContainerLayoutResourceId;
    int tvListTitleId;
    int tvListValueId;
    String[] listValueMiddle;
    String[] listValueBottom;
    User user = null;
    String returnVal; //only use for method retrieveUserSpecificNode()
    View view;
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes;
    private double mProgress = 0;

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to imageview");
        //assign to global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
        uploadNewPhoto(mSelectedUri);
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        //assign to global variable
        mSelectedBitmap = bitmap;
        mSelectedUri = null;
        uploadNewPhoto(mSelectedBitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        lvPersonalInfoTop = (ListView) findViewById(R.id.lvPersonalInfoTop);
        lvPersonalInfoMiddle = (ListView) findViewById(R.id.lvPersonalInfoMiddle);
        lvPersonalInfoBottom = (ListView) findViewById(R.id.lvPersonalInfoBottom);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgProgressBar = (ProgressBar) findViewById(R.id.imgProgressBar);
        listContainerLayoutResourceId = R.layout.list_row_text_with_value;
        tvListTitleId = R.id.tvListTitle;
        tvListValueId = R.id.tvListValue;

        init();
        initCroperino();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), PersonalInfoActivity.this, true, 10, 10, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, PersonalInfoActivity.this);
                    Croperino.runCropImage(CroperinoFileUtil.getTempFile(), PersonalInfoActivity.this, true, 10, 10, R.color.gray, R.color.gray_variant);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri i = Uri.fromFile(CroperinoFileUtil.getTempFile());
                    uploadNewPhoto(i);
                }
                break;
            default:
                break;
        }
    }

    private void init(){
        progressBar.setVisibility(View.VISIBLE);
        //Init Toolbar
        tvToolbarTitle.setText(TITLE_OWN_PROFILE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        retrieveUserInfo();
    }

    public void initCroperino(){
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/RideS/Pictures", "/sdcard/RideS/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(PersonalInfoActivity.this);
        CroperinoFileUtil.setupDirectory(PersonalInfoActivity.this);
    }

    public void initPersonalInfoTopList(ListView list, String[] listTitleProfilePic, String imageSrc){
        PersonalInfoTopListAdapter listAdapter = new PersonalInfoTopListAdapter(mContext, listTitleProfilePic, imageSrc);

        list.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: Profile Picture clicked");
                if (imgProgressBar.getVisibility() != View.VISIBLE) {
                    showSelectPhotoDialog();
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "Image is uploading...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initPersonalInfoMiddleList(ListView list, String[] listTitle, String[] listValue){
        TextWithValueListAdapter listAdapter = new TextWithValueListAdapter(mContext,
                listContainerLayoutResourceId, tvListTitleId, tvListValueId, listTitle, listValue);

        list.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position) {
                    case 0:
                        Log.d(TAG, "onItemClick: Profile Name clicked");
                        showProfileNameModal();
                        break;
                    case 1:
                        Log.d(TAG, "onItemClick: Gender clicked");
                        showGenderModal();
                        break;
                    case 2:
                        Log.d(TAG, "onItemClick: Phone number clicked");
                        showPhoneModal();
                        break;
                    case 3:
                        Log.d(TAG, "onItemClick: Signature clicked");
                        showSignatureModal();
                        break;
                }
            }
        });
    }

    public void initPersonalInfoBottomList(ListView list, String[] listTitle, String[] listValue){
        TextWithValueListAdapter listAdapter = new TextWithValueListAdapter(mContext,
                listContainerLayoutResourceId, tvListTitleId, tvListValueId, listTitle, listValue);

        list.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position) {
                    case 0:
                        Log.d(TAG, "onItemClick: Full Name clicked");
                        startActivity(new Intent(PersonalInfoActivity.this, VerifyMatrixActivity.class));
                        break;
                    case 1:
                        Log.d(TAG, "onItemClick: Matrix Number clicked");
                        startActivity(new Intent(PersonalInfoActivity.this, VerifyMatrixActivity.class));
                        break;
                    case 2:
                        Log.d(TAG, "onItemClick: License number clicked");
                        if(user.getUser_verifMatrixStatus() == getResources().getInteger(R.integer.user_verification_status_verified)) {
                            startActivity(new Intent(PersonalInfoActivity.this, VerifyLicenseActivity.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please verify your identity", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
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
                        Croperino.prepareGallery(PersonalInfoActivity.this);
                    }
                });

                dialogOpenCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Croperino.prepareCamera(PersonalInfoActivity.this);
                    }
                });
            }
        });

        dialog.show();
    }

    private void uploadNewPhoto(Bitmap bitmap){
        Log.d(TAG, "uploadingNewPhoto: uploading a new image bitmap to storage.");
        imgProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "uploadNewPhoto: " + imgProgressBar.isShown());
        BackgroundImageResize resize = new BackgroundImageResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadingNewPhoto: uploading a new image uri to storage.");
        imgProgressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "uploadNewPhoto: " + imgProgressBar.isShown());
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imagePath);
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
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
                    /*function for getting image uri */
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(PersonalInfoActivity.this,uris[0]);
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
        Toast.makeText(PersonalInfoActivity.this, "Uploading image", Toast.LENGTH_SHORT).show();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profilePic/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        UploadTask uploadTask = storageReference.putBytes(mUploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PersonalInfoActivity.this, "Upload completed", Toast.LENGTH_SHORT).show();

                //insert the download uri into the firebase database
                Task<Uri> urlTask = storageReference.getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri firebaseUri = urlTask.getResult();
                Log.d(TAG, "onSuccesss: firebase download url: " + firebaseUri.toString());
                updateUserSpecificNode(DatabaseHelper.TABLE_USER_ATTR_PROFILE_PIC_SRC, firebaseUri.toString());
                imgProgressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonalInfoActivity.this, "could not upload photo", Toast.LENGTH_SHORT).show();
                imgProgressBar.setVisibility(View.GONE);
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

    private void retrieveUserInfo(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference().child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Read mUser info from firebase database");
                user = dataSnapshot.getValue(User.class);
                String fullName = "";
                String matrixNo = "";
                if(user.getUser_verifMatrixStatus() ==
                        getResources().getInteger(R.integer.user_verification_status_verified)){
                    fullName = user.getUser_fullName() + " (Verified)";
                    matrixNo = user.getUser_matrixNo() + " (Verified)";
                }
                else{
                    if(user.getUser_verifMatrixStatus() ==
                            getResources().getInteger(R.integer.user_verification_status_new)){
                        fullName = "<font color='#7FD9D5'>Verification pending</font>";
                        matrixNo = "<font color='#7FD9D5'>Verification pending</font>";
                    }
                    else{
                        fullName = "<font color='#FF8A80'>Verify your identity now</font>";
                        matrixNo = "<font color='#FF8A80'>Verify your identity now</font>";
                    }
                }
                String profileName = (user.getUser_profileName() == null) ? "" : user.getUser_profileName();
                String gender = (user.getUser_gender() == null) ? "" : user.getUser_gender();
                String phone = (user.getUser_phoneNo() == null) ? "" : user.getUser_phoneNo();
                String licenseNo = "";
                if(user.getUser_verifLicenseStatus() ==
                        getResources().getInteger(R.integer.user_verification_status_verified)){
                    licenseNo = user.getUser_licenseNo() + " (Verified)";
                }
                else{
                    if(user.getUser_verifLicenseStatus() ==
                            getResources().getInteger(R.integer.user_verification_status_new)){
                        licenseNo = "<font color='#7FD9D5'>Verification pending</font>";
                    }
                    else{
                        licenseNo = "<font color='#FF8A80'>Verify your identity now</font>";
                    }
                }
                String signature = (user.getUser_signature() == null) ? "Let's RideS" : user.getUser_signature();
                String imgUrl = user.getUser_profilePicSrc();

                listValueMiddle = new String[]{profileName, gender, phone, signature};
                listValueBottom = new String[]{fullName, matrixNo, licenseNo};

                initPersonalInfoTopList(lvPersonalInfoTop, listTitleProfilePic, imgUrl);
                initPersonalInfoMiddleList(lvPersonalInfoMiddle, listTitleMiddle, listValueMiddle);
                initPersonalInfoBottomList(lvPersonalInfoBottom, listTitleBottom, listValueBottom);
                progressBar.setVisibility(View.GONE);
                imgProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: The read failed: " + databaseError.getCode());
            }

        });
    }

    private void showProfileNameModal(){
        Log.d(TAG, "showProfileNameModal: showing modal");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Set up the input
        final EditText input = new EditText(this);
        input.setSingleLine();
        input.setText(user.getUser_profileName());
        input.setLayoutParams(params);
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(15);
        input.setFilters(FilterArray);

        //set up the warning message
        final TextView warningMsg = new TextView(this);
        warningMsg.setText(" Cannot be empty");
        warningMsg.setTextColor(getResources().getColor(R.color.colorRed));
        warningMsg.setVisibility(View.GONE);
        warningMsg.setLayoutParams(params);

        container.addView(input);
        container.addView(warningMsg);


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(container)
                .setTitle("Profile Name")
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");
                        if(input.getText().toString().isEmpty()){
                            warningMsg.setVisibility(View.VISIBLE);
                            input.setBackgroundTintList( ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                            Toast.makeText(PersonalInfoActivity.this,"Please enter profile name",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String val = input.getText().toString();
                            if(updateUserSpecificNode(DatabaseHelper.TABLE_USER_ATTR_PROFILE_NAME, val)){
                                Toast.makeText(PersonalInfoActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(PersonalInfoActivity.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
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

    private void showGenderModal(){
        Log.d(TAG, "showGenderModal: showing modal");
        final String[] genders = new String[]{getResources().getString(R.string.male), getResources().getString(R.string.female)};
        int selectedItem = (user.getUser_gender().equals(genders[0])) ? 0 : 1;
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Gender")
                .setSingleChoiceItems(genders, selectedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: " + genders[i] + " clicked");

                        if(updateUserSpecificNode(DatabaseHelper.TABLE_USER_ATTR_GENDER, genders[i])){
                            Toast.makeText(PersonalInfoActivity.this, "Successfully update", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(PersonalInfoActivity.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                        dialogInterface.dismiss();
                    }
                })
                .create();

        dialog.show();

    }

    private void showSignatureModal(){
        Log.d(TAG, "showSignatureModal: showing modal");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        // Set up the input
        final EditText input = new EditText(this);
        input.setSingleLine();
        input.setText(user.getUser_signature());
        input.setLayoutParams(params);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(50);
        input.setFilters(FilterArray);
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

        container.addView(input);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(container)
                .setTitle("Signature")
                .setPositiveButton("SAVE", null) //Set to null. We override the onclick
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");

                        String val = input.getText().toString();
                        if(updateUserSpecificNode(DatabaseHelper.TABLE_USER_ATTR_SIGNATURE, val)){
                            Toast.makeText(PersonalInfoActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(PersonalInfoActivity.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                        }
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

    private void showPhoneModal(){
        Log.d(TAG, "showPhoneModal: showing modal");

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_add_emergency_contact)
                .setTitle("Phone number")
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                final CountryCodePicker ccp = (CountryCodePicker) dialog.findViewById(R.id.ccp);
                final EditText etPhone = (EditText) dialog.findViewById(R.id.etPhone);
                ccp.registerCarrierNumberEditText(etPhone);
                ccp.setNumberAutoFormattingEnabled(false);

                ccp.setFullNumber(user.getUser_phoneNo());

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");
                        String number = ccp.getFormattedFullNumber();
                        if(updateUserSpecificNode(DatabaseHelper.TABLE_USER_ATTR_PHONE_NO, number)){
                            Toast.makeText(PersonalInfoActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(PersonalInfoActivity.this, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                        }
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

    private boolean updateUserSpecificNode(String attr, String val){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference().child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        try {
            userRef.child(attr).setValue(val);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
