package com.abcx.firebirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.vinaygaba.rubberstamp.RubberStamp;
import com.vinaygaba.rubberstamp.RubberStampConfig;
import com.vinaygaba.rubberstamp.RubberStampPosition;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilityFunctions {
    public static Bitmap pasteWatermark(Bitmap bitmap, String buttonText, String address, Context context) {
        String watermarkText = "(" + buttonText + ") - " + getTime() + " - " + address;
        Log.d("water", "watermark: " + watermarkText);

        RubberStampConfig config = new RubberStampConfig.RubberStampConfigBuilder()
                .base(bitmap)
                .rubberStamp(watermarkText)
                .rubberStampPosition(RubberStampPosition.BOTTOM_LEFT)
                .alpha(255)
                .textColor(Color.rgb(239, 236, 213))
                .margin(1, -10)
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
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

}
