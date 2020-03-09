package com.offlineprogrammer.kidztokenz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

private RecyclerView recyclerView;
private KidAdapter mAdapter;
    private ArrayList<Kid> kidzList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //generateKidList();
        mAdapter = new KidAdapter(kidzList);


        recyclerView = findViewById(R.id.kidz_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        FloatingActionButton fab = findViewById(R.id.fab_add_kid);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddKidDialog(MainActivity.this);
            }
        });

    }

    private int pickMonster(){
        final TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.kidzMonsters);
        final Random rand = new Random();
        final int rndInt = rand.nextInt(imgs.length());
        return imgs.getResourceId(rndInt, 0);
    }

    private void showAddKidDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new kid")
                .setMessage("Enter the kid name")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kidName = String.valueOf(taskEditText.getText());
                        mAdapter.add(new Kid(kidName,pickMonster()),0);
                        recyclerView.scrollToPosition(0);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        taskEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                taskEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(taskEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        taskEditText.setMaxLines(1);
        taskEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL );
        taskEditText.setHint("Kid Name");
        taskEditText.requestFocus();
    }

    private void generateKidList() {


        for (int i = 0; i < 10; i++)

            kidzList.add(new Kid(String.format(Locale.US, " item %d", i),R.drawable.m1));





    }
}
