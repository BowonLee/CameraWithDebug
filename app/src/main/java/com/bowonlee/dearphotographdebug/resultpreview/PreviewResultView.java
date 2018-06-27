package com.bowonlee.dearphotograph.resultpreview;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;
import com.bowonlee.dearphotograph.maincamera.MainPhotoDrawerView;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;

public class PreviewResultView extends BasePhotoDrawerView{

    private Bitmap mCapturedBitmap;


    public PreviewResultView(Context context, Bitmap capturedBitmap){
        super(context);
        this.mCapturedBitmap = capturedBitmap;
    }

    public PreviewResultView(Context context) {
        super(context);
    }

    public PreviewResultView(Context context, Bitmap scaledBitmap, ModifiedPhoto tempphoto) {
        super(context);
        this.mCapturedBitmap = scaledBitmap;
        this.mModifiedPhoto = tempphoto;
    }


    @Override
    protected void onDraw(Canvas canvas) {
      //  canvas = new Canvas(mResultBitmap);

        if(mCapturedBitmap!=null) {
            canvas.rotate(0);
            canvas.drawBitmap(mCapturedBitmap, 0, 0, null);
        }
        super.onDraw(canvas);



    }


    public void setPhotoRotation(int rotate){
        mModifiedPhoto.setRotation(rotate);
        setCanvasRotate(rotate);
        this.postInvalidate();
    }



}
