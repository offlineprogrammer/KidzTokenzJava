package com.offlineprogrammer.kidztokenz;

import java.util.ArrayList;

class User {
    private String deviceToken;
    private String firebaseId;
    private String userId;
    private ArrayList<Kid> kidz;

    User(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    User() {

    }

    String getFirebaseId() {
        return firebaseId;
    }

    void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    String getDeviceToken() {
        return deviceToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Kid> getKidz() {
        return kidz;
    }

    public void setKidz(ArrayList<Kid> kidz) {
        this.kidz = kidz;
    }
}
