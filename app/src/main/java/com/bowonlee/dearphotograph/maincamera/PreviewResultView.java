package com.bowonlee.dearphotograph.maincamera;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;

public class PreviewResultView extends BasePhotoDrawerView{

    public PreviewResultView(Context context) {
        super(context);
    }

    public PreviewResultView(Context context,ModifiedPhoto modifiedPhoto,Bitmap captureResult){
        super(context,modifiedPhoto);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
