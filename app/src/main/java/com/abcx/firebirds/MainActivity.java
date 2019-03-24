package com.abcx.firebirds;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    Button btnWoodFired, btnFirst, btnSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "HVD-Fonts-BrandonGrotesque-Regular.otf");
        btnWoodFired = findViewById(R.id.woodFired);
        btnWoodFired.setTypeface(typeface);
        btnWoodFired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(btnWoodFired.getText().toString());
            }
        });

        btnFirst = findViewById(R.id.first);
        btnFirst.setTypeface(typeface);
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(btnFirst.getText().toString());
            }
        });

        btnSecond = findViewById(R.id.second);
        btnSecond.setTypeface(typeface);
        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(btnSecond.getText().toString());
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==REQUEST_CAMERA_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        }
    }

    public void click(String stg){
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("btn_extra", stg);
        startActivity(intent);
    }

}
