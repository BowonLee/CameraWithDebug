package com.bowonlee.dearphotograph.imagecrop;

import android.util.Pair;

import com.theartofdev.edmodo.cropper.CropImageView;

public class CropImageViewOptions {
    /*런타임에서 즉각적으로 변경되는 옵션들*/
    /*상태변경을 저장하는 프리퍼런스를 이용하여 옵션값을 저장할 수 있도록 구현하자.*/
    public CropImageView.ScaleType scaleType = CropImageView.ScaleType.CENTER_INSIDE;

    public CropImageView.CropShape cropShape = CropImageView.CropShape.RECTANGLE;

    public CropImageView.Guidelines guidelines = CropImageView.Guidelines.ON_TOUCH;

    public Pair<Integer, Integer> aspectRatio = new Pair<>(1, 1);

    public boolean autoZoomEnabled;

    public int maxZoomLevel;

    public boolean fixAspectRatio;

    public boolean multitouch;

    public boolean showCropOverlay;

    public boolean showProgressBar;

    public boolean flipHorizontally;

    public boolean flipVertically;
}
