package com.example.mytraker.services;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.content.Context;
import android.net.Uri;

public class BatteryOptimizationHelper {

    public static void requestDisableBatteryOptimization(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            if (!isIgnoringBatteryOptimizations(context)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

    public static boolean isIgnoringBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            return ((android.os.PowerManager) context.getSystemService(Context.POWER_SERVICE))
                    .isIgnoringBatteryOptimizations(packageName);
        }
        return true;  // Assume optimizations are ignored on pre-Marshmallow devices.
    }
}

