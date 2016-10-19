package com.iGap.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.iGap.interface_package.OnFileUpload;
import com.iGap.interface_package.OnFileUploadStatusResponse;
import com.iGap.interface_package.OnUserAvatarResponse;
import com.iGap.interface_package.OnUserProfileSetNickNameResponse;
import com.iGap.module.EditTextAdjustPan;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmAvatarToken;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestFileUpload;
import com.iGap.request.RequestFileUploadInit;
import com.iGap.request.RequestFileUploadStatus;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserProfileSetNickname;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityProfile extends ActivityEnhanced implements OnFileUpload, OnFileUploadStatusResponse, OnUserAvatarResponse {

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
        G.onFileUploadStatusResponse = this;
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
                                pathImageUser = G.imageFile.toString() + "_" + 0 + ".jpg";
                                pathImageFromCamera = new File(pathImageUser);
                                uriIntent = Uri.fromFile(pathImageFromCamera);
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

                byte[] fileHash = Utils.getFileHash(fileUploadStructure);
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
            mSelectedFiles.add(result);
            G.uploaderUtil.startUploading(result.fileSize, Long.toString(result.messageId));
        }
    }

    // selected files (paths)
    private static CopyOnWriteArrayList<FileUploadStructure> mSelectedFiles = new CopyOnWriteArrayList<>();

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

    /**
     * get file with hash string
     *
     * @param identity file hash
     * @return FileUploadStructure
     */
    @Nullable
    private FileUploadStructure getSelectedFile(String identity) {
        for (FileUploadStructure structure : mSelectedFiles) {
            if (structure.messageId == Long.parseLong(identity)) {
                return structure;
            }
        }
        return null;
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response) {
        try {
            FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
            // getting bytes from file as server said
            byte[] bytesFromFirst = Utils.getBytesFromStart(fileUploadStructure, firstBytesLimit);
            byte[] bytesFromLast = Utils.getBytesFromEnd(fileUploadStructure, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, fileUploadStructure.fileSize, fileUploadStructure.fileHash, Long.toString(fileUploadStructure.messageId), fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * does file exist in the list
     * useful for preventing from calling onFileUploadComplete() multiple for a file
     *
     * @param messageId file hash
     * @return boolean
     */
    private boolean isFileExistInList(long messageId) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.messageId == messageId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void OnFileUploadInit(String token, double progress, long offset, int limit, String fileHashAsIdentity, ProtoResponse.Response response) {
        // token needed for requesting upload
        // updating structure with new token
        FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
        fileUploadStructure.token = token;

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, fileHashAsIdentity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // TODO: 10/5/2016 [Alireza] inja mitooni view ro update koni, masalan progress

                if (isFileExistInList(Long.parseLong(fileHashAsIdentity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(fileHashAsIdentity, response);
                }
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
            }
        }
    }

    @Override
    public void onFileUpload(double progress, long nextOffset, int nextLimit, String identity, ProtoResponse.Response response) {
        final long startOnFileUploadTime = System.currentTimeMillis();

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        try {
            // update progress
            Log.i("SOC", "************************************ identity : " + identity + "  ||  progress : " + progress);
            Log.i("BreakPoint", identity + " > bad az update progress");

            if (progress != 100.0) {
                // TODO: 10/5/2016 [Alireza] inja mitooni view ro update koni, masalan progress

                Log.i("BreakPoint", identity + " > 100 nist");
                FileUploadStructure fileUploadStructure = getSelectedFile(identity);
                Log.i("BreakPoint", identity + " > fileUploadStructure");
                final long startGetNBytesTime = System.currentTimeMillis();
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) nextOffset, nextLimit);

                fileUploadStructure.getNBytesTime += System.currentTimeMillis() - startGetNBytesTime;

                Log.i("BreakPoint", identity + " > after bytes");
                // make request till uploading has finished
                final long startSendReqTime = System.currentTimeMillis();

                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes, identity);
                fileUploadStructure.sendRequestsTime += System.currentTimeMillis() - startSendReqTime;
                Log.i("BreakPoint", identity + " > after fileUpload request");

                fileUploadStructure.elapsedInOnFileUpload += System.currentTimeMillis() - startOnFileUploadTime;
            } else {
                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            }
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadComplete(String identity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(identity);

        new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, identity);
    }

    @Override
    public void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status, double progress, int recheckDelayMS, final String identity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        if (fileUploadStructure == null) {
            return;
        }
        if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSED && progress == 100D) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setImage(fileUploadStructure.filePath);
                }
            });

            new RequestUserAvatarAdd().userAddAvatar(fileUploadStructure.token);
            // TODO: 10/5/2016 [Alireza] inja mitooni view ro update koni, masalan progress

            // remove from selected files to prevent calling this method multiple times
            // multiple calling may occurs because of the server
            try {
                // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] uncomment plz
                //removeFromSelectedFiles(identity);
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
                e.printStackTrace();
            }

            // close file into structure
            try {
                if (fileUploadStructure != null) {
                    fileUploadStructure.closeFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSING || (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.UPLOADING) && progress == 100D) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, identity);
                }
            }, recheckDelayMS);
        } else {
            G.uploaderUtil.startUploading(fileUploadStructure.fileSize, Long.toString(fileUploadStructure.messageId));
        }
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
