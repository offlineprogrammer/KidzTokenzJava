package com.offlineprogrammer.kidztokenz;

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

public class KidActivity extends AppCompatActivity {

    private static final String TAG = "KidActivity";
    ImageView kidImageView;
    TextView kidNameTextView;
    ImageView tokenImageView;
    CardView tokenImageCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);

        kidImageView = findViewById(R.id.kidMonsterImage);
        tokenImageView = findViewById(R.id.tokenImageView);
        kidNameTextView = findViewById(R.id.myAwesomeTextView);
        tokenImageCard = findViewById(R.id.tokenImageCard);

        tokenImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(KidActivity.this, TokenzActivity.class);
                startActivityForResult(mIntent, 2);

            }
        });

        if (getIntent().hasExtra("selected_kid")) {
            Bundle data = getIntent().getExtras();
            Kid kid = (Kid) data.getParcelable("selected_kid");
           // Kid kid = getIntent().getParcelableExtra("selected_kid");
            Log.i(TAG, "onCreate: " + kid.toMap().toString());
            Log.i(TAG, "onCreate: " + kid.getTokenImage());
            Log.i(TAG, "onCreate: " + kid.getFirestoreId());
            Log.i(TAG, "onCreate: " + kid.getUserFirestoreId());
            Log.i(TAG, "onCreate STRING: " + kid.toString());
            kidImageView.setImageResource(kid.getMonsterImage());
            kidNameTextView.setText(kid.getKidName());
            tokenImageView.setImageResource(kid.getTokenImage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                int result=data.getIntExtra("Image",0);
                tokenImageView.setImageResource(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}
