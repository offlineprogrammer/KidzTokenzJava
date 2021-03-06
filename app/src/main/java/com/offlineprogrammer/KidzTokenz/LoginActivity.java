package com.offlineprogrammer.KidzTokenz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1001;

    GoogleSignInClient googleSignInClient;
    FirebaseHelper firebaseHelper;
    private Disposable disposable;
    private ProgressBar mLogInProgress;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogInProgress = findViewById(R.id.log_in_progress);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton.setOnClickListener(view -> {
            mLogInProgress.setVisibility(View.VISIBLE);
            signInToGoogle();
        });
        configureGoogleClient();
    }

    private void configureGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        // Initialize Firebase Auth
        firebaseHelper = new FirebaseHelper(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseHelper.firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            mLogInProgress.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            Timber.d("Currently Signed in: %s", currentUser.getEmail());
            Timber.d("onStart: %s", currentUser.getUid());
            getUserData();
        }
    }

    public void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                mLogInProgress.setVisibility(View.GONE);
                // Google Sign In failed, update UI appropriately
                Timber.tag(TAG).w(e, "Google sign in failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Timber.d("firebaseAuthWithGoogle:%s", account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseHelper.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseHelper.firebaseAuth.getCurrentUser();

                        Timber.d("signInWithCredential:success: currentUser: %s", Objects.requireNonNull(user).getEmail());
                        getUserData();
                    } else {
                        // If sign in fails, display a message to the user.
                        mLogInProgress.setVisibility(View.GONE);
                        Timber.tag(TAG).w(task.getException(), "signInWithCredential:failure");
                    }
                });
    }

        private void getUserData() {

            firebaseHelper.getUserData().observeOn(Schedulers.io())
                    //.observeOn(Schedulers.m)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new SingleObserver<User>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Timber.d("onSubscribe");
                            disposable = d;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e("onError: %s", e.getMessage());
                            getUserDataV1();
                        }

                        @Override
                        public void onSuccess(User user) {
                            Timber.d("onNext: %s", user.getUserId());
                            firebaseHelper.setUserFcmInstanceId()
                                    .subscribe(() -> {
                                        Timber.i("setUserFcmInstanceId: completed");

                                    }, throwable -> {
                                        // handle error
                                    });
                            runOnUiThread(() -> launchMainActivity(user));


                        }
                    });


        }


    private void getUserDataV1() {

        firebaseHelper.find_migrate_userV1().observeOn(Schedulers.io())
                //.observeOn(Schedulers.m)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("continueWithTask kidzList => onError: %s", e.getMessage());
                        saveUser();
                    }

                    @Override
                    public void onSuccess(User user) {
                        if (user.getKidz().size() > 0) {

                            firebaseHelper.updateKidzCollection()
                                    .subscribe(() -> {
                                        Timber.i("updateKidzCollection: completed");
                                        // handle completion
                                        runOnUiThread(() -> launchMainActivity(user));
                                    }, throwable -> {
                                        // handle error
                                    });


                        } else {
                            runOnUiThread(() -> launchMainActivity(user));

                        }


                    }
                });


    }


    private void saveUser() {
        firebaseHelper.saveUser().observeOn(Schedulers.io())
                //.observeOn(Schedulers.m)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Timber.d("onNext: %s", user.getUserId());
                        firebaseHelper.setUserFcmInstanceId()
                                .subscribe(() -> {
                                    Timber.i("setUserFcmInstanceId: completed");

                                }, throwable -> {
                                    // handle error
                                });

                        runOnUiThread(() -> launchMainActivity(user));


                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError: %s", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("onComplete");
                    }
                });
    }

    private void launchMainActivity(User user) {
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }
    }


    private void signOut() {
        // Firebase sign out
        firebaseHelper.firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    // Google Sign In failed, update UI appropriately
                    Timber.tag(TAG).w("Signed out of google");
                });
    }

}