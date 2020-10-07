package com.offlineprogrammer.KidzTokenz.task;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KidTask implements Parcelable {
    private String taskName;
    private int taskImage;
    private String taskImageResourceName;
    private Date createdDate;
    private String firestoreId;
    private String kidFirestoreId;
    private Boolean negativeReTask = false;
    private ArrayList<Long> taskTokenzScore = new ArrayList<>();
    private String firestoreImageUri;
    private String kidTaskUUID;

    public KidTask(String taskName,
                   int taskImage,
                   String taskImageResourceName,
                   Date createdDate,
                   Boolean negativeReTask) {
        this.taskName = taskName;
        this.taskImage =taskImage;
        this.taskImageResourceName = taskImageResourceName;
        this.createdDate = createdDate;
        this.negativeReTask = negativeReTask;
        this.kidTaskUUID = UUID.randomUUID().toString();

    }

    public KidTask() {

    }

    protected KidTask(Parcel in) {
        taskName = in.readString();
        taskImage = in.readInt();
        taskImageResourceName = in.readString();
        firestoreId = in.readString();
        kidFirestoreId = in.readString();
        firestoreImageUri = in.readString();
        negativeReTask = in.readByte() != 0;
        in.readList(taskTokenzScore, Long.class.getClassLoader());
        kidTaskUUID = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskName);
        dest.writeInt(taskImage);
        dest.writeString(taskImageResourceName);
        dest.writeString(firestoreId);
        dest.writeString(kidFirestoreId);
        dest.writeString(firestoreImageUri);
        dest.writeByte((byte) (negativeReTask ? 1 : 0));
        dest.writeList(taskTokenzScore);
        dest.writeString(kidTaskUUID);
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
        result.put("kidTaskUUID", this.kidTaskUUID);
        result.put("taskImageResourceName", this.taskImageResourceName);
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
                ", kidTaskUUID='" + kidTaskUUID + '\'' +
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


    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getKidTaskUUID() {
        return kidTaskUUID;
    }

    public void setKidTaskUUID() {
        this.kidTaskUUID = UUID.randomUUID().toString();
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

    public String getTaskImageResourceName() {
        return taskImageResourceName;
    }

    public void setTaskImageResourceName(String taskImageResourceName) {
        this.taskImageResourceName = taskImageResourceName;
    }
}
