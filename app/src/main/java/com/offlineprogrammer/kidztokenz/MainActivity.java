package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

private RecyclerView recyclerView;
private KidAdapter mAdapter;
    private DatabaseReference mDatabase;

    private ArrayList<Kid> kidzList = new ArrayList<>();
    private  User m_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDeviceToken();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAdapter = new KidAdapter(kidzList);
        recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = findViewById(R.id.fab_add_kid);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddKidDialog(MainActivity.this);
            }
        });

    }

    private int pickMonster(){
        final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzMonsters);
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
                        Kid newKid = new Kid(kidName,pickMonster());
                        mAdapter.add(newKid,0);
                        saveKid(newKid);

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


                // Access a Cloud Firestore instance from your Activity
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Create a new user
                Map<String, Object> user = new HashMap<>();
                user.put("deviceToken", m_User.getDeviceToken());
                user.put("userId", "guest");
                //user.put("kidz", "");



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
        });
    }

    private void saveKid(Kid newKid){

        Map<String, Object> kidValues = newKid.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
      //  String key = mDatabase.child("\"/users/\" + m_User.getFirebaseId() + \"/kidz/\"").push().getKey();
        String sKUserUlr = "/users/" + m_User.getFirebaseId() + "/";//+ "kidz";

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newKidRef = db.collection("users").document(m_User.getFirebaseId()).collection("kidz").document();

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





    }

}
