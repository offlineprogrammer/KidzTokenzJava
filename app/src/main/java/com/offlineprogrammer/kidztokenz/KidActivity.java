package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.offlineprogrammer.kidztokenz.kid.Kid;
import com.offlineprogrammer.kidztokenz.task.OnTaskListener;
import com.offlineprogrammer.kidztokenz.task.KidTask;
import com.offlineprogrammer.kidztokenz.task.TaskAdapter;
import com.offlineprogrammer.kidztokenz.task.TaskGridItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class KidActivity extends AppCompatActivity implements OnTaskListener {

    private static final String TAG = "KidActivity";
    ImageView kidImageView;
    TextView kidNameTextView;
    ImageView tokenImageView;
    CardView tokenImageCard;
    CardView tokenNumberCard;
    ImageView tokenNumberImageView;
    Kid selectedKid;
    private ArrayList<KidTask> taskzList = new ArrayList<>();

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);

        kidImageView = findViewById(R.id.kidMonsterImage);
        tokenImageView = findViewById(R.id.tokenImageView);
        kidNameTextView = findViewById(R.id.myAwesomeTextView);
        tokenImageCard = findViewById(R.id.tokenImageCard);
        tokenNumberCard = findViewById(R.id.tokenNumberCard);
        tokenNumberImageView = findViewById(R.id.tokenNumberImageView);

        configActionButton();
        setupRecyclerView();

        tokenImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(KidActivity.this, TokenzActivity.class);
                startActivityForResult(mIntent, 2);

            }
        });

        tokenNumberCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(KidActivity.this, TokenNumberActivity.class);
                startActivityForResult(mIntent, 3);

            }
        });

        if (getIntent().hasExtra("selected_kid")) {
            Bundle data = getIntent().getExtras();
            selectedKid = (Kid) data.getParcelable("selected_kid");
           // Kid kid = getIntent().getParcelableExtra("selected_kid");


            kidImageView.setImageResource(selectedKid.getMonsterImage());
            kidNameTextView.setText(selectedKid.getKidName());
            tokenImageView.setImageResource(selectedKid.getTokenImage());
            tokenNumberImageView.setImageResource(selectedKid.getTokenNumber());
            getkidTaskz(selectedKid.getFirestoreId());
        }

    }

    private void configActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog(KidActivity.this);
            }
        });
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(taskzList,this);
        taskRecyclerView = findViewById(R.id.taskz_recyclerview);
        taskRecyclerView.setHasFixedSize(true);
        taskRecyclerView.setAdapter(taskAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        taskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing_small);
        taskRecyclerView.addItemDecoration(new TaskGridItemDecoration(largePadding, smallPadding));

    }


    private void getkidTaskz(String kidId) {
        // final Boolean bFoundData ;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());

        selectedKidRef.collection("taskz").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d("Got Task Data", document.getId() + " => " + document.getData());
                                    KidTask myTask = document.toObject(KidTask.class);
                                    taskzList.add(myTask);
                                    taskAdapter.add(myTask,0);
                                    taskRecyclerView.scrollToPosition(0);
                                } else {
                                    // saveUser();
                                }
                            }


                            // configActionButton();
                        } else {
                            //  saveUser();
                            Log.d("Got Date", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void showAddTaskDialog(Context c) {



        final AlertDialog builder = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_task, null);
        final TextInputLayout taskNameText = dialogView.findViewById(R.id.taskname_text_input);


        taskNameText.requestFocus();

        Button okBtn= dialogView.findViewById(R.id.task_save_button);
        Button cancelBtn = dialogView.findViewById(R.id.task_cancel_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String taskName = String.valueOf(taskNameText.getEditText().getText());
                if (!isTaskNameValid(taskName)) {
                    taskNameText.setError(getString(R.string.kid_error_name));
                } else {
                    taskNameText.setError(null);
                    Date currentTime = Calendar.getInstance().getTime();
                    KidTask newTask = new KidTask(taskName, R.drawable.badface, currentTime);
                    newTask = saveTask(newTask);
                    taskAdapter.add(newTask, 0);
                    taskRecyclerView.scrollToPosition(0);
                    builder.dismiss();
                }


            }
        });

        taskNameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String kidName = String.valueOf(taskNameText.getEditText().getText());
                if (isTaskNameValid(kidName)) {
                    taskNameText.setError(null); //Clear the error
                }
                return false;
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setView(dialogView);
        builder.show();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    private KidTask saveTask(KidTask newTask) {

        taskzList.add(newTask);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newTaskRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId()).collection("taskz").document();
        newTask.setFirestoreId(newTaskRef.getId());
        newTask.setKidFirestoreId(selectedKid.getFirestoreId());
        Map<String, Object> taskValues = newTask.toMap();

        newTaskRef.set(taskValues, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Add Kid", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Add Kid", "Error writing document", e);
                    }
                });

        return newTask;

    }

    private boolean isTaskNameValid(String taskName) {
        return taskName != null && taskName.length() >= 2;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("Image",0);
                tokenImageView.setImageResource(result);
                updateKidTokenImage(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 3) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("Image",0);
                tokenNumberImageView.setImageResource(result);
                updateKidTokenNumberImage(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void updateKidTokenImage(int newTokenImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());

// Set the "isCapital" field of the city 'DC'
        selectedKidRef
                .update("tokenImage", newTokenImage)
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

    private void updateKidTokenNumberImage(int newTokenNumberImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());

// Set the "isCapital" field of the city 'DC'
        selectedKidRef
                .update("tokenNumber", newTokenNumberImage)
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

    @Override
    public void onTaskClick(int position) {

    }
}
