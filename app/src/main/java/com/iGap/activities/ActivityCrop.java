package com.iGap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.ImageHelper;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AttachFile;
import com.iGap.module.HelperCopyFile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityCrop extends ActivityEnhanced {

    public final String IMAGE_DIRECTORY_NAME = "Upload";
    private ImageView imgPic;
    private Uri uri;
    private TextView txtCancel, txtSet, txtCrop, txtAgreeImage;
    private long idAvatar;
    private String pathSaveImage;
    private String page;
    private String type;
    private int id;
    private String pathImageUser;
    private File mediaStorageDir;
    private File fileChat;
    private String result;
    private ProgressBar prgWaiting;
    AttachFile attachFile;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        attachFile = new AttachFile(this);

        prgWaiting = (ProgressBar) findViewById(R.id.crop_prgWaiting);
        imgPic = (ImageView) findViewById(R.id.pu_img_imageBefore);
        txtAgreeImage = (TextView) findViewById(R.id.pu_txt_agreeImage);

        txtCancel = (TextView) findViewById(R.id.pu_txt_cancel_crop);
        txtSet = (TextView) findViewById(R.id.pu_txt_set_crop);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            uri = Uri.parse(bundle.getString("IMAGE_CAMERA"));
            page = bundle.getString("PAGE");
            type = bundle.getString("TYPE");
            id = bundle.getInt("ID");
        }
        if (uri != null) {
            imgPic.setVisibility(View.INVISIBLE);
            prgWaiting.setVisibility(View.VISIBLE);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String Path = getRealPathFromURI(uri);

                    final Bitmap rotateImage = ImageHelper.correctRotateImage(Path, false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgPic.setImageBitmap(rotateImage);
                            imgPic.setVisibility(View.VISIBLE);
                            prgWaiting.setVisibility(View.GONE);
                        }
                    });

                }
            });
            thread.start();

        }
        RippleView rippleCrop = (RippleView) findViewById(R.id.pu_ripple_crop);
        txtCrop = (TextView) findViewById(R.id.pu_txt_crop);

        Log.i("VVBBBBBB", "onCreate: " + uri);
        if (uri != null && !uri.toString().equals("")) {
            rippleCrop.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

                @Override
                public void onComplete(RippleView rippleView) {
                    CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(120, 120)
                            .setAutoZoomEnabled(false)
                            .setInitialCropWindowPaddingRatio(.08f) // padding window from all
                            .setBorderCornerLength(50)
                            .setBorderCornerOffset(0)
                            .setAllowCounterRotation(true)
                            .setBorderCornerThickness(8.0f)
                            .setShowCropOverlay(true)
                            .setAspectRatio(1, 1)
                            .setFixAspectRatio(true)
                            .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                            .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                            .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                            .start(ActivityCrop.this);
                }
            });
        }


        RippleView rippleBack = (RippleView) findViewById(R.id.pu_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (type.equals("camera") || type.equals("crop_camera")) {
                    attachFile.requestTakePicture();

                } else if (type.equals("gallery")) {
                    attachFile.requestOpenGalleryForImageSingleSelect();
                }

            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null && type.equals("crop_camera") || type.equals("gallery")) {
                    pathImageUser = getRealPathFromURI(uri);
                    switch (page) {
                        case "NewGroup":
                            String timeStampGroup =
                                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                            new Date());
                            result = G.IMAGE_NEW_GROUP.toString() + " " + timeStampGroup;
                            HelperCopyFile.copyFile(pathImageUser, result);

                            break;
                        case "NewChanel":
                            String timeStampChannel =
                                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                            new Date());
                            result = G.IMAGE_NEW_CHANEL.toString() + " " + timeStampChannel;
                            HelperCopyFile.copyFile(pathImageUser, result);

                            break;
                        case "chat":
                            mediaStorageDir = new File(
                                    Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
                            String timeStamp =
                                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                            new Date());
                            fileChat = new File(mediaStorageDir.getPath()
                                    + File.separator
                                    + "IMG_"
                                    + timeStamp
                                    + ".jpg");
                            result = fileChat.toString();
                            HelperCopyFile.copyFile(pathImageUser, result);
                            break;
                        default:

                            result = G.imageFile.toString() + "_" + id + ".jpg";
                            HelperCopyFile.copyFile(pathImageUser, result);
                            break;
                    }
                } else {
                    result = getRealPathFromURI(uri);
                }
                if (page != null) {

                    Intent data = new Intent();
                    data.setData(Uri.parse(result));
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    //======================================================================================================// result from crop

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == AttachFile.request_code_TAKE_PICTURE) {
            String filePath = null;
            ImageHelper.correctRotateImage(AttachFile.imagePath, true);
            filePath = "file://" + AttachFile.imagePath;
            uri = Uri.parse(filePath);
            imgPic.setImageURI(uri);
            Log.i("DDD", "avatarId : " + filePath);
            Log.i("DDD", "exists : " + new File(filePath).exists());


        } else if (resultCode == Activity.RESULT_OK && requestCode == AttachFile.request_code_image_from_gallery_single_select) {
            String filePath = null;
            filePath = "file://" + AttachFile.getFilePathFromUri(data.getData());
            uri = Uri.parse(filePath);
            imgPic.setImageURI(uri);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                if (type.equals("camera")) {
                    type = "crop_camera";
                } else {
                    type = "gallery";
                }
                uri = result.getUri();
                imgPic.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            Toast.makeText(ActivityCrop.this, R.string.can_not_save_image, Toast.LENGTH_SHORT).show();
        }
    }

    //======================================================================================================//uri to string

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}
