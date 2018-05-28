package com.bowonlee.dearphotograph.maincamera;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.gallary.PhotoGallaryActivity;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;
import com.bowonlee.dearphotograph.modifier.ModifyPhotoActivity;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;

import java.util.ArrayList;
import java.util.Comparator;

import static android.app.Activity.RESULT_OK;

/**
 * Created by bowon on 2018-04-18.
 */
public class CameraFragment extends Fragment {

    private static final String TAG = "Camera2PreviewFragment";


    private RelativeLayout mRootLayout;

    interface CameraInterface{
        void onPostTakePicture(Bitmap bitmap,ModifiedPhoto modifiedPhoto);

    }
    private CameraFragment.CameraInterface cameraInterface;

    /*Fragment's UI*/
    private ArrayList<Button> mButtonGroup;
    private Button mButtonOpenGallary;
    private Button mButtonTakePicture;
    private Button mButtonRotatePicture;
    private CheckBox mCheckBoxCameraFacing;

    /*Fragment's UI - DrawerLayout*/

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

                Log.e("rotate",""+mMainPhotoDrawerView.getModifiedPhoto().getRotation());
                cameraInterface.onPostTakePicture(result, mMainPhotoDrawerView.getModifiedPhoto());

            }
        });



    }


    private void setModifiedView(){
        mMainPhotoDrawerView = new MainPhotoDrawerView(getActivity());
        mMainPhotoDrawerView.setOnTouchListener(mMainPhotoDrawerView);
        mRootLayout.addView(mMainPhotoDrawerView);

    }

    private void setButtons(View view){
        mButtonGroup = new ArrayList<>();
        mButtonTakePicture = (Button)view.findViewById(R.id.btn_fragment_camera_takepicture);
        mButtonOpenGallary= (Button)view.findViewById(R.id.btn_fragment_camera_open_gallary);
        mButtonRotatePicture = (Button)view.findViewById(R.id.btn_fragment_camera_rotate_picture);

        mButtonGroup.add(mButtonOpenGallary);
        mButtonGroup.add(mButtonTakePicture);
        mButtonGroup.add(mButtonRotatePicture);

        for(Button button : mButtonGroup){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_camera_open_gallary : {openGallary();}break;
                        case R.id.btn_fragment_camera_takepicture : {takePicture();}break;
                        case R.id.btn_fragment_camera_rotate_picture : {rotatePicture();};break;
                    }
                }

            });
        }

    }


    private void openGallary(){
        Intent intent = new Intent(getActivity(), ModifyPhotoActivity.class);
        this.startActivityForResult(intent,ModifyPhotoActivity.REQUEST_CODE);
    }
    private void takePicture(){
        mCameraView.capturePicture();

    }
    private void rotatePicture(){
        if(mMainPhotoDrawerView!=null) {
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








}
