package com.bowonlee.dearphotograph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BottomSheetOptionPanelCamera extends LinearLayout {

    static public final int ASPECT_RATIO_9_16 = 1;
    static public final int ASPECT_RATIO_3_4 = 2;
    static public final int ASPECT_RATIO_1_1 = 3;

    static public final int WHITEBALANCE_AUTO = 11;
    static public final int WHITEBALANCE_COLUDY = 12;
    static public final int WHITEBALANCE_DAYLIGHT = 13;
    static public final int WHITEBALANCE_FLUORSCENT = 14;
    static public final int WHITEBALANCE_INCANDSCENT = 15;

    static public final int TIMER_SEC_3 = 21;
    static public final int TIMER_SEC_5 = 22;
    static public final int TIMER_SEC_10 = 23;

    public interface CameraOptionCallback{
        void changeAspectRatio(int ratioType);
        void changeWhiteBalance(int whiteBalacneType);
        void changeTimerSecond(int timerSec);
    }

    private CameraOptionCallback mOptionCallback;

    private RadioGroup mRadiogroupAspectRatio;
    private RadioGroup mRadiogroupWhiteBalance;
    private RadioGroup mRadiogruopTimerSecond;



    public BottomSheetOptionPanelCamera(Context context, CameraOptionCallback cameraOptionCallback) {
        super(context);
        mOptionCallback = cameraOptionCallback;

        addView(inflate(context,R.layout.bottomsheet_camera_option,null)
        , new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        setAspectRatioGroup();
        setWhiteBalance();
        setTimerSecond();

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

    private void setWhiteBalance(){
        mRadiogroupWhiteBalance = findViewById(R.id.radioGroup_whiteBalance);
        mRadiogroupWhiteBalance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_whitebalance_auto : {mOptionCallback.changeWhiteBalance(WHITEBALANCE_AUTO);}break;
                    case R.id.radioButton_whitebalance_cloudy : {mOptionCallback.changeWhiteBalance(WHITEBALANCE_COLUDY);}break;
                    case R.id.radioButton_whitebalance_daylight : {mOptionCallback.changeWhiteBalance(WHITEBALANCE_DAYLIGHT);}break;
                    case R.id.radioButton_whitebalance_fluorescent : {mOptionCallback.changeWhiteBalance(WHITEBALANCE_FLUORSCENT);}break;
                    case R.id.radioButton_whitebalance_incandscent : {mOptionCallback.changeWhiteBalance(WHITEBALANCE_INCANDSCENT);}break;
                }
            }
        });
    }

    private void setTimerSecond(){
        mRadiogruopTimerSecond = findViewById(R.id.radioGroup_timerSecond);

        mRadiogruopTimerSecond.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_timersec_3 : {mOptionCallback.changeTimerSecond(TIMER_SEC_3);}break;
                    case R.id.radioButton_timersec_5 : {mOptionCallback.changeTimerSecond(TIMER_SEC_5);}break;
                    case R.id.radioButton_timersec_10 : {mOptionCallback.changeTimerSecond(TIMER_SEC_10);}break;
                }
            }
        });
    }


}
