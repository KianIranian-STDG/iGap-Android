package com.iGap.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.HelperCopyFile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ActivityCrop extends ActivityEnhanced {

    private ImageView imgPic;
    private Uri resultUri;
    private TextView txtCancel, txtSet, txtCrop, txtAgreeImage;

    private String page;
    private String type;
    private String pathImageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        imgPic = (ImageView) findViewById(R.id.pu_img_imageBefore);

        txtAgreeImage = (TextView) findViewById(R.id.pu_txt_agreeImage);

        txtCancel = (TextView) findViewById(R.id.pu_txt_cancel_crop);
        txtSet = (TextView) findViewById(R.id.pu_txt_set_crop);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            resultUri = Uri.parse(bundle.getString("IMAGE_CAMERA"));
            page = bundle.getString("PAGE");
            type = bundle.getString("TYPE");
        }
        if (resultUri != null) {

            imgPic.setImageURI(resultUri);
        }

        txtCrop = (TextView) findViewById(R.id.pu_txt_crop);
        txtCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(resultUri).setGuidelines(CropImageView.Guidelines.ON)
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


        txtAgreeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (page != null) {

                    if (page.equals("profile")) {

                        Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                        startActivity(intent);
                        finish();

                    } else if (page.equals("setting")) {

                        Intent intent = new Intent(ActivityCrop.this, ActivitySetting.class);
                        startActivity(intent);
                        finish();
                    } else if (page.equals("NEW_GROUP")) {
                        Intent intent = new Intent(ActivityCrop.this, ActivityNewGroup.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (page != null) {

                    if (page.equals("profile")) {

                        Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                        startActivity(intent);
                        finish();

                    } else if (page.equals("setting")) {

                        Intent intent = new Intent(ActivityCrop.this, ActivitySetting.class);
                        startActivity(intent);
                        finish();

                    } else if (page.equals("NEW_GROUP")) {
                        Intent intent = new Intent(ActivityCrop.this, ActivityNewGroup.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (resultUri != null && type.equals("crop") || type.equals("gallery")) {
                    pathImageUser = getRealPathFromURI(resultUri);
                    if (page.equals("NEW_GROUP")) {
                        if (G.IMAGE_GROUP.exists()) G.imageFile.delete();// if file exists delete
                        HelperCopyFile.copyFile(pathImageUser, G.IMAGE_GROUP.toString());
                    } else {
                        if (G.imageFile.exists()) G.imageFile.delete();// if file exists delete
                        HelperCopyFile.copyFile(pathImageUser, G.imageFile.toString());
                    }

                }
                if (page != null) {
                    if (page.equals("profile")) {

                        Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                        ActivityProfile.IsDeleteFile = true;
                        startActivity(intent);
                        finish();

                    } else if (page.equals("setting")) {
                        Intent intent = new Intent(ActivityCrop.this, ActivitySetting.class);

                        startActivity(intent);
                        finish();
                    } else if (page.equals("NEW_GROUP")) {
                        Intent intent = new Intent(ActivityCrop.this, ActivityNewGroup.class);
                        startActivity(intent);
                        finish();
                    }
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
                resultUri = result.getUri();
                imgPic.setImageURI(resultUri);

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


}
