package com.bowonlee.dearphotograph.gallary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.Toast;

import com.bowonlee.dearphotograph.FileIOHelper;
import com.bowonlee.dearphotograph.R;

/**
 * Created by bowon on 2018-04-11.
 */

public class PhotoGallaryActivity extends AppCompatActivity{

    private static String TAG = "PhotoGallary";
    private GridView mGridView;
    private String mFilePath;
    private ImageAdapter mImageAdapter;
    private FileIOHelper mFileIOHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gally);
        //  mImageAdapter = new ImageAdapter(this);

        Toast.makeText(this,"open",Toast.LENGTH_SHORT);
         mGridView = (GridView)findViewById(R.id.gridview_gallary);
         //mGridView.setAdapter(mImageAdapter);
      //   mGridView.setAdapter(new );
        mFileIOHelper = new FileIOHelper();


       // MediaStore.Images.Thumbnails.getThumbnail()
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    /*
    * 갤러리로부터 이미지 가져오기 + gridview로 가져온 이미지 리스트 보내주기
    *
    * */

}
