package com.bowonlee.dearphotograph.modify;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;

import com.bowonlee.dearphotograph.models.ModifiedPhoto;
import com.bowonlee.dearphotograph.models.Photo;

import java.io.IOException;

public class ModifyPhotoView extends View{

    private ModifiedPhoto photo;
    private Bitmap bitmap;
    private Context context;

    public ModifyPhotoView(Context context){
        super(context);
        this.context = context;
    }
    public ModifyPhotoView(Context context,ModifiedPhoto photo){
        super(context);
        this.photo = photo;
        this.context = context;

    }
    public void setPhoto(ModifiedPhoto photo){
        this.photo = photo;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(photo != null){
            Paint paint = new Paint();
            canvas.drawBitmap(decodeSampledBitmapFromUri(photo.getImageUri(),photo.getRatio()),photo.getStartXY().x,photo.getStartXY().y,paint);
        }else{

        }
    }


    private Bitmap decodeSampledBitmapFromUri(Uri res, float ratio) {

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
        Log.e("size before out",String.format("(%d,%d)",options.outWidth,options.outHeight));

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        result = BitmapFactory.decodeFile(res.getPath(),options);
        Log.e("size after out",String.format("(%d,%d)",options.outWidth,options.outHeight));

        Log.e("size after",String.format("(%d,%d)",width,height));
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




    public ModifiedPhoto getModifiedPhoto(){
        return photo;
    }



}
