package com.offlineprogrammer.kidztokenz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnKidListener {

private RecyclerView recyclerView;
private KidAdapter mAdapter;


    private ArrayList<Kid> kidzList = new ArrayList<>();
    private  User m_User;
    private Boolean bFoundData = false;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
        getDeviceToken();
    }

    private void setupRecyclerView() {
        mAdapter = new KidAdapter(kidzList,this);
        recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private int pickMonster(){
        final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzMonsters);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        return imgs.getResourceId(rndInt, 0);
    }

    private int pickTokenImage(){
        final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzTokenImages);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        return imgs.getResourceId(rndInt, 0);
    }

    private int pickTokenNumber(){
        final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzTokenNumbers);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        return imgs.getResourceId(rndInt, 0);
    }

    private void showAddKidDialog(Context c) {
        final EditText kidNameText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new kid")
                .setMessage("Enter the kid name")
                .setView(kidNameText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kidName = String.valueOf(kidNameText.getText());
                        Date currentTime = Calendar.getInstance().getTime();
                        Kid newKid = new Kid(kidName,pickMonster(),currentTime, pickTokenImage(),pickTokenNumber());
                        newKid = saveKid(newKid);
                        Log.i(TAG, "onClick UserFireStore : " + newKid.getUserFirestoreId());
                        Log.i(TAG, "onClick KidFireStore : " + newKid.getFirestoreId());
                        mAdapter.add(newKid,0);
                        recyclerView.scrollToPosition(0);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        kidNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                kidNameText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(kidNameText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        kidNameText.setMaxLines(1);
        kidNameText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL );
        kidNameText.setHint("Kid Name");
        kidNameText.requestFocus();
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
        user.put("userId", "guest");
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
    }

    private Kid saveKid(Kid newKid){

        kidzList.add(newKid);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newKidRef = db.collection("users").document(m_User.getFirebaseId()).collection("kidz").document();
        newKid.setFirestoreId(newKidRef.getId());
        newKid.setUserFirestoreId(m_User.getFirebaseId());
        Map<String, Object> kidValues = newKid.toMap();

        newKidRef.set(kidValues, SetOptions.merge())
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

        return newKid;
    }

    private void getUserData(String deviceToken) {
       // final Boolean bFoundData ;
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

                                m_User=document.toObject(User.class);
                                m_User.setFirebaseId(document.getId());

                                    getKidzData(m_User.getFirebaseId());
                                } else {
                                    saveUser();
                                }
                            }


                            configActionButton();
                        } else {
                            saveUser();
                            Log.d("Got Date", "Error getting documents: ", task.getException());
                        }
                    }
                });

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
      //  kidzList.get(position);

        kidzList = mAdapter.getAllItems();

        Log.i(TAG, "Clicked " + position);
        Intent intent = new Intent(this, KidActivity.class);
        Log.i(TAG, "onKidClick: " + kidzList.get(position).toString());
        intent.putExtra("selected_kid",kidzList.get(position));
        startActivity(intent);



    }
}
