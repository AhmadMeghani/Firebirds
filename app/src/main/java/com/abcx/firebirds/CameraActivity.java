package com.abcx.firebirds;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Button btnCapture, btnDone;
    private SurfaceView camLayout;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private ProgressDialog mProgressDialogue;
    private Camera.PictureCallback pictureCallback;
    private Bitmap bitmap, map;
    private Boolean pictureTaken = false;
    private String address = "";
    LocationManager locationManager;
    LocationListener locationListener;
    private static final int REQUEST_CAMERA_PERMISSION = 200, REQUEST_FINE_LOCATION = 300, REQUEST_WRITE_PERMISSION = 400;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camLayout = findViewById(R.id.cameraLayout);
        btnCapture = findViewById(R.id.clickButton);
        btnDone = findViewById(R.id.done);
        mProgressDialogue = new ProgressDialog(CameraActivity.this);
        mProgressDialogue.setTitle("Getting Location");
        mProgressDialogue.setMessage("Please wait while we get your location...");
        mProgressDialogue.setCanceledOnTouchOutside(false);
        mProgressDialogue.setCancelable(false);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
                locationManager.removeUpdates(this);
                Log.i("Log", location.toString());
                if (pictureTaken == false) {
                    mProgressDialogue.setTitle("Saving Image");
                    mProgressDialogue.setMessage("Please wait while we save your image...");
                    map = UtilityFunctions.pasteWatermark(map, getIntent().getStringExtra("btn_extra"),
                            address, CameraActivity.this);
                    storePhoto(map, UtilityFunctions.getTimeStamp());
                    mProgressDialogue.dismiss();
                    CameraActivity.this.camera.startPreview();
                    pictureTaken = true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {

            }
        }


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        surfaceHolder = camLayout.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) CameraActivity.this.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    new AlertDialog.Builder(CameraActivity.this)
                            .setTitle("GPS Disabled!")  // GPS not found
                            .setMessage("Enable GPS") // Want to enable?
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    CameraActivity.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    camera.takePicture(null, null, pictureCallback);
                    mProgressDialogue.show();
                    btnCapture.setEnabled(false);
                    btnCapture.setVisibility(View.GONE);
                    btnDone.setEnabled(false);
                    btnDone.setVisibility(View.GONE);
                }
            }
        });
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                map = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), null, true);
                map = RotateBitmap(map, 90);
                if (address != "") {
                    mProgressDialogue.dismiss();
                    map = UtilityFunctions.pasteWatermark(map, getIntent().getStringExtra("btn_extra"),
                            address, CameraActivity.this);
                    storePhoto(map, UtilityFunctions.getTimeStamp());
                    pictureTaken = true;
                } else {
                    //re check Location here

                }
                //storePhoto(map, UtilityFunctions.getTimeStamp());


                CameraActivity.this.camera.startPreview();
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION);
            }
            if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_PERMISSION);
            }
        }
        else if (requestCode == REQUEST_FINE_LOCATION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION);
            }
        }
        else if (requestCode == REQUEST_CAMERA_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }
        }
        else if (requestCode == REQUEST_WRITE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_PERMISSION);
            }
        }
    }
    private Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void storePhoto(Bitmap map, String path) {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/Firebird");
        if (!file.isDirectory()) {
            file.mkdir();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file + "/photo" + path + ".jpeg");
            map.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(file + "/photo" + path + ".jpeg");
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            camera = Camera.open();
        }catch (Exception e){
            e.printStackTrace();
        }
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFrameRate(20);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        params.setPictureSize(size.width, size.height);
        camera.setParameters(params);
        camera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void startListening(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        }
    }

    public void updateLocationInfo(Location location) {
        double lat = location.getLatitude();
        double log = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat, log, 1);
            if (listAddresses != null && listAddresses.size() > 0){
                address="";

                if (listAddresses.get(0).getAddressLine(0) != null) {
                    address += listAddresses.get(0).getAddressLine(0);
                }

                Log.d("address", "updateLocationInfo: "+address);

            }
        }catch(Exception e){
            e.printStackTrace();

        }

    }
}