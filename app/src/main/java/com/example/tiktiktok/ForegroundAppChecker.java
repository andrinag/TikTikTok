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
 *
 * The main idea and some of the structure of this class was done with ChatGPT but were
 * modified and expanded by us
 */
public class ForegroundAppChecker {

    static String lastOpenedApp = null; // used in case the most used app is "null"
    static UsageStatsManager usageStatsManager;

    /**
     * used to set the UsageStatsManager (used in the getForegroundApp() method) when the
     * app first starts up
     * should be done in oncreate() in MainActivity.java
     * @param context "this" can be used as a context in MainActivity.java
     */
    public static void createUsageStatsManager(Context context) {
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    /**
     * checks which app has been used last the past 5 seconds
     * @return String of the name of the app which has been used last
     */
    public static String getForegroundApp() {
        String foregroundApp = null;

        if (usageStatsManager != null) {
            long currentTime = System.currentTimeMillis();

            // Get the usage stats of the last 5 seconds
            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats
                    (UsageStatsManager.INTERVAL_BEST, currentTime - 5000, currentTime);

            if (usageStatsList != null && !usageStatsList.isEmpty()) {
                // Find the app with the most recent usage
                UsageStats recentUsage = null;
                for (UsageStats usageStats : usageStatsList) {
                    if (recentUsage == null ||
                            usageStats.getLastTimeUsed() > recentUsage.getLastTimeUsed()) {
                        recentUsage = usageStats;
                    }
                }

                if (recentUsage != null) {
                    // save the package name of the app last recently accessed
                    foregroundApp = recentUsage.getPackageName();
                }
            } else {
                // if the last accessed app is "null" set it to the one from 5 seconds ago
                foregroundApp = lastOpenedApp;
            }
        }

        lastOpenedApp = foregroundApp;      // set the lastOpenedApp to the one currently last used
        return foregroundApp;
    }

}
