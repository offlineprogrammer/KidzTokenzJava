package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.offlineprogrammer.kidztokenz.kid.Kid;
import com.offlineprogrammer.kidztokenz.task.KidTask;
import com.offlineprogrammer.kidztokenz.task.TaskAdapter;
import com.offlineprogrammer.kidztokenz.task.TaskGridItemDecoration;
import com.offlineprogrammer.kidztokenz.taskTokenz.OnTaskTokenzListener;
import com.offlineprogrammer.kidztokenz.taskTokenz.TaskTokenz;
import com.offlineprogrammer.kidztokenz.taskTokenz.TaskTokenzAdapter;
import com.offlineprogrammer.kidztokenz.taskTokenz.TaskTokenzGridItemDecoration;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TaskActivity extends AppCompatActivity implements OnTaskTokenzListener {

    ImageView taskImageView;
    TextView taskNameTextView;
    KidTask selectedTask;
    Kid selectedKid;
    ImageButton deleteImageButton;
    private static final String TAG = "TaskActivity";
    private ArrayList<TaskTokenz> taskTokenzList = new ArrayList<>();
    private ArrayList<Long> taskTokenzScore = new ArrayList<>();
    private RecyclerView taskTokenzRecyclerView;
    private TaskTokenzAdapter taskTokenzAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        taskImageView = findViewById(R.id.taskImage);
        taskNameTextView = findViewById(R.id.taskName);
        deleteImageButton = findViewById(R.id.delete_button);

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteTaskDialog(TaskActivity.this);
            }
        });

        getExtras();
    }

    private void showDeleteTaskDialog(TaskActivity taskActivity) {

        final AlertDialog builder = new AlertDialog.Builder(taskActivity).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_delete_task, null);
        Button okBtn = dialogView.findViewById(R.id.deletetask_confirm_button);
        Button cancelBtn = dialogView.findViewById(R.id.deletetask_cancel_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
                deleteTask();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteTask() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selectedTaskRef = db.collection("users").
                document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId()).
                collection("taskz").document(selectedTask.getFirestoreId());
        selectedTaskRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        finish();
                        //finishAndRemoveTask();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void getExtras() {
        if (getIntent().hasExtra("selected_task")) {
            Bundle data = getIntent().getExtras();
            selectedTask = data.getParcelable("selected_task");
            taskTokenzScore = selectedTask.getTaskTokenzScore();
            Log.i(TAG, "onCreate: " + taskTokenzScore);
            Log.i(TAG, "onCreate: " + selectedTask.getTaskName());
            taskImageView.setImageResource(selectedTask.getTaskImage());
            taskNameTextView.setText(selectedTask.getTaskName());
        }
        if (getIntent().hasExtra("selected_kid")) {
            Bundle data = getIntent().getExtras();
            selectedKid = data.getParcelable("selected_kid");
        }

        getTaskTokenzScore();


    }

    private void verfifyTokenzScore() {
        if (taskTokenzScore.size() != selectedKid.getTokenNumber()) {
            Log.i(TAG, "getExtras: taskTokenzScore.size() != selectedKid.getTokenNumber() ");
            Log.i(TAG, "getExtras: taskTokenScore Size is " + taskTokenzScore.size());
            Log.i(TAG, "getExtras: selectedKid.getTokenNumber() is " + selectedKid.getTokenNumber());
            taskTokenzScore = new ArrayList<>();
            for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
                taskTokenzScore.add(0L);
            }
            updateTaskTokenzScore();
            selectedTask.setTaskTokenzScore(taskTokenzScore);
            Log.i(TAG, "getExtras: taskTokenScore Size is now " + taskTokenzScore.size());
            Log.i(TAG, "getExtras: selectedTask.getTaskTokenzScore() " + selectedTask.getTaskTokenzScore());

        }
    }

    private void getTaskTokenzScore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selectedTaskRef = db.collection("users").
                document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId()).
                collection("taskz").document(selectedTask.getFirestoreId());
        selectedTaskRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                taskTokenzScore = (ArrayList<Long>) document.get("taskTokenzScore");
                                selectedTask.setTaskTokenzScore(taskTokenzScore);
                                Log.i(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.i(TAG, "No such document");
                            }
                            verfifyTokenzScore();
                            setupRecyclerView();
                        } else {
                            Log.i(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void setupRecyclerView() {
        int tokenImage;
        if (selectedTask.getNegativeReTask()) {
            tokenImage = selectedKid.getBadTokenImage();
        } else {
            tokenImage = selectedKid.getTokenImage();
        }

        for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
            taskTokenzList.add(new TaskTokenz(tokenImage, taskTokenzScore.get(i) > 0));
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
        try {

            Log.i(TAG, "updateTaskTokenzScore: Start updating the score...");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.i(TAG, "updateTaskTokenzScore: db...");
            DocumentReference selectedTaskRef = db.collection("users").
                    document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId()).
                    collection("taskz").document(selectedTask.getFirestoreId());

            Log.i(TAG, "updateTaskTokenzScore: selectedTaskRef..." + selectedTaskRef);
            selectedTaskRef
                    .update("taskTokenzScore", taskTokenzScore)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Error updating document", e);
                        }
                    });
            Log.i(TAG, "updateTaskTokenzScore: saved....");
        } catch (Exception e) {
            Log.i(TAG, "updateTaskTokenzScore: Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskTokenzClick(int position) {

        if (taskTokenzScore.get(position) == 1) {
            taskTokenzScore.set(position, 0L);
        } else {
            taskTokenzScore.set(position, 1L);
        }
        updateTaskTokenzScore();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
    }


}
