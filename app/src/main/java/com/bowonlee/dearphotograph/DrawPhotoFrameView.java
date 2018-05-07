package com.bowonlee.dearphotograph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.bowonlee.dearphotograph.models.Photo;

/*
* 사용자가 가져온 사진을 Draw 객체를 이용하여 그려준다.
*
* */
public class DrawPhotoFrameView extends View{
    Context parentContext;


    private Bitmap photo;
    private int parentTextureWidth;
    private int parentTextureHeight;

    public DrawPhotoFrameView(Context context) {
        super(context);
        this.parentContext = context;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(parentContext.getResources(),R.drawable.cafe_demoimage,op);

       // Log.e("bitmap",String.format("(%d,%d)",bitmap.getWidth(),bitmap.getHeight()));

        //canvas.drawBitmap(bitmap,0,0,null);

        Paint paint = new Paint();
        if(photo == null ){

        }else{

        }


    }


}
