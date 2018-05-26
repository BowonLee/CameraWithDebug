package com.bowonlee.dearphotograph.gallary;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.bowonlee.dearphotograph.models.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowon on 2018-04-19.
 */

/*
* 쿼리를 요청하여 이미지 파일들을 가져오는 비동기 콜백 클레스이다.
* leafpic에서는 반응형자바로 구성되어 있어 콜벡이 없었지만
* 비동기식으로 구현할 것이 아니라면 필요하다.
* */
public class PhotoLoader extends AsyncTaskLoader<List<Photo>>{
    private List<Photo> photos;
    private ContentResolver contentResolver;

    private Uri tableUri;
    private String[] projection ;
    private String selection ;
    private String[] selectionArgs ;

    public PhotoLoader(Context context) {
        super(context);
        contentResolver = context.getContentResolver();
    }

    @Override
    public List<Photo> loadInBackground() {
        tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        selection = null ;// return all row
        selectionArgs = null;

        Cursor imageCursor = contentResolver.query(tableUri,projection,selection,selectionArgs, MediaStore.MediaColumns.DATE_ADDED + " desc");
        ArrayList<Photo> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        int idColumIndex = imageCursor.getColumnIndex(projection[1]);

        if(imageCursor.moveToFirst()){
            do {

                String filePath = imageCursor.getString(dataColumnIndex);
                String imageId = imageCursor.getString(idColumIndex);

                Uri fullImageUri = Uri.parse(filePath);
                Uri thumnailUri = uriTothumnail(imageId);
                Photo photo = new Photo(thumnailUri, fullImageUri);
                result.add(photo);
            }while (imageCursor.moveToNext());
        }
        imageCursor.close();

        return result;
    }

    /*
    * ImageId를 통해 해당 Image의 썸네일을 받아오은 메소드이다.
    * 마지막 부분의 재귀 함수 부분은 썸네일이 아직 생성되지 않은 경우 요청을 다시 해주는 것이다.
    * 이미지가 생성된지 얼마 되지 않는경우 썸네일이 생성되어있지 않을 수 있다.
    *
    * */
    private Uri uriTothumnail(String imageId){
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
    public void deliverResult(List<Photo> photos) {
        if(isReset()){
            if (photos!=null){
                // 로더가 종료된 후 퀴리가 들어온 경우 비정상적인 상황

                onReleaseResources(photos);
            }
        }
        List<Photo> oldPhotos = this.photos;
        this.photos = photos;

        if (isStarted()){
            //로더가 작동 중 이라면 결과를 보내주도록 한다.
            super.deliverResult(photos);
        }
        if (oldPhotos!=null){
            // 이전의 사진 파일정보들은 모두 릴리즈시키도록 한다.
            onReleaseResources(oldPhotos);
        }
        super.deliverResult(photos);
    }

    @Override
    protected void onStartLoading() {
        if (photos != null){
            deliverResult(photos);
        }else{
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {cancelLoad();}

    @Override
    public void onCanceled(List<Photo> photos) {
        super.onCanceled(photos);
        onReleaseResources(photos);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (photos != null){
            onReleaseResources(photos);
            photos = null;
        }
    }

    protected void onReleaseResources(List<Photo> photos){
        //더이상 사용하지 않는 리소스들을 해제시켜준다. 현제 구현되어있지 않다.
        //List<Photo>자료형 만으로 구성된 자료는 따로 해제시킬 필요는 없다.
        //추후 직접 릴리즈해야 하는 경우가 생긴다면 이곳에서 처리하도록 한다.
    }
}
