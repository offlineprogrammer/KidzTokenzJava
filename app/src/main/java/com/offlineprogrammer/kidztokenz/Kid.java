package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;

public class Kid {
    private String kidName;

    public Kid(String kidName) {
        this.kidName = kidName;
    }

    @NonNull
    public String getKidName() {
        return kidName;
    }

    public void setKidName(@NonNull final String kidName) {
        this.kidName = kidName;
    }
}
