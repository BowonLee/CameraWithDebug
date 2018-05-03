package com.bowonlee.dearphotograph;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bowonlee.dearphotograph.gallary.PhotoGallaryActivity;
import com.bowonlee.dearphotograph.models.Photo;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements CameraFragment.CameraInterface, View.OnClickListener,
        OrientationHelper.OrientationChangeListener {


    /*
    * MainActivity의 변화가 있을 경우 먼저 테스트 해보는 테스트용 메인 코드
    *
    *
    * */
    private static final String ALBUMNAME = "DearPhotograph";

    public static final int RESULT_OK = 9456;
    public static final int RESULT_CANCLE = 9458;




    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private ImageView mImageView;
    private Button mTakePictureButton;
    private Button mOpenGallaryButton;
    private Button mFinishAppButton;


    //Sensor for change orientation
    private Sensor mAcellerometerSensor;
    private Sensor mMagneticSensor;
    private SensorManager mSensorManager;
    private OrientationHelper mSensorOrientation;

    private FileIOHelper mFileIOHelper;

    private CameraFragment mCameraFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mCameraFragment = CameraFragment.newInstance();


        mFileIOHelper = new FileIOHelper();
        mFileIOHelper.getAlbumStorageDir(ALBUMNAME);

        if(null  == savedInstanceState){
            mFragmentTransaction.replace(R.id.container,mCameraFragment).commit();
        }
        mImageView = (ImageView)findViewById(R.id.imageview);

        mTakePictureButton = (Button)findViewById(R.id.btn_take_picture);
        mOpenGallaryButton = (Button)findViewById(R.id.btn_open_gallary);
        mFinishAppButton = (Button)findViewById(R.id.btn_app_finish);

        mTakePictureButton.setOnClickListener(this);
        mOpenGallaryButton.setOnClickListener(this);
        mFinishAppButton.setOnClickListener(this);


        setRequestCameraPermission();

        mSensorOrientation = new OrientationHelper();
        mSensorOrientation.setOnOrientationListener(this);
        setSensors();
        mCameraFragment.setOnCameraInterface(this);
        mCameraFragment.setTextureSize(3,4);

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

        /*
         * 앱을 실행한 경우이면 surfaceTexture부터 생성하고 카메라를 오픈하지만
         * 단순히 화면만 껏다켠 경우는 카메라장치만 다시 열면 된다.
         * */

        if(mImageView!=null){
            //,    setmImageView();
        }
        setSensorListener();
        DisplayMetrics dm;
        dm = getApplicationContext().getResources().getDisplayMetrics();
        Log.e("Device Size",String.format("width : %d height : %d",dm.widthPixels,dm.heightPixels));
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
//

    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorOrientation.getEventListener());
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

    @Override
    public void OnOrientationChanged(int orientation) {
        if(orientation == OrientationHelper.ORIENTATION_PORTRAIT||orientation == OrientationHelper.ORIENTATION_PORTRAIT_REVERSE){
            //portrait
            if(orientation == OrientationHelper.ORIENTATION_PORTRAIT){
                //정방향
                Log.i("Current Orientation","Portrait");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_PORTRAIT_VIEW);
                rotateItemsByOrientation(OrientationHelper.ORIENTATION_PORTRAIT_ITEM);
            }else{
                //역방향
                Log.i("Current Orientation","Portrait Reverse");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_PORTRAIT_REVERSE_VIEW);
                rotateItemsByOrientation(OrientationHelper.ORIENTATION_PORTRAIT_REVERSE_ITEM);
            }
        }else{
            //landscape
            if(orientation == OrientationHelper.ORIENTATION_LANDSCAPE){
                //정방향
                Log.i("Current Orientation","Landscape");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_LANDSCAPE_VIEW);
                rotateItemsByOrientation(OrientationHelper.ORIENTATION_LANDSCAPE_ITEM);
            }else{
                //역방향
                Log.i("Current Orientation","Landscape Reverse");
                mCameraFragment.setOrientation(OrientationHelper.ORIENTATION_LANDSCPAE_REVERSE_VIEW);
                rotateItemsByOrientation(OrientationHelper.ORIENTATION_LANDSCAPE_REVERSE_ITEM);
            }


        }

    }
    public void rotateItemsByOrientation(float roation){
        // 내가 디바이스의 화면을 바라볼 때 기준 좌측으로 돌리기 + 90(nomal) 우측 - 90(reverse)


        mOpenGallaryButton.setRotation(roation);
        mFinishAppButton.setRotation(roation);
        mTakePictureButton.setRotation(roation);
        mImageView.setRotation(roation);



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
        startActivityForResult(intent,PhotoGallaryActivity.REQUEST_CODE);



    }

    private void takePicture(){
        /*사진 촬영과 저장*/
        mCameraFragment.takePicture();
    }
    private void finishApp(){
        finish();


    }




    public void setmImageView(Photo photo){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;


        mImageView.setImageBitmap(BitmapFactory.decodeFile(photo.getImageUri().getPath(),options));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PhotoGallaryActivity.REQUEST_CODE : {if(resultCode == RESULT_OK){
                Photo photo = data.getParcelableExtra("result");
                setmImageView(photo); }break;
            }
        }


    }
}
