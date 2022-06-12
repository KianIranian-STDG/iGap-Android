/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FileManagerFragment;
import net.iGap.fragments.FragmentMap;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperString;
import net.iGap.helper.ImageHelper;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.dialog.ChatAttachmentPopup;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.IPickFile;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static net.iGap.helper.HelperPermission.showDeniedPermissionMessage;

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
    public static final int request_code_trim_video = 22;
    public static final int REQUEST_CODE_PICK_FILE_FROM_INTENT = 23;
    public static boolean isInAttach = false;
    public static String imagePath = "";
    public static Uri imageUri;
    public static String mCurrentPhotoPath;
    public static String videoPath = "";
    public SecureRandom random = new SecureRandom();
    OnComplete complete;
    private PopupWindow popupWindow;
    private FragmentActivity context;
    private LocationManager locationManager;
    private ProgressDialog pd;
    private Boolean sendPosition = false;
    private FusedLocationProviderClient fusedLocationClient;
    Location locationGPS = null;
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

                String position = location.getLatitude() + "," + location.getLongitude();

                if (complete != null) complete.complete(true, position, "");
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    //=================================== Start Android 7

    public AttachFile(FragmentActivity context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    public static ArrayList<String> getClipData(ClipData clipData) {

        if (clipData != null) {
            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                if (item.getUri() == null) {
                    continue;
                }
                String path = getFilePathFromUri(item.getUri());
                list.add(path);
            }

            if (list.size() < 1) return null;

            return list;
        }

        return null;
    }

    //=================================== End Android 7

    public static String getFilePathFromUri(Uri uri) {

        if (uri == null) {
            return null;
        }

        String path;

        if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                path = FileUtils.getPath(G.context, uri);
            } else {
                path = uri.getPath();
            }
        } else {
            path = uri.getPath();
        }

        return path;
    }

    public static String getFilePathFromUriAndCheckForAndroid7(Uri uri, HelperGetDataFromOtherApp.FileType fileType) {

        String path = getFilePathFromUri(uri);

        if (path == null) {
            path = getPathN(uri, fileType, null);
        }

        return path;
    }

    //*************************************************************************************************************

    public static String getPathN(Uri uri, HelperGetDataFromOtherApp.FileType fileType, String type) {

        if (uri == null) {
            return null;
        }

        try {
            String name = AttachFile.getFileName(uri.getPath());
            if (name == null || name.length() == 0) {
                name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            }

            try {
                String substring = name.substring(name.lastIndexOf("."));

            } catch (StringIndexOutOfBoundsException e) {

                if (type != null) {
                    String ex = type.substring(type.lastIndexOf("/") + 1);
                    name += "." + ex;
                }
            }

            String destinationPath = "";

            switch (fileType) {

                case video:
                    destinationPath = G.context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
                    break;
                case audio:
                    destinationPath = G.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
                    break;
                case image:
                    destinationPath = G.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                    break;

            }

            destinationPath += File.separator + name;

            if (FileProvider.getUriForFile(G.context, G.context.getApplicationContext().getPackageName() + ".provider", new File(destinationPath)).equals(uri)) {
                // shared from igap to igap
            } else {
                InputStream input = G.context.getContentResolver().openInputStream(uri);
                AndroidUtils.copyFile(input, new File(destinationPath));
            }

            return destinationPath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileName(String path) {

        if (path == null) return "";
        if (path.length() < 1) return "";

        String filename = path.substring(path.lastIndexOf("/") + 1);

        return filename;
    }

    //*************************************************************************************************************

    public void dispatchTakePictureIntent(Fragment fragment) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(G.context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                if (fragment != null && fragment.isAdded()) {
                    fragment.startActivityForResult(takePictureIntent, request_code_TAKE_PICTURE);
                } else {
                    ((Activity) context).startActivityForResult(takePictureIntent, request_code_TAKE_PICTURE);
                }
            }
        }
    }

    public void dispatchTakePictureIntent() throws IOException {
        dispatchTakePictureIntent(null);
    }

    //*************************************************************************************************************

    /**
     * open page paint
     *
     * @throws IOException
     */

    public static void getAllShownImagesPath(Activity activity, int count, ChatAttachmentPopup.OnImagesGalleryPrepared onImagesGalleryPrepared) {
        if (ContextCompat.checkSelfPermission(G.context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<>();
                    Uri uri;
                    Cursor cursor;
                    int column_index_data;
                    String absolutePathOfImage;
                    uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                    String[] projection = {
                            MediaStore.MediaColumns.DATA
                    };

                    cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC");

                    if (cursor != null) {
                        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                        while (cursor.moveToNext()) {
                            absolutePathOfImage = cursor.getString(column_index_data);

                            StructBottomSheet item = new StructBottomSheet();
                            item.setId(listOfAllImages.size());
                            item.setPath(absolutePathOfImage);
                            item.isSelected = true;
                            listOfAllImages.add(item);
                            if (listOfAllImages.size() >= count) break;
                        }
                        cursor.close();
                        onImagesGalleryPrepared.imagesList(listOfAllImages);

                    }
                }
            }).start();
        }
    }

    //*************************************************************************************************************

    /**
     * open camera
     *
     * @param fragment
     * @throws IOException
     */

    public void requestTakePicture(final Fragment fragment) throws IOException {

        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }

        HelperPermission.getCameraPermission(context, new OnGetPermission() {
            @Override
            public void Allow() {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    dispatchTakePictureIntent(fragment);
                } else {
                    Uri outPath = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, 0);

                    if (outPath != null) {
                        try {
                            imagePath = outPath.getPath();
                            imageUri = outPath;
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPath);
                            if (fragment != null && fragment.isAdded()) {
                                fragment.startActivityForResult(intent, request_code_TAKE_PICTURE);
                            } else {
                                ((Activity) context).startActivityForResult(intent, request_code_TAKE_PICTURE);
                            }
                            isInAttach = true;
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_camera));

            }
        });

    }

    public void requestTakePicture() throws IOException {
        requestTakePicture(null);
    }

    //*************************************************************************************************************

    /**
     * open gallery for multi choose image
     *
     * @param fragment
     * @throws IOException
     * @throws AttachFile
     */
    public void requestOpenGalleryForImageMultipleSelect(final Fragment fragment) throws IOException {

        //this code use for open galary for image and video together
        //Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
        //((Activity) context).startActivityForResult(intent, request_code_media_from_gallery);

        HelperPermission.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setAction(Intent.ACTION_GET_CONTENT);

                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestOpenGalleryForImageMultipleSelect);
                } else {
                    ((Activity) context).startActivityForResult(intent, requestOpenGalleryForImageMultipleSelect);
                }

                isInAttach = true;
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
            }
        });
    }

    public void requestOpenGalleryForImageMultipleSelect() throws IOException {
        requestOpenGalleryForImageMultipleSelect(null);
    }

    //*************************************************************************************************************

    /**
     * open gallery for multi choose Video
     *
     * @param fragment
     * @throws IOException
     */
    public void requestOpenGalleryForVideoMultipleSelect(final Fragment fragment) throws IOException {

        HelperPermission.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);

                if (fragment != null) {
                    fragment.startActivityForResult(intent, requestOpenGalleryForVideoMultipleSelect);
                } else {
                    ((Activity) context).startActivityForResult(intent, requestOpenGalleryForVideoMultipleSelect);
                }


                isInAttach = true;
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
            }
        });
    }

    public void requestOpenGalleryForVideoMultipleSelect() throws IOException {
        requestOpenGalleryForVideoMultipleSelect(null);
    }

    //*************************************************************************************************************

    /**
     * open gallery for single choose image
     *
     * @param fragment
     * @throws IOException
     */
    public void requestOpenGalleryForImageSingleSelect(final Fragment fragment) throws IOException {

        HelperPermission.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                if (fragment != null) {
                    if (fragment.isAdded()) {
                        fragment.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                    }
                } else {
                    ((Activity) context).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                }


                isInAttach = true;
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
            }
        });
    }

    public void requestOpenGalleryForImageSingleSelect() throws IOException {
        requestOpenGalleryForImageSingleSelect(null);
    }

    //*************************************************************************************************************

    /**
     * open camera for record video
     *
     * @param fragment
     * @throws IOException
     */
    public void requestVideoCapture(final Fragment fragment) throws IOException {

        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
            Toast.makeText(context, context.getString(R.string.device_dosenot_camera_en), Toast.LENGTH_SHORT).show();
            return;
        }

        HelperPermission.getCameraPermission(context, new OnGetPermission() {
            @Override
            public void Allow() {


                File videoFile = null;
                try {
                    videoFile = createVideoFile();
                    videoPath = videoFile.getAbsolutePath();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                    return;
                }

                Uri outputUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    outputUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", videoFile);
                } else {
                    outputUri = Uri.fromFile(new File(videoPath));
                }


                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

                if (fragment != null) {
                    fragment.startActivityForResult(intent, request_code_VIDEO_CAPTURED);
                } else {
                    ((Activity) context).startActivityForResult(intent, request_code_VIDEO_CAPTURED);
                }


                isInAttach = true;
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_camera));
            }
        });
    }

    public void requestVideoCapture() throws IOException {
        requestVideoCapture(null);
    }

    //*************************************************************************************************************

    /**
     * open gallery for pick Audio
     *
     * @param fragment
     * @throws IOException
     */
    public void requestPickAudio(final Fragment fragment) throws IOException {
        //Intent intent = new Intent();
        //intent.setActionTyping(Intent.ACTION_PICK);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //((Activity) context).startActivityForResult(intent, request_code_pic_audi);

        HelperPermission.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                if (fragment != null) {
                    fragment.startActivityForResult(intent, request_code_pic_audi);
                } else {
                    ((Activity) context).startActivityForResult(intent, request_code_pic_audi);
                }


                isInAttach = true;
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
            }
        });
    }

    public void requestPickAudio() throws IOException {
        requestPickAudio(null);
    }

    /**
     * open folder for pick file
     *
     * @param listener
     * @throws IOException
     */
    public void requestPickFile(final IPickFile listener) throws IOException {

        /**Following codes where commented due to new google storage accessibility policy, do not let to app
         * that get top storage permission (MANAGE_STORAGE_PERMISSION)*/
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                new HelperFragment(context.getSupportFragmentManager(), FileManagerFragment.newInstance(listener)).setReplace(false).load();
//            } else {
//                showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
//            }
//        } else {
        HelperPermission.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        if (Build.VERSION.SDK_INT >= 18) {
                            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        }
                        photoPickerIntent.setType("*/*");
                        G.fragmentActivity.startActivityForResult(photoPickerIntent, REQUEST_CODE_PICK_FILE_FROM_INTENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    new HelperFragment(context.getSupportFragmentManager(), FileManagerFragment.newInstance(listener)).setReplace(false).load();
                }
            }

            @Override
            public void deny() {
                if (context != null) {
                    HelperError.showSnackMessage(context.getString(R.string.you_need_to_allow) + " " + context.getString(R.string.permission_storage), false);
                    showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
                }
            }
        });

    }

    public void requestOpenDocumentFolder(final IPickFile listener) throws IOException {

        HelperPermission.getStoragePermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                //Intent intent = new Intent(context, ActivityExplorer.class);
                //intent.putExtra("Mode", "documnet");
                //((Activity) context).startActivityForResult(intent, request_code_open_document);
                //if (G.onHelperSetAction != null) {
                //    G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.SENDING_DOCUMENT);
                //}

                /*FragmentExplorer fragment = new FragmentExplorer();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Listener", listener);
                bundle.putString("Mode", "documnet");
                fragment.setArguments(bundle);
*/
                /*new HelperFragment(fragment).setReplace(false).load();*/
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_storage));
            }
        });
    }
    //*************************************************************************************************************

    /**
     * pick contact number
     *
     * @param fragment
     * @throws IOException
     */
    public void requestPickContact(final Fragment fragment) throws IOException {

        HelperPermission.getContactPermision(context, new OnGetPermission() {
            @Override
            public void Allow() {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                if (fragment != null) {
                    fragment.startActivityForResult(intent, request_code_contact_phone);
                } else {
                    ((Activity) context).startActivityForResult(intent, request_code_contact_phone);
                }


                if (G.onHelperSetAction != null) {
                    G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.CHOOSING_CONTACT);
                }
                isInAttach = true;
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_contact));

            }
        });
    }

    public void requestPickContact() throws IOException {
        requestPickContact(null);
    }

    /**
     * get position
     *
     * @throws IOException
     */

    public void requestGetPosition() throws IOException {

        HelperPermission.getLocationPermission(context, new OnGetPermission() {
            @Override
            public void Allow() {
                getPosition();
            }

            @Override
            public void deny() {
                showDeniedPermissionMessage(G.context.getString(R.string.permission_location));
            }
        });
    }

    private void getPosition() {
        try {
            FragmentMap fragment = FragmentMap.newInstance();
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showSettingsAlert(fragment);
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                new HelperFragment(G.fragmentActivity.getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void showSettingsAlert(final Fragment fragment) {

        new MaterialDialog.Builder(context).title(context.getString(R.string.do_you_want_to_turn_on_gps)).positiveText(R.string.yes).negativeText(R.string.no).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);

                turnOnGps(fragment);
            }
        }).show();
    }

    private void turnOnGps(Fragment fragment) {

        ((Activity) context).startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), request_code_position);

        isInAttach = true;
    }

    public String saveGalleryPicToLocal(String galleryPath) {

        String result = "";
        if (galleryPath == null) return "";

        if (ImageHelper.isRotateNeed(galleryPath) || ImageHelper.isNeedToCompress(new File(galleryPath))) {
            ImageHelper imageHelper = new ImageHelper();
            Bitmap bitmap = imageHelper.decodeFile(new File(galleryPath));
            bitmap = imageHelper.correctRotate(galleryPath, bitmap);

            if (bitmap != null) {
                result = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, 1).getPath();
                ImageHelper.SaveBitmapToFile(result, bitmap);
            }

            return result;
        } else {

            return galleryPath;
        }
    }

    private Uri getOutputMediaFileUri(int type, int camera) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && camera == 0) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else {
            return Uri.fromFile(getOutputMediaFile(type));
        }
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(G.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();

            //if (!mediaStorageDir.mkdirs()) {
            //    return null;
            //}
        }

        File mediaFile;

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "image_" + HelperString.getRandomFileName(3) + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public void showDialogOpenCamera(View view, final ProgressBar prgWaiting, final Fragment fragment) {

        new MaterialDialog.Builder(context).items(R.array.capture).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                switch (which) {
                    case 0:

                        try {
                            requestTakePicture(fragment);
                            if (G.onHelperSetAction != null) {
                                G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.CAPTURING_IMAGE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                        if (prgWaiting != null) {
                            prgWaiting.setVisibility(View.VISIBLE);
                        }

                        break;
                    case 1:
                        try {
                            requestVideoCapture(fragment);
                            if (G.onHelperSetAction != null) {
                                G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.CAPTURING_VIDEO);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                        break;
                }
            }
        }).negativeText(R.string.B_cancel).show();
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + random.nextInt(1000) + 1);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).exists()) {
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()).mkdirs();
        }

        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public File createVideoFile() throws IOException {
        // Create an image file name
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + random.nextInt(1000) + 1);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
        String imageFileName = "VID_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).exists()) {
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()).mkdirs();
        }

        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void onClickGallery() {
        List<Intent> targets = new ArrayList<>();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        List<ResolveInfo> candidates = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (!packageName.equals("com.google.android.apps.photos") && !packageName.equals("com.google.android.apps.plus") && !packageName.equals("com.android.documentsui")) {
                Intent iWantThis = new Intent();
                iWantThis.setType("image/*");
                iWantThis.setAction(Intent.ACTION_PICK);
                iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                iWantThis.setPackage(packageName);
                targets.add(iWantThis);
            }
        }
        if (targets.size() > 0) {
            Intent chooser = Intent.createChooser(targets.remove(0), "Select Picture");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
            ((Activity) context).startActivityForResult(chooser, request_code_image_from_gallery_single_select);
        } else {
            Intent intent1 = new Intent(Intent.ACTION_PICK);
            intent1.setType("image/*");
            ((Activity) context).startActivityForResult(Intent.createChooser(intent1, "Select Picture"), request_code_image_from_gallery_single_select);
        }
    }
}
