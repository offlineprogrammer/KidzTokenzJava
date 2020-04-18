package com.offlineprogrammer.KidzTokenz.task;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KidTask implements Parcelable {
    private String taskName;
    private int taskImage;
    private Date createdDate;
    private String firestoreId;
    private String kidFirestoreId;
    private Boolean negativeReTask = false;
    private ArrayList<Long> taskTokenzScore = new ArrayList<>();
    private String firestoreImageUri;

    public KidTask(String taskName,
                   int taskImage,
                   Date createdDate,
                   Boolean negativeReTask) {
        this.taskName = taskName;
        this.taskImage =taskImage;
        this.createdDate=createdDate;
        this.negativeReTask = negativeReTask;

    }

    public KidTask() {

    }

    protected KidTask(Parcel in) {
        taskName = in.readString();
        taskImage = in.readInt();
        firestoreId = in.readString();
        kidFirestoreId = in.readString();
        negativeReTask = in.readByte() != 0;
        in.readList(taskTokenzScore, Long.class.getClassLoader());

    }

    public static final Creator<KidTask> CREATOR = new Creator<KidTask>() {
        @Override
        public KidTask createFromParcel(Parcel in) {
            return new KidTask(in);
        }

        @Override
        public KidTask[] newArray(int size) {
            return new KidTask[size];
        }
    };

    @NonNull
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NonNull final String taskName) {
        this.taskName = taskName;
    }

    public int getTaskImage() {
        return taskImage;
    }

    public void setTaskImage(int taskImage) {
        this.taskImage = taskImage;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("taskName", this.taskName);
        result.put("taskImage", this.taskImage);
        result.put("createdDate", this.createdDate);
        result.put("firestoreId", this.firestoreId);
        result.put("kidFirestoreId", this.kidFirestoreId);
        result.put("negativeReTask", this.negativeReTask);
        result.put("taskTokenzScore", this.taskTokenzScore);
        result.put("firestoreImageUri", this.firestoreImageUri);
        return result;
    }

    @Override
    public String toString() {
        return "KidTask{" +
                "firestoreId='" + firestoreId + '\'' +
                ", kidFirestoreId='" + kidFirestoreId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", taskImage='" + taskImage + '\'' +
                ", taskTokenzScore='" + taskTokenzScore + '\'' +
                ", negativeReTask='" + (negativeReTask ? 1 : 0) + '\'' +
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
        dest.writeString(taskName);
        dest.writeInt(taskImage);
        dest.writeString(firestoreId);
        dest.writeString(kidFirestoreId);
        dest.writeByte((byte) (negativeReTask ? 1 : 0));
        dest.writeList(taskTokenzScore);
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getKidFirestoreId() {
        return kidFirestoreId;
    }

    public void setKidFirestoreId(String kidFirestoreId) {
        this.kidFirestoreId = kidFirestoreId;
    }

    public Boolean getNegativeReTask() {
        return negativeReTask;
    }

    public void setNegativeReTask(Boolean negativeReTask) {
        this.negativeReTask = negativeReTask;
    }

    public ArrayList<Long> getTaskTokenzScore() {
        return taskTokenzScore;
    }

    public void setTaskTokenzScore(ArrayList<Long> taskTokenzScore) {
        this.taskTokenzScore = taskTokenzScore;
    }


    public String getFirestoreImageUri() {
        return firestoreImageUri;
    }

    public void setFirestoreImageUri(String firestoreImageUri) {
        this.firestoreImageUri = firestoreImageUri;
    }
}
