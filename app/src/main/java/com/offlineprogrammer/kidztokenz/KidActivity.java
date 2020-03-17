package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class KidActivity extends AppCompatActivity {

    private static final String TAG = "KidActivity";
    ImageView kidImageView;
    TextView kidNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);

        kidImageView = findViewById(R.id.kidMonsterImage);
       kidNameTextView = findViewById(R.id.myAwesomeTextView);

        if(getIntent().hasExtra("selected_kid")){
            Kid kid = getIntent().getParcelableExtra("selected_kid");
            Log.i(TAG, "onCreate: " + kid.getKidName());
            kidImageView.setImageResource(kid.getMonsterImage());
           kidNameTextView.setText(kid.getKidName());

            CardView cardView = findViewById(R.id.kidCard);
            cardView.setUseCompatPadding(true);
            cardView.setContentPadding(30, 30, 30, 0);
            cardView.setPreventCornerOverlap(true);
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setCardElevation(2.1f);
            cardView.setRadius(0);
            cardView.setMaxCardElevation(3f);

        }
    }
}
