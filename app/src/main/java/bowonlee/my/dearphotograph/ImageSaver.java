package bowonlee.my.dearphotograph;

import android.content.Context;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

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

    interface OnMImageSaveListener{
        void onSaveImageComplete(File mFile);
    }
    private OnMImageSaveListener mImageSaveListener;
    private Image mImage;

   /**
     * 이미지를 저장할 파일 객체
     */
    private File mFile;
    private Context mContext;

    private FileStroageHelper mFileStroageHelper;
    private String mFileName;
    ImageSaver(){}
    ImageSaver(Image image,Context context) {
        mImage = image;
        mContext =context;
    }

    public void setOnImageSaveListner(OnMImageSaveListener listner){
        mImageSaveListener = listner;

    }


    @Override
    public void run() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        String getTime = sdf.format(date);
        mFileName = "dpf"+getTime +".jpg";


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
        MediaScannerConnection.scanFile(mContext, new String[]{mFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage","Scanned : " + path);
                        Log.i("ExternalStorage","URI : " + uri);

                    }
                }
        );




    }
}
