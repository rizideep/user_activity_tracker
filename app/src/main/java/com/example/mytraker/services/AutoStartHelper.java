package com.example.mytraker.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AutoStartHelper {


    private static final String VIVO_AUTO_START_INTENT_ACTION = "com.iqoo.secure";

    public static void openVivoAutoStartSettings(Context context) {
        try {
            Intent intent = new Intent();
            Intent fallbackIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            context.startActivity(fallbackIntent);
        } catch (Exception e) {
            Log.e("AutoStartHelper", "Failed to open Auto-Start settings: " + e.getMessage());
            // Fallback: Take user to App Info settings if Vivo auto-start activity is not found
            Intent fallbackIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            context.startActivity(fallbackIntent);
        }
    }
}
