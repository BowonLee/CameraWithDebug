package com.bowonlee.dearphotograph.maincamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;
import com.bowonlee.dearphotograph.models.Photo;

/*
* 사용자가 가져온 사진을 Draw 객체를 이용하여 그려준다.
*
* */
public class DrawPhotoFrameView extends BasePhotoDrawerView {


    public DrawPhotoFrameView(Context context) {
        super(context);
    }
}
