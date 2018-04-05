package bowonlee.my.dearphotograph;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

/**
 * Created by bowon on 2018-04-04.
 */

public class PermissionHelper {

    public static int REQUEST_CAMERA_PERMISSION = 1;
    public static int REQUEST_LOCATE_PERMISSION = 2;
    private Activity mParentActivity;

    public PermissionHelper(Activity parentActivity){
         mParentActivity = parentActivity;
    }



    /*
    * 권한요청을 수행하는 다이얼로그
    * */
    public static class CustomDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity parent = getActivity();
            return new AlertDialog.Builder(getActivity()).setMessage(R.string.request_caemra_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
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
    /*
    * 권한요청이 한번이상 거부되었을 경우 출력되는 다이얼로그
    * */
    public static class DeniedDialog extends DialogFragment{

    }

}


