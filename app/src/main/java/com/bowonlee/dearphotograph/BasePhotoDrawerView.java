package com.bowonlee.dearphotograph;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
            setPhotoBitmap();
            Paint paint = new Paint();
            canvas.drawBitmap(mPhotoBitmap,mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,paint);

        }else{
        }
    }

    protected void rotateCW(Canvas canvas){
        canvas.save();
        canvas.rotate(90);
        canvas.restore();

    }

    protected void setPhotoBitmap(){
        mPhotoBitmap = resizedBitmapFromUri(mModifiedPhoto.getImageUri(),mModifiedPhoto.getRatio());

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
