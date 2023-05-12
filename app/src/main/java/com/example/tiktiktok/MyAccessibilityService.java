package com.example.tiktiktok;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private String currentPackageName;
    private long lastUpdateTime;

    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("In here");
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            long currentTime = System.currentTimeMillis();
            if (!packageName.equals(currentPackageName)) {
                // The user has switched to a different app, so stop tracking the previous app and start tracking the new app
                stopTrackingApp(currentPackageName, lastUpdateTime, currentTime);
                startTrackingApp(packageName, currentTime);
                currentPackageName = packageName;
                lastUpdateTime = currentTime;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void startTrackingApp(String packageName, long startTime) {

    }

    private void stopTrackingApp(String packageName, long startTime, long endTime) {
        // Your code to stop tracking app usage goes here
        long totalTime = endTime - startTime;
        System.out.println(packageName + " Time:  " + totalTime);
    }
}

