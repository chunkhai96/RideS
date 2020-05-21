package com.example.chunkhai.rides;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.Notification;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.ListAdapter.NotificationListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private final String TITLE = "Notification";

    //widgets
    RelativeLayout rlNoNotification;
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    ListView lvNotification;
    ProgressBar progressBar;

    //vars
    ArrayList<Notification> mNotifications;
    int listContainerLayoutResourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        rlNoNotification = (RelativeLayout) findViewById(R.id.rlNoNotification);
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        lvNotification = (ListView) findViewById(R.id.lvNotification);
        listContainerLayoutResourceId = R.layout.list_row_notification;


        init();
        getUserNotification();
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
    }

    private void getUserNotification() {
        Log.d(TAG, "getUserNotification: getting mUser notification list");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child(DatabaseHelper.TABLE_NOTIFICATION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNotifications = new ArrayList<>();
                String recipientUid = dataSnapshot.getKey();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notification notif = ds.getValue(Notification.class);
                    notif.setNotif_reciever_uid(recipientUid);
                    notif.setNotif_id(ds.getKey());
                    mNotifications.add(notif);
                }
                Collections.reverse(mNotifications);
                init();
                initNotificationList(lvNotification, mNotifications);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error on retrieving mUser notifications");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initNotificationList(ListView list, ArrayList<Notification> notifications){

        Log.d(TAG, "initNotificationList: initializing " + notifications.size());

        if(notifications.size() == 0){
            rlNoNotification.setVisibility(View.VISIBLE);
        }
        else {
            rlNoNotification.setVisibility(View.GONE);
        }

        NotificationListAdapter listAdapter = new NotificationListAdapter(getApplicationContext(),
                listContainerLayoutResourceId, notifications);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "OnItemCLick: notification (" + mNotifications.get(position).getNotif_id());
                if(mNotifications.get(position).getNotif_status() == getResources().getInteger(R.integer.notification_status_new)){
                    archiveNotification(mNotifications.get(position).getNotif_reciever_uid(), mNotifications.get(position).getNotif_id());
                }
                Intent intent = new Intent(NotificationActivity.this, RideDetailActivity.class);
                intent.putExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID, mNotifications.get(position).getNotif_ref_providedRideId());
                startActivity(intent);

            }
        });

    }

    private void archiveNotification(final String recipient_uid, final String notif_id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_NOTIFICATION)
                .child(recipient_uid)
                .child(notif_id)
                .child(DatabaseHelper.TABLE_NOTIFICATION_ATTR_STATUS);
        ref.setValue(getResources().getInteger(R.integer.notification_status_archived))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: User #" + recipient_uid + " - notification #" + notif_id + "archived");
                    }
                });
    }

}
