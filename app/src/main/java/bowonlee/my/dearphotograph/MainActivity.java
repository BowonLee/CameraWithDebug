package bowonlee.my.dearphotograph;

import android.graphics.SurfaceTexture;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    /*
    * 안드로이드의 카메라 프리뷰세션 여는 요청은 비동기 쓰레드 콜벡을 통해 이루어진다.
    * 따라서 권한 요청을 하기도 전에 카메라를 열려고 시도하기에 초기 1회 crash가 발생하게되므로
    * Dialog를 통해 잠시 앱의 동작을 멈추는 기능이 필요하다
    * */
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }





    /*
    * Camera의 캠쳐세션과 프리뷰세션을 출력할 surface이다.
    * */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
