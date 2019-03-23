package com.abcx.firebirds;

import android.content.Context;
import android.content.pm.PackageManager;

public class Camera {
    public Boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else return false;
    }
//    public static android.hardware.Camera getCameraInstance{
//        android.hardware.Camera c = null;
//        try{
//            c = android.hardware.Camera.open();
//        }catch (Exception e){
//
//        }
//        return c;
//    }
}
