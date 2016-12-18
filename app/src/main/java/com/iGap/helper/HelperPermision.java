package com.iGap.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.iGap.R;
import com.iGap.interfaces.OnGetPermision;
import java.util.ArrayList;

/**
 * Created by android3 on 10/26/2016.
 */

public class HelperPermision {

    private static final int MY_PERMISSIONS_CAMERA = 201;
    private static final int MY_PERMISSIONS_STORAGE = 202;
    private static final int MY_PERMISSIONS_CONTACTS = 203;
    private static final int MY_PERMISSIONS_CALENDAR = 204;
    private static final int MY_PERMISSIONS_LOCATION = 205;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 206;
    private static final int MY_PERMISSIONS_Phone = 207;
    private static final int MY_PERMISSIONS_Sms = 208;

    private static OnGetPermision ResultCamera;
    private static OnGetPermision ResultStorage;
    private static OnGetPermision ResultContact;
    private static OnGetPermision ResultCalendar;
    private static OnGetPermision ResultLocation;
    private static OnGetPermision ResultRecordAudio;
    private static OnGetPermision ResultPhone;
    private static OnGetPermision ResultSms;

    public static OnGetPermision onDenyStorage;

    //************************************************************************************************************
    public static void getCamarePermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultCamera = onGetPermision;

        int permissionCheck =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            getPremision(context, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_CAMERA);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getStoragePermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultStorage = onGetPermision;

        ArrayList<String> needPermosion = null;

        int permissionReadStorage =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPremision(context, mStringArray, MY_PERMISSIONS_STORAGE);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getContactPermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultContact = onGetPermision;

        ArrayList<String> needPermosion = null;

        int permissionReadContact =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        int permissionWriteContact =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
        //  int permissionWritGetAccunt = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);

        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.READ_CONTACTS);
        }

        if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.WRITE_CONTACTS);
        }

        //if (permissionWritGetAccunt != PackageManager.PERMISSION_GRANTED) {
        //    if (needPermosion == null) {
        //        needPermosion = new ArrayList<>();
        //    }
        //    needPermosion.add(Manifest.permission.GET_ACCOUNTS);
        //
        //}

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPremision(context, mStringArray, MY_PERMISSIONS_CONTACTS);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getCalendarPermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultCalendar = onGetPermision;

        ArrayList<String> needPermosion = null;

        int permissionReadCalendar =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        int permissionWriteCaledar =
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR);

        if (permissionReadCalendar != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.READ_CALENDAR);
        }

        if (permissionWriteCaledar != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.WRITE_CALENDAR);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPremision(context, mStringArray, MY_PERMISSIONS_CALENDAR);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getLocationPermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultLocation = onGetPermision;

        ArrayList<String> needPermosion = null;

        int permissionFineLocation =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoreseLocation =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionCoreseLocation != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPremision(context, mStringArray, MY_PERMISSIONS_LOCATION);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getMicroPhonePermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultRecordAudio = onGetPermision;

        int permissionCheck =
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            getPremision(context, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_RECORD_AUDIO);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getPhonePermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultPhone = onGetPermision;

        ArrayList<String> needPermosion = null;

        int permissionCallPhone =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        int permissionReadPhoneState =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);

        //        int permissionReadCallLog = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG);
        //        int permissionWriteCallLog = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG);
        //        int permissionAddVoiceMaile = ContextCompat.checkSelfPermission(context, Manifest.permission.ADD_VOICEMAIL);
        //        int permissionUseSip = ContextCompat.checkSelfPermission(context, Manifest.permission.USE_SIP);
        //        int permissionProcessOutgoingCall = ContextCompat.checkSelfPermission(context, Manifest.permission.PROCESS_OUTGOING_CALLS);

        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.CALL_PHONE);
        }

        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPremision(context, mStringArray, MY_PERMISSIONS_Phone);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    public static void getSmsPermision(Context context, OnGetPermision onGetPermision) {

        if (checkApi()) {
            if (onGetPermision != null)
                onGetPermision.Allow();
            return;
        }

        ResultSms = onGetPermision;

        ArrayList<String> needPermosion = null;

        int permissionReciveSms =
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        int permissionReadSms =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);

        //        int permissionSendSms = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        //        int permissionReciveWashBush = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_WAP_PUSH);
        //        int permissionMms = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_MMS);

        if (permissionReciveSms != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.RECEIVE_SMS);
        }

        if (permissionReadSms != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.READ_SMS);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPremision(context, mStringArray, MY_PERMISSIONS_Sms);
        } else {
            if (onGetPermision != null)
                onGetPermision.Allow();
        }
    }

    //************************************************************************************************************
    //************************************************************************************************************
    private static boolean checkApi() {
        if (Build.VERSION.SDK_INT >= 23) {
            return false;
        }
        return true;
    }

    //************************************************************************************************************
    public static void getPremision(final Context context, final String[] needPermision,
                                    final int requestCode) {

        if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, needPermision[0])) {
            showMessageOKCancel(context, context.getString(R.string.you_need_to_allow) + needPermision[0], new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, needPermision, requestCode);
                        }
                    });
            return;
        }

        ActivityCompat.requestPermissions((Activity) context, needPermision, requestCode);
    }

    private static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), okListener)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .create()
                .show();
    }

    //************************************************************************************************************
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA:
                actionResultBack(grantResults, ResultCamera);
                break;
            case MY_PERMISSIONS_STORAGE:
                actionResultBack(grantResults, ResultStorage);
                break;
            case MY_PERMISSIONS_CONTACTS:
                actionResultBack(grantResults, ResultContact);
                break;
            case MY_PERMISSIONS_CALENDAR:
                actionResultBack(grantResults, ResultCalendar);
                break;
            case MY_PERMISSIONS_LOCATION:
                actionResultBack(grantResults, ResultLocation);
                break;
            case MY_PERMISSIONS_RECORD_AUDIO:
                actionResultBack(grantResults, ResultRecordAudio);
                break;
            case MY_PERMISSIONS_Phone:
                actionResultBack(grantResults, ResultPhone);
                break;
            case MY_PERMISSIONS_Sms:
                actionResultBack(grantResults, ResultSms);
                break;
        }
    }

    //************************************************************************************************************

    private static void actionResultBack(int[] grantResults, OnGetPermision onGetPermision) {

        boolean allOk = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) allOk = false;
        }

        if (allOk) {
            if (onGetPermision != null) onGetPermision.Allow();
        } else if (onDenyStorage != null) {
            onDenyStorage.Allow();
        }
    }

    //************************************************************************************************************
}
