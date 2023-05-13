package com.example.tiktiktok;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Handler handler;
    private Runnable runnable;

    private Context context;

    Button goToSettingsButton;
    Switch trackingSwitch;
    TextView timeView;

    String currentApp;

    // ------------------------------------------------- //
    // timers for how long the apps have been in use     //
    // ------------------------------------------------- //
    int youtubeTimer;
    int tiktokTimer;
    int instagramTimer;
    // ------------------------------------------------- //


    // ------------------------------------------------- //
    // time limits when the warnings are being executed
    // ------------------------------------------------- //
    public static int firstWarning = 15;
    public static int secondWarning = 50;
    public static int thirdWarning = 75;
    public static int fourthWarning = 100;
    // ------------------------------------------------- //


    boolean trackingAllowed = false;

    public MainActivity() throws FileNotFoundException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // used for the AppChecker
        context = this;

        // checks if the permission has been given to display things over other apps
        checkOverlayPermission();

        trackingSwitch = (Switch) findViewById(R.id.trackingSwitch);

        handler = new Handler();
        youtubeTimer = 0;
        tiktokTimer = 0;
        instagramTimer = 0;
        currentApp = "nothing";

        timeView = (TextView) findViewById(R.id.timeView);
        timeView.setText("Click for starting the Timer");
        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isChecked = trackingSwitch.isChecked();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trackingAllowed = true;
                    startTracking();
                } else {
                    trackingAllowed = false;
                    handler.removeCallbacks(runnable);      // thread oder wases esch abschalte
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
        String yt = "com.google.android.youtube";
        String tiktok = "com.zhiliaoapp.musically";
        String instagram = "com.instagram.android";

        // checks every 5 seconds which app has been opened the longest in the last 5 seconds
        runnable = new Runnable() {
            @Override
            public void run() {
                currentApp = ForegroundAppChecker.getForegroundApp(context);     // calls the method which checks usage in last 5 seconds

                // ---------------------------------------------- //
                // USED FOR TESTING
                // ---------------------------------------------- //
                System.out.println("MOMENTAN: " + currentApp);
                // ---------------------------------------------- //

                // depending on which app is currently used a timer will be increased
                if (currentApp != null) {
                    if (currentApp.equals(yt)) {
                        youtubeTimer += 5;
                        System.out.println("YOUTUBE: " + youtubeTimer);
                    } else if (currentApp.equals(tiktok)) {
                        tiktokTimer += 5;
                        System.out.println("TIKTOK: " + tiktokTimer);
                    } else if (currentApp.equals(instagram)) {
                        instagramTimer += 5;
                        System.out.println("INSTAGRAM: " + instagramTimer);
                    }

                    // CONSEQUENCES
                    if (youtubeTimer == firstWarning || instagramTimer == firstWarning || tiktokTimer == firstWarning) {
                        // showing a warning popup
                        createOverlay(R.layout.first_warning);
                        System.out.println("LIMIT ERREICHT");
                    } else if (youtubeTimer == secondWarning || instagramTimer == secondWarning || tiktokTimer == secondWarning) {
                        // showing a warning popup
                        createOverlay(R.layout.second_warning);

                        // additional actions: ...
                        // TODO
                    } else if (youtubeTimer == thirdWarning || instagramTimer == thirdWarning || tiktokTimer == thirdWarning) {
                        // showing a warning popup
                        createOverlay(R.layout.third_warning);

                        // additional actions ...
                        // TODO
                    } else if (youtubeTimer == fourthWarning || instagramTimer == fourthWarning || tiktokTimer == fourthWarning) {
                        // showing a warning popup
                        createOverlay(R.layout.fourth_warning);

                        // additional actions ...
                        // TODO
                    }
                }

                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable, 5000);        //
    }

    /**
     * asks for permission to draw over other apps if it's not already given
     * is called when the app is first launcher
     */
    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }

    /**
     * Takes an xml file and creates a popup out of it
     * pop up will also be displayed over other apps
     * button in the overlay (id must be window_close) gets assigned as an exit button
     * @param layoutResId usage: createOverlay(R.layout.xml_name)
     */
    private void createOverlay(int layoutResId) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        View overlayView = LayoutInflater.from(this).inflate(layoutResId, null);

        windowManager.addView(overlayView, params);

        // Find the close button in the xml
        Button closeButton = overlayView.findViewById(R.id.window_close);

        // Set click listener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the overlay view from the window manager
                windowManager.removeView(overlayView);
            }
        });
    }

}