package com.iGap.module;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityExplorer;
import com.iGap.activitys.ActivityPaint;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ali on 9/12/2016.
 */
public class AttachFile {

    public static final int request_code_TAKE_PICTURE = 10;
    public static final int request_code_media_from_gallary = 11;
    public static final int request_code_VIDEO_CAPTURED = 12;
    public static final int request_code_pic_audi = 13;
    public static final int request_code_pic_file = 14;
    public static final int request_code_contact_phone = 15;
    public static final int request_code_position = 16;
    public static final int request_code_paint = 17;
    public static final int MEDIA_TYPE_IMAGE = 20;


    public static String imagePath = "";
    public static final String IMAGE_DIRECTORY_NAME = "Upload";


    private Context context;

    private LocationManager locationManager;
    private ProgressDialog pd;
    private Boolean sendPosition = false;
    OnComplete complete;

    public AttachFile(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    //*************************************************************************************************************

    public void requestPaint() {
        Intent intent = new Intent(context, ActivityPaint.class);
        ((Activity) context).startActivityForResult(intent, request_code_paint);
    }

    //*************************************************************************************************************

    public void requestTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outPath = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        if (outPath != null) {
            imagePath = outPath.getPath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPath);
            ((Activity) context).startActivityForResult(intent, request_code_TAKE_PICTURE);
        } else {
            Log.e("dddd", "file path is null");
        }
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        File mediaFile;

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

//*************************************************************************************************************

    public void requestOpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
        ((Activity) context).startActivityForResult(intent, request_code_media_from_gallary);
    }

    public void requestOpenGalleryForImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_media_from_gallary);
    }

//*************************************************************************************************************

    public void requestVideoCapture() {

        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        ((Activity) context).startActivityForResult(intent, request_code_VIDEO_CAPTURED);
    }

//*************************************************************************************************************

    public void requestPickAudio() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, request_code_pic_audi);
    }

    //*************************************************************************************************************

    public void requestPickFile() {
        Intent intent = new Intent(context, ActivityExplorer.class);
        ((Activity) context).startActivityForResult(intent, request_code_pic_file);
    }

    //*************************************************************************************************************

    public void requestPickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        ((Activity) context).startActivityForResult(intent, request_code_contact_phone);
    }

    //*************************************************************************************************************

    public void requestGetPosition(OnComplete complete) {

        this.complete = complete;

        getPosition();

    }

    private void getPosition() {


        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingsAlert();
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    location.getLatitude();
                    location.getLongitude();

                    String position = context.getString(R.string.my_Position_is) + "\n" + context.getString(R.string.latitude) + String.valueOf(location.getLatitude()) +
                            "\n" + context.getString(R.string.longitude) + String.valueOf(location.getLongitude());

                    if (complete != null)
                        complete.complete(true, position, "");


                } else {
                    sendPosition = true;
                    pd = new ProgressDialog(context);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage(context.getString(R.string.just_wait_en));
                    pd.setIndeterminate(false);
                    pd.setCancelable(true);
                    pd.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


        @Override
        public void onProviderEnabled(String provider) {

        }


        @Override
        public void onProviderDisabled(String provider) {

        }


        @Override
        public void onLocationChanged(Location location) {

            if (sendPosition) {
                sendPosition = false;

                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                location.getLatitude();
                location.getLongitude();

                String position = context.getString(R.string.my_Position_is) + "\n" + context.getString(R.string.latitude) + String.valueOf(location.getLatitude()) +
                        "\n" + context.getString(R.string.longitude) + String.valueOf(location.getLongitude());

                if (complete != null)
                    complete.complete(true, position, "");
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    };

    void showSettingsAlert() {

        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.do_you_want_to_turn_on_gps))
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        turnOnGps();
                    }
                })
                .show();
    }

    private void turnOnGps() {
        ((Activity) context).startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), request_code_position);
    }

    //*************************************************************************************************************

    private static String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = G.context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getFilePathFromUri(Uri uri) {

        String path = "";

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            path = getRealPathFromURI(uri);
        } else {
            path = uri.getPath();
        }

        return path;
    }

    //*************************************************************************************************************


}
