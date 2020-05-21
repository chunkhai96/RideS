package com.example.chunkhai.rides;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chunkhai.rides.Object.Discussion;
import com.example.chunkhai.rides.Util.DatabaseHelper;
import com.example.chunkhai.rides.Util.RecycleViewAdapter.RideDiscussionRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RideDiscussionActivity extends AppCompatActivity {
    private static final String TAG = "RideDiscussionActivity";
    private static final String TITLE = "Discussion";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    EditText etMsg;
    ImageButton btnSend;
    RecyclerView rvChatList;
    LinearLayout linLayoutEmptyConversation;

    //vars
    List<Discussion> mDicussions;
    String pr_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_discussion);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        etMsg = (EditText) findViewById(R.id.etMsg);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        rvChatList = (RecyclerView) findViewById(R.id.rvChatList);
        linLayoutEmptyConversation = (LinearLayout) findViewById(R.id.linLayoutEmptyConversation);

        pr_id = getIntent().getStringExtra(DatabaseHelper.TABLE_PROVIDEDRIDE_ATTR_ID);

        init();
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: send button clicked");
                String msg = etMsg.getText().toString();
                etMsg.setText("");
                if(!msg.equals("")) {
                    uploadUserMessage(msg);
                }
            }
        });

        getChatContent();
    }

    private void uploadUserMessage(String msg){
        Log.d(TAG, "uploadUserMessage: uploading mUser sent message to database");

        Discussion discussion = new Discussion(msg, System.currentTimeMillis()
                , FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_DISCUSSION)
                .child(pr_id)
                .push();
        ref.setValue(discussion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: succesfully uploaded");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Fail to upload message -> " + e);
                Toast.makeText(RideDiscussionActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getChatContent(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(DatabaseHelper.TABLE_DISCUSSION)
                .child(pr_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: successsfully retreived chat content");
                mDicussions = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Discussion chat = ds.getValue(Discussion.class);
                    mDicussions.add(chat);
                }
                if(!mDicussions.isEmpty()){
                    linLayoutEmptyConversation.setVisibility(View.GONE);
                }
                else {
                    linLayoutEmptyConversation.setVisibility(View.VISIBLE);
                }
                initChatRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initChatRecycleView(){
        Log.d(TAG, "initChatRecycleView: initializing chat list" + mDicussions.size());
        RideDiscussionRVAdapter adapter = new RideDiscussionRVAdapter(RideDiscussionActivity.this, mDicussions);

        LinearLayoutManager layoutManager = new LinearLayoutManager(RideDiscussionActivity.this);
        layoutManager.setStackFromEnd(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(RideDiscussionActivity.this,
                layoutManager.getOrientation());
        //rvMessageList.addItemDecoration(dividerItemDecoration);

        rvChatList.setLayoutManager(layoutManager);
        rvChatList.setItemAnimator(new DefaultItemAnimator());
        rvChatList.setAdapter(adapter);
    }
}
