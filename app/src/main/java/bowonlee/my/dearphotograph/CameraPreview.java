package bowonlee.my.dearphotograph;

import android.util.SparseIntArray;

/**
 * Created by bowon on 2018-03-28.
 */

public class CameraPreview {

    private static final String TAG = "Camera2Preview";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    /*Camrara Status*/
    private static final int STATE_PREVIEW = 0 ;
    private static final int STATE_WAITING_LOCK =1;
    private static final int STATE_WATING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    /*Device Size*/
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    public CameraPreview(){


    }


}
