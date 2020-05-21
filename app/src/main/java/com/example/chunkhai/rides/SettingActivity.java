package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.User;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.ListAdapter.TextOnlyListAdapter;
import com.example.chunkhai.rides.Util.ListAdapter.TextWithValueListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    //widget
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    ListView lvSetting1;
    ListView lvSetting2;
    ListView lvSetting3;

    //vars
    String title = "Setting";
    Context mContext;
    int listContainerLayoutResourceId;
    int tvListTitleId;
    String[] listTitle1 = {"Identification Verification", "License Verification", "Change Password"};
    String[] listTitle2 = {"Emergency Contact"};
    String[] listTitle3 = {"Log Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        lvSetting1 = (ListView) findViewById(R.id.lvSetting1);
        lvSetting2 = (ListView) findViewById(R.id.lvSetting2);
        lvSetting3 = (ListView) findViewById(R.id.lvSetting3);
        listContainerLayoutResourceId = R.layout.list_row_text_only;
        tvListTitleId = R.id.tvListTitle;

        //Init Toolbar
        tvToolbarTitle.setText(title);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initSettingList1(lvSetting1, listTitle1);
        initSettingList2(lvSetting2, listTitle2);
        initSettingList3(lvSetting3, listTitle3);
    }

    public void initSettingList1(ListView list, String[] listTitle){
        TextOnlyListAdapter listAdapter = new TextOnlyListAdapter(mContext,
                listContainerLayoutResourceId, tvListTitleId, listTitle);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position) {
                    case 0:
                        Log.d(TAG, "onItemClick: Identification Verification clicked");
                        startActivity(new Intent(SettingActivity.this, VerifyMatrixActivity.class));
                        break;
                    case 1:
                        Log.d(TAG, "onItemClick: License Verification clicked");
                        startActivity(new Intent(SettingActivity.this, VerifyLicenseActivity.class));
                        break;
                    case 2:
                        Log.d(TAG, "onItemClick: Change Password clicked");
                        showConfirmPasswordDialog();
                        break;
                }
            }
        });
    }

    public void initSettingList2(ListView list, String[] listTitle){
        TextOnlyListAdapter listAdapter = new TextOnlyListAdapter(mContext,
                listContainerLayoutResourceId, tvListTitleId, listTitle);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:
                        Log.d(TAG, "onItemClick: Emergency Contact clicked");
                        Intent intent = new Intent(SettingActivity.this, EmergencyContactActivity.class);
                        intent.putExtra(DatabaseHelper.TABLE_USER_ATTR_UID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    public void initSettingList3(ListView list, String[] listTitle){
        TextOnlyListAdapter listAdapter = new TextOnlyListAdapter(mContext,
                listContainerLayoutResourceId, tvListTitleId, listTitle);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:
                        Log.d(TAG, "onItemClick: Log Out clicked");
                        userLogout();
                        break;
                }
            }
        });
    }

    private void userLogout(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(DatabaseHelper.TABLE_USER)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(DatabaseHelper.TABLE_USER_ATTR_DEVICE_TOKEN)
                .setValue("");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    private void showConfirmPasswordDialog(){
        Log.d(TAG, "showConfirmPasswordDialog: showing modal");

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_reauthenticate)
                .setTitle("Please enter current password")
                .setPositiveButton("PROCEED", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                final EditText etCurrentPassword = (EditText) dialog.findViewById(R.id.etCurrentPassword);
                final TextView warningMsg = (TextView) dialog.findViewById(R.id.warningMsg);
                final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");

                        progressBar.setVisibility(View.VISIBLE);
                        String val = etCurrentPassword.getText().toString();

                        if(val.equals("") || val.isEmpty()){
                            warningMsg.setVisibility(View.VISIBLE);
                        }
                        else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), val);

                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User re-authenticated.");
                                                dialog.dismiss();
                                                showChangePasswordDialog();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Failed to re-authenticate");
                                            Toast.makeText(SettingActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
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

    private void showChangePasswordDialog(){
        Log.d(TAG, "changePassword: show model");

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_change_password)
                .setTitle("Please enter new password")
                .setPositiveButton("PROCEED", null)
                .setNegativeButton("CANCEL", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                final EditText etNewPassword = (EditText) dialog.findViewById(R.id.etNewPassword);
                final EditText etConfirmNewPassword = (EditText) dialog.findViewById(R.id.etConfirmNewPassword);
                final TextView warningMsg = (TextView) dialog.findViewById(R.id.warningMsg);
                final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");
                        progressBar.setVisibility(View.VISIBLE);
                        final String newPassword = etNewPassword.getText().toString();
                        String confirmNewPassword = etConfirmNewPassword.getText().toString();

                        if(newPassword.equals("") || newPassword.isEmpty() || confirmNewPassword.equals("") || newPassword.isEmpty()) {
                            warningMsg.setVisibility(View.VISIBLE);
                            warningMsg.setText("Both fields cannot be empty");
                        }
                        else {
                            if (newPassword.equals(confirmNewPassword)) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "User password updated.");
                                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                final DatabaseReference userRef = database.getReference().child(DatabaseHelper.TABLE_USER)
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                try {
                                                    userRef.child(DatabaseHelper.TABLE_USER_ATTR_PASSWORD).setValue(newPassword);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                dialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onComplete: Error on changing password -> " + e);
                                                Toast.makeText(SettingActivity.this, "Error on changing password", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                warningMsg.setVisibility(View.VISIBLE);
                                warningMsg.setText("Password not matches");
                            }
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
}
