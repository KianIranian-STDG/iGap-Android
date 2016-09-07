package com.iGap.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.HelperDecodeFile;
import com.iGap.module.SHP_SETTING;
import com.iGap.realm.RealmUserInfo;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class ActivitySetting extends ActivityEnhanced {

    private SharedPreferences sharedPreferences;
    private int messageTextSize = 16;

    private TextView txtBack, txtMenu, txtMessageTextSize, txtAutoDownloadData, txtAutoDownloadWifi, txtAutoDownloadRoaming, txtKeepMedia, txtLanguage;
    private ImageView imgMenu;

    private int poRbDialogLangouage = -1;
    private String textLanguage = "English";
    private int poRbDialogTextSize = -1;

    private ViewGroup ltMessageTextSize, ltLanguage, ltInAppBrowser, ltSentByEnter, ltEnableAnimation, ltAutoGifs, ltSaveToGallery;
    private EditText edtNickName, edtUserName, edtPhoneNumber;
    private ToggleButton toggleSentByEnter, toggleEnableAnimation, toggleAutoGifs, toggleSaveToGallery, toggleInAppBrowser;


    private AppBarLayout appBarLayout;

    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private Uri uriIntent;

    private FloatingActionButton fab;
    private CircleImageView circleImageView;
    public static Bitmap decodeBitmapProfile = null;

    private String nickName;
    private String userName;
    private String phoneName;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        final Realm realm = Realm.getDefaultInstance();

        final TextView txtNickNameTitle = (TextView) findViewById(R.id.ac_txt_nickname_title);

        edtNickName = (EditText) findViewById(R.id.st_edt_nikName);
        edtUserName = (EditText) findViewById(R.id.st_edt_userName);
        edtPhoneNumber = (EditText) findViewById(R.id.st_edt_phoneNumber);

        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            nickName = realmUserInfo.getNickName();
            userName = realmUserInfo.getUserName();
            phoneName = realmUserInfo.getPhoneNumber();
        }


        if (nickName != null) {
            edtNickName.setText(nickName);
            txtNickNameTitle.setText(nickName);
        }
        if (userName != null) edtUserName.setText(userName);
        if (phoneName != null) edtPhoneNumber.setText(phoneName);

        edtNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (!editable.toString().equals("")) {
                            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                            userInfo.setNickName(editable.toString());
                            txtNickNameTitle.setText(nickName);
                        }
                    }
                });
            }
        });

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(final Editable editable) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                    }
                });
            }
        });

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                    }
                });

            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.st_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                TextView titleToolbar = (TextView) findViewById(R.id.st_txt_titleToolbar);
                if (verticalOffset < -1) {

                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleToolbar.setVisibility(View.VISIBLE);

                } else {
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleToolbar.setVisibility(View.GONE);
                }
            }
        });

        // button back in toolbar
        txtBack = (TextView) findViewById(R.id.st_txt_back);
        txtBack.setTypeface(G.fontawesome);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // button popupMenu in toolbar
        imgMenu = (ImageView) findViewById(R.id.st_img_menuPopup);
        assert txtMenu != null;
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(ActivitySetting.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.album_overflow_rename:
                                Toast.makeText(ActivitySetting.this, "1", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.album_overflow_lock:
                                Toast.makeText(ActivitySetting.this, "2", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return true;
                    }
                });

                popupMenu.inflate(R.menu.sc_popup_menu);
                popupMenu.show();
            }
        });

        //fab button for set pic
        fab = (FloatingActionButton) findViewById(R.id.st_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });

        circleImageView = (CircleImageView) findViewById(R.id.st_img_circleImage);
        if (G.imageFile.exists()) {
            decodeBitmapProfile = HelperDecodeFile.decodeFile(G.imageFile);
            circleImageView.setImageBitmap(decodeBitmapProfile);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    realmUserInfo.setAvatarPath(G.imageFile.toString());
                }
            });

        } else {
            circleImageView.setImageResource(R.mipmap.b);
        }



        textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, "English");
        if (textLanguage.equals("English")) {
            poRbDialogLangouage = 0;
            Toast.makeText(ActivitySetting.this, "" + poRbDialogTextSize, Toast.LENGTH_SHORT).show();
        } else if (textLanguage.equals("فارسی")) {
            poRbDialogLangouage = 1;
            Toast.makeText(ActivitySetting.this, "" + poRbDialogTextSize, Toast.LENGTH_SHORT).show();

        } else if (textLanguage.equals("العربی")) {
            poRbDialogLangouage = 2;
            Toast.makeText(ActivitySetting.this, "" + poRbDialogTextSize, Toast.LENGTH_SHORT).show();

        } else if (textLanguage.equals("Deutsch")) {
            poRbDialogLangouage = 3;
            Toast.makeText(ActivitySetting.this, "" + poRbDialogTextSize, Toast.LENGTH_SHORT).show();
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
                                return false;
                            }
                        })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();

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

                                return false;
                            }
                        })
                        .positiveText("ok")
                        .show();
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


        ltSentByEnter = (ViewGroup) findViewById(R.id.st_layout_sendEnter);
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

                new MaterialDialog.Builder(ActivitySetting.this)
                        .title(R.string.st_keepMedia)
                        .content(R.string.st_dialog_content_keepMedia)
                        .positiveText("ForEver")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(ActivitySetting.this, "ForEver", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .negativeText("1WEEk")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Toast.makeText(ActivitySetting.this, "1WEEk", Toast.LENGTH_SHORT).show();
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

        realm.close();

    }

    //dialog for choose pic from gallery or camera
    private void startDialog() {
        final Dialog dialog = new Dialog(ActivitySetting.this);
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
                    Toast.makeText(ActivitySetting.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView picGallery = (TextView) dialog.findViewById(R.id.pu_layout_dialog_picGallery);
        picGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, myResultCodeGallery);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //=====================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", "setting");
            startActivity(intent);
            finish();

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "setting");
            startActivity(intent);
            finish();
        }
    }
}
