package com.example.mytraker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mytraker.roomdatabase.UserViewModel;
import com.example.mytraker.services.RecognitionService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {


    private UserViewModel userViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        // Example of deleting all locations
        Disposable deleteDisposable = userViewModel.deleteAllLocations()
                .subscribeOn(Schedulers.io())  // Perform the delete operation on the I/O thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Toast.makeText(this, "All location deleted", Toast.LENGTH_SHORT).show(),
                        // onComplete
                        throwable -> Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()   // onError
                );

        compositeDisposable.add(deleteDisposable);

        List<String> requiredPermissions = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissions.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }


        // Request all missing permissions at once
        if (!requiredPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, requiredPermissions.toArray(new String[0]), 100);
        } else {
            // All permissions are already granted, proceed to start the service
            startActivityRecognitionService();
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the service
                startActivityRecognitionService();
            } else {
                // Permission denied, handle as appropriate
                Log.e("MainActivity", "Activity Recognition permission denied");
                Toast.makeText(this, "Plz grant all permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startActivityRecognitionService() {
        Intent intent = new Intent(this, RecognitionService.class);
        ContextCompat.startForegroundService(this, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();  // Clean up disposables to avoid memory leaks
    }

}