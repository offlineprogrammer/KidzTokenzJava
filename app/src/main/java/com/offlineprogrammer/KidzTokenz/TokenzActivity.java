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
                R.drawable.bunny,
                R.drawable.badbunny, getResources().getResourceEntryName(R.drawable.bunny),
                getResources().getResourceEntryName(R.drawable.badbunny));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("elephant",
                R.drawable.elephant,
                R.drawable.badelephant, getResources().getResourceEntryName(R.drawable.elephant),
                getResources().getResourceEntryName(R.drawable.badelephant));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("face",
                R.drawable.face,
                R.drawable.badface, getResources().getResourceEntryName(R.drawable.face),
                getResources().getResourceEntryName(R.drawable.badface));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("giraffe",
                R.drawable.giraffe,
                R.drawable.badgiraffe, getResources().getResourceEntryName(R.drawable.giraffe),
                getResources().getResourceEntryName(R.drawable.badgiraffe));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("leopard",
                R.drawable.leopard,
                R.drawable.badleopard, getResources().getResourceEntryName(R.drawable.leopard),
                getResources().getResourceEntryName(R.drawable.badleopard));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("monkey",
                R.drawable.monkey,
                R.drawable.badmonkey, getResources().getResourceEntryName(R.drawable.monkey),
                getResources().getResourceEntryName(R.drawable.badmonkey));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("rocket",
                R.drawable.rocket,
                R.drawable.badrocket, getResources().getResourceEntryName(R.drawable.rocket),
                getResources().getResourceEntryName(R.drawable.badrocket));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("sheep",
                R.drawable.sheep,
                R.drawable.badsheep, getResources().getResourceEntryName(R.drawable.sheep),
                getResources().getResourceEntryName(R.drawable.badsheep));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("star",
                R.drawable.star,
                R.drawable.badstar, getResources().getResourceEntryName(R.drawable.star),
                getResources().getResourceEntryName(R.drawable.badstar));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("teddybear",
                R.drawable.teddybear,
                R.drawable.badteddybear, getResources().getResourceEntryName(R.drawable.teddybear),
                getResources().getResourceEntryName(R.drawable.badteddybear));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("train",
                R.drawable.train,
                R.drawable.badtrain, getResources().getResourceEntryName(R.drawable.train),
                getResources().getResourceEntryName(R.drawable.badtrain));
        mTokenList.add(mTokenData);
        mTokenData = new TokenData("triceratops",
                R.drawable.triceratops,
                R.drawable.badtriceratops, getResources().getResourceEntryName(R.drawable.triceratops),
                getResources().getResourceEntryName(R.drawable.badtriceratops));
        mTokenList.add(mTokenData);

         myAdapter = new TokenAdapter(TokenzActivity.this, mTokenList);
        mRecyclerView.setAdapter(myAdapter);

    }
}
