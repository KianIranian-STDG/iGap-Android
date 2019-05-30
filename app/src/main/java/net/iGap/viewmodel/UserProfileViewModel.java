package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableLong;
import android.os.Handler;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.Theme;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.eventbus.socketMessages;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.FragmentUserScore;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.RefreshWalletBalance;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestWalletGetAccessToken;

import org.jetbrains.annotations.NotNull;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.web.Web;

import java.util.ArrayList;

import io.realm.Realm;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Looper.getMainLooper;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;

public class UserProfileViewModel extends ViewModel implements RefreshWalletBalance, OnUserInfoMyClient, EventListener {

    private ObservableField<String> appVersion = new ObservableField<>("");
    private ObservableField<String> username = new ObservableField<>();
    private ObservableField<String> userPhoneNumber = new ObservableField<>();
    private ObservableField<String> userBio = new ObservableField<>();
    private ObservableLong currentCredit = new ObservableLong(0);
    private ObservableField<String> currentScore = new ObservableField<>("0");
    private ObservableBoolean isDarkMode = new ObservableBoolean(false);
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
    public MutableLiveData<ChangeImageModel> setUserAvatarPath = new MutableLiveData<>();
    public MutableLiveData<Long> goToChatPage = new MutableLiveData<>();

    private Realm mRealm;
    private RealmUserInfo userInfo;
    private String phoneNumber;
    private int retryConnectToWallet = 0;
    private SharedPreferences sharedPreferences;

    public UserProfileViewModel(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        appVersion.set(BuildConfig.VERSION_NAME);
        isDarkMode.set(G.isDarkTheme);

        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        if (userInfo != null) {
            username.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(userInfo.getUserInfo().getDisplayName()) : userInfo.getUserInfo().getDisplayName());
            phoneNumber = userInfo.getUserInfo().getPhoneNumber();
            userPhoneNumber.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(phoneNumber) : phoneNumber);
            userBio.set(userInfo.getUserInfo().getBio());

            setUserAvatar.setValue(G.userId);
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
        }

        //set credit amount
        if (G.selectedCard != null) {
            currentCredit.set(G.selectedCard.cashOutBalance);
        } else {
            currentCredit.set(0);
        }
    }

    public ObservableField<String> getAppVersion() {
        return appVersion;
    }

    public ObservableField<String> getUsername() {
        return username;
    }

    public ObservableField<String> getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public ObservableField<String> getUserBio() {
        return userBio;
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
        Log.wtf("view Model","value of is check: "+isCheck);
        if (isCheck != isDarkMode.get()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.wtf("view Model","value of is check: "+isCheck);
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

    public void getUserCredit() {
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
}
