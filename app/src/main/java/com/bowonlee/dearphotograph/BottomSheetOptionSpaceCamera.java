package com.bowonlee.dearphotograph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BottomSheetOptionSpaceCamera extends LinearLayout {
    static public final int ASPECT_RATIO_9_16 = 1;
    static public final int ASPECT_RATIO_3_4 = 2;
    static public final int ASPECT_RATIO_1_1 = 3;

    public interface CameraOptionCallback{
        void changeAspectRatio(int ratioType);
        void changeWhiteBalance();
        void timerTimeSet();
    }

    private CameraOptionCallback mOptionCallback;

    private RadioGroup mRadiogroupAspectRatio;




    public BottomSheetOptionSpaceCamera(Context context, CameraOptionCallback cameraOptionCallback) {
        super(context);
        mOptionCallback = cameraOptionCallback;

        addView(inflate(context,R.layout.bottomsheet_camera_option,null)
        , new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        setAspectRatioGroup();

    }

    private void setAspectRatioGroup(){
        mRadiogroupAspectRatio = findViewById(R.id.radioGroup_aspectRatio);


        mRadiogroupAspectRatio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_ratio_916 :{
                        mOptionCallback.changeAspectRatio(ASPECT_RATIO_9_16);
                    }break;
                    case R.id.radioButton_ratio_34 :{
                        mOptionCallback.changeAspectRatio(ASPECT_RATIO_3_4);
                    }break;
                    case R.id.radioButton_ratio_11 :{
                        mOptionCallback.changeAspectRatio(ASPECT_RATIO_1_1);
                    }break;
                }
            }
        });


    }



}
