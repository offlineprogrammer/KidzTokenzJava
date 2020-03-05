package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KidAdapter adapter = new KidAdapter(generateKidList());


        RecyclerView recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    private ArrayList<Kid> generateKidList() {
        ArrayList<Kid> kidzList = new ArrayList<>();

        for (int i = 0; i < 10; i++)

            kidzList.add(new Kid(String.format(Locale.US, " item %d", i),R.drawable.m1));




        return kidzList;
    }
}
