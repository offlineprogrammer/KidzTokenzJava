package com.offlineprogrammer.KidzTokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.offlineprogrammer.KidzTokenz.taskTokenz.OnTaskTokenzListener;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenz;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzAdapter;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzGridItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskActivity extends AppCompatActivity implements OnTaskTokenzListener {

    private PublisherAdView adView;
    ImageView taskImageView;
    TextView taskNameTextView;
    TextView taskmsg;
    KidTask selectedTask;
    Kid selectedKid;
    ImageButton deleteImageButton;
    ImageButton restartImageButton;
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
        restartImageButton = findViewById(R.id.restart_button);
        taskmsg = findViewById(R.id.taskmsg);

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteTaskDialog(TaskActivity.this);
            }
        });

        restartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTaskTokenzScore();
            }
        });

        getExtras();

        adView = findViewById(R.id.ad_view);
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("B3EEABB8EE11C2BE770B684D95219ECB"))
                        .build());

        // Create an ad request.
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private void resetTaskTokenzScore() {

        taskTokenzScore = new ArrayList<>();
        for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
            taskTokenzScore.add(0L);
        }
        updateTaskTokenzScore();
        selectedTask.setTaskTokenzScore(taskTokenzScore);
        int tokenImage;
        taskTokenzList.clear();
        if (selectedTask.getNegativeReTask()) {
            tokenImage = selectedKid.getBadTokenImage();
        } else {
            tokenImage = selectedKid.getTokenImage();
        }
        for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
            taskTokenzList.add(new TaskTokenz(tokenImage, taskTokenzScore.get(i) > 0));
        }

        taskTokenzAdapter.updateData(taskTokenzList);
        setTaskMsg();
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
                            setTaskMsg();
                        } else {
                            Log.i(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void setTaskMsg() {
        if (selectedTask.getNegativeReTask()) {
            // Is it inProgress
            if (taskTokenzScore.indexOf(0L)>-1) {
                taskmsg.setText(getResources().getString(R.string.ktz_negtask_inprogress_msg));
            } else {
                taskmsg.setText(getResources().getString(R.string.ktz_negtask_complete_msg));
            }

        } else {
            if (taskTokenzScore.indexOf(0L)>-1) {
                taskmsg.setText(getResources().getString(R.string.ktz_task_inprogress_msg));
            } else {
                taskmsg.setText(getResources().getString(R.string.ktz_task_complete_msg));
            }

        }
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
        setTaskMsg();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


}
