package com.bowonlee.dearphotograph.modifier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.BuildConfig;
import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.gallary.PhotoGallaryActivity;
import com.bowonlee.dearphotograph.maincamera.MainPhotoDrawerView;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by bowon on 2018-04-13.
 */
/*
* 테스트 페이지,
* CropLibary를 사용할 경우 이 엑티비티는 사용하지 않는다.
*
* */

public class ModifyPhotoActivity extends AppCompatActivity  {

    public static final int REQUEST_CODE = 3001;
    public static final int PARCELABLE_MODIFIY_RESULT = 3002;

    private Button mButtonComplete;
    private Button mButtonGallary;

    private ArrayList<Button> mButtonGroup;

    private Photo resultPhoto;
    private ImageView mImageView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);
        mButtonComplete = (Button)findViewById(R.id.btn_modifier_complete);
        mButtonGallary = (Button)findViewById(R.id.btn_modifier_gallary);
        mImageView = (ImageView)findViewById(R.id.imageView_modifier_result);

        setButtons();
        //getPhotoFromGallary();
        openGallary();
    }

    private void setButtons(){
        mButtonGroup = new ArrayList<>();
        mButtonGroup.add(mButtonComplete);
        mButtonGroup.add(mButtonGallary);

        for(Button button : mButtonGroup){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_modifier_complete : {completeModify();}break;
                        case R.id.btn_modifier_gallary : {openGallary();}break;
                    }
                }
            });
        }

    }

    private void openGallary(){
        Intent intent = new Intent(this,PhotoGallaryActivity.class);
        startActivityForResult(intent,PhotoGallaryActivity.REQUEST_CODE);

    }

    @SuppressLint("ResourceAsColor")
    private void getPhotoFromGallary(){

        Uri uri = Uri.fromFile(new File(resultPhoto.getImageUri().getPath()));
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setToolbarColor(Color.WHITE);
        options.setToolbarWidgetColor(Color.BLACK);
        options.setActiveWidgetColor(Color.BLACK);
        options.setDimmedLayerColor(Color.TRANSPARENT);
        options.setStatusBarColor(Color.parseColor("#03dac5"));
        options.setCircleDimmedLayer(true);
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), "temp.jpg"))).withOptions(options).start(this);


    }


    private void completeModify(){
        Intent intent = getIntent();
        intent.putExtra(String.valueOf(R.string.parcelable_result),resultPhoto);
        setResult(RESULT_OK,intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri resultUri;
        if(resultCode == RESULT_OK){

            if(requestCode == UCrop.REQUEST_CROP){
                resultUri = UCrop.getOutput(data);
                mImageView.setImageURI(resultUri);
                resultPhoto = new Photo(null,resultUri);
                completeModify();
            }
            if(requestCode == PhotoGallaryActivity.REQUEST_CODE){
                resultPhoto = data.getParcelableExtra(String.valueOf(R.string.parcelable_result));
                getPhotoFromGallary();
            }
        }else{
            finish();
        }

    }
}
