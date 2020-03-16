package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

class Kid {
    private String kidName;
    private int monsterImage;

    public Kid(String kidName, int monsterImage) {
        this.kidName = kidName;
        this.monsterImage=monsterImage;
    }

    public Kid() {

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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("kidName", this.kidName);
        result.put("monsterImage", this.monsterImage);

        return result;
    }

}
