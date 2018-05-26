package com.bowonlee.dearphotograph.modifier;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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

    private Button mButtonComplete;
    private Button mButtonCrop;
    private ArrayList<Button> mButtonGroup;

    private Photo photo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);
        mButtonComplete = (Button)findViewById(R.id.btn_modifier_complete);
        mButtonCrop = (Button)findViewById(R.id.btn_modifier_crop);

        setButtons();
        getPhotoFromGallary();
       //  pickFromGallery();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        startActivityForResult(Intent.createChooser(intent,"Select Picture" ),123);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getPhotoFromGallary(){
        photo = getIntent().getParcelableExtra(Photo.EXTRA_CODE);
       //Log.e("URI",);
        Uri uri = Uri.fromFile(new File(photo.getImageUri().getPath()));
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), "pic.jpg"))).start(this);

    }
    private void setButtons(){
        mButtonGroup = new ArrayList<>();
        mButtonGroup.add(mButtonComplete);
        mButtonGroup.add(mButtonCrop);

        for(Button button : mButtonGroup){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_modifier_complete : {}break;
                        case R.id.btn_modifier_crop : {}break;
                    }
                }
            });
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 123){
             final Uri uri = data.getData();
             if(uri !=null){
                 Log.e("Log",uri.getPath());
                 UCrop.of(uri, Uri.fromFile(new File(getCacheDir(),"pic.jpg"))).start(this);
             }
            }
        }

    }
}
