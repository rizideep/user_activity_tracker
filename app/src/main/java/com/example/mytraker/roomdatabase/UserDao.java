package com.example.mytraker.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    Completable insert(MyLocation myLocation);  // Return Completable for insert operation

    @Delete
    Completable delete(MyLocation myLocation);  // Return Completable for delete operation

    @Query("SELECT * FROM user_table ORDER BY mTimeMs ASC")
    Single<List<MyLocation>> getAllUsers(); // Fetches data once
}
