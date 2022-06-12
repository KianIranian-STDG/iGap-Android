package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.DataUsageFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.cell.TextCheckCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.AndroidUtils;
import net.iGap.module.FileUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DataStorageFragment extends BaseFragment {

    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();
    private boolean loading = true;
    private int sdCardSize;
    private String cacheSize;
    private String totalSize;
    private int keepMedia;
    private int autoPlayGif;
    private int sdkEnable;

    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private int sectionOneHeaderRow;
    private int dataUsageRow;
    private int keepMediaRow;
    private int sectionOneDividerRow;
    private int sectionTwoHeaderRow;
    private int autoDownloadDataRow;
    private int autoDownloadWifiRow;
    private int autoDownloadRoamingRow;
    private int sectionTwoDividerRow;
    private int sectionThreeHeaderRow;
    private int autoPlayGifRow;
    private int sectionThreeDividerRow;
    private int sectionFourHeaderRow;
    private int clearCashRow;
    private int clearAllMemoryRow;
    private int sdkEnableRow;
    private int sectionFourDividerRow;
    private int rowCount;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getStorageData();
        return true;
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof TextSettingsCell) {
                if (position == dataUsageRow) {
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new DataUsageFragment()).setReplace(false).load();
                    }
                } else if (position == keepMediaRow) {
                    showKeepMediaDialog();
                } else if (position == autoDownloadDataRow) {
                    showAutoDownloadDataDialog();
                } else if (position == autoDownloadWifiRow) {
                    showAutoDownloadWifiDialog();
                } else if (position == autoDownloadRoamingRow) {
                    showAutoDownloadRoamingDialog();
                } else if (position == clearCashRow) {
                    showClearCashDialog();
                } else if (position == clearAllMemoryRow) {
                    showClearAllMemoryDialog();
                }
            } else if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!((TextCheckCell) view).isChecked());
                if (position == autoPlayGifRow) {
                    setAutoPlayGif();
                } else if (position == sdkEnableRow) {
                    showActiveSDCardDialog();
                }
            }
        });
        return fragmentView;
    }

    private void showActiveSDCardDialog() {
        if (getContext() != null) {
            new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.are_you_sure)
                    .negativeText(R.string.B_cancel)
                    .content(R.string.change_storage_place)
                    .positiveText(R.string.B_ok)
                    .onPositive((dialog, which) -> setActiveSDCard()).show();
        }
    }

    private void setActiveSDCard() {
        sdkEnable = sdkEnable == 0 ? 1 : 0;
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_SDK_ENABLE, sdkEnable).apply();
        StartupActions.makeFolder();
    }

    private void showClearAllMemoryDialog() {
        if (getContext() != null) {
            MaterialDialog inDialog = new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .customView(R.layout.dialog_content_custom, true)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background)).build();
            View dialogView = inDialog.getCustomView();

            assert dialogView != null;
            TextView txtTitle = dialogView.findViewById(R.id.txtDialogTitle);
            txtTitle.setText(R.string.clean_up_chat_rooms);

            TextView iconTitle = dialogView.findViewById(R.id.iconDialogTitle);
            iconTitle.setTypeface(ResourcesCompat.getFont(iconTitle.getContext(), R.font.font_icons));
            iconTitle.setText(R.string.icon_clearing);

            TextView txtContent = dialogView.findViewById(R.id.txtDialogContent);
            txtContent.setText(R.string.do_you_want_to_clean_all_data_in_chat_rooms);

            TextView txtCancel = dialogView.findViewById(R.id.txtDialogCancel);
            TextView txtOk = dialogView.findViewById(R.id.txtDialogOk);

            txtOk.setOnClickListener(v -> {
                clearAll();
                inDialog.dismiss();
            });

            txtCancel.setOnClickListener(v -> inDialog.dismiss());
            inDialog.show();
        }
    }

    private void clearAll() {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                RealmRoomMessage.ClearAllMessage(realm1);
                RealmRoom.clearAllScrollPositions();
            }, () -> {
                totalSize = FileUtils.formatFileSize(new File(realm.getConfiguration().getPath()).length());
                listAdapter.notifyItemChanged(clearAllMemoryRow);
                MusicPlayer.closeLayoutMediaPlayer();
            });
        });
    }

    private void showClearCashDialog() {
        String[] data = new String[7];
        FileUtils fileUtils = new FileUtils();
        data[0] = cacheSize;
        data[1] = fileUtils.getImageFileSize();
        data[2] = fileUtils.getVideoFileSize();
        data[3] = fileUtils.getDocumentFileSize();
        data[4] = fileUtils.getAudioFileSize();
        data[5] = fileUtils.getMapFileSize();
        data[6] = fileUtils.getOtherFileSize();
        if (getContext() != null && data != null && data.length == 7) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.st_title_Clear_Cache)
                    .customView(R.layout.st_dialog_clear_cach, true)
                    .positiveText(R.string.st_title_Clear_Cache)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .show();

            View dialogView = dialog.getCustomView();
            assert dialogView != null;
            AppCompatCheckBox checkBoxAll = dialogView.findViewById(R.id.all);
            AppCompatCheckBox checkBoxPhoto = dialogView.findViewById(R.id.photo);
            AppCompatCheckBox checkBoxVideo = dialogView.findViewById(R.id.video);
            AppCompatCheckBox checkBoxDocument = dialogView.findViewById(R.id.document);
            AppCompatCheckBox checkBoxAudio = dialogView.findViewById(R.id.audio);
            AppCompatCheckBox checkBoxMap = dialogView.findViewById(R.id.map);
            AppCompatCheckBox checkBoxOtherFiles = dialogView.findViewById(R.id.other);

            AppCompatTextView txtTotalSize = dialogView.findViewById(R.id.allFileSize);
            AppCompatTextView photo = dialogView.findViewById(R.id.photoFileSize);
            AppCompatTextView video = dialogView.findViewById(R.id.videoFileSize);
            AppCompatTextView document = dialogView.findViewById(R.id.documentFileSize);
            AppCompatTextView txtAudio = dialogView.findViewById(R.id.audioFileSize);
            AppCompatTextView txtMap = dialogView.findViewById(R.id.mapFileSize);
            AppCompatTextView txtOtherFiles = dialogView.findViewById(R.id.otherFileSize);
            txtTotalSize.setText(data[0]);
            photo.setText(data[1]);
            video.setText(data[2]);
            document.setText(data[3]);
            txtAudio.setText(data[4]);
            txtMap.setText(data[5]);
            txtOtherFiles.setText(data[6]);

            checkBoxAll.setOnClickListener(v -> {
                boolean tmp = ((AppCompatCheckBox) v).isChecked();
                checkBoxPhoto.setChecked(tmp);
                checkBoxVideo.setChecked(tmp);
                checkBoxDocument.setChecked(tmp);
                checkBoxAudio.setChecked(tmp);
                checkBoxMap.setChecked(tmp);
                checkBoxOtherFiles.setChecked(tmp);
            });

            checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            });
            checkBoxPhoto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                checkBoxAll.setChecked(state);
            });
            checkBoxVideo.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                checkBoxAll.setChecked(state);
            });
            checkBoxDocument.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                checkBoxAll.setChecked(state);
            });
            checkBoxAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                checkBoxAll.setChecked(state);
            });
            checkBoxMap.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                checkBoxAll.setChecked(state);
            });
            checkBoxOtherFiles.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean state = checkBoxAudio.isChecked() && checkBoxPhoto.isChecked() && checkBoxVideo.isChecked() && checkBoxDocument.isChecked() && checkBoxOtherFiles.isChecked() && checkBoxMap.isChecked();
                checkBoxAll.setChecked(state);
            });

            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setClearCashData(
                            checkBoxPhoto.isChecked(),
                            checkBoxVideo.isChecked(),
                            checkBoxDocument.isChecked(),
                            checkBoxAudio.isChecked(),
                            checkBoxMap.isChecked(),
                            checkBoxOtherFiles.isChecked());
                    dialog.dismiss();
                }
            });
        }
    }

    private void setClearCashData(boolean isPhoto, boolean isVideo, boolean isDocument, boolean isAudio, boolean isMap, boolean isOther) {
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
        AndroidUtils.globalQueue.postRunnable(() -> fileUtils.getFileTotalSize(size -> {
            cacheSize = size;
            listAdapter.notifyItemChanged(clearCashRow);
        }));
    }

    private void setAutoPlayGif() {
        autoPlayGif = autoPlayGif == 0 ? 1 : 0;
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, autoPlayGif).apply();
    }

    private void showAutoDownloadRoamingDialog() {
        Integer[] values = new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1)
        };
        if (getContext() != null && values != null) {
            new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.title_auto_download_roaming)
                    .items(R.array.auto_download_data)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(R.string.B_ok)
                    .onPositive((dialog, which) -> setAutoDownloadOverRoaming(dialog.getSelectedIndices()))
                    .negativeText(R.string.B_cancel)
                    .show();
        }
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

        for (Integer item : items) {
            String roamingType = "";
            if (item > -1) {
                if ((item == 0)) {
                    roamingType = SHP_SETTING.KEY_AD_ROAMING_PHOTO;
                } else if ((item == 1)) {
                    roamingType = SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE;
                } else if ((item == 2)) {
                    roamingType = SHP_SETTING.KEY_AD_ROAMING_VIDEO;
                } else if ((item == 3)) {
                    roamingType = SHP_SETTING.KEY_AD_ROAMING_FILE;
                } else if ((item == 4)) {
                    roamingType = SHP_SETTING.KEY_AD_ROAMING_MUSIC;
                } else if ((item == 5)) {
                    roamingType = SHP_SETTING.KEY_AD_ROAMING_GIF;
                }
                sharedPreferences.edit().putInt(roamingType, item).apply();
            }
        }
    }

    private void showAutoDownloadWifiDialog() {
        Integer[] values = new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, 5)
        };
        if (getContext() != null && values != null) {
            new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.title_auto_download_wifi)
                    .items(R.array.auto_download_data)
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(R.string.B_ok)
                    .onPositive((dialog, which) -> setAutoDownloadOverWifi(dialog.getSelectedIndices()))
                    .negativeText(R.string.cancel)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .show();
        }
    }

    private void setAutoDownloadOverWifi(@NotNull Integer[] items) {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1)
                .apply();

        for (Integer item : items) {
            switch (item) {
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

    private void showAutoDownloadDataDialog() {
        Integer[] values = new Integer[]{
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1),
                sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, 5)
        };
        if (getContext() != null && values != null) {
            new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.title_auto_download_data)
                    .items(R.array.auto_download_data)
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallbackMultiChoice(values, (dialog, which, text) -> true).positiveText(getResources().getString(R.string.B_ok))
                    .onPositive((dialog, which) -> setAutoDownloadOverData(dialog.getSelectedIndices()))
                    .negativeText(getResources().getString(R.string.B_cancel))
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .show();
        }
    }

    private void setAutoDownloadOverData(@NotNull Integer[] items) {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1)
                .putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1)
                .apply();

        for (Integer item : items) {
            switch (item) {
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

    private void showKeepMediaDialog() {
        if (getContext() != null) {
            new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.st_keepMedia)
                    .titleGravity(GravityEnum.START)
                    .titleColor(getResources().getColor(android.R.color.black))
                    .items(R.array.keepMedia)
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallbackSingleChoice(keepMedia, (dialog, itemView, which, text) -> false).positiveText(R.string.B_ok)
                    .negativeText(R.string.B_cancel)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .onPositive((dialog, which) -> setKeepMediaText(dialog.getSelectedIndex())).show();
        }
    }

    private void getStorageData() {
        sdCardSize = FileUtils.getSdCardPathList().size();
        new FileUtils().getFileTotalSize(new FileUtils.Delegate() {
            @Override
            public void onSize(String size) {
                cacheSize = size;
            }
        });
        DbManager.getInstance().doRealmTask(realm -> {
            totalSize = FileUtils.formatFileSize(new File(realm.getConfiguration().getPath()).length());
        });
        switch (sharedPreferences.getInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0)) {
            case 30:
                keepMedia = 1;
                break;
            case 180:
                keepMedia = 2;
                break;
            default:
                keepMedia = 0;
                break;
        }
        autoPlayGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS) != 0 ? 1 : 0;
        sdkEnable = sharedPreferences.getInt(SHP_SETTING.KEY_SDK_ENABLE, 0) != 0 ? 1 : 0;
        updateRows();
    }

    private void updateRows() {
        sectionOneHeaderRow = 0;
        dataUsageRow = 1;
        keepMediaRow = 2;
        sectionOneDividerRow = 3;
        sectionTwoHeaderRow = 4;
        autoDownloadDataRow = 5;
        autoDownloadWifiRow = 6;
        autoDownloadRoamingRow = 7;
        sectionTwoDividerRow = 8;
        sectionThreeHeaderRow = 9;
        autoPlayGifRow = 10;
        sectionThreeDividerRow = 11;
        sectionFourHeaderRow = 12;
        clearCashRow = 13;
        clearAllMemoryRow = 14;
        if (sdCardSize > 0) {
            sdkEnableRow = 15;
            sectionFourDividerRow = 16;
            rowCount = 17;
        } else {
            sectionFourDividerRow = 15;
            rowCount = 16;
        }
        loading = false;
        if (listAdapter != null){
            listAdapter.notifyDataSetChanged();
        }
    }

    private String setKeepMediaText(int which) {
        switch (which) {
            case 1:
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 30).apply();
                return context.getResources().getString(R.string.keep_media_1month);
            case 2:
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 180).apply();
                return context.getResources().getString(R.string.keep_media_6month);
            default:
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0).apply();
                return context.getResources().getString(R.string.keep_media_forever);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.DataSettings));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type != 0 && type != 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(context);
                    break;
                case 1:
                    view = new TextSettingsCell(context);
                    break;
                case 2:
                    view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));
                    break;
                case 3:
                    view = new TextCheckCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == sectionOneHeaderRow) {
                        headerCell.setText(getString(R.string.disk_and_network_usage));
                    } else if (position == sectionTwoHeaderRow) {
                        headerCell.setText(getString(R.string.auto_download_media));
                    } else if (position == sectionThreeHeaderRow) {
                        headerCell.setText(getString(R.string.auto_play_media));
                    } else if (position == sectionFourHeaderRow) {
                        headerCell.setText(getString(R.string.cache));
                    }
                    break;
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    if (position == dataUsageRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.data_usage), "", true);
                    } else if (position == keepMediaRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_keepMedia), setKeepMediaText(keepMedia), true);
                    } else if (position == autoDownloadDataRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_auto_download_data), "", true);
                    } else if (position == autoDownloadWifiRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_auto_download_wifi), "", true);
                    } else if (position == autoDownloadRoamingRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_auto_download_roaming), "", true);
                    } else if (position == clearCashRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_title_Clear_Cache), cacheSize, true);
                    } else if (position == clearAllMemoryRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.clean_up_chat_rooms), totalSize, true);
                    }
                    break;
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == autoPlayGifRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_auto_gif), autoPlayGif  == 1, true);
                    } else if (position == sdkEnableRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.save_data_in_sd_card), sdkEnable  == 1, true);
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == sectionOneHeaderRow || position == sectionTwoHeaderRow || position == sectionThreeHeaderRow || position == sectionFourHeaderRow) {
                return 0;
            } else if (position == dataUsageRow || position == keepMediaRow || position == autoDownloadDataRow ||
                    position == autoDownloadWifiRow || position == autoDownloadRoamingRow || position == clearCashRow || position == clearAllMemoryRow) {
                return 1;
            } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow || position == sectionFourDividerRow) {
                return 2;
            } else if (position == autoPlayGifRow || position == sdkEnableRow) {
                return 3;
            }
            return position;
        }

        @Override
        public int getItemCount() {
            return loading ? 0 : rowCount;
        }
    }
}