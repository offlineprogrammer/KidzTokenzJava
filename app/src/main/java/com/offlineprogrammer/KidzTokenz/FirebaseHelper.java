package com.offlineprogrammer.KidzTokenz;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;


public class FirebaseHelper {
    public static final String USERS_COLLECTION = "users2.0";
    FirebaseFirestore m_db;
    FirebaseAuth firebaseAuth;
    FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "FirebaseHelper";
    Context mContext;
    KidzTokenz kidzTokenz;


    public FirebaseHelper(Context c) {
        m_db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mContext = c;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        kidzTokenz = (KidzTokenz) mContext;
    }

    public void logEvent(String event_name) {
        mFirebaseAnalytics.logEvent(event_name, null);
    }


    Observable<User> saveUser() {
        return Observable.create((ObservableEmitter<User> emitter) -> {
            Date currentTime = Calendar.getInstance().getTime();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            kidzTokenz.setUser(new User(currentUser.getUid(), currentUser.getEmail(), currentTime));
            Map<String, Object> user = kidzTokenz.getUser().toMap();
            m_db.collection("users").document(currentUser.getUid())
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        emitter.onNext(kidzTokenz.getUser());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error writing document", e);
                        emitter.onError(e);
                    });
        });
    }


    Observable<User> oldgetUserData() {
        return Observable.create((ObservableEmitter<User> emitter) -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            final DocumentReference docRef = m_db.collection(USERS_COLLECTION).document(currentUser.getUid());
            docRef.addSnapshotListener((snapshot, e) -> {
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
            });
        });
    }


    Single<User> getUserData() {
        return Single.create(emitter -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            final DocumentReference docRef = m_db.collection(USERS_COLLECTION).document(currentUser.getUid());
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "V2.0 Current data: " + snapshot.getData());
                    kidzTokenz.setUser(snapshot.toObject(User.class));
                    emitter.onSuccess(kidzTokenz.getUser());
                } else {
                    Log.d(TAG, "V2.0 Current data: null");
                    emitter.onError(new Exception("V2.0 o data found"));
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
                    .continueWithTask((Continuation<QuerySnapshot, Task<List<QuerySnapshot>>>) task -> {
                        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                        for (DocumentSnapshot ds : task.getResult()) {
                            tasks.add(ds.getReference().collection("kidz").get());
                            Log.d(TAG, " continueWithTask tasks => " + tasks.toString());
                        }
                        return Tasks.whenAllSuccess(tasks);
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<QuerySnapshot> list = task.getResult();
                            for (QuerySnapshot qs : list) {
                                for (DocumentSnapshot ds : qs) {
                                    Log.d(TAG, " continueWithTask => " + ds.getData());
                                    Kid myKid = ds.toObject(Kid.class);
                                    myKid.setKidUUID();
                                    kidzList.add(myKid);
                                }
                            }
                            Log.d(TAG, " continueWithTask kidzList => " + kidzList);
                            kidzTokenz.getUser().setKidz(kidzList);
                            Map<String, Object> user = kidzTokenz.getUser().toMap();
                            m_db.collection(USERS_COLLECTION).document(currentUser.getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> emitter.onSuccess(kidzTokenz.getUser()))
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error writing document", e);
                                        emitter.onError(e);
                                    });
                        } else {
                            emitter.onError(new Exception("No data found"));
                        }
                    });
        });
    }

    public Completable find_migrate_TaskzV1(Kid selectedKid) {
        return Completable.create(emitter -> {
            ArrayList<KidTask> kidTaskzList = new ArrayList<>();
            DocumentReference selectedKidRef = m_db.collection("users").document(selectedKid.getUserFirestoreId())
                    .collection("kidz").document(selectedKid.getFirestoreId());
            selectedKidRef.collection("taskz")
                    .orderBy("createdDate", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d("Got Task Data", document.getId() + " => " + document.getData());
                                    KidTask myKidTask = document.toObject(KidTask.class);
                                    myKidTask.setKidTaskUUID();
                                    kidTaskzList.add(myKidTask);
                                    writeBatchKidTaskz(kidTaskzList, selectedKid, emitter);
                                }
                            }

                        } else {
                            Log.d("Got Date", "Error getting documents: ", task.getException());
                            emitter.onError(task.getException());
                        }
                    });

        });

    }

    private void writeBatchKidTaskz(ArrayList<KidTask> kidTaskzList, Kid selectedKid, CompletableEmitter emitter) {
        WriteBatch batch = m_db.batch();
        for (KidTask oKidTask : kidTaskzList) {
            Map<String, Object> kidTaskValues = oKidTask.toMap();
            DocumentReference kidTaskRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId())
                    .collection("kidzTokenz").document(selectedKid.getKidUUID())
                    .collection("kidTaskz").document(oKidTask.getKidTaskUUID());
            batch.set(kidTaskRef, kidTaskValues);
        }
        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "updateKidzCollection successfully written!");
                emitter.onComplete();
            } else {
                Log.w(TAG, "updateKidzCollection Error writing document ", task.getException());
                emitter.onError(task.getException());
            }
        });
    }


    public Completable updateKidzCollection(Kid newKid) {
        return Completable.create(emitter -> {
            Map<String, Object> kidValues = newKid.toMap();
            m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId()).collection("kidzTokenz").document(newKid.getKidUUID())
                    .set(kidValues)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                            listenToUserDocument();
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error writing document", e);
                        emitter.onError(e);
                    });
        });
    }


    public Completable updateKidzCollection() {
        return Completable.create(emitter -> {

            WriteBatch batch = m_db.batch();

            for (Kid oKid : kidzTokenz.getUser().getKidz()) {
                Map<String, Object> kidValues = oKid.toMap();

                DocumentReference kidRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId()).collection("kidzTokenz").document(oKid.getKidUUID());
                batch.set(kidRef, kidValues);


            }

            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "updateKidzCollection successfully written!");
//                            listenToUserDocument();
                    emitter.onComplete();
                } else {
                    Log.w(TAG, "updateKidzCollection Error writing document ", task.getException());
                    emitter.onError(task.getException());

                }
                // ...
            });


        });
    }


    public Single<Kid> saveKid(Kid newKid) {
        return Single.create((SingleEmitter<Kid> emitter) -> {
            kidzTokenz.getUser().getKidz().add(0, newKid);
            DocumentReference newKidRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId());//.collection("kidz").document();
            newKidRef.update("kidz", kidzTokenz.getUser().getKidz())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Add Kid", "DocumentSnapshot successfully written!");
                        emitter.onSuccess(newKid);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Add Kid", "Error writing document", e);
                        emitter.onError(e);
                    });

        });
    }


    public Single<ArrayList<KidTask>> getkidTaskz(Kid selectedKid) {

        ArrayList<KidTask> taskzList = new ArrayList<>();

        return Single.create((SingleEmitter<ArrayList<KidTask>> emitter) -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            final DocumentReference docRef = m_db.collection(USERS_COLLECTION).document(currentUser.getUid())
                    .collection("kidzTokenz").document(selectedKid.getKidUUID());
            docRef.collection("kidTaskz")
                    .orderBy("createdDate", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d("Got Task Data", document.getId() + " => " + document.getData());
                                    KidTask taskz = document.toObject(KidTask.class);
                                    taskzList.add(taskz);
                                }
                            }
                            emitter.onSuccess(taskzList);
                        } else {
                            Log.d("Got Date", "Error getting documents: ", task.getException());
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

    private Kid findKidByUUID(String sKidUUID) {

        return kidzTokenz.getUser().getKidz().stream().filter(
                oKid -> sKidUUID.equals(oKid.getKidUUID())).findFirst().orElse(null);


    }

    private int findKidIndexByUUID(String sKidUUID) {

        AtomicInteger position = new AtomicInteger(-1);

        Kid firstNotHiddenItem = kidzTokenz.getUser().getKidz().stream()
                .peek(x -> position.incrementAndGet())  // increment every element encounter
                .filter(oKid -> sKidUUID.equals(oKid.getKidUUID()))
                .findFirst()
                .get();

        return position.get(); // 2


    }


    public Completable deleteKid(Kid selectedKid) {
        return Completable.create(emitter -> {
            kidzTokenz.getUser().getKidz().remove(findKidByUUID(selectedKid.getKidUUID()));
            DocumentReference newKidRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId());//.collection("kidz").document();
            newKidRef.update("kidz", kidzTokenz.getUser().getKidz())
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "DocumentSnapshot successfully deleted!");

                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Error updating document", e);
                        emitter.onError(e);
                    });


        });
    }

    public Completable deleteKidTaskzCollection(Kid selectedKid) {
        return Completable.create(emitter -> {
            DocumentReference selectedKidRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId())
                    .collection("kidzTokenz").document(selectedKid.getKidUUID());
            selectedKidRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "DocumentSnapshot successfully deleted!");
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Error updating document", e);
                        emitter.onError(e);
                    });


        });
    }

    public Completable updateTaskTokenzScore(KidTask selectedTask, Kid selectedKid) {
        return Completable.create(emitter -> {
            DocumentReference selectedTaskRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId())
                    .collection("kidzTokenz").document(selectedKid.getKidUUID())
                    .collection("kidTaskz").document(selectedTask.getKidTaskUUID());
            selectedTaskRef.update("taskTokenzScore", selectedTask.getTaskTokenzScore())
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "DocumentSnapshot successfully deleted!");
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Error updating document", e);
                        emitter.onError(e);
                    });


        });
    }


    public Single<KidTask> saveKidTask(KidTask newTask, Kid selectedKid) {
        return Single.create((SingleEmitter<KidTask> emitter) -> {
            Map<String, Object> kidTaskValues = newTask.toMap();
            m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId()).collection("kidzTokenz").document(selectedKid.getKidUUID())
                    .collection("kidTaskz").document(newTask.getKidTaskUUID())
                    .set(kidTaskValues)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                            listenToUserDocument();
                        emitter.onSuccess(newTask);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error writing document", e);
                        emitter.onError(e);
                    });
        });
    }

    public Completable updateKidSchema(int position) {
        return Completable.create(emitter -> {

            Kid selectedKid = kidzTokenz.getUser().getKidz().get(position);

            selectedKid.setKidSchema(Constants.V2SCHEMA);

            kidzTokenz.getUser().getKidz().set(position, selectedKid);

            DocumentReference newKidRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId());//.collection("kidz").document();
            newKidRef.update("kidz", kidzTokenz.getUser().getKidz())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Add Kid", "DocumentSnapshot successfully written!");
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Add Kid", "Error writing document", e);
                        emitter.onError(e);
                    });
        });
    }


    public Completable updateKid(Kid selectedKid) {
        return Completable.create(emitter -> {
            int position = kidzTokenz.getUser().getKidz().indexOf((findKidByUUID(selectedKid.getKidUUID())));
            Kid requiredKid = findKidByUUID(selectedKid.getKidUUID());
            requiredKid.setTokenNumberImageResourceName(selectedKid.getTokenNumberImageResourceName());
            requiredKid.setTokenNumber(selectedKid.getTokenNumber());
            requiredKid.setBadTokenImageResourceName(selectedKid.getBadTokenImageResourceName());
            requiredKid.setTokenImageResourceName(selectedKid.getTokenImageResourceName());
            kidzTokenz.getUser().getKidz().set(position, requiredKid);
            DocumentReference newKidRef = m_db.collection(USERS_COLLECTION).document(kidzTokenz.getUser().getUserId());//.collection("kidz").document();
            newKidRef.update("kidz", kidzTokenz.getUser().getKidz())
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "DocumentSnapshot successfully deleted!");

                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.i(TAG, "Error updating document", e);
                        emitter.onError(e);
                    });


        });
    }


}
