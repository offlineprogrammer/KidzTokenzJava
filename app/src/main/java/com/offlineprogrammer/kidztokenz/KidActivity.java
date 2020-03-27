package com.offlineprogrammer.kidztokenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class KidActivity extends AppCompatActivity {

    private static final String TAG = "KidActivity";
    ImageView kidImageView;
    TextView kidNameTextView;
    ImageView tokenImageView;
    CardView tokenImageCard;
    CardView tokenNumberCard;
    ImageView tokenNumberImageView;
    Kid selectedKid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);

        kidImageView = findViewById(R.id.kidMonsterImage);
        tokenImageView = findViewById(R.id.tokenImageView);
        kidNameTextView = findViewById(R.id.myAwesomeTextView);
        tokenImageCard = findViewById(R.id.tokenImageCard);
        tokenNumberCard = findViewById(R.id.tokenNumberCard);
        tokenNumberImageView = findViewById(R.id.tokenNumberImageView);

        tokenImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(KidActivity.this, TokenzActivity.class);
                startActivityForResult(mIntent, 2);

            }
        });

        tokenNumberCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(KidActivity.this, TokenNumberActivity.class);
                startActivityForResult(mIntent, 2);

            }
        });

        if (getIntent().hasExtra("selected_kid")) {
            Bundle data = getIntent().getExtras();
            selectedKid = (Kid) data.getParcelable("selected_kid");
           // Kid kid = getIntent().getParcelableExtra("selected_kid");
            Log.i(TAG, "onCreate: " + selectedKid.toString());
            Log.i(TAG, "onCreate: " + selectedKid.getTokenImage());
            Log.i(TAG, "onCreate: " + selectedKid.getFirestoreId());
            Log.i(TAG, "onCreate: " + selectedKid.getUserFirestoreId());
            Log.i(TAG, "onCreate STRING: " + selectedKid.toString());
            kidImageView.setImageResource(selectedKid.getMonsterImage());
            kidNameTextView.setText(selectedKid.getKidName());
            tokenImageView.setImageResource(selectedKid.getTokenImage());
            tokenNumberImageView.setImageResource(selectedKid.getTokenNumber());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("Image",0);
                tokenImageView.setImageResource(result);
                updateKidTokenImage(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void updateKidTokenImage(int newTokenImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference selectedKidRef = db.collection("users").document(selectedKid.getUserFirestoreId()).collection("kidz").document(selectedKid.getFirestoreId());

// Set the "isCapital" field of the city 'DC'
        selectedKidRef
                .update("tokenImage", newTokenImage)
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
