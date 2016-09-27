package com.iGap.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interface_package.OnUserProfileSetNickNameResponse;
import com.iGap.module.HelperDecodeFile;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserProfileSetNickname;

import io.realm.Realm;

public class ActivityProfile extends ActivityEnhanced {

    private TextView txtTitle, txtTitlInformation, txtDesc, txtAddPhoto;
    private Button btnLetsGo;
    private com.iGap.module.CircleImageView btnSetImage;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private EditText edtNikName;
    private Uri uriIntent;
    private String pathImageUser;
    public static boolean IsDeleteFile;


    public static Bitmap decodeBitmapProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!IsDeleteFile && G.imageFile.exists()) {
            G.imageFile.delete();
        }


        txtTitlInformation = (TextView) findViewById(R.id.pu_txt_title_information);
        txtTitlInformation.setTypeface(G.arialBold);
        txtDesc = (TextView) findViewById(R.id.pu_txt_title_desc);
        txtDesc.setTypeface(G.arial);
        txtAddPhoto = (TextView) findViewById(R.id.pu_txt_addPhoto);
        txtAddPhoto.setTypeface(G.arial);

        txtTitle = (TextView) findViewById(R.id.rg_txt_titleToolbar);

        txtTitle = (TextView) findViewById(R.id.pu_titleToolbar);
        txtTitle.setTypeface(G.FONT_IGAP);

        btnSetImage = (com.iGap.module.CircleImageView) findViewById(R.id.pu_profile_circle_image);
        btnSetImage.setOnClickListener(new View.OnClickListener() { // button for set image
            @Override
            public void onClick(View view) {
                startDialog(); // this dialog show 2 way for choose image : gallery and camera
            }
        });

        TextInputLayout txtInputNickName = (TextInputLayout) findViewById(R.id.pu_txtInput_nikeName);
//        txtInputNickName.setHint("Nickname");

        edtNikName = (EditText) findViewById(R.id.pu_edt_nikeName); // edit Text for NikName
        edtNikName.setTypeface(G.arial);
        btnLetsGo = (Button) findViewById(R.id.pu_btn_letsGo);
        btnLetsGo.setTypeface(G.arial);
        btnLetsGo.setOnClickListener(new View.OnClickListener() { // button for save data and go to next page
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();
                final String nickName = edtNikName.getText().toString();

                if (!nickName.equals("")) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

                            G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
                                @Override
                                public void onUserProfileNickNameResponse(final String nickName, ProtoResponse.Response response) {
                                    Intent intent = new Intent(G.context, ActivityMain.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onUserProfileNickNameError(int majorCode, int minorCode) {

                                    if (majorCode == 112 && minorCode == 1) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // TODO: 9/25/2016 Error 112 - USER_PROFILE_SET_NICKNAME_BAD_PAYLOAD
                                                //Invalid nickname
                                                Toast.makeText(ActivityProfile.this, "Invalid nickname. Please change nickname", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    } else if (majorCode == 113) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // TODO: 9/25/2016 Error 113 - USER_PROFILE_SET_NICKNAME_INTERNAL_SERVER_ERROR
                                            }
                                        });
                                    }
                                }
                            };
                            new RequestUserProfileSetNickname().userProfileNickName(nickName);

                            if (G.imageFile.exists()) {
                                realmUserInfo.setAvatarPath(G.imageFile.toString());
                            }
                            realmUserInfo.setNickName(nickName);
                        }
                    });
                    realm.close();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(G.context, "Please Write Your NickName", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        if (G.imageFile.exists()) {
            decodeBitmapProfile = HelperDecodeFile.decodeFile(G.imageFile);
            btnSetImage.setImageBitmap(decodeBitmapProfile);
            btnSetImage.setPadding(0, 0, 0, 0);
            btnSetImage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    //======================================================================================================dialog for choose image
    private void startDialog() {

        new MaterialDialog.Builder(this)
                .title("Choose Picture")
                .negativeText("CANCEL")
                .items(R.array.profile)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text.toString().equals("From Camera")) {

                            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                uriIntent = Uri.fromFile(G.imageFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                startActivityForResult(intent, myResultCodeCamera);
                                dialog.dismiss();

                            } else {
                                Toast.makeText(ActivityProfile.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
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

    //======================================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", "profile");
            startActivity(intent);
            finish();

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "profile");
            startActivity(intent);
            finish();
        }
    }


}
