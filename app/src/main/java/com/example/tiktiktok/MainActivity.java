package com.example.tiktiktok;

import android.content.Intent;
import android.gesture.Gesture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tiktiktok.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    Button goToSettingsButton;
    Switch trackingSwitch;
    TextView timeView;

    public static boolean trackingAllowed = false;

    long stoppingTime;

    private GestureDetectorCompat mDetector;

    public MainActivity() throws FileNotFoundException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // used for detecting scrolling
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        trackingSwitch = (Switch) findViewById(R.id.trackingSwitch);

        timeView = (TextView) findViewById(R.id.timeView);
        timeView.setText("Hello There Motherfucker");
        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isChecked = trackingSwitch.isChecked();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trackingAllowed = true;
                    startTracking();
                    timeView.setText("is checked");
                } else {
                    trackingAllowed = false;
                }
            }
        });

        /**
         * Changing to the settings menu
         */
        goToSettingsButton = findViewById(R.id.goToSettingsButton);
        goToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * was automatically added by android studio. Don't know what it is.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Method for switching to the settings menu
     */
    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(switchActivityIntent);
    }

    /**
     * start tracking the video content
     */
    private void startTracking() {
        System.out.println("start tracking");
    }

    public void runInBackground(int seconds) {
        // this.device.runAppInBackground(seconds);
    }

}