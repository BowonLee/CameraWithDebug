package com.bowonlee.dearphotograph.gallary;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.models.Photo;

/**
 * Created by bowon on 2018-04-20.
 */

public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
// RecyclerView에서 재활용 될 아이템

    private ImageView photoView;
    private Photo photo;
    public PhotoHolder(View view) {
        super(view);

        photoView= (ImageView)view.findViewById(R.id.photoholder_image);
    }

    public void setPhoto(Photo photo){
        this.photo = photo;
        photoView.setImageURI(photo.getThumnailUri());

    }

    @Override
    public void onClick(View v) {
        /*
        * 사진 선택
        * */

        Log.i("PhotoHolder","Image : " + photo.getImageUri().toString());
    }
}
