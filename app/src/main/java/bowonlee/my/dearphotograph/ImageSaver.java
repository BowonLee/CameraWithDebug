package bowonlee.my.dearphotograph;

import android.media.Image;
import android.media.MediaScannerConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Created by bowon on 2018-04-08.
 */

/*
* Image ExternalMemory Save(input Image - > saved filed JPEG)
*
*
*
*
* */

public class ImageSaver implements Runnable {
    /**
     * ImageReader에서 사용하기위한 이미지 객체
     */
    private final Image mImage;
    /**
     * 이미지를 저장할 파일 객체
     */
    private File mFile;

    private FileStroageHelper mFileStroageHelper;
    private String mFileName;
    ImageSaver(Image image) {
        mImage = image;
    }

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        String getTime = sdf.format(date);
        mFileName = getTime +".jpg";


        mFileStroageHelper = new FileStroageHelper();
        mFile = new File(mFileStroageHelper.getAlbumStorageDir("DearPhotograph"),mFileName);

        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
