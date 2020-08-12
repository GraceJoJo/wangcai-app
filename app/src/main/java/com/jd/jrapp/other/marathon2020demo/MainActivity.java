package com.jd.jrapp.other.marathon2020demo;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jd.jrapp.other.pet.ui.PetFloatWindow;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PetFloatWindow.Companion.getInstance().checkAndShow(MainActivity.this);
            }
        });
    }
}