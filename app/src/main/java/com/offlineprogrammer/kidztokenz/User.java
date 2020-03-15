package com.offlineprogrammer.kidztokenz;

class User {
    private String deviceToken;
    private String firebaseId;
    private String userId;

    User(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    User() {

    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
