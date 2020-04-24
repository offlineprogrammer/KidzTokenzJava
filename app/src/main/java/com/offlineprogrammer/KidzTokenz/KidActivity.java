package com.offlineprogrammer.KidzTokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.OnTaskListener;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.offlineprogrammer.KidzTokenz.task.TaskAdapter;
import com.offlineprogrammer.KidzTokenz.task.TaskGridItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class KidActivity extends AppCompatActivity implements OnTaskListener {

    private Uri mImageUri = null;
    ImageView taskSelectedImageView;
    private Uri outputFileUri;


    private static final String TAG = "KidActivity";
    ImageView kidImageView;
    TextView kidNameTextView;
    ImageView tokenImageView;
    CardView tokenImageCard;
    CardView tokenNumberCard;
    ImageView tokenNumberImageView;
    Kid selectedKid;
    ImageButton deleteImageButton;

    private ArrayList<KidTask> taskzList = new ArrayList<>();

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;

    ProgressDialog progressBar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private com.google.android.gms.ads.AdView adView;

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
        deleteImageButton = findViewById(R.id.delete_button);



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

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteKidDialog(KidActivity.this);
            }
        });

        if (getIntent().hasExtra("selected_kid")) {
           // setupProgressBar();
            Bundle data = getIntent().getExtras();
            selectedKid = data.getParcelable("selected_kid");
            kidImageView.setImageResource(selectedKid.getMonsterImage());
            kidNameTextView.setText(selectedKid.getKidName());
            tokenImageView.setImageResource(selectedKid.getTokenImage());
            tokenNumberImageView.setImageResource(selectedKid.getTokenNumberImage());
            getkidTaskz();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.ad_view);
        com.google.android.gms.ads.AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        /*MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("B3EEABB8EE11C2BE770B684D95219ECB"))
                        .build());
        // Create an ad request.
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        // Start loading the ad in the background.
        adView.loadAd(adRequest);
*/
    }

    private void setupProgressBar() {
        dismissProgressBar();
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Loading data ...");
        progressBar.show();
    }

    private void dismissProgressBar() {
        dismissWithCheck(progressBar);
    }

    public void dismissWithCheck(ProgressDialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished or destroyed
                // then dismiss it
                if (context instanceof Activity) {

                    // Api >=17
                    if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                        dismissWithTryCatch(dialog);
                    }
                } else
                    // if the Context used wasn't an Activity, then dismiss it too
                    dismissWithTryCatch(dialog);
            }
            dialog = null;
        }
    }

    public void dismissWithTryCatch(ProgressDialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            dialog = null;
        }
    }





    private void showDeleteKidDialog(Context c) {


        final AlertDialog builder = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_delete_kid, null);
        Button okBtn = dialogView.findViewById(R.id.deletekid_confirm_button);
        Button cancelBtn = dialogView.findViewById(R.id.deletekid_cancel_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               builder.dismiss();
                deleteKid();
                mFirebaseAnalytics.logEvent("kid_deleted", null);
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

    private void deleteKid() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());
        selectedKidRef.delete()
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
        taskAdapter = new TaskAdapter(taskzList, this);
        taskRecyclerView = findViewById(R.id.taskz_recyclerview);
        taskRecyclerView.setHasFixedSize(true);
        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        taskRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_taskz_grid_spacing_small);
        taskRecyclerView.addItemDecoration(new TaskGridItemDecoration(largePadding, smallPadding));

    }


    private void getkidTaskz() {

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
                                    taskAdapter.add(myTask, 0);
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
                        dismissProgressBar();
                    }
                });
    }

    private void showAddTaskDialog(Context c) {


        final AlertDialog builder = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_task, null);
        final TextInputLayout taskNameText = dialogView.findViewById(R.id.taskname_text_input);


        taskNameText.requestFocus();



        Button okBtn = dialogView.findViewById(R.id.task_save_button);
        Button cancelBtn = dialogView.findViewById(R.id.task_cancel_button);
        final SwitchMaterial negativeReInfSwitch =dialogView.findViewById(R.id.switch_negative_ReIn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String taskName = String.valueOf(taskNameText.getEditText().getText());
                if (!isTaskNameValid(taskName)) {
                    taskNameText.setError(getString(R.string.kid_error_name));
                } else {
                    taskNameText.setError(null);
                    Date currentTime = Calendar.getInstance().getTime();
                    Boolean negativeReTask = negativeReInfSwitch.isChecked();
                    KidTask newTask = new KidTask(taskName,
                                                    R.drawable.bekind,
                                                    "bekind",
                                                    currentTime,
                                                    negativeReTask);
                    ArrayList<Long>   taskTokenzScore = new ArrayList<>();
                    for (int i = 0; i<selectedKid.getTokenNumber(); i++){
                        taskTokenzScore.add(0L);
                    }
                    newTask.setTaskTokenzScore(taskTokenzScore);
                    newTask = saveTask(newTask);
                    taskzList.add(newTask);
                    taskAdapter.updateData(taskzList);
                    taskRecyclerView.scrollToPosition(0);
                    mFirebaseAnalytics.logEvent("task_created", null);
                    //onTaskClick(0);
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
            if (resultCode == Activity.RESULT_OK) {

                int result = data.getIntExtra("Image", 0);
                int badToken = data.getIntExtra("badImage", 0);
                String tokenImageImageResourceName =  data.getStringExtra("tokenImageImageResourceName");
                String badtokenImageImageResourceName =  data.getStringExtra("badtokenImageImageResourceName");
                tokenImageView.setImageResource(result);
                updateKidTokenImage(result,badToken,tokenImageImageResourceName,badtokenImageImageResourceName);


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {

                int selectedImage = data.getIntExtra("Image", 0);
                String selectedImageResourceName =  data.getStringExtra("ImageResource");
                tokenNumberImageView.setImageResource(selectedImage);
                int selectedTokenNumber = data.getIntExtra("TokenNumber", 0);
                Log.i(TAG, "onActivityResult: " + String.valueOf(selectedTokenNumber));
                updateKidTokenNumberImage(selectedImage, selectedImageResourceName, selectedTokenNumber);
          //      recreate();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 4) {

            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                if (action == null) {
                    isCamera = false;
                } else {
                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }

            Uri selectedImageUri;
            if (isCamera) {
                selectedImageUri = outputFileUri;
            } else {
                selectedImageUri = data == null ? null : data.getData();
            }

            taskSelectedImageView.setImageURI(selectedImageUri);

         //   Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
         //   taskSelectedImageView.setImageBitmap(thumbnail);

        } else if (requestCode == 5) {
            if (data != null) {
                mImageUri = data.getData();
                Log.i(TAG, "onActivityResult: " + mImageUri.toString());
                taskSelectedImageView.setImageURI(mImageUri);
            }
        }
    }//onActivityResult



    private void updateKidTokenImage(final int result, final int badToken, final String tokenImageImageResourceName, final String badtokenImageImageResourceName) {
        mFirebaseAnalytics.logEvent("tokenImage_updated", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());
        selectedKidRef
                .update("tokenImage", result,"badTokenImage",badToken,
                        "tokenImageResourceName", tokenImageImageResourceName,
                        "badTokenImageResourceName",badtokenImageImageResourceName )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        selectedKid.setTokenImage(result);
                        selectedKid.setBadTokenImage(badToken);
                        selectedKid.setTokenImageResourceName(tokenImageImageResourceName);
                        selectedKid.setBadTokenImageResourceName(badtokenImageImageResourceName);
                        recreate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void updateKidTokenNumberImage(final int newTokenNumberImage, final String selectedImageResourceName, final int selectedTokenNumber) {
        mFirebaseAnalytics.logEvent("tokenNumber_updated", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());

// Set the "isCapital" field of the city 'DC'
        selectedKidRef
                .update("tokenNumberImage", newTokenNumberImage,
                        "tokenNumberImageResourceName", selectedImageResourceName,
                        "tokenNumber", selectedTokenNumber)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        selectedKid.setTokenNumberImage(newTokenNumberImage);
                        selectedKid.setTokenNumberImageResourceName(selectedImageResourceName);
                        selectedKid.setTokenNumber(selectedTokenNumber);
                        recreate();
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

        taskzList = taskAdapter.getAllItems();

        Log.i(TAG, "Clicked " + position);
        Intent intent = new Intent(this, TaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_kid",  selectedKid);
        bundle.putParcelable("selected_task", taskzList.get(position));

        intent.putExtras(bundle);

      //  intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        Log.i(TAG, "onTaskClick: " + taskzList.get(position).toString());
       // intent.putExtra("selected_task",taskzList.get(position));
       // intent.putExtra("selected_kid",selectedKid);
        startActivity(intent);

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
        dismissWithCheck(progressBar);
        super.onDestroy();
    }

}
