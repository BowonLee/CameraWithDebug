package com.bowonlee.dearphotograph.resultpreview;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
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

    public final int MAX_OUTPUT_WIDTH = 3120;
    public final int MAX_OUTPUT_HEIGHT = 4160;


    public interface PreviewResultInterface{

        public void onCancelPreviewResult();

    }

    private PreviewResultInterface mPreviewResultInterface;

    private final String TAG = "PreviewResultFragment";

    /*UI*/
    private ArrayList<ImageButton> mButtonGruop;
    private ImageButton mButtonSaveImage;
    private ImageButton mButtonCancel;

    private PreviewResultView mPreviewResultView;
    private Bitmap mCapturedBitmap;
    private RelativeLayout mParentLayout;
    private ModifiedPhoto mModifiedPhoto;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreviewResultView = new PreviewResultView(getActivity(),mCapturedBitmap);

        mParentLayout = (RelativeLayout)view.findViewById(R.id.layout_preview_result);


        Log.e("before",""+mModifiedPhoto.getStartXY().y);
        mModifiedPhoto.setStartXY(new PointF(
                mModifiedPhoto.getStartXY().x,
                (float)( ((mModifiedPhoto.getStartXY().y -  (float)( (float)getResources().getDisplayMetrics().widthPixels*16.0/9.0 - (float)mCapturedBitmap.getHeight())/2.0)
                        )

                )
        ));
        Log.e("after",""+mModifiedPhoto.getStartXY().y+" : "+ (float)mCapturedBitmap.getHeight()/((float) getResources().getDisplayMetrics().widthPixels*16.0/9.0)
        +" : " + (float)( (float)getResources().getDisplayMetrics().widthPixels*16.0/9.0 - (float)mCapturedBitmap.getHeight())/2.0
        );
        mPreviewResultView.setPhoto(mModifiedPhoto);
        mParentLayout.addView(mPreviewResultView);

        mButtonSaveImage = (ImageButton)view.findViewById(R.id.btn_fragment_preview_result_save);
        mButtonCancel = (ImageButton)view.findViewById(R.id.btn_fragment_preview_result_cancel);

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
    }

    public void setCapturedBitmap(Bitmap capturedBitmap){
        this.mCapturedBitmap = capturedBitmap;
    }
    private void settingButtons(){

        mButtonGruop = new ArrayList<>();
        mButtonGruop.add(mButtonSaveImage);
        mButtonGruop.add(mButtonCancel);

        for(ImageButton btn : mButtonGruop){
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

        float outputAspectRatio = (float) mCapturedBitmap.getWidth()/(float)mCapturedBitmap.getHeight();
        float expendRatio = (float) tempphoto.getOutSize().getWidth()*tempphoto.getRatio()/(float) getResources().getDisplayMetrics().widthPixels;

        float outputWidth =  (float) tempphoto.getOutSize().getWidth()/expendRatio;

        if(outputAspectRatio == 9/16){
            if(outputWidth / (9/16)  > 4160){
                outputWidth = 2340;
            }
        }else{
            if(outputWidth > 3120){
                outputWidth = MAX_OUTPUT_WIDTH;
            }
        }
        Log.e("outputValue",String.format("%f %f %f",outputWidth, outputAspectRatio , expendRatio));

        float resizeRatio =  (outputWidth/(float) getResources().getDisplayMetrics().widthPixels);



        tempphoto.setRatio((tempphoto.getRatio()*resizeRatio));
        tempphoto.setStartXY(new PointF(mModifiedPhoto.getStartXY().x*resizeRatio,mModifiedPhoto.getStartXY().y*resizeRatio));

        PreviewResultView tempview = new PreviewResultView(getActivity(),
                Bitmap.createScaledBitmap(mCapturedBitmap,(int) outputWidth,(int) (outputWidth/outputAspectRatio),false),
                tempphoto);

        tempview.setPhoto(tempphoto);
        tempview.setPhotoRotation(tempphoto.getRotation());
        tempview.postInvalidate();


        Bitmap b = Bitmap.createBitmap((int) outputWidth,(int) (outputWidth/outputAspectRatio),Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        tempview.draw(c);
        new BitmapSaver(b,getActivity()).run();

        mPreviewResultView = null;

        mPreviewResultInterface.onCancelPreviewResult();

/*
        Bitmap b = Bitmap.createBitmap(mCapturedBitmap.getWidth(),mCapturedBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);
        new BitmapSaver(Bitmap.createScaledBitmap(b,mCapturedBitmap.getWidth(), mCapturedBitmap.getHeight(),false),getActivity()).run();
        mPreviewResultView = null;

        mPreviewResultInterface.onCancelPreviewResult();
*/
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
