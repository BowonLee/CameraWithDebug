package com.bowonlee.dearphotograph.gallary;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by bowon on 2018-04-11.
 */

public class ImageAdapter extends BaseAdapter{
    private Context mContext;


    private Bitmap getThumbnailBitmap(){


        return null;
    }

    ImageAdapter(Context context){
        mContext = context;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null) {
            imageView = new ImageView(mContext);

        }else{
            imageView = (ImageView)convertView;
        }
        imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(8, 8, 8, 8);
        int arr[] = {3,1,3};
        int arr2;
        int j = arr.length;


        //imageView.setImageResource(image from gallary);
        return imageView;
    }
}
