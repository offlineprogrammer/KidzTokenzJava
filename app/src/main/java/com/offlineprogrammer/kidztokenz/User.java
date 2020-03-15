package com.offlineprogrammer.kidztokenz;

class User {
    private String deviceToken;
    private String firebaseId;

    User(String deviceToken) {
        this.deviceToken = deviceToken;
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
}
