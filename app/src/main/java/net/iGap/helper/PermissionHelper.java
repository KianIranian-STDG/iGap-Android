package net.iGap.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class PermissionHelper {

    private Activity activity;
    private Fragment fragment;
    public final static int CameraAndVoicePermissionRequestCode = 100;
    public final static int CameraAndStoragePermissionRequestCode = 140;
    public final static int VoicePermissionRequestCode = 110;
    public final static int CameraPermissionRequestCode = 120;
    public final static int StoragePermissionRequestCode = 130;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public PermissionHelper(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    public boolean grantCameraAndVoicePermission() {
        String[] Permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            if (fragment != null)
                fragment.requestPermissions(Permissions, CameraAndVoicePermissionRequestCode);
            else
                ActivityCompat.requestPermissions(activity, Permissions, CameraAndVoicePermissionRequestCode);
            return false;
        }
    }

    public boolean grantCameraAndStoreagePermission() {
        String[] Permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            if (fragment != null)
                fragment.requestPermissions(Permissions, CameraAndStoragePermissionRequestCode);
            else
                ActivityCompat.requestPermissions(activity, Permissions, CameraAndStoragePermissionRequestCode);
            return false;
        }
    }

    public boolean grantVoicePermission() {
        String[] Permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            if (fragment != null)
                fragment.requestPermissions(Permissions, VoicePermissionRequestCode);
            else
                ActivityCompat.requestPermissions(activity, Permissions, VoicePermissionRequestCode);
            return false;
        }
    }

    public boolean grantReadPhoneStatePermission() {
        String[] Permissions = {Manifest.permission.READ_PHONE_STATE};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            if (fragment != null)
                fragment.requestPermissions(Permissions, VoicePermissionRequestCode);
            else
                ActivityCompat.requestPermissions(activity, Permissions, VoicePermissionRequestCode);
            return false;
        }
    }

    public boolean grantCameraPermission() {
        String[] Permissions = {Manifest.permission.CAMERA};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            if (fragment != null)
                fragment.requestPermissions(Permissions, CameraPermissionRequestCode);
            else
                ActivityCompat.requestPermissions(activity, Permissions, CameraPermissionRequestCode);
            return false;
        }
    }

    public boolean grantReadAndRightStoragePermission() {
        String[] Permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (hasPermissions(Permissions)) {
            return true;
        } else {
            if (fragment != null)
                fragment.requestPermissions(Permissions, StoragePermissionRequestCode);
            else
                ActivityCompat.requestPermissions(activity, Permissions, StoragePermissionRequestCode);
            return false;
        }
    }

    public boolean hasPermissions(String... permissions) {
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
