package com.example.mytraker.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mytraker.R;
import com.example.mytraker.roomdatabase.MyLocation;
import com.example.mytraker.roomdatabase.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class TrackingService extends Service {

    private static final String CHANNEL_ID = "LocationTrackingServiceChannel";
    private static final String TAG = "LocationTrackingService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public static final String IS_SERVICE_RUNNING_KEY = "isServiceRunning";

    private SharedPreferences sharedPreferences;

    private UserViewModel userViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        userViewModel = new UserViewModel(getApplication());
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);  // 10 seconds
        locationRequest.setFastestInterval(5000);  // 5 seconds

        // Initialize the LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "Location is null.");
                    Toast.makeText(TrackingService.this, "Location stopped", Toast.LENGTH_SHORT).show();
                    return;
                }
                MyLocation myLocation = getMyLocation(locationResult);
                userViewModel.insert(myLocation);
                Toast.makeText(TrackingService.this, "Location Updating Continuously", Toast.LENGTH_SHORT).show();
                // Subscribe to the observable from ViewModel
                Disposable disposable = userViewModel.getAllLocations()
                        .observeOn(AndroidSchedulers.mainThread())  // Observe on the main thread
                        .subscribe(
                                locationsList -> {
                                    // Convert List<User> to ArrayList<User>
                                    List<MyLocation> myLocationList = new ArrayList<>();
                                    myLocationList.clear();
                                    myLocationList.addAll(locationsList);
                                    if (!myLocationList.isEmpty()) {
                                        Location fristlocation = new Location("provider");
                                        fristlocation.setLatitude(myLocationList.get(0).getmLatitudeDegrees());
                                        fristlocation.setLongitude(myLocationList.get(0).getmLatitudeDegrees());
                                        Location lastlocation = new Location("provider");
                                        fristlocation.setLatitude(myLocationList.get(myLocationList.size() - 1).getmLatitudeDegrees());
                                        fristlocation.setLongitude(myLocationList.get(myLocationList.size() - 1).getmLatitudeDegrees());
                                        String distance = String.valueOf(fristlocation.distanceTo(lastlocation));
                                        Toast.makeText(TrackingService.this, "Total distance: " + distance,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                },  // OnNext
                                throwable -> {
                                    Toast.makeText(TrackingService.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }// OnError
                                // OnComplete
                        );

                // Add the disposable to CompositeDisposable to manage it
                compositeDisposable.add(disposable);

            }
        };
        // Request location updates
        startLocationUpdates();
    }

    private static @NonNull MyLocation getMyLocation(LocationResult locationResult) {
        MyLocation myLocation = new MyLocation(locationResult.getLastLocation().getTime(),
                locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), locationResult.getLastLocation().getSpeed());


        return myLocation;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking Service")
                .setContentText("Retrieving current location...")
                .setSmallIcon(R.mipmap.ic_launcher)  // Replace with your own icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        Log.d(TAG, "isServiceRunning." + isServiceRunning());
        sharedPreferences.edit().putBoolean(IS_SERVICE_RUNNING_KEY, true).apply();
        startForeground(1, notification);
        if (isServiceRunning()) {
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().putBoolean(IS_SERVICE_RUNNING_KEY, false).apply();
        stopLocationUpdates();  // Stop updates when service is destroyed
        compositeDisposable.clear();
    }

    private boolean isServiceRunning() {
        return sharedPreferences.getBoolean(IS_SERVICE_RUNNING_KEY, false);
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

