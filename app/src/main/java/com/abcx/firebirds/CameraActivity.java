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
import android.media.AudioManager;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Button btnCapture;
    private ImageButton btnDone, btnCamSwitch;
    private SurfaceView camLayout;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private ProgressDialog mProgressDialogue;
    private Camera.PictureCallback pictureCallback;
    private Camera.ShutterCallback shutterCallback;
    private Bitmap bitmap, map;
    private String address = "";
    private int currentCameraId;
    LocationManager locationManager;
    LocationListener locationListenerGPS, locationListenerNP;
    private Boolean flag = true;
    private static final int REQUEST_FINE_LOCATION = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camLayout = findViewById(R.id.cameraLayout);
        btnCapture = findViewById(R.id.clickButton);
        btnDone = findViewById(R.id.done);
        btnCamSwitch = findViewById(R.id.camSwitch);
        mProgressDialogue = new ProgressDialog(CameraActivity.this);
        mProgressDialogue.setTitle("Getting Location");
        mProgressDialogue.setMessage("Please wait while we get your location...");
        mProgressDialogue.setCanceledOnTouchOutside(false);
        mProgressDialogue.setCancelable(false);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
                locationManager.removeUpdates(locationListenerGPS);
                Log.i("LogGPS", location.toString());
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
        locationListenerNP = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
                locationManager.removeUpdates(locationListenerNP);
                Log.i("LogNP", location.toString());
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

        locationUpdater();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCamSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }else{
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                camera.stopPreview();
                camera.release();
                camera = Camera.open(currentCameraId);
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.setDisplayOrientation(90);
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
                            .setTitle("GPS Disabled!")
                            .setMessage("Do you want to Enable GPS?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    CameraActivity.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    mProgressDialogue.show();
                    camera.takePicture(shutterCallback, null, pictureCallback);
                    btnCapture.setEnabled(false);
                    //btnDone.setEnabled(false);
                    btnCamSwitch.setEnabled(false);
                }
            }
        });
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraActivity.this.camera.stopPreview();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                map = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), null, true);
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    map = RotateBitmap(map, 90);
                }else {
                    if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        map = RotateBitmap(map, -90);
                    }
                }
                if (address != ""){
                    Log.i("Tag", "Saving");
                    map = UtilityFunctions.pasteWatermark(map, getIntent().getStringExtra("btn_extra"),
                            address, CameraActivity.this);
                    storePhoto(map, UtilityFunctions.getTimeStamp());
                    mProgressDialogue.dismiss();
                }else{
                    locationListenerGPS = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            updateLocationInfo(location);
                            if (address != "" && flag == true) {
                                flag = false;
                                locationManager.removeUpdates(locationListenerGPS);
                                Log.i("Tag", "Saving");
                                Log.i("LogGPS", location.toString());
                                map = UtilityFunctions.pasteWatermark(map, getIntent().getStringExtra("btn_extra"),
                                        address, CameraActivity.this);
                                storePhoto(map, UtilityFunctions.getTimeStamp());
                                mProgressDialogue.dismiss();
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
                    locationListenerNP = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            updateLocationInfo(location);
                            if (address != "" && flag == true) {
                                flag = false;
                                locationManager.removeUpdates(locationListenerNP);
                                Log.i("Tag", "Saving");
                                Log.i("LogNP", location.toString());
                                map = UtilityFunctions.pasteWatermark(map, getIntent().getStringExtra("btn_extra"),
                                        address, CameraActivity.this);
                                storePhoto(map, UtilityFunctions.getTimeStamp());
                                mProgressDialogue.dismiss();
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
                }
            }

        };
        shutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
            }
        };
    }

    private void locationUpdater() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListenerGPS);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    updateLocationInfo(lastKnownLocation);
                }
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListenerNP);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    updateLocationInfo(lastKnownLocation);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationUpdater();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                }
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
            FileOutputStream outputStream = new FileOutputStream(file + "/photo-" + path + ".jpeg");
            map.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(file + "/photo-" + path + ".jpeg");
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            Toast.makeText(CameraActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }catch (Exception e){
            e.printStackTrace();
        }
        cameraParas();
    }

    private void cameraParas(){
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
            if (sizes.get(i).width > size.width) {
                size.width = sizes.get(i).width;
            }
            if (sizes.get(i).height > size.height) {
                size.height = sizes.get(i).height;
            }
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