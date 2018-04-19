package com.bowonlee.dearphotograph;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements CameraPreview.CameraInterface, View.OnClickListener {

    /*
    * 안드로이드의 카메라 프리뷰세션 여는 요청은 비동기 쓰레드 콜벡을 통해 이루어진다.
    * 따라서 권한 요청을 하기도 전에 카메라를 열려고 시도하기에 초기 1회 crash가 발생하게되므로
    * Dialog를 통해 잠시 앱의 동작을 멈추는 기능이 필요하다
    * */
    private static final String ALBUMNAME = "DearPhotograph";

    private AutoFitTextureView mTextureView;
    private CameraPreview cameraPreview;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private ImageView mImageView;
    private FileIOHelper mFileIOHelper;


    private Button mTakePictureButton;
    private Button mOpenGallaryButton;
    private Button mFinishAppButton;


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


    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.closeCamera();
        cameraPreview.stopBackgroundThread();
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
        Toast.makeText(this,"Post Excute In CapturePreview ",Toast.LENGTH_LONG);
        Log.e("Mainactivity","Post Excute In CapturePreview");

    }




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
        startActivity(intent);


    }

    private void takePicture(){
        /*사진 촬영과 저장*/
        cameraPreview.takePicture();
    }
    private void finishApp(){
        finish();


    }



    public void setmImageView(){
        //Bitmap image =
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;


        mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cafe_demoimage,options));

    }



}
