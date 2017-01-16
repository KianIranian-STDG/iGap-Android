package com.iGap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.fragments.FragmentDeleteAccount;
import com.iGap.fragments.FragmentDrawerMenu;
import com.iGap.fragments.FragmentPrivacyAndSecurity;
import com.iGap.fragments.FragmentShowAvatars;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperLogout;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperString;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnAvatarDelete;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnUserAvatarResponse;
import com.iGap.interfaces.OnUserProfileCheckUsername;
import com.iGap.interfaces.OnUserProfileGetEmail;
import com.iGap.interfaces.OnUserProfileGetGender;
import com.iGap.interfaces.OnUserProfileSetEmailResponse;
import com.iGap.interfaces.OnUserProfileSetGenderResponse;
import com.iGap.interfaces.OnUserProfileSetNickNameResponse;
import com.iGap.interfaces.OnUserProfileUpdateUsername;
import com.iGap.interfaces.OnUserSessionLogout;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.IncomingSms;
import com.iGap.module.OnComplete;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.SUID;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.proto.ProtoUserProfileCheckUsername;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserProfileCheckUsername;
import com.iGap.request.RequestUserProfileGetEmail;
import com.iGap.request.RequestUserProfileGetGender;
import com.iGap.request.RequestUserProfileSetEmail;
import com.iGap.request.RequestUserProfileSetGender;
import com.iGap.request.RequestUserProfileSetNickname;
import com.iGap.request.RequestUserProfileUpdateUsername;
import com.iGap.request.RequestUserSessionLogout;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Locale;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;
import static com.iGap.R.string.log_out;

public class ActivitySetting extends ActivityEnhanced implements OnUserAvatarResponse, OnFileUploadForActivities, OnFileDownloadResponse {

    public static String pathSaveImage;
    public static int KEY_AD_DATA_PHOTO = -1;
    public static int KEY_AD_DATA_VOICE_MESSAGE = -1;
    public static int KEY_AD_DATA_VIDEO = -1;
    public static int KEY_AD_DATA_FILE = -1;
    public static int KEY_AD_DATA_MUSIC = -1;
    public static int KEY_AD_DATA_GIF = -1;
    public static int KEY_AD_WIFI_PHOTO = -1;
    public static int KEY_AD_WIFI_VOICE_MESSAGE = -1;
    public static int KEY_AD_WIFI_VIDEO = -1;
    public static int KEY_AD_WIFI_FILE = -1;
    public static int KEY_AD_WIFI_MUSIC = -1;
    public static int KEY_AD_WIFI_GIF = -1;
    public static int KEY_AD_ROAMING_PHOTO = -1;
    public static int KEY_AD_ROAMING_VOICE_MESSAGE = -1;
    public static int KEY_AD_ROAMING_VIDEO = -1;
    public static int KEY_AD_ROAMING_FILE = -1;
    public static int KEY_AD_ROAMING_MUSIC = -1;
    public static int KEY_AD_ROAMINGN_GIF = -1;
    private SharedPreferences sharedPreferences;
    private TextView txtMenu, txtMessageTextSize, txtAutoDownloadData, txtAutoDownloadWifi, txtChatBackground, txtAutoDownloadRoaming, txtKeepMedia, txtLanguage, txtSizeClearCach;
    private RelativeLayout ltClearCache;
    private PopupWindow popupWindow;
    private int poRbDialogLangouage = -1;
    private String textLanguage = "English";
    private int poRbDialogTextSize = -1;
    private ViewGroup ltMessageTextSize, ltLanguage;
    private TextView txtNickName, txtUserName, txtPhoneNumber, txtNotifyAndSound, txtFaq, txtPrivacyPolicy, txtSticker, ltInAppBrowser, ltSentByEnter, ltEnableAnimation, ltAutoGifs, ltSaveToGallery;
    private ToggleButton toggleSentByEnter, toggleEnableAnimation, toggleAutoGifs, toggleSaveToGallery, toggleInAppBrowser, toggleCrop;
    private AppBarLayout appBarLayout;
    private Uri uriIntent;
    private long idAvatar;
    private File nameImageFile;
    private FloatingActionButton fab;
    private CircleImageView circleImageView;
    private String nickName;
    private String userName;
    private String phoneName;
    private ProtoGlobal.Gender userGender;
    private String userEmail;
    private long userId;
    private long lastUploadedAvatarId;
    private IncomingSms smsReceiver;
    private String regex;
    public ProgressBar prgWait;
    private static String identityCurrent;
    private TextView txtGander;
    private TextView txtEmail;
    private AttachFile attachFile;

    public static long getFolderSize(File dir) throws RuntimeException {
        long size = 0;
        if (dir == null) return size;

        if (dir.listFiles() != null) {

            for (File file : dir.listFiles()) {
                if (file != null) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getFolderSize(file);
                    }
                } else {
                    return size;
                }
            }
        }
        return size;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.0");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }
        return hrSize;
    }

    /*private void setImage() {
        final Realm realm = Realm.getDefaultInstance();

        RealmRegisteredInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst().getUserInfo();
        if (realmUserInfo != null && realmUserInfo.getLastAvatar() != null) {
            RealmAvatar realmAvatar = realmUserInfo.getLastAvatar();
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), circleImageView);
                if (G.onChangeUserPhotoListener != null) {
                    G.onChangeUserPhotoListener.onChangePhoto(realmAvatar.getFile().getLocalFilePath());
                }
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), circleImageView);
                if (G.onChangeUserPhotoListener != null) {
                    G.onChangeUserPhotoListener.onChangePhoto(realmAvatar.getFile().getLocalThumbnailPath());
                }
            } else {
                showInitials();
                requestDownloadAvatar(false, realmAvatar.getFile().getToken(), realmAvatar.getFile().getName(), (int) realmAvatar.getFile().getSmallThumbnail().getSize());
            }
        } else {
            showInitials();
        }
    }*/

    private void setImage(String path) {
        if (path != null) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(path), circleImageView);
            if (G.onChangeUserPhotoListener != null) {
                G.onChangeUserPhotoListener.onChangePhoto(path);
            }
        }
    }

    private void requestDownloadAvatar(boolean done, final String token, String name, int smallSize) {
        final String fileName = "thumb_" + token + "_" + name;
        if (done) {
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, token).findFirst().getFile().setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    String filePath = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, token).findFirst().getFile().getLocalThumbnailPath();
                    if (G.onChangeUserPhotoListener != null) {
                        G.onChangeUserPhotoListener.onChangePhoto(filePath);
                    }
                    realm.close();
                }
            });

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity = token + '*' + selector.toString() + '*' + smallSize + '*' + fileName + '*' + 0;

        new RequestFileDownload().download(token, 0, smallSize, selector, identity);
    }

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
        realm.close();

        if (G.onChangeUserPhotoListener != null) {
            G.onChangeUserPhotoListener.onChangePhoto(null);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        G.uploaderUtil.setActivityCallbacks(this);
        G.onUserAvatarResponse = this;
        G.onFileDownloadResponse = this;

        final Realm realm = Realm.getDefaultInstance();
        final TextView txtNickNameTitle = (TextView) findViewById(R.id.ac_txt_nickname_title);

        txtNickName = (TextView) findViewById(R.id.st_txt_nikName);
        txtUserName = (TextView) findViewById(R.id.st_txt_userName);
        txtPhoneNumber = (TextView) findViewById(R.id.st_txt_phoneNumber);
        txtGander = (TextView) findViewById(R.id.st_txt_gander);
        txtEmail = (TextView) findViewById(R.id.st_txt_email);
        prgWait = (ProgressBar) findViewById(R.id.st_prgWaiting_addContact);
        prgWait.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);


        G.onUserProfileGetGender = new OnUserProfileGetGender() {
            @Override
            public void onUserProfileGetGender(final ProtoGlobal.Gender gender) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                        realmUserInfo.setGender(gender);
                        userGender = gender;
                        if (userGender == ProtoGlobal.Gender.MALE) {
                            txtGander.setText(getResources().getString(R.string.Male));
                        } else if (userGender == ProtoGlobal.Gender.FEMALE) {
                            txtGander.setText(getResources().getString(R.string.Female));
                        } else {
                            txtGander.setText(getResources().getString(R.string.set_gender));
                        }


                    }
                });
                realm.close();
            }
        };
        new RequestUserProfileGetGender().userProfileGetGender();


        G.onUserProfileGetEmail = new OnUserProfileGetEmail() {
            @Override
            public void onUserProfileGetEmail(final String email) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                        realmUserInfo.setEmail(email);
                        userEmail = email;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!email.equals("")) {
                                    txtEmail.setText(email);
                                } else {
                                    txtEmail.setText(getResources().getString(R.string.set_email));
                                }

                            }
                        });
                    }
                });
                realm.close();
            }
        };

        new RequestUserProfileGetEmail().userProfileGetEmail();


        final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            userId = realmUserInfo.getUserId();
            nickName = realmUserInfo.getUserInfo().getDisplayName();
            userName = realmUserInfo.getUserInfo().getUsername();
            phoneName = realmUserInfo.getUserInfo().getPhoneNumber();
            userGender = realmUserInfo.getGender();
            userEmail = realmUserInfo.getEmail();
        }
        if (nickName != null) {
            txtNickName.setText(nickName);
            txtNickNameTitle.setText(nickName);
        }
        if (userName != null) txtUserName.setText(userName);
        if (phoneName != null) txtPhoneNumber.setText(phoneName);

        ViewGroup layoutNickname = (ViewGroup) findViewById(R.id.st_layout_nickname);
        layoutNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout layoutNickname = new LinearLayout(ActivitySetting.this);
                layoutNickname.setOrientation(LinearLayout.VERTICAL);

                String splitNickname[] = txtNickName.getText().toString().split(" ");
                String firsName = "";
                String lastName = "";
                StringBuilder stringBuilder = null;
                if (splitNickname.length > 1) {

                    lastName = splitNickname[splitNickname.length - 1];
                    stringBuilder = new StringBuilder();
                    for (int i = 0; i < splitNickname.length - 1; i++) {

                        stringBuilder.append(splitNickname[i]).append(" ");
                    }
                    firsName = stringBuilder.toString();
                } else {
                    firsName = splitNickname[0];
                }
                final View viewFirstName = new View(ActivitySetting.this);
                viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                TextInputLayout inputFirstName = new TextInputLayout(ActivitySetting.this);
                final EditText edtFirstName = new EditText(ActivitySetting.this);
                edtFirstName.setHint(getResources().getString(R.string.fac_First_Name));
                edtFirstName.setText(firsName);
                edtFirstName.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtFirstName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtFirstName.setPadding(0, 8, 0, 8);
                edtFirstName.setSingleLine(true);
                inputFirstName.addView(edtFirstName);
                inputFirstName.addView(viewFirstName, viewParams);
                final View viewLastName = new View(ActivitySetting.this);
                viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtFirstName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }

                TextInputLayout inputLastName = new TextInputLayout(ActivitySetting.this);
                final EditText edtLastName = new EditText(ActivitySetting.this);
                edtLastName.setHint(getResources().getString(R.string.fac_Last_Name));
                edtLastName.setText(lastName);
                edtLastName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtLastName.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtLastName.setPadding(0, 8, 0, 8);
                edtLastName.setSingleLine(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtLastName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                inputLastName.addView(edtLastName);
                inputLastName.addView(viewLastName, viewParams);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 15);
                LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lastNameLayoutParams.setMargins(0, 15, 0, 10);

                layoutNickname.addView(inputFirstName, layoutParams);
                layoutNickname.addView(inputLastName, lastNameLayoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_nickname)).positiveText(getResources().getString(R.string.B_ok)).customView(layoutNickname, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalFirsName = firsName;
                edtFirstName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                            positive.setEnabled(true);
                        } else {
                            positive.setEnabled(false);
                        }
                    }
                });

                final String finalLastName = lastName;
                edtLastName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!edtLastName.getText().toString().equals(finalLastName)) {
                            positive.setEnabled(true);
                        } else {
                            positive.setEnabled(false);
                        }
                    }
                });

                edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewFirstName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewFirstName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewLastName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewLastName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String fullName = "";
                        if (edtFirstName.length() == 0) {
                            fullName = " " + " " + edtLastName.getText().toString();
                        }
                        if (edtLastName.length() == 0) {
                            fullName = edtFirstName.getText().toString() + " " + " ";
                        }
                        if (edtLastName.length() > 0 && edtFirstName.length() > 0) {
                            fullName = edtFirstName.getText().toString() + " " + edtLastName.getText().toString();
                        }

                        G.onUserProfileSetNickNameResponse = new OnUserProfileSetNickNameResponse() {
                            @Override
                            public void onUserProfileNickNameResponse(final String nickName, ProtoResponse.Response response) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Realm realm1 = Realm.getDefaultInstance();
                                        realm1.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                realm.where(RealmUserInfo.class).findFirst().getUserInfo().setDisplayName(nickName);
                                                txtNickNameTitle.setText(nickName);
                                                FragmentDrawerMenu.txtUserName.setText(nickName);
                                            }
                                        });

                                        realm1.close();
                                        txtNickName.setText(nickName);
                                    }
                                });
                            }

                            @Override
                            public void onUserProfileNickNameError(int majorCode, int minorCode) {

                            }
                        };
                        new RequestUserProfileSetNickname().userProfileNickName(fullName);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        if (userGender == null || userGender.getNumber() == -1 || userGender == ProtoGlobal.Gender.UNKNOWN) {
            txtGander.setText(getResources().getString(R.string.set_gender));
        } else {
            txtGander.setText(userGender == ProtoGlobal.Gender.MALE ? getResources().getString(R.string.male) : getResources().getString(R.string.female));
        }
        ViewGroup layoutGander = (ViewGroup) findViewById(R.id.st_layout_gander);
        layoutGander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = -1;
                Realm realm1 = Realm.getDefaultInstance();

                try {
                    if (realm1.where(RealmUserInfo.class).findFirst().getGender().getNumber() == 1) {
                        position = 0;
                    } else if (realm1.where(RealmUserInfo.class).findFirst().getGender().getNumber() == 2) {
                        position = 1;
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }

                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_Gander)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.array_gander).itemsCallbackSingleChoice(position, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text != null) txtGander.setText(text.toString());

                        switch (which) {
                            case 0: {
                                new RequestUserProfileSetGender().setUserProfileGender(ProtoGlobal.Gender.MALE);
                                break;
                            }
                            case 1: {
                                new RequestUserProfileSetGender().setUserProfileGender(ProtoGlobal.Gender.FEMALE);
                                break;
                            }
                        }
                        return false;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();

                G.onUserProfileSetGenderResponse = new OnUserProfileSetGenderResponse() {
                    @Override
                    public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                        // empty
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {

                    }
                };

                realm1.close();
            }
        });

        if (userEmail == null || userEmail.equals("")) {
            txtEmail.setText(getResources().getString(R.string.set_email));
        } else {
            txtEmail.setText(userEmail);
        }

        ViewGroup ltEmail = (ViewGroup) findViewById(R.id.st_layout_email);
        ltEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout layoutEmail = new LinearLayout(ActivitySetting.this);
                layoutEmail.setOrientation(LinearLayout.VERTICAL);

                final View viewEmail = new View(ActivitySetting.this);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                final TextInputLayout inputEmail = new TextInputLayout(ActivitySetting.this);
                final EditText edtEmail = new EditText(ActivitySetting.this);
                edtEmail.setHint(getResources().getString(R.string.set_email));

                if (txtEmail == null || txtEmail.getText().toString().equals(getResources().getString(R.string.set_email))) {
                    edtEmail.setText("");
                } else {
                    edtEmail.setText(txtEmail.getText().toString());
                }

                edtEmail.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtEmail.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtEmail.setPadding(0, 8, 0, 8);
                edtEmail.setSingleLine(true);
                inputEmail.addView(edtEmail);
                inputEmail.addView(viewEmail, viewParams);

                viewEmail.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtEmail.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutEmail.addView(inputEmail, layoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_email)).positiveText(getResources().getString(R.string.save)).customView(layoutEmail, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalEmail = userEmail;
                edtEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        if (!edtEmail.getText().toString().equals(finalEmail)) {
                            positive.setEnabled(true);
                        } else {
                            positive.setEnabled(false);
                        }
                        inputEmail.setErrorEnabled(true);
                        inputEmail.setError("");
                    }
                });

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new RequestUserProfileSetEmail().setUserProfileEmail(edtEmail.getText().toString());
                    }
                });

                edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewEmail.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewEmail.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                G.onUserProfileSetEmailResponse = new OnUserProfileSetEmailResponse() {
                    @Override
                    public void onUserProfileEmailResponse(final String email, ProtoResponse.Response response) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Realm realm1 = Realm.getDefaultInstance();
                                realm1.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        realm.where(RealmUserInfo.class).findFirst().setEmail(email);
                                        txtEmail.setText(email);
                                        dialog.dismiss();

                                    }
                                });
                                realm1.close();
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {
                        if (majorCode == 114 && minorCode == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inputEmail.setErrorEnabled(true);
                                    positive.setEnabled(false);
                                    inputEmail.setError("" + getResources().getString(R.string.error_email));
                                }
                            });
                        } else if (majorCode == 115) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inputEmail.setErrorEnabled(true);
                                    positive.setEnabled(false);
                                    inputEmail.setError("" + getResources().getString(R.string.error_email));
                                }
                            });
                        }
                    }
                };

                dialog.show();
            }
        });

        ViewGroup layoutUserName = (ViewGroup) findViewById(R.id.st_layout_username);
        layoutUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout layoutUserName = new LinearLayout(ActivitySetting.this);
                layoutUserName.setOrientation(LinearLayout.VERTICAL);

                final View viewUserName = new View(ActivitySetting.this);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                final TextInputLayout inputUserName = new TextInputLayout(ActivitySetting.this);
                final EditText edtUserName = new EditText(ActivitySetting.this);
                edtUserName.setHint(getResources().getString(R.string.st_username));
                edtUserName.setText(txtUserName.getText().toString());
                edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtUserName.setPadding(0, 8, 0, 8);
                edtUserName.setSingleLine(true);
                inputUserName.addView(edtUserName);
                inputUserName.addView(viewUserName, viewParams);

                viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutUserName.addView(inputUserName, layoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_username)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalUserName = userName;
                edtUserName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        if (HelperString.regexCheckUsername(editable.toString())) {
                            new RequestUserProfileCheckUsername().userProfileCheckUsername(editable.toString());
                        } else {
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("INVALID");
                            positive.setEnabled(false);
                        }
                    }
                });
                G.onUserProfileCheckUsername = new OnUserProfileCheckUsername() {
                    @Override
                    public void OnUserProfileCheckUsername(final ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.AVAILABLE) {
                                    if (!edtUserName.getText().toString().equals(finalUserName)) {
                                        positive.setEnabled(true);
                                    } else {
                                        positive.setEnabled(false);
                                    }
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError("");
                                } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.INVALID) {

                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError("INVALID");
                                    positive.setEnabled(false);
                                } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.TAKEN) {
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError("TAKEN");
                                    positive.setEnabled(false);
                                }
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {

                    }
                };

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new RequestUserProfileUpdateUsername().userProfileUpdateUsername(edtUserName.getText().toString());
                    }
                });

                G.onUserProfileUpdateUsername = new OnUserProfileUpdateUsername() {
                    @Override
                    public void onUserProfileUpdateUsername(final String username) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm1 = Realm.getDefaultInstance();
                                realm1.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.where(RealmUserInfo.class).findFirst().getUserInfo().setUsername(username);
                                        txtUserName.setText(username);
                                        dialog.dismiss();
                                    }
                                });
                                realm1.close();
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {

                    }
                };

                edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                        } else {
                            viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                        }
                    }
                });

                // check each word with server

                dialog.show();
            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.st_appbar);
        final TextView titleToolbar = (TextView) findViewById(R.id.st_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.st_parentLayoutCircleImage);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, final int verticalOffset) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (verticalOffset < -5) {
                            viewGroup.animate().alpha(0).setDuration(500);
                            titleToolbar.animate().alpha(1).setDuration(250);
                            viewGroup.clearAnimation();
                            titleToolbar.clearAnimation();
                            titleToolbar.setVisibility(View.VISIBLE);
                            viewGroup.setVisibility(View.GONE);
                        } else {

                            titleToolbar.animate().alpha(0).setDuration(250);
                            viewGroup.animate().alpha(1).setDuration(500);
                            viewGroup.clearAnimation();
                            titleToolbar.clearAnimation();
                            viewGroup.setVisibility(View.VISIBLE);
                            titleToolbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        // button back in toolbar
        RippleView rippleBack = (RippleView) findViewById(R.id.st_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        // button popupMenu in toolbar
        RippleView rippleMore = (RippleView) findViewById(R.id.st_ripple_more);
        rippleMore.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                LinearLayout layoutDialog = new LinearLayout(ActivitySetting.this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));

                TextView txtLogOut = new TextView(ActivitySetting.this);
                TextView txtDeleteAccount = new TextView(ActivitySetting.this);

                txtLogOut.setText(getResources().getString(log_out));
                txtDeleteAccount.setText(getResources().getString(R.string.delete_account));

                txtLogOut.setTextColor(getResources().getColor(android.R.color.black));
                txtDeleteAccount.setTextColor(getResources().getColor(android.R.color.black));

                int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim12 = (int) getResources().getDimension(R.dimen.dp12);
                int dim16 = (int) getResources().getDimension(R.dimen.dp16);
                txtLogOut.setTextSize(14);
                txtDeleteAccount.setTextSize(14);

                txtLogOut.setPadding(dim20, dim12, dim12, dim20);
                txtDeleteAccount.setPadding(dim20, 0, dim12, dim16);
                layoutDialog.addView(txtLogOut, params);
                layoutDialog.addView(txtDeleteAccount, params);

                popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3, ActivitySetting.this.getTheme()));
                } else {
                    popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
                }
                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                }
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //TODO do sth here on dismiss
                    }
                });

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(layoutDialog, Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));
                //                popupWindow.showAsDropDown(v);

                txtLogOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.log_out))
                                .content(R.string.content_log_out)
                                .positiveText(getResources().getString(R.string.B_ok))
                                .negativeText(getResources().getString(R.string.B_cancel))
                                .iconRes(R.mipmap.exit_to_app_button)
                                .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        popupWindow.dismiss();
                                        showProgressBar();

                                        G.onUserSessionLogout = new OnUserSessionLogout() {
                                            @Override
                                            public void onUserSessionLogout() {

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        HelperLogout.logout();
                                                        hideProgressBar();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hideProgressBar();
                                                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });


                                            }

                                            @Override
                                            public void onTimeOut() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hideProgressBar();
                                                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });
                                            }
                                        };

                                        new RequestUserSessionLogout().userSessionLogout();
                                    }
                                })

                                .show();
                    }
                });

                txtDeleteAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.delete_account))
                                .content(getResources().getString(R.string.delete_account_text))
                                .positiveText(getResources().getString(R.string.B_ok))
                                .iconRes(R.mipmap.round_delete_button)
                                .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                                .negativeText(getResources().getString(R.string.B_cancel))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        FragmentDeleteAccount fragmentDeleteAccount = new FragmentDeleteAccount();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("PHONE", txtPhoneNumber.getText().toString());
                                        fragmentDeleteAccount.setArguments(bundle);
                                        getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentDeleteAccount, null).commit();

                                    }
                                })
                                .show();
                        popupWindow.dismiss();
                    }
                });
            }
        });

        //fab button for set pic
        fab = (FloatingActionButton) findViewById(R.id.st_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDialog(R.array.profile);

            }
        });

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(userId, mAvatarId, HelperAvatar.AvatarType.USER, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(final String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                                if (G.onChangeUserPhotoListener != null) {
                                    G.onChangeUserPhotoListener.onChangePhoto(null);
                                }
                            }
                        });
                    }
                });


            }
        };

        circleImageView = (CircleImageView) findViewById(R.id.st_img_circleImage);
        RippleView rippleImageView = (RippleView) findViewById(R.id.st_ripple_circleImage);
        rippleImageView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {


                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).count() > 0) {
                    FragmentShowAvatars.appBarLayout = fab;
                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                    ActivitySetting.this.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragment, null).commit();
                }
                realm.close();
            }
        });


        textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        if (textLanguage.equals("English")) {
            poRbDialogLangouage = 0;
        } else if (textLanguage.equals("فارسی")) {
            poRbDialogLangouage = 1;
        } else if (textLanguage.equals("العربی")) {
            poRbDialogLangouage = 2;
        } else if (textLanguage.equals("Deutsch")) {
            poRbDialogLangouage = 3;
        }

        txtLanguage = (TextView) findViewById(R.id.st_txt_language);
        txtLanguage.setText(textLanguage);
        ltLanguage = (ViewGroup) findViewById(R.id.st_layout_language);
        ltLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_Language)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.language).itemsCallbackSingleChoice(poRbDialogLangouage, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        txtLanguage.setText(text.toString());
                        poRbDialogLangouage = which;
                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SHP_SETTING.KEY_LANGUAGE, text.toString());
                        editor.apply();

                        switch (which) {
                            case 0:
                                setLocale("en");
                                G.onRefreshActivity.refresh("en");
                                break;
                            case 1:
                                setLocale("fa");
                                G.onRefreshActivity.refresh("fa");

                                break;
                            case 2:
                                setLocale("ar");
                                G.onRefreshActivity.refresh("ar");

                                break;
                            case 3:
                                setLocale("nl");
                                G.onRefreshActivity.refresh("nl");
                                break;
                        }

                        return false;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });

        final long sizeFolderPhoto = getFolderSize(new File(G.DIR_IMAGES));
        final long sizeFolderVideo = getFolderSize(new File(G.DIR_VIDEOS));
        final long sizeFolderDocument = getFolderSize(new File(G.DIR_DOCUMENT));

        final long total = sizeFolderPhoto + sizeFolderVideo + sizeFolderDocument;

        txtSizeClearCach = (TextView) findViewById(R.id.st_txt_clearCache);
        txtSizeClearCach.setText(formatFileSize(total));

        ltClearCache = (RelativeLayout) findViewById(R.id.st_layout_clearCache);
        ltClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final long sizeFolderPhotoDialog = getFolderSize(new File(G.DIR_IMAGES));
                final long sizeFolderVideoDialog = getFolderSize(new File(G.DIR_VIDEOS));
                final long sizeFolderDocumentDialog = getFolderSize(new File(G.DIR_DOCUMENT));

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_title_Clear_Cache)).customView(R.layout.st_dialog_clear_cach, wrapInScrollView).positiveText(getResources().getString(R.string.st_title_Clear_Cache)).show();

                View view = dialog.getCustomView();

                final File filePhoto = new File(G.DIR_IMAGES);
                assert view != null;
                TextView photo = (TextView) view.findViewById(R.id.st_txt_sizeFolder_photo);
                photo.setText(formatFileSize(sizeFolderPhotoDialog));

                final CheckBox checkBoxPhoto = (CheckBox) view.findViewById(R.id.st_checkBox_photo);
                final File fileVideo = new File(G.DIR_VIDEOS);
                TextView video = (TextView) view.findViewById(R.id.st_txt_sizeFolder_video);
                video.setText(formatFileSize(sizeFolderVideoDialog));

                final CheckBox checkBoxVideo = (CheckBox) view.findViewById(R.id.st_checkBox_video_dialogClearCash);

                final File fileDocument = new File(G.DIR_DOCUMENT);
                TextView document = (TextView) view.findViewById(R.id.st_txt_sizeFolder_document_dialogClearCash);
                document.setText(formatFileSize(sizeFolderDocumentDialog));

                final CheckBox checkBoxDocument = (CheckBox) view.findViewById(R.id.st_checkBox_document_dialogClearCash);

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (checkBoxPhoto.isChecked()) {
                            for (File file : filePhoto.listFiles())
                                if (!file.isDirectory()) file.delete();
                        }
                        if (checkBoxVideo.isChecked()) {
                            for (File file : fileVideo.listFiles())
                                if (!file.isDirectory()) file.delete();
                        }
                        if (checkBoxDocument.isChecked()) {
                            for (File file : fileDocument.listFiles())
                                if (!file.isDirectory()) file.delete();
                        }
                        long afterClearSizeFolderPhoto = getFolderSize(new File(G.DIR_IMAGES));
                        long afterClearSizeFolderVideo = getFolderSize(new File(G.DIR_VIDEOS));
                        long afterClearSizeFolderDocument = getFolderSize(new File(G.DIR_DOCUMENT));
                        long afterClearTotal = afterClearSizeFolderPhoto + afterClearSizeFolderVideo + afterClearSizeFolderDocument;
                        txtSizeClearCach.setText(formatFileSize(afterClearTotal));
                        dialog.dismiss();
                    }
                });
            }
        });

        //toggle crop

        final TextView txtCrop = (TextView) findViewById(R.id.stsp_txt_crop);
        final ToggleButton stsp_toggle_crop = (ToggleButton) findViewById(R.id.stsp_toggle_crop);

        int checkedEnableCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 0);
        if (checkedEnableCrop == 1) {
            stsp_toggle_crop.setChecked(true);
        } else {
            stsp_toggle_crop.setChecked(false);
        }

        txtCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (stsp_toggle_crop.isChecked()) {
                    stsp_toggle_crop.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_CROP, 0);
                    editor.apply();
                } else {
                    stsp_toggle_crop.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_CROP, 1);
                    editor.apply();
                }
            }
        });


        TextView txtPrivacySecurity = (TextView) findViewById(R.id.st_txt_privacySecurity);
        txtPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPrivacyAndSecurity fragmentPrivacyAndSecurity = new FragmentPrivacyAndSecurity();
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentPrivacyAndSecurity, null).commit();
            }
        });

        final TextView txtDataShams = (TextView) findViewById(R.id.st_txt_st_toggle_dataShams);
        final ToggleButton toggleEnableDataShams = (ToggleButton) findViewById(R.id.st_toggle_dataShams);

        int checkedEnableDataShams = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 0);
        if (checkedEnableDataShams == 1) {
            toggleEnableDataShams.setChecked(true);
        } else {
            toggleEnableDataShams.setChecked(false);
        }

        txtDataShams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleEnableDataShams.isChecked()) {
                    toggleEnableDataShams.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 0);
                    editor.apply();
                } else {
                    toggleEnableDataShams.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_ENABLE_DATA_SHAMS, 1);
                    editor.apply();
                }
            }
        });


        poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - 11;
        txtMessageTextSize = (TextView) findViewById(R.id.st_txt_messageTextSize_number);
        txtMessageTextSize.setText("" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14));

        ltMessageTextSize = (ViewGroup) findViewById(R.id.st_layout_messageTextSize);
        ltMessageTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_title_message_textSize)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.message_text_size).itemsCallbackSingleChoice(poRbDialogTextSize, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text != null) {
                            txtMessageTextSize.setText(text.toString().replace("(Hello)", "").trim());
                        }
                        poRbDialogTextSize = which;
                        int size = Integer.parseInt(text.toString().replace("(Hello)", "").trim());
                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, size);
                        editor.apply();

                        G.setUserTextSize();

                        return false;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).show();
            }
        });


        txtChatBackground = (TextView) findViewById(R.id.st_txt_chatBackground);
        txtChatBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivitySetting.this, ActivityChatBackground.class));
            }
        });

        ltInAppBrowser = (TextView) findViewById(R.id.st_txt_inAppBrowser);
        toggleInAppBrowser = (ToggleButton) findViewById(R.id.st_toggle_inAppBrowser);
        int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        if (checkedInappBrowser == 1) {
            toggleInAppBrowser.setChecked(true);
        } else {
            toggleInAppBrowser.setChecked(false);
        }

        ltInAppBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleInAppBrowser.isChecked()) {
                    toggleInAppBrowser.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);
                    editor.apply();
                } else {
                    toggleInAppBrowser.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                    editor.apply();
                }
            }
        });

        txtNotifyAndSound = (TextView) findViewById(R.id.st_txt_notifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivitySetting.this, ActivitySettingNotification.class));
            }
        });

        ltSentByEnter = (TextView) findViewById(R.id.st_txt_sendEnter);
        toggleSentByEnter = (ToggleButton) findViewById(R.id.st_toggle_sendEnter);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 1);
        if (checkedSendByEnter == 1) {
            toggleSentByEnter.setChecked(true);
        } else {
            toggleSentByEnter.setChecked(false);
        }

        ltSentByEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleSentByEnter.isChecked()) {

                    toggleSentByEnter.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
                    editor.apply();
                } else {
                    toggleSentByEnter.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 1);
                    editor.apply();
                }
            }
        });

        txtKeepMedia = (TextView) findViewById(R.id.st_txt_keepMedia);
        txtKeepMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_keepMedia).content(R.string.st_dialog_content_keepMedia).positiveText("ForEver").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_KEEP_MEDIA, false);
                        editor.apply();
                    }
                }).negativeText("1WEEk").onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SHP_SETTING.KEY_KEEP_MEDIA, true);
                        editor.apply();
                    }
                }).show();
            }
        });


        txtAutoDownloadData = (TextView) findViewById(R.id.st_txt_autoDownloadData);
        txtAutoDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KEY_AD_DATA_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
                KEY_AD_DATA_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
                KEY_AD_DATA_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
                KEY_AD_DATA_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
                KEY_AD_DATA_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
                KEY_AD_DATA_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, -1);

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_auto_download_data).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                        KEY_AD_DATA_PHOTO, KEY_AD_DATA_VOICE_MESSAGE, KEY_AD_DATA_VIDEO, KEY_AD_DATA_FILE, KEY_AD_DATA_MUSIC, KEY_AD_DATA_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1);
                        editor.apply();

                        for (Integer aWhich : which) {


                            if (aWhich == 0) {
                                editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, aWhich);
                            } else if (aWhich == 1) {
                                editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, aWhich);
                            } else if (aWhich == 2) {
                                editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, aWhich);
                            } else if (aWhich == 3) {
                                editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, aWhich);
                            } else if (aWhich == 4) {
                                editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, aWhich);
                            } else if (aWhich == 5) {
                                editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, aWhich);
                            }
                            editor.apply();
                        }

                        return true;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });


        txtAutoDownloadWifi = (TextView) findViewById(R.id.st_txt_autoDownloadWifi);
        txtAutoDownloadWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KEY_AD_WIFI_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
                KEY_AD_WIFI_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
                KEY_AD_WIFI_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
                KEY_AD_WIFI_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
                KEY_AD_WIFI_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
                KEY_AD_WIFI_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1);

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_auto_download_wifi).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                        KEY_AD_WIFI_PHOTO, KEY_AD_WIFI_VOICE_MESSAGE, KEY_AD_WIFI_VIDEO, KEY_AD_WIFI_FILE, KEY_AD_WIFI_MUSIC, KEY_AD_WIFI_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {


                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1);
                        editor.apply();

                        for (Integer aWhich : which) {
                            Log.i("JJJJ", "WIFI: " + aWhich);

                            if (aWhich == 0) {
                                editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, aWhich);
                            } else if (aWhich == 1) {
                                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, aWhich);
                            } else if (aWhich == 2) {
                                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, aWhich);
                            } else if (aWhich == 3) {
                                editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, aWhich);
                            } else if (aWhich == 4) {

                                editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, aWhich);
                            } else if (aWhich == 5) {
                                editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, aWhich);
                            }
                            editor.apply();
                        }

                        return true;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.cancel)).show();
            }
        });


        txtAutoDownloadRoaming = (TextView) findViewById(R.id.st_txt_autoDownloadRoaming);
        txtAutoDownloadRoaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                KEY_AD_ROAMING_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
                KEY_AD_ROAMING_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
                KEY_AD_ROAMING_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
                KEY_AD_ROAMING_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
                KEY_AD_ROAMING_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
                KEY_AD_ROAMINGN_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_auto_download_roaming).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                        KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMINGN_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        //

                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);
                        editor.apply();

                        for (Integer aWhich : which) {
                            if (aWhich > -1) {
                                if ((aWhich == 0)) {
                                    editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, aWhich);
                                } else if ((aWhich == 1)) {
                                    editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, aWhich);
                                } else if ((aWhich == 2)) {
                                    editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, aWhich);
                                } else if ((aWhich == 3)) {
                                    editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, aWhich);
                                } else if ((aWhich == 4)) {
                                    editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, aWhich);
                                } else if ((aWhich == 5)) {
                                    editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, aWhich);
                                }
                                editor.apply();
                            }
                        }
                        return true;

                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });

        ltEnableAnimation = (TextView) findViewById(R.id.st_txt_enableAnimation);
        toggleEnableAnimation = (ToggleButton) findViewById(R.id.st_toggle_enableAnimation);
        int checkedEnableAnimation = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 0);
        if (checkedEnableAnimation == 1) {
            toggleEnableAnimation.setChecked(true);
        } else {
            toggleEnableAnimation.setChecked(false);
        }

        ltEnableAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleEnableAnimation.isChecked()) {
                    toggleEnableAnimation.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 0);
                    editor.apply();
                } else {
                    toggleEnableAnimation.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 1);
                    editor.apply();
                }
            }
        });

        ltAutoGifs = (TextView) findViewById(R.id.st_txt_autoGif);
        toggleAutoGifs = (ToggleButton) findViewById(R.id.st_toggle_autoGif);
        int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS);
        if (checkedAutoGif == 1) {
            toggleAutoGifs.setChecked(true);
        } else {
            toggleAutoGifs.setChecked(false);
        }

        ltAutoGifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleAutoGifs.isChecked()) {
                    toggleAutoGifs.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
                    editor.apply();
                } else {
                    toggleAutoGifs.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 1);
                    editor.apply();
                }
            }
        });

        ltSaveToGallery = (TextView) findViewById(R.id.st_txt_saveGallery);
        toggleSaveToGallery = (ToggleButton) findViewById(R.id.st_toggle_saveGallery);
        int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 1);
        if (checkedSaveToGallery == 1) {
            toggleSaveToGallery.setChecked(true);
        } else {
            toggleSaveToGallery.setChecked(false);
        }

        ltSaveToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (toggleSaveToGallery.isChecked()) {

                    toggleSaveToGallery.setChecked(false);

                    editor.putInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
                    editor.apply();
                } else {
                    toggleSaveToGallery.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 1);
                    editor.apply();
                }
            }
        });

        txtPrivacyPolicy = (TextView) findViewById(R.id.st_txt_privacy_policy);
        txtPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySetting.this, ActivityWebView.class);
                intent.putExtra("PATH", "Policy");
                startActivity(intent);
            }
        });

        txtFaq = (TextView) findViewById(R.id.st_txt_faq);
        txtFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivitySetting.this, ActivityWebView.class);
                intent.putExtra("PATH", "FAQ");
                startActivity(intent);
            }
        });


        TextView txtVersionApp = (TextView) findViewById(R.id.st_txt_versionApp);
        txtVersionApp.setText(getString(R.string.iGap_version) + " v" + getAppVersion());

        realm.close();

        showImage();
    }


    // call this method for show image in enter to this activity
    private void showImage() {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), circleImageView);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });
    }

    //dialog for choose pic from gallery or camera
    private void startDialog(int r) {

        new MaterialDialog.Builder(this).title(getResources().getString(R.string.choose_picture)).negativeText(getResources().getString(R.string.B_cancel)).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(getResources().getString(R.string.array_From_Camera))) { // camera


                    try {
                        HelperPermision.getStoragePermision(ActivitySetting.this, new OnGetPermision() {
                            @Override
                            public void Allow() throws IOException {
                                HelperPermision.getCamarePermision(ActivitySetting.this, new OnGetPermision() {
                                    @Override
                                    public void Allow() {
                                        dialog.dismiss();
                                        useCamera();
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        new AttachFile(ActivitySetting.this).requestOpenGalleryForImageSingleSelect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(ActivitySetting.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                idAvatar = SUID.id().get();
                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                nameImageFile = new File(pathSaveImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uriIntent = Uri.fromFile(nameImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(ActivitySetting.this, getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //=====================================================================================result
    // from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                intent.putExtra("IMAGE_CAMERA", AttachFile.mCurrentPhotoPath);
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            } else {
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                if (uriIntent != null) {
                    ImageHelper.correctRotateImage(pathSaveImage, true);
                    intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                    intent.putExtra("TYPE", "camera");
                    intent.putExtra("PAGE", "setting");
                    intent.putExtra("ID", (int) (idAvatar + 1L));
                    startActivityForResult(intent, IntentRequests.REQ_CROP);
                }
            }

        } else if (requestCode == AttachFile.request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUri(data.getData()));
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);

            }
        } else if (requestCode == IntentRequests.REQ_CROP && resultCode == RESULT_OK) { // save path image on data base ( realm )

            if (data != null) {
                pathSaveImage = data.getData().toString();
            }

            lastUploadedAvatarId = idAvatar + 1L;

            new UploadTask(prgWait, ActivitySetting.this).execute(pathSaveImage, lastUploadedAvatarId);
        }
    }

    // change language
    public void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        startActivity(new Intent(ActivitySetting.this, ActivitySetting.class));
        overridePendingTransition(0, 0);
        finish();
    }

    //    private void getSms(String message) {
    //        String verificationCode = HelperString.regexExtractValue(message, regex);
    //
    //        if (verificationCode != null && !verificationCode.isEmpty()) {
    //
    //            G.onUserDelete = new OnUserDelete() {
    //                @Override
    //                public void onUserDeleteResponse() {
    //                    Log.i("UUU", "onUserDeleteResponse");
    //                    HelperLogout.logout();
    //                }
    //            };
    //
    //            Log.i("UUU", "RequestUserDelete verificationCode : " + verificationCode);
    //            new RequestUserDelete().userDelete(verificationCode, ProtoUserDelete.UserDelete.Reason.OTHER);
    //        }
    //    }

    @Override
    protected void onResume() {
        //        final IntentFilter filter = new IntentFilter();
        //        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //        smsReceiver = new IncomingSms(new OnSmsReceive() {
        //
        //            @Override
        //            public void onSmsReceive(String message) {
        //                try {
        //                    if (message != null && !message.isEmpty() && !message.equals("null") &&
        //                            !message.equals("")) {
        //                        getSms(message);
        //                    }
        //                } catch (Exception e1) {
        //                    e1.getStackTrace();
        //                }
        //            }
        //        });
        //
        //        registerReceiver(smsReceiver, filter);
        super.onResume();
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            showImage();
        } else {
            HelperAvatar.avatarAdd(userId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }

    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
        new RequestUserAvatarAdd().userAddAvatar(uploadStructure.token);
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {
        // TODO: 10/20/2016 [Alireza] update view something like updating progress
    }

    @Override
    public void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
        Realm realm = Realm.getDefaultInstance();
        if (selector != ProtoFileDownload.FileDownload.Selector.FILE) {
            // requested thumbnail
            RealmAvatar avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, token).findFirst();
            if (avatar != null) {
                requestDownloadAvatar(true, token, avatar.getFile().getName(), (int) avatar.getFile().getSmallThumbnail().getSize());
            }
        } else {
            // TODO: 11/22/2016 [Alireza] implement
        }
        realm.close();
    }

    @Override
    public void onAvatarDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType) {
        // empty
    }

    @Override
    public void onAvatarError() {

    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {

        private ProgressBar prg;
        private Activity myActivityReference;

        public UploadTask(ProgressBar prg, Activity myActivityReference) {
            this.prg = prg;
            this.myActivityReference = myActivityReference;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long avatarId = (long) params[1];
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
            myActivityReference.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            prg.setVisibility(View.GONE);
            identityCurrent = Long.toString(result.messageId);
            G.uploaderUtil.startUploading(result, identityCurrent);
        }
    }

    private String getAppVersion() {

        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    @Override
    public void onUploadStarted(FileUploadStructure struct) {
        showProgressBar();
    }

    @Override
    public void onBadDownload(String token) {
    }

    @Override
    public void onFileTimeOut(String identity) {
        if (identityCurrent.equals(identity)) {
            hideProgressBar();
        }
    }

    //***Show And Hide Progress

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }
}
