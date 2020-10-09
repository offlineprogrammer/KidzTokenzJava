package com.offlineprogrammer.KidzTokenz.kid;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.offlineprogrammer.KidzTokenz.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Keep
public class Kid  implements Parcelable {
    private String kidName;
    private int monsterImage;
    private String monsterImageResourceName;
    private Date createdDate;
    private int tokenImage;
    private String tokenImageResourceName;
    private int badTokenImage;
    private String badTokenImageResourceName;
    private int tokenNumberImage;
    private String tokenNumberImageResourceName;
    private String firestoreId;
    private String userFirestoreId;
    private int tokenNumber;
    private String kidUUID;
    private String kidSchema = Constants.V1SCHEMA;

    public Kid(String kidName,
               int monsterImage,
               String monsterImageResourceName,
               Date createdDate,
               int tokenImage,
               String tokenImageResourceName,
               int badTokenImage,
               String badTokenImageResourceName,
               int tokenNumberImage,
               String tokenNumberImageResourceName,
               int tokenNumber) {
        this.kidName = kidName;
        this.monsterImage = monsterImage;
        this.monsterImageResourceName = monsterImageResourceName;
        this.createdDate = createdDate;
        this.tokenImage = tokenImage;
        this.tokenImageResourceName = tokenImageResourceName;
        this.badTokenImage = badTokenImage;
        this.badTokenImageResourceName = badTokenImageResourceName;
        this.tokenNumberImage = tokenNumberImage;
        this.tokenNumberImageResourceName = tokenNumberImageResourceName;
        this.tokenNumber = tokenNumber;
        this.kidUUID = UUID.randomUUID().toString();
        this.kidSchema = Constants.V2SCHEMA;

    }

    public Kid() {

    }

    protected Kid(Parcel in) {
        kidName = in.readString();
        monsterImageResourceName = in.readString();
        tokenImageResourceName = in.readString();
        badTokenImageResourceName = in.readString();
        tokenNumberImageResourceName = in.readString();
        tokenNumber = in.readInt();
        kidUUID = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kidName);
        dest.writeString(monsterImageResourceName);
        dest.writeString(tokenImageResourceName);
        dest.writeString(badTokenImageResourceName);
        dest.writeString(tokenNumberImageResourceName);
        dest.writeInt(tokenNumber);
        dest.writeString(kidUUID);

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
        result.put("kidUUID", this.kidUUID);
        result.put("createdDate", this.createdDate);
        result.put("tokenImageResourceName", this.tokenImageResourceName);
        result.put("badTokenImageResourceName", this.badTokenImageResourceName);
        result.put("tokenNumberImageResourceName", this.tokenNumberImageResourceName);
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

    public String getKidUUID() {
        return kidUUID;
    }

    public void setKidUUID() {
        this.kidUUID = UUID.randomUUID().toString();
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

    public String getMonsterImageResourceName() {
        return monsterImageResourceName;
    }

    public void setMonsterImageResourceName(String monsterImageResourceName) {
        this.monsterImageResourceName = monsterImageResourceName;
    }

    public String getTokenImageResourceName() {
        return tokenImageResourceName;
    }

    public void setTokenImageResourceName(String tokenImageResourceName) {
        this.tokenImageResourceName = tokenImageResourceName;
    }

    public String getBadTokenImageResourceName() {
        return badTokenImageResourceName;
    }

    public void setBadTokenImageResourceName(String badTokenImageResourceName) {
        this.badTokenImageResourceName = badTokenImageResourceName;
    }

    public String getTokenNumberImageResourceName() {
        return tokenNumberImageResourceName;
    }

    public void setTokenNumberImageResourceName(String tokenNumberImageResourceName) {
        this.tokenNumberImageResourceName = tokenNumberImageResourceName;
    }

    public String getKidSchema() {
        return kidSchema;
    }

    public void setKidSchema(String kidSchema) {
        this.kidSchema = kidSchema;
    }
}
