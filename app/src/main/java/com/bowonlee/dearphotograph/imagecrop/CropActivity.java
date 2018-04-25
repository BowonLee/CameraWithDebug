package com.bowonlee.dearphotograph.imagecrop;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bowonlee.dearphotograph.R;

/*
* Cropper의 Sample과 비슷하게 구성하려 한다.
* DrawerLayout을 통한 Option 변경 + CropImageView를 가지고 있는 Fragment를 통한 Crop과 설정 적용
* 을 목표로 한다.
* */
public class CropActivity extends AppCompatActivity{

    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private CropFragment mCurrentFragment;

    private Uri mCropImageUri;

    private CropImageViewOptions mCropImageViewOptions ;

    public void setCurrentFragment(CropFragment fragment){mCurrentFragment = fragment;}
    public void setCurrentOptions(CropImageViewOptions options){
        mCropImageViewOptions = options;
        //update
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.cropper_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,
                R.string.crop_drawer_open,R.string.crop_drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if(savedInstanceState == null){
            //예외처리
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        //옵션에 따라 프레그먼트 갱신
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
 //       if(mCurrentFragment != null) fragment구성

        return super.onOptionsItemSelected(item);
    }
}
























