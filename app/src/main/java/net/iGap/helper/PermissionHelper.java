package net.iGap.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class PermissionHelper {

    private Activity activity;
    public final static int CameraAndVoicePermissionRequestCode = 100;
    public final static int VoicePermissionRequestCode = 110;
    public final static int CameraPermissionRequestCode = 120;
    public final static int StoragePermissionRequestCode = 130;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean grantCameraAndVoicePermission() {
        String[] Permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, Permissions, CameraAndVoicePermissionRequestCode);
            return false;
        }
    }

    public boolean grantVoicePermission() {
        String[] Permissions = {Manifest.permission.RECORD_AUDIO};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, Permissions, VoicePermissionRequestCode);
            return false;
        }
    }

    public boolean grantCameraPermission() {
        String[] Permissions = {Manifest.permission.CAMERA};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, Permissions, CameraPermissionRequestCode);
            return false;
        }
    }

    public boolean grantReadAndRightStoragePermission() {
        String[] Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, Permissions, StoragePermissionRequestCode);
            return false;
        }
    }

    private boolean hasPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
