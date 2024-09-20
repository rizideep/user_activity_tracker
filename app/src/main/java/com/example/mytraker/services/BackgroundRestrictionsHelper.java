package com.example.mytraker.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class BackgroundRestrictionsHelper {

    public static void checkAndRequestBackgroundRestrictions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Android 9.0 (API 28) and above
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
            Toast.makeText(context, "Please remove background restrictions for better performance.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "No background restrictions on this version.", Toast.LENGTH_SHORT).show();
        }
    }
}

