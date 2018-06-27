package com.bowonlee.dearphotographdebug.gallary;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import com.bowonlee.dearphotographdebug.models.Photo;

/**
 * Created by bowon on 2018-04-19.
 */

/*
 * 쿼리를 요청하여 이미지 파일들을 가져오는 비동기 콜백 클레스이다.
 * leafpic에서는 반응형자바로 구성되어 있어 콜벡이 없었지만
 * 비동기식으로 구현할 것이 아니라면 필요하다.
 * */
public class RecentPhotoLoader extends android.support.v4.content.AsyncTaskLoader<Photo>{

    private ContentResolver contentResolver;

    private Uri tableUri;
    private String[] projection ;
    private String selection ;
    private String[] selectionArgs ;
    Photo photo;
    public RecentPhotoLoader(Context context) {
        super(context);

        contentResolver = context.getContentResolver();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Photo loadInBackground() {
        tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        selection = null ;// return all row
        selectionArgs = null;

        if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            return null;
        }

        Cursor imageCursor = contentResolver.query(tableUri,projection,selection,selectionArgs, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        int idColumIndex = imageCursor.getColumnIndex(projection[1]);

        if(imageCursor.moveToFirst()){


                String filePath = imageCursor.getString(dataColumnIndex);
                String imageId = imageCursor.getString(idColumIndex);

                Uri fullImageUri = Uri.parse(filePath);
                Uri thumnailUri = uriTothumnail(imageId);
                photo = new Photo(thumnailUri, fullImageUri);


        }
        imageCursor.close();

        return photo;
    }

    /*
     * ImageId를 통해 해당 Image의 썸네일을 받아오은 메소드이다.
     * 마지막 부분의 재귀 함수 부분은 썸네일이 아직 생성되지 않은 경우 요청을 다시 해주는 것이다.
     * 이미지가 생성된지 얼마 되지 않는경우 썸네일이 생성되어있지 않을 수 있다.
     *
     * */
    private  Uri uriTothumnail(String imageId){
        tableUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Images.Thumbnails.DATA};
        selection = MediaStore.Images.Thumbnails.IMAGE_ID + "=?";
        selectionArgs = new String[]{imageId};

        Cursor thumnailCursor = contentResolver.query(tableUri,projection,
                selection, selectionArgs,null);
        if(thumnailCursor.moveToFirst()){
            int thumnailColumnIndex = thumnailCursor.getColumnIndex(projection[0]);

            String thumnailPath = thumnailCursor.getString(thumnailColumnIndex);
            thumnailCursor.close();
            return Uri.parse(thumnailPath);
        }else{
            MediaStore.Images.Thumbnails.getThumbnail(contentResolver,Long.parseLong(imageId),MediaStore.Images.Thumbnails.MINI_KIND,null);
            thumnailCursor.close();
            return uriTothumnail(imageId);
        }

    }

    @Override
    public void deliverResult(Photo photo) {
        if(isReset()){
            if (photo!=null){
                // 로더가 종료된 후 퀴리가 들어온 경우 비정상적인 상황


            }
        }

        this.photo = photo;

        if (isStarted()){
            //로더가 작동 중 이라면 결과를 보내주도록 한다.
            super.deliverResult(photo);
        }

        super.deliverResult(photo);
    }

    @Override
    protected void onStartLoading() {
        if (photo != null){
            deliverResult(photo);
        }else{
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {cancelLoad();}

    @Override
    public void onCanceled(Photo photo) {
        super.onCanceled(photo);

    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (photo != null){

            photo = null;
        }
    }


}
