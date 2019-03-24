package com.abcx.firebirds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.vinaygaba.rubberstamp.RubberStamp;
import com.vinaygaba.rubberstamp.RubberStampConfig;
import com.vinaygaba.rubberstamp.RubberStampPosition;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class imageView extends AppCompatActivity {

    private ImageView imageView;
    private String watermarkText = "", address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        watermark(bitmap);


    }

    public void watermark(Bitmap bitmap) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Log.d("date", "watermark: " + formattedDate);

        watermarkText = "(" + getIntent().getStringExtra("btn_text") + ") - " + formattedDate;
        Log.d("water", "watermark: " + watermarkText);


        RubberStampConfig config = new RubberStampConfig.RubberStampConfigBuilder()
                .base(bitmap)
                .rubberStamp(watermarkText)
                .rubberStampPosition(RubberStampPosition.BOTTOM_RIGHT)
                .alpha(255)
                .textColor(Color.WHITE)
                .textShadow(1.0f, 5, 5, Color.BLACK)
                .textSize(100)
                .build();

        RubberStamp rubberStamp = new RubberStamp(this);
        imageView.setImageBitmap(rubberStamp.addStamp(config));


    }
}
