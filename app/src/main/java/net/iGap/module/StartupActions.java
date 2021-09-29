package net.iGap.module;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPermission;
import net.iGap.module.accountManager.DbManager;
import net.iGap.network.LookUpClass;
import net.iGap.realm.RealmDataUsage;
import net.iGap.realm.RealmMigration;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.realm.CompactOnLaunchCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
//import ir.radsense.raadcore.Raad;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.Config.REALM_SCHEMA_VERSION;
import static net.iGap.G.DIR_AUDIOS;
import static net.iGap.G.DIR_CHAT_BACKGROUND;
import static net.iGap.G.DIR_DOCUMENT;
import static net.iGap.G.DIR_IMAGES;
import static net.iGap.G.DIR_IMAGE_USER;
import static net.iGap.G.DIR_MESSAGES;
import static net.iGap.G.DIR_STICKER;
import static net.iGap.G.DIR_TEMP;
import static net.iGap.G.DIR_VIDEOS;
import static net.iGap.G.IGAP;
import static net.iGap.G.IMAGE_NEW_CHANEL;
import static net.iGap.G.IMAGE_NEW_GROUP;
import static net.iGap.G.context;
import static net.iGap.G.imageFile;
import static net.iGap.G.imageLoader;
import static net.iGap.G.isSaveToGallery;
import static net.iGap.G.selectedLanguage;
import static net.iGap.G.updateResources;
import static net.iGap.G.userTextSize;

/**
 * all actions that need doing after open app
 */
public final class StartupActions {

    public StartupActions() {
        Log.wtf(this.getClass().getName(), "StartupActions");

        new Thread(this::manageSettingPreferences).start();
        new Thread(StartupActions::makeFolder).start();
        new Thread(this::initializeGlobalVariables).start();
        new Thread(ConnectionManager::manageConnection).start();
        new Thread(this::configDownloadManager).start();
        new Thread(this::manageTime).start();
        new Thread(StartupActions::getiGapAccountInstance).start();

        if (G.ISRealmOK/*realmConfiguration()*/) {
            DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(@NotNull Realm realm) {
                        try {
                            long time = TimeUtils.currentLocalTime() - 30 * 24 * 60 * 60 * 1000L;
                            RealmResults<RealmRoom> realmRooms = realm.where(RealmRoom.class).findAll();
                            RealmQuery<RealmRoomMessage> roomMessages = realm.where(RealmRoomMessage.class);

                            for (RealmRoom room : realmRooms) {
                                if (room.getLastMessage() != null) {
                                    roomMessages = roomMessages.notEqualTo("messageId", room.getLastMessage().getMessageId());
                                }
                            }

                            RealmResults<RealmRoomMessage> realmRoomMessages = roomMessages
                                    .greaterThan("messageId", 0)
                                    .lessThan("createTime", time).findAll();

                            for (RealmRoomMessage var : realmRoomMessages)
                                var.removeFromRealm(realm);

                        } catch (OutOfMemoryError error) {
                            error.printStackTrace();
                            HelperLog.getInstance().setErrorLog(new Exception(error.getMessage()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            HelperLog.getInstance().setErrorLog(e);
                        }
                    }
                });
            });
            new Thread(() -> checkDataUsage()).start();
            new Thread(this::connectToServer).start();
            Log.wtf(this.getClass().getName(), "StartupActions");
        }

    }

    private void checkDataUsage() {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmResults<RealmDataUsage> realmDataUsage = realm.where(RealmDataUsage.class).findAll();
            if (realmDataUsage.size() == 0)
                HelperDataUsage.initializeRealmDataUsage();
        });
    }

    private void manageTime() {
        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.isTimeWhole = sharedPreferences.getBoolean(SHP_SETTING.KEY_WHOLE_TIME, false);
    }

    private void configDownloadManager() {

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(G.context, config);


    }

    /**
     * detect and  initialize text size
     */
    public static void textSizeDetection(int size) {
        userTextSize = size;

        if (!G.context.getResources().getBoolean(R.bool.isTablet)) {

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
    }

    /**
     * create app folders if not created or removed from phone storage
     */
    public static void makeFolder() {
        try {
            manageAppDirectories();
            //before used from thread; isn't good idea
            //new Thread(new Runnable() {
            //    @Override
            //    public void run() {
            //    }
            //}).start();

            new File(DIR_IMAGES).mkdirs();
            new File(DIR_VIDEOS).mkdirs();
            new File(DIR_AUDIOS).mkdirs();
            new File(DIR_DOCUMENT).mkdirs();
            new File(DIR_MESSAGES).mkdirs();

            String file = ".nomedia";
            new File(DIR_IMAGES + "/" + file).createNewFile();
            new File(DIR_VIDEOS + "/" + file).createNewFile();
            new File(DIR_AUDIOS + "/" + file).createNewFile();
            new File(DIR_DOCUMENT + "/" + file).createNewFile();
            new File(DIR_MESSAGES + "/" + file).createNewFile();


            new File(DIR_CHAT_BACKGROUND).mkdirs();
            new File(DIR_IMAGE_USER).mkdirs();
            new File(DIR_STICKER).mkdirs();
            new File(DIR_TEMP).mkdirs();
            new File(DIR_CHAT_BACKGROUND + "/" + file).createNewFile();
            new File(DIR_IMAGE_USER + "/" + file).createNewFile();
            new File(DIR_STICKER + "/" + file).createNewFile();
            new File(DIR_TEMP + "/" + file).createNewFile();

            IMAGE_NEW_GROUP = new File(G.DIR_IMAGE_USER, "image_new_group.jpg");
            IMAGE_NEW_CHANEL = new File(G.DIR_IMAGE_USER, "image_new_chanel.jpg");
            imageFile = new File(DIR_IMAGE_USER, "image_user");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manageAppDirectories() {
        String rootPath = getCacheDir().getPath();

        if (!HelperPermission.grantedUseStorage()) {
            DIR_IMAGES = rootPath + G.IMAGES;
            DIR_VIDEOS = rootPath + G.VIDEOS;
            DIR_AUDIOS = rootPath + G.AUDIOS;
            DIR_DOCUMENT = rootPath + G.DOCUMENT;
            DIR_MESSAGES = rootPath + G.MESSAGES;

        } else {
            String selectedStorage = getSelectedStoragePath(rootPath);
            DIR_IMAGES = selectedStorage + G.IMAGES;
            DIR_VIDEOS = selectedStorage + G.VIDEOS;
            DIR_AUDIOS = selectedStorage + G.AUDIOS;
            DIR_DOCUMENT = selectedStorage + G.DOCUMENT;
            DIR_MESSAGES = selectedStorage + G.MESSAGES;
        }

        DIR_TEMP = rootPath + G.TEMP;
        DIR_CHAT_BACKGROUND = rootPath + G.CHAT_BACKGROUND;
        DIR_IMAGE_USER = rootPath + G.IMAGE_USER;
        DIR_STICKER = rootPath + G.STICKER;
    }

    private static String getSelectedStoragePath(String cashPath) {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean canWrite = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String selectedStorage = "";

        if (canWrite) {
            selectedStorage = G.DIR_APP;
        } else {
            selectedStorage = cashPath;
        }

        if (sharedPreferences.getInt(SHP_SETTING.KEY_SDK_ENABLE, 0) == 1) {
            if (G.DIR_SDCARD_EXTERNAL.equals("")) {
                List<String> storageList = FileUtils.getSdCardPathList();
                if (storageList.size() > 0) {
                    String sdPath = "";
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        sdPath = storageList.get(0) + IGAP;
                    } else {
                        File exFile = G.context.getExternalFilesDir(null);
                        if (exFile != null) {
                            sdPath = storageList.get(0) + exFile.getAbsolutePath().substring(exFile.getAbsolutePath().indexOf("/Android"));
                        }
                    }
                    File sdFile = new File(sdPath);
                    if ((sdFile.exists() && sdFile.canWrite()) || sdFile.mkdirs()) {
                        G.DIR_SDCARD_EXTERNAL = selectedStorage = sdPath;
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 0);
                        editor.apply();
                    }
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 0);
                    editor.apply();
                }
            } else {
                File sdFile = new File(G.DIR_SDCARD_EXTERNAL);
                if ((sdFile.exists() && sdFile.canWrite()) || sdFile.mkdirs()) {
                    selectedStorage = G.DIR_SDCARD_EXTERNAL;
                } else {
                    G.DIR_SDCARD_EXTERNAL = "";
                }
            }
        }
        new File(selectedStorage).mkdirs();
        return selectedStorage;
    }

    /**
     * if iGap Account not created yet, create otherwise just detect and return
     */
    public static Account getiGapAccountInstance() {

        if (G.iGapAccount != null) {
            return G.iGapAccount;
        }

        AccountManager accountManager = AccountManager.get(G.context);
        if (accountManager.getAccounts().length != 0) {
            for (Account account : accountManager.getAccounts()) {
                if (account.type.equals(G.context.getPackageName())) {
                    G.iGapAccount = account;
                    return G.iGapAccount;
                }
            }
        }

        G.iGapAccount = new Account(Config.IGAP_ACCOUNT, G.context.getPackageName());
        String password = "net.iGap";
        try {
            accountManager.addAccountExplicitly(G.iGapAccount, password, null);
        } catch (Exception e1) {
            e1.getMessage();
        }

        return G.iGapAccount;
    }

    public static File getCacheDir() {
        String state = null;
        try {
            state = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state == null || state.startsWith(Environment.MEDIA_MOUNTED)) {
            try {
                File file = G.context.getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            File file = G.context.getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(G.DIR_APP);
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

        /** clear map cache and use from new map tile url */
        if (preferences.getBoolean(SHP_SETTING.KEY_MAP_CLEAR_CACHE_GOOGLE, true)) {
            FragmentiGapMap.deleteMapFileCash();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(SHP_SETTING.KEY_MAP_CLEAR_CACHE_GOOGLE, false);
            editor.apply();
        }

        boolean isDisableAutoDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_DISABLE_TIME_DARK_THEME, true);
        if (!isDisableAutoDarkTheme) {
            checkTimeForAutoTheme(preferences);
        }

        Theme.setThemeColor();

        // setting for show layout vote in channel
        G.showVoteChannelLayout = preferences.getInt(SHP_SETTING.KEY_VOTE, 1) == 1;

        //setting for show layout sender name in group
        G.showSenderNameInGroup = preferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0) == 1;

        /**
         * detect need save to gallery automatically
         */
        int checkedSaveToGallery = preferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        isSaveToGallery = checkedSaveToGallery == 1;

        textSizeDetection(preferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14));
        languageDetection(preferences);
    }

    private void checkTimeForAutoTheme(SharedPreferences preferences) {
        long toMs;
        long fromMs;
        boolean auto = preferences.getBoolean(SHP_SETTING.KEY_IS_AUTOMATIC_TIME_DARK_THEME, true);

        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long now = System.currentTimeMillis() + offset;

        if (auto) {
            toMs = 28800000;
            fromMs = 68400000;
        } else {
            toMs = preferences.getLong(SHP_SETTING.KEY_SELECTED_MILISECOND_TO, 28800000);
            fromMs = preferences.getLong(SHP_SETTING.KEY_SELECTED_MILISECOND_FROM, 68400000);
        }

        try {
            String string1 = time(fromMs);
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);

            String string2 = time(toMs);
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);

            String someRandomTime = time(now);
            Date currentTime = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);

            if (currentTime.getTime() > time1.getTime() && currentTime.getTime() < time2.getTime()) {

                //checkes whether the current time is between 14:49:00 and 20:11:13.
                G.themeColor = Theme.DARK;
            } else {
                G.themeColor = Theme.DARK;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String time(long timeNow) {
        long second = (timeNow / 1000) % 60;
        long minute = (timeNow / (1000 * 60)) % 60;
        long hour = (timeNow / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d:%d", hour, minute, second, timeNow);
    }

    /**
     * detect language and set font type face
     */
    private void languageDetection(SharedPreferences sharedPreferences) {

        String language = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, "فارسی");

        switch (language) {
            case "فارسی":
                selectedLanguage = "fa";
                HelperCalander.isPersianUnicode = true;
                G.isAppRtl = true;
//                Raad.language = selectedLanguage;
//                Raad.isFA = true;
                break;
            case "English":
                selectedLanguage = "en";
                HelperCalander.isPersianUnicode = false;
                G.isAppRtl = false;
//                Raad.language = selectedLanguage;
//                Raad.isFA = false;
                break;
            case "Français":
                selectedLanguage = "fr";
                HelperCalander.isPersianUnicode = false;
                G.isAppRtl = false;
//                Raad.language = selectedLanguage;
//                Raad.isFA = false;
                break;
            case "Español":
                selectedLanguage = "es";
                HelperCalander.isPersianUnicode = false;
                G.isAppRtl = false;
//                Raad.language = selectedLanguage;
//                Raad.isFA = false;
                break;
            case "Russian":
                selectedLanguage = "ru";
                HelperCalander.isPersianUnicode = false;
                G.isAppRtl = false;
//                Raad.language = selectedLanguage;
//                Raad.isFA = false;
                break;
            case "العربی":
                selectedLanguage = "ar";
                HelperCalander.isPersianUnicode = true;
                G.isAppRtl = true;
//                Raad.language = selectedLanguage;
//                Raad.isFA = true;
                break;

            //کوردی لوکال از چپ به راست است و برای استفاده از این گویش از زبان های راست به چپ جایگزین استفاده شده است
            case "کوردی":
                selectedLanguage = "ur";
                HelperCalander.isPersianUnicode = true;
                G.isAppRtl = true;
//                Raad.language = selectedLanguage;
//                Raad.isFA = true;
                break;

            case "آذری":
                selectedLanguage = "iw";
                HelperCalander.isPersianUnicode = true;
                G.isAppRtl = true;
//                Raad.language = selectedLanguage;
//                Raad.isFA = true;
                break;
        }
        updateResources(context);
    }

    private void initializeGlobalVariables() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(false).build();
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(defaultOptions).build());
        imageLoader = ImageLoader.getInstance();

        LookUpClass.fillArrays();
    }

    /**
     * initialize realm and manage migration
     */
    private boolean realmConfiguration() {
        /**
         * before call RealmConfiguration client need to Realm.init(context);
         */
        try {
            Realm.init(context);
        } catch (Exception e) {
            G.ISRealmOK = false;
            return false;
        } catch (Error e) {
            G.ISRealmOK = false;
            return false;
        }

        /*RealmConfiguration configuredRealm = getInstance();
        Realm.setDefaultConfiguration(configuredRealm);*/
        return true;
    }

    /*public Realm getPlainInstance() {
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(context.getResources().getString(R.string.planDB))
                .schemaVersion(REALM_SCHEMA_VERSION)
                .compactOnLaunch(new CompactOnLaunchCallback() {
                    @Override
                    public boolean shouldCompact(long totalBytes, long usedBytes) {
                        final long thresholdSize = 50 * 1024 * 1024;
                        return (totalBytes > thresholdSize) && (((double) usedBytes / (double) totalBytes) < 0.8);
                    }
                })
                .migration(new RealmMigration())
                .build();
        return Realm.getInstance(configuration);
    }*/

    public RealmConfiguration getInstance() {
        SharedPreferences sharedPreferences = G.context.getSharedPreferences("AES-256", Context.MODE_PRIVATE);
        String stringArray = sharedPreferences.getString("myByteArray", null);
        if (stringArray == null) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String saveThis = Base64.encodeToString(key, Base64.DEFAULT);
            editor.putString("myByteArray", saveThis);
            editor.apply();
        }

        byte[] mKey = Base64.decode(sharedPreferences.getString("myByteArray", null), Base64.DEFAULT);

        RealmConfiguration oldConfig = new RealmConfiguration.Builder().name(context.getResources().getString(R.string.planDB))
                .schemaVersion(REALM_SCHEMA_VERSION)
                .compactOnLaunch()
                .migration(new RealmMigration()).build();
        RealmConfiguration newConfig;
        Log.wtf(this.getClass().getName(), "state true");
        newConfig = new RealmConfiguration.Builder()
                .name(net.iGap.module.accountManager.AccountManager.defaultDBName)
                .encryptionKey(mKey)
                .compactOnLaunch(new CompactOnLaunchCallback() {
                    @Override
                    public boolean shouldCompact(long totalBytes, long usedBytes) {
                        final long thresholdSize = 10 * 1024 * 1024;

                        if (totalBytes > 500 * 1024 * 1024) {
                            HelperLog.getInstance().setErrorLog(new Exception("DatabaseSize=" + totalBytes + " UsedSize=" + usedBytes));
                        }

                        return (totalBytes > thresholdSize) && (((double) usedBytes / (double) totalBytes) < 0.9);
                    }
                })
                .schemaVersion(REALM_SCHEMA_VERSION)
                .migration(new RealmMigration())
                .build();

        File oldRealmFile = new File(oldConfig.getPath());
        File newRealmFile = new File(newConfig.getPath());
        if (!oldRealmFile.exists()) {
            return newConfig;
        } else {
            Realm realm = null;
            try {
                realm = Realm.getInstance(oldConfig);
                realm.writeEncryptedCopyTo(newRealmFile, mKey);
                realm.close();
                Realm.deleteRealm(oldConfig);
                return newConfig;
            } catch (OutOfMemoryError oom) {
                //TODO : what is that, exception in catch, realm may be null and close it
                realm.close();
                return null;
            } catch (Exception e) {
                //TODO : what is that, exception in catch, realm may be null and close it
                e.printStackTrace();
                realm.close();
                return null;
            }
        }
    }
}