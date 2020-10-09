package com.offlineprogrammer.KidzTokenz;

import android.app.Application;

import timber.log.Timber;

public class KidzTokenz extends Application {
    private User m_User;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }



    public User getUser() {
        return m_User;
    }

    public void setUser(User user) {
        this.m_User = user;
    }



}
