package com.bowonlee.dearphotograph.maincamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.bowonlee.dearphotograph.BasePhotoDrawerView;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;

public class MainPhotoDrawerView extends BasePhotoDrawerView implements View.OnTouchListener{



    public static final String TAG = "ModifyPhotoView";

    private final int EVENT_INSIDE = 401;
    private final int EVENT_EDGE = 405;
    private final int EVENT_OUTSIDE = 408;
    private final int EVENT_EDGE_TOP = 411;
    private final int EVENT_EDGE_BOTTOM = 412;
    private final int EVENT_EDGE_LEFT = 413;
    private final int EVENT_EDGE_RIGHT = 414;
    private final int EVENT_EDGE_CORNER_TOP_LEFT = 415;
    private final int EVENT_EDGE_CORNER_TOP_RIGHT = 416;
    private final int EVENT_EDGE_CORNER_BOTTOM_LEFT = 417;
    private final int EVENT_EDGE_CORNER_BOTTON_RIGHT = 418;



    private float mFrameZoomX = 0;
    private float mFrameZoomY = 0;

    private int currentEventState = EVENT_OUTSIDE;

    public MainPhotoDrawerView(Context context) { super(context); }

    //이동 직전좌표
    private float touchPastX = 0;
    private float touchPastY = 0;

    private float mTouchDistanceX=0;
    private float mTouchDistanceY=0;
    //외곽선 자체의 두께
    private float frameWidth = 5f;
    //바깥외곽선과 내부 외곽선 사이의 폭
    private int boarderWidth = 40;


    public void movePhotoXY(float x, float y){
        mModifiedPhoto.setStartXY(new PointF(x,y));

    }

    public void setPhotoRotation(int rotate){
        mModifiedPhoto.setRotation(rotate);
        setCanvasRotate(rotate);
        this.postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {



        super.onDraw(canvas);
        try {
            drawFrame(canvas);
        //    drawBorderRect(canvas);
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

    }

    private void drawBorderRect(Canvas canvas){

        /*디버그용 - 가장자리 영역과 내부영역 나누기*/
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(10);

        float left = mModifiedPhoto.getStartXY().x + boarderWidth;
        float top = mModifiedPhoto.getStartXY().y + boarderWidth;
        float right = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth() - boarderWidth - mFrameZoomX;
        float bottom = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight() - boarderWidth - mFrameZoomY;


        canvas.drawRect(new RectF(left,top,right,bottom),paint);
    }



    private int distinguishEvent(float touchX,float touchY){
        float startX = getRotateRectWidthPivot(getPhotoRect()).left;
        float startY = getRotateRectWidthPivot(getPhotoRect()).top;
        float endX = getRotateRectWidthPivot(getPhotoRect()).right;
        float endY =getRotateRectWidthPivot(getPhotoRect()).bottom;

        if(     touchX>=startX&&
                touchX<endX&&
                touchY>=startY&&
                touchY<endY){

            if(     touchX>=startX+boarderWidth&&
                    touchX<endX-boarderWidth&&
                    touchY>=startY+boarderWidth&&
                    touchY<endY-boarderWidth) {
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

//                Log.e("touchEvent",String.format("lastXY(%d,%d)",mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y));

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

        movePhotoXY((int)( mModifiedPhoto.getStartXY().x - mTouchDistanceX),(int)( mModifiedPhoto.getStartXY().y - mTouchDistanceY));

        this.postInvalidate();


    }
    private void zoomInOutEvent(MotionEvent event){

        int state = 0;

        if(isEdgeTop(event)&&isEdgeLeft(event)){
            state = EVENT_EDGE_CORNER_TOP_LEFT;
        }else if(isEdgeTop(event)&&isEdgeRight(event)){
            state = EVENT_EDGE_CORNER_TOP_RIGHT;
        }else if(isEdgeBottom(event)&&isEdgeLeft(event)){
            state = EVENT_EDGE_CORNER_BOTTOM_LEFT;
        }else if(isEdgeBottom(event)&&isEdgeRight(event)){
            state = EVENT_EDGE_CORNER_BOTTON_RIGHT;
        }else if(isEdgeTop(event)){
            state = EVENT_EDGE_TOP;
        }else if(isEdgeBottom(event)){
            state = EVENT_EDGE_BOTTOM;
        }else if(isEdgeLeft(event)){
            state = EVENT_EDGE_LEFT;
        }else if(isEdgeRight(event)){
            state = EVENT_EDGE_RIGHT;
        }


        switch (state){

            case EVENT_EDGE_CORNER_TOP_RIGHT : {zoomEventTopRight(event);}break;
            case EVENT_EDGE_CORNER_BOTTOM_LEFT : {zoomEventBottonLeft(event);}break;
            case EVENT_EDGE_LEFT :break;
            case EVENT_EDGE_TOP :break;
            case EVENT_EDGE_CORNER_TOP_LEFT : {zoomEventTopLeft(event);}break;

            case EVENT_EDGE_BOTTOM :break;
            case EVENT_EDGE_RIGHT :break;
            case EVENT_EDGE_CORNER_BOTTON_RIGHT : {zoomEventBottomRight(event);}break;

        }



    }

    private void zoomEventTopLeft(MotionEvent event){
        float ratio;

        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

        if(mTouchDistanceX>=mTouchDistanceY){
            mFrameZoomX += mTouchDistanceX;
            mFrameZoomY +=  mTouchDistanceY;

            ratio =  ((float)mPhotoBitmap.getWidth()+mFrameZoomX)/mModifiedPhoto.getOutSize().getWidth();

            movePhotoXY(mModifiedPhoto.getStartXY().x-mFrameZoomX , mModifiedPhoto.getStartXY().y-mFrameZoomY);

            mModifiedPhoto.setRatio(ratio);


        }else{
            mFrameZoomX += (int)mTouchDistanceX;
            mFrameZoomY += (int) mTouchDistanceY;
           ratio =  ((float)mPhotoBitmap.getHeight()+mFrameZoomY)/mModifiedPhoto.getOutSize().getHeight();

            movePhotoXY(mModifiedPhoto.getStartXY().x-mFrameZoomX ,mModifiedPhoto.getStartXY().y-mFrameZoomY);

            mModifiedPhoto.setRatio(ratio);}

        this.postInvalidate();

        mFrameZoomX = 0;
        mFrameZoomY = 0;

    }

    private void zoomEventTopRight(MotionEvent event){
        float ratio;

        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

        if(mTouchDistanceX>=mTouchDistanceY){
            mFrameZoomX += mTouchDistanceX;
            mFrameZoomY +=  mTouchDistanceY;
       ratio =  ((float)mPhotoBitmap.getWidth()-mFrameZoomX)/mModifiedPhoto.getOutSize().getWidth();

            movePhotoXY(mModifiedPhoto.getStartXY().x, mModifiedPhoto.getStartXY().y-mFrameZoomY);
            mModifiedPhoto.setRatio(ratio);


        }else{
            mFrameZoomX += (int)mTouchDistanceX;
            mFrameZoomY += (int) mTouchDistanceY;
            ratio =  ((float)mPhotoBitmap.getHeight()+mFrameZoomY)/mModifiedPhoto.getOutSize().getHeight();

            movePhotoXY(mModifiedPhoto.getStartXY().x ,mModifiedPhoto.getStartXY().y-mFrameZoomY);
            mModifiedPhoto.setRatio(ratio);}

        this.postInvalidate();

        mFrameZoomX = 0;
        mFrameZoomY = 0;
    }
    private void zoomEventBottonLeft(MotionEvent event){
        float ratio;

        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

        if(mTouchDistanceX>=mTouchDistanceY){
            mFrameZoomX += mTouchDistanceX;
            mFrameZoomY +=  mTouchDistanceY;

            ratio =  ((float)mPhotoBitmap.getWidth()+mFrameZoomX)/mModifiedPhoto.getOutSize().getWidth();
            movePhotoXY(mModifiedPhoto.getStartXY().x-mFrameZoomX , mModifiedPhoto.getStartXY().y);

            mModifiedPhoto.setRatio(ratio);


        }else{
            mFrameZoomX += (int)mTouchDistanceX;
            mFrameZoomY += (int) mTouchDistanceY;

            ratio =  ((float)mPhotoBitmap.getHeight()-mFrameZoomY)/mModifiedPhoto.getOutSize().getHeight();
            movePhotoXY(mModifiedPhoto.getStartXY().x-mFrameZoomX ,mModifiedPhoto.getStartXY().y);

            mModifiedPhoto.setRatio(ratio);}

        this.postInvalidate();

        mFrameZoomX = 0;
        mFrameZoomY = 0;
    }
    private void zoomEventBottomRight(MotionEvent event){
        float ratio;
        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

        if(mTouchDistanceX>=mTouchDistanceY){
            mFrameZoomX += mTouchDistanceX;
            mFrameZoomY +=  mTouchDistanceY;
            ratio =  ((float)mPhotoBitmap.getWidth()-mFrameZoomX)/(float)mModifiedPhoto.getOutSize().getWidth();


        }else{
            mFrameZoomX += mTouchDistanceX;
            mFrameZoomY +=  mTouchDistanceY;

            ratio =  ((float)mPhotoBitmap.getHeight()-mFrameZoomY)/(float)mModifiedPhoto.getOutSize().getHeight();

        }

        mModifiedPhoto.setRatio(ratio);
        mFrameZoomX = 0;
        mFrameZoomY = 0;

        this.postInvalidate();
    }


    private boolean isEdgeTop(MotionEvent event){

        if(event.getY()<mModifiedPhoto.getStartXY().y+boarderWidth){
            return true;

        }
        return false;
    }
    private boolean isEdgeBottom(MotionEvent event){

        if(event.getY()>mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()-boarderWidth){
            return true;
        }
        return false;
    }
    private boolean isEdgeLeft(MotionEvent event){

        if(event.getX()<mModifiedPhoto.getStartXY().x+boarderWidth){
            return true;
        }
        return false;
    }
    private boolean isEdgeRight(MotionEvent event){

        if(event.getX()>mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()-boarderWidth){

            return true;
        }
        return false;
    }

    @Override
    public ModifiedPhoto getModifiedPhoto() {
       return super.getModifiedPhoto();
    }
}
