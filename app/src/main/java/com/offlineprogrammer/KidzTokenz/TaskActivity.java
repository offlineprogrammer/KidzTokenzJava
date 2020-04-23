package com.offlineprogrammer.KidzTokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.offlineprogrammer.KidzTokenz.taskTokenz.OnTaskTokenzListener;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenz;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzAdapter;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzGridItemDecoration;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;


public class TaskActivity extends AppCompatActivity implements OnTaskTokenzListener {

    private PublisherAdView adView;
    ImageView taskImageView;
    TextView taskNameTextView;
    TextView taskmsg;
    KidTask selectedTask;
    Kid selectedKid;
    ImageButton deleteImageButton;
    ImageButton restartImageButton;
    ImageButton select_button;
    ImageButton capture_button;
    private static final String TAG = "TaskActivity";
    private ArrayList<TaskTokenz> taskTokenzList = new ArrayList<>();
    private ArrayList<Long> taskTokenzScore = new ArrayList<>();
    private RecyclerView taskTokenzRecyclerView;
    private TaskTokenzAdapter taskTokenzAdapter;

    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_IMAGE_CAPTURE = 33;

    private Uri filePath;
    private Uri taskTokenImageUri = null;

    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String currentPhotoPath;
    KonfettiView viewKonfetti;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        taskImageView = findViewById(R.id.taskImage);
        taskNameTextView = findViewById(R.id.taskName);
        deleteImageButton = findViewById(R.id.delete_button);
        restartImageButton = findViewById(R.id.restart_button);
        select_button = findViewById(R.id.select_button);
        capture_button = findViewById(R.id.capture_button);
        taskmsg = findViewById(R.id.taskmsg);
        viewKonfetti = findViewById(R.id.viewKonfetti);



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

        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTaskImage();
            }
        });

        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureTaskImage();
            }
        });

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getExtras();

        configureAdView();
    }


    private void celebratCompletion() {
        viewKonfetti.bringToFront();
        viewKonfetti.setTranslationZ(1);
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA,Color.RED)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(4000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new nl.dionsegijn.konfetti.models.Size(10, 20))
                .setPosition(viewKonfetti.getX() + viewKonfetti.getWidth()/2, viewKonfetti.getY()+viewKonfetti.getHeight()/2)
                .burst(600);
                //.streamFor(300, 5000L);
    }

    private void configureAdView() {
        adView = findViewById(R.id.ad_view);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        adView.loadAd(adRequest);

        /*MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("B3EEABB8EE11C2BE770B684D95219ECB"))
                        .build());
        // Create an ad request.
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        // Start loading the ad in the background.
        adView.loadAd(adRequest);
*/
    }

    private void captureTaskImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.offlineprogrammer.KidzTokenz.fileprovider",
                        photoFile);
                filePath = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    private void selectTaskImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);


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



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data != null) {
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                taskImageView.setImageBitmap(bitmap);
                uploadImage();
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }


        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            taskImageView.setImageURI(filePath);
            uploadImage();

        }
    }


    private void uploadImage()
    {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child(
                            "images/"+selectedKid.getFirestoreId()+"/"+selectedTask.getFirestoreId()+"/"
                                    + UUID.randomUUID().toString());

            taskImageView.setImageURI(filePath);

       ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        selectedTask.setFirestoreImageUri(downloadURL);
                        taskImageView.setImageURI(filePath);
                        updateTaskImageUri();
                        progressDialog.dismiss();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
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
        if (getIntent().getExtras() != null) {
            selectedTask = getIntent().getExtras().getParcelable("selected_task");
            selectedKid = getIntent().getExtras().getParcelable("selected_kid");

            taskTokenzScore = selectedTask.getTaskTokenzScore();
            Log.i(TAG, "onCreate: " + taskTokenzScore);
            Log.i(TAG, "onCreate: " + selectedTask.getTaskName());
            taskNameTextView.setText(selectedTask.getTaskName());
            if (selectedTask.getFirestoreImageUri() == null) {
                taskImageView.setImageResource(selectedTask.getTaskImage());
            }
            else {
                taskTokenImageUri = Uri.parse(selectedTask.getFirestoreImageUri());
                GlideApp.with(TaskActivity.this)
                        .load(taskTokenImageUri)
                        .placeholder(R.drawable.bekind)
                        .into(taskImageView);
            }


        }


        getTaskTokenzScoreAndImage();


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

    private void getTaskTokenzScoreAndImage() {
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
                                String sImageUrl = (String) document.get("firestoreImageUri");
                                if(sImageUrl != null) {
                                taskTokenImageUri = Uri.parse(sImageUrl);
                                selectedTask.setTaskTokenzScore(taskTokenzScore);
                                selectedTask.setFirestoreImageUri(taskTokenImageUri.toString());

                                    GlideApp.with(TaskActivity.this)
                                            .load(taskTokenImageUri)
                                            .placeholder(R.drawable.bekind)
                                            .into(taskImageView);


                                }
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
                celebratCompletion();
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

    private void updateTaskImageUri() {
        try {


            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference selectedTaskRef = db.collection("users").
                    document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId()).
                    collection("taskz").document(selectedTask.getFirestoreId());
            selectedTaskRef
                    .update("firestoreImageUri", selectedTask.getFirestoreImageUri().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "DocumentSnapshot successfully updated!");
                            taskTokenImageUri = Uri.parse(selectedTask.getFirestoreImageUri());
                            GlideApp.with(TaskActivity.this)
                                    .load(taskTokenImageUri)
                                    .placeholder(R.drawable.bekind)
                                    .into(taskImageView);
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
 //       recreate();
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

        dismissProgressDialog();

        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
