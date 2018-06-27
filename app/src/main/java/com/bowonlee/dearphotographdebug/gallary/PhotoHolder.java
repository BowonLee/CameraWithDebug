package com.bowonlee.dearphotographdebug.gallary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bowonlee.dearphotographdebug.R;
import com.bowonlee.dearphotographdebug.models.Photo;

/**
 * Created by bowon on 2018-04-20.
 */

public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
// RecyclerView에서 재활용 될 아이템

    interface OnItemClickListener{
        void onItemClick(Photo photo);
    }



    private ImageView photoView;
    private Photo photo;
    private OnItemClickListener listener;

    public PhotoHolder(View view) {
        super(view);

        photoView= (ImageView)view.findViewById(R.id.photoholder_image);

        view.setOnClickListener(this);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
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
        listener.onItemClick(photo);


    }
}
