package com.bowonlee.dearphotograph.modifier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;

public class ModifyPhotoView extends BasePhotoDrawerView implements View.OnDragListener,View.OnTouchListener{

    private final int EVENT_INSIDE = 401;
    private final int EVENT_EDGE = 405;
    private final int EVENT_OUTSIDE = 408;


    //외곽선 자체의 두께
    private float frameWidth = 5f;
    //바깥외곽선과 내부 외곽선 사이의 폭
    private int boarderWidth = 30;

    public void movePhotoXY(int x, int y){
        mModifiedPhoto.setStartXY(new Point(x,y));

    }

    public void setPhotoRotation(int rotate){
        setCanvasRotate(rotate);

    }

    public ModifyPhotoView(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFrame(canvas);
        drawBorderRect(canvas);


    }

    private void drawFrame(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(frameWidth);
        canvas.drawRect(mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,
                mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth(),mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight(),paint);


    }

    private void drawBorderRect(Canvas canvas){

        Paint paint = new Paint();


        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(frameWidth);

        int left = mModifiedPhoto.getStartXY().x + boarderWidth;
        int top = mModifiedPhoto.getStartXY().y + boarderWidth;
        int right = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth() - boarderWidth;
        int bottom = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight() - boarderWidth;


        canvas.drawRect(new Rect(left,top,right,bottom),paint);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.e("InViewdragXY",String.format("(%f,%f)",event.getX(),event.getY()));

        return false;
    }

    private int distinguishEvent(float touchX,float touchY){
        int startX = getRotateRectWidthPivot(getPhotoRect()).left;
        int startY = getRotateRectWidthPivot(getPhotoRect()).top;
        int endX = getRotateRectWidthPivot(getPhotoRect()).right;
        int endY =getRotateRectWidthPivot(getPhotoRect()).bottom;
        if(touchX>=startX&&touchX<endX&&touchY>=startY&&touchY<endY){
            if(touchX>=startX+boarderWidth&&touchX<endX-boarderWidth&&touchY>=startX-boarderWidth&&touchY<endY-boarderWidth) {
                return EVENT_INSIDE;
            }

            return EVENT_EDGE;

        }else{
            return EVENT_OUTSIDE;
        }




    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (distinguishEvent(event.getX(),event.getY())){
            case EVENT_INSIDE : {
                Log.e("touchEvent","inside");
            }break;
            case EVENT_EDGE : {
                Log.e("touchEvent","edge");
            }break;
            case EVENT_OUTSIDE : {
                Log.e("touchEvent","outside");
            }break;
        }
        return false;
    }
}
