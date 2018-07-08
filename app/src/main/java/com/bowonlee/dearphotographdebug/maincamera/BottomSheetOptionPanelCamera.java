package com.bowonlee.dearphotographdebug.maincamera;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.bowonlee.dearphotographdebug.R;
import com.bowonlee.dearphotographdebug.models.OptionData;

public class BottomSheetOptionPanelCamera extends LinearLayout {



    public interface CameraOptionCallback{
        void changeAspectRatio(int ratioType);
        void changeWhiteBalance(int whiteBalacneType);
        void changeTimerSecond(int timerSec);
        void chageFrame(int frameType);
    }

    private CameraOptionCallback mOptionCallback;

    private RadioGroup mRadiogroupAspectRatio;
    private RadioGroup mRadiogroupWhiteBalance;
    private RadioGroup mRadiogruopTimerSecond;
    private RadioGroup mRadiogroupFrameType;


    public BottomSheetOptionPanelCamera(Context context, CameraOptionCallback cameraOptionCallback) {
        super(context);
        mOptionCallback = cameraOptionCallback;

        addView(inflate(context, R.layout.bottomsheet_camera_option,null)
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
                        mOptionCallback.changeAspectRatio(OptionData.ASPECT_RATIO_9_16);
                    }break;
                    case R.id.radioButton_ratio_34 :{
                        mOptionCallback.changeAspectRatio(OptionData.ASPECT_RATIO_3_4);
                    }break;
                    case R.id.radioButton_ratio_11 :{
                        mOptionCallback.changeAspectRatio(OptionData.ASPECT_RATIO_1_1);
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
                    case R.id.radioButton_whitebalance_auto : {mOptionCallback.changeWhiteBalance(OptionData.WHITEBALANCE_AUTO);}break;
                    case R.id.radioButton_whitebalance_cloudy : {mOptionCallback.changeWhiteBalance(OptionData.WHITEBALANCE_COLUDY);}break;
                    case R.id.radioButton_whitebalance_daylight : {mOptionCallback.changeWhiteBalance(OptionData.WHITEBALANCE_DAYLIGHT);}break;
                    case R.id.radioButton_whitebalance_fluorescent : {mOptionCallback.changeWhiteBalance(OptionData.WHITEBALANCE_FLUORSCENT);}break;
                    case R.id.radioButton_whitebalance_incandscent : {mOptionCallback.changeWhiteBalance(OptionData.WHITEBALANCE_INCANDSCENT);}break;
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
                    case R.id.radioButton_timersec_3 : {mOptionCallback.changeTimerSecond(OptionData.TIMER_SEC_3);}break;
                    case R.id.radioButton_timersec_5 : {mOptionCallback.changeTimerSecond(OptionData.TIMER_SEC_5);}break;
                    case R.id.radioButton_timersec_10 : {mOptionCallback.changeTimerSecond(OptionData.TIMER_SEC_10);}break;
                }
            }
        });
    }

    private void setFrmaeType(){
        mRadiogroupFrameType = findViewById(R.id.radioGroup_frameType);
        mRadiogroupFrameType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_frameType_noFrame : {mOptionCallback.chageFrame(OptionData.FRMAE_TYPE_NO_FRAME);}break;
                    case R.id.radioButton_frameType_type1 : {mOptionCallback.chageFrame(OptionData.FRMAE_TYPE_TYPE_1);}break;

                }
            }
        });
    }

    public void applyAspectRatio(int ratioType){
       switch (ratioType){
           case OptionData.ASPECT_RATIO_1_1 : {mRadiogroupAspectRatio.check(R.id.radioButton_ratio_11);}break;
           case OptionData.ASPECT_RATIO_3_4 : {mRadiogroupAspectRatio.check(R.id.radioButton_ratio_34);}break;
           case OptionData.ASPECT_RATIO_9_16 : {mRadiogroupAspectRatio.check(R.id.radioButton_ratio_916);}break;
       }
    }
    public void applyWhiteBalance(int whiteBalanceType){
        switch (whiteBalanceType){
            case OptionData.WHITEBALANCE_AUTO : {mRadiogroupWhiteBalance.check(R.id.radioButton_whitebalance_auto);}break;
            case OptionData.WHITEBALANCE_COLUDY : {mRadiogroupWhiteBalance.check(R.id.radioButton_whitebalance_cloudy);}break;
            case OptionData.WHITEBALANCE_DAYLIGHT : {mRadiogroupWhiteBalance.check(R.id.radioButton_whitebalance_daylight);}break;
            case OptionData.WHITEBALANCE_FLUORSCENT : {mRadiogroupWhiteBalance.check(R.id.radioButton_whitebalance_fluorescent);}break;
            case OptionData.WHITEBALANCE_INCANDSCENT : {mRadiogroupWhiteBalance.check(R.id.radioButton_whitebalance_incandscent);}break;
        }
    }
    public void applyTimerSecond(int timerSet){
        switch (timerSet){
            case OptionData.TIMER_SEC_3 : {mRadiogruopTimerSecond.check(R.id.radioButton_timersec_3);}break;
            case OptionData.TIMER_SEC_5 : {mRadiogruopTimerSecond.check(R.id.radioButton_timersec_5);}break;
            case OptionData.TIMER_SEC_10 : {mRadiogruopTimerSecond.check(R.id.radioButton_timersec_10);}break;
        }
    }
    public void applyFrmaeType(int frameType){
        switch (frameType){
            case OptionData.FRMAE_TYPE_NO_FRAME : {mRadiogroupFrameType.check(R.id.radioButton_frameType_noFrame);}break;
            case OptionData.FRMAE_TYPE_TYPE_1 : {mRadiogroupFrameType.check(R.id.radioButton_frameType_type1);}break;
        }
    }


}
