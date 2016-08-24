package com.iGap.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityCrop extends AppCompatActivity {

    private ImageView imgPic, imgCrop, imgAgreeImage;
    private String stringUri;
    private Uri resultUri;
    private TextView txtCancell, txtSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        imgPic = (ImageView) findViewById(R.id.pu_img_imageBefore);
        imgCrop = (ImageView) findViewById(R.id.pu_img_crop);
        imgAgreeImage = (ImageView) findViewById(R.id.pu_img_agreeImage);

        txtCancell = (TextView) findViewById(R.id.pu_txt_cancel_crop);
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
                        .setBorderCornerLength(50)
                        .setBorderCornerOffset(0)
                        .setAllowCounterRotation(true)
                        .setInitialCropWindowPaddingRatio(0)
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

        txtCancell.setOnClickListener(new View.OnClickListener() {
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

                copyImageWithUri(resultUri, "image_user");
                Intent intent = new Intent(ActivityCrop.this, ActivityProfile.class);
                startActivity(intent);
                ActivityProfile.resultUri = resultUri;
                ActivityProfile.IsDeleteFile = true;
                finish();
            }
        });
    }

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

    //======================================================================================================// copy image user in spacial folder


    private void copyImageWithUri(Uri uri, String nikName) {
        String sourceFilename = uri.getPath();
        Log.i("TAGSS", "0: " + uri);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        Log.i("TAGSS", "0: " + G.imageFile);
        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));

            if (G.imageFile.exists()) {
                G.imageFile.delete();
                Log.i("TAGSS", "1: " + G.imageFile);
            }
            Log.i("TAGSS", "2: " + uri.getPath());

            bos = new BufferedOutputStream(new FileOutputStream(G.imageFile, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            Log.i("TAGSS", "error1: ");
            e.getStackTrace();

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
                Log.i("TAGSS", "ff: " + G.imageFile);

            } catch (IOException e) {
                Log.i("TAGSS", "error2: ");
                e.getStackTrace();
            }
        }
    }
}
