package com.offlineprogrammer.kidztokenz.taskTokenz;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskTokenz implements Parcelable {
    private int taskTokenzImage;
    private Boolean isRewarded;

    public TaskTokenz(int taskTokenzImage,
                   Boolean isRewarded) {
        this.taskTokenzImage =taskTokenzImage;
        this.isRewarded = isRewarded;

    }

    public TaskTokenz() {

    }

    protected TaskTokenz(Parcel in) {
        taskTokenzImage = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isRewarded = in.readBoolean();
        }

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
        result.put("isRewarded", this.isRewarded);
        return result;
    }

    @Override
    public String toString() {
        return "TaskTokenz{" +
                " isRewarded='" + isRewarded.toString() + '\'' +
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isRewarded);
        }
    }





    public Boolean getIsRewarded() {
        return isRewarded;
    }

    public void setIsRewarded(Boolean negativeReTask) {
        isRewarded = negativeReTask;
    }
}
