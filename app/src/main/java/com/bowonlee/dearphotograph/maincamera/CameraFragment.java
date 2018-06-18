package com.bowonlee.dearphotograph.maincamera;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bowonlee.dearphotograph.BottomSheetOptionPanelCamera;
import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.gallary.RecentPhotoLoader;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;
import com.bowonlee.dearphotograph.modifier.ModifyPhotoActivity;
import com.otaliastudios.cameraview.AspectRatio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.SizeSelectors;
import com.otaliastudios.cameraview.WhiteBalance;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by bowon on 2018-04-18.
 */
public class CameraFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Photo> {

    private static final String TAG = "Camera2PreviewFragment";


    private RelativeLayout mRootLayout;

    private final int FLASH_AUTO = 2;
    private final int FLASH_ON = 1;
    private final int FLASH_OFF = 0;

    private long mTimerSet = 0;
    private long mTimerSettingValue = 3000;

    interface CameraInterface{
        void onPostTakePicture(Bitmap bitmap,ModifiedPhoto modifiedPhoto);

    }
    private CameraFragment.CameraInterface cameraInterface;

    /*Fragment's UI*/
    private ArrayList<ImageButton> mButtonGroup;
    private ImageButton mButtonOpenGallary;
    private ImageButton mButtonTakePicture;

    private ImageButton mButtonRotatePicture;
    private CheckBox mCheckBoxCameraFacing;
    private CheckBox mCheckBoxTimerActivate;
    private ImageButton mButtonFlashState;
    private ImageButton mButtonMoreOption;





    private TextView mTextviewCountDown;

    private int mStateFlash = 0;
    /*CameraSetting - BottomSheetBehavior with NestedScrollView*/
    private BottomSheetBehavior mBottomSheetBehavior;


    /*CameraView */
    private CameraView mCameraView;


    /*camera orientation*/
    private int mOrientation = 90;

    private int mPhotoRotation = 0;
    private MainPhotoDrawerView mMainPhotoDrawerView;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootLayout = (RelativeLayout)view.findViewById(R.id.layout_camera_root);
        mCameraView = (CameraView)view.findViewById(R.id.cameraview_fragment_camera);


        setButtons(view);
        setCheckbox(view);
        setModifiedView();
        setCameraView();
        setBottonSheet(view);
        setAutoFlashButton(view);
        getLoaderManager().initLoader(0,null,this);


    }

    private void setBottonSheet(View view){
        LinearLayout bottomSheetLayout = (LinearLayout)view.findViewById(R.id.bottom_sheet_fragment_camera_root);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetLayout.addView(new BottomSheetOptionPanelCamera(getContext(), new BottomSheetOptionPanelCamera.CameraOptionCallback() {
            @Override
            public void changeAspectRatio(int ratioType) {

                switch (ratioType){
                    case BottomSheetOptionPanelCamera.ASPECT_RATIO_9_16 : {

                        mCameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(9,16), 0));
                    }break;
                    case BottomSheetOptionPanelCamera.ASPECT_RATIO_3_4 : {
                        mCameraView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                        mCameraView.getLayoutParams().height = getResources().getDisplayMetrics().widthPixels*4/3;
                        mCameraView.setLayoutParams(mCameraView.getLayoutParams());
                        mCameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(3,4), 0));
                    }break;
                    case BottomSheetOptionPanelCamera.ASPECT_RATIO_1_1 : {
                        mCameraView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                        mCameraView.getLayoutParams().height = getResources().getDisplayMetrics().widthPixels;
                        mCameraView.setLayoutParams(mCameraView.getLayoutParams());

                        mCameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(1,1), 0));

                    }break;
                }
                mCameraView.stop();
                mCameraView.start();

            }

            @Override
            public void changeWhiteBalance(int whiteBalacneType) {
                switch (whiteBalacneType){
                    case BottomSheetOptionPanelCamera.WHITEBALANCE_AUTO : {mCameraView.setWhiteBalance(WhiteBalance.AUTO);}break;
                    case BottomSheetOptionPanelCamera.WHITEBALANCE_COLUDY : {mCameraView.setWhiteBalance(WhiteBalance.CLOUDY);}break;
                    case BottomSheetOptionPanelCamera.WHITEBALANCE_DAYLIGHT : {mCameraView.setWhiteBalance(WhiteBalance.DAYLIGHT);}break;
                    case BottomSheetOptionPanelCamera.WHITEBALANCE_INCANDSCENT : {mCameraView.setWhiteBalance(WhiteBalance.INCANDESCENT);}break;
                    case BottomSheetOptionPanelCamera.WHITEBALANCE_FLUORSCENT : {mCameraView.setWhiteBalance(WhiteBalance.FLUORESCENT);}break;
                }

            }

            @Override
            public void changeTimerSecond(int timerSec) {

                switch (timerSec){
                    case BottomSheetOptionPanelCamera.TIMER_SEC_3 : {mTimerSettingValue = 3000;}break;
                    case BottomSheetOptionPanelCamera.TIMER_SEC_5 : {mTimerSettingValue = 5000;}break;
                    case BottomSheetOptionPanelCamera.TIMER_SEC_10 : {mTimerSettingValue = 10000;}break;
                }
            }

        }));

    }


    private void setAutoFlashButton(View view){
        mButtonFlashState = (ImageButton)view.findViewById(R.id.btn_fragment_camera_flash);

        setFlashSetting();
        mButtonFlashState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStateFlash = (mStateFlash+1)%3;
                setFlashSetting();
            }
        });

    }
    private void setFlashSetting(){
        switch (mStateFlash){
            case FLASH_AUTO : { mCameraView.setFlash(Flash.AUTO);
           mButtonFlashState.setImageResource(R.drawable.baseline_flash_auto_white_18);}break;
            case FLASH_ON :   {mButtonFlashState.setImageResource(R.drawable.baseline_flash_on_white_18); mCameraView.setFlash(Flash.ON); }break;
            case FLASH_OFF :  {mButtonFlashState.setImageResource(R.drawable.baseline_flash_off_white_18); mCameraView.setFlash(Flash.OFF);}
        }
    }


    private void setCheckbox(View view){
        mCheckBoxCameraFacing = (CheckBox)view.findViewById(R.id.checkbox_fragment_camera_facing);
        mCheckBoxCameraFacing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.checkbox_fragment_camera_facing : { setCameraFacing(isChecked);}
                }
            }
        });

        mTextviewCountDown = (TextView)view.findViewById(R.id.textview_fragment_camera_timertext);
        mCheckBoxTimerActivate = (CheckBox)view.findViewById(R.id.checkbox_fragment_timer_activate);
        mCheckBoxTimerActivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTimerSet = 0;
                }else{
                    mTimerSet = mTimerSettingValue;
                }
            }
        });

    }
    private void setCameraFacing(boolean checked_state){
        if(checked_state){
            mCameraView.setFacing(Facing.FRONT);
        }else{
            mCameraView.setFacing(Facing.BACK);
        }
    }

    private void setCameraView(){

        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                Bitmap result;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;

                result = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(jpeg,0,jpeg.length,options),
                        mCameraView.getHeight(),mCameraView.getWidth(),false);

                Matrix matrix = new Matrix();


                if(mCameraView.getFacing() == Facing.FRONT){
                    matrix.postRotate(270);
                    matrix.postScale(-1,1);

                }else{
                    matrix.postRotate(90);
                }
                result = Bitmap.createBitmap(result,0,0,result.getWidth(),result.getHeight(),matrix,true);


                cameraInterface.onPostTakePicture(result, mMainPhotoDrawerView.getModifiedPhoto());


            }

        });

        mCameraView.setLayoutParams(mCameraView.getLayoutParams());

    }

    private void setModifiedView(){
        mMainPhotoDrawerView = new MainPhotoDrawerView(getActivity());
        mMainPhotoDrawerView.setOnTouchListener(mMainPhotoDrawerView);
        mRootLayout.addView(mMainPhotoDrawerView);

    }

    private void setButtons(View view){
        mButtonGroup = new ArrayList<>();
        mButtonTakePicture = (ImageButton)view.findViewById(R.id.btn_fragment_camera_takepicture);
        mButtonOpenGallary= (ImageButton)view.findViewById(R.id.btn_fragment_camera_open_gallary);
        mButtonRotatePicture = (ImageButton)view.findViewById(R.id.btn_fragment_camera_rotate_picture);
        mButtonMoreOption = (ImageButton)view.findViewById(R.id.btn_fragment_camera_more_option);


        mButtonGroup.add(mButtonOpenGallary);
        mButtonGroup.add(mButtonTakePicture);
        mButtonGroup.add(mButtonRotatePicture);
        mButtonGroup.add(mButtonMoreOption);
        for(ImageButton button : mButtonGroup){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_camera_more_option : {oprnButtonSheetOptionArea();}break;
                        case R.id.btn_fragment_camera_open_gallary : {openGallary();}break;
                        case R.id.btn_fragment_camera_takepicture : {takePicture();}break;
                        case R.id.btn_fragment_camera_rotate_picture : {rotatePicture();};break;
                    }
                }

            });
        }

    }
    private void oprnButtonSheetOptionArea(){
        //mButtom
        mBottomSheetBehavior.setSkipCollapsed(true);

        if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else{
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    private void openGallary(){
        Intent intent = new Intent(getActivity(), ModifyPhotoActivity.class);
        this.startActivityForResult(intent,ModifyPhotoActivity.REQUEST_CODE);
    }
    private void takePicture(){
        if(mMainPhotoDrawerView.getModifiedPhoto() != null){

            CountDownTimer timer = new CountDownTimer(mTimerSet,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTextviewCountDown.setVisibility(View.VISIBLE);
                    mTextviewCountDown.setText(
                                String.valueOf((millisUntilFinished)/1000)
                            );

                }
                @Override
                public void onFinish() {
                    mTextviewCountDown.setVisibility(View.GONE);
                    mCameraView.capturePicture();
                }
            };

            timer.start();
        }else{
            Toast.makeText(getActivity(),"Not Placed Photo" , Toast.LENGTH_SHORT).show();
        }

    }





    private void rotatePicture(){
        if(mMainPhotoDrawerView.getModifiedPhoto()!=null) {
            mPhotoRotation = (mPhotoRotation + 90)%360;
            mMainPhotoDrawerView.setPhotoRotation(mPhotoRotation);

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera,container,false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();

        getLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
        Log.e(TAG,"destroy");
    }



    public static CameraFragment newInstance(){return new CameraFragment();}
    public void setOnCameraInterface(CameraInterface cameraInterface){
        this.cameraInterface = cameraInterface;
    }

    public void refreshFragment(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }


    public void setOrientation(int orientation){
        mOrientation = orientation;
    }


    public Size getCameraPreviewSize(){
        //return new Size(mCameraView.getWidth(),mCameraView.getHeight());
        return new Size(720,1280);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("result",String.format("%d,%d",requestCode,resultCode));
        switch (requestCode){
            case ModifyPhotoActivity.REQUEST_CODE : {
                if(resultCode == RESULT_OK){
                    Photo photo = data.getParcelableExtra(String.valueOf(R.string.parcelable_result));
                    setmImageOnView(photo);
                }break;
            }
        }
    }





     void setmImageOnView(Photo photo){

        ModifiedPhoto modifiedPhoto = new ModifiedPhoto(photo);

        modifiedPhoto.setStartXY(new PointF(100,100));
        modifiedPhoto.setOutSize(getPhotoSize(modifiedPhoto.getImageUri()));
        modifiedPhoto.setRatio((float) mMainPhotoDrawerView.getReductionRatio(modifiedPhoto.getOutSize(),getCameraPreviewSize()));


        this.mMainPhotoDrawerView.setPhoto(modifiedPhoto);
        Log.e("dummy",String.format("%s , %s",modifiedPhoto.getImageUri(),modifiedPhoto.getThumnailUri()));

        this.mMainPhotoDrawerView.postInvalidate();



    }

    private Size getPhotoSize(Uri photoUri){
        Size result ;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(photoUri.getPath(),options);
        result = new Size(options.outWidth,options.outHeight);
        Log.e("photo",String.format("%d,%d",options.outWidth,options.outHeight));

        return result;
    }




    @Override
    public android.support.v4.content.Loader<Photo> onCreateLoader(int id, Bundle args) {

        return new RecentPhotoLoader(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Photo> loader, Photo data) {
        if(data !=null){
        mButtonOpenGallary.setImageURI(data.getThumnailUri());}

    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Photo> loader) {

    }




}
