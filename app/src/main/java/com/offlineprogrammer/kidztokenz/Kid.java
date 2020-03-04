package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;

public class Kid {
    private String kidName;
    private int monsterImage;

    public Kid(String kidName, int monsterImage) {
        this.kidName = kidName;
        this.monsterImage=monsterImage;
    }

    @NonNull
    public String getKidName() {
        return kidName;
    }

    public void setKidName(@NonNull final String kidName) {
        this.kidName = kidName;
    }

    public int getMonsterImage() {
        return monsterImage;
    }

    public void setMonsterImage(int monsterImage) {
        this.monsterImage = monsterImage;
    }
}
