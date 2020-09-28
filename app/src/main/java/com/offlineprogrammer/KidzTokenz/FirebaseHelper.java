package com.offlineprogrammer.KidzTokenz;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.offlineprogrammer.KidzTokenz.kid.Kid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;


public class FirebaseHelper {
    FirebaseFirestore m_db;
    FirebaseAuth firebaseAuth;
    FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "FirebaseHelper";
    Context mContext;

    KidzTokenz kidzTokenz;
    boolean userMigrated = false;



    public FirebaseHelper(Context c){
        m_db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mContext = c;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);

        kidzTokenz = (KidzTokenz) mContext;
    }

    Observable<User> saveUser() {
        return Observable.create((ObservableEmitter<User> emitter) -> {
            Date currentTime = Calendar.getInstance().getTime();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            kidzTokenz.setUser(new User(currentUser.getUid(),currentUser.getEmail(), currentTime));

            Map<String, Object> user = kidzTokenz.getUser().toMap();

            m_db.collection("users").document(currentUser.getUid())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
//                            listenToUserDocument();
                            emitter.onNext(kidzTokenz.getUser());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            emitter.onError(e);
                        }
                    });
        });
    }




    Observable<User> getUserData() {
        return Observable.create((ObservableEmitter<User> emitter) -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            final DocumentReference docRef = m_db.collection("users2.0").document(currentUser.getUid());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "V2.0 Current data: " + snapshot.getData());
                        kidzTokenz.setUser(snapshot.toObject(User.class));
                        emitter.onNext(kidzTokenz.getUser());

                    } else {
                        Log.d(TAG, "V2.0 Current data: null");
                        emitter.onError(new Exception("V2.0 o data found"));
                    }
                }
            });
        });
    }


    public Single<User> find_migrate_userV1() {

        return Single.create(emitter -> {

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            ArrayList<Kid> kidzList = new ArrayList<>();
            Date currentTime = Calendar.getInstance().getTime();

            kidzTokenz.setUser(new User(currentUser.getUid(),currentUser.getEmail(), currentTime));


            m_db.collection("users")
                    .whereEqualTo("userId", currentUser.getEmail())
                    .get()
                    .continueWithTask(new Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>() {
                        @Override
                        public Task<List<QuerySnapshot>> then(@NonNull Task<QuerySnapshot> task) {
                            List<Task<QuerySnapshot>> tasks = new ArrayList<Task<QuerySnapshot>>();
                            for (DocumentSnapshot ds : task.getResult()) {
                                tasks.add(ds.getReference().collection("kidz").get());
                                Log.d(TAG, " continueWithTask tasks => " + tasks.toString());
                            }

                            return Tasks.whenAllSuccess(tasks);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                            if (task.isSuccessful()) {

                                List<QuerySnapshot> list = task.getResult();
                                for (QuerySnapshot qs : list) {
                                    for (DocumentSnapshot ds : qs) {
                                        Log.d(TAG, " continueWithTask => " + ds.getData());
                                        Kid myKid = ds.toObject(Kid.class);
                                        kidzList.add(myKid);
                                    }


                                }
                                Log.d(TAG, " continueWithTask kidzList => " + kidzList);
                                kidzTokenz.getUser().setKidz(kidzList);
                                Map<String, Object> user = kidzTokenz.getUser().toMap();



                                m_db.collection("users2.0").document(currentUser.getUid())
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "continueWithTask kidzList => successfully written!");

                                                Log.d(TAG, "continueWithTask kidzList => user2.0 is updated for    " + user.toString() );
                                                Log.d(TAG, "continueWithTask kidzList => kidzTokenz.getUser()    " + kidzTokenz.getUser().toString() );
//                            listenToUserDocument();

                                                emitter.onSuccess(kidzTokenz.getUser());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                                emitter.onError(e);
                                            }
                                        });
                            } else {
                                emitter.onError(new Exception("No data found"));
                            }
                        }
                    });

        });

    }











}
