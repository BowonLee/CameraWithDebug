package com.bowonlee.dearphotograph;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bowonlee.dearphotograph.gallary.PhotoGallaryActivity;
import com.bowonlee.dearphotograph.models.Photo;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements CameraPreview.CameraInterface, View.OnClickListener,SensorEventListener {

    /*
    * 안드로이드의 카메라 프리뷰세션 여는 요청은 비동기 쓰레드 콜벡을 통해 이루어진다.
    * 따라서 권한 요청을 하기도 전에 카메라를 열려고 시도하기에 초기 1회 crash가 발생하게되므로
    * Dialog를 통해 잠시 앱의 동작을 멈추는 기능이 필요하다
    * */
    private static final String ALBUMNAME = "DearPhotograph";
    public static final int RESULT_OK = 9456;
    public static final int RESULT_CANCLE = 9458;


    private AutoFitTextureView mTextureView;
    private CameraPreview cameraPreview;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private ImageView mImageView;
    private Button mTakePictureButton;
    private Button mOpenGallaryButton;
    private Button mFinishAppButton;


    //Sensor for change orientation
    private Sensor mAcellerometerSensor;
    private Sensor mMagneticSensor;

    private SensorManager mSensorManager;
    private float[] mAcceleroArr;
    private float[] mMagneticArr;


    private FileIOHelper mFileIOHelper;



    //Orientation 설정을 위한 셋팅
    private final int ORIENTATION_PORTRAIT = ExifInterface.ORIENTATION_ROTATE_90; //6
    private final int ORIENTATION_LANDSCAPE_REVERSE = ExifInterface.ORIENTATION_ROTATE_180;// 3
    private final int ORIENTATION_LANDSCAPE = ExifInterface.ORIENTATION_NORMAL; //1
    private final int ORIENTATION_PORTRAIT_REVERSE = ExifInterface.ORIENTATION_ROTATE_270; //8

    int smoothness = 1;
    private float averagePitch = 0;
    private float averageRoll = 0;
    private int orientation = ORIENTATION_PORTRAIT;

    private float[] pitches;
    private float[] rolls;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileIOHelper = new FileIOHelper();
        mFileIOHelper.getAlbumStorageDir(ALBUMNAME);


        mTextureView = (AutoFitTextureView) findViewById(R.id.camera_preview_session);
        mImageView = (ImageView)findViewById(R.id.imageview);

        mTakePictureButton = (Button)findViewById(R.id.btn_take_picture);
        mOpenGallaryButton = (Button)findViewById(R.id.btn_open_gallary);
        mFinishAppButton = (Button)findViewById(R.id.btn_app_finish);

        mTakePictureButton.setOnClickListener(this);
        mOpenGallaryButton.setOnClickListener(this);
        mFinishAppButton.setOnClickListener(this);


        setRequestCameraPermission();

        setSensors();

        pitches = new float[smoothness];
        rolls = new float[smoothness];

    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        cameraPreview = new CameraPreview(this,mTextureView);

        cameraPreview.startBackgroundThread();
        /*
        * 앱을 실행한 경우이면 surfaceTexture부터 생성하고 카메라를 오픈하지만
        * 단순히 화면만 껏다켠 경우는 카메라장치만 다시 열면 된다.
        * */


        if(mTextureView.isAvailable()){

            cameraPreview.openCamera(mTextureView.getWidth(),mTextureView.getHeight());

        }else{
            cameraPreview.setSurface();
        }

        if(mImageView!=null){
          //,    setmImageView();
        }
        setSensorListener();
    }

    //sensor 가동 및 리스너
    private void setSensors(){
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        mAcellerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }
    private void setSensorListener(){

        //센서가 동작하지 않는 기기가 있을 수 있다. 이에 대한 예외처리가 필요할 것이다.
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null&&mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null) {
            mSensorManager.registerListener(this, mAcellerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }else{
            Log.e("MainActivity","Sensor is disable");
        }
//

    }
    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.closeCamera();
        cameraPreview.stopBackgroundThread();
        mSensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setRequestCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            new ConfirmationDialog().show(getSupportFragmentManager(),"dialog");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //퍼미션이 거부되었음,
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPostTakePicture() {
        Toast.makeText(this,"Post Excute In CapturePreview ",Toast.LENGTH_LONG).show();
        Log.e("Mainactivity","Post Excute In CapturePreview");

    }

    // sensors
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mAcceleroArr = event.values;


        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mMagneticArr = event.values;
        }



        if(mAcceleroArr!=null&&mMagneticArr!=null){
            float[] R = new float[9];
            float[] I = new float[9];

            if(SensorManager.getRotationMatrix(R,I,mAcceleroArr,mMagneticArr)){
                float[] orientationData = new float[3];
                SensorManager.getOrientation(R,orientationData);
                averagePitch = addValue(orientationData[1],pitches);
                averageRoll = addValue(orientationData[2],rolls);
                orientation = calculateOrientation();

                if(orientation == ORIENTATION_PORTRAIT||orientation == ORIENTATION_PORTRAIT_REVERSE){
                    Log.i("CurrentOrientation","portrait");
                }else{
                    Log.i("CurrentOrientation","landscape");

                }
            }
        }


    }

    private float addValue(float value, float[] values){
        float average = 0;
        value = (float)Math.round(Math.toDegrees(value));

        for (int i= 1;i<smoothness;i++){
            values[i -1] = values[i];
            average += values[i];
        }
        values[smoothness -1] = value;
        average = (average + value) / smoothness;
        return average;

    }

    private int calculateOrientation(){
        if((orientation == ORIENTATION_PORTRAIT||orientation == ORIENTATION_PORTRAIT_REVERSE
        &&(averageRoll>-30&&averageRoll<30))){
            if (averagePitch>0){
                return ORIENTATION_PORTRAIT_REVERSE;
            }else{
                return ORIENTATION_PORTRAIT;
            }
        }else{
            if(Math.abs(averagePitch)>=30){
                if(averagePitch>0){
                    return ORIENTATION_PORTRAIT_REVERSE;
                }else{
                    return ORIENTATION_PORTRAIT;
                }
            }else{
                if(averageRoll>0){
                    return ORIENTATION_LANDSCAPE_REVERSE;
                }else{
                    return ORIENTATION_LANDSCAPE;
                }
            }
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    //end of sensors

    //Dialog for Permissions
    public static class ConfirmationDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity parent = getActivity();
            return new AlertDialog.Builder(getActivity()).setMessage(R.string.request_caemra_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CAMERA_PERMISSION);
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(parent != null){
                                parent.finish();
                            }
                        }
                    }).create();

        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.btn_take_picture  : {takePicture();}break;
            case R.id.btn_open_gallary  : {openGallary();}break;
            case R.id.btn_app_finish    : {finishApp();}break;
        }
    }
    private void openGallary(){

        Intent intent = new Intent(MainActivity.this, PhotoGallaryActivity.class);
        startActivityForResult(intent,156);


    }

    private void takePicture(){
        /*사진 촬영과 저장*/
        cameraPreview.takePicture();
    }
    private void finishApp(){
        finish();


    }




    public void setmImageView(Photo photo){
        //Bitmap image =
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;


        //mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cafe_demoimage,options));
        mImageView.setImageURI(photo.getImageUri());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 156 : {if(resultCode == RESULT_OK){
                Photo photo = data.getParcelableExtra("result");
             setmImageView(photo);
            }}
        }


    }
}
