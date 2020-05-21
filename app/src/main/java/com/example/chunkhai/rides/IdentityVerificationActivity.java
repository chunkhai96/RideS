package com.example.chunkhai.rides;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chunkhai.rides.Util.ListAdapter.TextOnlyListAdapter;

public class IdentityVerificationActivity extends AppCompatActivity {
    private static final String TAG = "IdentityVerificationAct";
    private static final String TITLE = "Identity Verification";

    //widgets
    TextView tvToolbarTitle;
    RelativeLayout btnBack;
    ListView lvVerification;

    //vars

    Context mContext;
    int listContainerLayoutResourceId;
    int tvListTitleId;
    String[] listTitle = {"Martix/Staff Card Verification", "Driving License Verification"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verification);

        mContext = getApplicationContext();
        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);
        btnBack = (RelativeLayout) findViewById(R.id.btnback);
        lvVerification = (ListView) findViewById(R.id.lvVerification);
        listContainerLayoutResourceId = R.layout.list_row_text_only;
        tvListTitleId = R.id.tvListTitle;

        //Init Toolbar
        tvToolbarTitle.setText(TITLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initVerficationList(lvVerification, listTitle);
    }

    public void initVerficationList(ListView list, String[] listTitle){
        TextOnlyListAdapter listAdapter = new TextOnlyListAdapter(mContext,
                listContainerLayoutResourceId, tvListTitleId, listTitle);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:
                        Log.d(TAG, "onItemClick: Matrix/Staff Card Verification clicked");
                        startActivity(new Intent(IdentityVerificationActivity.this, VerifyMatrixActivity.class));
                        break;
                    case 1:
                        Log.d(TAG, "onItemClick: Driving License Verification clicked");
                        startActivity(new Intent(IdentityVerificationActivity.this, VerifyLicenseActivity.class));
                        break;
                }
            }
        });
    }
}
