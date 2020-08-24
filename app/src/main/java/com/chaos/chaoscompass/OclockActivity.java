package com.chaos.chaoscompass;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OclockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oclock);
        BarUtils.setColor(this,getResources().getColor(R.color.ocbg),0);
    }
}
