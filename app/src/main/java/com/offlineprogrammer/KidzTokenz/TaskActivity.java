package com.offlineprogrammer.KidzTokenz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.offlineprogrammer.KidzTokenz.taskTokenz.OnTaskTokenzListener;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenz;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzAdapter;
import com.offlineprogrammer.KidzTokenz.taskTokenz.TaskTokenzGridItemDecoration;
import com.transitionseverywhere.ChangeText;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


public class TaskActivity extends AppCompatActivity implements OnTaskTokenzListener {

    private static final int CAMERA_REQUEST = 2222;

    private Fragment currentFragment;


    private com.google.android.gms.ads.AdView adView;
    ImageView taskImageView;
    TextView taskNameTextView;
    TextView taskmsg;
    KidTask selectedTask;
    Kid selectedKid;
    ImageButton deleteImageButton;
    ImageButton restartImageButton;

    ImageButton capture_button;
    ConstraintLayout task_ctLayout;
    private FirebaseHelper firebaseHelper;

    private static final String TAG = "TaskActivity";
    private final ArrayList<TaskTokenz> taskTokenzList = new ArrayList<>();
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

    ReviewInfo reviewInfo;
    ReviewManager manager;
    private String shareImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        firebaseHelper = new FirebaseHelper(getApplicationContext());
        taskImageView = findViewById(R.id.taskImage);
        taskNameTextView = findViewById(R.id.taskName);
        deleteImageButton = findViewById(R.id.delete_button);
        restartImageButton = findViewById(R.id.restart_button);

        capture_button = findViewById(R.id.capture_button);
        taskmsg = findViewById(R.id.taskmsg);
        viewKonfetti = findViewById(R.id.viewKonfetti);
        task_ctLayout = findViewById(R.id.task_ctLayout);
        configActionButtons();

        getExtras();
        configureAdView();

    }

    private void Review() {
        manager = ReviewManagerFactory.create(this);
        manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                    manager.launchReviewFlow(TaskActivity.this, reviewInfo).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            firebaseHelper.logEvent("rating_failed");

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseHelper.logEvent("rating_completed");

                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                firebaseHelper.logEvent("rating_request_failed");

            }
        });
    }


    private void configActionButtons() {
        deleteImageButton.setOnClickListener(view -> showDeleteTaskDialog(TaskActivity.this));

        restartImageButton.setOnClickListener(view -> resetTaskTokenzScore());


        capture_button.setOnClickListener(view -> {
//                captureTaskImage();

            ImagePicker.create(TaskActivity.this).returnMode(ReturnMode.ALL)
                    .folderMode(true).includeVideo(false).limit(1).theme(R.style.AppTheme_NoActionBar).single().start();
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
        if (intent == null) {
            return;
        }
        this.imagePath = UCrop.getOutput(intent);

        if (taskTokenzScore.indexOf(0L) > -1) {
            GlideApp.with(this).load(this.imagePath.getPath()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into(this.taskImageView);
            uploadImage();
        } else {

            shareCelebration(imagePath);


        }


        // uploadImage();
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
                            Timber.e("continueWithTask kidzList => onError: %s", e.getMessage());
                        }

                        @Override
                        public void onSuccess(Uri imageUri) {
                            runOnUiThread(() -> runOnUiThread(() -> {
                                firebaseHelper.logEvent("image_uploaded");
                                selectedTask.setFirestoreImageUri(imageUri.toString());
                                updateTaskTokenzImage();
                                progressDialog.dismiss();
                            }));
                        }
                    });

        }


    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        CelebrateFragment celebrate;
        if (i2 == -1 && i == 69) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            String name = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 1).getName();
            if (Constants.CELEBRATE.equals(name) && (celebrate = (CelebrateFragment) supportFragmentManager.findFragmentByTag(name)) != null && celebrate.isVisible()) {
                celebrate.onCropFinish(intent);
            } else {
                onCropFinish(intent);
            }
        }


        if (ImagePicker.shouldHandle(i, i2, intent)) {
            Image firstImageOrNull = ImagePicker.getFirstImageOrNull(intent);
            if (firstImageOrNull != null) {
                UCrop.of(Uri.fromFile(new File(firstImageOrNull.getPath())), Uri.fromFile(new File(getCacheDir(), "cropped"))).withAspectRatio(1.0f, 1.0f).start(this);
            }
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
                .setPosition(viewKonfetti.getX() + viewKonfetti.getWidth() / 2, viewKonfetti.getY() + viewKonfetti.getHeight() / 2)
                .burst(600);
        //.streamFor(300, 5000L);

        shareCelebration(null);

        Review();
    }



    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static File store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        // File file2 = new File(path2, selectedKid.getKidName() + " " + System.currentTimeMillis() + ".jpg");
        File file = new File(dirPath, fileName + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOkAndFinish() {
        // Kiddy.didManuallyPaused = true;
        if (getCallingActivity() == null) {
            startActivity(new Intent(this, KidActivity.class));
            finish();
            return;
        }
        setResult(-1, new Intent());
        finish();
    }

    public void gotoSharePage(Bitmap bitmap, String str) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        this.setTitle(R.string.share);
        ShareFragment newInstance = ShareFragment.newInstance();
        newInstance.setData(bitmap, str, selectedKid);

        this.currentFragment = newInstance;
        beginTransaction.add(R.id.container, newInstance, Constants.SHARE);
        beginTransaction.addToBackStack(Constants.SHARE);
        beginTransaction.commit();
    }


    private void shareCelebration(Uri imagePath) {

        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        CelebrateFragment newInstance = CelebrateFragment.newInstance(selectedKid, selectedTask);

        FrameLayout main_container = findViewById(R.id.main_container);
        main_container.setVisibility(View.GONE);

        this.currentFragment = newInstance;
        beginTransaction.replace(R.id.container, newInstance, Constants.CELEBRATE);
        beginTransaction.addToBackStack(Constants.CELEBRATE);
        beginTransaction.commit();





  /*      final AlertDialog builder = new AlertDialog.Builder(TaskActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_celebrate, null);
        TextView share_celebrate_thoughtText = dialogView.findViewById(R.id.share_celebrate_desc_text_input);
        share_celebrate_thoughtText.requestFocus();
        Button okBtn = dialogView.findViewById(R.id.share_celebrate_button);
        TextView camera_button = dialogView.findViewById(R.id.camera_button);
        ImageView share_celebrate_ImageView = dialogView.findViewById(R.id.share_celebrate_ImageView);
        ImageView share_celebrate_edit_ImageView = dialogView.findViewById(R.id.share_celebrate_edit_ImageView);
        LinearLayout share_layout = dialogView.findViewById(R.id.share_layout);
        LinearLayout gift_layout = dialogView.findViewById(R.id.gift_layout);
        ImageView giftImage = dialogView.findViewById(R.id.giftImage);
        TextView redeem_text = dialogView.findViewById(R.id.redeem_text);


        if (imagePath == null) {

        } else {

            this.shareImagePath = this.imagePath.getPath();

            GlideApp.with(this).load(this.imagePath.getPath()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into(share_celebrate_ImageView);
            GlideApp.with(this).load(this.imagePath.getPath()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into(giftImage);
            camera_button.setVisibility(View.GONE);
            share_celebrate_ImageView.setVisibility(View.VISIBLE);
            //  share_celebrate_edit_ImageView.setVisibility(View.VISIBLE);
        }

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.create(TaskActivity.this).returnMode(ReturnMode.ALL)
                        .folderMode(true).includeVideo(false).limit(1).theme(R.style.AppTheme_NoActionBar).single().start();
                Log.i(TAG, "onClick: dissmissIT");
                builder.dismiss();
                Log.i(TAG, "onClick: dissmissIT");
            }
        });

        share_celebrate_edit_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.create(TaskActivity.this).returnMode(ReturnMode.ALL)
                        .folderMode(true).includeVideo(false).limit(1).theme(R.style.AppTheme_NoActionBar).single().start();
                builder.dismiss();
                Log.i(TAG, "onClick: dissmissIT");
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String thoughttxt = String.valueOf(share_celebrate_thoughtText.getText());
                if (!isThoughtTXTValid(thoughttxt)) {
                    share_celebrate_thoughtText.setError(getString(R.string.kid_error_name));
                } else {
                    share_celebrate_thoughtText.setError(null);
                    Date currentTime = Calendar.getInstance().getTime();
                    //  mFirebaseAnalytics.logEvent("kid_created", null);
                    //View v1 = getWindow().getDecorView().findViewById(android.R.id.content);//dialogView;
                    //  share_celebrate_edit_ImageView.setVisibility(View.GONE);

                    redeem_text.setText(thoughttxt);


//                    giftImage.setImageDrawable(share_celebrate_ImageView.getDrawable());

                    share_layout.setVisibility(View.GONE);
                    gift_layout.setVisibility(View.VISIBLE);

                    //SystemClock.sleep(2000);

                    //   dialogView.refreshDrawableState().invalidate();

                    View v1 = dialogView;


                    Bitmap viewImage = getScreenShot(v1);
                    File photoFile = null;
                    photoFile = store(viewImage, selectedKid.getKidName());
                    shareImage(photoFile);
                    //  builder.dismiss();
                }


            }
        });

        share_celebrate_thoughtText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String kidName = String.valueOf(share_celebrate_thoughtText.getText());
                if (isThoughtTXTValid(kidName)) {
                    share_celebrate_thoughtText.setError(null); //Clear the error
                }
                return false;
            }
        });

        builder.setView(dialogView);
        builder.show();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);*/

    }

    private void shareImage(File file) {


        Uri photoURI = FileProvider.getUriForFile(this,
                "com.offlineprogrammer.KidzTokenz.fileprovider",
                file);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.setType("*/*");


        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Progress");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Download Kiddy https://play.google.com/store/apps/details?id=com.kiddy.kiddy");
        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
        Intent chooser = Intent.createChooser(intent, "Share File");
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }


        try {


            startActivity(chooser);

            //  startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }




    private boolean isThoughtTXTValid(String thoughttxt) {
        return true;
    }

    private void configureAdView() {
        adView = findViewById(R.id.ad_view);
        com.google.android.gms.ads.AdRequest adRequest = new AdRequest.Builder().build();
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
            tokenImage = getApplicationContext().getResources().getIdentifier(selectedKid.getBadTokenImageResourceName(), "drawable",
                    getApplicationContext().getPackageName());
        } else {
            tokenImage = getApplicationContext().getResources().getIdentifier(selectedKid.getTokenImageResourceName(), "drawable",
                    getApplicationContext().getPackageName());
        }
        for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
            taskTokenzList.add(new TaskTokenz(tokenImage, taskTokenzScore.get(i) > 0));
        }

        taskTokenzAdapter.updateData(taskTokenzList);
        setTaskMsg();
        firebaseHelper.logEvent("score_reset");
        Review();
    }



    private void showDeleteTaskDialog(TaskActivity taskActivity) {
        final AlertDialog builder = new AlertDialog.Builder(taskActivity).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_delete_task, null);
        Button okBtn = dialogView.findViewById(R.id.deletetask_confirm_button);
        Button cancelBtn = dialogView.findViewById(R.id.deletetask_cancel_button);
        okBtn.setOnClickListener(v -> {
            builder.dismiss();
            deleteTask();

        });
        cancelBtn.setOnClickListener(v -> builder.dismiss());
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteTask() {

        firebaseHelper.deleteKidTask(selectedTask, selectedKid)
                .subscribe(() -> {
                    firebaseHelper.logEvent("task_deleted");
                    Timber.i("updateRewardImage: completed");
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
            Timber.i("onCreate: %s", taskTokenzScore);
            Timber.i("onCreate: %s", selectedTask.getTaskName());
            taskNameTextView.setText(selectedTask.getTaskName());
            if (selectedTask.getFirestoreImageUri() == null) {
                taskImageView.setImageResource(selectedTask.getTaskImage());
            } else {
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
            Timber.i("getExtras: taskTokenzScore.size() != selectedKid.getTokenNumber() ");
            Timber.i("getExtras: taskTokenScore Size is %s", taskTokenzScore.size());
            Timber.i("getExtras: selectedKid.getTokenNumber() is %s", selectedKid.getTokenNumber());
            taskTokenzScore = new ArrayList<>();
            for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
                taskTokenzScore.add(0L);
            }
            updateTaskTokenzScore();
            selectedTask.setTaskTokenzScore(taskTokenzScore);
            Timber.i("getExtras: taskTokenScore Size is now %s", taskTokenzScore.size());
            Timber.i("getExtras: selectedTask.getTaskTokenzScore() %s", selectedTask.getTaskTokenzScore());

        }
    }

    private void getTaskTokenzScoreAndImage() {

        Timber.i("getTaskTokenzScoreAndImage: Task %s", selectedTask);
        Timber.i("getTaskTokenzScoreAndImage: %s", selectedTask.getTaskTokenzScore());


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
                Review();
            }

        } else {
            if (taskTokenzScore.indexOf(0L)>-1) {
                taskmsg.setText(getResources().getString(R.string.ktz_task_inprogress_msg));
            } else {
                celebratCompletion();
                taskmsg.setText(getResources().getString(R.string.ktz_task_complete_msg));
                firebaseHelper.logEvent("task_completed");
                Review();
            }

        }
    }

    private void setupRecyclerView() {
        int tokenImage;
        if (selectedTask.getNegativeReTask()) {
            tokenImage = getApplicationContext().getResources().getIdentifier(selectedKid.getBadTokenImageResourceName(), "drawable",
                    getApplicationContext().getPackageName());
        } else {
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
                    .subscribe(() -> Timber.i("updateTaskTokenzScore: done"), throwable -> {
                        // handle error
                    });

        } catch (Exception e) {
            Timber.i("updateTaskTokenzScore: Error %s", e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTaskTokenzScore() {
        try {
            selectedTask.setTaskTokenzScore(taskTokenzScore);
            Timber.i("updateTaskTokenzScore: Start updating the score...");

            firebaseHelper.updateTaskTokenzScore(selectedTask, selectedKid)
                    .subscribe(() -> Timber.i("updateTaskTokenzScore: done"), throwable -> {
                        // handle error
                    });

        } catch (Exception e) {
            Timber.i("updateTaskTokenzScore: Error %s", e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed Called");
        goBack();
    }

    private void goBack() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "result");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBack();
        }
        return true;
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
