package com.bowonlee.dearphotograph.gallary;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.GridView;

import com.bowonlee.dearphotograph.R;

/**
 * Created by bowon on 2018-04-11.
 */

public class PhotoGallaryActivity extends Activity{

    private GridView mGridView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_gally);
        mGridView = (GridView)findViewById(R.id.gridview_gallary);

    }
}
