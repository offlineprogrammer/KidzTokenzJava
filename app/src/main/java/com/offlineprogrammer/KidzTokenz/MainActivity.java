package com.offlineprogrammer.KidzTokenz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kobakei.ratethisapp.RateThisApp;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.kid.KidAdapter;
import com.offlineprogrammer.KidzTokenz.kid.KidGridItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnKidListener {

    private RecyclerView recyclerView;
    private KidAdapter mAdapter;
    private PublisherAdView adView;
    ProgressDialog progressBar;



    private ArrayList<Kid> kidzList = new ArrayList<>();
    private  User m_User;
    private Boolean bFoundData = false;

    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupProgressBar();

        setupRecyclerView();
        getDeviceToken();

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
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();
        configureRateThisApp();


    }

    private void configureRateThisApp() {
        // Custom condition: 3 days and 2 launches
        RateThisApp.Config config = new RateThisApp.Config(3, 10);
        config.setMessage(R.string.ktz_app_rate_msg);
        RateThisApp.init(config);

        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // If the condition is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);

        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                RateThisApp.stopRateDialog(MainActivity.this);
                Log.i(TAG, "onYesClicked ");
            }

            @Override
            public void onNoClicked() {
                Log.i(TAG, "onNoClicked ");

            }

            @Override
            public void onCancelClicked() {
                Log.i(TAG, "onCancelClicked ");

            }
        });
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


    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Signed out of google");
                    }
                });
    }


    private void setupRecyclerView() {
        mAdapter = new KidAdapter(kidzList,this);
        recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);
        recyclerView.addItemDecoration(new KidGridItemDecoration(largePadding, smallPadding));

    }

    private int pickMonster(){
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
        /*final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzTokenNumbers);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        return imgs.getResourceId(rndInt, 0);*/
        return R.drawable.tn5;
    }

    private void showAddKidDialog(Context c) {


        //View dialogLayout = Inflater.class.inflate(R.layout.alert_dialog_add_kid, null);
        final AlertDialog builder = new AlertDialog.Builder(c).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_kid, null);
        final TextInputLayout kidNameText = (TextInputLayout) dialogView.findViewById(R.id.kidname_text_input);


        kidNameText.requestFocus();

        Button okBtn= dialogView.findViewById(R.id.kidname_save_button);
        Button cancelBtn = dialogView.findViewById(R.id.kidname_cancel_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                    newKid = saveKid(newKid);
                    Log.i(TAG, "onClick UserFireStore : " + newKid.getUserFirestoreId());
                    Log.i(TAG, "onClick KidFireStore : " + newKid.getFirestoreId());
                    mAdapter.add(newKid, 0);
                    recyclerView.scrollToPosition(0);
                    builder.dismiss();
                }


            }
        });

        kidNameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String kidName = String.valueOf(kidNameText.getEditText().getText());
                if (isKidNameValid(kidName)) {
                    kidNameText.setError(null); //Clear the error
                }
                return false;
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();

                // btnAdd1 has been clicked

            }
        });
        builder.setView(dialogView);
        builder.show();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

     //   kidNameText.setMaxLines(1);
     //   kidNameText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL );
     //   kidNameText.setHint("Kid Name");
     //   kidNameText.requestFocus();
    }

    private boolean isKidNameValid(String kidName) {
         return kidName != null && kidName.length() >= 2;
    }

    private void getDeviceToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                m_User = new User(deviceToken);
                getUserData(m_User.getDeviceToken());

            }
        });
    }

    private void saveUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user
        Map<String, Object> user = new HashMap<>();
        user.put("deviceToken", m_User.getDeviceToken());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            user.put("userId", currentUser.getEmail());

        } else {

        user.put("userId", "guest");
        }
// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        m_User.setFirebaseId(documentReference.getId());
                        Log.d("SavingData", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SavingData", "Error adding document", e);
                    }
                });
    }

    private void configActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab_add_kid);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddKidDialog(MainActivity.this);
            }
        });

/*        FloatingActionButton fab_signout = findViewById(R.id.fab_signout);
        fab_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });*/
    }

    private Kid saveKid(Kid newKid){

        kidzList.add(newKid);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newKidRef = db.collection("users").document(m_User.getFirebaseId()).collection("kidz").document();
        newKid.setFirestoreId(newKidRef.getId());
        newKid.setUserFirestoreId(m_User.getFirebaseId());
        //newKid.setTaskzList(new ArrayList<KidTask>());
        Map<String, Object> kidValues = newKid.toMap();

        newKidRef.set(kidValues, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Add Kid", "DocumentSnapshot successfully written!");
                        dismissProgressBar();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Add Kid", "Error writing document", e);
                        dismissProgressBar();
                    }
                });

        return newKid;
    }

    private void getUserData(String deviceToken) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("deviceToken", deviceToken)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                Log.d("Got Data", document.getId() + " => " + document.getData());
                                    ArrayList<Kid> list = (ArrayList<Kid>) document.get("kidz");
                                    String userId = (String) document.get("userId");
                                    m_User=document.toObject(User.class);
                                    m_User.setFirebaseId(document.getId());
                                    getKidzData(m_User.getFirebaseId());
                                    if (userId.equals("guest")) {
                                        updateUserId();
                                    }

                                } else {
                                    saveUser();
                                }
                            }

                            if (m_User.getFirebaseId()==null) {
                                saveUser();
                            }

                            dismissProgressBar();
                            configActionButton();
                        } else {
                            saveUser();
                            Log.d("Got Date", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference currentUserRef = db.collection("users").document(m_User.getFirebaseId());
            currentUserRef
                    .update("userId", currentUser.getEmail())
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
    }


    private void getKidzData(String UserFiebaseId) {
        // final Boolean bFoundData ;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(UserFiebaseId).collection("kidz")
                .orderBy("createdDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d("Got Data", document.getId() + " => " + document.getData());
                                    Kid myKid = document.toObject(Kid.class);
                                    kidzList.add(myKid);
                                    mAdapter.add(myKid,0);
                                    recyclerView.scrollToPosition(0);
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

    @Override
    public void onKidClick(int position) {
        kidzList = mAdapter.getAllItems();
        Log.i(TAG, "Clicked " + position);
        Intent intent = new Intent(this, KidActivity.class);
        Log.i(TAG, "onKidClick: " + kidzList.get(position).toString());
        intent.putExtra("selected_kid",kidzList.get(position));
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
