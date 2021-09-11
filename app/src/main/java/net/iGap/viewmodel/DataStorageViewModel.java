package net.iGap.viewmodel;

import android.content.SharedPreferences;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.AndroidUtils;
import net.iGap.module.FileUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.StartupActions;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DataStorageViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> goToDataUsagePage = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> showDialogKeepMedia = new SingleLiveEvent<>();
    private MutableLiveData<Integer[]> showAutoDownloadDataDialog = new MutableLiveData<>();
    private MutableLiveData<Integer[]> showAutoDownloadWifiDialog = new MutableLiveData<>();
    private MutableLiveData<Integer[]> showAutoDownloadRoamingDialog = new MutableLiveData<>();
    private MutableLiveData<String[]> showClearCashDialog = new MutableLiveData<>();
    private MutableLiveData<Boolean> showClearAllDialog = new MutableLiveData<>();
    private MutableLiveData<Boolean> showActiveSDCardDialog = new MutableLiveData<>();
    private ObservableInt keepMediaTime = new ObservableInt(R.string.keep_media_forever);
    private ObservableBoolean isSdkEnable = new ObservableBoolean(false);
    private ObservableBoolean isAutoGif = new ObservableBoolean(false);
    private ObservableInt showLayoutSdk = new ObservableInt(View.GONE);
    private ObservableField<String> cleanUpSize = new ObservableField<>("0 KB");
    private ObservableField<String> clearCacheSize = new ObservableField<>("...");


    private SharedPreferences sharedPreferences;
    private int keepMediaState;
    private String totalSize;

    public DataStorageViewModel(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;

        keepMediaState = sharedPreferences.getInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
        if (keepMediaState == 30) {
            keepMediaTime.set(R.string.keep_media_1month);
        } else if (keepMediaState == 180) {
            keepMediaTime.set(R.string.keep_media_6month);
        } else {
            keepMediaTime.set(R.string.keep_media_forever);
        }

        new FileUtils().getFileTotalSize(size -> clearCacheSize.set(totalSize = size));

        DbManager.getInstance().doRealmTask(realm -> {
            cleanUpSize.set(FileUtils.formatFileSize(new File(realm.getConfiguration().getPath()).length()));
        });

        showLayoutSdk.set(FileUtils.getSdCardPathList().size() > 0 ? View.VISIBLE : View.GONE);
        isSdkEnable.set(sharedPreferences.getInt(SHP_SETTING.KEY_SDK_ENABLE, 0) != 0);
        isAutoGif.set(sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS) != 0);
    }

    public SingleLiveEvent<Boolean> getGoToDataUsagePage() {
        return goToDataUsagePage;
    }

    public SingleLiveEvent<Integer> getShowDialogKeepMedia() {
        return showDialogKeepMedia;
    }

    public MutableLiveData<Integer[]> getShowAutoDownloadDataDialog() {
        return showAutoDownloadDataDialog;
    }

    public MutableLiveData<Integer[]> getShowAutoDownloadWifiDialog() {
        return showAutoDownloadWifiDialog;
    }

    public MutableLiveData<Integer[]> getShowAutoDownloadRoamingDialog() {
        return showAutoDownloadRoamingDialog;
    }

    public MutableLiveData<String[]> getShowClearCashDialog() {
        return showClearCashDialog;
    }

    public MutableLiveData<Boolean> getShowClearAllDialog() {
        return showClearAllDialog;
    }

    public MutableLiveData<Boolean> getShowActiveSDCardDialog() {
        return showActiveSDCardDialog;
    }

    public ObservableInt getKeepMediaTime() {
        return keepMediaTime;
    }

    public ObservableBoolean getIsSdkEnable() {
        return isSdkEnable;
    }

    public ObservableBoolean getIsAutoGif() {
        return isAutoGif;
    }

    public ObservableField<String> getCleanUpSize() {
        return cleanUpSize;
    }

    public ObservableField<String> getClearCacheSize() {
        return clearCacheSize;
    }

    public ObservableInt getShowLayoutSdk() {
        return showLayoutSdk;
    }

    public void onWifiDataUsageClick() {
        goToDataUsagePage.setValue(true);
    }

    public void onMobileDataUsageClick() {
        goToDataUsagePage.setValue(false);
    }

    public void onClickKeepMedia() {
        int position;
        switch (keepMediaState) {
            case 30:
                position = 1;
                break;
            case 180:
                position = 2;
                break;
            default:
                position = 0;
                break;
        }
        showDialogKeepMedia.setValue(position);
    }

    public void setKeepMediaTime(int position) {
        switch (position) {
            case 0:
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0).apply();
                keepMediaTime.set(R.string.keep_media_forever);
                break;
            case 1:
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 30).apply();
                keepMediaTime.set(R.string.keep_media_1month);
                break;
            case 2: {
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 180).apply();
                keepMediaTime.set(R.string.keep_media_6month);
                break;
            }
        }
    }

    public void onClickAutoDownloadData() {
        showAutoDownloadDataDialog.setValue(new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, 5)
        });
    }

    public void setAutoDownloadOverData(@NotNull Integer[] items) {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1)
                .apply();

        for (Integer aWhich : items) {
            switch (aWhich) {
                case 0:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, 0).apply();
                    break;
                case 1:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, 1).apply();
                    break;
                case 2:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, 2).apply();
                    break;
                case 3:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_DATA_FILE, 3).apply();
                    break;
                case 4:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, 4).apply();
                    break;
                case 5:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_DATA_GIF, 5).apply();
                    break;
            }
        }
    }

    public void onClickAutoDownloadWifi() {
        showAutoDownloadWifiDialog.setValue(new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, 5)
        });
    }

    public void setAutoDownloadOverWifi(@NotNull Integer[] items) {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1)
                .apply();

        for (Integer aWhich : items) {
            switch (aWhich) {
                case 0:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, 0).apply();
                    break;
                case 1:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, 1).apply();
                    break;
                case 2:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, 2).apply();
                    break;
                case 3:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_WIFI_FILE, 3).apply();
                    break;
                case 4:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, 4).apply();
                    break;
                case 5:
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_AD_WIFI_GIF, 5).apply();
                    break;

            }
        }
    }

    public void onClickAutoDownloadRoaming() {
        showAutoDownloadRoamingDialog.setValue(new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1)
        });
    }

    public void setAutoDownloadOverRoaming(@NotNull Integer[] items) {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1)
                .apply();

        for (Integer aWhich : items) {
            String tmp = "";
            if (aWhich > -1) {
                if ((aWhich == 0)) {
                    tmp = SHP_SETTING.KEY_AD_ROAMING_PHOTO;
                } else if ((aWhich == 1)) {
                    tmp = SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE;
                } else if ((aWhich == 2)) {
                    tmp = SHP_SETTING.KEY_AD_ROAMING_VIDEO;
                } else if ((aWhich == 3)) {
                    tmp = SHP_SETTING.KEY_AD_ROAMING_FILE;
                } else if ((aWhich == 4)) {
                    tmp = SHP_SETTING.KEY_AD_ROAMING_MUSIC;
                } else if ((aWhich == 5)) {
                    tmp = SHP_SETTING.KEY_AD_ROAMING_GIF;
                }
                sharedPreferences.edit().putInt(tmp, aWhich).apply();
            }
        }
    }

    public void onClickAutoGif(boolean isChecked) {
        setAutoPlayGift(!isChecked);
    }

    public void onCheckedChangeAutoGif(boolean isChecked) {
        setAutoPlayGift(isChecked);
    }

    private void setAutoPlayGift(boolean isPlayGift) {
        isAutoGif.set(isPlayGift);
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, isPlayGift ? 1 : 0).apply();
    }

    public void onClickClearCache() {
        String[] tmp = new String[7];
        FileUtils fileUtils = new FileUtils();
        tmp[0] = totalSize;
        tmp[1] = fileUtils.getImageFileSize();
        tmp[2] = fileUtils.getVideoFileSize();
        tmp[3] = fileUtils.getDocumentFileSize();
        tmp[4] = fileUtils.getAudioFileSize();
        tmp[5] = fileUtils.getMapFileSize();
        tmp[6] = fileUtils.getOtherFileSize();
        showClearCashDialog.setValue(tmp);
    }

    public void setClearCashData(boolean isPhoto, boolean isVideo, boolean isDocument, boolean isAudio, boolean isMap, boolean isOther) {
        FileUtils fileUtils = new FileUtils();
        if (isPhoto) {
            AndroidUtils.globalQueue.postRunnable(fileUtils::clearImageFile);
        }
        if (isVideo) {
            AndroidUtils.globalQueue.postRunnable(fileUtils::clearVideoFile);
        }
        if (isDocument) {
            AndroidUtils.globalQueue.postRunnable(fileUtils::clearDocumentFile);
        }
        if (isAudio) {
            AndroidUtils.globalQueue.postRunnable(fileUtils::clearAudioFile);
        }
        if (isMap) {
            AndroidUtils.globalQueue.postRunnable(fileUtils::clearMapFile);
        }
        if (isOther) {
            AndroidUtils.globalQueue.postRunnable(fileUtils::clearOtherFile);
        }
        AndroidUtils.globalQueue.postRunnable(() -> fileUtils.getFileTotalSize(size -> clearCacheSize.set(totalSize = size)));
    }

    public void onClickCleanUp() {
        showClearAllDialog.setValue(true);
    }

    public void clearAll() {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                RealmRoomMessage.ClearAllMessage(realm1);
                RealmRoom.clearAllScrollPositions();
            }, () -> {
                cleanUpSize.set(FileUtils.formatFileSize(new File(realm.getConfiguration().getPath()).length()));
                MusicPlayer.closeLayoutMediaPlayer();
            });
        });
    }

    public void onClickSdkEnable() {
        showActiveSDCardDialog.setValue(true);
    }

    public void setActiveSDCard() {
        isSdkEnable.set(!isSdkEnable.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_SDK_ENABLE, isSdkEnable.get() ? 1 : 0).apply();
        StartupActions.makeFolder();
    }
}
