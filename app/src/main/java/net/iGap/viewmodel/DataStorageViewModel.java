package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.FileUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;

import java.io.File;

import io.realm.Realm;

public class DataStorageViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> goToDataUsagePage = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> showDialogKeepMedia = new SingleLiveEvent<>();
    private MutableLiveData<Integer[]> showAutoDownloadDataDialog = new MutableLiveData<>();
    private MutableLiveData<Integer[]> showAutoDownloadWifiDialog = new MutableLiveData<>();
    private ObservableInt keepMediaTime = new ObservableInt(R.string.keep_media_forever);
    private ObservableBoolean isSdkEnable = new ObservableBoolean(false);
    private ObservableBoolean isAutoGif = new ObservableBoolean(false);
    private ObservableInt showLayoutSdk = new ObservableInt(View.GONE);
    private ObservableField<String> cleanUpSize = new ObservableField<>("0 KB");

    public MutableLiveData<Integer[]> autoDownloadRoamingListener = new MutableLiveData<>();

    public ObservableField<String> callbackClearCache = new ObservableField<>("0 KB");


    private SharedPreferences sharedPreferences;
    private int keepMediaState;
    private File fileMap;
    private int selectedClearCacheCheckBoxes = 0;

    private int KEY_AD_ROAMING_PHOTO = -1;
    private int KEY_AD_ROAMING_VOICE_MESSAGE = -1;
    private int KEY_AD_ROAMING_VIDEO = -1;
    private int KEY_AD_ROAMING_FILE = -1;
    private int KEY_AD_ROAMING_MUSIC = -1;
    private int KEY_AD_ROAMINGN_GIF = -1;
    private int KEY_AD_DATA_PHOTO = -1;
    private int KEY_AD_DATA_VOICE_MESSAGE = -1;
    private int KEY_AD_DATA_VIDEO = -1;
    private int KEY_AD_DATA_FILE = -1;
    private int KEY_AD_DATA_MUSIC = -1;
    private int KEY_AD_DATA_GIF = -1;
    private int KEY_AD_WIFI_PHOTO = -1;
    private int KEY_AD_WIFI_VOICE_MESSAGE = -1;
    private int KEY_AD_WIFI_VIDEO = -1;
    private int KEY_AD_WIFI_FILE = -1;
    private int KEY_AD_WIFI_MUSIC = -1;
    private int KEY_AD_WIFI_GIF = -1;

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

        callbackClearCache.set(new FileUtils().getFileTotalSize());

        try (Realm realm = Realm.getDefaultInstance()) {
            cleanUpSize.set(FileUtils.formatFileSize(new File(realm.getConfiguration().getPath()).length()));
        }

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

    public ObservableInt getShowLayoutSdk() {
        return showLayoutSdk;
    }

    public void onWifiDataUsageClick() {
        goToDataUsagePage.setValue(false);
    }

    public void onMobileDataUsageClick() {
        goToDataUsagePage.setValue(true);
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
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, 1)
        });
    }

    public void setAutoDownloadOverData(Integer[] items){
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1)
                .apply();

        for (Integer aWhich : items) {
            String tmp = "";
            if (aWhich == 0) {
                tmp = SHP_SETTING.KEY_AD_DATA_PHOTO;
            } else if (aWhich == 1) {
                tmp = SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE;
            } else if (aWhich == 2) {
                tmp = SHP_SETTING.KEY_AD_DATA_VIDEO;
            } else if (aWhich == 3) {
                tmp =SHP_SETTING.KEY_AD_DATA_FILE;
            } else if (aWhich == 4) {
                tmp = SHP_SETTING.KEY_AD_DATA_MUSIC;
            } else if (aWhich == 5) {
                tmp = SHP_SETTING.KEY_AD_DATA_GIF;
            }
            sharedPreferences.edit().putInt(tmp, 1).apply();
        }
    }

    public void onClickAutoDownloadWifi() {
        showAutoDownloadWifiDialog.setValue(new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, 1)
        });
    }

    public void setAutoDownloadOverWifi(Integer[] tmp){

        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1)
                .apply();

        for (Integer aWhich : tmp) {
            String tmp;
            if (aWhich == 0) {
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, aWhich);
            } else if (aWhich == 1) {
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, aWhich);
            } else if (aWhich == 2) {
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, aWhich);
            } else if (aWhich == 3) {
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, aWhich);
            } else if (aWhich == 4) {

                editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, aWhich);
            } else if (aWhich == 5) {
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, aWhich);
            }
            editor.apply();
        }
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onClickAutoGif(View view) {
        isAutoGif.set(!isAutoGif.get());
    }

    public void onCheckedChangeAutoGif(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isAutoGif.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
            editor.apply();
        }

    }

    public void onClickAutoDownloadRoaming(View view) {

        KEY_AD_ROAMING_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
        KEY_AD_ROAMING_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
        KEY_AD_ROAMING_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
        KEY_AD_ROAMING_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
        KEY_AD_ROAMING_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
        KEY_AD_ROAMINGN_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);

        Integer[] selected = new Integer[]{
                KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMINGN_GIF
        };

        autoDownloadRoamingListener.setValue(selected);
    }

    public void onClickClearCache(View v) {

        final long sizeFolderPhotoDialog = getFolderSize(new File(G.DIR_IMAGES));
        final long sizeFolderVideoDialog = getFolderSize(new File(G.DIR_VIDEOS));
        final long sizeFolderDocumentDialog = getFolderSize(new File(G.DIR_DOCUMENT));
        final long sizeFolderAudio = getFolderSize(new File(G.DIR_AUDIOS));
        final long sizeFolderMap = FileUtils.getFolderSize(fileMap);
        final long sizeFolderOtherFiles = getFolderSize(new File(G.DIR_TEMP));
        final long sizeFolderOtherFilesBackground = getFolderSize(new File(G.DIR_CHAT_BACKGROUND));
        final long sizeFolderOtherFilesImageUser = getFolderSize(new File(G.DIR_IMAGE_USER));
        final long sizeFolderOther = sizeFolderOtherFiles + sizeFolderOtherFilesImageUser + sizeFolderOtherFilesBackground;

        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(context).title(G.context.getResources().getString(R.string.st_title_Clear_Cache)).customView(R.layout.st_dialog_clear_cach, wrapInScrollView).positiveText(G.context.getResources().getString(R.string.st_title_Clear_Cache)).show();

        View view = dialog.getCustomView();

        final File filePhoto = new File(G.DIR_IMAGES);
        assert view != null;
        TextView photo = view.findViewById(R.id.st_txt_sizeFolder_photo);
        photo.setText(FileUtils.formatFileSize(sizeFolderPhotoDialog));

        long rTotalSize = sizeFolderPhotoDialog + sizeFolderVideoDialog + sizeFolderDocumentDialog + sizeFolderAudio + sizeFolderMap + sizeFolderOther;
        final TextView txtTotalSize = view.findViewById(R.id.st_txt_sizeFolder_all);
        txtTotalSize.setText(FileUtils.formatFileSize(rTotalSize));

        final CheckBox checkBoxAll = view.findViewById(R.id.st_checkBox_all);
        final CheckBox checkBoxPhoto = view.findViewById(R.id.st_checkBox_photo);
        final CheckBox checkBoxVideo = view.findViewById(R.id.st_checkBox_video_dialogClearCash);
        final CheckBox checkBoxDocument = view.findViewById(R.id.st_checkBox_document_dialogClearCash);
        final CheckBox checkBoxAudio = view.findViewById(R.id.st_checkBox_audio_dialogClearCash);
        final CheckBox checkBoxMap = view.findViewById(R.id.st_checkBox_map_dialogClearCash);
        final CheckBox checkBoxOtherFiles = view.findViewById(R.id.st_checkBox_otherFiles);

        onCacheCheckedChanged = (buttonView, isChecked) -> {
            boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
            checkBoxAll.setChecked(state);
        };

        final File fileVideo = new File(G.DIR_VIDEOS);
        checkBoxPhoto.setOnCheckedChangeListener(onCacheCheckedChanged);

        TextView video = view.findViewById(R.id.st_txt_sizeFolder_video);
        video.setText(FileUtils.formatFileSize(sizeFolderVideoDialog));
        checkBoxVideo.setOnCheckedChangeListener(onCacheCheckedChanged);

        final File fileDocument = new File(G.DIR_DOCUMENT);
        TextView document = view.findViewById(R.id.st_txt_sizeFolder_document_dialogClearCash);
        document.setText(FileUtils.formatFileSize(sizeFolderDocumentDialog));
        checkBoxDocument.setOnCheckedChangeListener(onCacheCheckedChanged);

        final File fileAudio = new File(G.DIR_AUDIOS);
        TextView txtAudio = view.findViewById(R.id.st_txt_audio_dialogClearCash);
        txtAudio.setText(FileUtils.formatFileSize(sizeFolderAudio));
        checkBoxAudio.setOnCheckedChangeListener(onCacheCheckedChanged);

        //final File fileMap = new File(G.DIR_AUDIOS);
        TextView txtMap = view.findViewById(R.id.st_txt_map_dialogClearCash);
        txtMap.setText(FileUtils.formatFileSize(sizeFolderMap));
        checkBoxMap.setOnCheckedChangeListener(onCacheCheckedChanged);

        final File fileOtherFiles = new File(G.DIR_TEMP);
        TextView txtOtherFiles = view.findViewById(R.id.st_txt_otherFiles);
        txtOtherFiles.setText(FileUtils.formatFileSize(sizeFolderOther));
        checkBoxOtherFiles.setOnCheckedChangeListener(onCacheCheckedChanged);

        ViewGroup layoutCheckAll = view.findViewById(R.id.st_checkBox_all_layout);

        layoutCheckAll.setOnClickListener(v1 -> {

            boolean isChecked = !checkBoxAll.isChecked();
            checkBoxPhoto.setChecked(isChecked);
            checkBoxVideo.setChecked(isChecked);
            checkBoxDocument.setChecked(isChecked);
            checkBoxAudio.setChecked(isChecked);
            checkBoxMap.setChecked(isChecked);
            checkBoxOtherFiles.setChecked(isChecked);
            checkBoxAll.setChecked(isChecked);

        });

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxPhoto.isChecked()) {
                    for (File file : filePhoto.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxVideo.isChecked()) {
                    for (File file : fileVideo.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxDocument.isChecked()) {
                    for (File file : fileDocument.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxAudio.isChecked()) {
                    for (File file : fileAudio.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                }
                if (checkBoxMap.isChecked()) {
                    FragmentiGapMap.deleteMapFileCash();
                }

                if (checkBoxOtherFiles.isChecked()) {
                    for (File file : fileOtherFiles.listFiles()) {
                        if (!file.isDirectory()) file.delete();
                    }
                    final File fileOtherFilesBackground = new File(G.DIR_CHAT_BACKGROUND);

                    if (fileOtherFilesBackground.listFiles() != null)
                        for (File fileBackground : fileOtherFilesBackground.listFiles()) {
                            if (!fileBackground.isDirectory()) fileBackground.delete();
                        }

                    final File fileOtherFilesImageUser = new File(G.DIR_IMAGE_USER);

                    if (fileOtherFilesImageUser.listFiles() != null)
                        for (File fileImageUser : fileOtherFilesImageUser.listFiles()) {
                            if (!fileImageUser.isDirectory()) fileImageUser.delete();
                        }
                }

                long afterClearSizeFolderPhoto = FileUtils.getFolderSize(new File(G.DIR_IMAGES));
                long afterClearSizeFolderVideo = FileUtils.getFolderSize(new File(G.DIR_VIDEOS));
                long afterClearSizeFolderDocument = FileUtils.getFolderSize(new File(G.DIR_DOCUMENT));
                long afterClearSizeFolderAudio = FileUtils.getFolderSize(new File(G.DIR_AUDIOS));
                long afterClearSizeFolderMap = FileUtils.getFolderSize(fileMap);
                long afterClearSizeFolderOtherFiles = FileUtils.getFolderSize(new File(G.DIR_TEMP));
                long afterClearSizeFolderOtherFilesBackground = FileUtils.getFolderSize(new File(G.DIR_CHAT_BACKGROUND));
                long afterClearSizeFolderOtherFilesImageUser = FileUtils.getFolderSize(new File(G.DIR_IMAGE_USER));
                long afterClearTotal = afterClearSizeFolderPhoto + afterClearSizeFolderVideo + afterClearSizeFolderDocument + afterClearSizeFolderAudio + afterClearSizeFolderMap + afterClearSizeFolderOtherFiles + afterClearSizeFolderOtherFilesImageUser + afterClearSizeFolderOtherFilesBackground;
                callbackClearCache.set(FileUtils.formatFileSize(afterClearTotal));
                txtTotalSize.setText(FileUtils.formatFileSize(afterClearTotal));
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(dialog1 -> onCacheCheckedChanged = null);


    }

    public void onClickCleanUp(View v) {
        final MaterialDialog inDialog = new MaterialDialog.Builder(context).customView(R.layout.dialog_content_custom, true).build();
        View view = inDialog.getCustomView();

        inDialog.show();

        TextView txtTitle = (TextView) view.findViewById(R.id.txtDialogTitle);
        txtTitle.setText(G.context.getResources().getString(R.string.clean_up_chat_rooms));

        TextView iconTitle = (TextView) view.findViewById(R.id.iconDialogTitle);
        iconTitle.setText(R.string.md_clean_up);

        TextView txtContent = (TextView) view.findViewById(R.id.txtDialogContent);
        txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

        TextView txtCancel = (TextView) view.findViewById(R.id.txtDialogCancel);
        TextView txtOk = (TextView) view.findViewById(R.id.txtDialogOk);

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (Realm realm = Realm.getDefaultInstance()) {
                    RealmRoomMessage.ClearAllMessage(realm);
                    RealmRoom.clearAllScrollPositions();
                    final long DbTotalSize = new File(realm.getConfiguration().getPath()).length();

                    callbackCleanUp.set(FileUtils.formatFileSize(DbTotalSize));

                    MusicPlayer.closeLayoutMediaPlayer();

                    inDialog.dismiss();

                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDialog.dismiss();
            }
        });

    }

    public void onClickSdkEnable(View view) {

        new MaterialDialog.Builder(context)
                .title(G.context.getResources().getString(R.string.are_you_sure))
                .negativeText(G.context.getResources().getString(R.string.B_cancel))
                .content(G.context.getResources().getString(R.string.change_storage_place))
                .positiveText(G.context.getResources().getString(R.string.B_ok))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        isSdkEnable.set(!isSdkEnable.get());

                    }
                }).show();

    }

    public void onCheckedSdkEnable(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSdkEnable.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_SDK_ENABLE, 0);
            editor.apply();
        }

        StartupActions.makeFolder();
    }
}
