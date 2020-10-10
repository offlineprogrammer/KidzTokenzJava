package com.offlineprogrammer.KidzTokenz;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;
import com.offlineprogrammer.KidzTokenz.kid.Kid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Keep
class User {
    private String deviceToken;
    private String firebaseId;
    private String userId;
    private Date dateCreated;
    private String userEmail;
    private ArrayList<Kid> kidz;
    private String fcmInstanceId;

    User(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    User(String userId, String userEmail, Date dateCreated ) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.dateCreated=dateCreated;

    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", this.userId);
        result.put("userEmail", this.userEmail);
        result.put("dateCreated", this.dateCreated);
        result.put("fcmInstanceId", this.fcmInstanceId);
        result.put("kidz", this.kidz);
        return result;
    }


    User() {

    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ArrayList<Kid> getKidz() {
        return kidz;
    }

    public void setKidz(ArrayList<Kid> kidz) {
        this.kidz = kidz;
    }

    public String getFcmInstanceId() {
        return fcmInstanceId;
    }

    public void setFcmInstanceId(String fcmInstanceId) {
        this.fcmInstanceId = fcmInstanceId;
    }
}
