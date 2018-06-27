package com.bowonlee.dearphotographdebug.gallary;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.util.List;

public class AlbumLoader extends AsyncTaskLoader<List<String>> {

    private ContentResolver mContentResolver;
    private Uri tableUri;
    private String[] projection ;
    private String selection ;
    private String[] selectionArgs ;

    public AlbumLoader(Context context) {
        super(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public List<String> loadInBackground() {






        return null;
    }
}
