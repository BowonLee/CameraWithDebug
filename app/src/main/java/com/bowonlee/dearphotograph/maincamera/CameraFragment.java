package com.bowonlee.dearphotograph.maincamera;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.gallary.PhotoGallaryActivity;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;
import com.bowonlee.dearphotograph.modifier.ModifyPhotoView;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by bowon on 2018-04-18.
 */
public class CameraFragment extends Fragment implements TextureView.SurfaceTextureListener, CameraFunction.CameraFunctionInterface {

    private static final String TAG = "Camera2Preview";

    public static final int RESULT_OK = 6001;
    private AutoFitTextureView mTextureView;

    private RelativeLayout mRootLayout;

    interface CameraInterface{
        void onPostTakePicture(Bitmap bitmap,ModifiedPhoto modifiedPhoto);

    }
    private CameraFragment.CameraInterface cameraInterface;

    /*Fragment's UI*/
    private ArrayList<Button> mButtonGroup;
    private Button mButtonOpenGallary;
    private Button mButtonTakePicture;

    /*camera orientation*/
    int mOrientation = 90;

    private CameraFunction mCameraFunction;
    private ModifiedPhoto mModifiedPhoto;

    private ModifyPhotoView mModifyPhotoView;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootLayout = (RelativeLayout)view.findViewById(R.id.layout_camera_root);
        mTextureView = (AutoFitTextureView)view.findViewById(R.id.texture_camera);
        mCameraFunction = new CameraFunction(getActivity(),mTextureView);
        mCameraFunction.setCameraFunctionInterface(this);

        setButtons(view);
        setModifiedView();

    }
    private void setModifiedView(){
        mModifyPhotoView = new ModifyPhotoView(getContext());
        mModifyPhotoView.setOnTouchListener(mModifyPhotoView);



        getActivity().addContentView(mModifyPhotoView,new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));

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
        startActivityForResult(intent,PhotoGallaryActivity.REQUEST_CODE);

    }
    private void takePicture(){
        mCameraFunction.takePicture();
    }

    @Override
    public void onTakePicture(Bitmap bitmap) {
        cameraInterface.onPostTakePicture(bitmap,mModifiedPhoto);
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

        mCameraFunction.startBackgroundThread();
        if (mTextureView.isAvailable()){
            mCameraFunction.openCamera(mTextureView.getWidth(),mTextureView.getHeight());
            Log.e("Fragment WH",mTextureView.getWidth()+mTextureView.getHeight()+"");
        }else{
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void onPause() {
        mCameraFunction.closeCamera();
        mCameraFunction.stopBackgroundThread();
        super.onPause();

    }




    @Override
    public void onDestroy() {
        super.onDestroy();

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

    public Size getReversePreviewSize(){
        return new Size(mCameraFunction.getPreviewSize().getHeight(),mCameraFunction.getPreviewSize().getWidth());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PhotoGallaryActivity.REQUEST_CODE : {
                Photo photo = data.getParcelableExtra(PhotoGallaryActivity.PARCELABLE_RESULT);
                setmImageOnView(photo);}break;
        }
    }





    public void setmImageOnView(Photo photo){

        mModifiedPhoto = new ModifiedPhoto(photo);
        mModifiedPhoto.setStartXY(new Point(100,100));
        mModifiedPhoto.setOutSize(getPhotoSize(mModifiedPhoto.getImageUri()));
        mModifiedPhoto.setRatio((float) mModifyPhotoView.getReductionRatio(mModifiedPhoto.getOutSize(),getReversePreviewSize()));
        mModifyPhotoView.setPhoto(mModifiedPhoto);

        Log.e("photo",String.format("%s",mModifiedPhoto.getImageUri()));
        mModifyPhotoView.postInvalidate();
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






    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCameraFunction.openCamera(width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mCameraFunction.configureTransform(width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
