package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.offlineprogrammer.kidztokenz.kid.Kid;
import com.offlineprogrammer.kidztokenz.task.KidTask;
import com.offlineprogrammer.kidztokenz.task.TaskAdapter;
import com.offlineprogrammer.kidztokenz.task.TaskGridItemDecoration;
import com.offlineprogrammer.kidztokenz.taskTokenz.OnTaskTokenzListener;
import com.offlineprogrammer.kidztokenz.taskTokenz.TaskTokenz;
import com.offlineprogrammer.kidztokenz.taskTokenz.TaskTokenzAdapter;
import com.offlineprogrammer.kidztokenz.taskTokenz.TaskTokenzGridItemDecoration;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements OnTaskTokenzListener {

    ImageView taskImageView;
    TextView taskNameTextView;
    KidTask selectedTask;
    Kid selectedKid;



    private static final String TAG = "TaskActivity";

    private ArrayList<TaskTokenz> taskTokenzList = new ArrayList<>();

    private RecyclerView taskTokenzRecyclerView;
    private TaskTokenzAdapter taskTokenzAdapter;


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
            taskImageView.setImageResource(selectedTask.getTaskImage());
            taskNameTextView.setText(selectedTask.getTaskName());
        }

        if (getIntent().hasExtra("selected_kid")) {
            Bundle data = getIntent().getExtras();
            selectedKid = data.getParcelable("selected_kid");

        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        taskTokenzAdapter = new TaskTokenzAdapter(taskTokenzList, this);
        taskTokenzRecyclerView = findViewById(R.id.taskz_recyclerview);
        taskTokenzRecyclerView.setHasFixedSize(true);
        taskTokenzRecyclerView.setAdapter(taskTokenzAdapter);
        taskTokenzRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        taskTokenzRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing_small);
        taskTokenzRecyclerView.addItemDecoration(new TaskTokenzGridItemDecoration(largePadding, smallPadding));

    }

    @Override
    public void onTaskTokenzClick(int position) {

    }
}
