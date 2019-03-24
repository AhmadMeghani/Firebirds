package com.abcx.firebirds;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1;
    String currentImagePath = null;
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
    }

    public void click(String stg){
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("btn_extra", stg);
        startActivity(intent);
    }

}
