package com.offlineprogrammer.KidzTokenz.taskTokenz;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class TaskTokenz implements Parcelable {
    private int taskTokenzImage;
    private Boolean rewarded;

    public TaskTokenz(int taskTokenzImage,
                   Boolean rewarded) {
        this.taskTokenzImage =taskTokenzImage;
        this.rewarded = rewarded;

    }

    public TaskTokenz() {

    }

    protected TaskTokenz(Parcel in) {
        taskTokenzImage = in.readInt();
        rewarded = in.readByte() != 0;

    }

    public static final Creator<TaskTokenz> CREATOR = new Creator<TaskTokenz>() {
        @Override
        public TaskTokenz createFromParcel(Parcel in) {
            return new TaskTokenz(in);
        }

        @Override
        public TaskTokenz[] newArray(int size) {
            return new TaskTokenz[size];
        }
    };

    public int getTaskTokenzImage() {
        return taskTokenzImage;
    }

    public void setTaskTokenzImage(int taskTokenzImage) {
        this.taskTokenzImage = taskTokenzImage;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("taskTokenzImage", this.taskTokenzImage);
        result.put("rewarded", this.rewarded);
        return result;
    }

    @Override
    public String toString() {
        return "TaskTokenz{" +
                " rewarded='" + rewarded.toString() + '\'' +
                ", taskTokenzImage='" + taskTokenzImage + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(taskTokenzImage);
        dest.writeByte((byte) (rewarded ? 1 : 0));
    }





    public Boolean getRewarded() {
        return rewarded;
    }

    public void setRewarded(Boolean negativeReTask) {
        rewarded = negativeReTask;
    }
}
