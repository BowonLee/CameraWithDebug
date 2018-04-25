package com.bowonlee.dearphotograph.imagecrop;

import android.app.Fragment;
import android.net.Uri;

import com.theartofdev.edmodo.cropper.CropImageView;

/*
*CropImageView를 적용하여 CropImage를 실제로 수행하는 프레그먼트
* 프래그먼트로 구성한 이유는 Crop의 Option을 변경하고 변경하상을 즉각적으로 적용하기 위해서이다.
* 또한 이미지를 Crop하는 모양 (타원, 크기, 좌우비)의 Overlay Fragment를 구성하기 위해서이다.
* */
public class CropFragment extends Fragment
        implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener{

   private CropImageView mCropImageView;










    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {

    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {

    }
}
