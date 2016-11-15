package com.iGap.module;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityExplorer;
import com.iGap.activities.ActivityPaint;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnGetPermision;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ali on 9/12/2016.
 */
public class AttachFile {

    public static final int request_code_TAKE_PICTURE = 10;
    public static final int request_code_image_from_gallery_single_select = 11;
    public static final int request_code_VIDEO_CAPTURED = 12;
    public static final int request_code_pic_audi = 13;
    public static final int request_code_pic_file = 14;
    public static final int request_code_contact_phone = 15;
    public static final int request_code_position = 16;
    public static final int request_code_paint = 17;
    public static final int MEDIA_TYPE_IMAGE = 18;
    public static final int requestOpenGalleryForImageMultipleSelect = 19;
    public static final int requestOpenGalleryForVideoMultipleSelect = 20;
    public static final int request_code_open_document = 21;

    private PopupWindow popupWindow;

    public static final String IMAGE_DIRECTORY_NAME = "Upload";
    public static boolean isInAttach = false;
    public static String imagePath = "";
    OnComplete complete;
    private Context context;
    private LocationManager locationManager;
    private ProgressDialog pd;
    private Boolean sendPosition = false;
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

                String position = String.valueOf(location.getLatitude()) +
                        "," + String.valueOf(location.getLongitude());

                if (complete != null) complete.complete(true, position, "");
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    //*************************************************************************************************************

    public AttachFile(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    public static String getFilePathFromUri(Uri uri) {
        String path;

        if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            path = FileUtils.getPath(G.context, uri);
        } else {
            path = uri.getPath();
        }

        return path;
    }

    public void requestPaint() {

        HelperPermision.getStoragePermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                Intent intent = new Intent(context, ActivityPaint.class);
                ((Activity) context).startActivityForResult(intent, request_code_paint);
            }
        });
    }

    public void requestTakePicture() {


        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }


        HelperPermision.getCamarePermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri outPath = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                if (outPath != null) {
                    imagePath = outPath.getPath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outPath);
                    ((Activity) context).startActivityForResult(intent, request_code_TAKE_PICTURE);
                    isInAttach = true;
                } else {
                    Log.e("dddd", "file path is null");
                }
            }
        });


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

    //*************************************************************************************************************
    public void requestOpenGalleryForImageMultipleSelect() {

        //this code use for open galary for image and video together
        //Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
        //((Activity) context).startActivityForResult(intent, request_code_media_from_gallery);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, requestOpenGalleryForImageMultipleSelect);
        isInAttach = true;
    }

    //*************************************************************************************************************
    public void requestOpenGalleryForVideoMultipleSelect() {

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, requestOpenGalleryForVideoMultipleSelect);
        isInAttach = true;
    }

    //*************************************************************************************************************
    public void requestOpenGalleryForImageSingleSelect() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
        isInAttach = true;
    }

    //*************************************************************************************************************

    //*************************************************************************************************************
    public void requestVideoCapture() {

        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }

        HelperPermision.getCamarePermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                ((Activity) context).startActivityForResult(intent, request_code_VIDEO_CAPTURED);
                isInAttach = true;
            }
        });


    }

    public void showDialogOpenCamera(View view) {

        LinearLayout layoutDialog = new LinearLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        TextView text1 = new TextView(context);
        TextView text2 = new TextView(context);


        text1.setTextColor(context.getResources().getColor(android.R.color.black));
        text2.setTextColor(context.getResources().getColor(android.R.color.black));


        text1.setText("capture image");
        text2.setText("capture video");


        int dim20 = (int) context.getResources().getDimension(R.dimen.dp20);
        int dim16 = (int) context.getResources().getDimension(R.dimen.dp16);
        int dim12 = (int) context.getResources().getDimension(R.dimen.dp12);
        int sp16 = (int) context.getResources().getDimension(R.dimen.sp12);

        text1.setTextSize(14);
        text2.setTextSize(14);


        text1.setPadding(dim20, dim12, dim12, dim20);
        text2.setPadding(dim20, 0, dim12, dim20);


        layoutDialog.addView(text1, params);
        layoutDialog.addView(text2, params);

        final int screenWidth = (int) (context.getResources().getDisplayMetrics().widthPixels / 1.7);

        popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.shadow2, context.getTheme()));
        } else {
            popupWindow.setBackgroundDrawable((context.getResources().getDrawable(R.mipmap.shadow2)));
        }
        if (popupWindow.isOutsideTouchable()) {
            popupWindow.dismiss();
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //TODO do sth here on dismiss
            }
        });

        popupWindow.showAsDropDown(view);

        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestTakePicture();

                popupWindow.dismiss();

            }
        });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestVideoCapture();

                popupWindow.dismiss();


            }
        });
    }

    //*************************************************************************************************************

    public void requestPickAudio() {
        //Intent intent = new Intent();
        //intent.setActionTyping(Intent.ACTION_PICK);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //((Activity) context).startActivityForResult(intent, request_code_pic_audi);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        ((Activity) context).startActivityForResult(intent, request_code_pic_audi);

        isInAttach = true;
    }

    public void requestPickFile() {
        HelperPermision.getStoragePermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                Intent intent = new Intent(context, ActivityExplorer.class);
                ((Activity) context).startActivityForResult(intent, request_code_pic_file);
            }
        });
    }

    public void requestPickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        ((Activity) context).startActivityForResult(intent, request_code_contact_phone);
        isInAttach = true;
    }

    public void requestGetPosition(OnComplete complete) {

        this.complete = complete;

        HelperPermision.getLocationPermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                getPosition();
            }
        });
    }

    private void getPosition() {

        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingsAlert();
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                    String position = String.valueOf(location.getLatitude()) +
                            "," + String.valueOf(location.getLongitude());

                    if (complete != null) complete.complete(true, position, "");
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

    public void requestOpenDocumentFolder() {


        HelperPermision.getStoragePermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                Intent intent = new Intent(context, ActivityExplorer.class);
                intent.putExtra("Mode", "documnet");
                ((Activity) context).startActivityForResult(intent, request_code_open_document);
            }
        });

    }

    //*************************************************************************************************************

    void showSettingsAlert() {

        new MaterialDialog.Builder(context).title(context.getString(R.string.do_you_want_to_turn_on_gps))
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
        isInAttach = true;
    }

    //*************************************************************************************************************

    public ArrayList<String> getClipData(ClipData clipData) {

        if (clipData != null) {
            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);

                Log.e("ddd", item.getUri() + "");
                String path = getFilePathFromUri(item.getUri());
                list.add(path);

                Log.e("ddd", path + "  " + i);
            }

            if (list.size() < 1) return null;

            return list;
        }

        return null;
    }

    public String getFileName(String path) {

        if (path == null) return "";
        if (path.length() < 1) return "";

        String filename = path.substring(path.lastIndexOf("/") + 1);

        return filename;
    }
}
