package com.bowonlee.dearphotograph.imagecrop;

import android.app.Fragment;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bowonlee.dearphotograph.R;
import com.theartofdev.edmodo.cropper.CropImageView;

/*
*CropImageView를 적용하여 CropImage를 실제로 수행하는 프레그먼트
* 프래그먼트로 구성한 이유는 Crop의 Option을 변경하고 변경하상을 즉각적으로 적용하기 위해서이다.
* 또한 이미지를 Crop하는 모양 (타원, 크기, 좌우비)의 Overlay Fragment를 구성하기 위해서이다.
* */
public class CropFragment extends Fragment
        implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener{

   private CropImageView mCropImageView;

   private CropDemoPreset mCropDemoPreset;

   public static CropFragment newInstance(CropDemoPreset demoPreset){
    CropFragment fragment = new CropFragment();
    Bundle args = new Bundle();
    args.putString("DECO_PRESET",demoPreset.name());
    fragment.setArguments(args);
    return fragment;
   }


   public void setCropImageViewOptions(CropImageViewOptions options){
    mCropImageView.setScaleType(options.scaleType);
    mCropImageView.setCropShape(options.cropShape);
    mCropImageView.setGuidelines(options.guidelines);
    mCropImageView.setAspectRatio(options.aspectRatio.first, options.aspectRatio.second);
    mCropImageView.setFixedAspectRatio(options.fixAspectRatio);
    mCropImageView.setMultiTouchEnabled(options.multitouch);
    mCropImageView.setShowCropOverlay(options.showCropOverlay);
    mCropImageView.setShowProgressBar(options.showProgressBar);
    mCropImageView.setAutoZoomEnabled(options.autoZoomEnabled);
    mCropImageView.setMaxZoom(options.maxZoomLevel);
    mCropImageView.setFlippedHorizontally(options.flipHorizontally);
    mCropImageView.setFlippedVertically(options.flipVertically);
   }


   public void setInitialCropRect(){mCropImageView.setCropRect(new Rect(100,300,500,1200));}

   public void resetCropRect(){mCropImageView.resetCropRect();}

   public void updateCurrentCropViewOptions() {
    CropImageViewOptions options = new CropImageViewOptions();
    options.scaleType = mCropImageView.getScaleType();
    options.cropShape = mCropImageView.getCropShape();
    options.guidelines = mCropImageView.getGuidelines();
    options.aspectRatio = mCropImageView.getAspectRatio();
    options.fixAspectRatio = mCropImageView.isFixAspectRatio();
    options.showCropOverlay = mCropImageView.isShowCropOverlay();
    options.showProgressBar = mCropImageView.isShowProgressBar();
    options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
    options.maxZoomLevel = mCropImageView.getMaxZoom();
    options.flipHorizontally = mCropImageView.isFlippedHorizontally();
    options.flipVertically = mCropImageView.isFlippedVertically();
    ((CropActivity) getActivity()).setCurrentOptions(options);
    }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
   }

 /*crop 시킬 이미지를 Uri를 통해 셋팅한다. */
    public void setImageUri(Uri imageUri){
        mCropImageView.setImageUriAsync(imageUri);
    }

 @Override
 public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
  super.onViewCreated(view, savedInstanceState);

  mCropImageView = view.findViewById(R.id.cropImageView);
  mCropImageView.setOnSetImageUriCompleteListener(this);
  mCropImageView.setOnCropImageCompleteListener(this);

  updateCurrentCropViewOptions();

  if(savedInstanceState == null){
   if(mCropDemoPreset == CropDemoPreset.SCALE_CENTER_INSIDE){
    mCropImageView.setImageResource(R.drawable.cafe_demoimage);
   }else{
    mCropImageView.setImageResource(R.drawable.cafe_demoimage);
   }
  }
 }

 @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
     if (error == null){
      // sucecss
      Toast.makeText(getActivity(),"Image load successful",Toast.LENGTH_SHORT).show();
     }else {
      //if error
     }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
     handleCropResult(result);
    }

    private void handleCropResult(CropImageView.CropResult result){
     if(result.getError()==null){

     }

    }

}
