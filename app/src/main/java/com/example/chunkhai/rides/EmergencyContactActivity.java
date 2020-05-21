package com.example.chunkhai.rides;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.EmergencyContact;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.ListAdapter.EmergencyContactListAdapter;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EmergencyContactActivity extends AppCompatActivity {
    private static final String TAG = "EmergencyContactActivit";
    private static final String TITLE = "Emergency Contact";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    RelativeLayout btnAdd;
    TextView tvNoContact;
    ListView lvEmergencyContact;
    TextView tvUserNote, tvViewerNote;

    //vars
    Context mContext;
    String uid;
    int listContainerLayoutResourceId;
    int tvListTitleId;
    ArrayList<String> emergencyContacts;
    String telNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        btnAdd = (RelativeLayout) findViewById(R.id.btnAdd);
        tvNoContact = (TextView) findViewById(R.id.tvNoContact);
        lvEmergencyContact = (ListView) findViewById(R.id.lvEmergencyContact);
        tvUserNote = (TextView) findViewById(R.id.tvUserNote);
        tvViewerNote = (TextView) findViewById(R.id.tvViewerNote);

        listContainerLayoutResourceId = R.layout.list_row_emergency_contact;
        tvListTitleId = R.id.tvListTitle;
        emergencyContacts = new ArrayList<>();

        uid = getIntent().getStringExtra(DatabaseHelper.TABLE_USER_ATTR_UID);

        init();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(uid.matches(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if (v.getId() == R.id.lvEmergencyContact) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                menu.setHeaderTitle(emergencyContacts.get(info.position));
                String[] menuItems = getResources().getStringArray(R.array.emergencyContactContextMenuItems);
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String selectedTelNum = emergencyContacts.get(info.position);

        if(menuItemIndex == 0){
            showEditEmergencyContactDialog(selectedTelNum);
        }
        else if(menuItemIndex == 1){
            deleteConfirm(selectedTelNum);
        }

        return true;
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

        if(uid.matches(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

            btnAdd.setVisibility(View.VISIBLE);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (emergencyContacts.size() < 10) {
                        showAddEmergencyContactDialog();
                    } else {
                        Toast.makeText(EmergencyContactActivity.this, "Maximum 10 emergency contact for each mUser", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvUserNote.setVisibility(View.VISIBLE);
            tvViewerNote.setVisibility(View.GONE);
        }
        else {
            tvUserNote.setVisibility(View.GONE);
            tvViewerNote.setVisibility(View.VISIBLE);
        }

        getUserEmergencyContact(uid);

    }

    private void getUserEmergencyContact(String uid){
        Log.d(TAG, "getUserEmergencyContact: getting mUser emergency contacts from database");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(DatabaseHelper.TABLE_EMERGENCY_CONTACT)
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: emergency number -> " + dataSnapshot.getChildrenCount());
                        emergencyContacts = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String telNum = ds.getKey();
                            emergencyContacts.add(telNum);
                        }
                        initEmergencyContactListView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: Error on getting mUser emergency contacts -> " + databaseError);
                        Toast.makeText(EmergencyContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initEmergencyContactListView(){
        Log.d(TAG, "initEmergencyContactListView: initializing rating and review list");

        if(emergencyContacts.size() <= 0){
            tvNoContact.setVisibility(View.VISIBLE);
        }
        else {
            tvNoContact.setVisibility(View.GONE);
        }

        EmergencyContactListAdapter adapter = new EmergencyContactListAdapter(this, listContainerLayoutResourceId, emergencyContacts);

        lvEmergencyContact.setAdapter(adapter);
        registerForContextMenu(lvEmergencyContact);
        setListViewHeightBasedOnChildren(lvEmergencyContact);

    }

    private void showAddEmergencyContactDialog(){
        Log.d(TAG, "showVehicleDetailDialog: showing modal");

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_add_emergency_contact)
                .setTitle("New Emergency Contact")
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CLOSE", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                final CountryCodePicker ccp = (CountryCodePicker) dialog.findViewById(R.id.ccp);
                final EditText etPhone = (EditText) dialog.findViewById(R.id.etPhone);
                ccp.registerCarrierNumberEditText(etPhone);
                ccp.setNumberAutoFormattingEnabled(false);

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");
                        telNum = ccp.getFormattedFullNumber();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(DatabaseHelper.TABLE_EMERGENCY_CONTACT)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(telNum)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Toast.makeText(EmergencyContactActivity.this, telNum + " already existed in your list"
                                                    , Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                            ref.child(DatabaseHelper.TABLE_EMERGENCY_CONTACT)
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(telNum)
                                                    .setValue(1)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(EmergencyContactActivity.this, " Added " + telNum
                                                                    , Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: Error on adding emergency contact -> " + e);
                                                            Toast.makeText(EmergencyContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled: Error on checking emergency contact existence -> " + databaseError);
                                        Toast.makeText(EmergencyContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });

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

    private void showEditEmergencyContactDialog(final String currentTelNum){
        Log.d(TAG, "showEditEmergencyContactDialog: showing modal");

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_add_emergency_contact)
                .setTitle("Edit Emergency Contact")
                .setPositiveButton("SAVE", null)
                .setNegativeButton("CLOSE", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                final CountryCodePicker ccp = (CountryCodePicker) dialog.findViewById(R.id.ccp);
                final EditText etPhone = (EditText) dialog.findViewById(R.id.etPhone);
                ccp.registerCarrierNumberEditText(etPhone);
                ccp.setNumberAutoFormattingEnabled(false);
                ccp.setFullNumber(currentTelNum);

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Alert dialog positive button clicked");
                        telNum = ccp.getFormattedFullNumber();

                        if(PhoneNumberUtils.compare(telNum, currentTelNum)){
                            return;
                        }
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(DatabaseHelper.TABLE_EMERGENCY_CONTACT)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(telNum)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Toast.makeText(EmergencyContactActivity.this, telNum + " already existed in your list", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            deleteEmergencyContact(currentTelNum);
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                            ref.child(DatabaseHelper.TABLE_EMERGENCY_CONTACT)
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(telNum)
                                                    .setValue(1)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(EmergencyContactActivity.this, " Updated", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: Error on update emergency contact -> " + e);
                                                            Toast.makeText(EmergencyContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled: Error on checking emergency contact existence -> " + databaseError);
                                        Toast.makeText(EmergencyContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });

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

    private void deleteEmergencyContact(String telNum) {
        Log.d(TAG, "deleteEmergencyContact: deleting " + telNum + " from database");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(DatabaseHelper.TABLE_EMERGENCY_CONTACT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(telNum)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EmergencyContactActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Error on deleting emergency contact -> " + e);
                        Toast.makeText(EmergencyContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteConfirm(final String telNum){
        new AlertDialog.Builder(this)
                .setTitle(telNum)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteEmergencyContact(telNum);
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
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
