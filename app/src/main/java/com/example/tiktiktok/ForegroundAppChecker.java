package com.example.tiktiktok;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import java.util.List;

/**
 * Contains a method which checks which app has been used the most in the last 5 seconds
 * uses USAGE_STATS_SERVICE, permission also needs to be manually given to
 * the app by the user ("Zugriff auf Nutzungsdaten")
 */
public class ForegroundAppChecker {

    static String lastOpenedApp = null; // used in case the most used app is "null"
    static UsageStatsManager usageStatsManager;

    /**
     * used to set the UsageStatsManager when the app first starts up (oncreate() in MainActivity.java)
     * @param context "this" can be used as a context in MainActivity.java
     */
    public static void createUsageStatsManager(Context context) {
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    /**
     * checks which app has been used last the past 5 seconds
     * @return String of the name of the app with the most usage
     */
    public static String getForegroundApp() {
        String foregroundApp = null;

        if (usageStatsManager != null) {
            long currentTime = System.currentTimeMillis();

            // Get the usage stats of the last 5 seconds
            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, currentTime - 5000, currentTime);

            if (usageStatsList != null && !usageStatsList.isEmpty()) {
                // Find the app with the most recent usage
                UsageStats recentUsage = null;
                for (UsageStats usageStats : usageStatsList) {
                    if (recentUsage == null || usageStats.getLastTimeUsed() > recentUsage.getLastTimeUsed()) {
                        recentUsage = usageStats;
                    }
                }

                if (recentUsage != null) {
                    // Get the package name of the foreground app
                    foregroundApp = recentUsage.getPackageName();
                }
            } else {
                System.out.println("USAGESTATSLIST ESCH LEER");
                foregroundApp = lastOpenedApp;      // if the most used app is "null" set it to the one opened before
            }
        } else {
            System.out.println("usageStatsManager esch null");
        }

        lastOpenedApp = foregroundApp;          // set the lastOpenedApp to the one currently most used
        System.out.println(foregroundApp);
        return foregroundApp;
    }

}
