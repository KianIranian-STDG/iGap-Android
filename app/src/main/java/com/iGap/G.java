package com.iGap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.iGap.helper.HelperFillLookUpClass;
import com.iGap.helper.HelperNotificationAndBadge;
import com.iGap.helper.MyService;
import com.iGap.interface_package.OnChatDelete;
import com.iGap.interface_package.OnChatDeleteMessageResponse;
import com.iGap.interface_package.OnChatEditMessageResponse;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.interface_package.OnClientGetRoomListResponse;
import com.iGap.interface_package.OnClientGetRoomResponse;
import com.iGap.interface_package.OnConnectionChangeState;
import com.iGap.interface_package.OnFileDownloadResponse;
import com.iGap.interface_package.OnFileUploadStatusResponse;
import com.iGap.interface_package.OnGroupAddAdmin;
import com.iGap.interface_package.OnGroupAddMember;
import com.iGap.interface_package.OnGroupAddModerator;
import com.iGap.interface_package.OnGroupClearMessage;
import com.iGap.interface_package.OnGroupCreate;
import com.iGap.interface_package.OnGroupEdit;
import com.iGap.interface_package.OnGroupKickAdmin;
import com.iGap.interface_package.OnGroupKickMember;
import com.iGap.interface_package.OnGroupKickModerator;
import com.iGap.interface_package.OnGroupLeft;
import com.iGap.interface_package.OnInfoCountryResponse;
import com.iGap.interface_package.OnInfoTime;
import com.iGap.interface_package.OnReceiveInfoLocation;
import com.iGap.interface_package.OnReceivePageInfoTOS;
import com.iGap.interface_package.OnSecuring;
import com.iGap.interface_package.OnUserContactDelete;
import com.iGap.interface_package.OnUserContactEdit;
import com.iGap.interface_package.OnUserContactGetList;
import com.iGap.interface_package.OnUserContactImport;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.interface_package.OnUserProfileGetEmail;
import com.iGap.interface_package.OnUserProfileGetGender;
import com.iGap.interface_package.OnUserProfileGetNickname;
import com.iGap.interface_package.OnUserProfileSetEmailResponse;
import com.iGap.interface_package.OnUserProfileSetGenderResponse;
import com.iGap.interface_package.OnUserProfileSetNickNameResponse;
import com.iGap.interface_package.OnUserRegistration;
import com.iGap.interface_package.OnUserUsernameToId;
import com.iGap.interface_package.OnUserVerification;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.ChatUpdateStatusUtil;
import com.iGap.module.ClearMessagesUtil;
import com.iGap.module.Contacts;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.UploaderUtil;
import com.iGap.realm.RealmMigrationClass;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientCondition;
import com.iGap.request.RequestUserContactsGetList;
import com.iGap.request.RequestUserLogin;
import com.iGap.request.RequestWrapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class G extends Application {

    public static Typeface neuroplp;
    public static Typeface robotoBold;
    public static Typeface robotoLight;
    public static Typeface robotoRegular;
    public static Typeface arialBold;
    public static Typeface arial;
    public static Typeface iranSans;
    public static Typeface verdana;
    public static Typeface VerdanaBold;
    public static Typeface fontawesome;
    public static Typeface flaticon;

    public static Context context;
    public static Handler handler;

    public static HelperNotificationAndBadge helperNotificationAndBadge;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;

    public static ArrayList<String> unSecure = new ArrayList<>();

    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();


    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static boolean isSecure = false;
    public static boolean allowForConnect = true;//TODO [Saeed Mozaffari] [2016-08-18 12:09 PM] - set allowForConnect to realm
    public static boolean userLogin = false;
    public static boolean socketConnection = false;
    public static boolean canRunReceiver = false;

    public static SecretKeySpec symmetricKey;
    public static String symmetricMethod;

    public static int ivSize;

    public static int requestCount = 0;
    public static int responseCount = 0;
    public static int handlerCount = 0;
    public static int errorCount = 0;
    public static int timeoutCount = 0;
    public static int userTextSize = 0;

    public static Activity currentActivity;
    public static LayoutInflater inflater;

    public static Typeface FONT_IGAP;
    public static Typeface HELETICBLK_TITR;
    public static Typeface ARIAL_TEXT;
    public static Typeface YEKAN_FARSI;
    public static Typeface YEKAN_BOLD;
    public static File imageFile;
    public static int COPY_BUFFER_SIZE = 1024;

    public static final String FAQ = "http://www.digikala.com";
    public static final String POLICY = "http://www.digikala.com";

    public static UploaderUtil uploaderUtil = new UploaderUtil();
    public static ClearMessagesUtil clearMessagesUtil = new ClearMessagesUtil();
    public static ChatSendMessageUtil chatSendMessageUtil = new ChatSendMessageUtil();
    public static ChatUpdateStatusUtil chatUpdateStatusUtil = new ChatUpdateStatusUtil();
    public static OnFileUploadStatusResponse onFileUploadStatusResponse;

    public static Config.ConnectionState connectionState;

    public static OnConnectionChangeState onConnectionChangeState;
    public static OnReceiveInfoLocation onReceiveInfoLocation;
    public static OnUserRegistration onUserRegistration;
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

    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP = DIR_SDCARD + "/iGap";
    public static final String DIR_IMAGES = DIR_APP + "/images";
    public static final String DIR_VIDEOS = DIR_APP + "/videos";
    public static final String DIR_AUDIOS = DIR_APP + "/audios";
    public static final String DIR_DOCUMENT = DIR_APP + "/document";
    public static final String DIR_SOUND_NOTIFICATION = DIR_APP + "/sound";
    public static final String DIR_CHAT_BACKGROUND = DIR_APP + "/chat_background";
    public static final String DIR_NEW_GROUP = DIR_APP + "/new_group";
    public static final String DIR_NEW_CHANEL = DIR_APP + "/new_chanel";
    public static final String DIR_ALL_IMAGE_USER_CONTACT = DIR_APP + "/all_image_user_contact";
    public static final String DIR_IMAGE_USER = DIR_APP + "/image_user";

    public static File chatBackground;
    public static File IMAGE_NEW_GROUP;
    public static File IMAGE_NEW_CHANEL;


    public static final String CHAT_MESSAGE_TIME = "H:mm";
    public static final String ROOM_LAST_MESSAGE_TIME = "h:mm a";


    @Override
    public void onCreate() {
        MultiDex.install(getApplicationContext());
        super.onCreate();


        SharedPreferences shKeepAlive = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int isStart = shKeepAlive.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
        if (isStart == 1) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
            Log.i("XXCCXXXXX", "2222: ");

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
        fillSecuringInterface();
        WebSocketClient.getInstance();

        neuroplp = Typeface.createFromAsset(this.getAssets(), "fonts/neuropol.ttf");
        robotoBold = Typeface.createFromAsset(getAssets(), "fonts/RobotoBold.ttf");
        robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");
        arialBold = Typeface.createFromAsset(this.getAssets(), "fonts/arialbd.ttf");
        arial = Typeface.createFromAsset(this.getAssets(), "fonts/arial.ttf");
        iranSans = Typeface.createFromAsset(this.getAssets(), "fonts/IRANSansMobile.ttf");
        verdana = Typeface.createFromAsset(this.getAssets(), "fonts/Verdana.ttf");
        VerdanaBold = Typeface.createFromAsset(this.getAssets(), "fonts/VerdanaBold.ttf");
        fontawesome = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome.ttf");
        flaticon = Typeface.createFromAsset(this.getAssets(), "fonts/Flaticon.ttf");
        FONT_IGAP = Typeface.createFromAsset(context.getAssets(), "fonts/neuropolitical.ttf");
        HELETICBLK_TITR = Typeface.createFromAsset(context.getAssets(), "fonts/ar.ttf");
        ARIAL_TEXT = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");
        YEKAN_FARSI = Typeface.createFromAsset(context.getAssets(), "fonts/yekan.ttf");
        YEKAN_BOLD = Typeface.createFromAsset(context.getAssets(), "fonts/yekan_bold.ttf");

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(getApplicationContext())
                .name("iGapLocalDatabase.realm")
                .schemaVersion(1)
                .migration(new RealmMigrationClass())
                .deleteRealmIfMigrationNeeded()
                .build());

        // Create global configuration and initialize ImageLoader with this config
        // https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Configuration
        // https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Display-Options
        // https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();
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

    }

    private void setFont() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        String language = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, "en");

        if (language.equals("فارسی")) {
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            setLocale("fa");

        } else if (language.equals("English")) {
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/arial.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            setLocale("en");
        } else if (language.equals("العربی")) {
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/arial.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            setLocale("ar");

        } else if (language.equals("Deutsch")) {
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/arial.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            );
            setLocale("nl");

        }
    }

    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    public static void setUserTextSize() {

        SharedPreferences sharedPreferencesSetting = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        userTextSize = sharedPreferencesSetting.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 16);

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

    private void fillUnSecureList() {
        unSecure.add("2");
    }

    private void fillSecuringInterface() {
        G.onSecuring = new OnSecuring() {
            @Override
            public void onSecure() {
                Log.i("FFF", "Secure");
                login();
            }
        };
    }

    private void login() { //TODO [Saeed Mozaffari] [2016-09-07 10:24 AM] - mitonim karhaie ke hamishe bad az login bayad anjam beshe ro dar classe login response gharar bedim

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("FFF", "Login");
                        Toast.makeText(G.context, "User Login!", Toast.LENGTH_SHORT).show();
                        new RequestClientCondition().clientCondition();
                        importContact();
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
                    Log.i("FFF", "Login 1");
                    Realm realm = Realm.getDefaultInstance();
                    Log.i("FFF", "Login 2");
                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                    Log.i("FFF", "Login 3 userInfo : " + userInfo);
                    if (!G.userLogin && userInfo != null && userInfo.getUserRegistrationState()) {
                        new RequestUserLogin().userLogin(userInfo.getToken());
                    }
                    realm.close();
                } else {
                    login();
                }
            }
        }, 1000);
    }

    public static void importContact() {

        G.onContactImport = new OnUserContactImport() {
            @Override
            public void onContactImport() {
                getContactListFromServer();
            }
        };
        Contacts.getListOfContact(true);
    }

    public static void getContactListFromServer() {
        G.onUserContactGetList = new OnUserContactGetList() {
            @Override
            public void onContactGetList() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, "Get Contact List!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        new RequestUserContactsGetList().userContactGetList();
    }
}
