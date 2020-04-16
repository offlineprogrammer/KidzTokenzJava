package com.offlineprogrammer.KidzTokenz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.offlineprogrammer.KidzTokenz.kid.KidGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class TokenzActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List< TokenData > mTokenList;
    TokenData mTokenData;
    TokenAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokenz);
         mRecyclerView = findViewById(R.id.tokenz_recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(TokenzActivity.this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);
        mRecyclerView.addItemDecoration(new KidGridItemDecoration(largePadding, smallPadding));


        mTokenList = new ArrayList<>();
        mTokenData = new TokenData("bunny",
                R.drawable.bunny);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("elephant",
                R.drawable.elephant);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("face",
                R.drawable.face);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("giraffe",
                R.drawable.giraffe);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("leopard",
                R.drawable.leopard);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("monkey",
                R.drawable.monkey);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("rocket",
                R.drawable.rocket);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("sheep",
                R.drawable.sheep);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("star",
                R.drawable.star);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("teddybear",
                R.drawable.teddybear);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("train",
                R.drawable.train);
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("triceratops",
                R.drawable.triceratops);
        mTokenList.add(mTokenData);

         myAdapter = new TokenAdapter(TokenzActivity.this, mTokenList);
        mRecyclerView.setAdapter(myAdapter);

    }
}
