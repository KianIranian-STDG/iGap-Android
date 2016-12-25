package com.iGap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.iGap.helper.HelperFillLookUpClass;
import com.iGap.helper.HelperNotificationAndBadge;
import com.iGap.helper.MyService;
import com.iGap.interfaces.*;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.ChatUpdateStatusUtil;
import com.iGap.module.ClearMessagesUtil;
import com.iGap.module.Contacts;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.UploaderUtil;
import com.iGap.module.enums.ConnectionMode;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmMigrationClass;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientCondition;
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestUserContactsGetList;
import com.iGap.request.RequestUserInfo;
import com.iGap.request.RequestUserLogin;
import com.iGap.request.RequestWrapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.spec.SecretKeySpec;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class G extends MultiDexApplication {

    public static final String FAQ = "http://www.digikala.com";
    public static final String POLICY = "http://www.digikala.com";
    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP = DIR_SDCARD + "/iGap";
    public static final String DIR_IMAGES = DIR_APP + "/iGap Images";
    public static final String DIR_VIDEOS = DIR_APP + "/iGap Videos";
    public static final String DIR_AUDIOS = DIR_APP + "/iGap Audios";
    public static final String DIR_DOCUMENT = DIR_APP + "/iGap Document";
    public static final String DIR_TEMP = DIR_APP + "/.temp";
    public static final String DIR_SOUND_NOTIFICATION = DIR_APP + "/.sound";
    public static final String DIR_CHAT_BACKGROUND = DIR_APP + "/.chat_background";
    public static final String DIR_NEW_GROUP = DIR_APP + "/.new_group";
    public static final String DIR_NEW_CHANEL = DIR_APP + "/.new_chanel";
    public static final String DIR_ALL_IMAGE_USER_CONTACT = DIR_APP + "/.all_image_user_contact";
    public static final String DIR_IMAGE_USER = DIR_APP + "/.image_user";

    public static final String CHAT_MESSAGE_TIME = "H:mm";
    public static final String ROOM_LAST_MESSAGE_TIME = "h:mm a";
    public static Typeface neuroplp;
    // list of actionId that can be doing without login
    public static Typeface arial;
    public static Typeface iranSans;
    public static Typeface fontawesome;
    public static Typeface flaticon;
    public static Context context;
    public static Handler handler;
    public static HelperNotificationAndBadge helperNotificationAndBadge;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    public static boolean isUserStatusOnline = false;
    public static ArrayList<String> unSecure = new ArrayList<>();
    // list of actionId that can be doing without secure
    public static ArrayList<String> unLogin = new ArrayList<>();
    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();
    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static boolean isSecure = false;
    public static boolean allowForConnect = true;
    //TODO [Saeed Mozaffari] [2016-08-18 12:09 PM] - set allowForConnect to realm
    public static boolean userLogin = false;
    public static boolean socketConnection = false;
    public static boolean canRunReceiver = false;
    public static SecretKeySpec symmetricKey;
    public static String symmetricMethod;
    public static int ivSize;
    public static int userTextSize = 0;
    public static Activity currentActivity;
    public static LayoutInflater inflater;
    public static Typeface FONT_IGAP;
    public static Typeface HELETICBLK_TITR;
    public static List<String> downloadingTokens = new ArrayList<>();

    public static int IMAGE_CORNER;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Typeface ARIAL_TEXT;
    public static Typeface YEKAN_FARSI;
    public static Typeface YEKAN_BOLD;
    public static File imageFile;
    public static int COPY_BUFFER_SIZE = 1024;
    public static UploaderUtil uploaderUtil = new UploaderUtil();
    public static ClearMessagesUtil clearMessagesUtil = new ClearMessagesUtil();
    public static ChatSendMessageUtil chatSendMessageUtil = new ChatSendMessageUtil();
    public static ChatUpdateStatusUtil chatUpdateStatusUtil = new ChatUpdateStatusUtil();
    public static OnFileUploadStatusResponse onFileUploadStatusResponse;
    public static Config.ConnectionState connectionState;
    public static OnConnectionChangeState onConnectionChangeState;
    public static OnReceiveInfoLocation onReceiveInfoLocation;
    public static OnUserRegistration onUserRegistration;
    public static OnClientSearchRoomHistory onClientSearchRoomHistory;
    public static OnUserVerification onUserVerification;
    public static OnReceivePageInfoTOS onReceivePageInfoTOS;
    public static OnUserLogin onUserLogin;
    public static OnUserProfileSetEmailResponse onUserProfileSetEmailResponse;
    public static OnUserProfileSetGenderResponse onUserProfileSetGenderResponse;
    public static OnUserProfileSetNickNameResponse onUserProfileSetNickNameResponse;
    public static OnInfoCountryResponse onInfoCountryResponse;
    public static OnInfoTime onInfoTime;
    public static OnUserContactImport onContactImport;
    public static OnUserContactEdit onUserContactEdit;
    public static OnUserContactGetList onUserContactGetList;
    public static OnUserContactDelete onUserContactdelete;
    public static OnClientGetRoomListResponse onClientGetRoomListResponse;
    public static OnClientGetRoomResponse onClientGetRoomResponse;
    public static OnSecuring onSecuring;
    public static OnChatGetRoom onChatGetRoom;
    public static OnChatEditMessageResponse onChatEditMessageResponse;
    public static OnChatDeleteMessageResponse onChatDeleteMessageResponse;
    public static OnChatDelete onChatDelete;
    public static OnUserUsernameToId onUserUsernameToId;
    public static OnUserProfileGetEmail onUserProfileGetEmail;
    public static OnUserProfileGetGender onUserProfileGetGender;
    public static OnUserProfileGetNickname onUserProfileGetNickname;
    public static OnGroupCreate onGroupCreate;
    public static OnGroupAddMember onGroupAddMember;
    public static OnGroupAddAdmin onGroupAddAdmin;
    public static OnGroupAddModerator onGroupAddModerator;
    public static OnGroupClearMessage onGroupClearMessage;
    public static OnGroupEdit onGroupEdit;
    public static OnGroupKickAdmin onGroupKickAdmin;
    public static OnGroupKickMember onGroupKickMember;
    public static OnGroupKickModerator onGroupKickModerator;
    public static OnGroupLeft onGroupLeft;
    public static OnFileDownloadResponse onFileDownloadResponse;
    public static OnUserInfoResponse onUserInfoResponse;
    public static OnUserAvatarResponse onUserAvatarResponse;
    public static OnGroupAvatarResponse onGroupAvatarResponse;
    public static OnChangeUserPhotoListener onChangeUserPhotoListener;
    public static OnClearChatHistory onClearChatHistory;
    public static OnDeleteChatFinishActivity onDeleteChatFinishActivity;
    public static OnClientGetRoomHistoryResponse onClientGetRoomHistoryResponse;
    public static OnUserAvatarDelete onUserAvatarDelete;
    public static OnUserAvatarGetList onUserAvatarGetList;
    public static OnDraftMessage onDraftMessage;
    public static OnUserDelete onUserDelete;
    public static OnUserProfileSetSelfRemove onUserProfileSetSelfRemove;
    public static OnUserProfileGetSelfRemove onUserProfileGetSelfRemove;
    public static OnUserProfileCheckUsername onUserProfileCheckUsername;
    public static OnUserProfileUpdateUsername onUserProfileUpdateUsername;
    public static OnGroupGetMemberList onGroupGetMemberList;
    public static OnUserGetDeleteToken onUserGetDeleteToken;
    public static OnGroupDelete onGroupDelete;
    public static OpenFragment onConvertToGroup;
    public static OnChatConvertToGroup onChatConvertToGroup;
    public static OnUserUpdateStatus onUserUpdateStatus;
    public static OnUpdateUserStatusInChangePage onUpdateUserStatusInChangePage;
    public static OnLastSeenUpdateTiming onLastSeenUpdateTiming;
    public static OnSetAction onSetAction;
    public static OnSetActionInRoom onSetActionInRoom;
    public static OnUserSessionGetActiveList onUserSessionGetActiveList;
    public static OnUserSessionTerminate onUserSessionTerminate;
    public static OnUserSessionLogout onUserSessionLogout;
    public static UpdateListAfterKick updateListAfterKick;
    public static OnHelperSetAction onHelperSetAction;
    public static OnChannelCreate onChannelCreate;
    public static OnChannelDelete onChannelDelete;
    public static OnChannelLeft onChannelLeft;
    public static OnChannelAddMember onChannelAddMember;
    public static OnChannelKickMember onChannelKickMember;
    public static OnChannelAddAdmin onChannelAddAdmin;
    public static OnChannelKickAdmin onChannelKickAdmin;
    public static OnChannelAddModerator onChannelAddModerator;
    public static OnChannelKickModerator onChannelKickModerator;
    public static OnChannelGetMemberList onChannelGetMemberList;
    public static OnChannelEdit onChannelEdit;
    public static OnChannelAvatarAdd onChannelAvatarAdd;
    public static OnChannelAvatarDelete onChannelAvatarDelete;
    public static OnChannelCheckUsername onChannelCheckUsername;
    public static OnGroupCheckUsername onGroupCheckUsername;
    public static OnGroupUpdateUsername onGroupUpdateUsername;
    public static OnChannelUpdateUsername onChannelUpdateUsername;
    public static OnGroupAvatarDelete onGroupAvatarDelete;
    public static OnRefreshActivity onRefreshActivity;
    public static OnGetUserInfo onGetUserInfo;
    public static OnFileDownloaded onFileDownloaded;
    public static OnUserInfoMyClient onUserInfoMyClient;
    public static OnChannelAddMessageReaction onChannelAddMessageReaction;
    public static OnChannelGetMessagesStats onChannelGetMessagesStats;
    public static OnChannelRemoveUsername onChannelRemoveUsername;
    public static OnChannelRevokeLink onChannelRevokeLink;
    public static OnChannelUpdateSignature onChannelUpdateSignature;
    public static OnClientCheckInviteLink onClientCheckInviteLink;
    public static OnClientGetRoomMessage onClientGetRoomMessage;
    public static OnClientJoinByInviteLink onClientJoinByInviteLink;
    public static OnClientJoinByUsername onClientJoinByUsername;
    public static OnClientResolveUsername onClientResolveUsername;
    public static OnClientSubscribeToRoom onClientSubscribeToRoom;
    public static OnClientUnsubscribeFromRoom onClientUnsubscribeFromRoom;
    public static OnGroupRemoveUsername onGroupRemoveUsername;
    public static OnGroupRevokeLink onGroupRevokeLink;
    public static OnUpdateAvatar onUpdateAvatar;


    public static File chatBackground;
    public static File IMAGE_NEW_GROUP;
    public static File IMAGE_NEW_CHANEL;
    public static ConnectionMode connectionMode;
    public static boolean isNetworkRoaming;

    public static ConcurrentHashMap<Long, RequestWrapper> currentUploadFiles = new ConcurrentHashMap<>();

    public static void setUserTextSize() {

        SharedPreferences sharedPreferencesSetting = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        userTextSize = sharedPreferencesSetting.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14);

        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                userTextSize = (userTextSize * 3) / 4;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                userTextSize = (userTextSize * 3) / 2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:// or 4
                userTextSize *= 2;
        }
    }

    public static void importContact() {

        G.onContactImport = new OnUserContactImport() {
            @Override
            public void onContactImport() {
                getContactListFromServer();
            }

        };


        // this can be go in the activity for cheke permision in api 6+
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Contacts.getListOfContact(true);
        }


    }

    private static void getContactListFromServer() {
        G.onUserContactGetList = new OnUserContactGetList() {
            @Override
            public void onContactGetList() {

            }
        };

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            new RequestUserContactsGetList().userContactGetList();
        }
    }

    public static void getUserInfo() {
        //TODO [Saeed Mozaffari] [2016-10-15 1:51 PM] - nabayad har bar etella'ate khodam ro
        // begiram. agar ham digar account taghiri dadae bashe response hamun zaman miayad va man
        // ba accountam yeki misham
        //TODO [Saeed Mozaffari] [2016-10-15 1:52 PM] - bayad zamani ke register kardam userInfo
        // ro begiram , fekr nemikonam ke deige niaz be har bar gereftan bashe
        Realm realm = Realm.getDefaultInstance();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        realm.close();

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                // fill own user info
                if (userId == user.getId()) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmAvatar avatar = RealmAvatar.put(user.getId(), user.getAvatar(), true);

                            RealmRegisteredInfo.putOrUpdate(user);

                            /*if (G.onChangeUserPhotoListener != null) {
                                G.onChangeUserPhotoListener.onChangeInitials(user.getInitials(), user.getColor());
                            }
*/
                            /*if (avatar != null && avatar.isValid()) {
                                if (!avatar.getFile().isFileExistsOnLocal() && !avatar.getFile().isThumbnailExistsOnLocal()) {
                                    requestDownloadAvatar(false, avatar.getFile().getToken(), avatar.getFile().getName(), (int) avatar.getFile().getSmallThumbnail().getSize());
                                } else {
                                    if (avatar.getFile().isFileExistsOnLocal()) {
                                        if (G.onChangeUserPhotoListener != null) {
                                            G.onChangeUserPhotoListener.onChangePhoto(avatar.getFile().getLocalFilePath());
                                        }
                                    } else if (avatar.getFile().isThumbnailExistsOnLocal()) {
                                        if (G.onChangeUserPhotoListener != null) {
                                            G.onChangeUserPhotoListener.onChangePhoto(avatar.getFile().getLocalThumbnailPath());
                                        }
                                    }
                                }
                            }*/
                        }
                    });

                    realm.close();
                }
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
        new RequestUserInfo().userInfo(userId);
    }

    @Override
    public void onCreate() { // comment
        super.onCreate();
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        SharedPreferences shKeepAlive = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int isStart = shKeepAlive.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
        if (isStart == 1) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

        setFont();

        new File(DIR_APP).mkdirs();
        new File(DIR_IMAGES).mkdirs();
        new File(DIR_VIDEOS).mkdirs();
        new File(DIR_AUDIOS).mkdirs();
        new File(DIR_DOCUMENT).mkdirs();
        new File(DIR_SOUND_NOTIFICATION).mkdirs();
        new File(DIR_CHAT_BACKGROUND).mkdirs();
        new File(DIR_NEW_GROUP).mkdirs();
        new File(DIR_NEW_CHANEL).mkdirs();
        new File(DIR_ALL_IMAGE_USER_CONTACT).mkdirs();
        new File(DIR_IMAGE_USER).mkdirs();
        new File(DIR_TEMP).mkdirs();

        chatBackground = new File(DIR_CHAT_BACKGROUND, "addChatBackground.jpg");
        IMAGE_NEW_GROUP = new File(G.DIR_NEW_GROUP, "image_new_group.jpg");
        IMAGE_NEW_CHANEL = new File(G.DIR_NEW_CHANEL, "image_new_chanel.jpg");
        imageFile = new File(DIR_IMAGE_USER, "image_user");

        context = getApplicationContext();
        handler = new Handler();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        helperNotificationAndBadge = new HelperNotificationAndBadge();

        HelperFillLookUpClass.fillLookUpClassArray();
        fillUnSecureList();
        fillUnLoginList();
        fillSecuringInterface();
        WebSocketClient.getInstance();

        iranSans = Typeface.createFromAsset(this.getAssets(), "fonts/IRANSansMobile.ttf");
        flaticon = Typeface.createFromAsset(this.getAssets(), "fonts/Flaticon.ttf");
        FONT_IGAP = Typeface.createFromAsset(context.getAssets(), "fonts/neuropolitical.ttf");


        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(getApplicationContext()).name("iGapLocalDatabase.realm").schemaVersion(1).migration(new RealmMigrationClass()).deleteRealmIfMigrationNeeded().build());

        // Create global configuration and initialize ImageLoader with this config
        // https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Configuration
        // https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Display-Options
        // https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).build());

        FONT_IGAP = Typeface.createFromAsset(context.getAssets(), "fonts/neuropolitical.ttf");

        SharedPreferences sharedPreferences = getSharedPreferences("CopyDataBase", MODE_PRIVATE);
        boolean isCopyFromAsset = sharedPreferences.getBoolean("isCopyRealm", true);

        if (isCopyFromAsset) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isCopyRealm", false);
            editor.apply();
            try {
                copyFromAsset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setUserTextSize();

        //G.onFileDownloadResponse = this;
    }


    public void setFont() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        String language = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, "en");

        switch (language) {
            case "فارسی":
                setLocale("fa");

                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());

                break;
            case "English":
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());
                setLocale("en");
                break;
            case "العربی":
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());
                setLocale("ar");

                break;
            case "Deutsch":
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());
                setLocale("nl");

                break;
            default:
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());
                break;

        }
    }

    public void setLocale(String lang) {


        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    private void copyFromAsset() throws IOException {
        InputStream inputStream = getAssets().open("CountryListA.realm");
        Realm realm = Realm.getDefaultInstance();
        String outFileName = realm.getPath();
        OutputStream outputStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        realm.close();
    }

    /**
     * list of actionId that can be doing without secure
     */
    private void fillUnSecureList() {
        unSecure.add("2");
    }

    /**
     * list of actionId that can be doing without login
     */
    private void fillUnLoginList() {
        unLogin.add("100");
        unLogin.add("101");
        unLogin.add("102");
        unLogin.add("500");
        unLogin.add("501");
        unLogin.add("502");
        unLogin.add("503");
    }

    private void fillSecuringInterface() {
        G.onSecuring = new OnSecuring() {
            @Override
            public void onSecure() {
                login();

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                switch (activeNetwork.getType()) {
                    case ConnectivityManager.TYPE_WIFI:

                        connectionMode = ConnectionMode.WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        connectionMode = ConnectionMode.MOBILE;
                        break;
                    case ConnectivityManager.TYPE_WIMAX:
                        connectionMode = ConnectionMode.WIMAX;
                        break;
                }

                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (tm.isNetworkRoaming()) {
                    isNetworkRoaming = tm.isNetworkRoaming();
                }
            }
        };
    }

    public static void sendWaitingRequestWrappers() {
        for (RequestWrapper requestWrapper : RequestQueue.WAITING_REQUEST_WRAPPERS) {
            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void login() { //TODO [Saeed Mozaffari] [2016-09-07 10:24 AM] - mitonim karhaie ke
        // hamishe bad az login bayad anjam beshe ro dar classe login response gharar bedim

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new RequestClientCondition().clientCondition();
                        getUserInfo();
                        importContact();
                        sendWaitingRequestWrappers();
                    }
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {

            }
        };

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (!G.userLogin && userInfo != null && userInfo.getUserRegistrationState()) {
                        new RequestUserLogin().userLogin(userInfo.getToken());
                    }
                    realm.close();
                } else {
                    Log.i("TTT", "Not Secure");
                    login();
                }
            }
        }, 1000);
    }
}
