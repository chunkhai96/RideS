package com.example.chunkhai.rides;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //widgets
    RelativeLayout relLayout;
    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    TextView warningMsgEmail, warningMsgPassword;
    TextView tvForgotPassword, tvRegister;
    ProgressBar progressBar;

    //vars
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //check is the mUser logged in before
        if(isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        relLayout = (RelativeLayout) findViewById(R.id.relLayout);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        warningMsgEmail = (TextView) findViewById(R.id.warningMsgEmail);
        warningMsgPassword = (TextView) findViewById(R.id.warningMsgPassword);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected()) {
                    userLogin();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please connect your device to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected()) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please connect your device to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean isLoggedIn(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        if(mFirebaseAuth.getCurrentUser() != null){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(cm.getActiveNetworkInfo() != null && netInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    private void userLogin(){
        warningMsgEmail.setVisibility(View.GONE);
        warningMsgPassword.setVisibility(View.GONE);
        String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Log.d(TAG, "userLogin: Email is empty");

            warningMsgEmail.setText("Please fill in your email");
            warningMsgEmail.setVisibility(View.VISIBLE);
            return;
        }

        if(TextUtils.isEmpty(password)){
            Log.d(TAG, "userLogin: Password is empty");

            warningMsgPassword.setText("Please fill in your password");
            warningMsgPassword.setVisibility(View.VISIBLE);
            return;
        }

        showProgressBar();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                    .child(DatabaseHelper.TABLE_USER)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if(!user.getUser_password().equals(password)){
                                        ref.child(DatabaseHelper.TABLE_USER_ATTR_PASSWORD).setValue(password);
                                    }
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    hideProgressBar();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(TAG, "onCancelled: Error on retrieve user info -> " + databaseError);
                                    hideProgressBar();
                                }
                            });
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Invalid email and password", Toast.LENGTH_LONG).show();
                            hideProgressBar();
                        }
                    }
                });
    }

    private void resetPassword(){
        warningMsgEmail.setVisibility(View.GONE);
        warningMsgPassword.setVisibility(View.GONE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = etEmail.getText().toString();

        if(emailAddress.equals("") || emailAddress.isEmpty()){
            warningMsgEmail.setText("Please fill in your email");
            warningMsgEmail.setVisibility(View.VISIBLE);
        }
        else {
            showProgressBar();
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                hideProgressBar();
                                warningMsgEmail.setVisibility(View.GONE);
                                warningMsgPassword.setVisibility(View.GONE);
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(LoginActivity.this, "Please check your email", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressBar();
                            Log.d(TAG, "Failed to send password reset email -> " + e);
                            Toast.makeText(LoginActivity.this, "Unregistered email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
