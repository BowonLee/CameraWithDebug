package com.bowonlee.dearphotograph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BottomSheetOptionSpaceCamera extends LinearLayout {
    interface CameraOptionCallback{
        void changeAspectRatio();
        void changeWhiteBalance();
        void timerTimeSet();
    }

    private CameraOptionCallback mOptionCallback;

    private RadioGroup RadiogroupAspectRatio;
    private RadioButton RadioButtonAspectRatio916;
    private RadioButton RadioButtonAspectRatio34;
    private RadioButton RadioButtonAspectRatio11;



    public BottomSheetOptionSpaceCamera(Context context, CameraOptionCallback cameraOptionCallback) {
        super(context);
        mOptionCallback = cameraOptionCallback;

        addView(inflate(context,R.layout.bottomsheet_camera_option,null)
        , new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
    }

    private void setAspectRatioGroup(){


    }



}
