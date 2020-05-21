package com.example.chunkhai.rides;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private final String TITLE = "Register";

    //widgets
    RelativeLayout relLayout;
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    CountryCodePicker ccp;
    EditText etProfileName, etPhone, etEmail, etPassword, etConPassword;
    RadioGroup radioGrpGender;
    RadioButton selectedGender;
    TextView warningMsgProfileName, warningMsgPhoneNo, warningMsgEmail, warningMsgPassword, warningMsgConPassword, warningMsgGender;
    Button btnRegister;
    ProgressBar progressBar;

    //vars
    String profileName, phoneNo, email, password, conPassword, gender;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: User register");

        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        etProfileName = (EditText) findViewById(R.id.etProfileName);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        etPhone = (EditText) findViewById(R.id.etPhone);
        ccp.registerCarrierNumberEditText(etPhone);
        ccp.setNumberAutoFormattingEnabled(false);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConPassword = (EditText) findViewById(R.id.etConPassword);
        radioGrpGender = (RadioGroup) findViewById(R.id.radioGrpGender);
        warningMsgProfileName = (TextView) findViewById(R.id.warningMsgProfileName);
        warningMsgPhoneNo = (TextView) findViewById(R.id.warningMsgPhoneNo);
        warningMsgEmail = (TextView) findViewById(R.id.warningMsgEmail);
        warningMsgPassword = (TextView) findViewById(R.id.warningMsgPassword);
        warningMsgConPassword = (TextView) findViewById(R.id.warningMsgConPassword);
        warningMsgGender = (TextView) findViewById(R.id.warningMsgGender);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        mAuth = FirebaseAuth.getInstance();

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegister();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void userRegister(){
        Log.d(TAG, "userRegister: Registering mUser");

        if(isAllFieldsFilledCorrectly()){
            showProgressBar();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");

                                String userId = mAuth.getCurrentUser().getUid();
                                User user = new User(profileName, password, email, phoneNo, gender, System.currentTimeMillis());

                                writeNewUser(userId, user);

                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isAllFieldsFilledCorrectly() {
        boolean isCorrect = false;

        int selectedRadioId = radioGrpGender.getCheckedRadioButtonId();
        selectedGender = (RadioButton) findViewById(selectedRadioId);
        gender = selectedGender.getText().toString();
        profileName = etProfileName.getText().toString();
        phoneNo = ccp.getFullNumber();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        conPassword = etConPassword.getText().toString();

        warningMsgProfileName.setVisibility(View.GONE);
        warningMsgPhoneNo.setVisibility(View.GONE);
        warningMsgEmail.setVisibility(View.GONE);
        warningMsgPassword.setVisibility(View.GONE);
        warningMsgConPassword.setVisibility(View.GONE);
        warningMsgGender.setVisibility(View.GONE);

        if(TextUtils.isEmpty(profileName)){
            Log.d(TAG, "isAllFieldsFilled: Profile name is empty");

            warningMsgProfileName.setVisibility(View.VISIBLE);
        }
        else if(TextUtils.isEmpty(etPhone.getText().toString())){
            Log.d(TAG, "isAllFieldsFilled: Mobile phone number is empty");

            warningMsgPhoneNo.setVisibility(View.VISIBLE);
        }
        else if(TextUtils.isEmpty(email)){
            Log.d(TAG, "isAllFieldsFilled: Email is empty");

            warningMsgEmail.setVisibility(View.VISIBLE);
        }
        else if(TextUtils.isEmpty(password)){
            Log.d(TAG, "isAllFieldsFilled: Password is empty");

            warningMsgPassword.setVisibility(View.VISIBLE);
        }
        else if(TextUtils.isEmpty(conPassword)){
            Log.d(TAG, "isAllFieldsFilled: Confirm password is empty");

            warningMsgConPassword.setVisibility(View.VISIBLE);
        }
        else if(!password.equals(conPassword)){
            Log.d(TAG, "isAllFieldsFilled: Password and confirm password did not match");

            warningMsgConPassword.setText("Not match");
            warningMsgConPassword.setVisibility(View.VISIBLE);
        }
        else if(TextUtils.isEmpty(gender)){
            Log.d(TAG, "isAllFieldsFilled: Gender is empty");

            warningMsgGender.setVisibility(View.VISIBLE);
        }
        else {
            isCorrect = true;
        }

        return isCorrect;
    }

    private void writeNewUser(String userId, User user) {
        Log.d(TAG, "writeNewUser: write the info to firebase");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DatabaseHelper.TABLE_USER).child(userId).setValue(user)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RegisterActivity.this, "successfully registered"
                        , Toast.LENGTH_SHORT).show();
                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finish();
                }
                hideProgressBar();
            }
        });
    }

    private void showProgressBar(){
        relLayout.setBackgroundColor(getResources().getColor(R.color.colorGreyWithTransparency));
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void hideProgressBar(){
        relLayout.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
