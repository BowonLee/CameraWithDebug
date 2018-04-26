package com.bowonlee.dearphotograph.gallary;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.models.Photo;


/**
 * Created by bowon on 2018-04-13.
 */
/*
* 테스트 페이지,
* CropLibary를 사용할 경우 이 엑티비티는 사용하지 않는다.
*
* */

public class PhotoPickActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mImageView;
    private TextView mTextView;
    private Button mButton;
    private Photo mPhoto;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_post_photo_pick);


        mImageView = (ImageView)findViewById(R.id.imageview_post_photo_pick);
        mTextView = (TextView)findViewById(R.id.textview_post_photo_pick_);
        mButton = (Button)findViewById(R.id.btn_post_photo_pick_complete);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_post_photo_pick_complete: {
                }
        }
    }
}
