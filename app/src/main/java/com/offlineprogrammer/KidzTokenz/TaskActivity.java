package com.offlineprogrammer.KidzTokenz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.ads.AdRequest;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.offlineprogrammer.KidzTokenz.taskTokenz.OnTaskTokenzListener;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenz;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzAdapter;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzGridItemDecoration;
import com.transitionseverywhere.ChangeText;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class TaskActivity extends AppCompatActivity implements OnTaskTokenzListener {

    private static final int CAMERA_REQUEST = 2222;


    private com.google.android.gms.ads.AdView adView;
    ImageView taskImageView;
    TextView taskNameTextView;
    TextView taskmsg;
    KidTask selectedTask;
    Kid selectedKid;
    ImageButton deleteImageButton;
    ImageButton restartImageButton;
    ImageButton select_button;
    ImageButton capture_button;
    ConstraintLayout task_ctLayout;
    private FirebaseHelper firebaseHelper;

    private static final String TAG = "TaskActivity";
    private ArrayList<TaskTokenz> taskTokenzList = new ArrayList<>();
    private ArrayList<Long> taskTokenzScore = new ArrayList<>();
    private RecyclerView taskTokenzRecyclerView;
    private TaskTokenzAdapter taskTokenzAdapter;

    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_IMAGE_CAPTURE = 33;


    private Uri taskTokenImageUri = null;
    private Uri imagePath;

    ProgressDialog progressDialog;
    String currentPhotoPath;
    KonfettiView viewKonfetti;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        firebaseHelper = new FirebaseHelper(getApplicationContext());
        taskImageView = findViewById(R.id.taskImage);
        taskNameTextView = findViewById(R.id.taskName);
        deleteImageButton = findViewById(R.id.delete_button);
        restartImageButton = findViewById(R.id.restart_button);
        select_button = findViewById(R.id.select_button);
        capture_button = findViewById(R.id.capture_button);
        taskmsg = findViewById(R.id.taskmsg);
        viewKonfetti = findViewById(R.id.viewKonfetti);
        task_ctLayout = findViewById(R.id.task_ctLayout);
        configActionButtons();

        getExtras();
        configureAdView();

    }

    private void configActionButtons() {
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
//                captureTaskImage();

                ImagePicker.create(TaskActivity.this).returnMode(ReturnMode.ALL)
                        .folderMode(true).includeVideo(false).limit(1).theme(R.style.AppTheme_NoActionBar).single().start();
            }
        });
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
    }


    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //  ((ClaimStarzActivity) this.context).isManuallyPaused(true);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).setTitle("Permissions Required").setPositiveButton("Settings").setNegativeButton("Cancel").setRequestCode(5).build().show();
        }
    }

    public void onCropFinish(Intent intent) {
        this.imagePath = UCrop.getOutput(intent);
        GlideApp.with(this).load(this.imagePath.getPath()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into(this.taskImageView);
        uploadImage();
    }

    private void uploadImage() {
        if (this.imagePath != null) {
            // Code for showing progressDialog while uploading
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            firebaseHelper.uploadImage(selectedKid, selectedTask, this.imagePath).observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new SingleObserver<Uri>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "continueWithTask kidzList => onError: " + e.getMessage());
                        }

                        @Override
                        public void onSuccess(Uri imageUri) {
                            runOnUiThread(() -> {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        firebaseHelper.logEvent("image_uploaded");
                                        selectedTask.setFirestoreImageUri(imageUri.toString());
                                        updateTaskTokenzImage();
                                        progressDialog.dismiss();
                                    }
                                });

                            });
                        }
                    });

        } else {
            //  savedClaimedStarz(null);

        }

    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (ImagePicker.shouldHandle(i, i2, intent)) {
            Image firstImageOrNull = ImagePicker.getFirstImageOrNull(intent);
            if (firstImageOrNull != null) {
                //UCrop.of(Uri.fromFile(new File(firstImageOrNull.getPath())), Uri.fromFile(new File(getCacheDir(), "cropped"))).start(this);
                UCrop.of(Uri.fromFile(new File(firstImageOrNull.getPath())), Uri.fromFile(new File(getCacheDir(), "cropped"))).withAspectRatio(1.0f, 1.0f).start(this);

                //   Uri destinationUri = Uri.fromFile(new File(myContext.getCacheDir(), "IMG_" + System.currentTimeMillis()));
                //   UCrop.of(sourceUri, destinationUri)
                //           .withMaxResultSize(1080, 768) // any resolution you want
                //           .start(mContext, YourFragment/YourActivity.this);

            }
        }

        if (i == UCrop.REQUEST_CROP) {
            onCropFinish(intent);
        }
    }


    private void celebratCompletion() {
        viewKonfetti.bringToFront();
        viewKonfetti.setTranslationZ(1);
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED)
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
        com.google.android.gms.ads.AdRequest adRequest = new AdRequest.Builder().build();
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



    private void selectTaskImage() {

        firebaseHelper.logEvent("photo_select");

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
        firebaseHelper.logEvent("score_reset");
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
                firebaseHelper.logEvent("task_deleted");
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

        firebaseHelper.deleteKidTask(selectedTask, selectedKid)
                .subscribe(() -> {
                    Log.i(TAG, "updateRewardImage: completed");
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "result");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();


                }, throwable -> {
                    // handle error
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
                       // .placeholder(R.drawable.bekind)
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

        Log.i(TAG, "getTaskTokenzScoreAndImage: Task " + selectedTask);
        Log.i(TAG, "getTaskTokenzScoreAndImage: " + selectedTask.getTaskTokenzScore());


        verfifyTokenzScore();
        setupRecyclerView();
        setTaskMsg();

    }

    private void setTaskMsg() {
        TransitionManager.beginDelayedTransition(task_ctLayout,
                new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
        if (selectedTask.getNegativeReTask()) {
            // Is it inProgress
            if (taskTokenzScore.indexOf(0L)>-1) {


                taskmsg.setText(getResources().getString(R.string.ktz_negtask_inprogress_msg));
            } else {
                taskmsg.setText(getResources().getString(R.string.ktz_negtask_complete_msg));
                firebaseHelper.logEvent("negativeTask_completed");
            }

        } else {
            if (taskTokenzScore.indexOf(0L)>-1) {
                taskmsg.setText(getResources().getString(R.string.ktz_task_inprogress_msg));
            } else {
                celebratCompletion();
                taskmsg.setText(getResources().getString(R.string.ktz_task_complete_msg));
                firebaseHelper.logEvent("task_completed");
            }

        }
    }

    private void setupRecyclerView() {
        int tokenImage;
        if (selectedTask.getNegativeReTask()) {
            tokenImage = getApplicationContext().getResources().getIdentifier(selectedKid.getBadTokenImageResourceName(), "drawable",
                    getApplicationContext().getPackageName());

            //tokenImage = selectedKid.getBadTokenImage();
        } else {
            //tokenImage = selectedKid.getTokenImage();
            tokenImage = getApplicationContext().getResources().getIdentifier(selectedKid.getTokenImageResourceName(), "drawable",
                    getApplicationContext().getPackageName());
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




    private void updateTaskTokenzImage() {
        try {
            firebaseHelper.updateTaskTokenzImage(selectedTask, selectedKid)
                    .subscribe(() -> Log.i(TAG, "updateTaskTokenzScore: done"), throwable -> {
                        // handle error
                    });

        } catch (Exception e) {
            Log.i(TAG, "updateTaskTokenzScore: Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTaskTokenzScore() {
        try {
            selectedTask.setTaskTokenzScore(taskTokenzScore);
            Log.i(TAG, "updateTaskTokenzScore: Start updating the score...");

            firebaseHelper.updateTaskTokenzScore(selectedTask, selectedKid)
                    .subscribe(() -> Log.i(TAG, "updateTaskTokenzScore: done"), throwable -> {
                        // handle error
                    });

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
