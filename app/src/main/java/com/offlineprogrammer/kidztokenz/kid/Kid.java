package com.offlineprogrammer.kidztokenz.kid;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Kid  implements Parcelable {
    private String kidName;
    private int monsterImage;
    private Date createdDate;
    private int tokenImage;
    private int badTokenImage;
    private int tokenNumberImage;
    private String firestoreId;
    private String userFirestoreId;
    private int tokenNumber;

    public Kid(String kidName,
               int monsterImage,
               Date createdDate,
               int tokenImage,
               int badTokenImage,
               int tokenNumberImage,
               int tokenNumber) {
        this.kidName = kidName;
        this.monsterImage=monsterImage;
        this.createdDate=createdDate;
        this.tokenImage=tokenImage;
        this.badTokenImage=badTokenImage;
        this.tokenNumberImage =tokenNumberImage;
        this.tokenNumber =tokenNumber;
    }

    public Kid() {

    }

    protected Kid(Parcel in) {
        kidName = in.readString();
        monsterImage = in.readInt();
        tokenImage=in.readInt();
        badTokenImage=in.readInt();
        firestoreId = in.readString();
        userFirestoreId = in.readString();
        tokenNumberImage =in.readInt();
        tokenNumber =in.readInt();

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
        result.put("badTokenImage", this.badTokenImage);
        result.put("firestoreId", this.firestoreId);
        result.put("userFirestoreId", this.userFirestoreId);
        result.put("tokenNumberImage", this.tokenNumberImage);
        result.put("tokenNumber", this.tokenNumber);

        return result;
    }

    @Override
    public String toString() {
        return "Kid{" +
                "firestoreId='" + firestoreId + '\'' +
                ", userFirestoreId='" + userFirestoreId + '\'' +
                ", kidName='" + kidName + '\'' +
                ", monsterImage='" + monsterImage + '\'' +
                ", tokenImage='" + tokenImage + '\'' +
                ", badTokenImage='" + badTokenImage + '\'' +
                ", tokenNumberImage='" + tokenNumberImage + '\'' +
                ", tokenNumber='" + tokenNumber + '\'' +
                '}';
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
        dest.writeInt(badTokenImage);
        dest.writeString(firestoreId);
        dest.writeString(userFirestoreId);
        dest.writeInt(tokenNumberImage);
        dest.writeInt(tokenNumber);
    }

    public int getTokenImage() {
        return tokenImage;
    }

    public void setTokenImage(int tokenImage) {
        this.tokenImage = tokenImage;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getUserFirestoreId() {
        return userFirestoreId;
    }

    public void setUserFirestoreId(String userFirestoreId) {
        this.userFirestoreId = userFirestoreId;
    }

    public int getTokenNumberImage() {
        return tokenNumberImage;
    }

    public void setTokenNumberImage(int tokenNumberImage) {
        this.tokenNumberImage = tokenNumberImage;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public int getBadTokenImage() {
        return badTokenImage;
    }

    public void setBadTokenImage(int badTokenImage) {
        this.badTokenImage = badTokenImage;
    }
}
