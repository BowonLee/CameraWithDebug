package com.bowonlee.dearphotographdebug.models;

import android.graphics.PointF;
import android.net.Uri;
import android.util.Size;


/*
* 사진 수정 단계을 거친 후의 사진 데이터
* 원본 사진에 비해 축소된 비율과 그려져야 할 시작 좌표를 추가한다.
* */
public class ModifiedPhoto extends Photo{
    public static final String EXTRA_CODE = "Modified Photo";

    private float ratio;
    private PointF startXY;
    private int rotation;

    // 동적인 축소비율계산을 위한 원본사진의 크기
    private Size outSize;


    public ModifiedPhoto(Uri thumnail, Uri image) {
        super(thumnail, image);
    }
    public ModifiedPhoto(Photo photo){
        super(photo.getThumnailUri(),photo.getImageUri());
    }

    public ModifiedPhoto(ModifiedPhoto modifiedPhoto){
        super(modifiedPhoto.getThumnailUri(),modifiedPhoto.getImageUri());
        this.ratio = modifiedPhoto.getRatio();
        this.startXY = modifiedPhoto.getStartXY();
        this.outSize = modifiedPhoto.getOutSize();
        this.rotation = modifiedPhoto.getRotation();


    }

    public void setRatio(float ratio){
        this.ratio = ratio;
    }
    public float getRatio() {
        return ratio;
    }

    public int getRotation() { return rotation; }
    public void setRotation(int rotation) { this.rotation = rotation; }

    public PointF getStartXY() {
        return startXY;
    }
    public void setStartXY(PointF startXY){
        this.startXY = startXY;
    }

    public Size getOutSize() { return outSize; }

    public void setOutSize(Size outSize) { this.outSize = outSize; }
}
