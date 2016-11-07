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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.HelperCopyFile;
import com.iGap.module.HelperDecodeFile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityCrop extends ActivityEnhanced {

    public final String IMAGE_DIRECTORY_NAME = "Upload";
    private ImageView imgPic;
    private Uri uri;
    private TextView txtCancel, txtSet, txtCrop, txtAgreeImage;
    private int idAvatar;
    private String pathSaveImage;
    private String page;
    private String type;
    private int id;
    private String pathImageUser;
    private File mediaStorageDir;
    private File fileChat;
    private String result;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

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

            imgPic.setImageURI(uri);
        }

        txtCrop = (TextView) findViewById(R.id.pu_txt_crop);

        RippleView rippleCrop = (RippleView) findViewById(R.id.pu_ripple_crop);
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

        RippleView rippleBack = (RippleView) findViewById(R.id.pu_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                finish();
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
                if (uri != null && type.equals("crop") || type.equals("gallery")) {
                    pathImageUser = getRealPathFromURI(uri);
                    switch (page) {
                        case "NewGroup":
                            result = G.IMAGE_NEW_GROUP.toString();
                            HelperCopyFile.copyFile(pathImageUser, result);

                            break;
                        case "NewChanel":

                            result = G.IMAGE_NEW_CHANEL.toString();
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

                    resizeImage(result);
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                type = "crop";
                uri = result.getUri();
                imgPic.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            Toast.makeText(ActivityCrop.this, "can't save Image", Toast.LENGTH_SHORT).show();
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

    private void resizeImage(String pathSaveImage) {
        Bitmap b = HelperDecodeFile.decodeFile(new File(pathSaveImage));
        try {
            FileOutputStream out = new FileOutputStream(pathSaveImage);

            if (b != null) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else {
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
