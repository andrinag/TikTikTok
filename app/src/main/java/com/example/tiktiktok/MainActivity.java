package com.example.tiktiktok;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tiktiktok.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Handler handler;
    private Runnable runnable;

    private Context context;
    private boolean fullBrightness = false;

    Switch trackingSwitch;

    Switch youTubeSwitch;

    Switch instagramSwitch;

    Switch tikTokSwitch;


    boolean instagramTracking;

    boolean youTubeTracking;

    boolean tikTokTracking;

    String yt = "com.google.android.youtube";

    String tiktok = "com.zhiliaoapp.musically";

    String instagram = "com.instagram.android";


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
    // public static int firstWarning = 300;
    public static int secondWarning = 30;
    // public static int secondWarning = 600;
    public static int thirdWarning = 45;
    // public static int thirdWarning = 900;
    public static int fourthWarning = 60;
    // public static int fourthWarning = 1200;

    public static int thirdWarningIgnored = thirdWarning + 5;

    public static int fourthWarningIgnored = fourthWarning + 5;
    // ------------------------------------------------- //

    boolean trackingAllowed = false;


    /**
     * gets called when the app is opened
     * @param savedInstanceState data to reload UI state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;     // used in some methods

        updateInstagramTimeGUI();
        updateTikTokGUI();
        updateYTTimeGUI();

        // checking permissions and may open windows to let the user give those permissions
        checkOverlayPermission();
        requestWriteSettingsPermission();

        // --------------------------------------------------- //
        //  getting the switches and setting them to inactive
        // --------------------------------------------------- //
        trackingSwitch = findViewById(R.id.trackingSwitch);
        instagramSwitch = findViewById(R.id.instagramSwitch);
        tikTokSwitch = findViewById(R.id.tikTokSwitch);
        youTubeSwitch = findViewById(R.id.youTubeSwitch);
        instagramSwitch.setClickable(false);
        tikTokSwitch.setClickable(false);
        youTubeSwitch.setClickable(false);
        youTubeSwitch.setAlpha(0.5f);
        tikTokSwitch.setAlpha(0.5f);
        instagramSwitch.setAlpha(0.5f);
        // --------------------------------------------------- //

        handler = new Handler();
        youtubeTimer = 0;
        tiktokTimer = 0;
        instagramTimer = 0;
        currentApp = "nothing";

        // needed in the ForegroundAppChecker class
        ForegroundAppChecker.createUsageStatsManager(this);

        /**
         * Listener for the Instagram Settings button. (Should the Tracking be allow?)
         */
        instagramSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isChecked = instagramSwitch.isChecked();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    instagramTracking = true;
                    System.out.println("Instagram Tracking");
                } else {
                    instagramTracking = false;
                    handler.removeCallbacks(runnable);
                }
                updateInstagramTimeGUI();

            }
        });

        /**
         * Listener for the YouTube Settings button. (Should the Tracking be allow?)
         */
        youTubeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isChecked = youTubeSwitch.isChecked();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    youTubeTracking = true;
                    System.out.println("YouTube Tracking");
                } else {
                    youTubeTracking = false;
                    handler.removeCallbacks(runnable);
                }
                updateYTTimeGUI();
            }
        });

        /**
         * Listener for the TikTok Settings button. (Should the Tracking be allow?)
         */
        tikTokSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isChecked = tikTokSwitch.isChecked();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tikTokTracking = true;
                    System.out.println("Tiktok Tracking on");
                } else {
                    tikTokTracking = false;
                    handler.removeCallbacks(runnable);
                }
                updateTikTokGUI();
            }
        });

        // setting what the trackingSwitch does
        // used to start/stop tracking
        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isChecked = trackingSwitch.isChecked();

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trackingAllowed = true;
                    startTracking();
                    youTubeSwitch.setClickable(true);
                    instagramSwitch.setClickable(true);
                    tikTokSwitch.setClickable(true);
                    youTubeSwitch.setAlpha(1);
                    tikTokSwitch.setAlpha(1);
                    instagramSwitch.setAlpha(1);
                } else {
                    trackingAllowed = false;
                    handler.removeCallbacks(runnable);      // terminate thread

                    // deactivating the app switches
                    youTubeSwitch.setChecked(false);
                    instagramSwitch.setChecked(false);
                    tikTokSwitch.setChecked(false);
                    youTubeSwitch.setClickable(false);
                    instagramSwitch.setClickable(false);
                    tikTokSwitch.setClickable(false);
                    youTubeSwitch.setAlpha(0.5f);
                    tikTokSwitch.setAlpha(0.5f);
                    instagramSwitch.setAlpha(0.5f);
                }
            }

        });

    }

    /**
     * Was automatically added by android studio in the example project.
     * Handles the click events.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController
                (this, R.id.nav_host_fragment_content_main);
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

        // checks every 5 seconds which app has been opened last in the last 5 seconds
        runnable = new Runnable() {
            @Override
            public void run() {

                // calls the method which checks usage in last 5 seconds
                currentApp = ForegroundAppChecker.getForegroundApp();

                updateInstagramTimeGUI();
                updateTikTokGUI();
                updateYTTimeGUI();


                // ---------------------------------------------- //
                // USED FOR TESTING & DEBUGGING
                // ---------------------------------------------- //
                System.out.println("CURRENT APP: " + currentApp);
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

                    // ----------------------------------------------------------- //
                    // executing warnings if thresholds have been reached exactly
                    // normally consist of a simple popup
                    // ----------------------------------------------------------- //

                    // first warning has been reached
                    if (youtubeTimer == firstWarning  && youTubeTracking && currentApp.equals(yt)
                            || instagramTimer == firstWarning && instagramTracking && currentApp.equals(instagram)
                            || tiktokTimer == firstWarning && tikTokTracking && currentApp.equals(tiktok)) {
                        // showing a warning popup
                        createOverlay(R.layout.first_warning);

                    // second warning has been reached
                    } else if (youtubeTimer == secondWarning && youTubeTracking && currentApp.equals(yt)
                            || instagramTimer == secondWarning && instagramTracking && currentApp.equals(instagram)
                            || tiktokTimer == secondWarning && tikTokTracking && currentApp.equals(tiktok)) {
                        // showing a warning popup
                        createOverlay(R.layout.second_warning);

                        // changing the screen brightness to full
                        setScreenBrightness(0);
                        fullBrightness = true;

                    // third warning has been reached
                    } else if (youtubeTimer == thirdWarning && youTubeTracking && currentApp.equals(yt)
                            || instagramTimer == thirdWarning && instagramTracking && currentApp.equals(instagram)
                            || tiktokTimer == thirdWarning && tikTokTracking && currentApp.equals(tiktok)) {
                        // showing a warning popup
                        createOverlay(R.layout.third_warning);
                        takeAwaySound();

                    // fourth warning has been reached
                    } else if (youtubeTimer == fourthWarning && youTubeTracking && currentApp.equals(yt)
                            || instagramTimer == fourthWarning && instagramTracking && currentApp.equals(instagram)
                            || tiktokTimer == fourthWarning && tikTokTracking && currentApp.equals(tiktok)) {
                        // showing a warning popup
                        createOverlay(R.layout.fourth_warning);

                    }

                    // ------------------------------------------------------------------------------- //
                    // warnings which keep executing every 5 seconds after threshold has been reached
                    // ------------------------------------------------------------------------------- //

                    // ADJUSTING SCREEN BRIGHTNESS
                    if (youtubeTimer >= secondWarning && currentApp.equals(yt) && youTubeTracking
                            || instagramTimer >= secondWarning && currentApp.equals(instagram) && instagramTracking
                            || tiktokTimer >= secondWarning && currentApp.equals(tiktok) && tikTokTracking) {
                        // changes the brightness to either full or max brightness every 5 seconds
                        if (fullBrightness) {
                            setScreenBrightness(0);
                            fullBrightness = false;
                        } else {
                            setScreenBrightness(255);
                            fullBrightness = true;
                        }
                    }
                    // TAKING AWAY SOUND
                    if (youtubeTimer >= thirdWarningIgnored && currentApp.equals(yt) && youTubeTracking
                            || instagramTimer >= thirdWarningIgnored && currentApp.equals(instagram) && instagramTracking
                            || tiktokTimer >= thirdWarningIgnored  && currentApp.equals(tiktok) && tikTokTracking) {
                        // if they keep turning off the sound, the sound will be turned off every 5s
                        takeAwaySound();
                    }

                    if (youtubeTimer >= fourthWarningIgnored && currentApp.equals(yt) && youTubeTracking
                            || instagramTimer >= fourthWarningIgnored && currentApp.equals(instagram) && instagramTracking
                            || tiktokTimer >= fourthWarningIgnored && currentApp.equals(tiktok) && tikTokTracking) {
                        // If they keep ignoring the fourth warning and stay on the app, the warning keeps showing up every 5s
                        createOverlay(R.layout.app_block_layover);
                    }

                }

                handler.postDelayed(this, 5000);        // time to wait until loop starts again
            }
        };

        handler.postDelayed(runnable, 5000);
    }

    /**
     * asks for permission to draw over other apps if it's not already given
     * is called when the app is first launched
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
     * Turns off the sound and puts the phone into do not disturb mode
     */
    public void takeAwaySound() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        checkSoundPermission();
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    /**
     * asks for permission to turn off the sound (its actually the do not disturb mode)
     * is always called when the app is launched for the first time.
     */
    public void checkSoundPermission() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
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

    /**
     * used to change the screen brightness of the device
     * @param brightnessValue the desired brightness; must be between 0 and 255
     *
     * This method was done with some help from chatGPT
     **/
    public void setScreenBrightness(int brightnessValue) {
        ContentResolver cR = getContentResolver();
        Window window = getWindow();
         writeBrightnessSettings(cR, brightnessValue);
         applyBrightness(window, brightnessValue);
    }

    /**
     * helper function for setScreenBrightness
     * @param contentResolver as taken from the setScreenBrightness method
     * @param brightnessValue the desired brightness; must be between 0 and 255
     */
    private void writeBrightnessSettings(ContentResolver contentResolver, int brightnessValue) {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
    }

    /**
     * helper function for setScreenBrightness
     * @param window current open window
     * @param brightnessValue desired brightness; must be between 0 and 255
     */
    private void applyBrightness(Window window, int brightnessValue) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightnessValue / 255f;
        window.setAttributes(layoutParams);
    }

    /**
     * requesting ACTION_MANAGE_WRITE_SETTINGS permission
     * used for changing the brightness
     */
    private void requestWriteSettingsPermission() {
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * updating the in-app GUI regarding the Instagram timer
     */
    private void updateInstagramTimeGUI() {
        TextView InstagramTime = findViewById(R.id.InstagramTime);
        if (instagramTracking) {
            if (instagramTimer < 60) {
                InstagramTime.setText("Time spent on Instagram:   " + "> 1 min");
            }
            InstagramTime.setText("Time spent on Instagram:   " + instagramTimer);
        } else {
            InstagramTime.setText("Instagram Tracking is not activated");
        }
    }

    /**
     * updating the in-app GUI regarding the YouTube timer
     */
    private void updateYTTimeGUI() {
        TextView YouTubeTime = findViewById(R.id.YouTubeTime);
        if (youTubeTracking) {
            if (youtubeTimer < 60) {
                YouTubeTime.setText("Time spent on YouTube     " + "> 1 min");
            }
            YouTubeTime.setText("Time spent on YouTube:     " + youtubeTimer);
        } else {
            YouTubeTime.setText("YouTube Tracking is not activated");
        }
    }

    /**
     * updating the in-app GUI regarding the TikTok timer
     */
    private void updateTikTokGUI() {
        TextView TikTikTime = findViewById(R.id.TikTikTime);
        if (tikTokTracking) {
            if (tiktokTimer < 60) {
                TikTikTime.setText("Time spent on TikTok:      " + " > 1 min ");
            }
            TikTikTime.setText("Time spent on TikTok:      " + tiktokTimer);
        } else {
            TikTikTime.setText("TikTok Tracking is not activated");
        }
    }
}