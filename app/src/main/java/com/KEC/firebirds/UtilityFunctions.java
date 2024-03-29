package com.KEC.firebirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.vinaygaba.rubberstamp.RubberStamp;
import com.vinaygaba.rubberstamp.RubberStampConfig;
import com.vinaygaba.rubberstamp.RubberStampPosition;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilityFunctions {
    public static Bitmap pasteWatermark(Bitmap bitmap, String buttonText, String address, Context context) {
        StringBuilder watermarkText = new StringBuilder("(" + buttonText + ") - " + getTime() + " - " + address);
        Log.d("water", "watermark: " + watermarkText);

        RubberStampConfig config = new RubberStampConfig.RubberStampConfigBuilder()
                .base(bitmap)
                .rubberStamp(watermarkText.toString())
                .rubberStampPosition(RubberStampPosition.BOTTOM_LEFT)
                .alpha(255)
                .textColor(Color.rgb(239, 236, 213))
                .margin(1, -50)
                .textShadow(1.0f, 1, 1, Color.BLACK)
                .textSize(50)
                .textFont("HVD-Fonts-BrandonGrotesque-Regular.otf")
                .build();

        RubberStamp rubberStamp = new RubberStamp(context);
        return rubberStamp.addStamp(config);
    }

    public static String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Log.d("date", "watermark: " + formattedDate);
        return formattedDate;
    }
    public static String getTimeStamp(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Log.d("date", "watermark: " + formattedDate);
        return formattedDate;
    }


}
