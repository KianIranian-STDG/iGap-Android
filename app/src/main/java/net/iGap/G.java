/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the kianiranian  Company - http://www.kianiranian.com/
 * All rights reserved.
 */

package net.iGap;

import android.accounts.Account;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.multidex.MultiDex;

import com.caspian.otpsdk.context.ApplicationContext;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yariksoffice.lingver.Lingver;

import net.iGap.activities.ActivityCustomError;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.activities.ActivityMain;
import net.iGap.api.webservice.JobServiceReconnect;
import net.iGap.fragments.emoji.OnStickerDownload;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.LooperThreadHelper;
import net.iGap.model.PassCode;
import net.iGap.module.AndroidUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.ChatUpdateStatusUtil;
import net.iGap.module.ClearMessagesUtil;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.StartupActions;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ConnectionState;
import net.iGap.observers.interfaces.*;
import net.iGap.proto.ProtoClientCondition;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestWrapper;

import org.paygear.RaadApp;
import org.paygear.model.Card;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.spec.SecretKeySpec;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import ir.metrix.sdk.Metrix;
import ir.radsense.raadcore.web.WebBase;

import static net.iGap.Config.DEFAULT_BOTH_CHAT_DELETE_TIME;

public class G extends ApplicationContext {

    public static final String IGAP = "/iGap";
    public static final String IMAGES = "/iGap Images";
    public static final String VIDEOS = "/iGap Videos";
    public static final String AUDIOS = "/iGap Audios";
    public static final String MESSAGES = "/iGap Messages";
    //public static Realm mRealm;
    public static final String DOCUMENT = "/iGap Document";
    public static final String TEMP = "/.temp";
    public static final String CHAT_BACKGROUND = "/.chat_background";
    public static final String IMAGE_USER = "/.image_user";
    public static final String STICKER = "/.sticker";
    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static boolean ISRealmOK = true;
    public static Context context;
    public static Handler handler;
    public static boolean isCalling = false;
    @Deprecated
    public static boolean isVideoCallRinging = false;
    //public static OnRejectCallStatus onRejectCallStatus;
    public static long mLastClickTime = SystemClock.elapsedRealtime();
    public static ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    public static List<Long> smsNumbers = new ArrayList<>();
    public static AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    public static SecretKeySpec symmetricKey;
    public static ProtoClientCondition.ClientCondition.Builder clientConditionGlobal;
    public static HelperCheckInternetConnection.ConnectivityType latestConnectivityType;
    public static ImageLoader imageLoader;
    public static ArrayList<String> unSecure = new ArrayList<>();
    public static ArrayList<String> unSecureResponseActionId = new ArrayList<>();
    public static ArrayList<String> unLogin = new ArrayList<>();// list of actionId that can be doing without secure
    public static ArrayList<String> waitingActionIds = new ArrayList<>();
    public static ArrayList<String> generalImmovableClasses = new ArrayList<>();
    public static ArrayList<Integer> forcePriorityActionId = new ArrayList<>();
    public static ArrayList<Integer> ignoreErrorCodes = new ArrayList<>();
    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static HashMap<String, ArrayList<Object>> requestQueueRelationMap = new HashMap<>();
    public static HashMap<Integer, Integer> priorityActionId = new HashMap<>();
    public static ActivityEnhanced currentActivity;
    public static FragmentActivity fragmentActivity;
    public static String latestActivityName;
    public static File IMAGE_NEW_GROUP;
    public static File IMAGE_NEW_CHANEL;
    public static File imageFile;
    public static String DIR_SDCARD_EXTERNAL = "";
    public static String DIR_APP = DIR_SDCARD + IGAP;
    public static String DIR_IMAGES = DIR_APP + IMAGES;
    public static String DIR_VIDEOS = DIR_APP + VIDEOS;
    public static String DIR_AUDIOS = DIR_APP + AUDIOS;
    public static String DIR_DOCUMENT = DIR_APP + DOCUMENT;
    public static String DIR_MESSAGES = DIR_APP + MESSAGES;
    public static String DIR_TEMP = DIR_APP + TEMP;
    public static String DIR_CHAT_BACKGROUND = DIR_APP + CHAT_BACKGROUND;
    public static String DIR_IMAGE_USER = DIR_APP + IMAGE_USER;
    public static String DIR_STICKER = DIR_APP + STICKER;
    public static String CHAT_MESSAGE_TIME = "H:mm";
    public static String selectedLanguage = null;
    public static String symmetricMethod;
    public static Ipromote ipromote;
    //    public static String menuBackgroundColor;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;
    public static boolean isUserStatusOnline = false;
    public static boolean isSecure = false;
    public static boolean userLogin = false;
    public static boolean canRunReceiver = false;
    public static boolean firstTimeEnterToApp = true; // use this field for get room list
    public static boolean firstEnter = true;
    public static boolean isSaveToGallery = false;
    public static boolean hasNetworkBefore;
    public static boolean isSendContact = false;
    public static boolean latestMobileDataState;
    public static boolean showVoteChannelLayout = true;
    public static boolean showSenderNameInGroup = false;
    @Deprecated
    public static boolean isInCall = false;
    public static boolean isShowRatingDialog = false;
    public static boolean isUpdateNotificaionColorMain = false;
    public static boolean isUpdateNotificaionColorChannel = false;
    public static boolean isUpdateNotificaionColorGroup = false;
    public static boolean isUpdateNotificaionColorChat = false;
    public static boolean isUpdateNotificaionCall = false;
    public static boolean isCalculatKeepMedia = true;
    public static boolean twoPaneMode = false;
    public static boolean isLandscape = false;
    public static boolean isAppRtl = false;
    public static boolean isLinkClicked = false;
    public static boolean isWalletRegister = false;
    public static int themeColor;
    public static String selectedTabInMainActivity = "";
    public static int ivSize;
    public static int userTextSize = 0;
    public static int COPY_BUFFER_SIZE = 1024;
    public static int maxChatBox = 0;
    public static int bothChatDeleteTime = DEFAULT_BOTH_CHAT_DELETE_TIME;
    public static long currentTime;
    public static String serverHashContact = null;
    public static String localHashContact = null;
    public static long latestHearBeatTime = System.currentTimeMillis();
    public static long currentServerTime;
    public static long latestResponse = System.currentTimeMillis();
    public static long serverHeartBeatTiming = 60 * 1000;
    public static ClearMessagesUtil clearMessagesUtil = new ClearMessagesUtil();
    public static ChatSendMessageUtil chatSendMessageUtil = new ChatSendMessageUtil();
    public static ChatUpdateStatusUtil chatUpdateStatusUtil = new ChatUpdateStatusUtil();
    public static ConnectionState connectionState;
    public static ConnectionState latestConnectionState;
    public static OnConnectionChangeState onConnectionChangeState;
    public static OnConnectionChangeStateChat onConnectionChangeStateChat;
    public static OnUpdating onUpdating;
    /*public static OnReceiveInfoLocation onReceiveInfoLocation;*/
    /*public static OnUserRegistration onUserRegistration;*/
    public static OnClientSearchRoomHistory onClientSearchRoomHistory;
    /*public static OnUserVerification onUserVerification;*/
    public static OnUserLogin onUserLogin;
    /*public static OnUserProfileSetEmailResponse onUserProfileSetEmailResponse;*/
    /*public static OnUserProfileSetGenderResponse onUserProfileSetGenderResponse;*/
    public static OnUserProfileSetNickNameResponse onUserProfileSetNickNameResponse;
    /*public static OnInfoCountryResponse onInfoCountryResponse;*/
    public static OnInfoTime onInfoTime;
    public static OnUserContactEdit onUserContactEdit;
    public static OnUserContactDelete onUserContactdelete;
    public static OnClientGetRoomListResponse onClientGetRoomListResponse;
    public static OnClientGetRoomResponse onClientGetRoomResponse;
    public static OnInquiry onInquiry;
    public static OnSecuring onSecuring;
    public static OnChatGetRoom onChatGetRoom;
    public static OnChatEditMessageResponse onChatEditMessageResponse;
    public static OnChatDeleteMessageResponse onChatDeleteMessageResponse;
    public static OnChatDelete onChatDelete;
    public static OnChatSendMessage onChatSendMessage;
    //    public static OnUserUsernameToId onUserUsernameToId;
    public static OnUserProfileGetNickname onUserProfileGetNickname;
    public static OnGroupCreate onGroupCreate;
    public static OnGroupAddMember onGroupAddMember;
    //    public static OnGroupClearMessage onGroupClearMessage;
    /*public static OnGroupEdit onGroupEdit;*/
//    public static OnGroupKickMember onGroupKickMember;
    public static OnGroupLeft onGroupLeft;
    public static OnFileDownloadResponse onFileDownloadResponse;
    public static OnUserInfoResponse onUserInfoResponse;
    public static OnUserAvatarResponse onUserAvatarResponse;
    public static OnGroupAvatarResponse onGroupAvatarResponse;
    public static OnClearChatHistory onClearChatHistory;
    public static OnDeleteChatFinishActivity onDeleteChatFinishActivity;
    public static OnClientGetRoomHistoryResponse onClientGetRoomHistoryResponse;
    public static OnUserAvatarDelete onUserAvatarDelete;
    /*public static OnUserProfileCheckUsername onUserProfileCheckUsername;*/
    /*public static OnUserProfileUpdateUsername onUserProfileUpdateUsername;*/
    public static OnGroupGetMemberList onGroupGetMemberList;
    public static OnGroupDelete onGroupDelete;
    public static OpenFragment onConvertToGroup;
    public static OnChatConvertToGroup onChatConvertToGroup;
    public static OnUserUpdateStatus onUserUpdateStatus;
    public static OnUpdateUserStatusInChangePage onUpdateUserStatusInChangePage;
    public static OnLastSeenUpdateTiming onLastSeenUpdateTiming;
    public static OnSetAction onSetAction;
    public static OnBotClick onBotClick;
    public static OnSetActionInRoom onSetActionInRoom;
    public static OnUserSessionGetActiveList onUserSessionGetActiveList;
    public static OnUserSessionTerminate onUserSessionTerminate;
    /*public static OnUserSessionLogout onUserSessionLogout;*/
    //    public static UpdateListAfterKick updateListAfterKick;
    public static OnHelperSetAction onHelperSetAction;
    public static OnChannelCreate onChannelCreate;
    public static OnChannelDelete onChannelDelete;
    public static OnChannelLeft onChannelLeft;
    public static OnChannelAddMember onChannelAddMember;
    public static OnChannelGetMemberList onChannelGetMemberList;
    /*public static OnChannelEdit onChannelEdit;*/
    public static OnChannelAvatarAdd onChannelAvatarAdd;
    public static OnChannelAvatarDelete onChannelAvatarDelete;
    public static OnChannelCheckUsername onChannelCheckUsername;
    /*public static OnGroupCheckUsername onGroupCheckUsername;*/
    /*public static OnGroupUpdateUsername onGroupUpdateUsername;*/
    public static OnChannelUpdateUsername onChannelUpdateUsername;
    public static OnGroupAvatarDelete onGroupAvatarDelete;
    /*public static OnRefreshActivity onRefreshActivity;*/
    //    public static OnGetUserInfo onGetUserInfo;
    public static OnFileDownloaded onFileDownloaded;
    public static OnStickerDownloaded onStickerDownloaded;
    public static OnStickerDownload onStickerDownload;
    public static OnUserInfoMyClient onUserInfoMyClient;
    public static OnChannelAddMessageReaction onChannelAddMessageReaction;
    public static OnChannelGetMessagesStats onChannelGetMessagesStats;
    public static OnChannelRemoveUsername onChannelRemoveUsername;
    public static OnChannelRevokeLink onChannelRevokeLink;
    public static OnChannelUpdateSignature onChannelUpdateSignature;
    public static OnChannelUpdateReactionStatus onChannelUpdateReactionStatus;
    public static OnChannelUpdateReactionStatus onChannelUpdateReactionStatusChat;
    public static OnClientCheckInviteLink onClientCheckInviteLink;
    public static OnClientJoinByInviteLink onClientJoinByInviteLink;
    public static OnClientResolveUsername onClientResolveUsername;
    public static OnClientSubscribeToRoom onClientSubscribeToRoom;
    public static OnClientUnsubscribeFromRoom onClientUnsubscribeFromRoom;
    /*public static OnGroupRemoveUsername onGroupRemoveUsername;*/
    /*public static OnGroupRevokeLink onGroupRevokeLink;*/
    public static OnUserContactsBlock onUserContactsBlock;
    public static OnUserContactsUnBlock onUserContactsUnBlock;
    public static OnClientCondition onClientCondition;
    public static OnGetWallpaper onGetWallpaper;
    public static OnGetWallpaper onGetProfileWallpaper;
    /*public static OnTwoStepPassword onTwoStepPassword;*/
    /*public static TwoStepSecurityConfirmEmail twoStepSecurityConfirmEmail;*/
    /*public static OnSecurityCheckPassword onSecurityCheckPassword;*/
    public static OnRecoverySecurityPassword onRecoverySecurityPassword;
    public static OnRecoveryEmailToken onRecoveryEmailToken;
    /*public static OnQrCodeNewDevice onQrCodeNewDevice;*/
    public static OnVerifyNewDevice onVerifyNewDevice;
    public static OnPushLoginToken onPushLoginToken;
    public static OnPushTwoStepVerification onPushTwoStepVerification;
    public static IClientSearchUserName onClientSearchUserName;
    @Deprecated
    public static OnCallLeaveView onCallLeaveView;
    @Deprecated
    public static OnVideoCallFrame onVideoCallFrame;
    @Deprecated
    public static ICallFinish iCallFinishChat;
    @Deprecated
    public static ICallFinish iCallFinishMain;
    //    public static IMainFinish iMainFinish;
//    public static IActivityFinish iActivityFinish;
    public static OnBlockStateChanged onBlockStateChanged;
    public static OnContactsGetList onContactsGetList;
    //public static OnCallLogClear onCallLogClear;
    public static OnMapUsersGet onMapUsersGet;
    public static OnPinedMessage onPinedMessage;
    //    public static OnSelectMenu onSelectMenu;
//    public static OnRemoveFragment onRemoveFragment;
    public static OnChatDeleteInRoomList onChatDeleteInRoomList;
    public static OnGroupDeleteInRoomList onGroupDeleteInRoomList;
    public static OnChannelDeleteInRoomList onChannelDeleteInRoomList;
    public static OnClientGetRoomResponseRoomList onClientGetRoomResponseRoomList;
    public static OnReport onReport;
    public static OnPhoneContact onPhoneContact;
    public static OnContactFetchForServer onContactFetchForServer;
    public static OnQueueSendContact onQueueSendContact;
    public static OnAudioFocusChangeListener onAudioFocusChangeListener;
    public static IDispatchTochEvent dispatchTochEventChat;
    public static IOnBackPressed onBackPressedChat;
    public static IOnBackPressed onBackPressedWebView;
    public static ISendPosition iSendPositionChat;
    public static ITowPanModDesinLayout iTowPanModDesinLayout;
    public static OnDateChanged onDateChanged;
    public static OnLocationChanged onLocationChanged;
    public static OnGetNearbyCoordinate onGetNearbyCoordinate;
    public static OnGeoGetComment onGeoGetComment;
    public static OnMapRegisterState onMapRegisterState;
    public static OnMapRegisterStateMain onMapRegisterStateMain;
    public static OpenBottomSheetItem openBottomSheetItem;
    public static OnUnreadChange onUnreadChange;
    public static OnMapClose onMapClose;
    public static OnRegistrationInfo onRegistrationInfo;
    public static OnGeoCommentResponse onGeoCommentResponse;
    public static OnGeoGetConfiguration onGeoGetConfiguration;
    public static OnNotifyTime onNotifyTime;
    public static OnPayment onPayment;
    public static OnMplResult onMplResult;
    public static OnVersionCallBack onVersionCallBack;
    public static OnContactImport onContactImport;
    public static OnMplTransaction onMplTransaction;
    public static OnMplTransactionInfo onMplTransactionInfo;
    /*public static OnUserProfileSetRepresentative onUserProfileSetRepresentative;*/
    @Deprecated
    public static ISignalingOffer iSignalingOffer;
    @Deprecated
    public static ISignalingRinging iSignalingRinging;
    @Deprecated
    public static ISignalingAccept iSignalingAccept;
    @Deprecated
    public static ISignalingCandidate iSignalingCandidate;
    @Deprecated
    public static ISignalingLeave iSignalingLeave;
    @Deprecated
    public static ISignalingSessionHold iSignalingSessionHold;
    public static ISignalingGetCallLog iSignalingGetCallLog;
    @Deprecated
    public static ISignalingCallBack iSignalingCallBack;
    @Deprecated
    public static ISignalingErrore iSignalingErrore;
    public static OneFragmentIsOpen oneFragmentIsOpen;
    public static boolean isFragmentMapActive = false; // for check network
    public static boolean isRestartActivity = false; // for check passCode
    public static boolean isFirstPassCode = true; // for check passCodeG.backgroundTheme
    public static boolean isTimeWhole = false;
    public static Account iGapAccount;
    public static Card selectedCard = null;
    public static long cardamount;
    public static String jwt = null;
    /*public static boolean isBluetoothConnected = false;
    public static boolean isHandsFreeConnected = false;*/
    /*public static SpeakerControlListener speakerControlListener;*/
    @Deprecated
    public static VideoCallListener videoCallListener;
    public static RefreshWalletBalance refreshWalletBalance;
    @Deprecated
    public static OnHoldBackgroundChanegeListener onHoldBackgroundChanegeListener;
    @Deprecated
    public static boolean isWebRtcConnected = false;
    public static boolean isDepricatedApp = false;
    public static int rotationState;
    public static int mainRingerMode = 0;
    public static boolean appChangeRinggerMode = false;
    public static LocationListener locationListener;
    public static boolean isLocationFromBot = false;
    public static boolean isNeedToCheckProfileWallpaper = false;
    public static String nationalCode;

    public static MutableLiveData<ConnectionState> connectionStateMutableLiveData = new MutableLiveData<>();
    public static SingleLiveEvent<Boolean> logoutAccount = new SingleLiveEvent<>();

    public static String downloadDirectoryPath;

    private static int makeColorTransparent100(String color) {
        if (color.length() == 9) {
            return Color.parseColor("#FF" + color.substring(3));
        } else {
            return Color.parseColor(color);
        }
    }

    public static void refreshRealmUi() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            refreshUiRealm();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    refreshUiRealm();
                }
            });
        }
    }

    private static void refreshUiRealm() {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.refresh();
        });
    }

    public static Context updateResources(Context baseContext) {
        if (G.selectedLanguage == null) {
            G.selectedLanguage = "fa";
        }

        Locale locale = new Locale(G.selectedLanguage);
        Locale.setDefault(locale);

        Resources res = baseContext.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            baseContext = baseContext.createConfigurationContext(configuration);
        } else {
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        G.context = baseContext;

        return baseContext;
    }

    public static String getNationalCode() {

        if (G.nationalCode == null)
            return DbManager.getInstance().doRealmTask(realm -> {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null)
                    G.nationalCode = realmUserInfo.getNationalCode();
                return G.nationalCode;
            });
        else
            return G.nationalCode;
    }

    @Override
    public void onCreate() {
        Log.wtf(this.getClass().getName(), "onCreate");
        super.onCreate();
        context = getApplicationContext();
        Fabric.with(getApplicationContext(), new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        CaocConfig.Builder.create().backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT).showErrorDetails(false).showRestartButton(true).trackActivities(true).restartActivity(ActivityMain.class).errorActivity(ActivityCustomError.class).apply();

        JobServiceReconnect.scheduleJob(getApplicationContext());
        PassCode.initPassCode(getApplicationContext());

        AndroidUtils.density = getApplicationContext().getResources().getDisplayMetrics().density;

        //init account manager for handle multi account

        try {
            Realm.init(this);
        } catch (Exception e) {
            G.ISRealmOK = false;
        } catch (Error e) {
            G.ISRealmOK = false;
        }
        AccountManager.initial(this);

        LooperThreadHelper.getInstance();
        Metrix.initialize(this, "jpbnabzrmeqvxme");
        Lingver.init(this, G.selectedLanguage == null ? Locale.getDefault() : new Locale(G.selectedLanguage));

        // dont remove below line please
        if (!BuildConfig.DEBUG && BuildConfig.Store.length() > 1) {
            Metrix.getInstance().setStore(BuildConfig.Store);
            Metrix.getInstance().setAppSecret(1, 1728320174, 43612053, 1626881868, 580653578);
        }

        handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                RaadApp.onCreate(getApplicationContext());
                /*Raad.init(getApplicationContext());*/
                WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
            }
        }).start();

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mainRingerMode = am.getRingerMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new StartupActions();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
     /*   try {
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
            WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);
        } catch (Exception e) {
        }*/

        downloadDirectoryPath = context.getFilesDir().getAbsolutePath() + "/stickers";

        if (!new File(downloadDirectoryPath).exists())
            new File(downloadDirectoryPath).mkdirs();

        Log.wtf(this.getClass().getName(), "onCreate");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateResources(base));
        MultiDex.install(this);
        /*new MultiDexUtils().getLoadedExternalDexClasses(this);*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResources(this);
        try {
            AndroidUtils.checkDisplaySize(G.context, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(String message) {
        G.handler.post(() -> Toast.makeText(G.context, message, Toast.LENGTH_SHORT).show());
    }

    public static void runOnUiThread(Runnable runnable, long delay) {
        if (handler != null)
            handler.postDelayed(runnable, delay);
    }

    public static void runOnUiThread(Runnable runnable) {
        if (handler != null)
            handler.post(runnable);
    }

    public static void cancelRunOnUiThread(Runnable runnable) {
        if (handler != null)
            handler.removeCallbacks(runnable);
    }
}
