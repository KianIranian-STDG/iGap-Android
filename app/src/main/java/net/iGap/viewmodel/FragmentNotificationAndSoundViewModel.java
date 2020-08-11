package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */


import android.content.SharedPreferences;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;

public class FragmentNotificationAndSoundViewModel extends ViewModel {

    public int ledColorMessage;
    public int ledColorGroup;
    public ObservableBoolean isAlertMassage = new ObservableBoolean();
    public ObservableBoolean isAlertGroup = new ObservableBoolean();
    public ObservableBoolean isMassagePreview = new ObservableBoolean();
    public ObservableBoolean isGroupPreView = new ObservableBoolean();
    public MutableLiveData<Integer> messageLedColor = new MutableLiveData<>();
    public MutableLiveData<Integer> groupLedColor = new MutableLiveData<>();
    public ObservableInt callbackVibrateMessage = new ObservableInt(R.string.array_Default);
    public ObservableInt callbackVibrateGroup = new ObservableInt(R.string.array_Default);
    public ObservableInt callbackPopUpNotificationMessage = new ObservableInt();
    public ObservableInt callbackPopUpNotificationGroup = new ObservableInt(R.string.st_sound);
    public ObservableField<String> callbackSoundMessage = new ObservableField<>();
    public ObservableField<String> callBackSoundGroup = new ObservableField<>();
    public SingleLiveEvent<Boolean> showMessageLedDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showGroupLedDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showMessageVibrationDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showGroupVibrationDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showMessagePopupNotification = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showGroupPopupNotification = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showMessageSound = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showGroupSound = new SingleLiveEvent<>();

    public ObservableBoolean isAppSound = new ObservableBoolean();
    public ObservableBoolean isInAppVibration = new ObservableBoolean();
    public ObservableBoolean isInAppPreView = new ObservableBoolean();
    public ObservableBoolean isSoundInChat = new ObservableBoolean();
    public ObservableBoolean isSeparateNotification = new ObservableBoolean();
    public ObservableBoolean isKeepService = new ObservableBoolean();
    public SingleLiveEvent<Integer> startVibration = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> playSound = new SingleLiveEvent<>();
    private String[] soundList;
    private SharedPreferences sharedPreferences;
    public int messageDialogSoundMessage;
    private int messageChooseSound;
    private int groupChooseSound;
    private int messageChooseVibrate;
    private int groupChooseVibrate;
    private int messageChoosePopup;
    private int groupChoosePopup;


    public FragmentNotificationAndSoundViewModel(SharedPreferences sharedPreferences, String[] sound) {
        this.sharedPreferences = sharedPreferences;
        this.soundList = sound;
        getInfo();
        setMessageVibrateTime(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0), false);
        setGroupVibrateTime(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0), false);
        saveSettingPopupConditionMessage(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0));
        saveSettingPopupConditionGroup(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0));
        getSoundMessagePosition(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0), false);
        getSoundGroupPosition(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0), false);
    }

    //===============================================================================
    //================================Getters/Setters================================
    //===============================================================================

    public void setAlertMessage(Boolean isChecked) {
        isAlertMassage.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 0).apply();
        }

    }

    public void setMessagePreview(Boolean isChecked) {
        isMassagePreview.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 0).apply();
        }
    }

    public void setAlertGroup(Boolean isChecked) {
        isAlertGroup.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 0).apply();
        }

    }

    public void setMessagePreviewGroup(Boolean isChecked) {
        isGroupPreView.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1)
                    .apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 0).apply();
        }
    }

    private void setAppSound(Boolean isChecked) {
        isAppSound.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 0).apply();
        }

    }

    private void setInAppVibrate(Boolean isChecked) {
        isInAppVibration.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 0).apply();
        }

    }

    private void setInAppPreView(Boolean isChecked) {
        isInAppPreView.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 0).apply();
        }

    }

    private void setInSoundChat(Boolean isChecked) {
        isSoundInChat.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 0).apply();
        }

    }

    public void setSeparateNotification(Boolean isChecked) {
        isSeparateNotification.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 0).apply();
        }
    }

    private void setKeepService(Boolean isChecked) {
        isKeepService.set(isChecked);
        if (isChecked) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1).apply();
        } else {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 0).apply();
        }

    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    /**
     * alert & preview
     **/
    public void onClickAlertMessage() {
        isAlertMassage.set(!isAlertMassage.get());
    }

    public void onClickMessagePreView() {
        isMassagePreview.set(!isMassagePreview.get());
    }

    public void onClickAlertGroup() {
        isAlertGroup.set(!isAlertGroup.get());
    }

    public void onClickMessagePreViewGroup() {
        isGroupPreView.set(!isGroupPreView.get());
    }

    /**
     * led settings
     **/
    public void onClickMessageLed() {
        showMessageLedDialog.setValue(true);
    }

    public void onClickGroupLed() {
        showGroupLedDialog.setValue(true);
    }

    public void setMessagePickerColor(int color) {
        messageLedColor.setValue(color);
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, color).apply();
    }

    public void setGroupPickerColor(int color) {
        groupLedColor.setValue(color);
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, color).apply();
    }

    /**
     * Vibration settings
     **/
    public void onClickMessageVibration() {
        showMessageVibrationDialog.setValue(true);
    }

    public void onClickGroupVibration() {
        showGroupVibrationDialog.setValue(true);
    }

    public void setMessageVibrateText(int messageChooseVibrate) {
        switch (messageChooseVibrate) {
            case 0:
                callbackVibrateMessage.set(R.string.array_Default);
                break;
            case 1:
                callbackVibrateMessage.set(R.string.array_Short);
                break;
            case 2:
                callbackVibrateMessage.set(R.string.array_Long);
                break;
            case 3:
                callbackVibrateMessage.set(R.string.array_Only_if_silent);
                break;
            case 4:
                callbackVibrateMessage.set(R.string.array_Disable);
                break;
        }
    }

    public void setGroupVibrateText(int groupChooseVibrate) {
        switch (groupChooseVibrate) {
            case 0:
                callbackVibrateGroup.set(R.string.array_Default);
                break;
            case 1:
                callbackVibrateGroup.set(R.string.array_Short);
                break;
            case 2:
                callbackVibrateGroup.set(R.string.array_Long);
                break;
            case 3:
                callbackVibrateGroup.set(R.string.array_Only_if_silent);
                break;
            case 4:
                callbackVibrateGroup.set(R.string.array_Disable);
                break;
        }
    }

    public void setMessageVibrateTime(int which, boolean isVibrate) {
        messageChooseVibrate = which;
        if (isVibrate) {
            switch (which) {
                case 0:
                    startVibration.setValue(350);
                    break;
                case 1:
                    startVibration.setValue(200);
                    break;
                case 2:
                    startVibration.setValue(500);
                    break;
                case 3:
                    startVibration.setValue(-1);
                    break;
                case 4:
                    startVibration.setValue(0);
                    break;
            }
        }
        setMessageVibrateText(which);

        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, which).apply();
    }

    public void setGroupVibrateTime(int which, boolean isSetVibrate) {
        groupChooseVibrate = which;
        if (isSetVibrate) {
            switch (which) {
                case 0:
                    startVibration.setValue(350);
                    break;
                case 1:
                    startVibration.setValue(200);
                    break;
                case 2:
                    startVibration.setValue(500);
                    break;
                case 3:
                    startVibration.setValue(-1);
                    break;
                case 4:
                    startVibration.setValue(0);
                    break;
            }
        }

        setGroupVibrateText(which);
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, which).apply();

    }

    public void chooseVibrate() {
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, messageChooseVibrate).apply();
    }

    public void groupChooseVibrate() {
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, groupChooseVibrate).apply();
    }

    /**
     * popupNotification
     **/
    public void onClickMessagePopUpNotification() {
        showMessagePopupNotification.setValue(true);
    }

    public void onClickGroupPopUpNotification() {
        showGroupPopupNotification.setValue(true);
    }

    public void saveSettingPopupConditionMessage(int which) {
        messageChoosePopup = which;
        switch (which) {
            case 0:
                callbackPopUpNotificationMessage.set(R.string.array_No_popup);
                break;
            case 1:
                callbackPopUpNotificationMessage.set(R.string.array_Only_when_screen_on);
                break;
            case 2:
                callbackPopUpNotificationMessage.set(R.string.array_Only_when_screen_off);
                break;
            case 3:
                callbackPopUpNotificationMessage.set(R.string.array_Always_show_popup);
                break;
        }
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, which).apply();
    }

    public void saveSettingPopupConditionGroup(int which) {
        groupChoosePopup = which;
        switch (which) {
            case 0:
                callbackPopUpNotificationGroup.set(R.string.array_No_popup);
                break;
            case 1:
                callbackPopUpNotificationGroup.set(R.string.array_Only_when_screen_on);
                break;
            case 2:
                callbackPopUpNotificationGroup.set(R.string.array_Only_when_screen_off);
                break;
            case 3:
                callbackPopUpNotificationGroup.set(R.string.array_Always_show_popup);
                break;
        }
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, which).apply();
    }

    public void setChooseMessagePopup() {
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, messageChoosePopup).apply();
    }

    public void setChooseGroupPopup() {
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, groupChoosePopup).apply();
    }

    /**
     * Sound settings
     **/

    public void onClickSoundMessage() {
        showMessageSound.setValue(true);
    }

    public void onClickSoundGroup() {
        showGroupSound.setValue(true);
    }

    public void setChooseMessageSound() {
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, messageChooseSound).apply();
    }

    public void setChooseGroupSound() {
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, groupChooseSound).apply();
    }

    public void getSoundMessagePosition(int which, boolean isSetSound) {
        messageChooseSound = which;
        callbackSoundMessage.set(soundList[which]);
        if (isSetSound)
            playSound(which);
    }

    public void getSoundGroupPosition(int which, boolean isSound) {
        groupChooseSound = which;
        callBackSoundGroup.set(soundList[which]);
        if (isSound)
            playSound(which);
    }

    /**
     * In-app settings
     **/

    public void onClickInAppSound() {
        isAppSound.set(!isAppSound.get());
    }

    public void onCheckedChangedAppSound(boolean isChecked) {
        setAppSound(isChecked);
    }

    public void onClickInAppVibration() {
        isInAppVibration.set(!isInAppVibration.get());
    }

    public void onCheckedChangedInAppVibration(boolean isChecked) {
        setInAppVibrate(isChecked);
    }

    public void onClickInAppPreView() {
        isInAppPreView.set(!isInAppPreView.get());
    }

    public void onCheckedChangedInAppPreView(boolean isChecked) {
        setInAppPreView(isChecked);
    }

    public void onClickSoundInChat() {
        isSoundInChat.set(!isSoundInChat.get());
    }

    public void onClickSeparateNotification() {
        isSeparateNotification.set(!isSeparateNotification.get());
    }

    public void onCheckedChangedSoundInChat(boolean isChecked) {
        setInSoundChat(isChecked);
    }

    public void onClickKeepService() {
        isKeepService.set(!isKeepService.get());
    }

    public void onCheckedChangedKeepService(boolean isChecked) {
        setKeepService(isChecked);
    }

    public void onResetDataInSharedPreference() {
        sharedPreferences.edit()
                .putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1)
                .putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1)
                .putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0)
                .putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0)
                .putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0)
                .putString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, G.fragmentActivity.getResources().getString(R.string.Default_Notification_tone))
                .putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1)
                .putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1)
                .putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0)
                .putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0)
                .putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0)
                .putString(SHP_SETTING.KEY_STNS_SOUND_GROUP, G.fragmentActivity.getResources().getString(R.string.Default_Notification_tone))
                .putInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1)
                .putInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1)
                .putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1)
                .putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1)
                .putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1)
                .putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1)
                .putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1)
                .putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_1_hour))
                .putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792)
                .putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792)
                .apply();
    }

    private boolean getBoolean(int num) {
        return num != 0;
    }

    private void getInfo() {
        isAlertMassage.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1)));
        isAlertGroup.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1)));
        isMassagePreview.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1)));
        isGroupPreView.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1)));
        ledColorMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
        ledColorGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
        messageDialogSoundMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
        isAppSound.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1)));
        isInAppVibration.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1)));
        isInAppPreView.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1)));
        isSoundInChat.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1)));
        isSeparateNotification.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1)));
        isKeepService.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1)));
    }

    public void playSound(int which) {
        int musicId;
        switch (which) {
            case 0:
                musicId = R.raw.igap;
                break;
            case 1:
                musicId = R.raw.aooow;
                break;
            case 2:
                musicId = R.raw.bbalert;
                break;
            case 3:
                musicId = R.raw.boom;
                break;
            case 4:
                musicId = R.raw.bounce;
                break;
            case 5:
                musicId = R.raw.doodoo;
                break;
            case 6:
                musicId = R.raw.jing;
                break;
            case 7:
                musicId = R.raw.lili;
                break;
            case 8:
                musicId = R.raw.msg;
                break;
            case 9:
                musicId = R.raw.newa;
                break;
            case 10:
                musicId = R.raw.none;
                break;
            case 11:
                musicId = R.raw.onelime;
                break;
            case 12:
                musicId = R.raw.tone;
                break;
            case 13:
                musicId = R.raw.woow;
                break;
            default:
                musicId = R.raw.igap;
        }

        playSound.setValue(musicId);

    }
}
