package com.bowonlee.dearphotograph.resultpreview;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.BitmapSaver;
import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.otaliastudios.cameraview.CameraView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;

import java.util.ArrayList;
import java.util.Collections;

public class PreviewResultFragment extends Fragment {

    public interface PreviewResultInterface{

        public void onCancelPreviewResult();

    }

    private PreviewResultInterface mPreviewResultInterface;

    private final String TAG = "PreviewResultFragment";

    /*UI*/
    private ArrayList<Button> mButtonGruop;
    private Button mButtonSaveImage;
    private Button mButtonCancel;

    private PreviewResultView mPreviewResultView;
    private Bitmap mCapturedBitmap;
    private RelativeLayout mParentLayout;
    private ModifiedPhoto mModifiedPhoto;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreviewResultView = new PreviewResultView(getActivity(),mCapturedBitmap,mModifiedPhoto);

        mParentLayout = (RelativeLayout)view.findViewById(R.id.layout_preview_result);



        mPreviewResultView.setPhoto(mModifiedPhoto);
        mPreviewResultView.setOnTouchListener(mPreviewResultView);

        mParentLayout.addView(mPreviewResultView);
        //this.getActivity().addContentView(mPreviewResultView,new ActionBar.LayoutParams(1080,1920));
        mButtonSaveImage = (Button)view.findViewById(R.id.btn_fragment_preview_result_save);
        mButtonCancel = (Button)view.findViewById(R.id.btn_fragment_preview_result_cancel);

        settingButtons();


        mPreviewResultView.postInvalidate();


    }

    @Override
    public void onResume() {
        super.onResume();
        mButtonSaveImage.bringToFront();
        mButtonCancel.bringToFront();

    }
    public void setModifiedPhoto(ModifiedPhoto modifiedPhoto){
        //this.mModifiedPhoto = modifiedPhoto;
        this.mModifiedPhoto = new ModifiedPhoto(modifiedPhoto);
     //   mModifiedPhoto.setOutSize(mModifiedPhoto.getOutSize());
    //    mModifiedPhoto.setRotation(modifiedPhoto.getRotation());
    }

    public void setCapturedBitmap(Bitmap capturedBitmap){
        this.mCapturedBitmap = capturedBitmap;
    }
    private void settingButtons(){

        mButtonGruop = new ArrayList<>();
        mButtonGruop.add(mButtonSaveImage);
        mButtonGruop.add(mButtonCancel);

        for(Button btn : mButtonGruop){
            mParentLayout.bringChildToFront(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_preview_result_cancel :{
                            mPreviewResultInterface.onCancelPreviewResult();
                            onDestroy();}break;
                        case R.id.btn_fragment_preview_result_save : {
                            saveView(mPreviewResultView);
                        }break;
                    }
                }
            });
        }

    }

    private void saveView(View view){

        ModifiedPhoto tempphoto = new ModifiedPhoto(mModifiedPhoto);
        float resizeRatio = (float) (1080.0/720.0);
        Log.e("resize",resizeRatio+"");
        tempphoto.setRatio((tempphoto.getRatio()*resizeRatio));
        tempphoto.setStartXY(new PointF(mModifiedPhoto.getStartXY().x*resizeRatio,mModifiedPhoto.getStartXY().y*resizeRatio));
        Log.e("Result XY",String.format("(%f,%f),(%f,%f)",mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,tempphoto.getStartXY().x,tempphoto.getStartXY().y));
        PreviewResultView tempview = new PreviewResultView(getActivity(),Bitmap.createScaledBitmap(mCapturedBitmap,1080,1920,false),tempphoto);
        tempview.setPhoto(tempphoto);
        tempview.setPhotoRotation(tempphoto.getRotation());
        tempview.postInvalidate();


        //Bitmap b = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Bitmap b = Bitmap.createBitmap(1080,1920,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        //view.draw(c);
        tempview.draw(c);
        new BitmapSaver(b,getActivity()).run();
//        new BitmapSaver(Bitmap.createScaledBitmap(b,1080,1920,false),getActivity()).run();

        mPreviewResultView = null;

        mPreviewResultInterface.onCancelPreviewResult();


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_preview_result,container,false);
    }
    public void setPreviewResultInterface(PreviewResultInterface previewResultInterface){
        this.mPreviewResultInterface = previewResultInterface;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }


    private Size getLargestSize(){



        return null;
    }
}
