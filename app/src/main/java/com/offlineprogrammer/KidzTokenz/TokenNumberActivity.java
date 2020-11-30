package com.offlineprogrammer.KidzTokenz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.kid.KidGridItemDecoration;

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
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);
        mRecyclerView.addItemDecoration(new KidGridItemDecoration(largePadding, smallPadding));

        mTokenNumberList = new ArrayList<>();

        mTokenNumberData = new TokenNumberData("One",
                R.drawable.tn1, getResources().getResourceEntryName(R.drawable.tn1), 1);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Two",
                R.drawable.tn2, getResources().getResourceEntryName(R.drawable.tn2), 2);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Three",
                R.drawable.tn3, getResources().getResourceEntryName(R.drawable.tn3), 3);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Four",
                R.drawable.tn4, getResources().getResourceEntryName(R.drawable.tn4), 4);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Five",
                R.drawable.tn5, getResources().getResourceEntryName(R.drawable.tn5), 5);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Six",
                R.drawable.tn6, getResources().getResourceEntryName(R.drawable.tn6), 6);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Seven",
                R.drawable.tn7, getResources().getResourceEntryName(R.drawable.tn7), 7);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Eight",
                R.drawable.tn8, getResources().getResourceEntryName(R.drawable.tn8), 8);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Nine",
                R.drawable.tn9, getResources().getResourceEntryName(R.drawable.tn9), 9);
        mTokenNumberList.add(mTokenNumberData);
        mTokenNumberData = new TokenNumberData("Ten",
                R.drawable.tn10, getResources().getResourceEntryName(R.drawable.tn10), 10);
        mTokenNumberList.add(mTokenNumberData);


        myAdapter = new TokenNumberAdapter(TokenNumberActivity.this, mTokenNumberList);
        mRecyclerView.setAdapter(myAdapter);
    }
}
