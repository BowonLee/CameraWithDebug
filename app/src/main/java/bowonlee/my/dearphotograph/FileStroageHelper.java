package bowonlee.my.dearphotograph;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by bowon on 2018-04-08.
 */

/*
* 앱 내부에서 사용할 저장소 디렉토리를 만들고 관리하는 클레스
*
* */

public class FileStroageHelper {
    private String TAG = "FileStorageHelper";

    /*공유 디렉토리 File Path 생성*/
    public File getAlbumStorageDir(String albumName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),albumName);
        if(!file.mkdirs()){
            Log.e(TAG,"Directory not created");
        }

        return file;
    }


}
