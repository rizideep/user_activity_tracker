package com.example.mytraker.roomdatabase;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

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

    public Single<List<MyLocation>> getAllLocatins() {
        // Return the Flowable directly to the UI
        return repository.getAllUsers();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Dispose of any active subscriptions
        disposables.clear();
    }


    public void calculateTotalCoveredDistance() {


        disposables.add(getAllLocatins()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    // Convert List<User> to ArrayList<User>
                    List<MyLocation> myLocationList = new ArrayList<>();
                    myLocationList.clear();
                    myLocationList.addAll(users);

                    if (!myLocationList.isEmpty()) {
                        Location fristlocation = new Location("provider");
                        fristlocation.setLatitude(myLocationList.get(0).getmLatitudeDegrees());
                        fristlocation.setLongitude(myLocationList.get(0).getmLatitudeDegrees());
                        Location lastlocation = new Location("provider");
                        fristlocation.setLatitude(myLocationList.get(myLocationList.size()-1).getmLatitudeDegrees());
                        fristlocation.setLongitude(myLocationList.get(myLocationList.size()-1).getmLatitudeDegrees());
                        String distance = String.valueOf(fristlocation.distanceTo(lastlocation));
                        totalCoveredDistance.setValue(distance);
                    }


                }, throwable -> {
                    // Handle error
                }));


    }

}

