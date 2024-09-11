package com.example.mytraker.roomdatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class MyLocation {
    @PrimaryKey(autoGenerate = true)
    int id;
    long mTimeMs;
    double mLatitudeDegrees;
    double mLongitudeDegrees;
    float mSpeedMetersPerSecond;

    public MyLocation(long mTimeMs, double mLatitudeDegrees, double mLongitudeDegrees, float mSpeedMetersPerSecond) {
        this.mTimeMs = mTimeMs;
        this.mLatitudeDegrees = mLatitudeDegrees;
        this.mLongitudeDegrees = mLongitudeDegrees;
        this.mSpeedMetersPerSecond = mSpeedMetersPerSecond;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getmTimeMs() {
        return mTimeMs;
    }

    public void setmTimeMs(long mTimeMs) {
        this.mTimeMs = mTimeMs;
    }

    public double getmLatitudeDegrees() {
        return mLatitudeDegrees;
    }

    public void setmLatitudeDegrees(double mLatitudeDegrees) {
        this.mLatitudeDegrees = mLatitudeDegrees;
    }

    public double getmLongitudeDegrees() {
        return mLongitudeDegrees;
    }

    public void setmLongitudeDegrees(double mLongitudeDegrees) {
        this.mLongitudeDegrees = mLongitudeDegrees;
    }

    public float getmSpeedMetersPerSecond() {
        return mSpeedMetersPerSecond;
    }

    public void setmSpeedMetersPerSecond(float mSpeedMetersPerSecond) {
        this.mSpeedMetersPerSecond = mSpeedMetersPerSecond;
    }
}

