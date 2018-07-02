package com.bowonlee.dearphotographdebug.gallary;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.bowonlee.dearphotographdebug.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class AlbumLoader extends AsyncTaskLoader<List<String>> {

    private ContentResolver mContentResolver;
    private Uri tableUri;
    private String[] projection ;
    private String selection ;
    private String[] selectionArgs ;
    private ArrayList<String> mAlbums;


    public AlbumLoader(Context context) {
        super(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public List<String> loadInBackground() {

        mAlbums = new ArrayList<>();
        tableUri = MediaStore.Files.getContentUri("external");

        projection = new String[]{
                MediaStore.Files.FileColumns.PARENT,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                "count(*)",
                MediaStore.Images.Media.DATA,
                "max(" + MediaStore.Images.Media.DATE_MODIFIED + ")"
        };

        selection = String.format("%s=?) group by (%s) ",
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.PARENT);
        selectionArgs = null;

        Cursor imageCursor = mContentResolver.query(
                tableUri,
                projection,
                selection,
                selectionArgs,
                MediaStore.MediaColumns.DATE_ADDED + " desc");

        ArrayList<Photo> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        int idColumIndex = imageCursor.getColumnIndex(projection[1]);

        if(imageCursor.moveToFirst()){
            do {

                String filePath = imageCursor.getString(dataColumnIndex);
                String imageId = imageCursor.getString(idColumIndex);
                Log.e("Albums",filePath + " 12" + imageId);

            }while (imageCursor.moveToNext());
        }
        imageCursor.close();

        Log.e("album","on loader");
        return mAlbums;
    }
}
