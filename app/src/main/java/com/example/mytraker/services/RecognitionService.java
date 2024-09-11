package com.example.mytraker.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mytraker.R;
import com.example.mytraker.customre.MyCustomReceiver;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnSuccessListener;

public class RecognitionService extends Service {

    private static final String TAG = "ActivityRecognition";
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent pendingIntent;
    private static final String CHANNEL_ID = "ActivityTrackingServiceChannel";

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        activityRecognitionClient = ActivityRecognition.getClient(this);
    }


    private void startActivityRecognition() {
        Intent intent = new Intent(this, MyCustomReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            return;
        }

        activityRecognitionClient.requestActivityUpdates(3000, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d(TAG, "Successfully started activity recognition updates");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to start activity recognition updates", e));
    }

    private void stopActivityRecognition() {
        if (pendingIntent != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            activityRecognitionClient.removeActivityUpdates(pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Log.d(TAG, "Successfully stopped activity recognition updates");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to stop activity recognition updates", e));
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Recognitions Start", Toast.LENGTH_SHORT).show();
        startActivityRecognition();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Activity tracking service")
                .setContentText("Retrieving user activity...")
                .setSmallIcon(R.mipmap.ic_launcher) // Replace with your own icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(5, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        } else {
     // For Android versions below 10, use the traditional startForeground() method without the flag
            startForeground(5, notification);
        }

        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopActivityRecognition();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
