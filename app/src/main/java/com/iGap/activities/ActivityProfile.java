package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnUserAvatarResponse;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnUserProfileSetNickNameResponse;
import com.iGap.module.AndroidUtils;
import com.iGap.module.EditTextAdjustPan;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserProfileSetNickname;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityProfile extends ActivityEnhanced
        implements OnUserAvatarResponse, OnFileUploadForActivities {

    public final static String ARG_USER_ID = "arg_user_id";
    public static boolean IsDeleteFile;
    public static Bitmap decodeBitmapProfile = null;
    private TextView txtTitle, txtTitlInformation, txtDesc, txtAddPhoto;
    private Button btnLetsGo;
    private com.iGap.module.CircleImageView btnSetImage;
    private EditTextAdjustPan edtNikName;
    private Uri uriIntent;
    private String pathImageUser;
    private File pathImageFromCamera = new File(G.imageFile.toString() + "_" + 0 + ".jpg");
    private int idAvatar;
    private int lastUploadedAvatarId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        delete();
        txtTitlInformation = (TextView) findViewById(R.id.pu_txt_title_information);

        txtDesc = (TextView) findViewById(R.id.pu_txt_title_desc);

        txtAddPhoto = (TextView) findViewById(R.id.pu_txt_addPhoto);

        G.uploaderUtil.setActivityCallbacks(this);
        G.onUserAvatarResponse = this;

        txtTitle = (TextView) findViewById(R.id.rg_txt_titleToolbar);

        txtTitle = (TextView) findViewById(R.id.pu_titleToolbar);
        txtTitle.setTypeface(G.FONT_IGAP);

        final View lineEditText = findViewById(R.id.pu_line_below_editText);
        btnSetImage = (com.iGap.module.CircleImageView) findViewById(R.id.pu_profile_circle_image);
        btnSetImage.setOnClickListener(new View.OnClickListener() { // button for set image
            @Override
            public void onClick(View view) {
                startDialog(); // this dialog show 2 way for choose image : gallery and camera
            }
        });

        final TextInputLayout txtInputNickName =
                (TextInputLayout) findViewById(R.id.pu_txtInput_nikeName);
        //        txtInputNickName.setHint("Nickname");

        edtNikName = (EditTextAdjustPan) findViewById(R.id.pu_edt_nikeName); // edit Text for NikName

        btnLetsGo = (Button) findViewById(R.id.pu_btn_letsGo);

        edtNikName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtInputNickName.setErrorEnabled(true);
                txtInputNickName.setError("");
                txtInputNickName.setHintTextAppearance(R.style.remove_error_appearance);
                edtNikName.setTextColor(getResources().getColor(R.color.border_editText));
                lineEditText.setBackgroundColor(getResources().getColor(android.R.color.black));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnLetsGo.setOnClickListener(
                new View.OnClickListener() { // button for save data and go to next page
                    @Override
                    public void onClick(View view) {

                        Realm realm = Realm.getDefaultInstance();
                        final String nickName = edtNikName.getText().toString();

                        if (!nickName.equals("")) {

                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    setNickName();
                                }
                            });
                            realm.close();
                        } else {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void run() {

                                    txtInputNickName.setErrorEnabled(true);
                                    txtInputNickName.setError(
                                            getResources().getString(R.string.Toast_Write_NickName));
                                    txtInputNickName.setHintTextAppearance(R.style.error_appearance);
                                    edtNikName.setTextColor(getResources().getColor(R.color.red));
                                    lineEditText.setBackgroundColor(
                                            getResources().getColor(R.color.red));
                                }
                            });
                        }
                    }
                });
    }

    private void setNickName() {

        G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName,
                                                      ProtoResponse.Response response) {
                getUserInfo();
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

                if (majorCode == 112 && minorCode == 1) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 112 - USER_PROFILE_SET_NICKNAME_BAD_PAYLOAD
                            //Invalid nickname
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                    getResources().getString(R.string.Toast_Invalid_nickname),
                                    Snackbar.LENGTH_LONG);
                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();

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

        new RequestUserProfileSetNickname().userProfileNickName(edtNikName.getText().toString());
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                                realmUserInfo.getUserInfo().setDisplayName(user.getDisplayName());
                                realmUserInfo.getUserInfo().setInitials(user.getInitials());
                                realmUserInfo.getUserInfo().setColor(user.getColor());
                                realmUserInfo.setUserRegistrationState(true);

                                final long userId = realmUserInfo.getUserId();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(G.context, ActivityMain.class);
                                        intent.putExtra(ActivityProfile.ARG_USER_ID, userId);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });
                        realm.close();
                    }
                });

            }

            @Override
            public void onUserInfoTimeOut() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
            }
        };

        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        new RequestUserInfo().userInfo(realmUserInfo.getUserId());
        realm.close();
    }

    public void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pathImageUser = G.imageFile.toString() + "_" + 0 + ".jpg";
        pathImageFromCamera = new File(pathImageUser);
        uriIntent = Uri.fromFile(pathImageFromCamera);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);

        startActivityForResult(intent, IntentRequests.REQ_CAMERA);

    }

    //======================================================================================================dialog for choose image

    public void useGallery() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IntentRequests.REQ_GALLERY);
    }

    private void startDialog() {

        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.choose_picture))
                .negativeText(getResources().getString(R.string.B_cancel))
                .items(R.array.profile)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which,
                                            CharSequence text) {

                        switch (which) {
                            case 0: {
                                useGallery();
                                dialog.dismiss();
                                break;

                            }
                            case 1: {
                                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                    useCamera();
                                    dialog.dismiss();
                                } else {
                                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                            getResources().getString(R.string.please_check_your_camera),
                                            Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snack.dismiss();
                                        }
                                    });
                                    snack.show();
                                }
                                break;
                            }
                        }
                    }
                })
                .show();
    }

    //======================================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentRequests.REQ_CAMERA
                && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", "profile");
            intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
            startActivityForResult(intent, IntentRequests.REQ_CROP);
        } else if (requestCode == IntentRequests.REQ_GALLERY
                && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivityProfile.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "profile");
            intent.putExtra("ID", (int) getIntent().getLongExtra(ARG_USER_ID, -1));
            startActivityForResult(intent, IntentRequests.REQ_CROP);
        } else if (requestCode == IntentRequests.REQ_CROP && resultCode == RESULT_OK) {

            Log.i("ZZZZ", "ActivityProfile crop: " + data.getData().toString());

            if (data != null) {
                pathImageUser = data.getData().toString();
            }

            lastUploadedAvatarId = idAvatar + 1;

            new UploadTask().execute(pathImageUser, lastUploadedAvatarId);
        }
    }

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
    public void onFileUploading(FileUploadStructure uploadStructure, String identity,
                                double progress) {
        // TODO: 10/20/2016 [Alireza] update view something like updating progress
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
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final RealmResults<RealmAvatar> realmAvatars =
                realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findAll();
        if (!realmAvatars.isEmpty()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmAvatars.deleteAllFromRealm();
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
                long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, lastUploadedAvatarId).findFirst();
                realmAvatar.setOwnerId(userId);
                realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR));
                realmAvatar.setId(avatar.getId());
            }
        });
        realm.close();
    }

    private static class UploadTask
            extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                int avatarId = (int) params[1];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure =
                        new FileUploadStructure(fileName, fileSize, filePath, avatarId);
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
}
