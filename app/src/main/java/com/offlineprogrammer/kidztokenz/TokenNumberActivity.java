package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class TokenNumberActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List< TokenNumberData > mTokenNumberList;
    TokenNumberData mTokenNumberData;
    TokenNumberAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_number);
        mRecyclerView = findViewById(R.id.token_number_recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(TokenNumberActivity.this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mTokenNumberList = new ArrayList<>();

        mTokenNumberData = new TokenNumberData("One",
                R.drawable.tn1);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Two",
                R.drawable.tn2);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Three",
                R.drawable.tn3);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Four",
                R.drawable.tn4);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Five",
                R.drawable.tn5);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Six",
                R.drawable.tn6);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Seven",
                R.drawable.tn7);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Eight",
                R.drawable.tn8);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Nine",
                R.drawable.tn9);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Ten",
                R.drawable.tn10);
        mTokenNumberList.add(mTokenNumberData);


        myAdapter = new TokenNumberAdapter(TokenNumberActivity.this, mTokenNumberList);
        mRecyclerView.setAdapter(myAdapter);
    }
}
