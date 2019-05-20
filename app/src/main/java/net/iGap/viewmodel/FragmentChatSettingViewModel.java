package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.view.View;

import net.iGap.G;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;

import static android.content.Context.MODE_PRIVATE;


public class FragmentChatSettingViewModel  {


    private SharedPreferences sharedPreferences;

    public MutableLiveData<String> callbackTextSize = new MutableLiveData<>();
    public ObservableField<Boolean> isShowVote = new ObservableField<>();
    public ObservableField<Boolean> isSenderNameGroup = new ObservableField<>();
    public ObservableField<Boolean> isSendEnter = new ObservableField<>();
    public ObservableField<Boolean> isInAppBrowser = new ObservableField<>();
    public ObservableField<Boolean> isCompress = new ObservableField<>();
    public ObservableField<Boolean> isTrim = new ObservableField<>();
    public ObservableField<Boolean> isDefaultPlayer = new ObservableField<>();
    public ObservableField<Boolean> isCrop = new ObservableField<>();
    public ObservableField<Boolean> isCameraButtonSheet = new ObservableField<>(true);

    public FragmentChatSettingViewModel() {
        getInfo();
    }

    private void getInfo() {

        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        int checkedEnableCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1);
        isCrop.set(getBoolean(checkedEnableCrop));

        boolean checkCameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        isCameraButtonSheet.set(checkCameraButtonSheet);

        int checkedEnableVote = sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1);
        isShowVote.set(getBoolean(checkedEnableVote));

        int checkedEnablShowSenderInGroup = sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
        isSenderNameGroup.set(getBoolean(checkedEnablShowSenderInGroup));

        int checkedEnableCompress = sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1);
        isCompress.set(getBoolean(checkedEnableCompress));


        String textSize = "" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 16);
        callbackTextSize.setValue(textSize);

        if (HelperCalander.isPersianUnicode) {
            callbackTextSize.setValue(HelperCalander.convertToUnicodeFarsiNumber(callbackTextSize.getValue()));
        }

        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        isSendEnter.set(getBoolean(checkedSendByEnter));

        int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        isInAppBrowser.set(getBoolean(checkedInAppBrowser));

        int checkedEnableTrim = sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1);
        isTrim.set(getBoolean(checkedEnableTrim));

        int checkedEnableDefaultPlayer = sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1);
        isDefaultPlayer.set(getBoolean(checkedEnableDefaultPlayer));

    }

    public void onClickCompress(View view) {
        isCompress.set(!isCompress.get());
    }

    public void onCheckedChangedCompress(boolean isChecked) {

        isCompress.set(isChecked);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_COMPRESS, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_COMPRESS, 0);
            editor.apply();
        }

    }

    public void onClickTrim(View view) {
        isTrim.set(!isTrim.get());
    }

    public void onCheckedChangedTrim(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isTrim.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_TRIM, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_TRIM, 0);
            editor.apply();
        }

    }

    public void onClickDefaultVideo(View view) {
        isDefaultPlayer.set(!isDefaultPlayer.get());
    }

    public void onCheckedDefaultVideo(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isDefaultPlayer.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 0);
            editor.apply();
        }
    }

    public void onClickCrop(View view) {
        isCrop.set(!isCrop.get());
    }

    public void onCheckedChangedCrop(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isCrop.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_CROP, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_CROP, 0);
            editor.apply();
        }

    }

    public void onClickCameraButtonSheet(View v) {
        isCameraButtonSheet.set(!isCameraButtonSheet.get());
    }

    public void onCheckedChangedCameraButtonSheet(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isCameraButtonSheet.set(isChecked);
        if (isChecked) {
            editor.putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
            editor.apply();
        } else {
            editor.putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, false);
            editor.apply();
        }

    }

    public void onClickChatBackground(View view) {
        new HelperFragment(FragmentChatBackground.newInstance()).setReplace(false).load();
    }

    public void onClickSenderNameGroup(View view) {

        isSenderNameGroup.set(!isSenderNameGroup.get());
    }

    public void onCheckedChangedSenderNameGroup(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSenderNameGroup.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 1);
            editor.apply();
            G.showSenderNameInGroup = true;
        } else {
            editor.putInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
            editor.apply();
            G.showSenderNameInGroup = false;
        }

    }

    public void onClickSendEnter(View view) {

        isSendEnter.set(!isSendEnter.get());
    }

    public void onCheckedChangedSendEnter(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSendEnter.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 1);
        } else {
            editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        }
        editor.apply();
    }

    public void onClickAppBrowser(View view) {

        isInAppBrowser.set(!isInAppBrowser.get());
    }

    public void onCheckedAppBrowser(boolean isChecked) {


        SharedPreferences.Editor editor = sharedPreferences.edit();
        isInAppBrowser.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        } else {
            editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);
        }
        editor.apply();

    }

    public void onClickShowVote(View view) {
        isShowVote.set(!isShowVote.get());
    }

    public void onCheckedChangedShowVote(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isShowVote.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_VOTE, 1);
            editor.apply();
            G.showVoteChannelLayout = true;
        } else {
            editor.putInt(SHP_SETTING.KEY_VOTE, 0);
            editor.apply();
            G.showVoteChannelLayout = false;
        }
    }

    private boolean getBoolean(int num) {
        if (num == 0) {
            return false;
        }
        return true;
    }

    public void setTextSizeToPref(int size){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, size);
        editor.apply();

        StartupActions.textSizeDetection(sharedPreferences);

    }
}
