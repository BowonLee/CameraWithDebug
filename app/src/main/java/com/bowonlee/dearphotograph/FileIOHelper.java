package com.bowonlee.dearphotograph;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowon on 2018-04-08.
 */

/*
*   앱 내부에서 사용할 저장소 디렉토리를 만들고 관리하는 클레스
*
* */

public class FileIOHelper {
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

  /*인자값을 토대로 쿼리를 생성해 리턴해준다. */
  private void makeQuary(Context context,String mediapath){

      Uri externalUri = MediaStore.Files.getContentUri("external");
      String[] projection = new String[]{MediaStore.Files.FileColumns.PARENT};
      String selection = MediaStore.Files.FileColumns.DATA + "=?";
      String[] selectionArgs = new String[]{mediapath};
      CancellationSignal sortOrder = null;


      context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
              new String[]{MediaStore.Files.FileColumns.PARENT},MediaStore.Files.FileColumns.DATA+"=?",new String[]{mediapath},null);


  }

  //모든 미디어 엘범을 요청하는 쿼리를 제공한다.
  /*public Cursor takeAllAlbums(Context context){

      return makeQuary();
  }*/

  //모든 사진 파일을 요청하는 쿼리를 주는 클레스 주요 기능의 구현을 위해 테스트용 메소드로 작성한다.
  //이후 빌더패턴을 적용하도록 한다.
  public List<Uri> takeAllMediaDataUri(Context context){
      Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      String[] projection = new String[]{MediaStore.Images.Media.DATA};
      String selection = MediaStore.Files.FileColumns.DATA + "=?";
      //String[] selectionArgs = new String[]{mediapath};
      CancellationSignal sortOrder = null;

      Cursor imageCursor =  context.getContentResolver().query(externalUri,projection,null,null,null);

      ArrayList<Uri> result = new ArrayList<>(imageCursor.getCount());
      int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);

      if(imageCursor == null){
          //error
      }else if(imageCursor.moveToFirst()){
          do {
              String filePath = imageCursor.getString(dataColumnIndex);
              Uri imageUri = Uri.parse(filePath);
              result.add(imageUri);
          }while (imageCursor.moveToNext());
      }else{
          //emptyCursor
      }
      imageCursor.close();
      return result;

  }



}
