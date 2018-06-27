package com.bowonlee.dearphotographdebug.gallary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bowonlee.dearphotographdebug.R;

/**
 * Created by bowon on 2018-04-11.
 */

public class PhotoGallaryActivity extends AppCompatActivity{


    private static final String TAG = "PhotoGallary";
    public static final int REQUEST_CODE = 4001;

    private RecyclerView mGridPhotoGallary;
    private PhotoAdapter mPhotoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);


        mGridPhotoGallary = (RecyclerView) findViewById(R.id.gallary_recyclerview);
        mPhotoAdapter = new PhotoAdapter(this);

        getLoaderManager().initLoader(0,null,mPhotoAdapter);
        mGridPhotoGallary.setLayoutManager(new GridLayoutManager(null,3, LinearLayoutManager.VERTICAL,false));
        mGridPhotoGallary.setAdapter(mPhotoAdapter);


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
