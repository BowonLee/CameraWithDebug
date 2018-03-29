package bowonlee.my.dearphotograph;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
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

    /*Orientation of Camera*/
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private int mSensorOrientation;

    /*Camrara Status*/
    private static final int STATE_PREVIEW = 0 ;
    private static final int STATE_WAITING_LOCK =1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
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

    /*Camera Device Option*/
    private boolean mFlashSupported;

    /*For StillImageCapture */
    private ImageReader mImageReader;
    private File mFile;

    /*Use Activity */
    private Context mContext;

    /*Callback Method*/

    /*
    * Device상태에 따른 카메라의 동작을 정의한 Callback Method이다.
    */

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            //createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;

        }
    };

    /*
    * imageCapture를 지원하는 callback method 이다.
    * 사진을 찍는 요청이 들어올 경우 작동한다.
    * */

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        /**
         * AF : auto focus      : 자동 초점 조정
         * AE : auto exposure   : 자동 노출 - 빛 처리
         * */
        private void progress(CaptureResult result){
            switch (mState){
                case STATE_PREVIEW:{
                    //사진 촬영을 위한 요청이 없는 경우
                    break;
                }
                case STATE_WAITING_LOCK : {
                    Integer afState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if(afState == null){
                        //captureStillPicture();
                    }else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED==afState ||
                    CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState){
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if(aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED){
                            mState = STATE_PICTURE_TAKEN;
                            //captureStillpicture
                        }else{
                            //runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE:{
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if(aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE||
                            aeState==CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED){
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE : {
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if(aeState == null||aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE){
                        mState = STATE_PICTURE_TAKEN;
                        //captureStillPicture();
                    }
                }
            }

        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            progress(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            progress(result);
        }
    };

    /*CurrentState Parameter*/
    private int mState = STATE_PREVIEW;




    public CameraPreview(Context context){
        mContext = context;
    }

    /*
    * CaptureSession의 CaptureCallback에서 수행되는 내부 메서드들이다.
    * */

    /*
    * AF/AE를 지원하는 기기에서 AF/AE 설정이 되어있지 않은 경우 수행되며 설정 후
    * 디바이스의 상태를 WAITING_PRECAPTURE로 변경한 뒤 Callback을 호출한다.
    * */
    private void runPrecaptureSequence(){
        try{

            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);

            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(),mCaptureCallback,mBackroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }
    /*
    * 디바이스의 사전 셋팅이 모두 끝난 뒤 captureBuilder를 호출하여 이미지를 저장하는 메서드이다.
    * CaptureBuilder의 호출 뒤에는 사진이 저장됨을 알리고, 촬영을 위한 셋팅을 모두 초기화시킨다.
    * */
    private void captureStillPicture(){
        try{
            final Activity activity = (Activity)mContext;
            if(null == activity || null == mCameraDevice){
                return;
            }
            /* 촬영한 사진은 여기서 CaptureRequest의 builder를 이용하여 사진을 저장한다. */
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    // 캡쳐가 완료되면 작업이 수행됨을 알려주거나, 후처리를 한다.

                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(),CaptureCallback,null);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    /* 사진의 저장을 위해 현제 화면의 상태를 알려준다*/
    private int getOrientation(int rotation){

        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /*촬영이 끝나고 나서 촬영을 위해 한 셋팅을 다시 초기화 시킨다.*/
    private void unlockFocus(){
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(),mCaptureCallback,mBackroundHandler);
            // 이 설정으로 인해 카메라는 원래 상태로 복귀한다.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest,mCaptureCallback,mBackroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    /* 카메라의 플레쉬 설정*/
    private void setAutoFlash(CaptureRequest.Builder requestBuilder){
        if(mFlashSupported){
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

}
