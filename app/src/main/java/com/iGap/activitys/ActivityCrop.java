package com.iGap.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ActivityCrop extends ActivityEnhanced {

    private ImageView imgPic, imgCrop, imgAgreeImage;
    private Uri resultUri;
    private TextView txtCancel, txtSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        imgPic = (ImageView) findViewById(R.id.pu_img_imageBefore);
        imgCrop = (ImageView) findViewById(R.id.pu_img_crop);
        imgAgreeImage = (ImageView) findViewById(R.id.pu_img_agreeImage);

        txtCancel = (TextView) findViewById(R.id.pu_txt_cancel_crop);
        txtSet = (TextView) findViewById(R.id.pu_txt_set_crop);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            resultUri = Uri.parse(bundle.getString("IMAGE_CAMERA"));
        }
        if (resultUri != null) {

            imgPic.setImageURI(resultUri);
        }
        imgCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity(resultUri).setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(120, 120)
                        .setAutoZoomEnabled(false)
                        .setInitialCropWindowPaddingRatio(.08f)
//                        .setMaxCropResultSize(2500,2500)
                        .setBorderCornerLength(50)
                        .setBorderCornerOffset(0)
                        .setAllowCounterRotation(true)
                        .setBorderCornerThickness(10.0f)
                        .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                        .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                        .start(ActivityCrop.this);
            }
        });
        imgAgreeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                startActivity(intent);
                finish();
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                startActivity(intent);
                finish();
            }
        });
        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                G.saveImageUserProfile = resultUri;
//                HelperCopyFile.copyFile(G.saveImageUserProfile.toString()+".jpg",G.IMAGE_USER);
                Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                startActivity(intent);
                ActivityProfile.IsDeleteFile = true;
                finish();
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

                resultUri = result.getUri();
                imgPic.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            Toast.makeText(ActivityCrop.this, "can't save Image", Toast.LENGTH_SHORT).show();
        }
    }
}
