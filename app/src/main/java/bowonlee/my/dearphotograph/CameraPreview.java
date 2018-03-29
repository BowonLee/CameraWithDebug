package bowonlee.my.dearphotograph;

import android.content.Context;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import java.io.File;
import java.util.concurrent.Semaphore;

/**
 * Created by bowon on 2018-03-28.
 */

public class CameraPreview {

    private static final String TAG = "Camera2Preview";

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /*Camrara Status*/
    private static final int STATE_PREVIEW = 0 ;
    private static final int STATE_WAITING_LOCK =1;
    private static final int STATE_WATING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;

    /*Device Size*/
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /*CameraComponent*/
    private String mCameraId;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;


    /*PreviewComponent*/
    private Size mPreviewSize;


    /*For BackgroundThread*/
    private HandlerThread mBacHandlerThread;
    private Handler mBackroundHandler;

    /*Syncronizing Semapore*/
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private boolean mFlashSupported;

    /*For StillImageCapture */
    private ImageReader mImageReader;
    private File mFile;

    /*Use Activity */
    private Context mContext;

    /*Callback Method*/




    public CameraPreview(Context context){
        mContext = context;
    }


}
