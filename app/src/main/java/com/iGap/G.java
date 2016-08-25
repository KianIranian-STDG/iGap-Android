package com.iGap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;

import com.iGap.helper.HelperFillLookUpClass;
import com.iGap.interface_package.OnReceiveInfoLocation;
import com.iGap.interface_package.OnReceivePageInfoTOS;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.interface_package.OnUserRegistration;
import com.iGap.interface_package.OnUserVerification;
import com.iGap.realm.RealmMigrationClass;
import com.iGap.request.RequestWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.spec.SecretKeySpec;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by android on 8/1/2016.
 */
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


    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static boolean isSecure = false;
    public static boolean allowForConnect = true;//TODO [Saeed Mozaffari] [2016-08-18 12:09 PM] - set allowForConnect to realm
    public static boolean userLogin = false;

    public static SecretKeySpec symmetricKey;
    public static String symmetricMethod;

    public static int ivSize;

    public static int requestCount = 0;
    public static int responseCount = 0;
    public static int handlerCount = 0;
    public static int errorCount = 0;
    public static int timeoutCount = 0;


    //mo
    public static Activity currentActivity;
    public static LayoutInflater inflater;

    public static Typeface FONT_IGAP;
    public static Typeface HELETICBLK_TITR;
    public static Typeface ARIAL_TEXT;
    public static Typeface YEKAN_FARSI;
    public static Typeface YEKAN_BOLD;
    public static File imageFile;
    public static Uri saveImageUserProfile = null;
    public static Bitmap decodeBitmapProfile = null;
    public static int COPY_BUFFER_SIZE = 1024;

    public static RealmConfiguration realmConfig;
    public static Realm realm;

    public static OnReceiveInfoLocation onReceiveInfoLocation;
    public static OnUserRegistration onUserRegistration;
    public static OnUserVerification onUserVerification;
    public static OnReceivePageInfoTOS onReceivePageInfoTOS;
    public static OnUserLogin onUserLogin;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        handler = new Handler();

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        HelperFillLookUpClass.fillLookUpClassArray();
        fillUnSecureList();
        WebSocketClient.getInstance();

        realmConfig = new RealmConfiguration.Builder(getApplicationContext())
                .name("CountryListA.realm")
                .schemaVersion(1)
                .migration(new RealmMigrationClass())
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(realmConfig);

        String imageUser = Environment.getExternalStorageDirectory() + "/.image_user";

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
        //realm.close();

    }
    private void copyFromAsset() throws IOException {
        InputStream inputStream = getAssets().open("CountryListA.realm");
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
    }

    private void fillUnSecureList() {
        unSecure.add("2");
    }

}
