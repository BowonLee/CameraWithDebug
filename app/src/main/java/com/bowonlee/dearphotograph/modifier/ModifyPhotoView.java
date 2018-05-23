package com.bowonlee.dearphotograph.modifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;

public class ModifyPhotoView extends BasePhotoDrawerView implements View.OnTouchListener{


    public static final String TAG = "ModifyPhotoView";

    private final int EVENT_INSIDE = 401;
    private final int EVENT_EDGE = 405;
    private final int EVENT_OUTSIDE = 408;


    private int mFrameZoomX = 0;
    private int mFrameZoomY = 0;

    private int currentEventState = EVENT_OUTSIDE;

    public ModifyPhotoView(Context context) { super(context); }

    //이동 직전좌표
    private float touchPastX = 0;
    private float touchPastY = 0;

    private float mTouchDistanceX=0;
    private float mTouchDistanceY=0;
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

    private Bitmap tempBit;
    public void setTemp(Bitmap temp){
        this.tempBit = temp;

        //mPhotoBitmap = temp;
    }

    @Override
    protected void onDraw(Canvas canvas) {


        super.onDraw(canvas);
        try {
            drawFrame(canvas);
            drawBorderRect(canvas);


        }catch (NullPointerException e){
            e.printStackTrace();
        }

        }

    private void drawFrame(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(frameWidth);


        canvas.drawRect(mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,
                mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()-mFrameZoomX,mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()-mFrameZoomY,paint);
        Log.e(TAG,String.format("%d,%d,%d,%d",mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,
                mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()-mFrameZoomX,mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()-mFrameZoomY));

    }

    private void drawBorderRect(Canvas canvas){

        Paint paint = new Paint();


        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(frameWidth);

        int left = mModifiedPhoto.getStartXY().x + boarderWidth;
        int top = mModifiedPhoto.getStartXY().y + boarderWidth;
        int right = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth() - boarderWidth - mFrameZoomX;
        int bottom = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight() - boarderWidth - mFrameZoomY;


        canvas.drawRect(new Rect(left,top,right,bottom),paint);
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
        if(mModifiedPhoto==null){
            return false;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN : {
                Log.e("touchEvent","action down");
                currentEventState = distinguishEvent(event.getX(),event.getY());
                touchPastX = event.getX();
                touchPastY = event.getY();
                if(currentEventState == EVENT_OUTSIDE)return false;


            }return true;
            case MotionEvent.ACTION_MOVE : {
                if(currentEventState == EVENT_INSIDE){movePhotoEvent(event);}
               if(currentEventState == EVENT_EDGE){zoomInOutEvent(event);}


            }return true;
            case MotionEvent.ACTION_UP  : {
                currentEventState = EVENT_OUTSIDE;

                Log.e("touchEvent",String.format("lastXY(%d,%d)",mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y));

                return false;
            }
        }
        return false;

    }

    private void movePhotoEvent(MotionEvent event){

        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

     //   Log.e("eventOccur",String.format("move (%f,%f) Distance(%f,%f)",event.getX(),event.getY(),mTouchDistanceX,mTouchDistanceY));
        movePhotoXY((int)( mModifiedPhoto.getStartXY().x - mTouchDistanceX),(int)( mModifiedPhoto.getStartXY().y - mTouchDistanceY));
        this.postInvalidate();


    }
    private void zoomInOutEvent(MotionEvent event){
        float ratio;
        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

        if(mTouchDistanceX>=mFrameZoomY){
            mFrameZoomX += (int)mTouchDistanceX;
            mFrameZoomY += (int) mTouchDistanceX;
        }else{
            mFrameZoomX += (int)mTouchDistanceY;
            mFrameZoomY += (int) mTouchDistanceY;
        }

        ratio =  ((float)mPhotoBitmap.getWidth()-mFrameZoomX)/(float)mModifiedPhoto.getOutSize().getWidth();


        Log.e("eventOccur",String.format("Ratio(%f,%f) bitSize outSize(%d,%d), ",mModifiedPhoto.getRatio(),ratio,mPhotoBitmap.getWidth(),mModifiedPhoto.getOutSize().getWidth()));

        mModifiedPhoto.setRatio(ratio);
        Log.e("eventOccur",String.format("edge(%f,%f) %f, ",event.getX(),event.getY(),ratio));
       mFrameZoomX = 0;
       mFrameZoomY = 0;

        this.postInvalidate();

    }



}
