package com.raman.kumar.shrikrishan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by mann on 9/2/18.
 */

public class MainActivity1 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_lay1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout lay1=(LinearLayout) findViewById(R.id.lay1);
        LinearLayout lay2=(LinearLayout) findViewById(R.id.lay2);
        LinearLayout lay3=(LinearLayout) findViewById(R.id.lay3);
        LinearLayout lay4=(LinearLayout) findViewById(R.id.lay4);
        // To maintain FB Login session
        lay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity1.this,AudioActivity.class);
                startActivity(i);
            }
        });
        lay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         Intent i=new Intent(MainActivity1.this,ImageActivity.class);
                startActivity(i);
            }
        });
        lay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
