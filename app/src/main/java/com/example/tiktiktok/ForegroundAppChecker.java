package com.example.tiktiktok;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import java.util.List;

public class ForegroundAppChecker {

    public static String getForegroundApp(Context context) {
        String foregroundApp = null;

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
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
            }
        }
        System.out.println(foregroundApp);
        return foregroundApp;
    }
}
