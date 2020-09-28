package com.offlineprogrammer.KidzTokenz;

import android.app.Application;

public class KidzTokenz extends Application {
    private User m_User;

    @Override
    public void onCreate() {
        super.onCreate();
    }



    public User getUser() {
        return m_User;
    }

    public void setUser(User user) {
        this.m_User = user;
    }



}
