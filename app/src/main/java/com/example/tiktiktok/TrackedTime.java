package com.example.tiktiktok;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackedTime implements Application.ActivityLifecycleCallbacks{

        private int activityReferences = 0;
        private boolean isActivityChangingConfigurations = false;
        private long startingTime;
        private long stopTime;
        /**startingTime is the UNIX timestamp (though I increased readability by converting into millisecond to second) your app is being foregrounded to the user and stopTime is when it is being backgrounded (Paused)*/

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

    @Override
        public void onActivityStarted(Activity activity) {
            System.out.println("acitivity started");
            if (++activityReferences == 1 && !isActivityChangingConfigurations) {

                // App enters foreground
                startingTime=System.currentTimeMillis()/1000; //This is the starting time when the app began to start
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            isActivityChangingConfigurations = activity.isChangingConfigurations();
            if (--activityReferences == 0 && !isActivityChangingConfigurations) {

                // App enters background
                stopTime = System.currentTimeMillis() / 1000;//This is the ending time when the app is stopped

                long totalSpentTime = stopTime - startingTime; //This is the total spent time of the app in foreground. Here you can post the data of total spending time to the server.
                System.out.println(activity.getApplicationInfo().packageName + " " + totalSpentTime + "s");
            }
        }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
