package com.bowonlee.dearphotograph.maincamera;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
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
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.gallary.PhotoGallaryActivity;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by bowon on 2018-04-18.
 */
public class CameraFragment extends Fragment {

    private static final String TAG = "Camera2PreviewFragment";

    public static final int RESULT_OK = 6001;

    private RelativeLayout mRootLayout;

    interface CameraInterface{
        void onPostTakePicture(Bitmap bitmap,ModifiedPhoto modifiedPhoto);

    }
    private CameraFragment.CameraInterface cameraInterface;

    /*Fragment's UI*/
    private ArrayList<Button> mButtonGroup;
    private Button mButtonOpenGallary;
    private Button mButtonTakePicture;
    private CameraView mCameraView;
    /*camera orientation*/
    int mOrientation = 90;

    private MainPhotoDrawerView mMainPhotoDrawerView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootLayout = (RelativeLayout)view.findViewById(R.id.layout_camera_root);
        mCameraView = (CameraView)view.findViewById(R.id.cameraview_fragment_camera);

        setButtons(view);
        setModifiedView();
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
                matrix.postRotate(90);

                result = Bitmap.createBitmap(result,0,0,result.getWidth(),result.getHeight(),matrix,true);

                cameraInterface.onPostTakePicture(result, mMainPhotoDrawerView.getModifiedPhoto());

            }
        });
    }



    private void setModifiedView(){


        mMainPhotoDrawerView = new MainPhotoDrawerView(getActivity());

        mMainPhotoDrawerView.setOnTouchListener(mMainPhotoDrawerView);



        mRootLayout.addView(mMainPhotoDrawerView);
        Log.e(TAG,"addview");
       //getActivity().addContentView(mModifyPhotoView,new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));

    }

    private void setButtons(View view){
        mButtonGroup = new ArrayList<>();
        mButtonTakePicture = (Button)view.findViewById(R.id.btn_fragment_camera_takepicture);
        mButtonOpenGallary= (Button)view.findViewById(R.id.btn_fragment_camera_open_gallary);

        mButtonGroup.add(mButtonOpenGallary);
        mButtonGroup.add(mButtonTakePicture);

        for(Button button : mButtonGroup){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_camera_open_gallary : {openGallary();}break;
                        case R.id.btn_fragment_camera_takepicture : {takePicture();}break;

                    }
                }

            });
        }

    }

    private void openGallary(){
        Intent intent = new Intent(getActivity(), PhotoGallaryActivity.class);

        this.startActivityForResult(intent,PhotoGallaryActivity.REQUEST_CODE);

    }
    private void takePicture(){
        mCameraView.capturePicture();

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

    static class CompareSizeByArea implements Comparator<Size>{
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long)lhs.getWidth() * lhs.getHeight() - (long)rhs.getWidth() * rhs.getHeight());
        }
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
            case PhotoGallaryActivity.REQUEST_CODE : {
                if(resultCode == RESULT_OK){
                    Photo photo = data.getParcelableExtra(PhotoGallaryActivity.PARCELABLE_RESULT);
                    setmImageOnView(photo);
                }break;
            }
        }
    }





     void setmImageOnView(Photo photo){

        ModifiedPhoto modifiedPhoto = new ModifiedPhoto(photo);

        modifiedPhoto.setStartXY(new Point(100,100));
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
