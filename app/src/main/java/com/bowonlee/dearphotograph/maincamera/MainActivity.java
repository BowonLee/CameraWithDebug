package com.bowonlee.dearphotograph.maincamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bowonlee.dearphotograph.FileIOHelper;
import com.bowonlee.dearphotograph.OrientationHelper;
import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.resultpreview.PreviewResultFragment;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements CameraFragment.CameraInterface,
        OrientationHelper.OrientationChangeListener, PreviewResultFragment.PreviewResultInterface {


    private static final String ALBUMNAME = "DearPhotograph";

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private long mBackPressedTime = 0;

    //Sensor for change orientation
    private Sensor mAcellerometerSensor;
    private Sensor mMagneticSensor;
    private SensorManager mSensorManager;
    private OrientationHelper mSensorOrientation;

    //FileSystem
    private FileIOHelper mFileIOHelper;

    //FragmentControl Camera & ResultPreview
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private CameraFragment mCameraFragment;
    private PreviewResultFragment mPreviewResultFragment;

    //For FireBaseSet
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity","onCreate");
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        mFileIOHelper = new FileIOHelper();
        mFileIOHelper.getAlbumStorageDir(ALBUMNAME);


        setRequestCameraPermission();
        mSensorOrientation = new OrientationHelper();
        mSensorOrientation.setOnOrientationListener(this);
        startCameraFragment();
        setFireBase();
    }

    private void setFireBase(){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private void hideUi(){
                    getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideUi();
        setSensors();
        setSensorListener();

    }

    //sensor 가동 및 리스너
    private void setSensors(){
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAcellerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    private void setSensorListener(){
        //센서가 동작하지 않는 기기가 있을 수 있다.(기기특성, 고장, 일시적 오류 등) 이에 대한 예외처리가 필요할 것이다.
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null&&mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null) {
            mSensorManager.registerListener(mSensorOrientation.getEventListener(), mAcellerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(mSensorOrientation.getEventListener(), mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Log.e("MainActivity","Sensor is disable");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorOrientation.getEventListener());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setRequestCameraPermission(){

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            new ConfirmationDialog().show(getSupportFragmentManager(),"dialog");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //권한 요청이 거부당함
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPostTakePicture(Bitmap captureBitmap,ModifiedPhoto modifiedPhoto) {
        Toast.makeText(this,"Post Excute In CapturePreview ",Toast.LENGTH_SHORT).show();
        Log.e("main rotate",modifiedPhoto.getRotation()+"");
        startPreviewResultFragment(captureBitmap,modifiedPhoto);



    }


    @Override
    public void onCancelPreviewResult() {
        startCameraFragment();
    }

    public void startCameraFragment(){

        mCameraFragment = CameraFragment.newInstance();

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        mFragmentTransaction.replace(R.id.main_container,mCameraFragment).commit();
        mCameraFragment.setOnCameraInterface(this);
      ;
        //   mCameraFragment.setTextureSize(3,4);
    }

    private void startPreviewResultFragment(Bitmap captureBitmap,ModifiedPhoto modifiedPhoto){

        mPreviewResultFragment = new PreviewResultFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        mPreviewResultFragment.setCapturedBitmap(captureBitmap);
        mPreviewResultFragment.setModifiedPhoto(modifiedPhoto);

        mFragmentTransaction.replace(R.id.main_container,mPreviewResultFragment).commit();
        mPreviewResultFragment.setPreviewResultInterface(this);

    }




    @Override
    public void OnOrientationChanged(int orientation) {
        int itemOrientation = 0;
        if(orientation == OrientationHelper.ORIENTATION_PORTRAIT || orientation == OrientationHelper.ORIENTATION_PORTRAIT_REVERSE){
            //portrait
            if(orientation == OrientationHelper.ORIENTATION_PORTRAIT){
                //정방향
                Log.i("Current Orientation","Portrait");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_PORTRAIT_VIEW);
                itemOrientation = OrientationHelper.ORIENTATION_PORTRAIT_ITEM;
            }else{
                //역방향
                Log.i("Current Orientation","Portrait Reverse");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_PORTRAIT_REVERSE_VIEW);
                itemOrientation = OrientationHelper.ORIENTATION_PORTRAIT_REVERSE_ITEM;
            }
        }else {
            //landscape
            if(orientation == OrientationHelper.ORIENTATION_LANDSCAPE){
                //정방향
                Log.i("Current Orientation","Landscape");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_LANDSCAPE_VIEW);
                itemOrientation = OrientationHelper.ORIENTATION_LANDSCAPE_ITEM;
            }else{
                //역방향
                Log.i("Current Orientation","Landscape Reverse");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_LANDSCPAE_REVERSE_VIEW);
                itemOrientation = OrientationHelper.ORIENTATION_LANDSCAPE_REVERSE_ITEM;
            }


        }
        rotateItemsByOrientation(itemOrientation);



    }
    public void rotateItemsByOrientation(float roation){
        // 내가 디바이스의 화면을 바라볼 때 기준 좌측으로 돌리기 + 90(nomal) 우측 - 90(reverse)
        if(mCameraFragment.isVisible()){
          //mCameraFragment.setItemOrientation(roation);
        }
    }







    @Override
    public void onBackPressed() {
     //   super.onBackPressed();

        if(System.currentTimeMillis()>mBackPressedTime+2000){
                mBackPressedTime = System.currentTimeMillis();
                Toast.makeText(this,"한번 더 누르시면 종료합니다.",Toast.LENGTH_SHORT).show();
                return;
        }
        if(System.currentTimeMillis()<=mBackPressedTime+2000){
            //Toast.makeText(this,"한번 더 누르시면 종료합니다.",Toast.LENGTH_SHORT);
            this.finish();
        }
    }


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

}
