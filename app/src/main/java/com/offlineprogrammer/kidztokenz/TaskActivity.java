package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.offlineprogrammer.kidztokenz.task.KidTask;

public class TaskActivity extends AppCompatActivity {

    ImageView taskImageView;
    TextView taskNameTextView;
    KidTask selectedTask;

    private static final String TAG = "TaskActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskImageView = findViewById(R.id.taskImage);
        taskNameTextView = findViewById(R.id.taskName);

        if (getIntent().hasExtra("selected_task")) {
            Bundle data = getIntent().getExtras();
            selectedTask = data.getParcelable("selected_task");
            Log.i(TAG, "onCreate: " + selectedTask.toString());
            Log.i(TAG, "onCreate: " + selectedTask.getTaskName());

            taskNameTextView.setText(selectedTask.getTaskName());
        }
    }
}
