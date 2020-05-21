package com.example.chunkhai.rides;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hololo.tutorial.library.CurrentFragmentListener;
import com.hololo.tutorial.library.PermissionStep;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class Tutorial extends TutorialActivity{
    private static final String TAG = "Tutorial";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String CAMERA = Manifest.permission.CAMERA;
    public static final String TUTORIAL_PREFERENCE = "Tutorial";
    public static final String TUTORIAL_SHOWED = "showed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment(new Step.Builder().setTitle("Search & Join")
                .setContent("Search ride in our database in a central carpooling platform.")
                .setBackgroundColor(Color.parseColor("#4472C4")) // int background color
                .setDrawable(R.drawable.tutorial1) // int top drawable
                .setSummary("First step to reduce pollution")
                .build());

        addFragment(new Step.Builder().setTitle("Publish & Share")
                .setContent("Publish a ride and share the fare with others in this largest carpool community.")
                .setBackgroundColor(Color.parseColor("#00B0F0")) // int background color
                .setDrawable(R.drawable.tutorial2) // int top drawable
                .setSummary("Sharing is caring")
                .build());

        addFragment(new Step.Builder().setTitle("Schedule Summary")
                .setContent("All the ride schedule is manageable")
                .setBackgroundColor(Color.parseColor("#FF0066")) // int background color
                .setDrawable(R.drawable.tutorial3) // int top drawable
                .setSummary("Forgetful is not a problem")
                .build());

        addFragment(new Step.Builder().setTitle("Discussion")
                .setContent("Discuss about the trip, chatting, communicate.")
                .setBackgroundColor(Color.parseColor("#00B050")) // int background color
                .setDrawable(R.drawable.tutorial4) // int top drawable
                .setSummary("We know you try to talk")
                .build());

        // Permission Step
        addFragment(new PermissionStep.Builder().setTitle("Permission")
                .setContent("Please allow us to serve you better")
                .setBackgroundColor(Color.parseColor("#37464f"))
                .setDrawable(R.drawable.tutorial5)
                .setSummary("The only last step to begin")
                .setPermissions(new String[]{FINE_LOCATION,COURSE_LOCATION,READ_STORAGE,WRITE_STORAGE, CAMERA})
                .build());
    }

    @Override
    public void currentFragmentPosition(int position) {
        Log.d(TAG, "currentFragmentPosition: " + position);
    }

    @Override
    public void finishTutorial() {
        SharedPreferences sharedPreferences = getSharedPreferences(TUTORIAL_PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TUTORIAL_SHOWED, 1);
        editor.apply();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
