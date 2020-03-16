package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class KidActivity extends AppCompatActivity {

    private static final String TAG = "KidActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);

        if(getIntent().hasExtra("selected_kid")){
            Kid kid = getIntent().getParcelableExtra("selected_kid");
            Log.i(TAG, "onCreate: " + kid.getKidName());

        }
    }
}
