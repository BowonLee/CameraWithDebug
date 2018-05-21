package com.bowonlee.dearphotograph.modifier;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;


/**
 * Created by bowon on 2018-04-13.
 */
/*
* 테스트 페이지,
* CropLibary를 사용할 경우 이 엑티비티는 사용하지 않는다.
*
* */

public class ModifyPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 5001;

    private Button complteButton;
    private Button cropPhotoButton;
    private Button rotatePhotoButton;

    private ModifyPhotoView mModifyPhotoView;
    private ModifiedPhoto modifiedPhoto;

    private FrameLayout mContainer;

    private int photoRotation = 0;

    private RelativeLayout mRelativeLayout;
    private LinearLayout mLinearLayout;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modifier);

        mModifyPhotoView = new ModifyPhotoView(this);
        mModifyPhotoView.setOnTouchListener(mModifyPhotoView);
        mContainer = (FrameLayout)findViewById(R.id.container_modifier);

        addContentView(mModifyPhotoView,new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));


        mRelativeLayout = (RelativeLayout)findViewById(R.id.relative_modify_activity_main);
        mLinearLayout = (LinearLayout)findViewById(R.id.linear_modify_group_button);

        complteButton = (Button)findViewById(R.id.btn_modify_complete);
        cropPhotoButton = (Button)findViewById(R.id.btn_modify_crop);
        rotatePhotoButton = (Button)findViewById(R.id.btn_modify_rotate);

        complteButton.setOnClickListener(this);
        cropPhotoButton.setOnClickListener(this);
        rotatePhotoButton.setOnClickListener(this);


    }

    public void getExtras(){
        Intent intent = getIntent();
        Photo tempPhoto;
        tempPhoto = intent.getParcelableExtra(Photo.EXTRA_CODE);

        modifiedPhoto = new ModifiedPhoto(tempPhoto);
        modifiedPhoto.setStartXY(new Point(100,100));

        getPhotoSize(modifiedPhoto.getImageUri());

     modifiedPhoto.setOutSize(getPhotoSize(modifiedPhoto.getImageUri()));

        modifiedPhoto.setRatio((float) mModifyPhotoView.getReductionRatio(getPhotoSize(modifiedPhoto.getImageUri()),
                new Size(mModifyPhotoView.getWidth(),mModifyPhotoView.getHeight())));




        mModifyPhotoView.setPhoto(modifiedPhoto);


    }

    private Size getPhotoSize(Uri photoUri){
        Size result ;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(photoUri.getPath(),options);
        result = new Size(options.outWidth,options.outHeight);
        return result;
    }
    //720,1280
    @Override
    protected void onResume() {
        super.onResume();
        hideUi();




        if(mModifyPhotoView != null) {
            mModifyPhotoView.postInvalidate();
        }


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getExtras();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_modify_complete : {}break;
            case R.id.btn_modify_crop : {}break;
            case R.id.btn_modify_rotate : {rotatePhoto();}break;
        }
    }


    public void rotatePhoto(){
        // 누를 때 마다 시계방향으로 회전
        photoRotation = (photoRotation +90)%360;
        mModifyPhotoView.setPhotoRotation(photoRotation);
        if(mModifyPhotoView != null) {
            mModifyPhotoView.postInvalidate();
        }

    }
    private void hideUi(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    public ModifiedPhoto getModifiedPhotoData(){
        return this.modifiedPhoto;
    }

}
