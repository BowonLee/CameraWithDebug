package com.bowonlee.dearphotograph.maincamera;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;

public class PreviewResultView extends BasePhotoDrawerView{

    private Bitmap mCapturedBitmap;

    private Bitmap mResultBitmap;
    public PreviewResultView(Context context, Bitmap capturedBitmap, ModifiedPhoto mModifiedPhoto){

        super(context);
        this.mCapturedBitmap = capturedBitmap;


        mResultBitmap = mCapturedBitmap;
    }

    public PreviewResultView(Context context) {
        super(context);
    }



    public void setCapturedBitmap(Bitmap bitmap){
        mCapturedBitmap =bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
      //  canvas = new Canvas(mResultBitmap);

        if(mCapturedBitmap!=null) {
            canvas.drawBitmap(mCapturedBitmap, 0, 0, null);
        }
        super.onDraw(canvas);



    }



}
