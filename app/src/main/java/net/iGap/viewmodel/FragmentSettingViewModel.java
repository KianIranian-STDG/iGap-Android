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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.InverseMethod;
import android.databinding.ObservableField;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.databinding.FragmentSettingBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.fragments.FragmentBio;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.fragments.FragmentChatSettings;
import net.iGap.fragments.FragmentData;
import net.iGap.fragments.FragmentDeleteAccount;
import net.iGap.fragments.FragmentIVandProfile;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentNotificationAndSound;
import net.iGap.fragments.FragmentPrivacyAndSecurity;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.FragmentThemColor;
import net.iGap.fragments.ReagentFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.module.AttachFile;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.MEditText;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.StartupActions;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmPrivacy;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileCheckUsername;
import net.iGap.request.RequestUserProfileGetBio;
import net.iGap.request.RequestUserProfileGetEmail;
import net.iGap.request.RequestUserProfileGetGender;
import net.iGap.request.RequestUserProfileGetRepresentative;
import net.iGap.request.RequestUserProfileSetBio;
import net.iGap.request.RequestUserProfileSetEmail;
import net.iGap.request.RequestUserProfileSetGender;
import net.iGap.request.RequestUserProfileSetNickname;
import net.iGap.request.RequestUserProfileUpdateUsername;
import net.iGap.request.RequestUserSessionLogout;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

import static net.iGap.G.context;

public class FragmentSettingViewModel extends ViewModel {

    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> bio = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> birthDate = new MutableLiveData<>();
    public MutableLiveData<Integer> gender = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();

    public LiveData<Boolean> getUsernameErrorEnable() {
        return usernameErrorEnable;
    }

    public LiveData<Integer> getUsernameErrorMessage() {
        return usernameErrorMessage;
    }

    public LiveData<Boolean> getEmailErrorEnable() {
        return emailErrorEnable;
    }

    public LiveData<Integer> getEmailErrorMessage() {
        return emailErrorMessage;
    }

    private MutableLiveData<Boolean> usernameErrorEnable = new MutableLiveData<>();
    private MutableLiveData<Integer> usernameErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> emailErrorEnable = new MutableLiveData<>();
    private MutableLiveData<Integer> emailErrorMessage = new MutableLiveData<>();
    //ui
    public MutableLiveData<Boolean> goToShowAvatar = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogDeleteAccount = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToManageSpacePage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLogout = new MutableLiveData<>();
    public MutableLiveData<Boolean> showError = new MutableLiveData<>();
    public MutableLiveData<Boolean> showSubmitButton = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogChooseImage = new MutableLiveData<>();

    public String phoneNumber;
    private String currentName;
    private String currentUserName;
    private String currentUserEmail;
    private int currentGender;
    private String currentBio;
    private String currentBirthDate;
    public long userId;

    public static String pathSaveImage;
    /*public static int KEY_AD_DATA_PHOTO = -1;
    public static int KEY_AD_DATA_VOICE_MESSAGE = -1;
    public static int KEY_AD_DATA_VIDEO = -1;
    public static int KEY_AD_DATA_FILE = -1;
    public static int KEY_AD_DATA_MUSIC = -1;
    public static int KEY_AD_DATA_GIF = -1;
    public static int KEY_AD_WIFI_PHOTO = -1;
    public static int KEY_AD_WIFI_VOICE_MESSAGE = -1;
    public static int KEY_AD_WIFI_VIDEO = -1;
    public static int KEY_AD_WIFI_FILE = -1;
    public static int KEY_AD_WIFI_MUSIC = -1;
    public static int KEY_AD_WIFI_GIF = -1;
    public static int KEY_AD_ROAMING_PHOTO = -1;
    public static int KEY_AD_ROAMING_VOICE_MESSAGE = -1;
    public static int KEY_AD_ROAMING_VIDEO = -1;
    public static int KEY_AD_ROAMING_FILE = -1;
    public static int KEY_AD_ROAMING_MUSIC = -1;
    public static int KEY_AD_ROAMINGN_GIF = -1;*/
    static boolean isActiveRun = false;
    /*private static SharedPreferences sharedPreferences;*/
    public ObservableField<Boolean> isCameraButtonSheet = new ObservableField<>(true);
    /*boolean isCheckedThemeDark;*/
    private int poRbDialogTextSize = -1;
    private Uri uriIntent;
    private long idAvatar;

    private Realm mRealm;
    private RealmUserInfo realmUserInfo;
    private RealmPrivacy realmPrivacy;
    private RealmRegisteredInfo mRealmRegisteredInfo;
    /*private int[] fontSizeArray = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};*/


    public FragmentSettingViewModel() {

        gender.setValue(-1);

        //what is request ?!
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        boolean isIntroduce = realmUserInfo != null && (realmUserInfo.getRepresentPhoneNumber() == null || realmUserInfo.getRepresentPhoneNumber().length() < 1);
        realm.close();

        if (isIntroduce) {
            new RequestUserProfileGetRepresentative().userProfileGetRepresentative(new RequestUserProfileGetRepresentative.OnRepresentReady() {
                @Override
                public void onRepresent(String phoneNumber) {
                    try (Realm realm = Realm.getDefaultInstance()) {
                        RealmUserInfo.setRepresentPhoneNumber(realm, phoneNumber);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailed() {
                }
            });
        }

        new RequestUserProfileGetGender().userProfileGetGender();
        new RequestUserProfileGetEmail().userProfileGetEmail();
        new RequestUserProfileGetBio().getBio();

        usernameErrorMessage.setValue(R.string.is_empty);
        emailErrorMessage.setValue(R.string.is_empty);
        /*realmPrivacy = getRealm().where(RealmPrivacy.class).findFirst();*/
        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
        if (realmUserInfo == null) {
            G.fragmentActivity.onBackPressed();
            return;
        }

        Log.wtf("view Model", "call updateUserInfoUI from constrictor");
        updateUserInfoUI(realmUserInfo);


        /*if (realmPrivacy == null) {
            RealmPrivacy.updatePrivacy("", "", "", "", "", "");
        }*/
        /*sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);*/

        /*String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        callbackLanguage.set(textLanguage);*/

        /*int checkedEnableCrop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1);
        isCrop.set(getBoolean(checkedEnableCrop));*/

        /*boolean checkCameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        isCameraButtonSheet.set(checkCameraButtonSheet);*/

        /*int checkedEnableVote = sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1);
        isShowVote.set(getBoolean(checkedEnableVote));*/

        /*int checkedEnablShowSenderInGroup = sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
        isSenderNameGroup.set(getBoolean(checkedEnablShowSenderInGroup));*/

        /*int checkedEnableCompress = sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1);
        isCompress.set(getBoolean(checkedEnableCompress));*/

        /*int typeData = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 0);
        switch (typeData) {
            case 0:
                callbackDataShams.set(G.fragmentActivity.getResources().getString(R.string.miladi));
                break;
            case 1:
                callbackDataShams.set(G.fragmentActivity.getResources().getString(R.string.shamsi));
                break;
            case 2:
                callbackDataShams.set(G.fragmentActivity.getResources().getString(R.string.ghamari));
                break;
        }*/

        /*FragmentSetting.dateType = new FragmentSetting.DateType() {
            @Override
            public void dataName(String type) {
                callbackDataShams.set(type);
            }
        };*/

        /*boolean checkedEnableAutoRotate = sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
        isAutoRotate.set(checkedEnableAutoRotate);*/

        /*boolean checkedEnableMultiTab = sharedPreferences.getBoolean(SHP_SETTING.KEY_MULTI_TAB, false);
        isMultiTab.set(checkedEnableMultiTab);*/

        /*boolean checkedEnableTime = sharedPreferences.getBoolean(SHP_SETTING.KEY_WHOLE_TIME, false);
        isTime.set(checkedEnableTime);*/

        /*poRbDialogTextSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - 11;
        String textSize = "" + sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14);
        callbackTextSize.set(textSize);
        if (HelperCalander.isPersianUnicode) {
            callbackTextSize.set(HelperCalander.convertToUnicodeFarsiNumber(callbackTextSize.get()));
        }*/

        /*int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        isSendEnter.set(getBoolean(checkedSendByEnter));*/

        /*int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        isInAppBrowser.set(getBoolean(checkedInAppBrowser));*/


        /*boolean checkedThemeDark = sharedPreferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);
        isThemeDark.set(checkedThemeDark);
        if (isThemeDark.get()) {
            isAutoThemeDark.set(View.VISIBLE);
        } else {
            isAutoThemeDark.set(View.GONE);
        }*/

        /*if (G.isDarkTheme) {
            isGoneLayoutColor.set(View.GONE);
        } else {
            isGoneLayoutColor.set(View.VISIBLE);
        }*/


        /*int checkedAutoGif = sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS);
        isAutoGif.set(getBoolean(checkedAutoGif));*/

        /*int checkedSaveToGallery = sharedPreferences.getInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
        isSaveGallery.set(getBoolean(checkedSaveToGallery));*/


        /*int checkedEnableTrim = sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1);
        isTrim.set(getBoolean(checkedEnableTrim));*/

        /*int checkedEnableDefaultPlayer = sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1);
        isDefaultPlayer.set(getBoolean(checkedEnableDefaultPlayer));*/

        /*callbackVersionApp.set(G.fragmentActivity.getResources().getString(R.string.iGap_version) + " " + getAppVersion());*/
    }

    private void updateUserInfoUI(RealmUserInfo userInfo) {
        if (checkValidationForRealm(userInfo)) {
            userId = userInfo.getUserId();
            currentName = userInfo.getUserInfo().getDisplayName();
            currentUserName = userInfo.getUserInfo().getUsername();
            currentUserEmail = userInfo.getEmail();
            currentBio = userInfo.getUserInfo().getBio();
            phoneNumber = userInfo.getUserInfo().getPhoneNumber();
            ProtoGlobal.Gender userGender = userInfo.getGender();
            if (userGender != null) {
                if (userGender == ProtoGlobal.Gender.MALE) {
                    currentGender = R.id.male;
                } else if (userGender == ProtoGlobal.Gender.FEMALE) {
                    currentGender = R.id.female;
                }
            } else {
                currentGender = -1;
            }
            gender.setValue(currentGender);
            name.setValue(currentName);
            bio.setValue(currentBio);
            userName.setValue(currentUserName);
            email.setValue(currentUserEmail);
            /*if (userInfo.getRepresentPhoneNumber() == null || userInfo.getRepresentPhoneNumber().length() < 1) {
                callbackSetRepresent.set("");
            } else {
                callbackSetRepresent.set(userInfo.getRepresentPhoneNumber());
            }*/
        }
    }


    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onAvatarClick() {
        goToShowAvatar.setValue(true);
    }

    public void onAddImageClick() {
        showDialogChooseImage.setValue(true);
    }

    public void onClickNotifyAndSound() {
        new HelperFragment(new FragmentNotificationAndSound()).setReplace(false).load();
    }

    public void onClickPrivacySecurity() {
        new HelperFragment(new FragmentPrivacyAndSecurity()).setReplace(false).load();
    }

    public void onClickDataStorage() {
        goToManageSpacePage.setValue(true);
    }

    public void onChatSettingClick() {
        new HelperFragment(new FragmentChatSettings()).setReplace(false).load();
    }

    public void onLogoutClick() {
        showDialogLogout.setValue(true);
    }

    public void logout() {
        showLoading.setValue(true);
        new RequestUserSessionLogout().userSessionLogout(new OnUserSessionLogout() {
            @Override
            public void onUserSessionLogout() {
                G.handler.post(() -> {
                    HelperLogout.logout();
                    showLoading.setValue(false);
                });
            }

            @Override
            public void onError() {

            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    showLoading.setValue(false);
                    showError.setValue(true);
                });
            }
        });
    }

    public void onDeleteAccountClick() {
        showDialogDeleteAccount.setValue(true);
    }

    public void nameTextChangeListener(String newName) {
        if (!newName.equals(currentName)) {
            showSubmitButton.setValue(true);
        } else {
            if (currentBio.equals(bio.getValue()) && currentUserEmail.equals(email.getValue()) && currentUserName.equals(userName.getValue()) && currentGender == gender.getValue()) {
                showSubmitButton.setValue(false);
            }
        }
    }

    private void sendRequestSetName() {
        showLoading.setValue(true);
        new RequestUserProfileSetNickname().userProfileNickName(name.getValue(), new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                //setAvatar();
                RealmRoom.updateChatTitle(userId, nickName);
                G.handler.post(() -> showLoading.setValue(false));
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                G.handler.post(() -> showLoading.setValue(false));
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                G.handler.post(() -> showLoading.setValue(false));
            }
        });
    }

    public void usernameTextChangeListener(String newUsername) {
        if (HelperString.regexCheckUsername(newUsername)) {
            new RequestUserProfileCheckUsername().userProfileCheckUsername(newUsername, new OnUserProfileCheckUsername() {
                @Override
                public void OnUserProfileCheckUsername(final ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status) {
                    G.handler.post(() -> {
                        if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.AVAILABLE) {
                            showSubmitButton.setValue(!currentUserName.equals(userName.getValue()));
                            usernameErrorEnable.setValue(true);
                            usernameErrorMessage.setValue(R.string.is_empty);
                        } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.INVALID) {
                            usernameErrorMessage.setValue(R.string.INVALID);
                            usernameErrorEnable.setValue(true);
                            showSubmitButton.setValue(false);
                        } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.TAKEN) {
                            usernameErrorMessage.setValue(R.string.TAKEN);
                            usernameErrorEnable.setValue(true);
                            showSubmitButton.setValue(false);
                        }
                    });
                }

                @Override
                public void Error(int majorCode, int minorCode) {

                }
            });
        } else {
            usernameErrorEnable.setValue(true);
            usernameErrorMessage.setValue(R.string.INVALID);
            showSubmitButton.setValue(false);
        }
    }

    private void sendRequestSetUsername() {
        showLoading.setValue(true);
        new RequestUserProfileUpdateUsername().userProfileUpdateUsername(userName.getValue(), new OnUserProfileUpdateUsername() {
            @Override
            public void onUserProfileUpdateUsername(final String username) {
                G.handler.post(() -> showLoading.setValue(false));
            }

            @Override
            public void Error(final int majorCode, int minorCode, final int time) {
                G.handler.post(() -> {
                    if (majorCode == 175) {
                        showLoading.setValue(false);
                        dialogWaitTime(R.string.USER_PROFILE_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                    }
                });
            }

            @Override
            public void timeOut() {
                G.handler.post(() -> showLoading.setValue(false));
            }
        });
    }

    public void emailTextChangeListener(String newEmail) {
        if (!newEmail.equals(currentUserEmail)) {
            showSubmitButton.setValue(true);
            emailErrorMessage.setValue(R.string.is_empty);
            emailErrorEnable.setValue(false);
        } else {
            if (currentName.equals(name.getValue()) && currentUserName.equals(userName.getValue()) && currentBio.equals(bio.getValue()) && currentGender == gender.getValue()) {
                showSubmitButton.setValue(false);
            }
        }
    }

    private void sendRequestSetEmail() {
        showLoading.setValue(true);
        new RequestUserProfileSetEmail().setUserProfileEmail(email.getValue(), new OnUserProfileSetEmailResponse() {
            @Override
            public void onUserProfileEmailResponse(final String email, ProtoResponse.Response response) {
                G.handler.post(() -> showLoading.setValue(false));
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    showLoading.setValue(false);
                    if (majorCode == 114 && minorCode == 1) {
                        emailErrorMessage.setValue(R.string.error_email);
                        emailErrorEnable.setValue(true);
                        showSubmitButton.setValue(false);
                    } else if (majorCode == 115) {
                        emailErrorEnable.setValue(true);
                        showSubmitButton.setValue(false);
                        emailErrorMessage.setValue(R.string.error_email);
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> showLoading.setValue(false));
            }
        });
    }

    public void bioTextChangeListener(String newBio) {
        if (!currentBio.equals(newBio)) {
            showSubmitButton.setValue(true);
        } else {
            if (currentName.equals(name.getValue()) && currentUserName.equals(userName.getValue()) && currentUserEmail.equals(email.getValue()) && currentGender == gender.getValue()) {
                showSubmitButton.setValue(false);
            }
        }
    }

    private void sendRequestSetBio() {
        new RequestUserProfileSetBio().setBio(bio.getValue());
    }

    public void onCheckedListener(int checkedId) {
        if (checkedId != currentGender) {
            showSubmitButton.setValue(true);
        } else {
            if (currentName.equals(name.getValue()) && currentUserName.equals(userName.getValue()) && currentUserEmail.equals(email.getValue()) && currentBio.equals(bio.getValue())) {
                showSubmitButton.setValue(false);
            }
        }
    }

    private void sendRequestSetGender() {
        showLoading.setValue(true);
        new RequestUserProfileSetGender().setUserProfileGender(gender.getValue() == R.id.male ? ProtoGlobal.Gender.MALE : ProtoGlobal.Gender.FEMALE, new OnUserProfileSetGenderResponse() {
            @Override
            public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                G.handler.post(() -> showLoading.setValue(false));
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                G.handler.post(() -> showLoading.setValue(false));
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> showLoading.setValue(false));
            }
        });
    }

    /*public void onClickRepresent(View view) {
        if (RequestUserProfileGetRepresentative.numberOfPendingRequest == 0) {
            if (callbackSetRepresent.get().equals("")) {
                new HelperFragment(ReagentFragment.newInstance(false)).setReplace(false).load();
            }
        } else {
            HelperError.showSnackMessage(G.context.getString(R.string.try_later), false);
        }
    }*/

    /*public void onClickIVand(View view) {
        new HelperFragment(new FragmentIVandProfile()).setReplace(false).load();
    }*/


    //todo:move to page chat setting
    /*public void onClickLanguage(View view) {
        new HelperFragment(new FragmentLanguage()).setReplace(false).load();
    }*/

    //todo:move to page chat setting
    /*public void onClickDataShams(View view) {
        new HelperFragment(new FragmentData()).setReplace(false).load();
    }*/


    //todo:move to page chat setting
    /*public void onClickAutoRotate(View view) {
        isAutoRotate.set(!isAutoRotate.get());
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangedAutoRotate(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isAutoRotate.set(isChecked);
        if (isChecked) {
            editor.putBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
            editor.apply();
            fragmentSetting.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        } else {
            editor.putBoolean(SHP_SETTING.KEY_AUTO_ROTATE, false);
            editor.apply();
            fragmentSetting.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
    }*/

    //todo:move to page chat setting
    /*public void onClickMessageTextSize(View view) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_title_message_textSize))
                .titleGravity(GravityEnum.START)
                .titleColor(G.context.getResources().getColor(android.R.color.black))
                .items(HelperCalander.isPersianUnicode ? R.array.message_text_size_persian : R.array.message_text_size)
                .itemsCallbackSingleChoice(poRbDialogTextSize, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text != null) {
                            callbackTextSize.set(text.toString().replace("(Hello)", "").trim());

                            if (HelperCalander.isPersianUnicode) {
                                callbackTextSize.set(HelperCalander.convertToUnicodeFarsiNumber(callbackTextSize.get()));
                            }
                        }
                        poRbDialogTextSize = which;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, fontSizeArray[which]);
                        editor.apply();

                        StartupActions.textSizeDetection(sharedPreferences);

                        return false;
                    }
                })
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .show();
    }*/

    //todo:move to page chat setting
    /*public void onClickChatBackground(View view) {
        new HelperFragment(FragmentChatBackground.newInstance()).setReplace(false).load();
    }*/

    //todo:move to page chat setting
    /*public void onClickShowVote(View view) {
        isShowVote.set(!isShowVote.get());
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangedShowVote(boolean isChecked) {
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
    }*/

    //todo:move to page chat setting
    /*public void onClickMultiTab(View view) {

//        isMultiTab.set(!isMultiTab.get());
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        if (isMultiTab.get()) {
//            G.multiTab = true;
//            editor.putBoolean(SHP_SETTING.KEY_MULTI_TAB, true);
//            editor.apply();
//        } else {
//            G.multiTab = false;
//            editor.putBoolean(SHP_SETTING.KEY_MULTI_TAB, false);
//            editor.apply();
//        }
//        FragmentMain.roomAdapterHashMap = null;
//
//        if (onRefreshActivity != null) {
//            G.isRestartActivity = true;
//            onRefreshActivity.refresh("ar");
//        }
//        if (FragmentSetting.onRemoveFragmentSetting != null)
//            FragmentSetting.onRemoveFragmentSetting.removeFragment();

    }*/

    //todo:move to page chat setting
    /*public void onClickTime(View view) {

        isTime.set(!isTime.get());
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (isTime.get()) {
            G.isTimeWhole = true;
            editor.putBoolean(SHP_SETTING.KEY_WHOLE_TIME, true);
            editor.apply();
        } else {
            G.isTimeWhole = false;
            editor.putBoolean(SHP_SETTING.KEY_WHOLE_TIME, false);
            editor.apply();
        }
        if (G.onNotifyTime != null) {
            G.onNotifyTime.notifyTime();
        }
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangedMultiTab(boolean isChecked) {


    }*/


    //todo:move to page chat setting
    /*public void onClickSenderNameGroup(View view) {

        isSenderNameGroup.set(!isSenderNameGroup.get());
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangedSenderNameGroup(boolean isChecked) {

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

    }*/

    //todo:move to page chat setting
    /*public void onClickSendEnter(View view) {

        isSendEnter.set(!isSendEnter.get());
    }*/


    //todo:move to page chat setting
    /*public void onCheckedChangedSendEnter(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSendEnter.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 1);
        } else {
            editor.putInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        }
        editor.apply();
    }*/

    //todo:move to page chat setting
    /*public void onClickAppBrowser(View view) {

        isInAppBrowser.set(!isInAppBrowser.get());
    }*/


    //todo:move to page chat setting
    /*public void onCheckedAppBrowser(boolean isChecked) {


        SharedPreferences.Editor editor = sharedPreferences.edit();
        isInAppBrowser.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        } else {
            editor.putInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);
        }
        editor.apply();

    }*/


    //todo:move to page chat setting
    /*public void onClickThemeColor(View v) {
        new HelperFragment(new FragmentThemColor()).setReplace(false).load();
    }*/

    //todo:move to page chat setting
    /*public void onClickAutoDownloadData(View view) {


        KEY_AD_DATA_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
        KEY_AD_DATA_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
        KEY_AD_DATA_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
        KEY_AD_DATA_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
        KEY_AD_DATA_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
        KEY_AD_DATA_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_DATA_GIF, 5);

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_auto_download_data).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                KEY_AD_DATA_PHOTO, KEY_AD_DATA_VOICE_MESSAGE, KEY_AD_DATA_VIDEO, KEY_AD_DATA_FILE, KEY_AD_DATA_MUSIC, KEY_AD_DATA_GIF
        }, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, -1);
                editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, -1);
                editor.apply();

                for (Integer aWhich : which) {

                    if (aWhich == 0) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_PHOTO, aWhich);
                    } else if (aWhich == 1) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VOICE_MESSAGE, aWhich);
                    } else if (aWhich == 2) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_VIDEO, aWhich);
                    } else if (aWhich == 3) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_FILE, aWhich);
                    } else if (aWhich == 4) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_MUSIC, aWhich);
                    } else if (aWhich == 5) {
                        editor.putInt(SHP_SETTING.KEY_AD_DATA_GIF, aWhich);
                    }
                    editor.apply();
                }

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();

    }*/

    //todo:move to page chat setting
    /*public void onClickAutoDownloadWifi(View view) {

        KEY_AD_WIFI_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
        KEY_AD_WIFI_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
        KEY_AD_WIFI_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
        KEY_AD_WIFI_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
        KEY_AD_WIFI_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
        KEY_AD_WIFI_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_WIFI_GIF, 5);

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_auto_download_wifi).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                KEY_AD_WIFI_PHOTO, KEY_AD_WIFI_VOICE_MESSAGE, KEY_AD_WIFI_VIDEO, KEY_AD_WIFI_FILE, KEY_AD_WIFI_MUSIC, KEY_AD_WIFI_GIF
        }, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt(SHP_SETTING.KEY_AD_WIFI_PHOTO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VOICE_MESSAGE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_VIDEO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_FILE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_MUSIC, -1);
                editor.putInt(SHP_SETTING.KEY_AD_WIFI_GIF, -1);
                editor.apply();

                for (Integer aWhich : which) {
                    Log.i("JJJJ", "WIFI: " + aWhich);

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

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).show();

    }*/

    //todo:move to page chat setting
    /*public void onClickAutoDownloadRoaming(View view) {

        KEY_AD_ROAMING_PHOTO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
        KEY_AD_ROAMING_VOICE_MESSAGE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
        KEY_AD_ROAMING_VIDEO = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
        KEY_AD_ROAMING_FILE = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
        KEY_AD_ROAMING_MUSIC = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
        KEY_AD_ROAMINGN_GIF = sharedPreferences.getInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.title_auto_download_roaming).items(R.array.auto_download_data).itemsCallbackMultiChoice(new Integer[]{
                KEY_AD_ROAMING_PHOTO, KEY_AD_ROAMING_VOICE_MESSAGE, KEY_AD_ROAMING_VIDEO, KEY_AD_ROAMING_FILE, KEY_AD_ROAMING_MUSIC, KEY_AD_ROAMINGN_GIF
        }, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                //

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, -1);
                editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, -1);
                editor.apply();

                for (Integer aWhich : which) {
                    if (aWhich > -1) {
                        if ((aWhich == 0)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_PHOTO, aWhich);
                        } else if ((aWhich == 1)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VOICE_MESSAGE, aWhich);
                        } else if ((aWhich == 2)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_VIDEO, aWhich);
                        } else if ((aWhich == 3)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_FILE, aWhich);
                        } else if ((aWhich == 4)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_MUSIC, aWhich);
                        } else if ((aWhich == 5)) {
                            editor.putInt(SHP_SETTING.KEY_AD_ROAMING_GIF, aWhich);
                        }
                        editor.apply();
                    }
                }
                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }*/

    //todo:move to page chat setting
    /*public void onClickAutoGif(View view) {
        isAutoGif.set(!isAutoGif.get());
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangeAutoGif(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isAutoGif.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0);
            editor.apply();
        }

    }*/

    //todo:move to page chat setting
    /*public void onClickSaveGallery(View view) {

        isSaveGallery.set(!isSaveGallery.get());
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangedSaveGallery(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isSaveGallery.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 1);
            editor.apply();
            G.isSaveToGallery = true;
        } else {
            editor.putInt(SHP_SETTING.KEY_SAVE_TO_GALLERY, 0);
            G.isSaveToGallery = false;
            editor.apply();
        }

    }*/

    //todo:move to page chat setting
    /*public void onClickCompress(View view) {
        isCompress.set(!isCompress.get());
    }*/

    //todo:move to page chat setting
    /*public void onCheckedChangedCompress(boolean isChecked) {

        isCompress.set(isChecked);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_COMPRESS, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_COMPRESS, 0);
            editor.apply();
        }

    }*/
    //todo:move to page chat setting
    /*public void onClickTrim(View view) {
        isTrim.set(!isTrim.get());
    }*/
    //todo:move to page chat setting
    /*public void onCheckedChangedTrim(boolean isChecked) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        isTrim.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_TRIM, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_TRIM, 0);
            editor.apply();
        }

    }*/
    //todo:move to page chat setting
    /*public void onClickDefaultVideo(View view) {
        isDefaultPlayer.set(!isDefaultPlayer.get());
    }*/
    //todo:move to page chat setting
    /*public void onCheckedDefaultVideo(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isDefaultPlayer.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 0);
            editor.apply();
        }
    }*/
    //todo:move to page chat setting
    /*public void onClickCrop(View view) {
        isCrop.set(!isCrop.get());
    }*/
    //todo:move to page chat setting
    /*public void onCheckedChangedCrop(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isCrop.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_CROP, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_CROP, 0);
            editor.apply();
        }

    }*/
    //todo:move to page chat setting
    /*public void onClickCameraButtonSheet(View v) {
        isCameraButtonSheet.set(!isCameraButtonSheet.get());
    }*/
    //todo:move to page chat setting
    /*public void onCheckedChangedCameraButtonSheet(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isCameraButtonSheet.set(isChecked);
        if (isChecked) {
            editor.putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
            editor.apply();
        } else {
            editor.putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, false);
            editor.apply();
        }

    }*/
    //todo:move to page chat setting
    /*public void onClickiGapHome(View view) {
        final String link;
        if (HelperCalander.isPersianUnicode) {
            link = "https://www.igap.net/fa";
        } else {
            link = "https://www.igap.net/";
        }
        HelperUrl.openBrowser(link);
    }*/
    //todo:move to page chat setting
    /*public void onClickPrivacyBlog(View view) {

        final String blogLink;
        if (HelperCalander.isPersianUnicode) {
            blogLink = "https://blog.igap.net/fa";
        } else {
            blogLink = "https://blog.igap.net";
        }

        HelperUrl.openBrowser(blogLink);
    }*/
    //todo:move to page chat setting
    /*public void onClickTicket(View view) {

        final String supportLink;
        if (HelperCalander.isPersianUnicode) {
            supportLink = "https://support.igap.net/fa";
        } else {
            supportLink = "https://support.igap.net";
        }
        HelperUrl.openBrowser(supportLink);

    }*/
    //todo:move to page chat setting
    /*public void updateIvandScore(int score) {
        callbackSetIVand.set(G.currentActivity.getString(R.string.st_ivand_text_setting) + " " + score);
    }*/

    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        return realmUserInfo != null && realmUserInfo.isManaged() && realmUserInfo.isValid() && realmUserInfo.isLoaded();
    }


    /*private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(fragmentSetting);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                idAvatar = SUID.id().get();
                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                File nameImageFile = new File(pathSaveImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uriIntent = Uri.fromFile(nameImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                fragmentSetting.startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }*/


    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        if (isActiveRun) {
            boolean wrapInScrollView = true;
            final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            }).show();

            View v = dialog.getCustomView();

            final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
            CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) ((millisUntilFinished) / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                    //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                }

                @Override
                public void onFinish() {
                    //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                }
            };
            countWaitTimer.start();
        }
    }

    private boolean getBoolean(int num) {
        if (num == 0) {
            return false;
        }
        return true;
    }

    /*private String getAppVersion() {

        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }*/


    public void onResume() {
        /*G.onUserAvatarResponse = fragmentSetting;*/

        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();

        if (realmUserInfo == null) {
            //finish();
            G.fragmentActivity.onBackPressed();
            return;
        }

        /*realmUserInfo.addChangeListener(realmModel -> {
            Log.wtf("view Model", "call updateUserInfoUI from =realmUserInfo change listener");
            updateUserInfoUI((RealmUserInfo) realmModel);
        });*/

        mRealmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), G.userId);
        if (mRealmRegisteredInfo != null) {
            mRealmRegisteredInfo.addChangeListener(realmModel -> {
                Log.wtf("view Model", "call updateUserInfoUI from =mRealmRegisteredInfo change listener");
                updateUserInfoUI(realmUserInfo);
            });
            /*Log.wtf("view Model", "call updateUserInfoUI from =mRealmRegisteredInfo not null");
            updateUserInfoUI(realmUserInfo);*/
        }
    }

    public void onPause() {
        if (realmUserInfo != null && realmUserInfo.isValid()) {
            realmUserInfo.removeAllChangeListeners();
        }

        if (mRealmRegisteredInfo != null && mRealmRegisteredInfo.isValid()) {
            mRealmRegisteredInfo.removeAllChangeListeners();
        }
    }

    public void onStop() {
        updateRoomListIfNeeded();
    }

    public void onDestroy() {

        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }

    private void updateRoomListIfNeeded() {

        try {

            for (Fragment f : G.fragmentManager.getFragments()) {

                if (f == null) {
                    continue;
                }

                if (f instanceof FragmentMain || f instanceof FragmentCall) {
                    f.onResume();
                }
            }
        } catch (Exception e) {
            HelperLog.setErrorLog("fragment setting   updateRoomListIfNeeded    " + e.toString());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


    }


}
