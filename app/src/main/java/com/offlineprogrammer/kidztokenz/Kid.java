package com.offlineprogrammer.kidztokenz;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Kid  implements Parcelable {
    private String kidName;
    private int monsterImage;
    private Date createdDate;
    private int tokenImage;

    public Kid(String kidName,
               int monsterImage,
               Date createdDate,
               int tokenImage) {
        this.kidName = kidName;
        this.monsterImage=monsterImage;
        this.createdDate=createdDate;
        this.tokenImage=tokenImage;
    }

    public Kid() {

    }

    protected Kid(Parcel in) {
        kidName = in.readString();
        monsterImage = in.readInt();
        tokenImage=in.readInt();
    }

    public static final Creator<Kid> CREATOR = new Creator<Kid>() {
        @Override
        public Kid createFromParcel(Parcel in) {
            return new Kid(in);
        }

        @Override
        public Kid[] newArray(int size) {
            return new Kid[size];
        }
    };

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
        result.put("createdDate", this.createdDate);
        result.put("tokenImage", this.tokenImage);

        return result;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kidName);
        dest.writeInt(monsterImage);
        dest.writeInt(tokenImage);
    }

    public int getTokenImage() {
        return tokenImage;
    }

    public void setTokenImage(int tokenImage) {
        this.tokenImage = tokenImage;
    }
}
