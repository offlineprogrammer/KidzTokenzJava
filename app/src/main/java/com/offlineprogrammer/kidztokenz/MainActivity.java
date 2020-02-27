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
       // kidzList = findViewById(R.id.kidz_list);

        RecyclerView recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    private List<Kid> generateKidList() {
        List<Kid> kidzList = new ArrayList<>();

        for (int i = 0; i < 100; i++)
            kidzList.add(new Kid(String.format(Locale.US, "This is item %d", i)));

        return kidzList;
    }
}
