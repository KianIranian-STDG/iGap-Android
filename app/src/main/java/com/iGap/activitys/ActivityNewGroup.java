package com.iGap.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.ContactGroupFragment;
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
    private TextView txtNextStep, txtCancel;

    private EditText edtGroupName, edtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        //=======================back on toolbar
        txtBack = (MaterialDesignTextView) findViewById(R.id.stng_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ActivityNewGroup.this, ActivityMain.class));
                finish();
            }
        });

        //=======================set image for group
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

        //=======================name of group
        edtGroupName = (EditText) findViewById(R.id.stng_edt_newGroup);

        //=======================description group
        edtDescription = (EditText) findViewById(R.id.stng_edt_description);


        //=======================button next step
        txtNextStep = (TextView) findViewById(R.id.ng_txt_nextStep);
        txtNextStep.setTypeface(G.arial);
        txtNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (G.IMAGE_GROUP.exists()) {
                    if (edtDescription.getText().toString().length() > 0) {
                        if (edtGroupName.getText().toString().length() > 0) {
                            Fragment fragment = ContactGroupFragment.newInstance();
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.ng_fragmentContainer, fragment).commit();
                            ActivityMain.mLeftDrawerLayout.closeDrawer();
                        } else {
                            Toast.makeText(ActivityNewGroup.this, "Please Description tour Group", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityNewGroup.this, "Please Enter Your Name Group", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //=======================button cancel
        txtCancel = (TextView) findViewById(R.id.ng_txt_cancel);
        txtCancel.setTypeface(G.arial);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityNewGroup.this, ActivityMain.class));
                finish();
            }
        });

    }


    //=======================result for picture
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
