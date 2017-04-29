package com.iGap.module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperDownloadFile;
import com.iGap.helper.HelperFillLookUpClass;
import com.iGap.helper.HelperNotificationAndBadge;
import com.iGap.helper.HelperUploadFile;
import com.iGap.helper.MyService;
import com.iGap.realm.RealmMigration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.content.Context.MODE_PRIVATE;
import static com.iGap.G.DIR_APP;
import static com.iGap.G.DIR_AUDIOS;
import static com.iGap.G.DIR_CHAT_BACKGROUND;
import static com.iGap.G.DIR_DOCUMENT;
import static com.iGap.G.DIR_IMAGES;
import static com.iGap.G.DIR_IMAGE_USER;
import static com.iGap.G.DIR_TEMP;
import static com.iGap.G.DIR_VIDEOS;
import static com.iGap.G.IMAGE_NEW_CHANEL;
import static com.iGap.G.IMAGE_NEW_GROUP;
import static com.iGap.G.appBarColor;
import static com.iGap.G.attachmentColor;
import static com.iGap.G.context;
import static com.iGap.G.headerTextColor;
import static com.iGap.G.helperNotificationAndBadge;
import static com.iGap.G.imageFile;
import static com.iGap.G.imageLoader;
import static com.iGap.G.isSaveToGallery;
import static com.iGap.G.notificationColor;
import static com.iGap.G.selectedLanguage;
import static com.iGap.G.toggleButtonColor;
import static com.iGap.G.unLogin;
import static com.iGap.G.unSecure;
import static com.iGap.G.userTextSize;
import static com.iGap.G.waitingActionIds;

/**
 * all actions that need doing after open app
 */
public final class StartupActions {

    public StartupActions() {
        initializeGlobalVariables();
        realmConfiguration();
        connectToServer();
        manageSettingPreferences();
        makeFolder();
        ConnectionManager.manageConnection();

        /**
         * initialize download and upload listeners
         */
        new HelperDownloadFile();
        new HelperUploadFile();
    }

    /**
     * start connecting to the sever
     */
    private void connectToServer() {
        WebSocketClient.getInstance();
        new LoginActions();
    }

    /**
     * detect preferences value and initialize setting fields
     */
    private void manageSettingPreferences() {
        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        appBarColor = preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Config.default_appBarColor);
        notificationColor = preferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, Config.default_notificationColor);
        toggleButtonColor = preferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, Config.default_toggleButtonColor);
        attachmentColor = preferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, Config.default_attachmentColor);
        headerTextColor = preferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, Config.default_headerTextColor);

        /**
         * start background app service
         */
        int isStart = preferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
        if (isStart == 1) {
            Intent intent = new Intent(context, MyService.class);
            context.startService(intent);
        }

        /**
         * detect need save to gallery automatically
         */
        int checkedSaveToGallery = preferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        isSaveToGallery = checkedSaveToGallery == 1;

        /**
         * copy country list
         */
        SharedPreferences sharedPreferences = context.getSharedPreferences("CopyDataBase", MODE_PRIVATE);
        boolean isCopyFromAsset = sharedPreferences.getBoolean("isCopyRealm", true);
        if (isCopyFromAsset) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isCopyRealm", false);
            editor.apply();
            try {
                copyCountryListFromAsset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        textSizeDetection(preferences);
        languageDetection(preferences);
    }

    /**
     * copy country list from assets just once for use in registration
     *
     * @throws IOException
     */
    private void copyCountryListFromAsset() throws IOException {
        InputStream inputStream = context.getAssets().open("CountryListA.realm");
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
     * detect and  initialize text size
     */
    public static void textSizeDetection(SharedPreferences sharedPreferences) {
        userTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14);
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

    /**
     * detect language and set font type face
     */
    private void languageDetection(SharedPreferences sharedPreferences) {
        String language = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, "en");

        switch (language) {
            case "فارسی":
                selectedLanguage = "fa";
                HelperCalander.isLanguagePersian = true;
                break;
            case "English":
                selectedLanguage = "en";
                HelperCalander.isLanguagePersian = false;
                break;
            case "العربی":
                selectedLanguage = "ar";
                HelperCalander.isLanguagePersian = false;
                break;
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/IRANSansMobile.ttf").setFontAttrId(R.attr.fontPath).build());
    }

    /**
     * create app folders if not created or removed from phone storage
     */
    public static void makeFolder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new File(DIR_APP).mkdirs();
                new File(DIR_IMAGES).mkdirs();
                new File(DIR_VIDEOS).mkdirs();
                new File(DIR_AUDIOS).mkdirs();
                new File(DIR_DOCUMENT).mkdirs();
                new File(DIR_CHAT_BACKGROUND).mkdirs();
                new File(DIR_IMAGE_USER).mkdirs();
                new File(DIR_TEMP).mkdirs();

                String file = ".nomedia";
                new File(DIR_IMAGES + "/" + file).mkdirs();
                new File(DIR_VIDEOS + "/" + file).mkdirs();
                new File(DIR_AUDIOS + "/" + file).mkdirs();
                new File(DIR_DOCUMENT + "/" + file).mkdirs();
                new File(DIR_CHAT_BACKGROUND + "/" + file).mkdirs();
                new File(DIR_IMAGE_USER + "/" + file).mkdirs();
                new File(DIR_TEMP + "/" + file).mkdirs();
            }
        }).start();

        //TODO [Saeed Mozaffari] [2017-04-22 12:07 PM] - check this values and if don't need clear that
        IMAGE_NEW_GROUP = new File(G.DIR_IMAGE_USER, "image_new_group.jpg");
        IMAGE_NEW_CHANEL = new File(G.DIR_IMAGE_USER, "image_new_chanel.jpg");
        imageFile = new File(DIR_IMAGE_USER, "image_user");
    }

    private void initializeGlobalVariables() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(false).build();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(defaultOptions).build());
        imageLoader = ImageLoader.getInstance();
        helperNotificationAndBadge = new HelperNotificationAndBadge();

        HelperFillLookUpClass.fillLookUpClassArray();
        fillUnSecureList();
        fillUnLoginList();
        fillWaitingRequestActionIdAllowed();
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
        unLogin.add("30100");
        unLogin.add("30101");
        unLogin.add("30102");
        unLogin.add("30500");
        unLogin.add("30501");
        unLogin.add("30502");
        unLogin.add("30503");
    }

    /**
     * list of actionId that will be storing in waitingActionIds list
     * and after that user login send this request again
     */
    private void fillWaitingRequestActionIdAllowed() {
        waitingActionIds.add("30201");
        waitingActionIds.add("30310");
        waitingActionIds.add("30410");
        //waitingActionIds.add("700");
        //waitingActionIds.add("701");
        //waitingActionIds.add("702");
        //waitingActionIds.add("703");
        //waitingActionIds.add("705");
    }

    /**
     * initialize realm and manage migration
     */
    private void realmConfiguration() {
        /**
         * before call RealmConfiguration client need to Realm.init(context);
         */
        Realm.init(context);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("iGapLocalDatabase.realm").schemaVersion(8).migration(new RealmMigration()).build();
        DynamicRealm dynamicRealm = DynamicRealm.getInstance(configuration);
        /**
         * Returns version of Realm file on disk
         */
        if (dynamicRealm.getVersion() == -1) {
            Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("iGapLocalDatabase.realm").schemaVersion(8).deleteRealmIfMigrationNeeded().build());
        } else {
            Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("iGapLocalDatabase.realm").schemaVersion(8).migration(new RealmMigration()).build());
        }
        dynamicRealm.close();

        try {
            Realm.compactRealm(configuration);
        } catch (UnsupportedOperationException e) {
            Log.e("ErrorLog", " G.realmConfiguration        " + e.toString());
            e.printStackTrace();
        }
    }
}