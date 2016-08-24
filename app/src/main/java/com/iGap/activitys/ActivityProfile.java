package com.iGap.activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.realm.RealmUserInfo;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class ActivityProfile extends AppCompatActivity {

    private TextView txtTitle;
    private Button btnSetImage, btnLetsGo;
    private CircleImageView imgSetImage;

    private int myResultCodeCamera = 1;
    private Bitmap myBitmapCamera;
    private String myfinalImageCamera;

    private int myResultCodeGallery = 0;
    private String myfinalImageGallery;

    public static Uri resultUri = null;

    private EditText edtNikName;

    public static boolean IsDeleteFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//
        if (!IsDeleteFile && G.imageFile.exists()) {
            G.imageFile.delete();
        }

        txtTitle = (TextView) findViewById(R.id.pu_titleToolbar);
        txtTitle.setTypeface(G.FONT_IGAP);

        btnSetImage = (Button) findViewById(R.id.pu_btn_giveImage);
        btnSetImage.setOnClickListener(new View.OnClickListener() { // button for set image
            @Override
            public void onClick(View view) {
                startDalog(); // this dialog show 2 way for choose image : gallery and camera

            }
        });

        imgSetImage = (CircleImageView) findViewById(R.id.pu_profile_circle_image);
        if (imgSetImage != null) { // for choose another image

            imgSetImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startDalog();
                }
            });
        } else {

            btnSetImage.setVisibility(View.VISIBLE);
        }

        edtNikName = (EditText) findViewById(R.id.pu_edt_nikeName); // edit Text for NikName

        btnLetsGo = (Button) findViewById(R.id.pu_btn_letsGo);
        btnLetsGo.setOnClickListener(new View.OnClickListener() { // button for save data and go to next page
            @Override
            public void onClick(View view) {

                final String nickName = edtNikName.getText().toString();

                if (!nickName.equals("")) {
                    G.realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmUserInfo userInfo = realm.createObject(RealmUserInfo.class);
                            userInfo.setNickName(nickName);
                        }
                    });
                    Intent intent = new Intent(G.context, ActivityMain.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        if (G.imageFile.exists()) {

            Uri u = Uri.fromFile(G.imageFile);
            imgSetImage.setVisibility(View.VISIBLE);
            imgSetImage.setImageURI(u);
            btnSetImage.setVisibility(View.GONE);
        } else if (resultUri != null) {

            imgSetImage.setVisibility(View.VISIBLE);
            imgSetImage.setImageURI(resultUri);
            btnSetImage.setVisibility(View.GONE);

        }


    }

    //======================================================================================================dialog for choose image
    private void startDalog() {

        final Dialog dialog = new Dialog(ActivityProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog2);
        TextView picCamera = (TextView) dialog.findViewById(R.id.pu_layout_dialog_picCamera);
        picCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                    dialog.dismiss();

                } else {
                    Toast.makeText(ActivityProfile.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView picGallery = (TextView) dialog.findViewById(R.id.pu_layout_dialog_picGallery);
        picGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //======================================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Bundle bundle = data.getExtras();
            myBitmapCamera = (Bitmap) bundle.get("data");

            Uri uriImag = getImageUri(G.context, myBitmapCamera);

            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriImag.toString());
            startActivity(intent);

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery

            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            startActivity(intent);
        }
    }
    //======================================================================================================// save data for orientate

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Uri uri = Uri.fromFile(G.imageFile);

        if (G.imageFile.exists()) {
            btnSetImage.setVisibility(View.GONE);
            imgSetImage.setVisibility(View.VISIBLE);
            imgSetImage.setImageURI(uri);

        } else {
            btnSetImage.setVisibility(View.VISIBLE);
            imgSetImage.setVisibility(View.GONE);
        }
    }
}
