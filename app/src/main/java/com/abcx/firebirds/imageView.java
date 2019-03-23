package com.abcx.firebirds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.watermark.androidwm.WatermarkBuilder;
import com.watermark.androidwm.bean.WatermarkText;

public class imageView extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        imageView.setImageBitmap(bitmap);


        WatermarkText watermarkText = new WatermarkText("ABCD")
                .setPositionX(0.5)
                .setPositionY(0.9)
                .setTextColor(Color.WHITE)
                .setTextAlpha(255)
                .setTextSize(10)
                .setTextShadow(0.01f, 2, 2, Color.BLACK);

        WatermarkBuilder
                .create(this, bitmap)
                .loadWatermarkText(watermarkText)
                .getWatermark()
                .setToImageView(imageView);
    }
}
