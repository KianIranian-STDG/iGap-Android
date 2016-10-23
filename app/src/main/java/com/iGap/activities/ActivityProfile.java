package com.iGap.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interface_package.OnFileUploadForActivities;
import com.iGap.interface_package.OnUserAvatarResponse;
import com.iGap.interface_package.OnUserProfileSetNickNameResponse;
import com.iGap.module.AndroidUtils;
import com.iGap.module.EditTextAdjustPan;
import com.iGap.module.FileUploadStructure;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmAvatarToken;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserProfileSetNickname;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityProfile extends ActivityEnhanced implements OnUserAvatarResponse, OnFileUploadForActivities {

    private TextView txtTitle, txtTitlInformation, txtDesc, txtAddPhoto;
    private Button btnLetsGo;
    private com.iGap.module.CircleImageView btnSetImage;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private EditTextAdjustPan edtNikName;
    private Uri uriIntent;
    private String pathImageUser;
    private int myResultCrop = 3;
    public static boolean IsDeleteFile;
    private File pathImageFromCamera = new File(G.imageFile.toString() + "_" + 0 + ".jpg");
    public final static String ARG_USER_ID = "arg_user_id";

    private int idAvatar;

    public static Bitmap decodeBitmapProfile = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        delete();
        txtTitlInformation = (TextView) findViewById(R.id.pu_txt_title_information);
        txtTitlInformation.setTypeface(G.arialBold);
        txtDesc = (TextView) findViewById(R.id.pu_txt_title_desc);
        txtDesc.setTypeface(G.arial);
        txtAddPhoto = (TextView) findViewById(R.id.pu_txt_addPhoto);
        txtAddPhoto.setTypeface(G.arial);

        G.uploaderUtil.setActivityCallbacks(this);
        G.onUserAvatarResponse = this;
        G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, ProtoResponse.Response response) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                        userInfo.setUserRegistrationState(true);
                    }
                });
                realm.close();

                Intent intent = new Intent(G.context, ActivityMain.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnLetsGo.setEnabled(true);
                        btnSetImage.setEnabled(true);
                        edtNikName.setEnabled(true);
                    }
                });

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

        edtNikName = (EditTextAdjustPan) findViewById(R.id.pu_edt_nikeName); // edit Text for NikName
        edtNikName.setTypeface(G.arial);
        btnLetsGo = (Button) findViewById(R.id.pu_btn_letsGo);
        btnLetsGo.setTypeface(G.arial);
        btnLetsGo.setOnClickListener(new View.OnClickListener() { // button for save data and go to next page
            @Override
            public void onClick(View view) {

                Realm realm = Realm.getDefaultInstance();
                final String nickName = edtNikName.getText().toString();

                if (!nickName.equals("")) {
                    btnLetsGo.setEnabled(false);
                    btnSetImage.setEnabled(false);
                    edtNikName.setEnabled(false);
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                            if (realmUserInfo != null) {
                                realmUserInfo.setNickName(nickName);

                                if (!realmUserInfo.getNickName().equalsIgnoreCase(nickName)) {
                                    new RequestUserProfileSetNickname().userProfileNickName(nickName);
                                } else {
                                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                                    userInfo.setUserRegistrationState(true);

                                    Intent intent = new Intent(G.context, ActivityMain.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                new RequestUserProfileSetNickname().userProfileNickName(nickName);
                            }
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
    }

    public void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pathImageUser = G.imageFile.toString() + "_" + 0 + ".jpg";
        pathImageFromCamera = new File(pathImageUser);
        uriIntent = Uri.fromFile(pathImageFromCamera);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);

        startActivityForResult(intent, myResultCodeCamera);

    }

    public void useGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, myResultCodeGallery);
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
                                useCamera();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(ActivityProfile.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            useGallery();
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
            intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
            startActivityForResult(intent, myResultCrop);

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "profile");
            intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
            startActivityForResult(intent, myResultCrop);
        } else if (requestCode == myResultCrop && resultCode == RESULT_OK) {

            Log.i("ZZZZ", "ActivityProfile crop: " + data.getData().toString());

            if (data != null) {
                pathImageUser = data.getData().toString();
            }

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    RealmAvatarPath realmAvatarPath = realm.createObject(RealmAvatarPath.class);
                    realmAvatarPath.setId(idAvatar + 1);
                    realmAvatarPath.setPathImage(pathImageUser);
                    realmUserInfo.getAvatarPath().add(realmAvatarPath);

                }
            });
            realm.close();

            lastUploadedAvatarId = idAvatar + 1;

            new UploadTask().execute(pathImageUser, lastUploadedAvatarId);
        }
    }

    private int lastUploadedAvatarId;

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setImage(uploadStructure.filePath);
            }
        });

        new RequestUserAvatarAdd().userAddAvatar(uploadStructure.token);
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {
        // TODO: 10/20/2016 [Alireza] update view something like updating progress
    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                int avatarId = (int) params[1];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, avatarId);
                fileUploadStructure.openFile(filePath);

                byte[] fileHash = AndroidUtils.getFileHash(fileUploadStructure);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);
            G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
        }
    }

    private void setImage(String path) {
        if (pathImageUser != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            btnSetImage.setPadding(0, 0, 0, 0);
            btnSetImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pathImageFromCamera.exists()) {
                pathImageFromCamera.delete();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void delete() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmAvatarPath> realmAvatarPaths = realm.where(RealmAvatarPath.class).findAll();
        if (realmAvatarPaths.size() > 0) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmAvatarPaths.deleteAllFromRealm();
                }
            });
        }
        realm.close();
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                RealmAvatarToken realmAvatarPath = realm.createObject(RealmAvatarToken.class);
                realmAvatarPath.setId(lastUploadedAvatarId);
                realmAvatarPath.setToken(avatar.getFile().getToken());
                realmUserInfo.addAvatarToken(realmAvatarPath);
            }
        });
        realm.close();
    }
}
