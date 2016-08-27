package com.iGap.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.HelperCopyFile;
import com.iGap.module.HelperDecodeFile;
import com.iGap.realm.RealmUserInfo;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class ActivityProfile extends ActivityEnhanced {

    private TextView txtTitle;
    private Button btnSetImage, btnLetsGo;
    private CircleImageView imgSetImage;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private EditText edtNikName;
    private Uri uriIntent;
    private String pathImageUser;
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
                startDialog(); // this dialog show 2 way for choose image : gallery and camera
            }
        });

        imgSetImage = (CircleImageView) findViewById(R.id.pu_profile_circle_image);
        if (imgSetImage != null) { // for choose another image

            imgSetImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startDialog();
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
                            RealmUserInfo realmUserInfo = G.realm.where(RealmUserInfo.class).findFirst();
                            realmUserInfo.setNickName(nickName);
                            if (pathImageUser != null) {
                                HelperCopyFile.copyFile(pathImageUser, G.imageFile.toString());
                                realmUserInfo.setAvatarPath(G.imageFile.toString());
                            }
                        }
                    });
                    Intent intent = new Intent(G.context, ActivityMain.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        if (G.saveImageUserProfile != null) {
            pathImageUser = getRealPathFromURI(G.saveImageUserProfile);
            File ts2 = new File(pathImageUser);
            G.decodeBitmapProfile = HelperDecodeFile.decodeFile(ts2);
            imgSetImage.setVisibility(View.VISIBLE);
            imgSetImage.setImageBitmap(G.decodeBitmapProfile);
            btnSetImage.setVisibility(View.GONE);

        }
    }

    //======================================================================================================dialog for choose image
    private void startDialog() {

        final Dialog dialog = new Dialog(ActivityProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_profile);
        TextView picCamera = (TextView) dialog.findViewById(R.id.pu_layout_dialog_picCamera);
        picCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriIntent = Uri.fromFile(G.imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                    startActivityForResult(intent, myResultCodeCamera);
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
                G.saveImageUserProfile = Uri.fromFile(G.imageFile);
                startActivityForResult(intent, myResultCodeGallery);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //======================================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            startActivity(intent);

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            G.saveImageUserProfile = data.getData();
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            startActivity(intent);
        }
    }
    //======================================================================================================// save data for orientate

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (G.saveImageUserProfile != null) {
            pathImageUser = getRealPathFromURI(G.saveImageUserProfile);
            File ts2 = new File(pathImageUser);
            G.decodeBitmapProfile = HelperDecodeFile.decodeFile(ts2);
            imgSetImage.setVisibility(View.VISIBLE);
            imgSetImage.setImageBitmap(G.decodeBitmapProfile);
            btnSetImage.setVisibility(View.GONE);
        }
    }

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
