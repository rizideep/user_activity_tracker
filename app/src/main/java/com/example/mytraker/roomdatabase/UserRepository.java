package com.example.mytraker.roomdatabase;

import android.app.Application;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

public class UserRepository {

    private UserDao userDao;
    private CompositeDisposable disposables = new CompositeDisposable();  // For managing disposables

    public UserRepository(Application application) {
        UserDatabase database = UserDatabase.getInstance(application);
        userDao = database.userDao();
    }

    public void insert(MyLocation myLocation) {
        // Use Completable and schedule the operation on the IO thread
        Completable completable = userDao.insert(myLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        disposables.add(completable.subscribe(() -> {
            // Handle success
        }, throwable -> {
            // Handle error
        }));
    }

    public void delete(MyLocation myLocation) {
        // Same as insert, using Completable for delete
        Completable completable = userDao.delete(myLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        disposables.add(completable.subscribe(() -> {
            // Handle success
        }, throwable -> {
            // Handle error
        }));
    }


    // Delete all locations
    public Completable deleteAllLocations() {
        return userDao.deleteAllLocations();
    }


    // Method to fetch all locations
    public Single<List<MyLocation>> getAllLocations() {
        return userDao.getAllLocations();
    }


    // Call this method to clear all disposables when the repository is no longer in use
    public void clearDisposables() {
        disposables.clear();
    }
}

