package com.iGap.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.HelperDecodeFile;
import com.iGap.module.MaterialDesignTextView;

public class ActivityNewGroup extends ActivityEnhanced {

    private MaterialDesignTextView txtBack;
    private CircleImageView imgCircleImageView;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private Uri uriIntent;
    public static Bitmap decodeBitmapProfile = null;

    private EditText edtGroupName, edtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        txtBack = (MaterialDesignTextView) findViewById(R.id.stng_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imgCircleImageView = (CircleImageView) findViewById(R.id.stng_profile_circle_image);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivityNewGroup.this)
                        .title("Choose Picture")
                        .negativeText("CANCEL")
                        .items(R.array.profile)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (text.toString().equals("From Camera")) {

                                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {


                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        uriIntent = Uri.fromFile(G.IMAGE_GROUP);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                        startActivityForResult(intent, myResultCodeCamera);
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(ActivityNewGroup.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, myResultCodeGallery);
                                    dialog.dismiss();
                                }
                            }
                        })
                        .show();
            }
        });

        if (G.IMAGE_GROUP.exists()) {
            decodeBitmapProfile = HelperDecodeFile.decodeFile(G.IMAGE_GROUP);
            imgCircleImageView.setImageBitmap(decodeBitmapProfile);
            imgCircleImageView.setPadding(0, 0, 0, 0);
            imgCircleImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }


        edtGroupName = (EditText) findViewById(R.id.stng_edt_newGroup);
        edtDescription = (EditText) findViewById(R.id.stng_edt_description);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivityNewGroup.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", "NEW_GROUP");
            startActivity(intent);
            finish();

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivityNewGroup.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "NEW_GROUP");
            startActivity(intent);
            finish();
        }
    }
}
