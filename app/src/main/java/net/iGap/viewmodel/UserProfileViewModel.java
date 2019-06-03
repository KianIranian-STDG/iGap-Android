package net.iGap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.eventbus.socketMessages;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.FragmentUserScore;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.RefreshWalletBalance;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserIVandGetScore;
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
import net.iGap.request.RequestWalletGetAccessToken;

import org.jetbrains.annotations.NotNull;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.web.Web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Looper.getMainLooper;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;

public class UserProfileViewModel extends ViewModel implements RefreshWalletBalance, OnUserInfoMyClient, EventListener, OnUserAvatarResponse {

    private final String TAG = UserProfileViewModel.class.getName();

    private ObservableField<String> appVersion = new ObservableField<>("");
    private ObservableField<String> userPhoneNumber = new ObservableField<>();
    private ObservableLong currentCredit = new ObservableLong(0);
    private ObservableField<String> currentScore = new ObservableField<>("0");
    private ObservableBoolean isDarkMode = new ObservableBoolean(false);
    private ObservableInt editProfileIcon = new ObservableInt(R.string.edit_icon);
    private ObservableField<String> name = new ObservableField<>("");
    private ObservableField<String> userName = new ObservableField<>("");
    private ObservableField<String> bio = new ObservableField<>("");
    private ObservableField<String> email = new ObservableField<>("");
    private ObservableField<String> birthDate = new ObservableField<>("");
    private ObservableInt gender = new ObservableInt(-1);
    private MutableLiveData<Boolean> usernameErrorEnable = new MutableLiveData<>();
    private ObservableInt usernameErrorMessage = new ObservableInt(R.string.empty_error_message);
    private MutableLiveData<Boolean> emailErrorEnable = new MutableLiveData<>();
    private ObservableInt emailErrorMessage = new ObservableInt(R.string.empty_error_message);
    private ObservableInt showLoading = new ObservableInt(View.GONE);
    //ui
    public MutableLiveData<Boolean> goToAddMemberPage = new MutableLiveData<>();
    public MutableLiveData<String> goToWalletAgreementPage = new MutableLiveData<>();
    public MutableLiveData<String> goToWalletPage = new MutableLiveData<>();
    public MutableLiveData<String> shareInviteLink = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToScannerPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> checkLocationPermission = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToIGapMapPage = new MutableLiveData<>();
    public MutableLiveData<String> goToFAQPage = new MutableLiveData<>();
    public MutableLiveData<Long> goToShowAvatarPage = new MutableLiveData<>();
    public MutableLiveData<Long> setUserAvatar = new MutableLiveData<>();
    public MutableLiveData<DeleteAvatarModel> deleteAvatar = new MutableLiveData<>();
    public MutableLiveData<ChangeImageModel> setUserAvatarPath = new MutableLiveData<>();
    public MutableLiveData<Long> goToChatPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEditProfile = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogChooseImage = new MutableLiveData<>();

    private Realm mRealm;
    private RealmUserInfo userInfo;
    private String phoneNumber;
    private int retryConnectToWallet = 0;
    private SharedPreferences sharedPreferences;
    private String currentName;
    private String currentUserName;
    private String currentUserEmail;
    private int currentGender;
    private String currentBio;
    private String currentBirthDate;
    private long userId;
    public String pathSaveImage;
    private long idAvatar;

    private AvatarHandler avatarHandler;

    public UserProfileViewModel(SharedPreferences sharedPreferences, AvatarHandler avatarHandler) {
        this.sharedPreferences = sharedPreferences;
        this.avatarHandler = avatarHandler;

        isEditProfile.setValue(false);
        appVersion.set(BuildConfig.VERSION_NAME);
        isDarkMode.set(G.isDarkTheme);

        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        //set credit amount
        if (G.selectedCard != null) {
            currentCredit.set(G.selectedCard.cashOutBalance);
        } else {
            currentCredit.set(0);
        }

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
                        e.printStackTrace();
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
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(int score) {
                G.handler.post(() -> currentScore.set(String.valueOf(score)));
            }

            @Override
            public void onError() {

            }
        });

        updateUserInfoUI();

        FragmentShowAvatars.onComplete = (result, messageOne, MessageTow) -> {
            long mAvatarId = 0;
            if (messageOne != null && !messageOne.equals("")) {
                mAvatarId = Long.parseLong(messageOne);
            }
            long finalMAvatarId = mAvatarId;
            G.handler.post(() -> deleteAvatar.setValue(new DeleteAvatarModel(userId, finalMAvatarId)));
        };

        FragmentEditImage.completeEditImage = (path, message, textImageList) -> {
            pathSaveImage = path;
            long lastUploadedAvatarId = idAvatar + 1L;
            showLoading.set(View.VISIBLE);
            HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                @Override
                public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress >= 100) {
                        new RequestUserAvatarAdd().userAddAvatar(struct.token);
                    }
                }

                @Override
                public void OnError() {
                    showLoading.set(View.GONE);
                }
            });
        };

        G.onUserAvatarResponse = this;
    }

    private void updateUserInfoUI() {
        if (checkValidationForRealm(userInfo)) {
            userId = userInfo.getUserId();
            phoneNumber = userInfo.getUserInfo().getPhoneNumber();
            userPhoneNumber.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(phoneNumber) : phoneNumber);
            setUserAvatar.setValue(G.userId);
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
            gender.set(currentGender);
            name.set(currentName);
            bio.set(currentBio);
            userName.set(currentUserName);
            email.set(currentUserEmail);
            G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
                @Override
                public void onChangePhoto(final String imagePath) {
                    G.handler.post(() -> setUserAvatarPath.setValue(new ChangeImageModel(imagePath, userInfo.getUserInfo().getInitials(), userInfo.getUserInfo().getColor())));
                }

                @Override
                public void onChangeInitials(final String initials, final String color) {
                    G.handler.post(() -> setUserAvatarPath.setValue(new ChangeImageModel(null, initials, color)));
                }
            };
            userInfo.addChangeListener(realmModel -> {
                Log.wtf("view Model", "call updateUserInfoUI from =realmUserInfo change listener");
                userInfo = (RealmUserInfo) realmModel;
                updateUserInfoUI();
            });
        }
    }

    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        return realmUserInfo != null && realmUserInfo.isManaged() && realmUserInfo.isValid() && realmUserInfo.isLoaded();
    }

    public ObservableField<String> getAppVersion() {
        return appVersion;
    }

    public ObservableField<String> getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public ObservableLong getCurrentCredit() {
        return currentCredit;
    }

    public ObservableField<String> getCurrentScore() {
        return currentScore;
    }

    public ObservableBoolean getIsDarkMode() {
        return isDarkMode;
    }

    public ObservableInt getEditProfileIcon() {
        return editProfileIcon;
    }

    public ObservableField<String> getName() {
        return name;
    }

    public ObservableField<String> getUserName() {
        return userName;
    }

    public ObservableField<String> getBio() {
        return bio;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public ObservableField<String> getBirthDate() {
        return birthDate;
    }

    public ObservableInt getGender() {
        return gender;
    }

    public LiveData<Boolean> getUsernameErrorEnable() {
        return usernameErrorEnable;
    }

    public ObservableInt getUsernameErrorMessage() {
        return usernameErrorMessage;
    }

    public LiveData<Boolean> getEmailErrorEnable() {
        return emailErrorEnable;
    }

    public ObservableInt getEmailErrorMessage() {
        return emailErrorMessage;
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }

    public void onEditProfileClick() {
        if (isEditProfile.getValue() != null) {
            if (isEditProfile.getValue()) {
                if (editProfileIcon.get() == R.string.check_icon) {
                    submitData();
                } else {
                    editProfileIcon.set(R.string.edit_icon);
                    isEditProfile.setValue(false);
                }
            } else {
                editProfileIcon.set(R.string.close_icon);
                isEditProfile.setValue(true);
            }
        }
    }

    public void onCloudMessageClick() {
        final RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, G.userId).findFirst();
        if (realmRoom != null) {
            goToChatPage.setValue(realmRoom.getId());
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(ProtoGlobal.Room room) {
                    goToChatPage.setValue(room.getId());
                    G.onChatGetRoom = null;
                }

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {

                }
            };
            new RequestChatGetRoom().chatGetRoom(G.userId);
        }
    }

    public void onSettingClick() {
        new HelperFragment(new FragmentSetting()).load();
    }

    public void onAddClick() {
        goToAddMemberPage.setValue(true);
    }

    public void onCreditClick() {
        if (!G.isWalletRegister) {
            goToWalletAgreementPage.setValue(phoneNumber.substring(2));
        } else {
            goToWalletPage.setValue(phoneNumber.substring(2));
        }
    }

    public void onScoreClick() {
        new HelperFragment(new FragmentUserScore()).setReplace(false).load();
    }

    public void onInviteFriendsClick() {
        shareInviteLink.setValue("Hey Join iGap : https://www.igap.net I'm waiting for you!");
    }

    public void onQRCodeScannerClick() {
        goToScannerPage.setValue(true);
    }

    public void onNearByClick() {
        checkLocationPermission.setValue(true);
    }

    public void haveLocationPermission() {
        try {
            if (!waitingForConfiguration) {
                waitingForConfiguration = true;
                if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                    G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                        @Override
                        public void onGetConfiguration() {
                            G.handler.postDelayed(() -> waitingForConfiguration = false, 2000);
                            G.handler.post(() -> goToIGapMapPage.setValue(true));
                        }

                        @Override
                        public void getConfigurationTimeOut() {
                            G.handler.postDelayed(() -> waitingForConfiguration = false, 2000);
                        }
                    };
                    new RequestGeoGetConfiguration().getConfiguration();
                } else {
                    G.handler.postDelayed(() -> waitingForConfiguration = false, 2000);
                    goToIGapMapPage.setValue(true);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void onThemeClick(boolean isCheck) {
        if (isCheck != isDarkMode.get()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isCheck) {
                int themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_THEME_COLOR, Theme.CUSTOM);
                editor.putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DARK);
                editor.putInt(SHP_SETTING.KEY_OLD_THEME_COLOR, themeColor);
                editor.apply();
            } else {
                int themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_OLD_THEME_COLOR, Theme.CUSTOM);
                editor.putInt(SHP_SETTING.KEY_THEME_COLOR, themeColor);
                editor.apply();
            }
            Theme.setThemeColor();
            FragmentThemColorViewModel.resetApp();
        }
    }

    public void onFAQClick() {
        goToFAQPage.setValue(HelperCalander.isPersianUnicode ? "https://blog.igap.net/fa" : "https://blog.igap.net");
    }

    public void onAvatarClick() {
        if (userInfo != null) {
            goToShowAvatarPage.setValue(userInfo.getUserId());
        }
    }

    public void onAddImageClick() {
        showDialogChooseImage.setValue(true);
    }

    public void nameTextChangeListener(String newName) {
        if (isEditMode()) {
            if (!newName.equals(currentName)) {
                editProfileIcon.set(R.string.check_icon);
            } else {
                if (currentBio.equals(bio.get()) && currentUserEmail.equals(email.get()) && currentUserName.equals(userName.get()) && currentGender == gender.get()) {
                    editProfileIcon.set(R.string.close_icon);
                }
            }
        }
    }

    public void usernameTextChangeListener(String newUsername) {
        if (isEditMode()) {
            if (HelperString.regexCheckUsername(newUsername)) {
                new RequestUserProfileCheckUsername().userProfileCheckUsername(newUsername, new OnUserProfileCheckUsername() {
                    @Override
                    public void OnUserProfileCheckUsername(final ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status) {
                        G.handler.post(() -> {
                            if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.AVAILABLE) {
                                editProfileIcon.set(!currentUserName.equals(userName.get()) ? R.string.check_icon : R.string.close_icon);
                                usernameErrorEnable.setValue(true);
                                usernameErrorMessage.set(R.string.empty_error_message);
                            } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.INVALID) {
                                usernameErrorMessage.set(R.string.INVALID);
                                usernameErrorEnable.setValue(true);
                                editProfileIcon.set(R.string.close_icon);
                            } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.TAKEN) {
                                usernameErrorMessage.set(R.string.TAKEN);
                                usernameErrorEnable.setValue(true);
                                editProfileIcon.set(R.string.close_icon);
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {

                    }
                });
            } else {
                usernameErrorEnable.setValue(true);
                usernameErrorMessage.set(R.string.INVALID);
                editProfileIcon.set(R.string.close_icon);
            }
        }
    }

    public void emailTextChangeListener(String newEmail) {
        if (isEditMode()) {
            Log.wtf(TAG, "emailTextChangeListener: " + newEmail);
            if (!newEmail.equals(currentUserEmail)) {
                editProfileIcon.set(R.string.check_icon);
                emailErrorMessage.set(R.string.empty_error_message);
                emailErrorEnable.setValue(false);
            } else {
                if (currentName.equals(name.get()) && currentUserName.equals(userName.get()) && currentBio.equals(bio.get()) && currentGender == gender.get()) {
                    editProfileIcon.set(R.string.close_icon);
                }
            }
        }
    }

    public void bioTextChangeListener(String newBio) {
        if (isEditMode()) {
            Log.wtf(TAG, "bioTextChangeListener: " + newBio);
            if (!currentBio.equals(newBio)) {
                editProfileIcon.set(R.string.check_icon);
            } else {
                if (currentName.equals(name.get()) && currentUserName.equals(userName.get()) && currentUserEmail.equals(email.get()) && currentGender == gender.get()) {
                    editProfileIcon.set(R.string.close_icon);
                }
            }
        }
    }

    public void onCheckedListener(int checkedId) {
        if (isEditMode()) {
            Log.wtf(TAG, "onCheckedListener: " + checkedId);
            if (checkedId != currentGender) {
                editProfileIcon.set(R.string.check_icon);
            } else {
                if (currentName.equals(name.get())) {
                    if (currentUserName.equals(userName.get())) {
                        if (currentUserEmail.equals(email.get())) {
                            if (currentBio.equals(bio.get())) {
                                editProfileIcon.set(R.string.close_icon);
                            }
                        }
                    }
                }
            }
        }
    }

    private void getUserCredit() {
        WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
        if (Auth.getCurrentAuth() != null) {
            Web.getInstance().getWebService().getCredit(Auth.getCurrentAuth().getId()).enqueue(new Callback<ArrayList<Card>>() {
                @Override
                public void onResponse(@NotNull Call<ArrayList<Card>> call, @NotNull Response<ArrayList<Card>> response) {
                    if (response.body() != null) {
                        retryConnectToWallet = 0;
                        if (response.body().size() > 0)
                            G.selectedCard = response.body().get(0);
                        G.cardamount = G.selectedCard.cashOutBalance;
                        currentCredit.set(G.selectedCard.cashOutBalance);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArrayList<Card>> call, @NotNull Throwable t) {

                    if (retryConnectToWallet < 3) {
                        Crashlytics.logException(new Exception(t.getMessage()));
                        getUserCredit();
                        retryConnectToWallet++;
                    }
                }
            });
        }
    }

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }

    private boolean isEditMode() {
        return isEditProfile.getValue() != null && isEditProfile.getValue();
    }

    private void submitData() {
        showLoading.set(View.VISIBLE);
        if (!currentName.equals(name.get())) {
            sendRequestSetName();
        } else if (!currentUserName.equals(userName.get())) {
            sendRequestSetUsername();
        } else if (!currentBio.equals(bio.get())) {
            sendRequestSetBio();
        } else if (!currentUserEmail.equals(email.get())) {
            sendRequestSetEmail();
        } else if (currentGender != gender.get()) {
            sendRequestSetGender();
        } else {
            showLoading.set(View.GONE);
            isEditProfile.setValue(false);
            editProfileIcon.set(R.string.edit_icon);
        }
    }

    private void sendRequestSetName() {
        showLoading.set(View.VISIBLE);
        new RequestUserProfileSetNickname().userProfileNickName(name.get(), new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                //setAvatar();
                RealmRoom.updateChatTitle(userId, nickName);
                G.handler.post(() -> {
                    currentName = nickName;
                    showLoading.set(View.GONE);
                    submitData();
                });
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                G.handler.post(() -> showLoading.set(View.GONE));
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                G.handler.post(() -> showLoading.set(View.GONE));
            }
        });
    }

    private void sendRequestSetUsername() {
        showLoading.set(View.VISIBLE);
        new RequestUserProfileUpdateUsername().userProfileUpdateUsername(userName.get(), new OnUserProfileUpdateUsername() {
            @Override
            public void onUserProfileUpdateUsername(final String username) {
                G.handler.post(() -> {
                    showLoading.set(View.GONE);
                    currentUserName = username;
                    submitData();
                });
            }

            @Override
            public void Error(final int majorCode, int minorCode, final int time) {
                G.handler.post(() -> {
                    showLoading.set(View.GONE);
                    if (majorCode == 175) {
                        dialogWaitTime(R.string.USER_PROFILE_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                    }
                });
            }

            @Override
            public void timeOut() {
                G.handler.post(() -> showLoading.set(View.GONE));
            }
        });
    }

    private void sendRequestSetEmail() {
        showLoading.set(View.VISIBLE);
        new RequestUserProfileSetEmail().setUserProfileEmail(email.get(), new OnUserProfileSetEmailResponse() {
            @Override
            public void onUserProfileEmailResponse(final String email, ProtoResponse.Response response) {
                G.handler.post(() -> {
                    showLoading.set(View.GONE);
                    currentUserEmail = email;
                    submitData();
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    showLoading.set(View.GONE);
                    if (majorCode == 114 && minorCode == 1) {
                        emailErrorMessage.set(R.string.error_email);
                        emailErrorEnable.setValue(true);
                        editProfileIcon.set(R.string.close_icon);
                    } else if (majorCode == 115) {
                        emailErrorEnable.setValue(true);
                        editProfileIcon.set(R.string.close_icon);
                        emailErrorMessage.set(R.string.error_email);
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> showLoading.set(View.GONE));
            }
        });
    }

    private void sendRequestSetBio() {
        new RequestUserProfileSetBio().setBio(bio.get());
        currentBio = bio.get();
        submitData();
    }

    private void sendRequestSetGender() {
        showLoading.set(View.VISIBLE);
        new RequestUserProfileSetGender().setUserProfileGender(gender.get() == R.id.male ? ProtoGlobal.Gender.MALE : ProtoGlobal.Gender.FEMALE, new OnUserProfileSetGenderResponse() {
            @Override
            public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                G.handler.post(() -> {
                    showLoading.set(View.GONE);
                    currentGender = gender == ProtoGlobal.Gender.MALE ? R.id.male : R.id.female;
                    submitData();
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                G.handler.post(() -> showLoading.set(View.GONE));
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> showLoading.set(View.GONE));
            }
        });
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialog.getCustomView();

        final TextView remindTime = v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override
            public void onFinish() {
                //                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
        };
        countWaitTimer.start();
    }

    public File getImageFile() {
        idAvatar = SUID.id().get();
        pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
        return new File(pathSaveImage);
    }

    @Override
    public void onAvatarAdd(ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            setUserAvatar.setValue(userId);
        } else {

            avatarHandler.avatarAdd(userId, pathSaveImage, avatar, avatarPath -> G.handler.post(() -> {
                showLoading.set(View.GONE);
                if (G.onChangeUserPhotoListener != null) {
                    G.onChangeUserPhotoListener.onChangePhoto(avatarPath);
                }
            }));
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddTimeOut() {
        G.handler.post(() -> showLoading.set(View.GONE));
    }

    @Override
    public void onAvatarError() {
        G.handler.post(() -> showLoading.set(View.GONE));
    }

    @Override
    public void setRefreshBalance() {
        getUserCredit();
    }

    @Override
    public void onUserInfoMyClient() {
        setUserAvatar.setValue(G.userId);
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.ON_ACCESS_TOKEN_RECIVE) {
            int response = (int) message[0];
            switch (response) {
                case socketMessages.SUCCESS:
                    new Handler(getMainLooper()).post(() -> {
                        getUserCredit();
                        retryConnectToWallet = 0;
                    });

                    break;

                case socketMessages.FAILED:
                    if (retryConnectToWallet < 3) {
                        new RequestWalletGetAccessToken().walletGetAccessToken();
                        retryConnectToWallet++;
                    }

                    break;
            }
            // backthread
        }
    }

    public class ChangeImageModel {
        private String imagePath;
        private String initials;
        private String color;

        ChangeImageModel(String imagePath, String initials, String color) {
            this.imagePath = imagePath;
            this.initials = initials;
            this.color = color;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getInitials() {
            return initials;
        }

        public String getColor() {
            return color;
        }
    }

    public class DeleteAvatarModel {
        private long userId;
        private long avatarId;

        public DeleteAvatarModel(long userId, long avatarId) {
            this.userId = userId;
            this.avatarId = avatarId;
        }

        public long getUserId() {
            return userId;
        }

        public long getAvatarId() {
            return avatarId;
        }
    }
}
