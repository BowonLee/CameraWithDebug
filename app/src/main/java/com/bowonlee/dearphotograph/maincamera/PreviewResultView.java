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

public class PreviewResultView extends View{

    private Bitmap mCapturedBitmap;

    public PreviewResultView(Context context,Bitmap capturedBitmap){
        super(context);
        this.mCapturedBitmap = capturedBitmap;
    }

    public PreviewResultView(Context context) {
        super(context);
    }



    public void setCapturedBitmap(Bitmap bitmap){
        mCapturedBitmap =bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
      //  canvas.rotate(90,260,540);
       if(mCapturedBitmap!=null) {
           canvas.drawBitmap(mCapturedBitmap, 0, 0, null);

           Log.e("BitmapDraw", String.format("draw %d,%d", mCapturedBitmap.getWidth(), mCapturedBitmap.getHeight()));
       }
    }
}
