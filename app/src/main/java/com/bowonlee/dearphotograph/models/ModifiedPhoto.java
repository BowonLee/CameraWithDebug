package com.bowonlee.dearphotograph.models;

import android.graphics.Point;
import android.net.Uri;
import android.util.Size;


/*
* 사진 수정 단계을 거친 후의 사진 데이터
* 원본 사진에 비해 축소된 비율과 그려져야 할 시작 좌표를 추가한다.
* */
public class ModifiedPhoto extends Photo{
    public static final String EXTRA_CODE = "Modified Photo";

    private float ratio;
    private Point startXY;
    private int orientation;

    // 동적인 축소비율계산을 위한 원본사진의 크기
    private Size outSize;



    public ModifiedPhoto(Uri thumnail, Uri image) {
        super(thumnail, image);
    }
    public ModifiedPhoto(Photo photo){
        super(photo.getThumnailUri(),photo.getImageUri());
    }

    public void setRatio(float ratio){
        this.ratio = ratio;
    }
    public float getRatio() {
        return ratio;
    }

    //public int getOrientation() { return orientation; }
    public void setOrientation(int orientation) { this.orientation = orientation; }

    public Point getStartXY() {
        return startXY;
    }
    public void setStartXY(Point startXY){
        this.startXY = startXY;
    }

    public Size getOutSize() { return outSize; }

    public void setOutSize(Size outSize) { this.outSize = outSize; }
}
