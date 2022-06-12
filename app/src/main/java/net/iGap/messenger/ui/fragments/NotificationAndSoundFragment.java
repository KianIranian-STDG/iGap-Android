package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.NotificationLedCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.cell.TextCheckCell;
import net.iGap.messenger.ui.cell.TextDetailSettingsCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

public class NotificationAndSoundFragment extends BaseFragment {

    private final int sectionOneHeaderRow = 0;
    private final int alertRow = 1;
    private final int previewRow = 2;
    private final int inductorLedRow = 3;
    private final int vibrationRow = 4;
    private final int popupRow = 5;
    private final int soundRow = 6;
    private final int sectionOneDividerRow = 7;
    private final int sectionTwoHeaderRow = 8;
    private final int groupAlertRow = 9;
    private final int groupPreviewRow = 10;
    private final int groupInductorLedRow = 11;
    private final int groupVibrationRow = 12;
    private final int groupPopupRow = 13;
    private final int groupSoundRow = 14;
    private final int sectionTwoDividerRow = 15;
    private final int sectionThreeHeaderRow = 16;
    private final int appSoundRow = 17;
    private final int appVibrationRow = 18;
    private final int appPreviewRow = 19;
    private final int soundInChatRow = 20;
    private final int sectionThreeDividerRow = 21;
    private final int sectionFourHeaderRow = 22;
    private final int separateNotificationRow = 23;
    private final int keepServiceAliveRow = 24;
    private final int sectionFourDividerRow = 25;
    private final int sectionFiveHeaderRow = 26;
    private final int resetAllRow = 27;
    private final int sectionFiveDividerRow = 28;
    private final int rowCount = 29;

    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();
    private RecyclerListView listView;

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
        listView.setAdapter(new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!((TextCheckCell) view).isChecked());
                if (position == alertRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_ALERT_MESSAGE);
                } else if (position == previewRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE);
                } else if (position == groupAlertRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_ALERT_GROUP);
                } else if (position == groupPreviewRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP);
                } else if (position == appSoundRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_APP_SOUND_NEW);
                } else if (position == appVibrationRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW);
                } else if (position == appPreviewRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW);
                } else if (position == soundInChatRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW);
                } else if (position == separateNotificationRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION);
                } else if (position == keepServiceAliveRow) {
                    saveInSharedPreferences(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE);
                }
            } else if (view instanceof NotificationLedCell) {
                if (position == inductorLedRow) {
                    showInductorLedColorPickDialog(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, view);
                } else if (position == groupInductorLedRow) {
                    showInductorLedColorPickDialog(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, view);
                }
            } else if (view instanceof TextSettingsCell) {
                if (position == vibrationRow) {
                    showSettingDialog(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, view, R.string.st_vibrate, R.array.vibrate);
                } else if (position == popupRow) {
                    showSettingDialog(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, view, R.string.st_popupNotification, R.array.popup_Notification);
                } else if (position == soundRow) {
                    showSettingDialog(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, view, R.string.Ringtone, R.array.sound_message);
                } else if (position == groupVibrationRow) {
                    showSettingDialog(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, view, R.string.st_vibrate, R.array.vibrate);
                } else if (position == groupPopupRow) {
                    showSettingDialog(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, view, R.string.st_popupNotification, R.array.popup_Notification);
                } else if (position == groupSoundRow) {
                    showSettingDialog(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, view, R.string.Ringtone, R.array.sound_message);
                }
            } else if (view instanceof TextDetailSettingsCell) {
                showResetDialog();
            }
        });
        return fragmentView;
    }

    private void showResetDialog() {
        new MaterialDialog.Builder(context)
                .title(R.string.st_title_reset)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .content(R.string.st_dialog_reset_all_notification)
                .positiveText(R.string.st_dialog_reset_all_notification_yes)
                .negativeText(R.string.st_dialog_reset_all_notification_no)
                .onPositive((dialog, which) -> {
                    resetSharedPreferencesValues();
                    Toast.makeText(context, R.string.st_reset_all_notification, Toast.LENGTH_SHORT).show();
                    if (getActivity() != null)
                        new HelperFragment(getActivity().getSupportFragmentManager(), new NotificationAndSoundFragment()).remove();
                    new HelperFragment(getActivity().getSupportFragmentManager(), new NotificationAndSoundFragment()).setReplace(false).load();
                }).show();
    }

    private void resetSharedPreferencesValues() {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1)
                .putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1)
                .putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1)
                .putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1)
                .putInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1)
                .putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1)
                .putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792)
                .putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792)
                .putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0)
                .putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0)
                .putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0)
                .putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0)
                .putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0)
                .putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0)
                .putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1)
                .putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1)
                .putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1)
                .putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1)
                .apply();
    }

    private void showSettingDialog(String key, View view, int title, int items) {
        new MaterialDialog.Builder(context)
                .title(title)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .titleGravity(GravityEnum.START)
                .items(items)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .alwaysCallSingleChoiceCallback()
                .itemsCallbackSingleChoice(sharedPreferences.getInt(key, 0), (dialog, view1, which, text) -> {
                    sharedPreferences.edit().putInt(key, which).apply();
                    return true;
                }).positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                .onPositive((dialog, which) -> {
                    switch (key) {
                        case SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE:
                        case SHP_SETTING.KEY_STNS_VIBRATE_GROUP:
                            ((TextSettingsCell) view).setTextAndValue(context.getString(title), setVibrate(sharedPreferences.getInt(key, 0),true), true);
                            break;
                        case SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE:
                        case SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP:
                            ((TextSettingsCell) view).setTextAndValue(context.getString(title), setPopupText(sharedPreferences.getInt(key, 0)), true);
                            break;
                        case SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION:
                        case SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION:
                            ((TextSettingsCell) view).setTextAndValue(context.getString(title), setSoundPosition(sharedPreferences.getInt(key, 0),true), true);
                            break;
                    }
                }).show();
    }

    private void showInductorLedColorPickDialog(String key, View view) {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .customView(R.layout.popup_colorpicker, true)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .title(R.string.st_led_color).titleGravity(GravityEnum.START)
                .positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                .onNegative((dialog1, which) -> dialog1.dismiss()).build();
        View customView = dialog.getCustomView();
        ColorPicker colorPicker = customView.findViewById(R.id.picker);
        SVBar svBar = customView.findViewById(R.id.svBar);
        OpacityBar opacityBar = customView.findViewById(R.id.opacityBar);
        colorPicker.addSVBar(svBar);
        colorPicker.addOpacityBar(opacityBar);
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
            sharedPreferences.edit().putInt(key, colorPicker.getColor()).apply();
            ((NotificationLedCell) view).setTextAndColor(getResources().getString(R.string.st_led_color), colorPicker.getColor(), true);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void saveInSharedPreferences(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, sharedPreferences.getInt(key, 1) == 1 ? 0 : 1);
        editor.apply();
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.NotificationsAndSounds));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    private String setSoundPosition(int which,boolean playSound) {
        int musicId;
        String title;
        switch (which) {
            case 1:
                musicId = R.raw.aooow;
                title = "aooow";
                break;
            case 2:
                musicId = R.raw.bbalert;
                title = "bbalert";
                break;
            case 3:
                musicId = R.raw.boom;
                title = "boom";
                break;
            case 4:
                musicId = R.raw.bounce;
                title = "bounce";
                break;
            case 5:
                musicId = R.raw.doodoo;
                title = "doodoo";
                break;
            case 6:
                musicId = R.raw.jing;
                title = "jing";
                break;
            case 7:
                musicId = R.raw.lili;
                title = "lili";
                break;
            case 8:
                musicId = R.raw.msg;
                title = "msg";
                break;
            case 9:
                musicId = R.raw.newa;
                title = "newa";
                break;
            case 10:
                musicId = R.raw.none;
                title = "none";
                break;
            case 11:
                musicId = R.raw.onelime;
                title = "onelime";
                break;
            case 12:
                musicId = R.raw.tone;
                title = "tone";
                break;
            case 13:
                musicId = R.raw.woow;
                title = "woow";
                break;
            default:
                musicId = R.raw.igap;
                title = context.getResources().getString(R.string.Default_Notification_tone);
                break;
        }
        if (playSound) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, musicId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> mp.release());
        }
        return title;
    }

    private String setPopupText(int which) {
        switch (which) {
            case 1:
                return context.getResources().getString(R.string.array_Only_when_screen_on);
            case 2:
                return context.getResources().getString(R.string.array_Only_when_screen_off);
            case 3:
                return context.getResources().getString(R.string.array_Always_show_popup);
            default:
                return context.getResources().getString(R.string.array_No_popup);
        }
    }

    private String setVibrate(int which,boolean playVibrate) {
        int vibrateId;
        String title;
        switch (which) {
            case 1:
                vibrateId = 200;
                title = context.getResources().getString(R.string.array_Short);
                break;
            case 2:
                vibrateId = 500;
                title = context.getResources().getString(R.string.array_Long);
                break;
            case 3:
                vibrateId = -1;
                title = context.getResources().getString(R.string.array_Only_if_silent);
                break;
            case 4:
                vibrateId = 0;
                title = context.getResources().getString(R.string.array_Disable);
                break;
            default:
                vibrateId = 350;
                title = context.getResources().getString(R.string.array_Default);
                break;
        }
        if (playVibrate){
            if (vibrateId == -1) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                }
            } else {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(vibrateId);
            }
        }
        return title;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type != 0 && type != 4;
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
                    view = new TextCheckCell(context);
                    break;
                case 2:
                    view = new NotificationLedCell(context);
                    break;
                case 3:
                    view = new TextSettingsCell(context);
                    break;
                case 4:
                    view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));
                    break;
                case 5:
                    view = new TextDetailSettingsCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == sectionOneHeaderRow) {
                        headerCell.setText(getString(R.string.st_title_messageNotification));
                    } else if (position == sectionTwoHeaderRow) {
                        headerCell.setText(getString(R.string.st_title_groupNotification));
                    } else if (position == sectionThreeHeaderRow) {
                        headerCell.setText(getString(R.string.st_title_in_app_notification));
                    } else if (position == sectionFourHeaderRow) {
                        headerCell.setText(getString(R.string.st_title_other));
                    } else if (position == sectionFiveHeaderRow) {
                        headerCell.setText(getString(R.string.st_reset_all_notification));
                    }
                    break;
                case 1:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == alertRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_alert), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1)  == 1, true);
                    } else if (position == previewRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_messagePreview), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1)  == 1, true);
                    } else if (position == groupAlertRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_alert), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1)  == 1, true);
                    } else if (position == groupPreviewRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_messagePreview), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1)  == 1, true);
                    } else if (position == appSoundRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_in_app_sound), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1)  == 1, true);
                    } else if (position == appVibrationRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_in_app_vibrate), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1)  == 1, true);
                    } else if (position == appPreviewRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_in_app_preview), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1)  == 1, true);
                    } else if (position == soundInChatRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_in_chat_sound), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1) == 1, false);
                    } else if (position == separateNotificationRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.separate_notifications), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1) == 1, true);
                    } else if (position == keepServiceAliveRow) {
                        textCheckCell.setTextAndValueAndCheck(context.getString(R.string.st_keep_alive_service), context.getString(R.string.st_text_keep_alive_service), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1), true, false);
                    }
                    break;
                case 2:
                    NotificationLedCell notificationLedCell = (NotificationLedCell) holder.itemView;
                    if (position == inductorLedRow) {
                        notificationLedCell.setTextAndColor(getResources().getString(R.string.st_led_color), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792), true);
                    } else if (position == groupInductorLedRow) {
                        notificationLedCell.setTextAndColor(getResources().getString(R.string.st_led_color), sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792), true);
                    }
                    break;
                case 3:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    if (position == vibrationRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_vibrate), setVibrate(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0),false), true);
                    } else if (position == popupRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_popupNotification), setPopupText(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0)), true);
                    } else if (position == soundRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_sound), setSoundPosition(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0),false), false);
                    } else if (position == groupVibrationRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_vibrate), setVibrate(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0),false), true);
                    } else if (position == groupPopupRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_popupNotification), setPopupText(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0)), true);
                    } else if (position == groupSoundRow) {
                        textSettingsCell.setTextAndValue(context.getString(R.string.st_sound), setSoundPosition(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0),false), false);
                    }
                    break;
                case 5:
                    TextDetailSettingsCell settingsCell = (TextDetailSettingsCell) holder.itemView;
                    settingsCell.setMultilineDetail(true);
                    if (position == resetAllRow) {
                        settingsCell.setTextAndValue(context.getString(R.string.st_reset_all_notification), context.getString(R.string.st_dialog_reset_all_notification), false);
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == sectionOneHeaderRow || position == sectionTwoHeaderRow || position == sectionThreeHeaderRow || position == sectionFourHeaderRow || position == sectionFiveHeaderRow) {
                return 0;
            } else if (position == alertRow || position == previewRow || position == groupAlertRow || position == groupPreviewRow || position == appSoundRow || position == appVibrationRow ||
                    position == appPreviewRow || position == soundInChatRow || position == separateNotificationRow || position == keepServiceAliveRow) {
                return 1;
            } else if (position == inductorLedRow || position == groupInductorLedRow) {
                return 2;
            } else if (position == vibrationRow || position == popupRow || position == soundRow || position == groupVibrationRow || position == groupPopupRow || position == groupSoundRow) {
                return 3;
            } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow || position == sectionFourDividerRow || position == sectionFiveDividerRow) {
                return 4;
            } else if (position == resetAllRow) {
                return 5;
            }
            return position;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }
}
