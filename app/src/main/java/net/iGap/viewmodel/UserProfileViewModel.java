package net.iGap.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructCountry;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.eventbus.SocketMessages;
import net.iGap.observers.interfaces.OnGeoGetConfiguration;
import net.iGap.observers.interfaces.OnInfoCountryResponse;
import net.iGap.observers.interfaces.OnUserAvatarResponse;
import net.iGap.observers.interfaces.OnUserIVandGetScore;
import net.iGap.observers.interfaces.OnUserInfoMyClient;
import net.iGap.observers.interfaces.OnUserProfileCheckUsername;
import net.iGap.observers.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.observers.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.observers.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.observers.interfaces.OnUserProfileSetRepresentative;
import net.iGap.observers.interfaces.OnUserProfileUpdateUsername;
import net.iGap.observers.interfaces.RefreshWalletBalance;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoInfoWallpaper;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.proto.ProtoUserProfileCheckUsername;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.realm.RealmWallpaper;
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

import static android.os.Looper.getMainLooper;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;

public class UserProfileViewModel extends ViewModel implements RefreshWalletBalance, OnUserInfoMyClient, EventManager.EventDelegate, OnUserAvatarResponse {
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();
    private final ObservableField<String> appVersion = new ObservableField<>("");
    private final ObservableField<String> userPhoneNumber = new ObservableField<>();
    private final ObservableLong currentCredit = new ObservableLong(0);
    private final ObservableField<String> currentScore = new ObservableField<>("0");
    private final ObservableBoolean isDarkMode = new ObservableBoolean(false);
    private final ObservableInt editProfileIcon = new ObservableInt(View.VISIBLE);
    private final ObservableField<String> name = new ObservableField<>("");
    private final ObservableField<String> userName = new ObservableField<>("");
    private final ObservableField<String> bio = new ObservableField<>("");
    private final ObservableField<String> email = new ObservableField<>("");
    private final ObservableField<String> birthDate = new ObservableField<>("");
    private final ObservableInt gender = new ObservableInt(-1);
    private final MutableLiveData<Boolean> usernameErrorEnable = new MutableLiveData<>();
    private final ObservableInt usernameErrorMessage = new ObservableInt(R.string.empty_error_message);
    private final MutableLiveData<Boolean> emailErrorEnable = new MutableLiveData<>();
    private final ObservableInt emailErrorMessage = new ObservableInt(R.string.empty_error_message);
    private final ObservableInt showLoading = new ObservableInt(View.GONE);
    private final ObservableInt textsGravity = new ObservableInt(Gravity.LEFT);
    private final ObservableBoolean showReferralErrorLiveData = new ObservableBoolean(false);
    private final ObservableInt referralError = new ObservableInt(R.string.waiting_for_network);
    private final ObservableInt showAddAvatarButton = new ObservableInt(View.GONE);
    private final MutableLiveData<Boolean> showDialogSelectCountry = new MutableLiveData<>();
    private final MutableLiveData<Boolean> referralEnableLiveData = new MutableLiveData<>();
    private final ObservableField<String> referralNumberObservableField = new ObservableField<>("");
    public ObservableField<String> referralCountryCodeObservableField = new ObservableField<>("+98");
    public ObservableField<String> referralMaskObservableField = new ObservableField<>("###-###-####");

    //ui
    private final SingleLiveEvent<Boolean> goToAddMemberPage = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> goToWalletAgreementPage = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> goToWalletPage = new SingleLiveEvent<>();
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
    public SingleLiveEvent<Boolean> showDialogChooseImage = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> updateNewTheme = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> updateTwoPaneView = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> showError = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> showEditError = new SingleLiveEvent<>();
    public MutableLiveData<String> changeUserProfileWallpaperPath = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> openAccountsDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> setCurrentFragment = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> popBackStack = new SingleLiveEvent<>();
    private final MutableLiveData<Boolean> cancelIconClick = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closeKeyboard = new MutableLiveData<>();
    private final ObservableInt checkProfileShow = new ObservableInt(View.GONE);
    private final ObservableInt cancelProfileShow = new ObservableInt(View.GONE);
    private int phoneMax = 10;
    private RealmUserInfo userInfo;
    private String phoneNumber;
    private int retryConnectToWallet = 0;
    private String currentName;
    private String currentUserName;
    private String currentUserEmail;
    private int currentGender;
    private String currentBio;
    private long userId;
    public String pathSaveImage;
    private long idAvatar;
    private final AvatarHandler avatarHandler;
    private boolean isEditProfile;
    private int retryRequestTime = -1;
    private int getIvanScoreTime = 0;
    private boolean hasError;
    private boolean nameError;
    private boolean userNameError;
    private boolean emailError;
    private boolean genderError;

    public UserProfileViewModel(AvatarHandler avatarHandler) {
        this.avatarHandler = avatarHandler;
        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmUserInfo.class).findFirst();
            checkProfileWallpaper(realm);
        });

        updateUserInfoUI();
        if (checkValidationForRealm(userInfo)) {
            userInfo.addChangeListener(realmModel -> {
                userInfo = (RealmUserInfo) realmModel;
                updateUserInfoUI();
            });
        }

        setCurrentFragment.setValue(isEditProfile);
        appVersion.set(BuildConfig.VERSION_NAME);
    }

    public void init() {
        //set credit amount

        getUserCredit();
        isDarkMode.set(G.themeColor == Theme.DARK);

        //set user info text gravity
        if (!G.isAppRtl) {
            textsGravity.set(Gravity.LEFT);
        } else {
            textsGravity.set(Gravity.RIGHT);
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
        if (G.isNeedToCheckProfileWallpaper) {
            getProfileWallpaperFromServer();
        }
    }

    private void updateUserInfoUI() {
        if (checkValidationForRealm(userInfo)) {
            if (userInfo.getUserId() != 0)
                userId = userInfo.getUserId();

            currentCredit.set(userInfo.getWalletAmount());
            phoneNumber = userInfo.getUserInfo().getPhoneNumber();
            userPhoneNumber.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(phoneNumber) : phoneNumber);
            setUserAvatar.setValue(userInfo.getUserId());
            currentName = userInfo.getUserInfo().getDisplayName() != null ? userInfo.getUserInfo().getDisplayName() : "";
            currentUserName = userInfo.getUserInfo().getUsername() != null ? userInfo.getUserInfo().getUsername() : "";
            currentUserEmail = userInfo.getEmail() != null ? userInfo.getEmail() : "";
            currentBio = userInfo.getUserInfo().getBio() != null ? userInfo.getUserInfo().getBio() : "";
            currentScore.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(userInfo.getIvandScore())) : String.valueOf(userInfo.getIvandScore()));

            ProtoGlobal.Gender userGender = userInfo.getGender();
            if (userGender != null) {
                if (userGender == ProtoGlobal.Gender.MALE) {
                    currentGender = R.id.male;
                } else if (userGender == ProtoGlobal.Gender.FEMALE) {
                    currentGender = R.id.female;
                }
            }
            gender.set(currentGender);
            name.set(currentName);
            bio.set(currentBio);
            userName.set(currentUserName);
            email.set(currentUserEmail);
        }
    }

    public void onEditProfileClick() {
        String tmp = referralNumberObservableField.get();
        if (referralNumberObservableField == null || tmp.isEmpty()) {
            getReferral();
        }
        checkProfileShow.set(View.VISIBLE);
        cancelProfileShow.set(View.VISIBLE);
        editProfileIcon.set(View.GONE);
        isEditProfile = true;
        setCurrentFragment.setValue(isEditProfile);
        showAddAvatarButton.set(isEditProfile ? View.VISIBLE : View.GONE);
    }

    private void showEditIcon() {
        checkProfileShow.set(View.GONE);
        cancelProfileShow.set(View.GONE);
        editProfileIcon.set(View.VISIBLE);
        checkEditModeForOnBackPressed();
    }

    public void onCheckClick() {
        referralError.set(R.string.empty_error_message);
        showReferralErrorLiveData.set(false);
        closeKeyboard.setValue(true);
        submitData();
    }

    public void onCancelClick() {
        closeKeyboard.setValue(true);
        hasError = false;
        showEditIcon();
        isEditProfile = false;
        setCurrentFragment.setValue(isEditProfile);
        cancelIconClick.setValue(true);
    }

    public void onAccountsClicked() {
        openAccountsDialog.setValue(true);
    }

    public void onCloudMessageClick() {
        showLoading.set(View.VISIBLE);
        retryRequestTime++;
        RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", userInfo.getUserId()).findFirst();
        });
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

    public void onCreditClick() {
        if (!userInfo.isWalletRegister()) {
            goToWalletAgreementPage.setValue(HelperNumerical.getPhoneNumberStartedWithZero(phoneNumber));
        } else {
            goToWalletPage.setValue(HelperNumerical.getPhoneNumberStartedWithZero(phoneNumber));
        }
    }

    public void onScoreClick() {
        goToUserScorePage.setValue(true);
    }

    public void onInviteFriendsClick() {
        HelperTracker.sendTracker(HelperTracker.TRACKER_INVITE_FRIEND);
        shareInviteLink.setValue(BuildConfig.INVITE_FRIEND_LINK);
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

    public void onDarkModeClicked(boolean isChecked) {
        isDarkMode.set(!isChecked);
        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
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
        goToFAQPage.setValue(BuildConfig.FAQ_LINK);
    }

    public void onAvatarClick() {
        if (DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmAvatar.class).equalTo("ownerId", userId).findFirst();
        }) != null) {
            goToShowAvatarPage.setValue(userInfo.getUserId());
        }
    }

    public void onAddImageClick() {
        showDialogChooseImage.setValue(true);
    }

    public void usernameTextChangeListener(String newUsername) {
        if (!newUsername.equals(currentUserName)) {
            if (HelperString.regexCheckUsername(newUsername)) {
                new RequestUserProfileCheckUsername().userProfileCheckUsername(newUsername, new OnUserProfileCheckUsername() {
                    @Override
                    public void OnUserProfileCheckUsername(final ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status) {
                        G.handler.post(() -> {
                            if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.AVAILABLE) {
                                usernameErrorEnable.setValue(true);
                                usernameErrorMessage.set(R.string.empty_error_message);
                            } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.INVALID) {
                                usernameErrorMessage.set(R.string.INVALID);
                                usernameErrorEnable.setValue(true);
                            } else if (status == ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status.TAKEN) {
                                usernameErrorMessage.set(R.string.TAKEN);
                                usernameErrorEnable.setValue(true);
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
            }
        }
    }

    public void emailTextChangeListener(String newEmail) {
        if (!newEmail.equals(currentUserEmail)) {
            emailErrorMessage.set(R.string.empty_error_message);
            emailErrorEnable.setValue(false);
        }
    }

    public void referralTextChangeListener(String phoneNumber) {
        referralError.set(R.string.empty_error_message);
        if (phoneNumber.startsWith("0")) {
            referralError.set(R.string.Toast_First_0);
            phoneNumber = "";
            hasError = true;
            showLoading.set(View.GONE);
        }
        referralNumberObservableField.set(phoneNumber);
    }

    public void genderCheckedListener(int checkedId) {
        if (currentGender != checkedId) {
            gender.set(checkedId);
        } else {
            gender.set(currentGender);
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
        WebBase.apiKey = BuildConfig.WEB_BASE_API_KEY;
        if (Auth.getCurrentAuth() != null) {
            Web.getInstance().getWebService().getCards(null, false, true).enqueue(new Callback<ArrayList<Card>>() {
                @Override
                public void onResponse(@NotNull Call<ArrayList<Card>> call, @NotNull Response<ArrayList<Card>> response) {
                    if (response.body() != null) {
                        retryConnectToWallet = 0;
                        long cardAmount = 0;

                        for (Card card : response.body()) {
                            if (card.isRaadCard()) {
                                G.selectedCard = card;
                                cardAmount += card.balance;
                            }
                            if (card.type == 1 && (card.bankCode == 69 && card.clubId != null)) {
                                cardAmount += card.balance;
                            }
                        }

                        long finalCardAmount = cardAmount;
                        new Thread(() -> DbManager.getInstance().doRealmTransaction(realm -> {
                            RealmUserInfo user = realm.where(RealmUserInfo.class).findFirst();
                            if (user != null) {
                                user.setWalletAmount(finalCardAmount);
                            }

                        })).start();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArrayList<Card>> call, @NotNull Throwable t) {

                    if (retryConnectToWallet < 3) {
                        FirebaseCrashlytics.getInstance().recordException(t);
                        getUserCredit();
                        retryConnectToWallet++;
                    }
                }
            });
        }
    }

    @Override
    protected void onCleared() {
        if (userInfo != null) {
            userInfo.removeAllChangeListeners();
        }
        super.onCleared();
    }

    private void submitData() {
        if (!currentName.equals(name.get()) || !currentUserName.equals(userName.get()) || !currentBio.equals(bio.get()) || !currentUserEmail.equals(email.get()) || currentGender != gender.get()) {
            showLoading.set(View.VISIBLE);
            if (!currentName.equals(name.get()))
                sendRequestSetName();
            if (!currentUserName.equals(userName.get()))
                sendRequestSetUsername();
            if (!currentBio.equals(bio.get()))
                sendRequestSetBio();
            if (!currentUserEmail.equals(email.get()))
                sendRequestSetEmail();
            if (currentGender != gender.get())
                sendRequestSetGender();
            if (!referralNumberObservableField.get().equals("") && referralEnableLiveData.getValue()) {
                referralTextChangeListener(referralNumberObservableField.get());
                if (!hasError) {
                    setReferral(referralCountryCodeObservableField.get().replace("+", "") + referralNumberObservableField.get().replaceAll("-", "").replaceAll(" ", ""));
                }
            }
        } else {
            showEditIcon();
        }
    }

    public void checkResponse() {
        if (!hasError && !nameError && !userNameError && !emailError && !genderError) {
            showLoading.set(View.GONE);
            isEditProfile = false;
            setCurrentFragment.setValue(isEditProfile);
            showEditIcon();
        } else if (hasError || nameError || userNameError || emailError || genderError) {
            if (nameError)
                showEditError.postValue(R.string.name_error);
            if (userNameError)
                showEditError.postValue(R.string.user_name_error);
            if (emailError)
                showEditError.postValue(R.string.email_error);
            if (genderError)
                showEditError.postValue(R.string.gender_error);
            isEditProfile = true;
            setCurrentFragment.setValue(isEditProfile);
            checkProfileShow.set(View.VISIBLE);
            cancelProfileShow.set(View.VISIBLE);
            editProfileIcon.set(View.GONE);
            showLoading.set(View.GONE);
            popBackStack.setValue(true);
        }
    }

    private void sendRequestSetName() {
        new RequestUserProfileSetNickname().userProfileNickName(name.get(), new OnUserProfileSetNickNameResponse() {
            @Override
            public void onUserProfileNickNameResponse(final String nickName, String initials) {
                G.handler.post(() -> {
                    nameError = false;
                    checkResponse();
                    RealmRoom.updateChatTitle(userId, nickName);
                    AccountManager.getInstance().updateCurrentNickName(nickName);
                    currentName = nickName;
                });
            }

            @Override
            public void onUserProfileNickNameError(int majorCode, int minorCode) {
                checkResponse();
                nameError = true;
                currentName = name.get();
            }

            @Override
            public void onUserProfileNickNameTimeOut() {
                nameError = true;
                checkResponse();
            }
        });
    }

    private void sendRequestSetUsername() {
        new RequestUserProfileUpdateUsername().userProfileUpdateUsername(userName.get(), new OnUserProfileUpdateUsername() {
            @Override
            public void onUserProfileUpdateUsername(final String username) {
                checkResponse();
                userNameError = false;
                currentUserName = username;
            }

            @Override
            public void Error(final int majorCode, int minorCode, final int time) {
                G.handler.post(() -> {
                    currentUserName = userName.get();
                    checkResponse();
                    userNameError = true;
                    if (majorCode == 175) {
                        dialogWaitTime(R.string.USER_PROFILE_UPDATE_USERNAME_UPDATE_LOCK, time);
                    }

                });
            }

            @Override
            public void timeOut() {
                checkResponse();
                userNameError = true;
            }
        });
    }

    private void sendRequestSetEmail() {
        new RequestUserProfileSetEmail().setUserProfileEmail(email.get(), new OnUserProfileSetEmailResponse() {
            @Override
            public void onUserProfileEmailResponse(final String email, ProtoResponse.Response response) {
                checkResponse();
                emailError = false;
                currentUserEmail = email;
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                checkResponse();
                emailError = true;
                currentUserEmail = email.get();
                if (majorCode == 114 && minorCode == 1) {
                    emailErrorEnable.setValue(true);
                } else if (majorCode == 115) {
                    emailErrorEnable.setValue(true);
                }
            }

            @Override
            public void onTimeOut() {
                checkResponse();
                emailError = true;
            }
        });
    }

    private void sendRequestSetBio() {
        new RequestUserProfileSetBio().setBio(bio.get());
        currentBio = bio.get();
    }

    private void sendRequestSetGender() {
        new RequestUserProfileSetGender().setUserProfileGender(gender.get() == R.id.male ? ProtoGlobal.Gender.MALE : ProtoGlobal.Gender.FEMALE, new OnUserProfileSetGenderResponse() {
            @Override
            public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                checkResponse();
                genderError = false;
                currentGender = (gender == ProtoGlobal.Gender.MALE) ? R.id.male : R.id.female;
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                checkResponse();
                genderError = true;
                currentGender = (gender.get() == R.id.male ? ProtoGlobal.Gender.MALE.getNumber() : ProtoGlobal.Gender.FEMALE.getNumber());
            }

            @Override
            public void onTimeOut() {
                checkResponse();
                genderError = true;
            }
        });
    }

    private void dialogWaitTime(int title, long time) {
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
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder ivandGetScore) {

            }

            @Override
            public void onError(int major, int minor) {
                if (getIvanScoreTime < 3) {
                    if (major == 5 && minor == 1) {
                        getIVandScore();
                    }
                }
            }
        });
    }

    public boolean checkEditModeForOnBackPressed() {
        if (isEditProfile) {
            popBackStack.setValue(true);
            showAddAvatarButton.set(View.GONE);
            cancelProfileShow.set(View.GONE);
            checkProfileShow.set(View.GONE);
            editProfileIcon.set(View.VISIBLE);
            isEditProfile = false;
            return false;
        } else {
            return true;
        }
    }

    private void getProfileWallpaperFromServer() {
        G.onGetProfileWallpaper = list -> {
            G.isNeedToCheckProfileWallpaper = false;
            DbManager.getInstance().doRealmTask(realm -> {
                RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo("type", ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE).findFirst();

                if (realmWallpaper != null) {
                    int count = realmWallpaper.getWallPaperList().size();
                    assert realmWallpaper.getWallPaperList().get(count - 1) != null;
                    if (!realmWallpaper.getWallPaperList().get(count - 1).getFile().getToken().equals(list.get(0).getFile().getToken())) {
                        RealmWallpaper.updateWallpaper(list);
                    }
                } else {
                    RealmWallpaper.updateField(list, "", ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE);
                }
                getProfileWallpaper(realm);
            });
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
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo("type", ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE).findFirst();

            if (realmWallpaper != null) {
                RealmAttachment pf = realmWallpaper.getWallPaperList().get(realmWallpaper.getWallPaperList().size() - 1).getFile();
                String bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                changeUserProfileWallpaperPath.postValue(bigImagePath);

            } else {
                changeUserProfileWallpaperPath.postValue(null);
            }
        } catch (Exception e) {
            changeUserProfileWallpaperPath.postValue(null);
        }
    }

    private void getProfileWallpaper(Realm realm) {
        try {
            RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).equalTo("type", ProtoInfoWallpaper.InfoWallpaper.Type.PROFILE_WALLPAPER_VALUE).findFirst();

            if (realmWallpaper != null) {

                if (realmWallpaper.getWallPaperList() != null && realmWallpaper.getWallPaperList().size() > 0) {
                    RealmAttachment pf = realmWallpaper.getWallPaperList().get(realmWallpaper.getWallPaperList().size() - 1).getFile();
                    String bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                    if (!new File(bigImagePath).exists()) {
                        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, bigImagePath, 2, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String mPath, final int progress) {
                                if (progress == 100) {
                                    changeUserProfileWallpaperPath.postValue(bigImagePath);
                                }
                            }

                            @Override
                            public void OnError(String token) {
                            }
                        });
                    } else {
                        changeUserProfileWallpaperPath.postValue(bigImagePath);
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
        setUserAvatar.setValue(AccountManager.getInstance().getCurrentUser().getId());
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }

    public void uploadAvatar(String path) {
        pathSaveImage = path;
        long lastUploadedAvatarId = idAvatar + 1L;
        showLoading.set(View.VISIBLE);
        Uploader.getInstance().upload(UploadObject.createForAvatar(lastUploadedAvatarId, pathSaveImage, null, ProtoGlobal.RoomMessageType.IMAGE, new OnUploadListener() {
            @Override
            public void onProgress(String id, int progress) {
            }

            @Override
            public void onFinish(String id, String token) {
                new RequestUserAvatarAdd().userAddAvatar(token);
            }

            @Override
            public void onError(String id) {
                G.handler.post(() -> showLoading.set(View.GONE));
            }

        }));
    }

    public void onCountryCodeClick() {
        if (showReferralErrorLiveData.get()) {
            showDialogSelectCountry.setValue(true);
        }
    }

    public void setReferral(String phoneNumber) {
        new RequestUserProfileSetRepresentative().userProfileSetRepresentative(phoneNumber, new OnUserProfileSetRepresentative() {
            @Override
            public void onSetRepresentative(String phone) {
                referralEnableLiveData.postValue(false);
                referralNumberObservableField.set("");
                G.handler.post(() -> {
                    if (!hasError) {
                        showEditIcon();
                        isEditProfile = false;
                    }
                });
            }

            @Override
            public void onErrorSetRepresentative(int majorCode, int minorCode) {
                showReferralErrorLiveData.set(true);
                hasError = true;
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
                referralNumberObservableField.set("");
            }
        });
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
    }

    private void getReferral() {
        Log.wtf(this.getClass().getName(), "getReferral");
        showDialogSelectCountry.postValue(false);
        showReferralErrorLiveData.set(false);
        new RequestUserProfileGetRepresentative().userProfileGetRepresentative(new RequestUserProfileGetRepresentative.OnRepresentReady() {
            @Override
            public void onRepresent(String phoneNumber) {
                if (phoneNumber.startsWith("98")) {
                    referralMaskObservableField.set("##-###-###-####");
                }
                referralNumberObservableField.set(phoneNumber);
                referralError.set(R.string.empty_error_message);
                if (phoneNumber.equals("")) {
                    referralEnableLiveData.postValue(true);
                    countryReader();
                } else {
                    referralCountryCodeObservableField.set("");
                    referralEnableLiveData.postValue(false);
                }
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

    @Override
    public void receivedEvent(int id, int account, Object... args) {

        if (id == EventManager.ON_ACCESS_TOKEN_RECIVE) {
            int response = (int) args[0];
            switch (response) {
                case SocketMessages.SUCCESS:
                    new Handler(getMainLooper()).post(() -> {
                        getUserCredit();
                        retryConnectToWallet = 0;
                    });

                    break;

                case SocketMessages.FAILED:
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
        private final String imagePath;
        private final String initials;
        private final String color;

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
        private final long userId;
        private final long avatarId;

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
        private final long roomId;
        private final long peerId;

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

    public RealmUserInfo getUserInfo() {
        return userInfo;
    }

    public ObservableInt getCheckProfileShow() {
        return checkProfileShow;
    }

    public ObservableInt getCancelProfileShow() {
        return cancelProfileShow;
    }

    public MutableLiveData<Boolean> getCancelIconClick() {
        return cancelIconClick;
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

    public ObservableInt getShowAddAvatarButton() {
        return showAddAvatarButton;
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

    public SingleLiveEvent<Boolean> getPopBackStack() {
        return popBackStack;
    }

    public ObservableBoolean getShowReferralErrorLiveData() {
        return showReferralErrorLiveData;
    }

    public ObservableInt getReferralError() {
        return referralError;
    }

    public SingleLiveEvent<Boolean> getGoToAddMemberPage() {
        return goToAddMemberPage;
    }

    public SingleLiveEvent<String> getGoToWalletAgreementPage() {
        return goToWalletAgreementPage;
    }

    public SingleLiveEvent<String> getGoToWalletPage() {
        return goToWalletPage;
    }

    public SingleLiveEvent<Boolean> getUpdateNewTheme() {
        return updateNewTheme;
    }

    public SingleLiveEvent<Boolean> getUpdateTwoPaneView() {
        return updateTwoPaneView;
    }

    public MutableLiveData<Boolean> getCloseKeyboard() {
        return closeKeyboard;
    }
}
