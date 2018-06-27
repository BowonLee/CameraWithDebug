package com.bowonlee.dearphotograph.models;

import android.content.Context;
import android.content.SharedPreferences;

public class OptionData {

    private final String prefKey = "settingData";

    public static final String KEY_ASPECT_RATIO = "aspectRatio";
    public static final String KEY_WHITE_BALANCE = "whiteBalance";
    public static final String KEY_TIMER_SET = "timerSet";
    public static final String KEY_CAMERA_FACING = "cameraFacing";
    public static final String KEY_TIMER_ON = "tImerOn";
    public static final String KEY_FLASH_STATE = "flashState";


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

    static public final int FLASH_AUTO      = 0 ;
    static public final int FLASH_ON        = 1 ;
    static public final int FLASH_OFF       = 2 ;

    static public final int CAMERA_FACING_BACK = 41;
    static public final int CAMERA_FACING_FRONT = 42;

    static public final int TIMER_OFF = 51;
    static public final int TIMER_ON = 52;


    private SharedPreferences mPreference;
    private SharedPreferences.Editor mEditor;

    public OptionData(Context context){

        mPreference = context.getSharedPreferences(prefKey,Context.MODE_PRIVATE);
        mEditor = mPreference.edit();
    }

    public void setData(String key,int value){

        mEditor.putInt(key, value);
        mEditor.commit();

    }

    public int getSingleData(String key){
        return mPreference.getInt(key,0);
    }



    public int[] getAllData(){
        int[] result = new int[6];

        result[0] = mPreference.getInt(KEY_ASPECT_RATIO,ASPECT_RATIO_3_4);
        result[1] = mPreference.getInt(KEY_WHITE_BALANCE,WHITEBALANCE_AUTO);
        result[2] = mPreference.getInt(KEY_TIMER_SET,TIMER_SEC_3);
        result[3] = mPreference.getInt(KEY_CAMERA_FACING,CAMERA_FACING_BACK);
        result[4] = mPreference.getInt(KEY_TIMER_ON,TIMER_OFF);
        result[5] = mPreference.getInt(KEY_FLASH_STATE,FLASH_AUTO);

        return result;

    }










}
