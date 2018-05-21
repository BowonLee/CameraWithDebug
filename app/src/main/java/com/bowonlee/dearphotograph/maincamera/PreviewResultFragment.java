package com.bowonlee.dearphotograph.maincamera;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bowonlee.dearphotograph.R;

import java.util.ArrayList;

public class PreviewResultFragment extends Fragment {

    interface PreviewResultInterface{

        public void onCancelPreviewResult();
    }

    private PreviewResultInterface mPreviewResultInterface;

    private final String TAG = "PreviewResultFragment";

    private ArrayList<Button> mButtonGruop;
    private Button mButtonSaveImage;
    private Button mButtonCancel;
    private PreviewResultView mPreviewResultView;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreviewResultView = new PreviewResultView(getContext());
        getActivity().addContentView(mPreviewResultView, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT));
        mButtonSaveImage = (Button)view.findViewById(R.id.btn_fragment_preview_result_save);
        mButtonCancel = (Button)view.findViewById(R.id.btn_fragment_preview_result_cancel);


        settingButtons();
    }
    private void settingButtons(){

        mButtonGruop = new ArrayList<>();
        mButtonGruop.add(mButtonSaveImage);
        mButtonGruop.add(mButtonCancel);

        for(Button btn : mButtonGruop){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_preview_result_cancel :{onDestroy();}break;
                        case R.id.btn_fragment_preview_result_save : {}break;
                    }
                }
            });
        }

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
        mPreviewResultInterface.onCancelPreviewResult();
        super.onDestroy();

    }
}
