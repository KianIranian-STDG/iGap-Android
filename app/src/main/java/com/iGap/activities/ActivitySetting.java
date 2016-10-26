package com.iGap.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.iGap.fragments.FragmentShowImage;
import com.iGap.fragments.FragmentSticker;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.helper.HelperLogout;
import com.iGap.interface_package.OnFileUploadForActivities;
import com.iGap.interface_package.OnUserAvatarDelete;
import com.iGap.interface_package.OnUserAvatarResponse;
import com.iGap.interface_package.OnUserInfoResponse;
import com.iGap.interface_package.OnUserProfileSetNickNameResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.HelperDecodeFile;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmAvatarToken;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserAvatarAdd;
import com.iGap.request.RequestUserAvatarDelete;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserProfileSetNickname;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivitySetting extends ActivityEnhanced implements OnUserAvatarResponse, OnFileUploadForActivities {

    private SharedPreferences sharedPreferences;
    private int messageTextSize = 16;

    private TextView txtMenu, txtMessageTextSize, txtAutoDownloadData, txtAutoDownloadWifi, txtChatBackground,
            txtAutoDownloadRoaming, txtKeepMedia, txtLanguage, txtSizeClearCach;
    private MaterialDesignTextView imgMenu, txtBack;

    private RelativeLayout ltClearCache;

    private PopupWindow popupWindow;

    private int poRbDialogLangouage = -1;
    private String textLanguage = "English";
    private int poRbDialogTextSize = -1;

    private ViewGroup ltMessageTextSize, ltLanguage, ltInAppBrowser, ltSentByEnter, ltEnableAnimation, ltAutoGifs, ltSaveToGallery;
    private TextView txtNickName, txtUserName, txtPhoneNumber, txtNotifyAndSound, txtFaq, txtPrivacyPolicy, txtSticker;
    private ToggleButton toggleSentByEnter, toggleEnableAnimation, toggleAutoGifs, toggleSaveToGallery, toggleInAppBrowser, toggleCrop;

    private AppBarLayout appBarLayout;

    private Uri uriIntent;
    private long idAvatar;
    private File nameImageFile;
    private String pathImageDecode;
    private RealmResults<RealmAvatarPath> realmAvatarPaths;
    public static String pathSaveImage;


    private FloatingActionButton fab;
    private CircleImageView circleImageView;
    public static Bitmap decodeBitmapProfile = null;

    private String nickName;
    private String userName;
    private String phoneName;
    private long userId;

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

    private CharSequence inputText = "";

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

        final Realm realm = Realm.getDefaultInstance();
        final TextView txtNickNameTitle = (TextView) findViewById(R.id.ac_txt_nickname_title);

        txtNickName = (TextView) findViewById(R.id.st_txt_nikName);
        txtUserName = (TextView) findViewById(R.id.st_txt_userName);
        txtPhoneNumber = (TextView) findViewById(R.id.st_txt_phoneNumber);

        final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            userId = realmUserInfo.getUserId();
            nickName = realmUserInfo.getNickName();
            userName = realmUserInfo.getUserName();
            phoneName = realmUserInfo.getPhoneNumber();
        }
        if (nickName != null) {
            txtNickName.setText(nickName);
            txtNickNameTitle.setText(nickName);
        }
        if (userName != null) txtUserName.setText(userName);
        if (phoneName != null) txtPhoneNumber.setText(phoneName);


        txtNickName.setOnClickListener(new View.OnClickListener() {
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
                View viewFirstName = new View(ActivitySetting.this);
                viewFirstName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);


                TextInputLayout inputFirstName = new TextInputLayout(ActivitySetting.this);
                final EditText edtFirstName = new EditText(ActivitySetting.this);
                edtFirstName.setHint(firsName);
                edtFirstName.setPadding(0, 0, 0, 0);

                edtFirstName.setSingleLine(true);
                inputFirstName.addView(edtFirstName);
                inputFirstName.addView(viewFirstName, viewParams);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtFirstName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }

                View viewLastName = new View(ActivitySetting.this);
                viewLastName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));

                TextInputLayout inputLastName = new TextInputLayout(ActivitySetting.this);
                final EditText edtLastName = new EditText(ActivitySetting.this);
                edtLastName.setHint(lastName);
                edtLastName.setPadding(0, 0, 0, 0);
                edtLastName.setSingleLine(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtLastName.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                inputLastName.addView(edtLastName);
                inputLastName.addView(viewLastName, viewParams);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 30);

                layoutNickname.addView(inputFirstName, layoutParams);
                layoutNickname.addView(inputLastName, layoutParams);


                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this)
                        .title("Nickname")
                        .positiveText("SAVE")
                        .customView(layoutNickname, true)
                        .widgetColor(getResources().getColor(R.color.toolbar_background))
                        .negativeText("CANCEL")
                        .build();

                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setClickable(false);
                positive.setAlpha(0.5f);

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
                            positive.setClickable(true);
                            positive.setAlpha(1.0f);
                        } else {
                            positive.setClickable(false);
                            positive.setAlpha(0.5f);
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
                            positive.setClickable(true);
                            positive.setAlpha(1.0f);
                        } else {
                            positive.setClickable(false);
                            positive.setAlpha(0.5f);
                        }
                    }
                });

                final String finalLastName1 = lastName;
                final String finalFirsName1 = firsName;
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String fullName = "";
                        if (edtFirstName.length() == 0) {

                            fullName = finalFirsName1 + " " + edtLastName.getText().toString();
                        }
                        if (edtLastName.length() == 0) {
                            fullName = edtFirstName.getText().toString() + " " + finalLastName1;
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
                                                realm.where(RealmUserInfo.class).findFirst().setNickName(nickName);
                                                txtNickNameTitle.setText(nickName);
                                                FragmentDrawerMenu.txtUserName.setText(nickName);
                                                getUserInfo();
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
                        Log.i("CCCC", "fullName: " + fullName);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivitySetting.this)
                        .title("Username")
                        .positiveText("SAVE")
                        .alwaysCallInputCallback()// callback input change evrTime
                        .widgetColor(getResources().getColor(R.color.toolbar_background))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                //TODO [Saeed Mozaffari] [2016-09-10 3:51 PM] - waiting for proto

                            }
                        })
                        .negativeText("CANCEL")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input("please Enter a NickName", txtUserName.getText().toString(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                View positive = dialog.getActionButton(DialogAction.POSITIVE);

                                if (!input.toString().equals(txtUserName.getText().toString())) {

                                    positive.setClickable(true);
                                    positive.setAlpha(1.0f);
                                } else {
                                    positive.setClickable(false);
                                    positive.setAlpha(0.5f);
                                }
                            }

                        }).show();
            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.st_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                TextView titleToolbar = (TextView) findViewById(R.id.st_txt_titleToolbar);
                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.st_parentLayoutCircleImage);
                if (verticalOffset < -5) {

                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);

                } else {
                    titleToolbar.setVisibility(View.GONE);
                    viewGroup.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(0).setDuration(500);
                    viewGroup.animate().alpha(1).setDuration(700);
                }
            }
        });
        // button back in toolbar
        txtBack = (MaterialDesignTextView) findViewById(R.id.st_txt_back);
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
                TextView textView = new TextView(ActivitySetting.this);
                textView.setText("LogOut");
                textView.setTextColor(getResources().getColor(android.R.color.black));

                int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim12 = (int) getResources().getDimension(R.dimen.dp12);

                textView.setTextSize(16);
                textView.setPadding(dim20, dim12, dim12, dim12);

                layoutDialog.addView(textView, params);

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
                popupWindow.showAtLocation(layoutDialog,
                        Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));
//                popupWindow.showAsDropDown(v);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HelperLogout.logout();
                        popupWindow.dismiss();
                    }
                });
            }

        });
        imgMenu = (MaterialDesignTextView) findViewById(R.id.st_img_menuPopup);

        realmAvatarPaths = realm.where(RealmAvatarPath.class).findAll();
        //fab button for set pic
        fab = (FloatingActionButton) findViewById(R.id.st_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Realm realm = Realm.getDefaultInstance();
                RealmResults<RealmAvatarPath> realmAvatarPaths = realm.where(RealmAvatarPath.class).findAll();


                if (realmAvatarPaths.size() > 0) {
                    startDialog(R.array.profile_delete);
                } else {

                    startDialog(R.array.profile);
                }
            }
        });

        circleImageView = (CircleImageView) findViewById(R.id.st_img_circleImage);
        RippleView rippleImageView = (RippleView) findViewById(R.id.st_ripple_circleImage);
        rippleImageView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                ArrayList<StructMessageInfo> items = setItem();
                // Collections.reverse(items);

                Fragment fragment = FragmentShowImage.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("listPic", items);
                bundle.putInt("SelectedImage", 0);
                fragment.setArguments(bundle);
                ActivitySetting.this.getFragmentManager().beginTransaction().add(R.id.st_layoutParent, fragment, "Show_Image_fragment").commit();
            }

        });
        setAvatar();
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

                new MaterialDialog.Builder(ActivitySetting.this)
                        .title("Language")
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.language)
                        .itemsCallbackSingleChoice(poRbDialogLangouage, new MaterialDialog.ListCallbackSingleChoice() {
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
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
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
                final MaterialDialog dialog = new MaterialDialog.Builder(ActivitySetting.this)
                        .title("Clear Cash")
                        .customView(R.layout.st_dialog_clear_cach, wrapInScrollView)
                        .positiveText("CLEAR CASH")
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
                                if (!file.isDirectory())
                                    file.delete();
                        }
                        if (checkBoxVideo.isChecked()) {
                            for (File file : fileVideo.listFiles())
                                if (!file.isDirectory())
                                    file.delete();
                        }
                        if (checkBoxDocument.isChecked()) {
                            for (File file : fileDocument.listFiles())
                                if (!file.isDirectory())
                                    file.delete();
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

        poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 16) - 11;
        txtMessageTextSize = (TextView) findViewById(R.id.st_txt_messageTextSize_number);
        txtMessageTextSize.setText("" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 16));

        ltMessageTextSize = (ViewGroup) findViewById(R.id.st_layout_messageTextSize);
        ltMessageTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(ActivitySetting.this)
                        .title("Messages Text Size")
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
                        .positiveText("ok")
                        .show();
            }
        });

        txtSticker = (TextView) findViewById(R.id.st_txt_sticker);
        txtSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentSticker fragmentSticker = new FragmentSticker();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentSticker).commit();
            }
        });

        txtChatBackground = (TextView) findViewById(R.id.st_txt_chatBackground);
        txtChatBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivitySetting.this, ActivityChatBackground.class));
            }
        });


        ltInAppBrowser = (ViewGroup) findViewById(R.id.st_layout_inAppBrowser);
        toggleInAppBrowser = (ToggleButton) findViewById(R.id.st_toggle_inAppBrowser);
        int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);
        if (checkedInappBrowser == 1) {
            toggleInAppBrowser.setChecked(true);
        } else {
            toggleInAppBrowser.setChecked(false);
        }

        toggleInAppBrowser.setOnClickListener(new View.OnClickListener() {
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


        ltSentByEnter = (ViewGroup) findViewById(R.id.st_layout_sendEnter);
        toggleSentByEnter = (ToggleButton) findViewById(R.id.st_toggle_sendEnter);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        if (checkedSendByEnter == 1) {
            toggleSentByEnter.setChecked(true);
        } else {
            toggleSentByEnter.setChecked(false);
        }

        toggleSentByEnter.setOnClickListener(new View.OnClickListener() {
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

                new MaterialDialog.Builder(ActivitySetting.this)
                        .title(R.string.st_keepMedia)
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

                new MaterialDialog.Builder(ActivitySetting.this)
                        .title(R.string.st_auto_download_data)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(new Integer[]{KEY_AD_DATA_PHOTO
                                , KEY_AD_DATA_VOICE_MESSAGE
                                , KEY_AD_DATA_VIDEO
                                , KEY_AD_DATA_FILE
                                , KEY_AD_DATA_MUSIC
                                , KEY_AD_DATA_GIF}, new MaterialDialog.ListCallbackMultiChoice() {
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
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();
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
                new MaterialDialog.Builder(ActivitySetting.this)
                        .title(R.string.st_auto_download_wifi)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(new Integer[]{KEY_AD_WIFI_PHOTO
                                , KEY_AD_WIFI_VOICE_MESSAGE
                                , KEY_AD_WIFI_VIDEO
                                , KEY_AD_WIFI_FILE
                                , KEY_AD_WIFI_MUSIC
                                , KEY_AD_WIFI_GIF}, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

//
                                for (int i = 0; i < which.length; i++) {


                                    if (which[i] == 0) {

                                        KEY_AD_WIFI_PHOTO = which[i];
                                    } else if (which[i] == 1) {
                                        KEY_AD_WIFI_VOICE_MESSAGE = which[i];
                                    } else if (which[i] == 2) {
                                        KEY_AD_WIFI_VIDEO = which[i];
                                    } else if (which[i] == 3) {
                                        KEY_AD_WIFI_FILE = which[i];
                                    } else if (which[i] == 4) {
                                        KEY_AD_WIFI_MUSIC = which[i];
                                    } else if (which[i] == 5) {
                                        KEY_AD_WIFI_GIF = which[i];
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
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();
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
                new MaterialDialog.Builder(ActivitySetting.this)
                        .title(R.string.st_auto_download_roaming)
                        .items(R.array.auto_download_data)
                        .itemsCallbackMultiChoice(new Integer[]{KEY_AD_ROAMING_PHOTO
                                , KEY_AD_ROAMING_VOICE_MESSAGE
                                , KEY_AD_ROAMING_VIDEO
                                , KEY_AD_ROAMING_FILE
                                , KEY_AD_ROAMING_MUSIC
                                , KEY_AD_ROAMINGN_GIF}, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

//
                                for (int i = 0; i < which.length; i++) {

                                    if (which[i] == 0) {
                                        KEY_AD_ROAMING_PHOTO = which[i];
                                    } else if (which[i] == 1) {
                                        KEY_AD_ROAMING_VOICE_MESSAGE = which[i];
                                    } else if (which[i] == 2) {
                                        KEY_AD_ROAMING_VIDEO = which[i];
                                    } else if (which[i] == 3) {
                                        KEY_AD_ROAMING_FILE = which[i];
                                    } else if (which[i] == 4) {
                                        KEY_AD_ROAMING_MUSIC = which[i];
                                    } else if (which[i] == 5) {
                                        KEY_AD_ROAMINGN_GIF = which[i];
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
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();
            }
        });

        ltEnableAnimation = (ViewGroup) findViewById(R.id.st_layout_enableAnimation);
        toggleEnableAnimation = (ToggleButton) findViewById(R.id.st_toggle_enableAnimation);
        int checkedEnableAnimation = sharedPreferences.getInt(SHP_SETTING.KEY_ENABLE_ANIMATION, 0);
        if (checkedEnableAnimation == 1) {
            toggleEnableAnimation.setChecked(true);
        } else {
            toggleEnableAnimation.setChecked(false);
        }

        toggleEnableAnimation.setOnClickListener(new View.OnClickListener() {
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

        ltAutoGifs = (ViewGroup) findViewById(R.id.st_layout_autoGif);
        toggleAutoGifs = (ToggleButton) findViewById(R.id.st_toggle_autoGif);
        int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
        if (checkedAutoGif == 1) {
            toggleAutoGifs.setChecked(true);
        } else {
            toggleAutoGifs.setChecked(false);
        }

        toggleAutoGifs.setOnClickListener(new View.OnClickListener() {
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

        ltSaveToGallery = (ViewGroup) findViewById(R.id.st_layout_saveGallery);
        toggleSaveToGallery = (ToggleButton) findViewById(R.id.st_toggle_saveGallery);
        int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        if (checkedSaveToGallery == 1) {
            toggleSaveToGallery.setChecked(true);

        } else {
            toggleSaveToGallery.setChecked(false);
        }

        toggleSaveToGallery.setOnClickListener(new View.OnClickListener() {
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
    }

    private void getUserInfo() {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, ProtoResponse.Response response) {

                // if response is for own user do this action
                if (user.getId() == userId) {

                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.where(RealmUserInfo.class).findFirst().setInitials(user.getInitials());
                        }
                    });
                    realm.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setInitials(user.getInitials(), user.getColor());
                        }
                    });
                }
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError() {

            }
        };

        new RequestUserInfo().userInfo(userId);
    }

    //dialog for choose pic from gallery or camera
    private void startDialog(int r) {

        new MaterialDialog.Builder(this)
                .title("Choose Picture")
                .negativeText("CANCEL")
                .items(r)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {


                        if (text.toString().equals("From Camera")) {

                            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                                idAvatar = System.nanoTime();
                                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                                nameImageFile = new File(pathSaveImage);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                uriIntent = Uri.fromFile(nameImageFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                startActivityForResult(intent, IntentRequests.REQ_CAMERA);
//                                realm.close();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(ActivitySetting.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                            }
                        } else if (text.toString().equals("Delete photo")) {

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
                                                    for (RealmAvatarPath avatarPath : realm.where(RealmAvatarPath.class).findAll()) {
                                                        Log.i("XXX", "RealmAvatarPath 4 avatarPath.getId() : " + avatarPath.getId());
                                                        if (avatarId == avatarPath.getId()) {
                                                            new File(avatarPath.getPathImage()).delete();
                                                            avatarPath.deleteFromRealm();

                                                            //realm.where(RealmAvatarToken.class).equalTo(RealmAvatarTokenFields.TOKEN, token).findFirst().deleteFromRealm();
                                                        }
                                                    }
                                                }
                                            });
                                            realm.close();
                                            setAvatar();
                                        }
                                    });
                                }
                            };
                            Realm realm1 = Realm.getDefaultInstance();
                            RealmResults<RealmAvatarPath> realmAvatarPaths = realm1.where(RealmAvatarPath.class).findAll();
                            realmAvatarPaths = realmAvatarPaths.sort("id", Sort.DESCENDING);
                            Log.i("XXX", "RequestUserAvatarDelete 1 avatarId : " + realmAvatarPaths.first().getId());

//                            RealmAvatarToken realmAvatarToken = realm1.where(RealmAvatarToken.class).equalTo(RealmAvatarTokenFields.ID, realmAvatarPaths.first().getId()).findFirst();
//                            realmAvatarToken.getToken();
//                            Log.i("XXX", "RequestUserAvatarDelete 1 realmAvatarToken.getToken() : " + realmAvatarToken.getToken());
//
//                             /*
//                              * set token for identity , when i get response fetch RealmAvatarToken
//                              * with this identity(token) and delete that row from RealmAvatarToken
//                              * */

                            new RequestUserAvatarDelete().userAvatarDelete(realmAvatarPaths.first().getId(), "");
                            realm1.close();

                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            idAvatar = System.nanoTime();
                            startActivityForResult(intent, IntentRequests.REQ_GALLERY);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    private long lastUploadedAvatarId;

    //=====================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentRequests.REQ_CAMERA && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
            if (uriIntent != null) {

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
//            getIndexRealm();
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    RealmAvatarPath realmAvatarPath = realm.createObject(RealmAvatarPath.class);
                    realmAvatarPath.setId((int) (idAvatar + 1L));
                    Log.i("CCC", "pathSaveImage : " + pathSaveImage);
                    realmAvatarPath.setPathImage(pathSaveImage);
                    realmUserInfo.getAvatarPath().add(realmAvatarPath);

                }
            });
            realm.close();
            setAvatar();

            lastUploadedAvatarId = idAvatar + 1L;

            G.onChangeUserPhotoListener.onChangePhoto(pathSaveImage);
            new UploadTask().execute(pathSaveImage, lastUploadedAvatarId);

        }
    }

    // change language
    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, ActivitySetting.class);
        startActivity(refresh);
        finish();
    }

    public static long getFolderSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                System.out.println(file.getName() + " " + file.length());
                size += file.length();
            } else
                size += getFolderSize(file);
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

    public void setAvatar() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatarPath> realmAvatarPaths = realm.where(RealmAvatarPath.class).findAll();
        realmAvatarPaths = realmAvatarPaths.sort("id", Sort.DESCENDING);
        if (realmAvatarPaths.size() > 0) {
            pathImageDecode = realmAvatarPaths.first().getPathImage();
            decodeBitmapProfile = HelperDecodeFile.decodeFile(new File(pathImageDecode));
            circleImageView.setImageBitmap(decodeBitmapProfile);
            G.onChangeUserPhotoListener.onChangePhoto(pathImageDecode);
        } else {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp88), realmUserInfo.getInitials(), realmUserInfo.getColor()));
            G.onChangeUserPhotoListener.onChangePhoto(null);
        }
        realm.close();
    }

    private void setInitials(String initials, String color) {
        Log.i("VVV", "initials : " + initials);
        Log.i("VVV", "color : " + color);
        circleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) circleImageView.getContext().getResources().getDimension(R.dimen.dp88), initials, color));
        G.onChangeUserPhotoListener.onChangeInitials(initials, color);
    }

    public ArrayList<StructMessageInfo> setItem() {

        ArrayList<StructMessageInfo> items = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatarPath> realmItemList = realm.where(RealmAvatarPath.class).findAll();
        realmItemList = realmItemList.sort("id", Sort.DESCENDING);
        for (int i = 0; i < realmItemList.size(); i++) {
            StructMessageInfo item = new StructMessageInfo();
            item.filePath = realmItemList.get(i).getPathImage();
            item.messageID = realmItemList.get(i).getId() + "";
            items.add(item);
        }

        return items;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentShowImage myFragment = (FragmentShowImage) getFragmentManager().findFragmentByTag("Show_Image_fragment");
        if (myFragment != null && myFragment.isVisible()) {
            getFragmentManager().beginTransaction().remove(myFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                RealmAvatarToken realmAvatarPath = realm.createObject(RealmAvatarToken.class);
                realmAvatarPath.setId((int) lastUploadedAvatarId);
                realmAvatarPath.setToken(avatar.getFile().getToken());
                realmUserInfo.addAvatarToken(realmAvatarPath);
            }
        });
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

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
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
            G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
        }
    }
}
