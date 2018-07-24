package com.bowonlee.dearphotographdebug.gallary;

import android.Manifest;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bowonlee.dearphotographdebug.R;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowon on 2018-04-11.
 */

public class PhotoGallaryActivity extends AppCompatActivity {


    private static final String TAG = "PhotoGallary";
    public static final int REQUEST_CODE = 4001;

    private RecyclerView mGridPhotoGallary;
    private PhotoAdapter mPhotoAdapter;
    private ArrayList<String> albumList;
    private Spinner mSpinnerAlbumList;
    private TextView mTextviewCurrentAlbum;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);


        mGridPhotoGallary = (RecyclerView) findViewById(R.id.gallary_recyclerview);
        mSpinnerAlbumList = (Spinner)findViewById(R.id.spinner_gallary_albumlist);
     //   mTextviewCurrentAlbum = (TextView)findViewById(R.id.textview_gallary_currentalbum);

        setPhotoAdapter();

        mGridPhotoGallary.setLayoutManager(new GridLayoutManager(null,3, LinearLayoutManager.VERTICAL,false));
        mGridPhotoGallary.setAdapter(mPhotoAdapter);

        getBucketDisplayList();
        setSpinnerAlbumList();


    }

    private void checkReadPermission(){
        //if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
    }


    private void setPhotoAdapter(){
        mPhotoAdapter = new PhotoAdapter(this,getString(R.string.gallary_all_albums));
        getLoaderManager().initLoader(0,null,mPhotoAdapter);

    }


    private void setSpinnerAlbumList(){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,albumList);
        mSpinnerAlbumList.setAdapter(adapter);
        mSpinnerAlbumList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPhotoAdapter.setAlbumName(parent.getItemAtPosition(position).toString());
                getLoaderManager().restartLoader(0,null,mPhotoAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void getBucketDisplayList() {
        String bucket;
        albumList = new ArrayList<>();

        if(!albumList.contains(getString(R.string.gallary_all_albums)))
            albumList.add(getString(R.string.gallary_all_albums));

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE},null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            int bucketColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            do {
                bucket = cursor.getString(bucketColumn);
                if(!albumList.contains(bucket))
                albumList.add(bucket);
            } while (cursor.moveToNext());

            cursor.close();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();

    }



}
