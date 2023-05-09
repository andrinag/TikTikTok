package com.example.tiktiktok;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String DEBUG_TAG = "Gestures";

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());

        // if tracking is switched on this method will update a counter in the ScrollTracker
        if (MainActivity.trackingAllowed == true) {
            ScrollTracker.scrollCounter++;
            System.out.println(ScrollTracker.scrollCounter);
        }
        return true;
    }


}
