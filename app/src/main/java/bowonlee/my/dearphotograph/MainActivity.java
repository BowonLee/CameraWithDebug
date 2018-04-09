package bowonlee.my.dearphotograph;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    /*
    * 안드로이드의 카메라 프리뷰세션 여는 요청은 비동기 쓰레드 콜벡을 통해 이루어진다.
    * 따라서 권한 요청을 하기도 전에 카메라를 열려고 시도하기에 초기 1회 crash가 발생하게되므로
    * Dialog를 통해 잠시 앱의 동작을 멈추는 기능이 필요하다
    * */
    private static final String ALBUMNAME = "DearPhotograph";

    private AutoFitTextureView mTextureView;
    private CameraPreview cameraPreview;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int LOCATE_PERMISSION =2;
    private ImageView mImageView;
    private FileStroageHelper mFileStroageHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileStroageHelper = new FileStroageHelper();
        mFileStroageHelper.getAlbumStorageDir(ALBUMNAME);

        mTextureView = (AutoFitTextureView) findViewById(R.id.camera_preview_session);
        mImageView = (ImageView)findViewById(R.id.imageview);
        setRequestCameraPermission();

    }



    @Override
    protected void onResume() {
        super.onResume();
        cameraPreview = new CameraPreview(this,mTextureView);

        cameraPreview.startBackgroundThread();

        /*
        * 앱을 실행한 경우이면 surfaceTexture부터 생성하고 카메라를 오픈하지만
        * 단순히 화면만 껏다켠 경우는 카메라장치만 다시 열면 된다.
        * */
        if(mTextureView.isAvailable()){
            Log.e("MainActivity",String.format("textureview width : %d textureviewHeight : %d ",mTextureView.getWidth(),mTextureView.getHeight()) );

            cameraPreview.openCamera(mTextureView.getWidth(),mTextureView.getHeight());

        }else{
            cameraPreview.setSurface();
        }

        if(mImageView!=null){
          //,    setmImageView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.closeCamera();
        cameraPreview.stopBackgroundThread();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setRequestCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            new ConfirmationDialog().show(getSupportFragmentManager(),"dialog");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //퍼미션이 거부되었음,
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static class ConfirmationDialog extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity parent = getActivity();
            return new AlertDialog.Builder(getActivity()).setMessage(R.string.request_caemra_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CAMERA_PERMISSION);
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(parent != null){
                                parent.finish();
                            }
                        }
                    }).create();

        }
    }



    public void setmImageView(){
        //Bitmap image =
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;


        mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cafe_demoimage,options));

    }


    private void takePicture(){
        /*사진 촬영과 저장*/
    }
    public void onClickSutter(View view){
        cameraPreview.takePicture();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
