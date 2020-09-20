/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.top.lib.mpl.view.PaymentInitiator;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.CallSelectFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentChatSettings;
import net.iGap.fragments.FragmentGallery;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.fragments.FragmentNewGroup;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.PaymentFragment;
import net.iGap.fragments.TabletEmptyChatFragment;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.fragments.kuknos.KuknosSendFrag;
import net.iGap.helper.CardToCardHelper;
import net.iGap.helper.DirectPayHelper;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCalculateKeepMedia;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.PermissionHelper;
import net.iGap.helper.ServiceContact;
import net.iGap.model.PassCode;
import net.iGap.model.payment.Payment;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.ContactUtils;
import net.iGap.module.FileUtils;
import net.iGap.module.LoginActions;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyPhonStateService;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountHelper;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.SubmitScoreDialog;
import net.iGap.module.enums.ConnectionState;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.eventbus.socketMessages;
import net.iGap.observers.interfaces.DataTransformerListener;
import net.iGap.observers.interfaces.FinishActivity;
import net.iGap.observers.interfaces.ITowPanModDesinLayout;
import net.iGap.observers.interfaces.OnChatClearMessageResponse;
import net.iGap.observers.interfaces.OnChatSendMessageResponse;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnGroupAvatarResponse;
import net.iGap.observers.interfaces.OnMapRegisterState;
import net.iGap.observers.interfaces.OnMapRegisterStateMain;
import net.iGap.observers.interfaces.OnPayment;
import net.iGap.observers.interfaces.OnUpdating;
import net.iGap.observers.interfaces.OnUserInfoMyClient;
import net.iGap.observers.interfaces.OnVerifyNewDevice;
import net.iGap.observers.interfaces.OpenFragment;
import net.iGap.observers.interfaces.RefreshWalletBalance;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestUserIVandSetActivity;
import net.iGap.request.RequestUserVerifyNewDevice;
import net.iGap.request.RequestWalletGetAccessToken;
import net.iGap.request.RequestWalletIdMapping;
import net.iGap.viewmodel.UserScoreViewModel;

import org.paygear.RaadApp;
import org.paygear.fragment.PaymentHistoryFragment;

import java.io.File;
import java.io.IOException;

import static net.iGap.G.context;
import static net.iGap.G.isSendContact;
import static net.iGap.fragments.BottomNavigationFragment.DEEP_LINK_CALL;
import static net.iGap.fragments.BottomNavigationFragment.DEEP_LINK_CHAT;

public class ActivityMain extends ActivityEnhanced implements OnUserInfoMyClient, OnPayment, OnChatClearMessageResponse, OnChatSendMessageResponse, OnGroupAvatarResponse, OnMapRegisterStateMain, EventListener, RefreshWalletBalance, ToolbarListener, ProviderInstaller.ProviderInstallListener {

    public static final String openChat = "openChat";
    public static final String userId = "userId";
    public static final String OPEN_DEEP_LINK = "openDeepLink";

    public static final String DEEP_LINK = "deepLink";

    public static final String openMediaPlyer = "openMediaPlyer";

    public static final int requestCodePaymentCharge = 198;
    public static final int requestCodePaymentBill = 199;
    public static final int requestCodeQrCode = 200;
    public static final int kuknosRequestCodeQrCode = 202;
    public static final int requestCodeBarcode = 201;
    public static final int electricityBillRequestCodeQrCode = 203;
    public static final int WALLET_REQUEST_CODE = 1024;
    private static final int ERROR_DIALOG_REQUEST_CODE = 1;

    private boolean retryProviderInstall;

    public static boolean isOpenChatBeforeSheare = false;
    public static boolean isLock = false;
    public static FinishActivity finishActivity;
    public static boolean disableSwipe = false;
    public static OnBackPressedListener onBackPressedListener;
    private static long oldTime;
    public static boolean isUseCamera = false;
    public static boolean isStoragePage = true;
    public static boolean waitingForConfiguration = false;
    private SharedPreferences sharedPreferences;
    private TextView iconLock;
    private int retryConnectToWallet = 0;
    private MyPhonStateService myPhonStateService;
    public DataTransformerListener<Intent> dataTransformer;
    private BroadcastReceiver audioManagerReciver;

    public static void setMediaLayout() {
        try {
            if (MusicPlayer.mp != null) {

                if (MusicPlayer.shearedMediaLayout != null) {
                    MusicPlayer.initLayoutTripMusic(MusicPlayer.shearedMediaLayout);

                    if (MusicPlayer.chatLayout != null) {
                        MusicPlayer.chatLayout.setVisibility(View.GONE);
                    }

                    if (MusicPlayer.mainLayout != null) {
                        MusicPlayer.mainLayout.setVisibility(View.GONE);
                    }
                } else if (MusicPlayer.chatLayout != null) {
                    MusicPlayer.initLayoutTripMusic(MusicPlayer.chatLayout);

                    if (MusicPlayer.mainLayout != null) {
                        MusicPlayer.mainLayout.setVisibility(View.GONE);
                    }
                } else if (MusicPlayer.mainLayout != null) {
                    MusicPlayer.initLayoutTripMusic(MusicPlayer.mainLayout);
                }
            } else {

                if (MusicPlayer.mainLayout != null) {
                    MusicPlayer.mainLayout.setVisibility(View.GONE);
                }

                if (MusicPlayer.chatLayout != null) {
                    MusicPlayer.chatLayout.setVisibility(View.GONE);
                }

                if (MusicPlayer.shearedMediaLayout != null) {
                    MusicPlayer.shearedMediaLayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    public static void doIvandScore(String content, Activity activity) {
        boolean isSend = new RequestUserIVandSetActivity().setActivity(content, new RequestUserIVandSetActivity.OnSetActivities() {
            @Override
            public void onSetActivitiesReady(String message, boolean isOk) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        SubmitScoreDialog dialog = new SubmitScoreDialog(activity, message, isOk);
                        dialog.show();
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String message = G.context.getString(R.string.error_submit_qr_code);
                        if (majorCode == 10183 && minorCode == 2) {
                            message = G.context.getString(R.string.E_10183);
                        } else if (majorCode == 10184 && minorCode == 1) {
                            message = G.context.getString(R.string.error_ivand_limit_gift);
                        }

                        SubmitScoreDialog dialog = new SubmitScoreDialog(activity, message, false);
                        dialog.show();
                    }
                });
            }
        });

        if (!isSend) {
            HelperError.showSnackMessage(G.context.getString(R.string.wallet_error_server), false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Config.FILE_LOG_ENABLE) {
            FileLog.i("Main activity on destroy");
        }

        if (G.ISRealmOK) {
            if (myPhonStateService != null) {
                unregisterReceiver(myPhonStateService);
            }

            if (audioManagerReciver != null) {
                unregisterReceiver(audioManagerReciver);
            }
            if (G.imageLoader != null) {
                G.imageLoader.clearMemoryCache();
            }
            if (G.refreshWalletBalance != null) {
                G.refreshWalletBalance = null;
            }
            RealmRoom.clearAllActions();
            if (G.onAudioFocusChangeListener != null) {
                G.onAudioFocusChangeListener.onAudioFocusChangeListener(AudioManager.AUDIOFOCUS_LOSS);
            }
            EventManager.getInstance().removeEventListener(EventManager.ON_ACCESS_TOKEN_RECIVE, this);
            try {
                AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);

                am.setRingerMode(G.mainRingerMode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * delete content of folder chat background in the first registration
     */
    private void deleteContentFolderChatBackground() {
        FileUtils.deleteRecursive(new File(G.DIR_CHAT_BACKGROUND));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        isOpenChatBeforeSheare = true;
        checkIntent(intent);

        if (intent.getExtras() != null && intent.getExtras().getString(DEEP_LINK) != null) {
            handleDeepLink(intent);
        }

        if (G.isFirstPassCode) {
            openActivityPassCode();
        }
    }

    public void handleDeepLink(Intent intent) {
        if (intent != null && intent.getExtras() != null)
            handleDeepLink(intent.getExtras().getString(DEEP_LINK, DEEP_LINK_CHAT));
    }

    public void handleDeepLink(String uri) {
        BottomNavigationFragment bottomNavigationFragment = (BottomNavigationFragment) getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
        if (bottomNavigationFragment != null)
            bottomNavigationFragment.autoLinkCrawler(uri, new DiscoveryFragment.CrawlerStruct.OnDeepValidLink() {
                @Override
                public void linkValid(String link) {

                }

                @Override
                public void linkInvalid(String link) {
                    HelperError.showSnackMessage(link + " " + context.getResources().getString(R.string.link_not_valid), false);
                }
            });
    }

    private void checkIntent(Intent intent) {

        if (G.isRestartActivity) {
            return;
        }

        if (intent.getAction() != null && intent.getAction().equals("net.iGap.payment")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.mainFrame);
            if (fragment instanceof PaymentFragment) {
                ((PaymentFragment) fragment).setPaymentResult(new Payment(
                        intent.getStringExtra("status"),
                        intent.getStringExtra("message"),
                        intent.getStringExtra("order_id"),
                        intent.getStringExtra("tax"),
                        intent.getStringExtra("discount")
                ));
            }
        }

        new HelperGetDataFromOtherApp(this, intent);
        //check has shared data if true setup main fragment (room list) ui
        Fragment fragmentBottomNav = getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
        if (fragmentBottomNav instanceof BottomNavigationFragment) {
            ((BottomNavigationFragment) fragmentBottomNav).checkHasSharedData(true);//set true just for checking state
            ((BottomNavigationFragment) fragmentBottomNav).isFirstTabItem();
        }

        if (intent.getAction() != null && intent.getAction().equals("net.iGap.activities.OPEN_ACCOUNT")) {
            new HelperFragment(getSupportFragmentManager(), new FragmentSetting()).load();
        }

        Bundle extras = intent.getExtras();

        if (extras != null) {

            long roomId = extras.getLong(ActivityMain.openChat);
            if (!FragmentLanguage.languageChanged && roomId > 0) { // if language changed not need check enter to chat
//                GoToChatActivity goToChatActivity = new GoToChatActivity(roomId);
                // TODO this change is duo to room null bug. if it works server must change routine.
                long peerId = extras.getLong("PeerID");
                long userId = extras.getLong(ActivityMain.userId);
                if (AccountManager.getInstance().getCurrentUser().getId() != userId) {
                    new AccountHelper().changeAccount(userId);
                    RaadApp.onCreate(this);
                    updateUiForChangeAccount();
                }
                HelperUrl.goToActivityFromFCM(this, roomId, peerId);
            }
            FragmentLanguage.languageChanged = false;

            boolean openMediaPlayer = extras.getBoolean(ActivityMain.openMediaPlyer);
            if (openMediaPlayer) {
                if (getSupportFragmentManager().findFragmentByTag(FragmentMediaPlayer.class.getName()) == null) {
                    FragmentMediaPlayer fragment = new FragmentMediaPlayer();
                    new HelperFragment(getSupportFragmentManager(), fragment).setReplace(false).load();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Config.FILE_LOG_ENABLE) {
            FileLog.i("Main activity on create");
        }

        setContentView(R.layout.activity_main);

        detectDeviceType();
        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        G.logoutAccount.observe(this, isLogout -> {
            if (isLogout != null && isLogout) {
                boolean haveOtherAccount = new AccountHelper().logoutAccount();
                RaadApp.onCreate(this);
                if (haveOtherAccount) {
                    updateUiForChangeAccount();
                } else {
                    try {
                        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancelAll();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                    if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {
                        MusicPlayer.stopSound();
                        MusicPlayer.closeLayoutMediaPlayer();
                    }
                    startActivity(new Intent(this, ActivityRegistration.class));
                    finish();
                }
            }
        });
        if (G.ISRealmOK) {
            finishActivity = new FinishActivity() {
                @Override
                public void finishActivity() {
                    // ActivityChat.this.finish();
                    finish();
                }
            };

            if (G.isFirstPassCode) {
                openActivityPassCode();
            }

            initTabStrip(getIntent());
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PHONE_STATE");
            myPhonStateService = new MyPhonStateService();

            //add it for handle ssl handshake error
            checkGoogleUpdate();

            registerReceiver(myPhonStateService, intentFilter);
            G.refreshWalletBalance = this;

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //code...
                }
            };
            IntentFilter ringgerFilter = new IntentFilter(
                    AudioManager.RINGER_MODE_CHANGED_ACTION);


            audioManagerReciver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //code...
                    if (!G.appChangeRinggerMode) {
                        AudioManager mainAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        G.mainRingerMode = mainAudioManager.getRingerMode();
                    }

                }
            };

            registerReceiver(audioManagerReciver, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (Build.BRAND.equalsIgnoreCase("xiaomi") || Build.BRAND.equalsIgnoreCase("Honor") || Build.BRAND.equalsIgnoreCase("oppo") || Build.BRAND.equalsIgnoreCase("asus"))
                    isChinesPhone();
            }

            RaadApp.paygearHistoryOpenChat = new PaymentHistoryFragment.PaygearHistoryOpenChat() {
                @Override
                public void paygearId(String id) {

                    new RequestWalletIdMapping().walletIdMapping(id);
                }
            };

            EventManager.getInstance().addEventListener(EventManager.ON_ACCESS_TOKEN_RECIVE, this);

//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            boolean deleteFolderBackground = sharedPreferences.getBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, true);
            if (deleteFolderBackground) {
                deleteContentFolderChatBackground();
                sharedPreferences.edit().putBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, false).apply();
            }

            if (G.twoPaneMode) {
                G.isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

                designLayout(chatLayoutMode.none);
                setDialogFragmentSize();

                G.iTowPanModDesinLayout = new ITowPanModDesinLayout() {
                    @Override
                    public void onLayout(chatLayoutMode mode) {
                        designLayout(mode);
                    }

                    @Override
                    public boolean getBackChatVisibility() {
                        return G.twoPaneMode && findViewById(R.id.fullScreenFrame).getVisibility() == View.VISIBLE;
                    }

                    @Override
                    public void setBackChatVisibility(boolean visibility) {
                        if (G.twoPaneMode) {
                            findViewById(R.id.fullScreenFrame).setVisibility(View.VISIBLE);
                        }
                    }
                };
            }

            isOpenChatBeforeSheare = false;
            checkIntent(getIntent());

            initComponent();

            G.onPayment = this;

            sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            boolean isGetContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT, false);
            /**
             * just do this action once
             */
            new PermissionHelper(this).grantReadPhoneStatePermission();

            if (!isGetContactList) {
                try {
                    HelperPermission.getContactPermision(ActivityMain.this, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException {
                            if (!G.isSendContact) {
                                G.isSendContact = true;
                                LoginActions.importContact();
                            }
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT, true);
                            editor.apply();
                        }

                        @Override
                        public void deny() {

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT, true);
                            editor.apply();

                            /**
                             * user not allowed to import contact, so client set
                             * isSendContact = true for avoid from try again
                             */
                            isSendContact = true;
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            HelperNotification.getInstance().cancelNotification();
            G.onGroupAvatarResponse = this;

            G.onConvertToGroup = new OpenFragment() {
                @Override
                public void openFragmentOnActivity(String type, final Long roomId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FragmentNewGroup fragmentNewGroup = new FragmentNewGroup();
                            Bundle bundle = new Bundle();
                            bundle.putString("TYPE", "ConvertToGroup");
                            bundle.putLong("ROOMID", roomId);
                            fragmentNewGroup.setArguments(bundle);

                            try {
                                new HelperFragment(getSupportFragmentManager(), fragmentNewGroup).setStateLoss(true).load();
                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        }
                    });
                }
            };

            G.clearMessagesUtil.setOnChatClearMessageResponse(this);
            connectionState();
            new Thread(this::checkKeepMedia).start();

            G.onVerifyNewDevice = new OnVerifyNewDevice() {
                @Override
                public void verifyNewDevice(String appName, int appId, int appBuildVersion, String appVersion, ProtoGlobal.Platform platform, String platformVersion, ProtoGlobal.Device device, String deviceName, boolean twoStepVerification) {

                    final String content = "" + "App name: " + appName + "\n" + "Build version: " + appBuildVersion + "\n" + "App version: " + appVersion + "\n" + "Platform: " + platform + "\n" + "Platform version: " + platformVersion + "\n" + "Device: " + device + "\n" + "Device name: " + deviceName;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (HelperCalander.isPersianUnicode) {
                                new MaterialDialog.Builder(ActivityMain.this).title(R.string.Input_device_specification).contentGravity(GravityEnum.END).content(content).positiveText(R.string.B_ok).show();
                            } else {
                                new MaterialDialog.Builder(ActivityMain.this).title(R.string.Input_device_specification).contentGravity(GravityEnum.START).content(content).positiveText(R.string.B_ok).show();
                            }
                        }
                    });
                }

                @Override
                public void errorVerifyNewDevice(final int majorCode, final int minCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            };

            boolean isDefaultBg = sharedPreferences.getBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, true);
            if (isDefaultBg) {
                sharedPreferences.edit().putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "").apply();
            }

        } else {
            TextView textView = new TextView(this);
            setContentView(textView);
            showToast(textView);
        }


        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtils.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.wtf(this.getClass().getName(), "------------------------------------------------");
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    Log.wtf(this.getClass().getName(), "fragment: " + getSupportFragmentManager().getBackStackEntryAt(i).getName());
                }
                Log.wtf(this.getClass().getName(), "------------------------------------------------");
            }
        });
        Log.wtf(this.getClass().getName(), "onCreate");
    }

    /**
     * if device is tablet twoPaneMode will be enabled
     */
    private void detectDeviceType() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);


        G.twoPaneMode = findViewById(R.id.roomListFrame) != null;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && G.twoPaneMode) {
            G.maxChatBox = metrics.widthPixels - (metrics.widthPixels / 3) - ViewMaker.i_Dp(R.dimen.dp80);
        } else {
            G.maxChatBox = metrics.widthPixels - ViewMaker.i_Dp(R.dimen.dp80);
        }

    }

    private void setDialogFragmentSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int size = Math.min(width, height) - 50;

        FrameLayout frameFragmentContainer = findViewById(R.id.detailFrame);
        ViewGroup.LayoutParams lp = frameFragmentContainer.getLayoutParams();
        lp.width = size;
        lp.height = size;

        findViewById(R.id.fullScreenFrame).setOnClickListener(view -> onBackPressed());
    }

    private void showToast(View view) {
        Toast.makeText(ActivityMain.this, "نسخه نصب شده مناسب گوشی شما نیست!! \nلطفا از مارکت های معتبر دانلود کنید.", Toast.LENGTH_LONG).show();
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                showToast(view);
            }
        }, 2000);
    }

    private void connectionState() {

        G.onConnectionChangeState = connectionStateR -> runOnUiThread(() -> {
            G.connectionState = connectionStateR;
            G.connectionStateMutableLiveData.postValue(connectionStateR);
        });

        G.onUpdating = new OnUpdating() {
            @Override
            public void onUpdating() {
                runOnUiThread(() -> {
                    G.connectionState = ConnectionState.UPDATING;
                    G.connectionStateMutableLiveData.postValue(ConnectionState.UPDATING);
                });
            }

            @Override
            public void onCancelUpdating() {
                /**
                 * if yet still G.connectionState is in update state
                 * show latestState that was in previous state
                 */
                if (G.connectionState == ConnectionState.UPDATING) {
                    G.onConnectionChangeState.onChangeState(ConnectionState.IGAP);
                    G.connectionStateMutableLiveData.postValue(ConnectionState.IGAP);
                }
            }
        };
    }

    /*private void getWallpaperAsDefault() {
        try {
            RealmWallpaper realmWallpaper = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmWallpaper.class).equalTo(RealmWallpaperFields.TYPE, ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE).findFirst();
            });
            if (realmWallpaper != null) {
                if (realmWallpaper.getWallPaperList() != null && realmWallpaper.getWallPaperList().size() > 0) {
                    RealmAttachment pf = realmWallpaper.getWallPaperList().get(realmWallpaper.getWallPaperList().size() - 1).getFile();
                    String bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                    if (!new File(bigImagePath).exists()) {
                        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, bigImagePath, 2, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String mPath, final int progress) {

                                if (progress == 100) {
                                    setDefaultBackground(bigImagePath);
                                }

                            }

                            @Override
                            public void OnError(String token) {
                            }
                        });

                    } else {
                        setDefaultBackground(bigImagePath);

                    }
                } else {
                    getImageListFromServer();
                }
            } else {
                getImageListFromServer();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }

    }*/

    /*private void setDefaultBackground(String bigImagePath) {
        String finalPath = "";
        try {
            finalPath = HelperSaveFile.saveInPrivateDirectory(this, bigImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, finalPath);
        editor.putBoolean(SHP_SETTING.KEY_CHAT_BACKGROUND_IS_DEFAULT, true);
        editor.apply();
    }*/

    /*private void getImageListFromServer() {
        Log.e("wallpaper", "request in main ");
        G.onGetWallpaper = new OnGetWallpaper() {
            @Override
            public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {
                Log.e("wallpaper", "resp in main");
                RealmWallpaper.updateField(list, "", ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND_VALUE);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getWallpaperAsDefault();
                    }
                });
            }
        };

        new RequestInfoWallpaper().infoWallpaper(ProtoInfoWallpaper.InfoWallpaper.Type.CHAT_BACKGROUND);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case DirectPayHelper.requestCodeDirectPay:
                int errorType = 0;
                switch (resultCode) {
                    case 1:

                        /*
                        for example:
                        enData:{"PayInfo":null,"PayData":"cHeOCQFF+29LUGXpTnzpz1yofTqgK+pP0ojhabaKEqUSBvzFuhf86bhUnsPCeMOdRkwzeYnmygZyNhWTmvJ8bc9qJSl7xidX0QV5yMG7wxAfIPaZWiUV8TlRhWyzMUWSS1MW8CGF07yfYHnD7SuwNucsHN3VatM2nwWOu4UXvco=","DataSign":"mhVO8u4Wime9Yh\/abvZskpi3jZdhfmuyLbYnqnjte9jmGGAHWXthDJLhN8Jfl65Wq9OTDIM51+nmQSZokqBCM8YFuMYOdrNLffbRHB5ZEKIAu+acYJhx2XdV\/7N6h9h2iMa77eaC0m0FKhYHlVNK5TDZc8Mz55o2swIhS37Beik=","AutoConfirm":false}
                        message:مبلغ تراکنش کمتر از حد تعیین شده توسط صادرکننده کارت و یا بیشتر از حد مجاز می باشد
                        status:61
                         */
                        DirectPayHelper.setResultOfDirectPay(data.getStringExtra("enData"), 0, null, data.getStringExtra("message"));
                        break;
                    case 2:
                        errorType = data.getIntExtra("errorType", 0);
                        break;
                    case 5:
                        errorType = data.getIntExtra("errorType", 0);
                        break;
                }
                if (errorType != 0) {
                    showErrorTypeMpl(errorType);
                }
                break;

            case CardToCardHelper.requestCodeCardToCard:
                String message = "";

                switch (resultCode) {
                    case 2:
                        message = getString(R.string.dialog_canceled);
                        break;
                    case 3:
                        message = getString(R.string.server_error);
                        break;
                    case 1:
                        break;
                }
                if (data != null && data.getIntExtra("errorType", 0) != 0) {
                    message = getErrorTypeMpl(data.getIntExtra("errorType", 0));
                } else {
                    if (data != null && data.getStringExtra("message") != null && !data.getStringExtra("message").equals("")) {
                        message = data.getStringExtra("message");
                    }
                }

                if (data != null && data.getStringExtra("enData") != null && !data.getStringExtra("enData").equals("")) {
                    CardToCardHelper.setResultOfCardToCard(data.getStringExtra("enData"), 0, null, message);
                } else {
                    if (message.length() > 0) {
                        HelperError.showSnackMessage(message, false);
                    }
                }

                break;
            case requestCodePaymentCharge:
            case requestCodePaymentBill:
                getPaymentResultCode(resultCode, data);
                break;
            case requestCodeQrCode:
                IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
                if (result.getContents() != null) {
                    new RequestUserVerifyNewDevice().verifyNewDevice(result.getContents());
                }
                break;
            case kuknosRequestCodeQrCode:
                IntentResult kuknosWID = IntentIntegrator.parseActivityResult(resultCode, data);
                if (kuknosWID.getContents() != null) {
                    KuknosSendFrag myFragment = (KuknosSendFrag) getSupportFragmentManager().findFragmentByTag(KuknosSendFrag.class.getName());
                    if (myFragment != null && myFragment.isVisible()) {
                        myFragment.setWalletIDQrCode(kuknosWID.getContents());
                    }
                }
                break;
            case WALLET_REQUEST_CODE:
                /*try {
                    getUserCredit();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                break;
            case UserScoreViewModel.REQUEST_CODE_QR_IVAND_CODE:
                IntentResult result2 = IntentIntegrator.parseActivityResult(resultCode, data);
                if (result2.getContents() != null) {
                    doIvandScore(result2.getContents(), ActivityMain.this);
                }
                break;

            case ERROR_DIALOG_REQUEST_CODE:
                // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
                // before the instance state is restored throws an error. So instead,
                // set a flag here, which will cause the fragment to delay until
                // onPostResume.
                retryProviderInstall = true;
                break;

            case AttachFile.request_code_trim_video:
                if (resultCode == RESULT_OK) {
                    Fragment fragmentGallery = getSupportFragmentManager().findFragmentById(G.twoPaneMode ? R.id.detailFrame : R.id.mainFrame);
                    if (fragmentGallery instanceof FragmentGallery) {
                        getSupportFragmentManager().popBackStack();
                        getSupportFragmentManager().popBackStack();
                        goneDetailFrameInTabletMode();
                        Fragment fragmentChat = getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                        if (fragmentChat instanceof FragmentChat) {
                            ((FragmentChat) fragmentChat).manageTrimVideoResult(data);
                        } else {
                            //todo:// fix fragment chat backstack
                            if (dataTransformer != null) {
                                dataTransformer.transform(AttachFile.request_code_trim_video, data);
                            }
                        }
                    }
                }
                break;
        }
    }

    public void goneDetailFrameInTabletMode() {
        if (G.twoPaneMode) findViewById(R.id.fullScreenFrame).setVisibility(View.GONE);
    }

    /**
     * This method is only called if the provider is successfully updated
     * (or is already up-to-date).
     */
    @Override
    public void onProviderInstalled() {
        // Provider is up-to-date, app can make secure network calls.
        Log.wtf(this.getClass().getName(), "onProviderInstalled");
    }

    /**
     * This method is called if updating fails; the error code indicates
     * whether the error is recoverable.
     */
    @Override
    public void onProviderInstallFailed(int errorCode, Intent intent) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        if (availability.isUserResolvableError(errorCode)) {
            // Recoverable error. Show a dialog prompting the user to
            // install/update/enable Google Play services.
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                availability.showErrorDialogFragment(this, errorCode, ERROR_DIALOG_REQUEST_CODE, dialog -> {
                    // The user chose not to take the recovery action
                    onProviderInstallerNotAvailable();
                });
            }
        } else {
            // Google Play services is not available.
            onProviderInstallerNotAvailable();
        }
    }

    /**
     * On resume, check to see if we flagged that we need to reinstall the
     * provider.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (retryProviderInstall) {
            // We can now safely retry installation.
            ProviderInstaller.installIfNeededAsync(this, this);
        }
        retryProviderInstall = false;
    }

    private void onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
        new MaterialDialog.Builder(this)
                .title(R.string.attention).titleColor(Color.parseColor("#1DE9B6"))
                .titleGravity(GravityEnum.CENTER)
                .buttonsGravity(GravityEnum.CENTER)
                .content("برای استفاده از این بخش نیاز به گوگل سرویس است.").contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok).onPositive((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkKeepMedia() {

        final int keepMedia = sharedPreferences.getInt(SHP_SETTING.KEY_KEEP_MEDIA_NEW, 0);
        if (keepMedia != 0 && G.isCalculatKeepMedia) {// if Was selected keep media at 1week
            G.isCalculatKeepMedia = false;
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long last;
                    long currentTime = G.currentTime;
                    long saveTime = sharedPreferences.getLong(SHP_SETTING.KEY_KEEP_MEDIA_TIME, -1);
                    if (saveTime == -1) {
                        last = keepMedia;
                    } else {
                        long days = (long) keepMedia * 1000L * 60 * 60 * 24;

                        long b = currentTime - saveTime;
                        last = b / days;
                    }

                    if (last >= keepMedia) {
                        new HelperCalculateKeepMedia().calculateTime();
                    }
                }
            }, 5000);
        }
    }

    private void getPaymentResultCode(int resultCode, Intent data) {

        if (G.onMplResult != null) {
            G.onMplResult.onResult(false);
        }

        String enData = "", message = "", status = "0";
        int errorType = 0, orderId = 0;

        switch (resultCode) {
            case 1:// payment ok
                enData = data.getStringExtra("enData");
                message = data.getStringExtra("message");
                status = String.valueOf(data.getIntExtra("status", 0));
                break;
            case 2://payment error
                errorType = data.getIntExtra("errorType", 0);
                orderId = data.getIntExtra("OrderID", 0);
                break;
            case 3://bill payment ok
                enData = data.getStringExtra("enData");
                message = data.getStringExtra("message");
                status = String.valueOf(data.getIntExtra("status", 0));
                break;
            case 4://bill payment error
                errorType = data.getIntExtra("errorType", 0);
                break;
            case 5://internal error payment
                errorType = data.getIntExtra("errorType", 0);
                orderId = data.getIntExtra("OrderID", 0);
                break;
            case 6://internal error bill
                errorType = data.getIntExtra("errorType", 0);
                break;
            case 7:// charge payment ok
                enData = data.getStringExtra("enData");
                message = data.getStringExtra("message");
                status = String.valueOf(data.getIntExtra("status", 0));
                break;
            case 8: // charge payment error
                errorType = data.getIntExtra("errorType", 0);
                break;
            case 9:// internal error charge
                errorType = data.getIntExtra("errorType", 0);
                break;
        }

        if (errorType != 0) {
            showErrorTypeMpl(errorType);
        }
    }

    public void checkGoogleUpdate() {
        Log.wtf(this.getClass().getName(), "installIfNeeded");
        ProviderInstaller.installIfNeededAsync(this, this);
        Log.wtf(this.getClass().getName(), "installIfNeeded");
    }

    //*******************************************************************************************************************************************

    private void showErrorTypeMpl(int errorType) {
        String message = getErrorTypeMpl(errorType);

        if (message.length() > 0) {
            HelperError.showSnackMessage(message, false);
        }
    }

    private String getErrorTypeMpl(int errorType) {
        String message = "";
        switch (errorType) {
            case 2:
                message = getString(R.string.time_out_error);
                break;
            case 1000:
                message = getString(R.string.connection_error);
                break;
            case 1001:
                message = getString(R.string.server_error);
                break;
            case 1002:
                message = getString(R.string.network_error);
                break;
            case 201:
                message = getString(R.string.dialog_canceled);
                break;
            case 2334:
                message = getString(R.string.device_root);
                break;
        }

        return message;
    }

    //*******************************************************************************************************************************************

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        AndroidUtils.checkDisplaySize(this, newConfig);
        if (G.twoPaneMode) {

            boolean beforeState = G.isLandscape;

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                G.isLandscape = true;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                G.isLandscape = false;
            }

            /*if (beforeState != G.isLandscape) {
             *//*designLayout(chatLayoutMode.none);*//*
                Log.wtf(this.getClass().getName(), "onConfigurationChanged");
                setViewConfigurationChanged();
            }*/
            setDialogFragmentSize();
        }
        super.onConfigurationChanged(newConfig);
        G.rotationState = newConfig.orientation;
    }

    private void setViewConfigurationChanged() {
        if (G.twoPaneMode) {
            if (G.isLandscape) {
                Log.wtf(this.getClass().getName(), "isLandscape");
                findViewById(R.id.mainFrame).setVisibility(View.VISIBLE);
                findViewById(R.id.roomListFrame).setVisibility(View.VISIBLE);
            } else {
                Log.wtf(this.getClass().getName(), "not Landscape");
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                if (fragment instanceof FragmentChat) {
                    findViewById(R.id.roomListFrame).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.mainFrame).setVisibility(View.GONE);
                }
            }
        }
    }

    //******************************************************************************************************************************

    private void initTabStrip(Intent intent) {
        if (G.twoPaneMode) {
            new HelperFragment(getSupportFragmentManager(), new TabletEmptyChatFragment()).load(true);
        }
        Log.wtf(this.getClass().getName(), "initTabStrip");
        BottomNavigationFragment bottomNavigationFragment = new BottomNavigationFragment();

        if (intent.getExtras() != null && intent.getExtras().getString(DEEP_LINK) != null) {
            bottomNavigationFragment.setCrawlerMap(intent.getExtras().getString(DEEP_LINK, DEEP_LINK_CALL));
        }

        new HelperFragment(getSupportFragmentManager(), bottomNavigationFragment).load();
    }


    /**
     * send client condition
     */

    @Override
    protected void onStart() {
        super.onStart();
        if (G.ISRealmOK) {
            if (!G.isFirstPassCode) {
                openActivityPassCode();
            }
            G.isFirstPassCode = false;

        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    public void openActivityPassCode() {
        if (PassCode.getInstance().isPassCode()) {
            ActivityMain.isLock = HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE);
        }

        if (PassCode.getInstance().isPassCode() && isLock && !G.isRestartActivity && !isUseCamera && isStoragePage) {
            enterPassword();
        } else if (!G.isRestartActivity) {
            long currentTime = System.currentTimeMillis();
            long timeLock = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
            long oldTime = sharedPreferences.getLong(SHP_SETTING.KEY_OLD_TIME, 0);
            long calculatorTimeLock = currentTime - oldTime;

            if (timeLock > 0 && calculatorTimeLock > (timeLock * 1000)) {
                enterPassword();
            }
        }
        /**
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         */
        isUseCamera = false;

        G.isRestartActivity = false;
    }

    private void initComponent() {

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        G.onMapRegisterState = new OnMapRegisterState() {
            @Override
            public void onState(final boolean state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state) {
                            editor.putBoolean(SHP_SETTING.REGISTER_STATUS, true);
                            editor.apply();
                        } else {
                            editor.putBoolean(SHP_SETTING.REGISTER_STATUS, false);
                            editor.apply();
                        }
                    }
                });
            }
        };
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener, boolean isDisable) {
        if (!isDisable) {
            ActivityMain.onBackPressedListener = onBackPressedListener;
        } else {
            ActivityMain.onBackPressedListener = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (G.dispatchTochEventChat != null) {
            G.dispatchTochEventChat.getToch(ev);
        }

        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //Fix for support lib bug, happening when onDestroy() is
            HelperLog.getInstance().setErrorLog(e);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        Log.wtf(this.getClass().getName(), "onBackPressed");
        if (G.ISRealmOK) {
            if (G.onBackPressedWebView != null) {
                Log.wtf(this.getClass().getName(), "onBackPressedWebView");
                if (G.onBackPressedWebView.onBack()) {
                    return;
                }
            }

            if (G.onBackPressedChat != null) {
                Log.wtf(this.getClass().getName(), "onBackPressedChat");
                if (G.onBackPressedChat.onBack()) {
                    return;
                }
            }

            if (onBackPressedListener != null) {
                Log.wtf(this.getClass().getName(), "onBackPressedChat");
                onBackPressedListener.doBack();
            }
            if (G.twoPaneMode) {
                Log.wtf(this.getClass().getName(), "twoPaneMode");
                if (findViewById(R.id.fullScreenFrame).getVisibility() == View.VISIBLE) {//handle back in fragment show like dialog
                    Log.wtf(this.getClass().getName(), "fullScreenFrame VISIBLE");
                    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fullScreenFrame);
                    if (frag == null) {
                        Log.wtf(this.getClass().getName(), "pop from: detailFrame");
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detailFrame);
                        if (fragment != null) {
                            getSupportFragmentManager().popBackStackImmediate();
                        }
                        Fragment fragmentShowed = getSupportFragmentManager().findFragmentById(R.id.detailFrame);
                        if (fragmentShowed == null) {
                            findViewById(R.id.fullScreenFrame).setVisibility(View.GONE);
                        }
                    } else {
                        Log.wtf(this.getClass().getName(), "pop from: fullScreenFrame");
                        getSupportFragmentManager().popBackStackImmediate();
                        findViewById(R.id.fullScreenFrame).setVisibility(View.GONE);
                    }
                } else {
                    Log.wtf(this.getClass().getName(), "fullScreenFrame not VISIBLE");
                    if (getSupportFragmentManager().getBackStackEntryCount() > 2) {
                        Log.wtf(this.getClass().getName(), "pop from: backStack");
                        if (getSupportFragmentManager().getBackStackEntryAt(2).getName().equals(FragmentChat.class.getName())) {
                            if (G.isLandscape) {
                                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.roomListFrame);
                                if (fragment instanceof BottomNavigationFragment) {
                                    if (((BottomNavigationFragment) fragment).isAllowToBackPressed()) {
                                        getSupportFragmentManager().popBackStackImmediate();
                                    }
                                } else {
                                    getSupportFragmentManager().popBackStackImmediate();
                                }
                            } else {
                                getSupportFragmentManager().popBackStackImmediate();
                                findViewById(R.id.mainFrame).setVisibility(View.GONE);
                                findViewById(R.id.roomListFrame).setVisibility(View.VISIBLE);
                            }
                        } else {
                            getSupportFragmentManager().popBackStackImmediate();
                        }
                    } else {
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.roomListFrame);
                        if (fragment instanceof BottomNavigationFragment) {
                            if (((BottomNavigationFragment) fragment).isAllowToBackPressed()) {
                                if (((BottomNavigationFragment) fragment).isFirstTabItem()) {
                                    Log.wtf(this.getClass().getName(), "pop from: finish");
                                    finish();
                                }
                            }
                        }
                    }
                }
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    if (!(getSupportFragmentManager().findFragmentById(R.id.mainFrame) instanceof PaymentFragment)) {
//                        List fragmentList = getSupportFragmentManager().getFragments();
                        boolean handled = false;
                        try {
                            // because some of our fragments are NOT extended from BaseFragment
//                            Log.wtf("amini", "onBackPressed: " + fragmentList.get(fragmentList.size() - 1).getClass().getName());
                            Fragment frag = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());
//                            Log.wtf("amini", "on back frag H " + frag.getClass().getName());
                            handled = ((BaseFragment) frag).onBackPressed();
//                            handled = ((BaseFragment) fragmentList.get(fragmentList.size() - 1)).onBackPressed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!handled) {
                            super.onBackPressed();
                        }
                    }
                } else {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                    if (fragment instanceof BottomNavigationFragment) {
                        if (((BottomNavigationFragment) fragment).isAllowToBackPressed()) {
                            if (((BottomNavigationFragment) fragment).isFirstTabItem()) {
                                finish();
                            }
                        }
                    }
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        Log.wtf(this.getClass().getName(), "onResume");
        super.onResume();
        if (G.ISRealmOK) {
            resume();
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        Log.wtf(this.getClass().getName(), "onResume");
    }

    public void resume() {
        /**
         * after change language in ActivitySetting this part refresh Activity main
         */
        designLayout(chatLayoutMode.none);

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponseRoomList(this);
        G.onUserInfoMyClient = this;
        G.onMapRegisterStateMain = this;
        G.onPayment = this;


        try {
            startService(new Intent(this, ServiceContact.class));
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = getIntent();
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null && appLinkData.getHost() != null && appLinkData.getHost().equals("com.android.contacts")) {
            ContactUtils contactUtils = new ContactUtils(G.context, appLinkData);
            String userId = contactUtils.retrieveNumber(); // we set retrieveNumber as userId

            if (intent.getType().equalsIgnoreCase("vnd.android.cursor.item/vnd.net.iGap.call")) {

                try {
                    check(Long.parseLong(userId));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    HelperPublicMethod.goToChatRoom(Long.parseLong(userId), null, null);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        } else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            if (data != null && data.getHost().equals("deep_link")) {
                Intent intentTemp = new Intent();
                intentTemp.putExtra(DEEP_LINK, data.getQuery());
                handleDeepLink(intentTemp);
            } else {
                HelperUrl.getLinkInfo(intent, ActivityMain.this);
            }
        }

        getIntent().setData(null);

        //ActivityMain.setMediaLayout();

        /*if (G.isPassCode) {
            iconLock.setVisibility(View.VISIBLE);

            if (isLock) {
                iconLock.setText(getResources().getString(R.string.md_igap_lock));
            } else {
                iconLock.setText(getResources().getString(R.string.md_igap_lock_open_outline));
            }
        } else {
            iconLock.setVisibility(View.GONE);
        }*/

    }

    private void enterPassword() {
        Intent intent = new Intent(ActivityMain.this, ActivityEnterPassCode.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        Log.wtf(this.getClass().getName(), "onPause");
        super.onPause();
        if (G.ISRealmOK) {
            DbManager.getInstance().doRealmTask(realm -> {
                AppUtils.updateBadgeOnly(realm, -1);
            });
        }
        Log.wtf(this.getClass().getName(), "onPause");
    }


    @Override
    public void onChatClearMessage(final long roomId, long clearId) {
        //empty
    }

    @Override
    public void onUserInfoTimeOut() {
        //empty
    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {
        //empty
    }

    @Override
    public void onStateMain(boolean state) {
    }

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

    }

    //******* GroupAvatar and ChannelAvatar

    @Override
    public void onAvatarAddError() {

    }

    private void check(final long userId) {
        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    check(userId);
                }
            }, 1000);
        }

    }

    @Override
    public void onUserInfoMyClient() {

    }


    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //empty
    }

    //*****************************************************************************************************************************

    @Override
    public void onMessageReceive(final long roomId, final String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();
            if (room != null && realmRoomMessage != null) {
                /**
                 * client checked  (room.getUnreadCount() <= 1)  because in HelperMessageResponse unreadCount++
                 */
                if (room.getUnreadCount() <= 1) {
                    realmRoomMessage.setFutureMessageId(realmRoomMessage.getMessageId());
                }
            }
        });

        /**
         * don't send update status for own message
         */
        if (roomMessage.getAuthor().getUser() != null && roomMessage.getAuthor().getUser().getUserId() != AccountManager.getInstance().getCurrentUser().getId()) {
            // user has received the message, so I make a new delivered update status request
            // todo:please check in group and channel that user is joined

            if (roomType == ProtoGlobal.Room.Type.CHAT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            } else if (roomType == ProtoGlobal.Room.Type.GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            }
        }
    }

    @Override
    public void onMessageFailed(final long roomId, long messageId) {
        //empty
    }

    //*************************************************************

    public void designLayout(final chatLayoutMode mode) {
        if (G.twoPaneMode) {
            /*findViewById(R.id.mainFrame).setVisibility(G.isLandscape ? View.VISIBLE : View.GONE);*/
            /*if (G.isLandscape) {
                new HelperFragment(getSupportFragmentManager(), new TabletEmptyChatFragment()).load(true);
            } else {
                //todo: check if exist fragment remove it
                new HelperFragment(getSupportFragmentManager(),new FragmentMain()).remove();
            }*/
                    /*if (frameFragmentContainer != null) {
                        if (frameFragmentContainer.getChildCount() == 0) {
                            *//*if (frameFragmentBack != null) {
                                frameFragmentBack.setVisibility(View.GONE);
                            }*//*
                        } else if (frameFragmentContainer.getChildCount() == 1) {
                            disableSwipe = true;
                        } else {
                            disableSwipe = false;
                        }
                    } else {*/
                        /*if (frameFragmentBack != null) {
                            frameFragmentBack.setVisibility(View.GONE);
                        }*/
            /*}*/

            if (G.isLandscape) {
                /*setWeight(frameChatContainer, 2);*/
                /*setWeight(frameFragmentContainer, 1);*/
            } else {

                if (mode == chatLayoutMode.show) {
                    /*setWeight(frameChatContainer, 1);*/
                    /*setWeight(frameFragmentContainer, 0);*/
                } else if (mode == chatLayoutMode.hide) {
                    /*setWeight(frameChatContainer, 0);*/
                    /*setWeight(frameFragmentContainer, 1);*/
                } else {
                            /*if (frameChatContainer.getChildCount() > 0) {
                                setWeight(frameChatContainer, 1);
                                *//*setWeight(frameFragmentContainer, 0);*//*
                            } else {
                                setWeight(frameChatContainer, 0);
                                *//*setWeight(frameFragmentContainer, 1);*//*
                            }*/
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        oldTime = System.currentTimeMillis();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SHP_SETTING.KEY_OLD_TIME, oldTime);
        editor.apply();
        G.onPayment = null;
    }

    /**
     * bottom navigation new message badge counter
     * unReadCount get user all unread message count
     * change badge color if color = 0 get default badge color
     * badge counter for other bottom navigation item should add listener to OnBottomNavigationBadge
     */

    @Override
    public void onChargeToken(int status, String token, int expireTime, String message) {
        if (status == 0) {
            Intent intent = new Intent(ActivityMain.this, PaymentInitiator.class);
            intent.putExtra("Type", "3");
            intent.putExtra("Token", token);
            startActivityForResult(intent, requestCodePaymentCharge);
        } else {
            if (G.onMplResult != null) {
                G.onMplResult.onResult(true);
            }
            HelperError.showSnackMessage(message, false);
        }
    }

    @Override
    public void onBillToken(int status, String token, int expireTime, String message) {
        if (status == 0) {
            Intent intent = new Intent(ActivityMain.this, PaymentInitiator.class);
            intent.putExtra("Type", "2");
            intent.putExtra("Token", token);
            startActivityForResult(intent, requestCodePaymentBill);
        } else {
            if (G.onMplResult != null) {
                G.onMplResult.onResult(true);
            }
            HelperError.showSnackMessage(message, false);
        }
    }

    @Override
    public void setRefreshBalance() {
        /*try {
            getUserCredit();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*public void getUserCredit() {

        WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
        if (Auth.getCurrentAuth() != null) {
            Web.getInstance().getWebService().getCredit(Auth.getCurrentAuth().getId()).enqueue(new Callback<ArrayList<Card>>() {
                @Override
                public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                    if (response.body() != null) {
                        retryConnectToWallet = 0;
                        if (response.body().size() > 0)
                            G.selectedCard = response.body().get(0);

                        G.cardamount = G.selectedCard.cashOutBalance;

                        if (G.selectedCard != null) {
                            if (itemCash != null) {
                                itemCash.setVisibility(View.VISIBLE);
                                itemCash.setText("" + getResources().getString(R.string.wallet_Your_credit) + " " +String.format(getString(R.string.wallet_Reial),G.cardamount));
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Card>> call, Throwable t) {

                    if (retryConnectToWallet < 3) {
                        Crashlytics.logException(new Exception(t.getMessage()));
                        getUserCredit();
                        retryConnectToWallet++;
                    }
                }
            });
        }
    }*/

    @Override
    public void receivedMessage(int id, Object... message) {

        if (id == EventManager.ON_ACCESS_TOKEN_RECIVE) {
            int response = (int) message[0];
            switch (response) {
                case socketMessages.SUCCESS:
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            /*getUserCredit();*/
                            retryConnectToWallet = 0;
                        }
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

    private void isChinesPhone() {
        final SharedPreferences settings = getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();


            new MaterialDialog.Builder(this)
                    .title(R.string.attention).titleColor(Color.parseColor("#1DE9B6"))
                    .titleGravity(GravityEnum.CENTER)
                    .buttonsGravity(GravityEnum.CENTER)
                    .checkBoxPrompt(getString(R.string.dont_show_again), false, new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                          /*  if (isChecked) {
                                editor.putBoolean(saveIfSkip, isChecked);
                                editor.apply();

                            }*/

                        }
                    })
                    .content(R.string.permission_auto_start).contentGravity(GravityEnum.CENTER)
                    .negativeText(R.string.ignore).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (dialog.isPromptCheckBoxChecked()) {
                        editor.putBoolean(saveIfSkip, true);
                        editor.apply();
                    }
                    dialog.dismiss();
                }
            })
                    .positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    if (dialog.isPromptCheckBoxChecked()) {
                        editor.putBoolean(saveIfSkip, true);
                        editor.apply();
                    }
                    dialog.dismiss();
                    try {

                        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);


                        } else if (Build.BRAND.equalsIgnoreCase("oppo")) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));

                        } else if (Build.BRAND.equalsIgnoreCase("Letv")) {

                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                            startActivity(intent);

                        } else if (Build.BRAND.equalsIgnoreCase("Honor")) {

                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);

                        } else if (Build.BRAND.equalsIgnoreCase("asus")) {
                            Intent intent = new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"));
                            startActivity(intent);

                        }
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }


                }
            }).show();

        }
    }

    @Override
    public void onLeftIconClickListener(View view) {

    }

    @Override
    public void onSearchClickListener(View view) {

    }

    @Override
    public void onRightIconClickListener(View view) {

    }

    public void onUpdateContacts() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
        if (fragment instanceof BottomNavigationFragment) {
            ((BottomNavigationFragment) fragment).updateContacts();
        }
    }

    public enum MainAction {
        downScrool, clinetCondition
    }

    public enum chatLayoutMode {
        none, show, hide
    }

    public interface MainInterface {
        void onAction(MainAction action);
    }

    public interface OnBackPressedListener {
        void doBack();
    }

    public void goToUserProfile() {
        getSupportFragmentManager().popBackStackImmediate(BottomNavigationFragment.class.getName(), 0);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.roomListFrame);
        if (fragment instanceof BottomNavigationFragment) {
            ((BottomNavigationFragment) fragment).goToUserProfile();
        }
    }

    public void goToChatPage(FragmentChat fragmentChat) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
        if (fragment instanceof FragmentChat) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, fragmentChat).commit();
        /*new HelperFragment(getSupportFragmentManager(), fragmentChat).setReplace(false).load();*/
    }

    //removeAllFragmentLoadedLikeDialogInTabletMode
    public void removeAllFragment() {
        getSupportFragmentManager().popBackStack(BottomNavigationFragment.class.getName(), 0);
        findViewById(R.id.fullScreenFrame).setVisibility(View.GONE);
    }

    public void removeAllFragmentFromMain() {
        if (G.twoPaneMode) {
            findViewById(R.id.fullScreenFrame).setVisibility(View.GONE);
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
            if (fragment instanceof FragmentChat) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
            getSupportFragmentManager().popBackStack(TabletEmptyChatFragment.class.getName(), 0);
        } else {
            getSupportFragmentManager().popBackStack(BottomNavigationFragment.class.getName(), 0);
        }
    }

    /**
     * base on main fragment structure that onResume just call one in main fragment
     * we should get bottom nav fragment (contains all startup fragments) and after
     * that get our fragment from bottom nav fragment and do our job
     */
    public void updatePassCodeState() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
        if (fragment instanceof BottomNavigationFragment) {
            ((BottomNavigationFragment) fragment).checkPassCodeIconVisibility();
        }
    }

    public void setForwardMessage(boolean enable) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
        if (fragment instanceof BottomNavigationFragment) {
            ((BottomNavigationFragment) fragment).setForwardMessage(enable);
            ((BottomNavigationFragment) fragment).isFirstTabItem();
        }
    }

    public void checkHasSharedData(boolean enable) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BottomNavigationFragment.class.getName());
        if (fragment instanceof BottomNavigationFragment) {
            ((BottomNavigationFragment) fragment).checkHasSharedData(enable);
        }
    }

    public void goToTabletEmptyPage() {
        Log.wtf(this.getClass().getName(), "goToTabletEmptyPage");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);
        if (fragment instanceof FragmentChat) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public Fragment getFragment(String fragmentTag) {
        return getSupportFragmentManager().findFragmentByTag(fragmentTag);
    }

    public void updateUiForChangeAccount() {
        int t = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < t; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        initTabStrip(getIntent());
        // Clear all notification
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    public void chatBackgroundChanged() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentChatSettings.class.getName());
        if (fragment instanceof FragmentChatSettings) {
            ((FragmentChatSettings) fragment).chatBackgroundChange();
        }
        if (G.twoPaneMode) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(TabletEmptyChatFragment.class.getName());
            if (f instanceof TabletEmptyChatFragment) {
                ((TabletEmptyChatFragment) f).getChatBackground();
            }
        }
    }
}
