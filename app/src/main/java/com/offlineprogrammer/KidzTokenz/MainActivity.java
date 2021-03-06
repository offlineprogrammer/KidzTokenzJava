package com.offlineprogrammer.KidzTokenz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.kid.KidAdapter;
import com.offlineprogrammer.KidzTokenz.kid.KidGridItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity implements OnKidListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private RecyclerView recyclerView;
    GoogleSignInClient googleSignInClient;
    private com.google.android.gms.ads.AdView adView;
    ProgressDialog progressBar;
    FirebaseHelper firebaseHelper;
    private KidAdapter kidAdapter;
    private Disposable disposable;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseHelper = new FirebaseHelper(getApplicationContext());
        setupProgressBar();
        setupRecyclerView();
        setupAds();
        configActionButton();

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





    private void setupRecyclerView() {
        try {

            kidAdapter = new KidAdapter(firebaseHelper.kidzTokenz.getUser().getKidz(), this);
            recyclerView = findViewById(R.id.kidz_recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(kidAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);// getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing);
            int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);
            recyclerView.addItemDecoration(new KidGridItemDecoration(largePadding, smallPadding));
            dismissProgressBar();


        } catch (Exception e) {
            signOut();
        }

    }

    private void signOut() {
        // Firebase sign out
        firebaseHelper.firebaseAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    // Google Sign In failed, update UI appropriately
                    Timber.tag(TAG).w("Signed out of google");
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                });

    }


    private int pickMonster() {
        final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzMonsters);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        return imgs.getResourceId(rndInt, 0);
    }

    private  ArrayList<Integer> pickTokenImage(){
        ArrayList<Integer> tokenImgsList = new ArrayList<>();
        final TypedArray tokenImgs;
        final TypedArray badtokenImgs;
        tokenImgs = getResources().obtainTypedArray(R.array.kidzTokenImages);
        badtokenImgs = getResources().obtainTypedArray(R.array.kidzBadTokenImages);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(tokenImgs.length());
        tokenImgsList.add(tokenImgs.getResourceId(rndInt, 0));
        tokenImgsList.add(badtokenImgs.getResourceId(rndInt, 0));
        return tokenImgsList;
    }

    private int pickTokenNumber(){
        return R.drawable.tn5;
    }

    private void showAddKidDialog(Context c) {

        final AlertDialog builder = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_kid, null);
        final TextInputLayout kidNameText = dialogView.findViewById(R.id.kidname_text_input);
        kidNameText.requestFocus();
        Button okBtn = dialogView.findViewById(R.id.kidname_save_button);
        Button cancelBtn = dialogView.findViewById(R.id.kidname_cancel_button);
        okBtn.setOnClickListener(v -> {
            String kidName = String.valueOf(kidNameText.getEditText().getText());
            if (!isKidNameValid(kidName)) {
                kidNameText.setError(getString(R.string.kid_error_name));
            } else {
                kidNameText.setError(null);
                Date currentTime = Calendar.getInstance().getTime();
                ArrayList<Integer> tokenImgsList = pickTokenImage();
                int monsterImage = pickMonster();
                String monsterImageResourceName = getResources().getResourceEntryName(monsterImage);
                int tokenNumberImage = pickTokenNumber();
                Kid newKid = new Kid(kidName,
                        monsterImage,
                        monsterImageResourceName,
                        currentTime,
                        tokenImgsList.get(0),
                        getResources().getResourceEntryName(tokenImgsList.get(0)),
                        tokenImgsList.get(1),
                        getResources().getResourceEntryName(tokenImgsList.get(1)),
                        tokenNumberImage,
                        getResources().getResourceEntryName(tokenNumberImage),
                        5);
                setupProgressBar();
                saveKid(newKid);
                builder.dismiss();
            }
        });

        kidNameText.setOnKeyListener((view, i, keyEvent) -> {
            String kidName = String.valueOf(Objects.requireNonNull(kidNameText.getEditText()).getText());
            if (isKidNameValid(kidName)) {
                kidNameText.setError(null); //Clear the error
            }
            return false;
        });
        cancelBtn.setOnClickListener(v -> builder.dismiss());
        builder.setView(dialogView);
        builder.show();
        Objects.requireNonNull(builder.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private boolean isKidNameValid(String kidName) {
         return kidName != null && kidName.length() >= 2;
    }

    private void configActionButton() {
        // throw new RuntimeException("Test Crash"); // Force a crash
        FloatingActionButton fab = findViewById(R.id.fab_add_kid);
        fab.setOnClickListener(view -> showAddKidDialog(MainActivity.this));
    }

    private void saveKid(Kid newKid) {

        firebaseHelper.saveKid(newKid).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<Kid>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onError(Throwable e) {
                        Timber.e("continueWithTask kidzList => onError: %s", e.getMessage());
                    }

                    @Override
                    public void onSuccess(Kid kid) {
                        runOnUiThread(() -> {
                            firebaseHelper.logEvent("kid_created");
                            updateRecyclerView();
                            firebaseHelper.updateKidzCollection(kid)
                                    .subscribe(() -> {
                                        Timber.i("updateKidzCollection: completed");
                                        // handle completion
                                    }, throwable -> {
                                        // handle error
                                    });
                        });
                    }
                });

    }

    private void updateRecyclerView() {
        kidAdapter = null;
        kidAdapter = new KidAdapter(firebaseHelper.kidzTokenz.getUser().getKidz(), this);
        recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setAdapter(kidAdapter);
        dismissProgressBar();
    }


    @Override
    public void onKidClick(int position) {
        Kid selectedKid = firebaseHelper.kidzTokenz.getUser().getKidz().get(position);
        Intent intent = new Intent(this, KidActivity.class);
        Timber.i("onKidClick: %s", selectedKid);
        intent.putExtra("selected_kid", selectedKid);
        if (selectedKid.getKidSchema().equals(Constants.V1SCHEMA)) {
            Timber.d("onKidClick: find_migrate_TaskzV1 ");
            firebaseHelper.find_migrate_TaskzV1(selectedKid)
                    .subscribe(() -> {
                        Timber.i("find_migrate_TaskzV1: completed");
                        firebaseHelper.updateKidSchema(position)
                                .subscribe(() -> {
                                    Timber.i("updateKidSchema: completed");
                                    startActivity(intent);

                                }, throwable -> {
                                    // handle error
                                });
                    }, throwable -> {
                        // handle error
                    });

        } else {
            startActivity(intent);
        }
    }

    private void updateKidSchema(int position) {
        firebaseHelper.updateKidSchema(position)
                .subscribe(() -> Timber.i("updateKidSchema: completed"), throwable -> {
                    // handle error
                });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
    }

    /**
     * Called when leaving the activity
     */
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
