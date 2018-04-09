package bowonlee.my.dearphotograph;

import android.media.Image;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by bowon on 2018-04-08.
 */

/*
*   앱 내부에서 사용할 저장소 디렉토리를 만들고 관리하는 클레스
*
* */

public class FileStroageHelper {
    private String TAG = "FileStorageHelper";

    /*공유 디렉토리 File Path 생성*/
    public File getAlbumStorageDir(String albumName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),albumName);

        if(!file.exists()){
            if(!file.mkdirs()){
               Log.e(TAG,"Directory not created");
            }
        }else{
            Log.i(TAG,"Directory alreay createed");
        }

        return file;
    }
    /*
    *
    * Input : Image
    * output : True/False 작업의 성공 여부
    * */
    public File createImageToJPEG(Image image){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DearPhotoGraph");

        return file;
    }

}
