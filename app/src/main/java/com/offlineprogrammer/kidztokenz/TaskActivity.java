package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private ArrayList<Integer>   taskTokenzScore = new ArrayList<>();


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
            taskTokenzScore = selectedTask.getTaskTokenzScore();





            Log.i(TAG, "onCreate: " + selectedTask.toString());
            Log.i(TAG, "onCreate: " + selectedTask.getTaskName());
            taskImageView.setImageResource(selectedTask.getTaskImage());
            taskNameTextView.setText(selectedTask.getTaskName());
        }

        if (getIntent().hasExtra("selected_kid")) {
            Bundle data = getIntent().getExtras();
            selectedKid = data.getParcelable("selected_kid");
            if(taskTokenzScore.size() != selectedKid.getTokenNumber()) {
                taskTokenzScore = new ArrayList<>();
                for (int i = 0; i<selectedKid.getTokenNumber(); i++){
                    taskTokenzScore.add(0);
                }
                updateTaskTokenzScore();
            }


        }

        setupRecyclerView();

    }

    private void setupRecyclerView() {
        int taskTokenzImage;

        if (selectedTask.getNegativeReTask()) {
            taskTokenzImage = R.drawable.bunny;
        } else {
            taskTokenzImage = R.drawable.badbunny;
        }

        for (int i = 0; i<selectedKid.getTokenNumber(); i++){

            taskTokenzList.add(new TaskTokenz(selectedKid.getTokenImage(), taskTokenzScore.get(i) > 0));
        }

        taskTokenzAdapter = new TaskTokenzAdapter(taskTokenzList, this);
        taskTokenzRecyclerView = findViewById(R.id.taskTokenzz_recyclerview);
        taskTokenzRecyclerView.setHasFixedSize(true);
        taskTokenzRecyclerView.setAdapter(taskTokenzAdapter);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        taskTokenzRecyclerView.setLayoutManager(layoutManager);
        taskTokenzRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing_small);
        taskTokenzRecyclerView.addItemDecoration(new TaskTokenzGridItemDecoration(largePadding, smallPadding));
    }


    private void updateTaskTokenzScore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selectedTaskRef = db.collection("users").
                document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId()).
                collection("taskz").document(selectedTask.getFirestoreId());

        selectedTaskRef
                .update("taskTokenzScore", taskTokenzScore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void loadTaskTokenzData() {

        int taskTokenzImage;

        if (selectedTask.getNegativeReTask()) {
            taskTokenzImage = R.drawable.bunny;
        } else {
            taskTokenzImage = R.drawable.badbunny;
        }

        for (int i = 0; i<selectedKid.getTokenNumber(); i++){

            taskTokenzList.add(new TaskTokenz(taskTokenzImage,false));

        }

    }

    @Override
    public void onTaskTokenzClick(int position) {

        if (taskTokenzScore.get(position) == 1){
            taskTokenzScore.set(position,0);
        } else {
            taskTokenzScore.set(position,1);
        }

        updateTaskTokenzScore();
    }
}
