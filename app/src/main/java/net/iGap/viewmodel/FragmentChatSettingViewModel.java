package net.iGap.viewmodel;

import android.content.SharedPreferences;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.model.ThemeModel;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.StartupActions;
import net.iGap.module.Theme;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FragmentChatSettingViewModel extends ViewModel {

    private final int MAX_TEXT_SIZE = 30;
    private final int MIN_TEXT_SIZE = 11;

    private ObservableBoolean isTime = new ObservableBoolean(false);
    private ObservableBoolean isShowVote = new ObservableBoolean(false);
    private ObservableBoolean isSenderNameGroup = new ObservableBoolean(false);
    private ObservableBoolean isSendEnter = new ObservableBoolean(false);
    private ObservableBoolean isSoundPlayInChat = new ObservableBoolean(false);
    private ObservableBoolean isInAppBrowser = new ObservableBoolean(false);
    private ObservableBoolean isCompress = new ObservableBoolean(false);
    private ObservableBoolean isTrim = new ObservableBoolean(false);
    private ObservableBoolean isDefaultPlayer = new ObservableBoolean(false);
    private ObservableBoolean isCrop = new ObservableBoolean(false);
    private ObservableBoolean isCameraButtonSheet = new ObservableBoolean(true);
    private ObservableField<String> textSizeValue = new ObservableField<>("14");
    private ObservableInt dateType = new ObservableInt(R.string.miladi);
    private ObservableInt textSize = new ObservableInt(14 - MIN_TEXT_SIZE);
    private ObservableInt textSizeMax = new ObservableInt(MAX_TEXT_SIZE - MIN_TEXT_SIZE);
    private SingleLiveEvent<Boolean> goToChatBackgroundPage = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> goToDateFragment = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedThemePosition = new MutableLiveData<>();
    private MutableLiveData<List<ThemeModel>> themeList = new SingleLiveEvent<>();
    private MutableLiveData<Integer> updateTextSizeSampleView = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> updateNewTheme = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> updateTwoPaneView = new SingleLiveEvent<>();
    private MutableLiveData<String> setChatBackground = new MutableLiveData<>();
    private MutableLiveData<Integer> setChatBackgroundDefault = new MutableLiveData<>();

    private SharedPreferences sharedPreferences;

    public FragmentChatSettingViewModel(@NotNull SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;

        isCrop.set(sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) != 0);
        isCameraButtonSheet.set(sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true));
        isShowVote.set(sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1) != 0);
        isSenderNameGroup.set(sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0) != 0);
        isCompress.set(sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 0) != 0);
        isSendEnter.set(sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0) != 0);
        isSoundPlayInChat.set(sharedPreferences.getInt(SHP_SETTING.KEY_PLAY_SOUND_IN_CHAT, 1) != 0);
        isInAppBrowser.set(sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1) != 0);
        isTrim.set(sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) != 0);
        isDefaultPlayer.set(sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1) != 0);
        isTime.set(sharedPreferences.getBoolean(SHP_SETTING.KEY_WHOLE_TIME, false));

        textSize.set(sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - MIN_TEXT_SIZE);
        setTextSizeValue(sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14));
        dateIsChange();

        themeList.setValue(new Theme().getThemeList());
        selectedThemePosition.setValue(themeList.getValue().indexOf(new ThemeModel(sharedPreferences.getInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DEFAULT), 0)));
    }

    public ObservableBoolean getIsTime() {
        return isTime;
    }

    public ObservableBoolean getIsShowVote() {
        return isShowVote;
    }

    public ObservableBoolean getIsSenderNameGroup() {
        return isSenderNameGroup;
    }

    public ObservableBoolean getIsSendEnter() {
        return isSendEnter;
    }

    public ObservableBoolean getIsSoundPlayInChat() {
        return isSoundPlayInChat;
    }

    public ObservableBoolean getIsInAppBrowser() {
        return isInAppBrowser;
    }

    public ObservableBoolean getIsCompress() {
        return isCompress;
    }

    public ObservableBoolean getIsTrim() {
        return isTrim;
    }

    public ObservableBoolean getIsDefaultPlayer() {
        return isDefaultPlayer;
    }

    public ObservableBoolean getIsCrop() {
        return isCrop;
    }

    public ObservableBoolean getIsCameraButtonSheet() {
        return isCameraButtonSheet;
    }

    public ObservableField<String> getTextSizeValue() {
        return textSizeValue;
    }

    public ObservableInt getDateType() {
        return dateType;
    }

    public ObservableInt getTextSizeMax() {
        return textSizeMax;
    }

    public ObservableInt getTextSize() {
        return textSize;
    }

    public SingleLiveEvent<Boolean> getGoToChatBackgroundPage() {
        return goToChatBackgroundPage;
    }

    public MutableLiveData<Boolean> getGoToDateFragment() {
        return goToDateFragment;
    }

    public MutableLiveData<Integer> getSelectedThemePosition() {
        return selectedThemePosition;
    }

    public MutableLiveData<List<ThemeModel>> getThemeList() {
        return themeList;
    }

    public MutableLiveData<Integer> getUpdateTextSizeSampleView() {
        return updateTextSizeSampleView;
    }

    public MutableLiveData<String> getSetChatBackground() {
        return setChatBackground;
    }

    public MutableLiveData<Integer> getSetChatBackgroundDefault() {
        return setChatBackgroundDefault;
    }

    public SingleLiveEvent<Boolean> getUpdateNewTheme() {
        return updateNewTheme;
    }

    public SingleLiveEvent<Boolean> getUpdateTwoPaneView() {
        return updateTwoPaneView;
    }

    public void onDateClick() {
        goToDateFragment.postValue(true);
    }

    public void onClickCompress() {
        isCompress.set(!isCompress.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_COMPRESS, isCompress.get() ? 1 : 0).apply();
    }

    public void onClickTime() {
        isTime.set(!isTime.get());
        sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_WHOLE_TIME, isTime.get()).apply();
        G.isTimeWhole = isTime.get();
        if (G.onNotifyTime != null) {
            G.onNotifyTime.notifyTime();
        }
    }

    public void onClickTrim() {
        isTrim.set(!isTrim.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_TRIM, isTrim.get() ? 1 : 0).apply();
    }

    public void onClickDefaultVideo() {
        isDefaultPlayer.set(!isDefaultPlayer.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, isDefaultPlayer.get() ? 1 : 0).apply();
    }

    public void onClickCrop() {
        isCrop.set(!isCrop.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_CROP, isCrop.get() ? 1 : 0).apply();
    }

    public void onClickCameraButtonSheet() {
        isCameraButtonSheet.set(!isCameraButtonSheet.get());
        sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, isCameraButtonSheet.get()).apply();
    }

    public void onClickChatBackground() {
        goToChatBackgroundPage.setValue(true);
    }

    public void onClickSenderNameGroup() {
        isSenderNameGroup.set(!isSenderNameGroup.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, isSenderNameGroup.get() ? 1 : 0).apply();
        G.showSenderNameInGroup = isSenderNameGroup.get();
    }

    public void onClickSendEnter() {
        isSendEnter.set(!isSendEnter.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_SEND_BT_ENTER, isSendEnter.get() ? 1 : 0).apply();
    }

    public void onPlaySoundClick() {
        isSoundPlayInChat.set(!isSoundPlayInChat.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_PLAY_SOUND_IN_CHAT, isSoundPlayInChat.get() ? 1 : 0).apply();
    }

    public void onClickAppBrowser() {
        isInAppBrowser.set(!isInAppBrowser.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_IN_APP_BROWSER, isInAppBrowser.get() ? 1 : 0).apply();
    }

    public void onClickShowVote() {
        isShowVote.set(!isShowVote.get());
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_VOTE, isShowVote.get() ? 1 : 0).apply();
        G.showVoteChannelLayout = isShowVote.get();
    }

    public void onProgressChangedTextSize(int progress, boolean fromUser) {
        if (fromUser) {
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, progress + MIN_TEXT_SIZE).apply();
            StartupActions.textSizeDetection(progress + MIN_TEXT_SIZE);
            setTextSizeValue((progress + MIN_TEXT_SIZE));
        }
    }

    public void dateIsChange() {
        int dateTypeResId;
        switch (sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 1)) {
            case 1:
                dateTypeResId = R.string.shamsi;
                break;
            case 2:
                dateTypeResId = R.string.ghamari;
                break;
            default:
                dateTypeResId = R.string.miladi;
        }
        dateType.set(dateTypeResId);
    }

    public void getChatBackground() {
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            setChatBackground.setValue(backGroundPath);
        } else {
            setChatBackgroundDefault.setValue(R.drawable.chat_default_background_pattern);
        }
    }

    public void setTheme(int oldTheme, int newTheme) {
        if (themeList.getValue() != null) {
            sharedPreferences.edit()
                    .putInt(SHP_SETTING.KEY_OLD_THEME_COLOR, themeList.getValue().get(oldTheme != -1 ? oldTheme : 0).getThemeId())
                    .putInt(SHP_SETTING.KEY_THEME_COLOR, themeList.getValue().get(newTheme).getThemeId())
                    .putBoolean(SHP_SETTING.KEY_THEME_DARK, themeList.getValue().get(newTheme).getThemeId() == Theme.DARK)
                    .apply();
            G.themeColor = themeList.getValue().get(newTheme).getThemeId();
            updateNewTheme.setValue(true);
            if (G.twoPaneMode) {
                updateTwoPaneView.setValue(true);
            }
            selectedThemePosition.setValue(newTheme);
        }
    }

    private void setTextSizeValue(int size) {
        String tmp = String.valueOf(size);
        if (HelperCalander.isPersianUnicode) {
            textSizeValue.set(HelperCalander.convertToUnicodeFarsiNumber(tmp));
        } else {
            textSizeValue.set(tmp);
        }
        updateTextSizeSampleView.setValue(size);
    }
}
