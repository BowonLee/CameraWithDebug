package com.bowonlee.dearphotograph.resultpreview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bowonlee.dearphotograph.BitmapSaver;
import com.bowonlee.dearphotograph.R;
import com.bowonlee.dearphotograph.models.ModifiedPhoto;

import java.util.ArrayList;

public class PreviewResultFragment extends Fragment {

    public interface PreviewResultInterface{

        public void onCancelPreviewResult();

    }



    private PreviewResultInterface mPreviewResultInterface;

    private final String TAG = "PreviewResultFragment";

    private ArrayList<Button> mButtonGruop;
    private Button mButtonSaveImage;
    private Button mButtonCancel;
    private PreviewResultView mPreviewResultView;
    private Bitmap mCapturedBitmap;
    private RelativeLayout mParentLayout;
    private ModifiedPhoto mModifiedPhoto;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreviewResultView = new PreviewResultView(getContext(),mCapturedBitmap,mModifiedPhoto);

        mParentLayout = (RelativeLayout)view.findViewById(R.id.layout_preview_result);



        mPreviewResultView.setPhoto(mModifiedPhoto);

        mParentLayout.addView(mPreviewResultView);

        mButtonSaveImage = (Button)view.findViewById(R.id.btn_fragment_preview_result_save);
        mButtonCancel = (Button)view.findViewById(R.id.btn_fragment_preview_result_cancel);

        settingButtons();


        mPreviewResultView.postInvalidate();


    }

    @Override
    public void onResume() {
        super.onResume();
        mButtonSaveImage.bringToFront();
        mButtonCancel.bringToFront();

    }
    public void setModifiedPhoto(ModifiedPhoto modifiedPhoto){
        //this.mModifiedPhoto = modifiedPhoto;
        this.mModifiedPhoto = new ModifiedPhoto(modifiedPhoto);

    }

    public void setCapturedBitmap(Bitmap capturedBitmap){
        this.mCapturedBitmap = capturedBitmap;
    }
    private void settingButtons(){

        mButtonGruop = new ArrayList<>();
        mButtonGruop.add(mButtonSaveImage);
        mButtonGruop.add(mButtonCancel);

        for(Button btn : mButtonGruop){
            mParentLayout.bringChildToFront(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_preview_result_cancel :{
                            mPreviewResultInterface.onCancelPreviewResult();
                            onDestroy();}break;
                        case R.id.btn_fragment_preview_result_save : {
                            saveView(mPreviewResultView);
                        }break;
                    }
                }
            });
        }

    }

    private void saveView(View view){
        Bitmap b = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);

        new BitmapSaver(b,getContext()).run();
        mPreviewResultView = null;
        mPreviewResultInterface.onCancelPreviewResult();



    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_preview_result,container,false);
    }
    public void setPreviewResultInterface(PreviewResultInterface previewResultInterface){
        this.mPreviewResultInterface = previewResultInterface;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }
}
