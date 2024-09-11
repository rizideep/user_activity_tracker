package com.example.mytraker.roomdatabase;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private CompositeDisposable disposables = new CompositeDisposable();// Manage subscriptions

    public MutableLiveData<String> totalCoveredDistance = new MutableLiveData<>();


    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);

    }

    public void insert(MyLocation myLocation) {
        repository.insert(myLocation);
    }

    public void delete(MyLocation myLocation) {
        repository.delete(myLocation);
    }



    // This method fetches all locations from the repository
    public Single<List<MyLocation>> getAllLocations() {
        return repository.getAllLocations()
                .subscribeOn(Schedulers.io());  // Perform the operation on the I/O thread
    }


    // Delete all locations
    public Completable deleteAllLocations() {
        return repository.deleteAllLocations();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        // Dispose of any active subscriptions
        disposables.clear();
    }




}

