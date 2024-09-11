package com.example.mytraker.customre;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.mytraker.services.TrackingService;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "ActivityRecognition";
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
       // Initialize variables to track the activity with the highest probability
        DetectedActivity highestProbabilityActivity = null;
        int highestProbability = 0;

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            List<DetectedActivity> detectedActivities = result.getProbableActivities();
            for (DetectedActivity activity : detectedActivities) {
                String activityType = getActivityType(activity.getType());
                Log.d(TAG, "Detected activity: " + activityType + " with confidence: " + activity.getConfidence());
                int confidence = activity.getConfidence();
                if (confidence > highestProbability) {
                    highestProbability = confidence;
                    highestProbabilityActivity = activity;
                }
            }

            // Check if the activity with the highest probability is not STILL
            if (highestProbabilityActivity != null) {
                String highestActivityType = getActivityType(highestProbabilityActivity.getType());
                if (!highestActivityType.equalsIgnoreCase("Still")) {
                    Toast.makeText(mContext, "Activity: " + highestActivityType + " confidence: " + highestProbability,
                            Toast.LENGTH_SHORT).show();
                    startTrackingService(mContext);
                }
            }
        }
    }

    private String getActivityType(int activityType) {
        switch (activityType) {
            case DetectedActivity.STILL:
                return "Still";
            case DetectedActivity.WALKING:
                return "Walking";
            case DetectedActivity.RUNNING:
                return "Running";
            case DetectedActivity.ON_BICYCLE:
                return "On Bicycle";
            case DetectedActivity.IN_VEHICLE:
                return "In Vehicle";
            case DetectedActivity.ON_FOOT:
                return "On Foot";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
            default:
                return "Unknown default";
        }
    }


    @SuppressLint("NewApi")
    private void startTrackingService(Context context) {
        Intent serviceIntent = new Intent(context, TrackingService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}