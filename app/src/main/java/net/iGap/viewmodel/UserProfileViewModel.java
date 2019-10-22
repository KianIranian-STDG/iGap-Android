package net.iGap.viewmodel;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnUserProfileCheckUsername;
import net.iGap.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.interfaces.OnUserProfileSetRepresentative;
import net.iGap.interfaces.OnUserProfileUpdateUsername;
import net.iGap.interfaces.RefreshWalletBalance;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.structs.StructCountry;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.realm.RealmWallpaper;
import net.iGap.realm.RealmWallpaperFields;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestInfoUpdate;
import net.iGap.request.RequestInfoWallpaper;
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
import net.iGap.request.RequestUserProfileSetRepresentative;
import net.iGap.request.RequestUserProfileUpdateUsername;
import net.iGap.request.RequestWalletGetAccessToken;

import org.jetbrains.annotations.NotNull;
import org.paygear.model.Card;
import org.paygear.web.Web;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.realm.Realm;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Looper.getMainLooper;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;

public class UserProfileViewModel extends ViewModel implements RefreshWalletBalance, OnUserInfoMyClient, EventListener, OnUserAvatarResponse {
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();

    private ObservableField<String> appVersion = new ObservableField<>("");
    private ObservableField<String> userPhoneNumber = new ObservableField<>();
    private ObservableLong currentCredit = new ObservableLong(0);
    private ObservableField<String> currentScore = new ObservableField<>("0");
    private ObservableBoolean isDarkMode = new ObservableBoolean(false);
    public ObservableInt editProfileIcon = new ObservableInt(R.string.edit_icon);
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
    private ObservableInt textsGravity = new ObservableInt(Gravity.LEFT);

    private MutableLiveData<Boolean> showDialogSelectCountry = new MutableLiveData<>();
    public MutableLiveData<Boolean> showReferralErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> referralEnableLiveData = new MutableLiveData<>();
    private ObservableField<String> referralNumberObservableField = new ObservableField<>("");
    public ObservableField<String> referralCountryCodeObservableField = new ObservableField<>("+98");
    public ObservableField<Integer> referralError = new ObservableField<>(R.string.already_registered);
    private int phoneMax = 10;
    private boolean sendReferral = false;

    //ui
    public SingleLiveEvent<Boolean> goToAddMemberPage = new SingleLiveEvent<>();
    public SingleLiveEvent<String> goToWalletAgreementPage = new SingleLiveEvent<>();
    public SingleLiveEvent<String> goToWalletPage = new SingleLiveEvent<>();
    public SingleLiveEvent<String> shareInviteLink = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToScannerPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> checkLocationPermission = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToIGapMapPage = new SingleLiveEvent<>();
    public SingleLiveEvent<String> goToFAQPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToSettingPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToUserScorePage = new SingleLiveEvent<>();
    public SingleLiveEvent<Long> goToShowAvatarPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showDialogBeLastVersion = new SingleLiveEvent<>();
    public SingleLiveEvent<String> showDialogUpdate = new SingleLiveEvent<>();
    public MutableLiveData<Long> setUserAvatar = new MutableLiveData<>();
    public SingleLiveEvent<DeleteAvatarModel> deleteAvatar = new SingleLiveEvent<>();
    public SingleLiveEvent<ChangeImageModel> setUserAvatarPath = new SingleLiveEvent<>();
    public SingleLiveEvent<GoToChatModel> goToChatPage = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> isEditProfile = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> showDialogChooseImage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> updateNewTheme = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> updateTwoPaneView = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> showError = new SingleLiveEvent<>();
    public MutableLiveData<Drawable> changeUserProfileWallpaper = new MutableLiveData<>();
    public MutableLiveData<Boolean> setCurrentFragment = new MutableLiveData<>();

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

    private int retryRequestTime = -1;

    //Todo: fixed it
    private int getIvanScoreTime = 0;

    public UserProfileViewModel(SharedPreferences sharedPreferences, AvatarHandler avatarHandler) {
        this.sharedPreferences = sharedPreferences;
        this.avatarHandler = avatarHandler;
        mRealm = Realm.getDefaultInstance();
        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        checkProfileWallpaper(mRealm);
        updateUserInfoUI();
        if (checkValidationForRealm(userInfo)) {
            userInfo.addChangeListener(realmModel -> {
                userInfo = (RealmUserInfo) realmModel;
                updateUserInfoUI();
            });
        }


        appVersion.set(BuildConfig.VERSION_NAME);

        //set user info text gravity
        if (!G.isAppRtl) {
            textsGravity.set(Gravity.LEFT);
        } else {
            textsGravity.set(Gravity.RIGHT);
        }
    }

    public SingleLiveEvent<Boolean> getUpdateNewTheme() {
        return updateNewTheme;
    }

    public SingleLiveEvent<Boolean> getUpdateTwoPaneView() {
        return updateTwoPaneView;
    }

    public void init() {
        setCurrentFragment.setValue(isEditMode());
        isDarkMode.set(G.themeColor == Theme.DARK);
        //set credit amount
        if (G.selectedCard != null) {
            currentCredit.set(G.cardamount);
        } else {
            getUserCredit();
        }

        FragmentShowAvatars.onComplete = (result, messageOne, MessageTow) -> {
            long mAvatarId = 0;
            if (messageOne != null && !messageOne.equals("")) {
                mAvatarId = Long.parseLong(messageOne);
            }
            long finalMAvatarId = mAvatarId;
            deleteAvatar.postValue(new DeleteAvatarModel(userId, finalMAvatarId));
        };

        G.onUserAvatarResponse = this;

        getIVandScore();
        new RequestUserProfileGetGender().userProfileGetGender();
        new RequestUserProfileGetEmail().userProfileGetEmail();
        new RequestUserProfileGetBio().getBio();
        if (G.isNeedToCheckProfileWallpaper)
            getProfileWallpaperFromServer();

    }

    public void updateUserInfoUI() {
        if (checkValidationForRealm(userInfo)) {
            userId = userInfo.getUserId();
            phoneNumber = userInfo.getUserInfo().getPhoneNumber();
            userPhoneNumber.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(phoneNumber) : phoneNumber);
            setUserAvatar.setValue(userInfo.getUserId());
            currentName = userInfo.getUserInfo().getDisplayName();
            currentUserName = userInfo.getUserInfo().getUsername();
            currentUserEmail = userInfo.getEmail();
            currentBio = userInfo.getUserInfo().getBio();
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
        }
    }

    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        return realmUserInfo != null && realmUserInfo.isValid();
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

    public ObservableInt getTextsGravity() {
        return textsGravity;
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

    public ObservableField<String> getReferralNumberObservableField() {
        return referralNumberObservableField;
    }

    public ArrayList<StructCountry> getStructCountryArrayList() {
        return structCountryArrayList;
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

    public MutableLiveData<Boolean> getReferralEnableLiveData() {
        return referralEnableLiveData;
    }

    public ObservableInt getEmailErrorMessage() {
        return emailErrorMessage;
    }

    public MutableLiveData<Boolean> getShowDialogSelectCountry() {
        return showDialogSelectCountry;
    }

    public ObservableInt getShowLoading() {
        return showLoading;
    }
//
//    public void onEditProfileClick() {
//        if (isEditProfile.getValue() != null && isEditProfile.getValue()) {
//            editProfileIcon.set(R.string.edit_icon);
//            isEditProfile.setValue(false);
//        } else {
//            editProfileIcon.set(R.string.close_icon);
//            isEditProfile.setValue(true);
//            if (editProfileIcon.get() == R.string.close_icon) {
//                isEditProfile.setValue(true);
//            } else {
//                isEditProfile.setValue(false);
//            }
//
//        }
//        setCurrentFragment.setValue(isEditProfile.getValue());
//    }
    public void onEditProfileClick() {
        if (isEditProfile.getValue() != null && isEditProfile.getValue()) {
            if (editProfileIcon.get() == R.string.check_icon) {
                submitData();
            } else {
                editProfileIcon.set(R.string.edit_icon);
                isEditProfile.setValue(false);
            }
        } else {
            editProfileIcon.set(R.string.close_icon);
            isEditProfile.setValue(true);
            getReferral();
        }
        setCurrentFragment.setValue(isEditProfile.getValue());
    }

    public void onCloudMessageClick() {
        showLoading.set(View.VISIBLE);
        retryRequestTime++;
        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userInfo.getUserId()).findFirst();
        if (realmRoom != null) {
            showLoading.set(View.GONE);
            goToChatPage.setValue(new GoToChatModel(realmRoom.getId(), userInfo.getUserId()));
        } else {
            if (retryRequestTime < 3) {
                new RequestChatGetRoom().chatGetRoom(userInfo.getUserId(), new RequestChatGetRoom.OnChatRoomReady() {
                    @Override
                    public void onReady(ProtoGlobal.Room room) {
                        RealmRoom.putOrUpdate(room);
                        G.handler.post(() -> {
                            G.refreshRealmUi();
                            showLoading.set(View.GONE);
                            goToChatPage.postValue(new GoToChatModel(room.getId(), userInfo.getUserId()));
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {
                        if (majorCode == 5 && minorCode == 1) {
                            showError.postValue(R.string.connection_error);
                            G.handler.post(() -> showLoading.set(View.GONE));
                        } else {
                            showError.postValue(R.string.error);
                            G.handler.post(() -> showLoading.set(View.GONE));
                        }
                    }
                });
            } else {
                showLoading.set(View.GONE);
                showError.setValue(R.string.error);
                retryRequestTime = -1;
            }
        }
    }

    public void onSettingClick() {
        goToSettingPage.setValue(true);
    }

    public void onAddClick() {
        goToAddMemberPage.setValue(true);
    }

    public void onCreditClick() {
        if (!G.isWalletRegister) {
            goToWalletAgreementPage.setValue(HelperNumerical.getPhoneNumberStartedWithZero(phoneNumber));
        } else {
            goToWalletPage.setValue(HelperNumerical.getPhoneNumberStartedWithZero(phoneNumber));
        }
    }

    public void onScoreClick() {
        goToUserScorePage.setValue(true);
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
        isDarkMode.set(!isCheck);
        if (isDarkMode.get()) {
            G.themeColor = Theme.DARK;
            int themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DEFAULT);
            sharedPreferences.edit().
                    putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DARK).
                    putInt(SHP_SETTING.KEY_OLD_THEME_COLOR, themeColor).
                    apply();
        } else {
            int themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_OLD_THEME_COLOR, Theme.DEFAULT);
            G.themeColor = themeColor;
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_THEME_COLOR, themeColor).apply();
        }

        updateNewTheme.setValue(true);
        if (G.twoPaneMode) {
            updateTwoPaneView.setValue(true);
        }
    }

    public void onFAQClick() {
        goToFAQPage.setValue("https://blog.igap.net/fa/%d9%be%d8%b1%d8%b3%d8%b4%e2%80%8c%d9%87%d8%a7%db%8c-%d9%85%d8%aa%d8%af%d8%a7%d9%88%d9%84/");
    }

    public void onAvatarClick() {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst() != null) {
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

    public void referralTextChangeListener(String phoneNumber) {
        if (isEditMode()) {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "";
            }
            if (phoneNumber.length() == phoneMax && sendReferral) {
                editProfileIcon.set(R.string.check_icon);
            } else {
                editProfileIcon.set(R.string.close_icon);
            }
            referralNumberObservableField.set(phoneNumber);
        }
    }

    public void bioTextChangeListener(String newBio) {
        if (isEditMode()) {
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

    public void onChangeLogClick() {
        showLoading.set(View.VISIBLE);
        new RequestInfoUpdate().infoUpdate(BuildConfig.VERSION_CODE, new RequestInfoUpdate.updateInfoCallback() {
            @Override
            public void onSuccess(int lastVersion, String Body) {
                showLoading.set(View.GONE);
                if (lastVersion <= BuildConfig.VERSION_CODE) {
                    showDialogBeLastVersion.postValue(true);
                } else {
                    showDialogUpdate.postValue(HtmlCompat.fromHtml(Body, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                }
            }

            @Override
            public void onError(int major, int minor) {
                showLoading.set(View.GONE);
                if (major == 5 && minor == 1) {
                    showError.setValue(R.string.connection_error);
                } else {
                    showError.setValue(R.string.error);
                }
            }
        });
    }

    private void getUserCredit() {
        WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
        if (Auth.getCurrentAuth() != null) {
            Web.getInstance().getWebService().getCards(null, false, true).enqueue(new Callback<ArrayList<Card>>() {
                @Override
                public void onResponse(@NotNull Call<ArrayList<Card>> call, @NotNull Response<ArrayList<Card>> response) {
                    if (response.body() != null) {
                        retryConnectToWallet = 0;
                        G.cardamount = 0;

                        for (Card card : response.body()) {
                            if (card.isRaadCard()) {
                                G.selectedCard = card;
                                G.cardamount += card.balance;
                            }
                            if (card.type == 1 && (card.bankCode == 69 && card.clubId != null)) {
                                G.cardamount += card.balance;
                            }
                        }

                        currentCredit.set(G.cardamount);
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

    @Override
    protected void onCleared() {
        userInfo.removeAllChangeListeners();
        mRealm.close();
        super.onCleared();
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
        } else if (!referralNumberObservableField.get().equals("") && referralEnableLiveData.getValue()) {
            Log.wtf(this.getClass().getName(), "setReferral");
            setReferral(referralCountryCodeObservableField.get().replace("+", "") + referralNumberObservableField.get().replace(" ", ""));
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

    private void getIVandScore() {
        getIvanScoreTime++;
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                G.handler.post(() -> currentScore.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(score.getScore())) : String.valueOf(score.getScore())));
            }

            @Override
            public void onError(int major, int minor) {
                if (getIvanScoreTime > 3) {
                    if (major == 5 && minor == 1) {
                        getIVandScore();
                    }
                }
            }
        });
    }

    public boolean checkEditModeForOnBackPressed() {
        if (isEditProfile.getValue() != null) {
            if (isEditProfile.getValue()) {
                isEditProfile.setValue(false);
                getEditProfileIcon().set(R.string.edit_icon);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void getProfileWallpaperFromServer() {
        G.onGetProfileWallpaper = list -> {

            G.isNeedToCheckProfileWallpaper = false;

            try (Realm realm = Realm.getDefaultInstance()) {
                RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo(RealmWallpaperFields.TYPE, ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE).findFirst();

                if (realmWallpaper != null) {

                    if (realmWallpaper.getWallPaperList().get(0).getFile().getToken().equals(list.get(0).getFile().getToken())) {
                        getProfileWallpaper(realm);
                    } else {
                        RealmWallpaper.updateWallpaper(list);
                        getProfileWallpaper(realm);
                    }
                } else {
                    RealmWallpaper.updateField(list, "", ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE);
                    getProfileWallpaper(realm);
                }
            }
        };

        new RequestInfoWallpaper().infoWallpaper(ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER);
    }

    /**
     * just when user profile downloaded and available in realm load it
     * else send null to mutable live data to set default at fragment
     *
     * @param realm
     */
    private void checkProfileWallpaper(Realm realm) {
        try {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo(RealmWallpaperFields.TYPE, ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE).findFirst();

            if (realmWallpaper != null) {
                RealmAttachment pf = realmWallpaper.getWallPaperList().get(realmWallpaper.getWallPaperList().size() - 1).getFile();
                String bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                if (!new File(bigImagePath).exists()) {
                    changeUserProfileWallpaper.postValue(null);
                } else {
                    setProfileWallpaperOrDefault(bigImagePath);
                }
            } else {
                changeUserProfileWallpaper.postValue(null);
            }
        } catch (Exception e) {
            changeUserProfileWallpaper.postValue(null);
        }
    }

    private void getProfileWallpaper(Realm realm) {
        try {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo(RealmWallpaperFields.TYPE, ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE).findFirst();

            if (realmWallpaper != null) {

                if (realmWallpaper.getWallPaperList() != null && realmWallpaper.getWallPaperList().size() > 0) {
                    RealmAttachment pf = realmWallpaper.getWallPaperList().get(realmWallpaper.getWallPaperList().size() - 1).getFile();
                    String bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                    if (!new File(bigImagePath).exists()) {
                        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, bigImagePath, 2, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String mPath, final int progress) {
                                if (progress == 100) {
                                    setProfileWallpaperOrDefault(bigImagePath);
                                }
                            }

                            @Override
                            public void OnError(String token) {
                            }
                        });
                    } else {
                        setProfileWallpaperOrDefault(bigImagePath);
                    }
                } else {
                    getProfileWallpaperFromServer();
                }
            } else {
                getProfileWallpaperFromServer();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }

    }

    private void setProfileWallpaperOrDefault(String backGroundPath) {

        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                try {
                    Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                    changeUserProfileWallpaper.postValue(d);
                    return;
                } catch (OutOfMemoryError e) {
                    changeUserProfileWallpaper.postValue(null);
                    ActivityManager activityManager = (ActivityManager) G.context.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);
                    Crashlytics.logException(new Exception("FragmentChat -> Device Name : " + Build.BRAND + " || memoryInfo.availMem : " + memoryInfo.availMem + " || memoryInfo.totalMem : " + memoryInfo.totalMem + " || memoryInfo.lowMemory : " + memoryInfo.lowMemory));
                }
            }
        }

        changeUserProfileWallpaper.postValue(null);
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
                if (checkValidationForRealm(userInfo)) {
                    setUserAvatarPath.postValue(new ChangeImageModel(avatarPath, userInfo.getUserInfo().getInitials(), userInfo.getUserInfo().getColor()));
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

    public void uploadAvatar(String path) {
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
                G.handler.post(() -> showLoading.set(View.GONE));
            }
        });
    }

    public void onCountryCodeClick() {
        if (showReferralErrorLiveData.getValue() != null && showReferralErrorLiveData.getValue()) {
            showDialogSelectCountry.setValue(true);
        }
    }


    public void setReferral(String phoneNumber) {
        new RequestUserProfileSetRepresentative().userProfileSetRepresentative(phoneNumber, new OnUserProfileSetRepresentative() {
            @Override
            public void onSetRepresentative(String phone) {
                referralEnableLiveData.postValue(false);
                referralNumberObservableField.set("");
                G.handler.post(() -> submitData());
            }

            @Override
            public void onErrorSetRepresentative(int majorCode, int minorCode) {
                showReferralErrorLiveData.postValue(true);
                showLoading.set(View.GONE);
                switch (majorCode) {
                    case 10177:
                        if (minorCode == 2) {
                            referralError.set(R.string.referral_error_yourself);
                        } else {
                            referralError.set(R.string.phone_number_is_not_valid);
                        }
                        break;
                    case 10178:
                        if (minorCode == 2)
                            referralError.set(R.string.already_registered);
                        else
                            referralError.set(R.string.server_error);
                        break;
                }
            }
        });
        editProfileIcon.set(R.string.check_icon);
    }

    private void countryReader() {
        structCountryArrayList = new ArrayList<>();
        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String[] listArray = list.split("\\r?\\n");
        //Convert array
        for (String s : listArray) {
            StructCountry structCountry = new StructCountry();
            String[] listItem = s.split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);
            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }
            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());
//
//        referralCountryCodeObservableField.set("+" + sharedPreferences.getInt("callingCode", 98));
//        String pattern = sharedPreferences.getString("pattern", "");
//        referralCountryCodeObservableField.set((pattern != null && !pattern.equals("")) ? pattern.replace("X", "#").replace(" ", "-") : "##################");

    }

    private void getReferral() {
        showDialogSelectCountry.postValue(false);
        showReferralErrorLiveData.postValue(false);
        new RequestUserProfileGetRepresentative().userProfileGetRepresentative(new RequestUserProfileGetRepresentative.OnRepresentReady() {
            @Override
            public void onRepresent(String phoneNumber) {
                G.handler.postDelayed(() -> {
                    referralNumberObservableField.set(phoneNumber);

                    if (phoneNumber.equals("")) {
                        referralEnableLiveData.postValue(true);
                        countryReader();
                        sendReferral = true;
                    } else {
                        referralCountryCodeObservableField.set("");
                        referralEnableLiveData.postValue(false);
                        sendReferral = false;
                    }
                }, 500);
            }

            @Override
            public void onFailed() {
                referralEnableLiveData.postValue(false);
            }
        });
    }


    public void setCountry(StructCountry country) {
        phoneMax = country.getPhonePattern().replace(" ", "").length();
        new RequestInfoCountry().infoCountry(country.getAbbreviation(), new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                G.handler.post(() -> {
                    referralCountryCodeObservableField.set("+" + callingCode);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        });
        referralNumberObservableField.set("");
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

    public class GoToChatModel {
        private long roomId;
        private long peerId;

        public GoToChatModel(long roomId, long peerId) {
            this.roomId = roomId;
            this.peerId = peerId;
        }

        public long getRoomId() {
            return roomId;
        }

        public long getPeerId() {
            return peerId;
        }
    }
}
