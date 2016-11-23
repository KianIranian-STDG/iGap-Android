package com.iGap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.iGap.fragments.FragmentPrivacyAndSecurity;
import com.iGap.fragments.FragmentShowAvatars;
import com.iGap.fragments.FragmentSticker;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperLogout;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnUserAvatarDelete;
import com.iGap.interfaces.OnUserAvatarResponse;
import com.iGap.interfaces.OnUserProfileCheckUsername;
import com.iGap.interfaces.OnUserProfileSetEmailResponse;
import com.iGap.interfaces.OnUserProfileSetGenderResponse;
import com.iGap.interfaces.OnUserProfileSetNickNameResponse;
import com.iGap.interfaces.OnUserProfileUpdateUsername;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.IncomingSms;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.proto.ProtoUserProfileCheckUsername;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserAvatarDelete;
import com.iGap.request.RequestUserProfileCheckUsername;
import com.iGap.request.RequestUserProfileSetEmail;
import com.iGap.request.RequestUserProfileSetGender;
import com.iGap.request.RequestUserProfileSetNickname;
import com.iGap.request.RequestUserProfileUpdateUsername;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.R.id.st_layoutParent;

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
    private ProtoGlobal.Gender gander;
    private String email;
    private long userId;
    private long lastUploadedAvatarId;
    private IncomingSms smsReceiver;
    private String regex;
    public ProgressBar prgWait;

    public static long getFolderSize(File dir) {
        long size = 0;
        if (dir == null)
            return size;

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                System.out.println(file.getName() + " " + file.length());
                size += file.length();
            } else {
                size += getFolderSize(file);
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

    private void setImage() {
        final Realm realm = Realm.getDefaultInstance();

        RealmAvatar realmAvatar = realm.where(RealmUserInfo.class).findFirst().getUserInfo().getLastAvatar();
        if (realmAvatar != null) {
            if (realmAvatar.getFile().isFileExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), circleImageView);
                G.onChangeUserPhotoListener.onChangePhoto(realmAvatar.getFile().getLocalFilePath());
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), circleImageView);
                G.onChangeUserPhotoListener.onChangePhoto(realmAvatar.getFile().getLocalThumbnailPath());
            } else {
                showInitials();
                requestDownloadAvatar(false, realmAvatar.getFile().getToken(), realmAvatar.getFile().getName(), (int) realmAvatar.getFile().getSmallThumbnail().getSize());
            }
        } else {
            showInitials();
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
                    G.onChangeUserPhotoListener.onChangePhoto(filePath);
                    realm.close();
                }
            });

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity =
                token + '*' + selector.toString() + '*' + smallSize + '*' + fileName + '*' + 0;

        new RequestFileDownload().download(token, 0, smallSize,
                selector, identity);
    }

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        circleImageView.setImageBitmap(
                HelperImageBackColor.drawAlphabetOnPicture(
                        (int) circleImageView.getContext().getResources().getDimension(R.dimen.dp100)
                        , realmUserInfo.getUserInfo().getInitials()
                        , realmUserInfo.getUserInfo().getColor()));
        realm.close();

        G.onChangeUserPhotoListener.onChangePhoto(null);
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
        prgWait = (ProgressBar) findViewById(R.id.st_prgWaiting_addContact);
        prgWait.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.toolbar_background),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
        final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            userId = realmUserInfo.getUserId();
            nickName = realmUserInfo.getUserInfo().getDisplayName();
            userName = realmUserInfo.getUserInfo().getUsername();
            phoneName = realmUserInfo.getUserInfo().getPhoneNumber();
            gander = realmUserInfo.getGender();
            email = realmUserInfo.getEmail();
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

                final MaterialDialog dialog =
                        new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_nickname)).positiveText(getResources().getString(R.string.B_ok))
                                .customView(layoutNickname, true)
                                .widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel))
                                .build();

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

                                if (majorCode == 112) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_112), Snackbar.LENGTH_LONG);

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
                                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_113), Snackbar.LENGTH_LONG);

                                            snack.setAction("CANCEL", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    snack.dismiss();
                                                }
                                            });
                                            snack.show();
                                        }
                                    });
                                }
                            }
                        };
                        new RequestUserProfileSetNickname().userProfileNickName(fullName);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        final TextView txtGander = (TextView) findViewById(R.id.st_txt_gander);
        if (gander == null || gander.getNumber() == -1) {
            txtGander.setText(getResources().getString(R.string.set_gender));
        } else {
            txtGander.setText(gander == ProtoGlobal.Gender.MALE ? "Male" : "Female");
        }

        ViewGroup layoutGander = (ViewGroup) findViewById(R.id.st_layout_gander);
        layoutGander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm1 = Realm.getDefaultInstance();
                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_Gander))
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.array_gander)
                        .itemsCallbackSingleChoice((realm1.where(RealmUserInfo.class).findFirst().getGender().getNumber() == 1 ? 0 : 1), new MaterialDialog.ListCallbackSingleChoice() {
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
                        }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel))
                        .show();

                G.onUserProfileSetGenderResponse = new OnUserProfileSetGenderResponse() {
                    @Override
                    public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                        // empty
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {
                        if (majorCode == 116 && minorCode == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_116), Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snack.dismiss();
                                        }
                                    });
                                    snack.show();
                                }
                            });
                        } else if (majorCode == 117) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_117), Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snack.dismiss();
                                        }
                                    });
                                    snack.show();
                                }
                            });
                        }
                    }
                };

                realm1.close();
            }
        });

        final TextView txtEmail = (TextView) findViewById(R.id.st_txt_email);
        if (email == null) {
            txtEmail.setText(getResources().getString(R.string.set_email));
        } else {
            txtEmail.setText(email);
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

                if (email == null) {
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

                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_email)).positiveText(getResources().getString(R.string.save))
                        .customView(layoutEmail, true)
                        .widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel))
                        .build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                final String finalEmail = email;
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

                final MaterialDialog dialog =
                        new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_username)).positiveText(getResources().getString(R.string.save))
                                .customView(layoutUserName, true)
                                .widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel))
                                .build();

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
                        new RequestUserProfileCheckUsername().userProfileCheckUsername(editable.toString());
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
                edtUserName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                dialog.show();
            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.st_appbar);
        final TextView titleToolbar = (TextView) findViewById(R.id.st_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.st_parentLayoutCircleImage);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset < -5) {

                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(0).setDuration(500);
                    titleToolbar.animate().alpha(1).setDuration(250);
                } else {

                    titleToolbar.setVisibility(View.GONE);
                    viewGroup.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(0).setDuration(250);
                    viewGroup.animate().alpha(1).setDuration(500);
                }
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

                txtLogOut.setText(getResources().getString(R.string.log_out));
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
                        HelperLogout.logout();
                        popupWindow.dismiss();

                    }
                });

                txtDeleteAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new MaterialDialog.Builder(ActivitySetting.this)
                                .title(getResources().getString(R.string.delete_account))
                                .content(getResources().getString(R.string.delete_account_text))
                                .positiveText(getResources().getString(R.string.B_ok))
                                .negativeText(getResources().getString(R.string.B_cancel))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        FragmentDeleteAccount fragmentDeleteAccount = new FragmentDeleteAccount();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("PHONE", txtPhoneNumber.getText().toString());
                                        fragmentDeleteAccount.setArguments(bundle);
                                        getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                                .replace(R.id.st_layoutParent, fragmentDeleteAccount)
                                                .commit();

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

                Realm realm = Realm.getDefaultInstance();

                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).count() > 0) {
                    startDialog(R.array.profile_delete);
                } else {
                    startDialog(R.array.profile);
                }

                realm.close();
            }
        });

        circleImageView = (CircleImageView) findViewById(R.id.st_img_circleImage);
        RippleView rippleImageView = (RippleView) findViewById(R.id.st_ripple_circleImage);
        rippleImageView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).count() > 0) {
                    FragmentShowAvatars.appBarLayout = fab;

                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId);
                    ActivitySetting.this.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(st_layoutParent, fragment, null).commit();
                }
                realm.close();
            }
        });
        textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, "English");
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

                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_Language))
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black)).items(R.array.language).itemsCallbackSingleChoice(poRbDialogLangouage, new MaterialDialog.ListCallbackSingleChoice() {
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
                                break;
                            case 1:
                                setLocale("fa");

                                break;
                            case 2:
                                setLocale("ar");

                                break;
                            case 3:
                                setLocale("nl");
                                break;
                        }

                        return false;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel))
                        .show();
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
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_title_Clear_Cache))
                        .customView(R.layout.st_dialog_clear_cach, wrapInScrollView)
                        .positiveText(getResources().getString(R.string.st_title_Clear_Cache))
                        .show();

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

        TextView txtPrivacySecurity = (TextView) findViewById(R.id.st_txt_privacySecurity);
        txtPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPrivacyAndSecurity fragmentPrivacyAndSecurity = new FragmentPrivacyAndSecurity();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(st_layoutParent, fragmentPrivacyAndSecurity)
                        .commit();
            }
        });

        poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 16) - 11;
        txtMessageTextSize = (TextView) findViewById(R.id.st_txt_messageTextSize_number);
        txtMessageTextSize.setText("" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 16));

        ltMessageTextSize = (ViewGroup) findViewById(R.id.st_layout_messageTextSize);
        ltMessageTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(ActivitySetting.this).title(getResources().getString(R.string.st_title_message_textSize))
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.message_text_size)
                        .itemsCallbackSingleChoice(poRbDialogTextSize, new MaterialDialog.ListCallbackSingleChoice() {
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
                        })
                        .positiveText(getResources().getString(R.string.B_ok))
                        .show();
            }
        });

        txtSticker = (TextView) findViewById(R.id.st_txt_sticker);
        txtSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentSticker fragmentSticker = new FragmentSticker();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(st_layoutParent, fragmentSticker)
                        .commit();
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
        int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);
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
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
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

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_keepMedia)
                        .content(R.string.st_dialog_content_keepMedia)
                        .positiveText("ForEver")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            }
                        })
                        .negativeText("1WEEk")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            }
                        })
                        .show();
            }
        });

        KEY_AD_DATA_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
        KEY_AD_DATA_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
        KEY_AD_DATA_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
        KEY_AD_DATA_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
        KEY_AD_DATA_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
        KEY_AD_DATA_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, -1);
        txtAutoDownloadData = (TextView) findViewById(R.id.st_txt_autoDownloadData);
        txtAutoDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_auto_download_data).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                        KEY_AD_DATA_PHOTO, KEY_AD_DATA_VOICE_MESSAGE, KEY_AD_DATA_VIDEO, KEY_AD_DATA_FILE, KEY_AD_DATA_MUSIC, KEY_AD_DATA_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        for (int i = 0; i < which.length; i++) {

                            if (which[i] == 0) {
                                KEY_AD_DATA_PHOTO = which[i];
                            } else if (which[i] == 1) {
                                KEY_AD_DATA_VOICE_MESSAGE = which[i];
                            } else if (which[i] == 2) {
                                KEY_AD_DATA_VIDEO = which[i];
                            } else if (which[i] == 3) {
                                KEY_AD_DATA_FILE = which[i];
                            } else if (which[i] == 4) {
                                KEY_AD_DATA_MUSIC = which[i];
                            } else if (which[i] == 5) {
                                KEY_AD_DATA_GIF = which[i];
                            }
                        }

                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, KEY_AD_DATA_PHOTO);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, KEY_AD_DATA_VOICE_MESSAGE);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, KEY_AD_DATA_VIDEO);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, KEY_AD_DATA_FILE);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, KEY_AD_DATA_MUSIC);
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, KEY_AD_DATA_GIF);
                        editor.apply();

                        return true;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });

        KEY_AD_WIFI_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
        KEY_AD_WIFI_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
        KEY_AD_WIFI_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
        KEY_AD_WIFI_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
        KEY_AD_WIFI_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
        KEY_AD_WIFI_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1);

        txtAutoDownloadWifi = (TextView) findViewById(R.id.st_txt_autoDownloadWifi);
        txtAutoDownloadWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_auto_download_wifi).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                        KEY_AD_WIFI_PHOTO, KEY_AD_WIFI_VOICE_MESSAGE, KEY_AD_WIFI_VIDEO, KEY_AD_WIFI_FILE, KEY_AD_WIFI_MUSIC, KEY_AD_WIFI_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        //
                        for (Integer aWhich : which) {

                            if (aWhich == 0) {

                                KEY_AD_WIFI_PHOTO = aWhich;
                            } else if (aWhich == 1) {
                                KEY_AD_WIFI_VOICE_MESSAGE = aWhich;
                            } else if (aWhich == 2) {
                                KEY_AD_WIFI_VIDEO = aWhich;
                            } else if (aWhich == 3) {
                                KEY_AD_WIFI_FILE = aWhich;
                            } else if (aWhich == 4) {
                                KEY_AD_WIFI_MUSIC = aWhich;
                            } else if (aWhich == 5) {
                                KEY_AD_WIFI_GIF = aWhich;
                            }
                        }

                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, KEY_AD_WIFI_PHOTO);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, KEY_AD_WIFI_VOICE_MESSAGE);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, KEY_AD_WIFI_VIDEO);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, KEY_AD_WIFI_FILE);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, KEY_AD_WIFI_MUSIC);
                        editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, KEY_AD_WIFI_GIF);
                        editor.apply();

                        return true;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.cancel)).show();
            }
        });

        KEY_AD_ROAMING_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
        KEY_AD_ROAMING_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
        KEY_AD_ROAMING_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
        KEY_AD_ROAMING_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
        KEY_AD_ROAMING_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
        KEY_AD_ROAMINGN_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);

        txtAutoDownloadRoaming = (TextView) findViewById(R.id.st_txt_autoDownloadRoaming);
        txtAutoDownloadRoaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(ActivitySetting.this).title(R.string.st_auto_download_roaming).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                        KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMINGN_GIF
                }, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        //
                        for (Integer aWhich : which) {

                            if (aWhich == 0) {
                                KEY_AD_ROAMING_PHOTO = aWhich;
                            } else if (aWhich == 1) {
                                KEY_AD_ROAMING_VOICE_MESSAGE = aWhich;
                            } else if (aWhich == 2) {
                                KEY_AD_ROAMING_VIDEO = aWhich;
                            } else if (aWhich == 3) {
                                KEY_AD_ROAMING_FILE = aWhich;
                            } else if (aWhich == 4) {
                                KEY_AD_ROAMING_MUSIC = aWhich;
                            } else if (aWhich == 5) {
                                KEY_AD_ROAMINGN_GIF = aWhich;
                            }
                        }

                        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_PHOTO);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VOICE_MESSAGE);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_VIDEO);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_FILE);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMING_MUSIC);
                        editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, KEY_AD_ROAMINGN_GIF);
                        editor.apply();

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
        int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
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
        int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
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

        realm.close();

        setImage();
    }

    //dialog for choose pic from gallery or camera
    private void startDialog(int r) {

        new MaterialDialog.Builder(this).title(getResources().getString(R.string.choose_picture))
                .negativeText(getResources().getString(R.string.B_cancel))
                .items(r)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals(getResources().getString(R.string.array_From_Camera))) {
                            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                idAvatar = SUID.id().get();
                                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                                nameImageFile = new File(pathSaveImage);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                uriIntent = Uri.fromFile(nameImageFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                startActivityForResult(intent, IntentRequests.REQ_CAMERA);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(ActivitySetting.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                            }
                        } else if (text.toString().equals(getResources().getString(R.string.array_Delete_photo))) {
                            G.onUserAvatarDelete = new OnUserAvatarDelete() {
                                @Override
                                public void onUserAvatarDelete(final long avatarId, final String token) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Realm realm = Realm.getDefaultInstance();
                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    Log.i("XXX", "RealmAvatarPath 3");
                                                    RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, avatarId).findFirst();
                                                    if (realmAvatar != null) {
                                                        realmAvatar.deleteFromRealm();
                                                    }
                                                }
                                            });
                                            realm.close();
                                            setImage();
                                        }
                                    });
                                }
                            };

                            Realm realm = Realm.getDefaultInstance();
                            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmUserInfo.class).findFirst().getUserInfo();
                            new RequestUserAvatarDelete().userAvatarDelete(realmRegisteredInfo.getLastAvatar().getId());
                            realm.close();

                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            idAvatar = SUID.id().get();
                            startActivityForResult(intent, IntentRequests.REQ_GALLERY);
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    //=====================================================================================result
    // from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentRequests.REQ_CAMERA && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
            if (uriIntent != null) {

                ImageHelper.correctRotateImage(pathSaveImage);

                intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", "setting");
                intent.putExtra("ID", (int) (idAvatar + 1L));
                startActivityForResult(intent, IntentRequests.REQ_CROP);
            }
        } else if (requestCode == IntentRequests.REQ_GALLERY && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", data.getData().toString());
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
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, avatar.getId());
                realmAvatar.setOwnerId(userId);
                realmAvatar.setFile(RealmAttachment.build(avatar.getFile(), AttachmentFor.AVATAR));
                String newFilePath = G.DIR_IMAGE_USER + "/" + avatar.getFile().getToken() + "_" + avatar.getFile().getName();
                realmAvatar.getFile().setLocalFilePath(newFilePath);

                try {
                    AndroidUtils.copyFile(new File(pathSaveImage), new File(newFilePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // have to be inside a delayed handler
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                G.onChangeUserPhotoListener.onChangePhoto(pathSaveImage);
                setImage();
            }
        }, 500);

        realm.close();
    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                circleImageView.setImageURI(Uri.fromFile(new File(uploadStructure.filePath)));
            }
        });

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
            myActivityReference.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            prg.setVisibility(View.VISIBLE);
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
            G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
        }
    }
}
