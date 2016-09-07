package com.iGap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.iGap.helper.HelperCheckInternetConnection;
import com.iGap.helper.HelperFillLookUpClass;
import com.iGap.interface_package.OnChatDeleteMessageResponse;
import com.iGap.interface_package.OnChatEditMessageResponse;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.interface_package.OnClientGetRoomListResponse;
import com.iGap.interface_package.OnClientGetRoomResponse;
import com.iGap.interface_package.OnInfoCountryResponse;
import com.iGap.interface_package.OnInfoTime;
import com.iGap.interface_package.OnReceiveInfoLocation;
import com.iGap.interface_package.OnReceivePageInfoTOS;
import com.iGap.interface_package.OnSecuring;
import com.iGap.interface_package.OnUserContactDelete;
import com.iGap.interface_package.OnUserContactGetList;
import com.iGap.interface_package.OnUserContactImport;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.interface_package.OnUserProfileEmailResponse;
import com.iGap.interface_package.OnUserProfileGenderResponse;
import com.iGap.interface_package.OnUserProfileNickNameResponse;
import com.iGap.interface_package.OnUserRegistration;
import com.iGap.interface_package.OnUserVerification;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.ChatUpdateStatusUtil;
import com.iGap.module.ClearMessagesUtil;
import com.iGap.module.Contacts;
import com.iGap.module.UploaderUtil;
import com.iGap.realm.RealmMigrationClass;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserContactsGetList;
import com.iGap.request.RequestUserLogin;
import com.iGap.request.RequestWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class G extends Application {

    public static Typeface neuroplp;
    public static Typeface robotoBold;
    public static Typeface robotoLight;
    public static Typeface robotoRegular;
    public static Typeface arialBold;
    public static Typeface arial;
    public static Typeface verdana;
    public static Typeface VerdanaBold;
    public static Typeface fontawesome;

    public static Context context;
    public static Handler handler;

    public static ArrayList<String> unSecure = new ArrayList<>();

    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();


    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static boolean isSecure = false;
    public static boolean allowForConnect = true;//TODO [Saeed Mozaffari] [2016-08-18 12:09 PM] - set allowForConnect to realm
    public static boolean userLogin = false;
    public static boolean socketConnectingOrConnected = false;
    public static boolean internetConnection = false;

    public static SecretKeySpec symmetricKey;
    public static String symmetricMethod;

    public static int ivSize;

    public static int requestCount = 0;
    public static int responseCount = 0;
    public static int handlerCount = 0;
    public static int errorCount = 0;
    public static int timeoutCount = 0;

    public static Activity currentActivity;
    public static LayoutInflater inflater;

    public static Typeface FONT_IGAP;
    public static Typeface HELETICBLK_TITR;
    public static Typeface ARIAL_TEXT;
    public static Typeface YEKAN_FARSI;
    public static Typeface YEKAN_BOLD;
    public static File imageFile;
    public static int COPY_BUFFER_SIZE = 1024;

    public static RealmConfiguration realmConfig;
    public static UploaderUtil uploaderUtil = new UploaderUtil();
    public static ClearMessagesUtil clearMessagesUtil = new ClearMessagesUtil();
    public static ChatSendMessageUtil chatSendMessageUtil = new ChatSendMessageUtil();
    public static ChatUpdateStatusUtil chatUpdateStatusUtil = new ChatUpdateStatusUtil();

    public static OnReceiveInfoLocation onReceiveInfoLocation;
    public static OnUserRegistration onUserRegistration;
    public static OnUserVerification onUserVerification;
    public static OnReceivePageInfoTOS onReceivePageInfoTOS;
    public static OnUserLogin onUserLogin;
    public static OnUserProfileEmailResponse onUserProfileEmailResponse;
    public static OnUserProfileGenderResponse onUserProfileGenderResponse;
    public static OnUserProfileNickNameResponse onUserProfileNickNameResponse;
    public static OnInfoCountryResponse onInfoCountryResponse;
    public static OnInfoTime onInfoTime;
    public static OnUserContactImport onContactImport;
    public static OnUserContactGetList onUserContactGetList;
    public static OnUserContactDelete onUserContactdelete;
    public static OnClientGetRoomListResponse onClientGetRoomListResponse;
    public static OnSecuring onSecuring;
    public static OnChatGetRoom onChatGetRoom;
    public static OnChatEditMessageResponse onChatEditMessageResponse;
    public static OnChatDeleteMessageResponse onChatDeleteMessageResponse;
    public static OnClientGetRoomResponse onClientGetRoomResponse;

    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP = DIR_SDCARD + "/iGap";
    public static final String DIR_IMAGES = DIR_APP + "/images";
    public static final String DIR_VIDEOS = DIR_APP + "/videos";
    public static final String DIR_AUDIOS = DIR_APP + "/audios";


    @Override
    public void onCreate() {
        MultiDex.install(getApplicationContext());
        super.onCreate();
        new File(DIR_APP).mkdirs();
        new File(DIR_IMAGES).mkdirs();
        new File(DIR_VIDEOS).mkdirs();
        new File(DIR_AUDIOS).mkdirs();

        context = getApplicationContext();
        handler = new Handler();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        checkInternetConnection();
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
        verdana = Typeface.createFromAsset(this.getAssets(), "fonts/Verdana.ttf");
        VerdanaBold = Typeface.createFromAsset(this.getAssets(), "fonts/VerdanaBold.ttf");
        fontawesome = Typeface.createFromAsset(this.getAssets(), "fonts/fontawesome.ttf");

        HELETICBLK_TITR = Typeface.createFromAsset(context.getAssets(), "fonts/ar.ttf");
        ARIAL_TEXT = Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf");
        YEKAN_FARSI = Typeface.createFromAsset(context.getAssets(), "fonts/yekan.ttf");
        YEKAN_BOLD = Typeface.createFromAsset(context.getAssets(), "fonts/yekan_bold.ttf");

        realmConfig = new RealmConfiguration.Builder(getApplicationContext())
                .name("iGapLocalDatabase.realm")
                .schemaVersion(1)
                .migration(new RealmMigrationClass())
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfig);

        String imageUser = Environment.getExternalStorageDirectory() + "/image_user";

        FONT_IGAP = Typeface.createFromAsset(context.getAssets(), "fonts/neuropolitical.ttf");
        imageFile = new File(imageUser);
        if (!imageFile.exists()) {
            imageFile.mkdirs();
        }
        imageFile = new File(imageUser, "image_user.jpg");
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

    private void checkInternetConnection() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (HelperCheckInternetConnection.hasActiveInternetConnection()) {
                    G.internetConnection = true;
                } else {
                    G.internetConnection = false;
                }
            }
        });
        thread.start();
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
                        importContact();
                    }
                });
            }

            @Override
            public void onLoginError() {

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
        Contacts.getListOfContact();
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
