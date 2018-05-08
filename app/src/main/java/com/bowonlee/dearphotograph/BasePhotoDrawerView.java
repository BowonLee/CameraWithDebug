package com.bowonlee.dearphotograph;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;

import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;

import java.io.IOException;

public class BasePhotoDrawerView extends View{

    protected ModifiedPhoto mModifiedPhoto;
    protected Bitmap mPhotoBitmap;
    private Rect photoRect;

    private int rotateDegree = 0;

    public BasePhotoDrawerView(Context context){
        super(context);
    }
    public BasePhotoDrawerView(Context context, ModifiedPhoto photo){
        super(context);
        mModifiedPhoto = photo;

    }

    public void setPhoto(ModifiedPhoto photo){
        mModifiedPhoto = photo;
    }

    public ModifiedPhoto getModifiedPhoto(){
        return mModifiedPhoto;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mModifiedPhoto != null){
            Paint paint = new Paint();
            setPhotoBitmap();

            canvas.rotate(rotateDegree,
                    mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()/2,mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()/2);
         //   canvas.rotate(90);
            canvas.drawBitmap(mPhotoBitmap,mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,paint);

        }else{
        }
    }


    protected void setCanvasRotate(int degree){
        this.rotateDegree = degree;

    }
    protected int getCanvasRotate(){return rotateDegree;}
    protected void setPhotoBitmap(){
        mPhotoBitmap = resizedBitmapFromUri(mModifiedPhoto.getImageUri(),mModifiedPhoto.getRatio());

    }

    protected Rect getPhotoRect(){
        int top,left,bottom,right;
         //사진의 원래 영역
            left = mModifiedPhoto.getStartXY().x;
            top  = mModifiedPhoto.getStartXY().y;
            right = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth();
            bottom = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight();
        Log.e("rect",String.format("(%d,%d)(%d,%d)",left,top,right,bottom));
        photoRect = new Rect(left,top,right,bottom);

        return photoRect;
    }

    protected Rect getRotateRectWidthPivot(Rect rect){
         int pivotX = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()/2;
         int pivotY = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()/2;
        int left,top,right,bottom;
        rotateDegree %=180;
        double rad = Math.toRadians(rotateDegree);

        left = (int)((rect.left-pivotX) * Math.cos(rad) - (rect.top-pivotY)*Math.sin(rad))+pivotX;
        top = (int)((rect.left-pivotX)*Math.sin(rad) + (rect.top-pivotY)*Math.cos(rad))+pivotY;
        right = (int)((rect.right-pivotX) * Math.cos(rad) - (rect.bottom-pivotY)*Math.sin(rad))+pivotX;
        bottom = (int)((rect.right-pivotX) *Math.sin(rad) + (rect.bottom-pivotY)*Math.cos(rad))+pivotY;


        if(rotateDegree == 0){
        }else{
            left = left - mPhotoBitmap.getHeight();
            right = right + mPhotoBitmap.getHeight();
        }
        Log.e("rect",String.format("degree : %d (%d,%d)(%d,%d)",rotateDegree,left,top,right,bottom));
        return new Rect(left,top,right,bottom);

    }

    private Bitmap resizedBitmapFromUri(Uri res, float ratio) {

        Bitmap result;
        int width ;
        int height ;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(res.getPath(),options);

        width = (int) (options.outWidth*ratio);
        height = (int) (options.outHeight*ratio);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);


        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        result = BitmapFactory.decodeFile(res.getPath(),options);

        return result;


    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.e("insampleSize"," is : "+inSampleSize);
        return inSampleSize;
    }




}
