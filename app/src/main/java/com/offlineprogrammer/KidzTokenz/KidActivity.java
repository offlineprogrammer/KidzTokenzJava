package com.offlineprogrammer.KidzTokenz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.offlineprogrammer.KidzTokenz.task.OnTaskListener;
import com.offlineprogrammer.KidzTokenz.task.TaskAdapter;
import com.offlineprogrammer.KidzTokenz.task.TaskGridItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class KidActivity extends AppCompatActivity implements OnTaskListener {

    FirebaseHelper firebaseHelper;


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
    private Disposable disposable;

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;

    ProgressDialog progressBar;

    private com.google.android.gms.ads.AdView adView;

    ReviewInfo reviewInfo;
    ReviewManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);
        firebaseHelper = new FirebaseHelper(getApplicationContext());
        kidImageView = findViewById(R.id.kidMonsterImage);
        tokenImageView = findViewById(R.id.tokenImageView);
        kidNameTextView = findViewById(R.id.myAwesomeTextView);
        tokenImageCard = findViewById(R.id.tokenImageCard);
        tokenNumberCard = findViewById(R.id.tokenNumberCard);
        tokenNumberImageView = findViewById(R.id.tokenNumberImageView);
        deleteImageButton = findViewById(R.id.delete_button);
        configActionButton();
        setupRecyclerView();
        getIntentData();
        setupAds();
    }

    private void getIntentData() {
        if (getIntent().getExtras() != null) {
            selectedKid = getIntent().getExtras().getParcelable("selected_kid");
            kidImageView.setImageResource(getApplicationContext().getResources().getIdentifier(selectedKid.getMonsterImageResourceName(), "drawable",
                    getApplicationContext().getPackageName()));
            kidNameTextView.setText(selectedKid.getKidName());
            tokenImageView.setImageResource(getApplicationContext().getResources().getIdentifier(selectedKid.getTokenImageResourceName(), "drawable",
                    getApplicationContext().getPackageName()));
            tokenNumberImageView.setImageResource(getApplicationContext().getResources().getIdentifier(selectedKid.getTokenNumberImageResourceName(), "drawable",
                    getApplicationContext().getPackageName()));
            getkidTaskz();
            setTitle(selectedKid.getKidName());
        }
    }

    private void setupAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
        okBtn.setOnClickListener(v -> {
            builder.dismiss();
            deleteKid();

        });
        cancelBtn.setOnClickListener(v -> builder.dismiss());
        builder.setView(dialogView);
        builder.show();
    }

    private void deleteKid() {

        firebaseHelper.deleteKid(selectedKid)
                .subscribe(() -> {
                    Timber.i("updateRewardImage: completed");
                    firebaseHelper.logEvent("kid_deleted");
                    firebaseHelper.deleteKidTaskzCollection(selectedKid)
                            .subscribe(() -> finish(), throwable -> {
                                // handle error
                            });

                }, throwable -> {
                    // handle error
                });

    }

    private void configActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(view -> showAddTaskDialog(KidActivity.this));

        tokenImageCard.setOnClickListener(view -> {
            Intent mIntent = new Intent(KidActivity.this, TokenzActivity.class);
            startActivityForResult(mIntent, 2);
        });

        tokenNumberCard.setOnClickListener(view -> {
            Intent mIntent = new Intent(KidActivity.this, TokenNumberActivity.class);
            startActivityForResult(mIntent, 3);
        });

        deleteImageButton.setOnClickListener(view -> showDeleteKidDialog(KidActivity.this));
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

        firebaseHelper.getkidTaskz(selectedKid).observeOn(Schedulers.io())
                //.observeOn(Schedulers.m)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<ArrayList<KidTask>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(ArrayList<KidTask> taskz) {
                        Timber.d("onNext:  " + taskz.size());
                        taskzList = taskz;

                        runOnUiThread(() -> updateRecylerView(taskz));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError: " + e.getMessage());
                    }


                });

    }

    private void Review() {
        manager = ReviewManagerFactory.create(this);
        manager.requestReviewFlow().addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                    manager.launchReviewFlow(KidActivity.this, reviewInfo).addOnFailureListener(new OnFailureListener() {
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


    private void updateRecylerView(ArrayList<KidTask> taskz) {
        taskAdapter.updateData(taskz);
        taskRecyclerView.scrollToPosition(0);

    }


    private void showAddTaskDialog(Context c) {
        final AlertDialog builder = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_task, null);
        final TextInputLayout taskNameText = dialogView.findViewById(R.id.taskname_text_input);
        taskNameText.requestFocus();
        Button okBtn = dialogView.findViewById(R.id.task_save_button);
        Button cancelBtn = dialogView.findViewById(R.id.task_cancel_button);
        final SwitchMaterial negativeReInfSwitch = dialogView.findViewById(R.id.switch_negative_ReIn);
        okBtn.setOnClickListener(v -> {
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
                ArrayList<Long> taskTokenzScore = new ArrayList<>();
                for (int i = 0; i < selectedKid.getTokenNumber(); i++) {
                    taskTokenzScore.add(0L);
                }
                newTask.setTaskTokenzScore(taskTokenzScore);
                saveTask(newTask);
                taskzList.add(newTask);
                updateRecylerView(taskzList);

                builder.dismiss();
            }


        });


        taskNameText.setOnKeyListener((view, i, keyEvent) -> {
            String kidName = String.valueOf(taskNameText.getEditText().getText());
            if (isTaskNameValid(kidName)) {
                taskNameText.setError(null); //Clear the error
            }
            return false;
        });
        cancelBtn.setOnClickListener(v -> builder.dismiss());
        builder.setView(dialogView);
        builder.show();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    private void saveTask(KidTask newTask) {

        firebaseHelper.saveKidTask(newTask, selectedKid).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<KidTask>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("continueWithTask kidzList => onError: %s", e.getMessage());
                    }

                    @Override
                    public void onSuccess(KidTask kid) {
                        runOnUiThread(() -> {
                            firebaseHelper.logEvent("task_created");

                            //   updateRecyclerView();

                        });
                    }
                });


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
                String tokenImageImageResourceName = data.getStringExtra("tokenImageImageResourceName");
                String badtokenImageImageResourceName = data.getStringExtra("badtokenImageImageResourceName");
                tokenImageView.setImageDrawable(null);
                tokenImageView.setImageResource(getApplicationContext().getResources().getIdentifier(tokenImageImageResourceName, "drawable",
                        getApplicationContext().getPackageName()));
                updateKidTokenImage(result, badToken, tokenImageImageResourceName, badtokenImageImageResourceName);
            }
            //Write your code if there's no result
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                int selectedImage = data.getIntExtra("Image", 0);
                String selectedImageResourceName = data.getStringExtra("ImageResource");
                tokenNumberImageView.setImageResource(selectedImage);
                int selectedTokenNumber = data.getIntExtra("TokenNumber", 0);
                Timber.i("onActivityResult: " + selectedTokenNumber);
                tokenNumberImageView.setImageDrawable(null);
                tokenNumberImageView.setImageResource(getApplicationContext().getResources().getIdentifier(selectedImageResourceName, "drawable",
                        getApplicationContext().getPackageName()));
                updateKidTokenNumberImage(selectedImage, selectedImageResourceName, selectedTokenNumber);

            }
            //Write your code if there's no result
        } else if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {

                getkidTaskz();
                Review();

            }

        }

    }//onActivityResult



    private void updateKidTokenImage(final int result, final int badToken, final String tokenImageImageResourceName, final String badtokenImageImageResourceName) {

        selectedKid.setBadTokenImageResourceName(badtokenImageImageResourceName);
        selectedKid.setTokenImageResourceName(tokenImageImageResourceName);
        firebaseHelper.updateKid(selectedKid)
                .subscribe(() -> {
                    Timber.i("updateKidTokenImage: completed");
                    firebaseHelper.logEvent("tokenImage_updated");
                    Review();
                }, throwable -> {
                    // handle error
                });

    }

    private void updateKidTokenNumberImage(final int newTokenNumberImage, final String selectedImageResourceName, final int selectedTokenNumber) {

        selectedKid.setTokenNumberImageResourceName(selectedImageResourceName);
        selectedKid.setTokenNumber(selectedTokenNumber);

        firebaseHelper.updateKid(selectedKid)
                .subscribe(() -> {
                    Timber.i("updateKidTokenNumberImage: completed");
                    firebaseHelper.logEvent("tokenNumber_updated");
                    Review();

                }, throwable -> {
                    // handle error
                });




    }

    @Override
    public void onTaskClick(int position) {

        taskzList = taskAdapter.getAllItems();

        Timber.i("Clicked %s", position);
        Intent intent = new Intent(this, TaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_kid", selectedKid);
        bundle.putParcelable("selected_task", taskzList.get(position));

        intent.putExtras(bundle);

        //  intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        Timber.i("onTaskClick: %s", taskzList.get(position).toString());
        // intent.putExtra("selected_task",taskzList.get(position));
        // intent.putExtra("selected_kid",selectedKid);

        startActivityForResult(intent, 4);


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
        dismissWithCheck(progressBar);
        super.onDestroy();
    }

}
