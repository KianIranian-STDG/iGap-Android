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
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vanniktech.emoji.sticker.struct.StructSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.dialog.SubmitScoreDialog;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.eventbus.socketMessages;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.fragments.FragmentNewGroup;
import net.iGap.fragments.FragmentSetting;
import net.iGap.fragments.FragmentUserProfile;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.fragments.RegisteredContactsFragment;
import net.iGap.fragments.SearchFragment;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.helper.CardToCardHelper;
import net.iGap.helper.DirectPayHelper;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCalculateKeepMedia;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ServiceContact;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.FinishActivity;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.ITowPanModDesinLayout;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChatClearMessageResponse;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnClientCondition;
import net.iGap.interfaces.OnConnectionChangeState;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGetWallpaper;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnMapRegisterState;
import net.iGap.interfaces.OnMapRegisterStateMain;
import net.iGap.interfaces.OnPayment;
import net.iGap.interfaces.OnRefreshActivity;
import net.iGap.interfaces.OnUnreadChange;
import net.iGap.interfaces.OnUpdating;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnVerifyNewDevice;
import net.iGap.interfaces.OneFragmentIsOpen;
import net.iGap.interfaces.OpenFragment;
import net.iGap.interfaces.RefreshWalletBalance;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.BottomNavigation;
import net.iGap.libs.bottomNavigation.Event.OnBottomNavigationBadge;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.libs.floatingAddButton.StateChangeListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.ContactUtils;
import net.iGap.module.FileUtils;
import net.iGap.module.FixAppBarLayoutBehavior;
import net.iGap.module.LoginActions;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyAppBarLayout;
import net.iGap.module.MyPhonStateService;
import net.iGap.module.NotSwipeableViewPager;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmStickers;
import net.iGap.realm.RealmUserInfo;
import net.iGap.realm.RealmWallpaper;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestInfoWallpaper;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserIVandSetActivity;
import net.iGap.request.RequestUserVerifyNewDevice;
import net.iGap.request.RequestWalletGetAccessToken;
import net.iGap.request.RequestWalletIdMapping;
import net.iGap.viewmodel.ActivityCallViewModel;
import net.iGap.viewmodel.FragmentIVandProfileViewModel;

import org.paygear.RaadApp;
import org.paygear.fragment.PaymentHistoryFragment;
import org.paygear.model.Card;
import org.paygear.web.Web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import ir.pec.mpl.pecpayment.view.PaymentInitiator;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.G.context;
import static net.iGap.G.isSendContact;
import static net.iGap.G.userId;
import static net.iGap.R.string.updating;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;

public class ActivityMain extends ActivityEnhanced implements OnUserInfoMyClient, OnPayment, OnUnreadChange, OnChatClearMessageResponse, OnChatSendMessageResponse, OnClientCondition, OnGroupAvatarResponse, DrawerLayout.DrawerListener, OnMapRegisterStateMain, EventListener, RefreshWalletBalance, ToolbarListener {

    public static final String openChat = "openChat";
    public static final String openMediaPlyer = "openMediaPlyer";
    public static final int requestCodePaymentCharge = 198;
    public static final int requestCodePaymentBill = 199;
    public static final int requestCodeQrCode = 200;
    public static final int requestCodeBarcode = 201;
    public static final int WALLET_REQUEST_CODE = 1024;

    public static boolean isMenuButtonAddShown = false;
    public static boolean isOpenChatBeforeSheare = false;
    public static boolean isLock = true;
    public static boolean isActivityEnterPassCode = false;
    public static FinishActivity finishActivity;
    public static boolean disableSwipe = false;
    public static OnBackPressedListener onBackPressedListener;
    public static boolean isUseCamera = false;
    public static boolean waitingForConfiguration = false;
    private static long oldTime;
    private static long currentTime;
    public TextView iconLock;
    public ArcMenu arcMenu;
    FragmentCall fragmentCall;
    FloatingActionButton btnStartNewChat;
    FloatingActionButton btnCreateNewGroup;
    FloatingActionButton btnCreateNewChannel;
    SampleFragmentPagerAdapter sampleFragmentPagerAdapter;
    private LinearLayout mediaLayout;
    private FrameLayout frameChatContainer;
    private RelativeLayout frameMainContainer;
    private FrameLayout frameFragmentBack;
    private FrameLayout frameFragmentContainer;
    private MyAppBarLayout appBarLayout;
    private Typeface titleTypeface;
    private SharedPreferences sharedPreferences;
    private ImageView imgNavImage;
    private ProgressBar contentLoading;
    private Realm mRealm;
    private boolean isNeedToRegister = false;
    private NotSwipeableViewPager mViewPager;
    private ArrayList<Fragment> pages = new ArrayList<>();
    private String phoneNumber;
    private TextView itemCash;
    private ViewGroup itemNavWallet;
    private int currentFabIcon = 0;
    private RealmUserInfo userInfo;
    private int lastMarginTop = 0;
    private int retryConnectToWallet = 0;
    private BottomNavigation bottomNavigation;

    public static void setWeight(View view, int value) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = value;
        view.setLayoutParams(params);

        if (value > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

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
            HelperLog.setErrorLog(e);
        }
    }

    public static void setStripLayoutCall() {
        if (G.isInCall) {
            if (ActivityCall.stripLayoutChat != null) {
                ActivityCall.stripLayoutChat.setVisibility(View.VISIBLE);

                if (ActivityCall.stripLayoutMain != null) {
                    ActivityCall.stripLayoutMain.setVisibility(View.GONE);
                }
            } else {
                if (ActivityCall.stripLayoutMain != null) {
                    ActivityCall.stripLayoutMain.setVisibility(View.VISIBLE);
                }
            }
        } else {

            if (ActivityCall.stripLayoutMain != null) {
                ActivityCall.stripLayoutMain.setVisibility(View.GONE);
            }

            if (ActivityCall.stripLayoutChat != null) {
                ActivityCall.stripLayoutChat.setVisibility(View.GONE);
            }
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

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {

            mRealm = Realm.getDefaultInstance();
        }

        return mRealm;
    }

    private void closeRealm() {
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeRealm();
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
    }

    private void checkIntent(Intent intent) {

        if (G.isRestartActivity) {
            return;
        }

        new HelperGetDataFromOtherApp(intent);

        if (intent.getAction() != null && intent.getAction().equals("net.iGap.activities.OPEN_ACCOUNT")) {
            new HelperFragment(new FragmentSetting()).load();
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {

            long roomId = extras.getLong(ActivityMain.openChat);
            if (!FragmentLanguage.languageChanged && roomId > 0) { // if language changed not need check enter to chat
                GoToChatActivity goToChatActivity = new GoToChatActivity(roomId);
                long peerId = extras.getLong("PeerID");
                if (peerId > 0) {
                    goToChatActivity.setPeerID(peerId);
                }
                goToChatActivity.startActivity();
            }
            FragmentLanguage.languageChanged = false;

            boolean openMediaPlayer = extras.getBoolean(ActivityMain.openMediaPlyer);
            if (openMediaPlayer) {
                if (getSupportFragmentManager().findFragmentByTag(FragmentMediaPlayer.class.getName()) == null) {
                    FragmentMediaPlayer fragment = new FragmentMediaPlayer();
                    new HelperFragment(fragment).setReplace(false).load();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("bagi", "ActivityMain:onCreate:start");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        MyPhonStateService myPhonStateService = new MyPhonStateService();

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


        BroadcastReceiver audioManagerReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //code...
                if (!G.appChangeRinggerMode) {
                    AudioManager mainAudioManager = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                    G.mainRingerMode = mainAudioManager.getRingerMode();
                }

            }
        };

        registerReceiver(audioManagerReciver, ringgerFilter);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.BRAND.equalsIgnoreCase("xiaomi") || Build.BRAND.equalsIgnoreCase("Honor") || Build.BRAND.equalsIgnoreCase("oppo") || Build.BRAND.equalsIgnoreCase("asus"))
                isChinesPhone();
        }
//        setTheme(R.style.AppThemeTranslucent);

        if (G.isFirstPassCode) {
            openActivityPassCode();
        }
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        //isNeedToRegister = true; // continue app even don't have storage permission
        //isOnGetPermission = true;
        //}
        super.onCreate(savedInstanceState);

        RaadApp.paygearHistoryOpenChat = new PaymentHistoryFragment.PaygearHistoryOpenChat() {
            @Override
            public void paygearId(String id) {

                new RequestWalletIdMapping().walletIdMapping(id);
            }
        };

        EventManager.getInstance().addEventListener(EventManager.ON_ACCESS_TOKEN_RECIVE, this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        finishActivity = new FinishActivity() {
            @Override
            public void finishActivity() {
                // ActivityChat.this.finish();
                finish();
            }
        };

        if (isNeedToRegister) {

            Intent intent = new Intent(this, ActivityRegisteration.class);
            startActivity(intent);

            finish();
            return;
        }


        G.fragmentManager = getSupportFragmentManager();

        //checkAppAccount();

        try {
            HelperPermission.getPhonePermision(this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


        userInfo = getRealm().where(RealmUserInfo.class).findFirst();

        if (userInfo == null || !userInfo.getUserRegistrationState()) { // user registered before
            isNeedToRegister = true;
            Intent intent = new Intent(this, ActivityRegisteration.class);
            startActivity(intent);

            if (mRealm != null && !mRealm.isClosed()) {
                mRealm.close();
            }

            finish();
            return;
        }


        if (!G.userLogin) {
            /**
             * set true mFirstRun for get room history after logout and login again
             */

            //licenceChecker();

            sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

            boolean deleteFolderBackground = sharedPreferences.getBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, true);

            if (deleteFolderBackground) {
                deleteContentFolderChatBackground();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, false);
                editor.apply();
            }
        }
        setContentView(R.layout.activity_main);

        frameChatContainer = findViewById(R.id.am_frame_chat_container);
        frameMainContainer = findViewById(R.id.am_frame_main_container);

        if (G.twoPaneMode) {
            G.isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

            frameFragmentBack = findViewById(R.id.am_frame_fragment_back);
            frameFragmentContainer = findViewById(R.id.am_frame_fragment_container);

            G.oneFragmentIsOpen = new OneFragmentIsOpen() {
                @Override
                public void justOne() {

                    disableSwipe = frameFragmentContainer.getChildCount() == 0;


                }
            };

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            int size = Math.min(width, height) - 50;

            ViewGroup.LayoutParams lp = frameFragmentContainer.getLayoutParams();
            lp.width = size;
            lp.height = size;


            designLayout(chatLayoutMode.none);

            frameFragmentBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onBackPressed();
                }
            });

            G.iTowPanModDesinLayout = new ITowPanModDesinLayout() {
                @Override
                public void onLayout(chatLayoutMode mode) {
                    designLayout(mode);
                }

                @Override
                public boolean getBackChatVisibility() {

                    return frameFragmentBack != null && frameFragmentBack.getVisibility() == View.VISIBLE;

                }

                @Override
                public void setBackChatVisibility(boolean visibility) {

                    if (true) {
                        if (frameFragmentBack != null) {
                            frameFragmentBack.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };


        } else {
            frameChatContainer.setVisibility(View.GONE);
        }

        isOpenChatBeforeSheare = false;
        checkIntent(getIntent());


        initTabStrip();

        initFloatingButtonCreateNew();

        arcMenu.setBackgroundTintColor();

        btnStartNewChat.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        btnCreateNewGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));
        btnCreateNewChannel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        final G application = (G) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RoomList");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mediaLayout = findViewById(R.id.amr_ll_music_layout);

        MusicPlayer.setMusicPlayer(mediaLayout);
        MusicPlayer.mainLayout = mediaLayout;

        ActivityCall.stripLayoutMain = findViewById(R.id.am_ll_strip_call);


        appBarLayout = findViewById(R.id.appBarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new FixAppBarLayoutBehavior());

        final ViewGroup toolbar = findViewById(R.id.rootToolbar);

        appBarLayout.addOnMoveListener(new MyAppBarLayout.OnMoveListener() {
            @Override
            public void onAppBarLayoutMove(AppBarLayout appBarLayout, int verticalOffset, boolean moveUp) {
                int marginTop = Math.round(AndroidUtils.dpToPx(ActivityMain.this, 10f) * 1.0f * Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
                if (lastMarginTop != marginTop) {
                    lastMarginTop = marginTop;
//                    LinearLayout.LayoutParams param = ((LinearLayout.LayoutParams) navigationTabStrip.getLayoutParams());
//                    param.setMargins(0, marginTop, 0, 0);
//                    navigationTabStrip.setLayoutParams(param);
                }
                toolbar.clearAnimation();
                if (moveUp) {
                    if (toolbar.getAlpha() != 0F) {
                        toolbar.animate().setDuration(150).alpha(0F).start();
                    }
                } else {
                    if (toolbar.getAlpha() != 1F) {
                        toolbar.animate().setDuration(150).alpha(1F).start();
                    }
                }
            }
        });

        initComponent();

        G.onPayment = this;

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean isGetContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT, false);
        /**
         * just do this action once
         */
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
                            new HelperFragment(fragmentNewGroup).setStateLoss(true).load();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        lockNavigation();
                    }
                });
            }
        };

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);


        connectionState();

        initDrawerMenu();

        checkKeepMedia();


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


        // Log.i("#token",FirebaseInstanceId.getInstance().getToken().toString());
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.isEmpty()) {
            getWallpaperAsDefault();
        }

        ApiEmojiUtils.getAPIService().getFavoritSticker().enqueue(new Callback<StructSticker>() {
            @Override
            public void onResponse(Call<StructSticker> call, Response<StructSticker> response) {

                if (response.body() != null) {
                    if (response.body().getOk()) {
                        RealmStickers.updateStickers(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<StructSticker> call, Throwable t) {
            }
        });

        Log.d("bagi", "ActivityMain:onCreate:end");
    }

    private void getWallpaperAsDefault() {
        try {
            RealmWallpaper realmWallpaper = getRealm().where(RealmWallpaper.class).findFirst();
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

        } catch (NullPointerException e2) {

        } catch (Exception e3) {

        }

    }

    private void setDefaultBackground(String bigImagePath) {
        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, bigImagePath);
        editor.apply();
    }

    private void getImageListFromServer() {
        G.onGetWallpaper = new OnGetWallpaper() {
            @Override
            public void onGetWallpaperList(final List<ProtoGlobal.Wallpaper> list) {
                RealmWallpaper.updateField(list, "");
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getWallpaperAsDefault();
                    }
                });
            }
        };

        new RequestInfoWallpaper().infoWallpaper();
    }

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
                        Log.d("bagi", "enData:" + data.getStringExtra("enData"));
                        Log.d("bagi", "message:" + data.getStringExtra("message"));
                        Log.d("bagi", "status:" + data.getIntExtra("status", 0));
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
            case WALLET_REQUEST_CODE:
                try {
                    getUserCredit();
                } catch (Exception e) {
                }

                break;
            case FragmentIVandProfileViewModel.REQUEST_CODE_QR_IVAND_CODE:
                IntentResult result2 = IntentIntegrator.parseActivityResult(resultCode, data);
                if (result2.getContents() != null) {
                    doIvandScore(result2.getContents(), ActivityMain.this);
                }
                break;

        }
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

        if (G.twoPaneMode) {

            boolean beforeState = G.isLandscape;

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                G.isLandscape = true;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                G.isLandscape = false;
            }

            if (beforeState != G.isLandscape) {
                designLayout(chatLayoutMode.none);
            }


        }

        super.onConfigurationChanged(newConfig);
        G.rotationState = newConfig.orientation;
    }

    private void initFloatingButtonCreateNew() {
        arcMenu = findViewById(R.id.ac_arc_button_add);
        arcMenu.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
                isMenuButtonAddShown = false;
            }
        });

        btnStartNewChat = findViewById(R.id.ac_fab_start_new_chat);
        btnStartNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                //bundle.putString("TITLE", "New Chat");
                bundle.putString("TITLE", "ADD");
                fragment.setArguments(bundle);

                try {
                    //getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    //    R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).replace(R.id.fragmentContainer, fragment, "register_contact_fragment").commit();

                    new HelperFragment(fragment).load();

                } catch (Exception e) {
                    e.getStackTrace();
                }
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }

                lockNavigation();
            }
        });

        btnCreateNewGroup = findViewById(R.id.ac_fab_crate_new_group);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);
                try {
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();

                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
            }
        });

        btnCreateNewChannel = findViewById(R.id.ac_fab_crate_new_channel);
        btnCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                try {
                    new HelperFragment(fragment).load();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
            }
        });

        arcMenu.fabMenu.setOnClickListener(v -> {

            if (mViewPager == null || mViewPager.getAdapter() == null) {
                return;
            }

            try {

                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentMain) {

                    FragmentMain fm = (FragmentMain) adapter.getItem(mViewPager.getCurrentItem());
                    switch (fm.mainType) {

                        case all:
                            //arcMenu.toggleMenu();
                            btnStartNewChat.performClick();

                            break;
                        case chat:
                            btnStartNewChat.performClick();
                            break;
                        case group:
                            btnCreateNewGroup.performClick();
                            break;
                        case channel:
                            btnCreateNewChannel.performClick();
                            break;
                    }
                } else if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentCall) {

                        ((FragmentCall) adapter.getItem(mViewPager.getCurrentItem())).showContactListForCall();
                    }
                } catch (Exception e) {
                    HelperLog.setErrorLog(e);
            }
        });
    }

    private void onSelectItem(int position) {
        FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();

        if (adapter.getItem(position) instanceof FragmentMain) {

            findViewById(R.id.amr_btn_search).setVisibility(View.VISIBLE);
            findViewById(R.id.am_btn_menu).setVisibility(View.GONE);
            setFabIcon(R.mipmap.plus);
            arcMenu.fabMenu.hide();
            arcMenu.setVisibility(View.VISIBLE);
        } else if (adapter.getItem(position) instanceof FragmentCall) {

            findViewById(R.id.amr_btn_search).setVisibility(View.GONE);
            findViewById(R.id.am_btn_menu).setVisibility(View.VISIBLE);
            setFabIcon(R.drawable.ic_call_black_24dp);
            arcMenu.fabMenu.hide();
            arcMenu.setVisibility(View.VISIBLE);
        } else if (adapter.getItem(position) instanceof DiscoveryFragment) {
            findViewById(R.id.amr_btn_search).setVisibility(View.GONE);
            findViewById(R.id.am_btn_menu).setVisibility(View.GONE);
            arcMenu.setVisibility(View.GONE);
        }

        if (arcMenu.isMenuOpened()) {
            arcMenu.toggleMenu();
        }

        arcMenu.fabMenu.show();
    }

    private void setFabIcon(int res) {

        if (res == currentFabIcon) {
            return;
        }
        currentFabIcon = res;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arcMenu.fabMenu.setImageDrawable(getResources().getDrawable(res, context.getTheme()));
        } else {
            arcMenu.fabMenu.setImageDrawable(getResources().getDrawable(res));
        }
    }

    //******************************************************************************************************************************

    private void initTabStrip() {

        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setPagingEnabled(false);

        boolean isRtl = HelperCalander.isPersianUnicode;

        bottomNavigation = findViewById(R.id.bn_main_bottomNavigation);
        bottomNavigation.setDefaultItem(2);

        bottomNavigation.setOnItemChangeListener(i -> {
            if (isRtl) {
                if (i == 4)
                    mViewPager.setCurrentItem(0 , false);
                if (i == 3)
                    mViewPager.setCurrentItem(1 , false);
                if (i == 2)
                    mViewPager.setCurrentItem(2 , false);
                if (i == 1)
                    mViewPager.setCurrentItem(3 , false);
                if (i == 0)
                    mViewPager.setCurrentItem(4 , false);
            } else {
                mViewPager.setCurrentItem(i , false);
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (isRtl) {
                    if (i == 4)
                        bottomNavigation.setCurrentItem(0);
                    if (i == 3)
                        bottomNavigation.setCurrentItem(1);
                    if (i == 2)
                        bottomNavigation.setCurrentItem(2);
                    if (i == 1)
                        bottomNavigation.setCurrentItem(3);
                    if (i == 0)
                        bottomNavigation.setCurrentItem(4);
                } else
                    bottomNavigation.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        findViewById(R.id.loadingContent).setVisibility(View.VISIBLE);
        mViewPager.setOffscreenPageLimit(5);

        if (HelperCalander.isPersianUnicode) {
            G.handler.postDelayed(() -> {
                pages.add(new FragmentUserProfile());
                pages.add(DiscoveryFragment.newInstance(0));
                pages.add(FragmentMain.newInstance(FragmentMain.MainType.all));
                fragmentCall = FragmentCall.newInstance(true);
                pages.add(fragmentCall);
                pages.add(RegisteredContactsFragment.newInstance());


                sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(sampleFragmentPagerAdapter);
                mViewPager.setCurrentItem(bottomNavigation.getDefaultItem());

                findViewById(R.id.loadingContent).setVisibility(View.GONE);
            }, 200);

        } else {

            G.handler.postDelayed(() -> {

                pages.add(RegisteredContactsFragment.newInstance());
                fragmentCall = FragmentCall.newInstance(true);
                pages.add(fragmentCall);
                pages.add(FragmentMain.newInstance(FragmentMain.MainType.all));
                pages.add(DiscoveryFragment.newInstance(0));
                //  pages.add(new FragmentSetting());
                pages.add(new FragmentUserProfile());

                sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(sampleFragmentPagerAdapter);
                findViewById(R.id.loadingContent).setVisibility(View.GONE);
                mViewPager.getAdapter().notifyDataSetChanged();
                mViewPager.setCurrentItem(bottomNavigation.getDefaultItem());

            }, 200);

        }


        MaterialDesignTextView txtMenu = findViewById(R.id.am_btn_menu);

        txtMenu.setOnClickListener(v -> {
            try {
                fragmentCall.openDialogMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (HelperCalander.isPersianUnicode) {
            ViewMaker.setLayoutDirection(mViewPager, View.LAYOUT_DIRECTION_RTL);
        }
    }


    /**
     * send client condition
     */

    @Override
    protected void onStart() {
        super.onStart();

        if (!G.isFirstPassCode) {
            openActivityPassCode();
        }
        G.isFirstPassCode = false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    public void openActivityPassCode() {
        if (!isActivityEnterPassCode && G.isPassCode && isLock && !G.isRestartActivity && !isUseCamera) {
            enterPassword();
        } else if (!isActivityEnterPassCode && !G.isRestartActivity) {
            currentTime = System.currentTimeMillis();
            SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            long timeLock = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
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

    /**
     * init  menu drawer
     */

    private void initDrawerMenu() {
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        setDrawerInfo(true);

        // gone or visible view call
        RealmCallConfig callConfig = getRealm().where(RealmCallConfig.class).findFirst();
        if (callConfig == null)
            new RequestSignalingGetConfiguration().signalingGetConfiguration();
    }

    private void closeDrawer() {

    }

    private void openMapFragment() {
        try {
            HelperPermission.getLocationPermission(ActivityMain.this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    try {
                        if (!waitingForConfiguration) {
                            waitingForConfiguration = true;
                            if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                                G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                                    @Override
                                    public void onGetConfiguration() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                                            }
                                        });
                                    }

                                    @Override
                                    public void getConfigurationTimeOut() {
                                        G.handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waitingForConfiguration = false;
                                            }
                                        }, 2000);
                                    }
                                };
                                new RequestGeoGetConfiguration().getConfiguration();
                            } else {
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        waitingForConfiguration = false;
                                    }
                                }, 2000);
                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                            }
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lockNavigation();
                        }
                    });
                }

                @Override
                public void deny() {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeDrawer();
    }

    private void initComponent() {


        iconLock = findViewById(R.id.am_btn_lock);

        iconLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLock) {
                    iconLock.setText(getResources().getString(R.string.md_igap_lock_open_outline));
                    isLock = false;
                } else {
                    iconLock.setText(getResources().getString(R.string.md_igap_lock));
                    isLock = true;
                }

            }
        });


        contentLoading = findViewById(R.id.loadingContent);

        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isRegisterStatus = sharedPreferences.getBoolean(SHP_SETTING.REGISTER_STATUS, false);
        if (isRegisterStatus) {
            startAnimationLocation();
        } else {
            stopAnimationLocation();
        }

        G.onMapRegisterState = new OnMapRegisterState() {
            @Override
            public void onState(final boolean state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state) {
                            startAnimationLocation();
                            editor.putBoolean(SHP_SETTING.REGISTER_STATUS, true);
                            editor.apply();
                        } else {
                            stopAnimationLocation();
                            editor.putBoolean(SHP_SETTING.REGISTER_STATUS, false);
                            editor.apply();
                        }
                    }
                });
            }
        };

        View amr_btn_search = findViewById(R.id.amr_btn_search);
        amr_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SearchFragment.newInstance();

                try {
                    //  getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "Search_fragment").commit();
                    new HelperFragment(fragment).load();


                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
            }
        });

        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }
    }

    public void stopAnimationLocation() {
        //
    }

    public void startAnimationLocation() {
        //
    }

    private void connectionState() {
        final TextView txtIgap = findViewById(R.id.cl_txt_igap);

        Typeface typeface = G.typeface_IRANSansMobile;

        if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            txtIgap.setText(R.string.waiting_for_network);
            txtIgap.setTypeface(typeface, Typeface.BOLD);
        } else if (G.connectionState == ConnectionState.CONNECTING) {
            txtIgap.setText(R.string.connecting);
            txtIgap.setTypeface(typeface, Typeface.BOLD);
        } else if (G.connectionState == ConnectionState.UPDATING) {
            txtIgap.setText(updating);
            txtIgap.setTypeface(typeface, Typeface.BOLD);
        } else {
            txtIgap.setText(R.string.app_name);
            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
        }

        G.onConnectionChangeState = new OnConnectionChangeState() {
            @Override
            public void onChangeState(final ConnectionState connectionStateR) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Typeface typeface = null;
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            typeface = titleTypeface;
                        }
                        G.connectionState = connectionStateR;

                        G.connectionStateMutableLiveData.postValue(connectionStateR);


                        if (connectionStateR == ConnectionState.WAITING_FOR_NETWORK) {
                            txtIgap.setText(R.string.waiting_for_network);
                            txtIgap.setTypeface(typeface, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.CONNECTING) {
                            txtIgap.setText(R.string.connecting);
                            txtIgap.setTypeface(typeface, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.UPDATING) {
                            txtIgap.setText(R.string.updating);
                            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
                        } else if (connectionStateR == ConnectionState.IGAP) {
                            txtIgap.setText(R.string.app_name);
                            txtIgap.setTypeface(titleTypeface, Typeface.BOLD);
                        } else {
                            txtIgap.setTypeface(typeface, Typeface.BOLD);
                        }
                    }
                });
            }
        };

        G.onUpdating = new OnUpdating() {
            @Override
            public void onUpdating() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Typeface typeface = null;
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            typeface = titleTypeface;
                        }
                        G.connectionState = ConnectionState.UPDATING;
                        G.connectionStateMutableLiveData.postValue(ConnectionState.UPDATING);

                        txtIgap.setText(R.string.updating);
                        txtIgap.setTypeface(typeface, Typeface.BOLD);
                    }
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

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //mLeftDrawerLayout.toggle();
        return false;
    }

    /**
     * set drawer info
     *
     * @param updateFromServer if is set true send request to sever for get own info
     */
    private void setDrawerInfo(boolean updateFromServer) {

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

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (G.onBackPressedWebView != null) {
            if (G.onBackPressedWebView.onBack()) {
                return;
            }
        }

        if (G.onBackPressedExplorer != null) {
            if (G.onBackPressedExplorer.onBack()) {
                return;
            }
        } else if (G.onBackPressedChat != null) {
            if (G.onBackPressedChat.onBack()) {
                return;
            }
        }

        if (onBackPressedListener != null) {
            onBackPressedListener.doBack();
        }

        super.onBackPressed();
        if (G.fragmentManager != null && G.fragmentManager.getBackStackEntryCount() < 1) {
            if (!this.isFinishing()) {
                resume();
            }
        }
        designLayout(chatLayoutMode.none);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("bagi", "ActivityMain:onResume:start");

        resume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Log.d("bagi", "ActivityMain:onResume:end");
    }

    public void resume() {
        /**
         * after change language in ActivitySetting this part refresh Activity main
         */
        G.onRefreshActivity = new OnRefreshActivity() {
            @Override
            public void refresh(String changeLanguag) {

                G.isUpdateNotificaionColorMain = false;
                G.isUpdateNotificaionColorChannel = false;
                G.isUpdateNotificaionColorGroup = false;
                G.isUpdateNotificaionColorChat = false;
                G.isUpdateNotificaionCall = false;

                new HelperFragment().removeAll(false);

                ActivityMain.this.recreate();

            }
        };

        designLayout(chatLayoutMode.none);


        if (contentLoading != null) {
            AppUtils.setProgresColler(contentLoading);
        }

        if (G.isInCall) {
            findViewById(R.id.am_ll_strip_call).setVisibility(View.VISIBLE);

            ActivityCallViewModel.txtTimerMain = findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivityMain.this, ActivityCall.class));
                }
            });

            G.iCallFinishMain = new ICallFinish() {
                @Override
                public void onFinish() {
                    try {

                        findViewById(R.id.am_ll_strip_call).setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        } else {
            findViewById(R.id.am_ll_strip_call).setVisibility(View.GONE);
        }


        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));


        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponseRoomList(this);
        G.onClientCondition = this;
        G.onUserInfoMyClient = this;
        G.onMapRegisterStateMain = this;
        G.onUnreadChange = this;
        G.onPayment = this;


        try {
            startService(new Intent(this, ServiceContact.class));
        } catch (Exception e) {
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

        } else {
            HelperUrl.getLinkinfo(intent, ActivityMain.this);
        }
        getIntent().setData(null);
        setDrawerInfo(false);

        ActivityMain.setMediaLayout();

        if (G.isPassCode) {
            iconLock.setVisibility(View.VISIBLE);

            if (isLock) {
                iconLock.setText(getResources().getString(R.string.md_igap_lock));
            } else {
                iconLock.setText(getResources().getString(R.string.md_igap_lock_open_outline));
            }
        } else {
            iconLock.setVisibility(View.GONE);
        }

        onFinance(G.isMplActive, G.isWalletActive);

    }

    private void enterPassword() {

        closeDrawer();
        Intent intent = new Intent(ActivityMain.this, ActivityEnterPassCode.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isNeedToRegister) {
            return;
        }

        AppUtils.updateBadgeOnly(getRealm(), -1);

        G.onUnreadChange = null;

        if (mViewPager != null && mViewPager.getAdapter() != null) {

            try {

                FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();

                if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentMain) {

                    FragmentMain fm = (FragmentMain) adapter.getItem(mViewPager.getCurrentItem());
                    G.selectedTabInMainActivity = fm.mainType.toString();
                } else if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentCall) {

                    G.selectedTabInMainActivity = adapter.getItem(mViewPager.getCurrentItem()).getClass().getName();
                } else if (adapter.getItem(mViewPager.getCurrentItem()) instanceof DiscoveryFragment) {

                    G.selectedTabInMainActivity = adapter.getItem(mViewPager.getCurrentItem()).getClass().getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (state) {
            startAnimationLocation();
        } else {
            stopAnimationLocation();
        }
    }

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

    }

    //******* GroupAvatar and ChannelAvatar

    @Override
    public void onAvatarAddError() {

    }

    private void check(final long userId) {
        if (G.userLogin) {
            FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    check(userId);
                }
            }, 1000);
        }

    }

    public void setImage() {
        avatarHandler.getAvatar(new ParamWithAvatarType(imgNavImage, G.userId).avatarSize(R.dimen.dp100).avatarType(AvatarHandler.AvatarType.USER).showMain());

        /*G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
            @Override
            public void onChangePhoto(final String imagePath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imagePath == null || !new File(imagePath).exists()) {
                            //Realm realm1 = Realm.getDefaultInstance();
                            imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), userInfo.getUserInfo().getInitials(), userInfo.getUserInfo().getColor()));
                            //realm1.close();
                        } else {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgNavImage);
                        }
                    }
                });
            }

            @Override
            public void onChangeInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        };*/
    }

    @Override
    public void onUserInfoMyClient() {
        setImage();
    }


    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //empty
    }

    //*****************************************************************************************************************************

    @Override
    public void onMessageReceive(final long roomId, final String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();
                if (room != null && realmRoomMessage != null) {
                    /**
                     * client checked  (room.getUnreadCount() <= 1)  because in HelperMessageResponse unreadCount++
                     */
                    if (room.getUnreadCount() <= 1) {
                        realmRoomMessage.setFutureMessageId(realmRoomMessage.getMessageId());
                        room.setFirstUnreadMessage(realmRoomMessage);
                    }
                }
            }
        });
        realm.close();
        for (Fragment f : pages) {
            if (f instanceof FragmentMain) {
                FragmentMain mainFragment = (FragmentMain) f;
                switch (mainFragment.mainType) {
                    case all:
                        mainFragment.onAction(MainAction.downScrool);
                        break;
                    case chat:
                        if (roomType == ProtoGlobal.Room.Type.CHAT) {
                            mainFragment.onAction(MainAction.downScrool);
                        }
                        break;
                    case group:
                        if (roomType == ProtoGlobal.Room.Type.GROUP) {
                            mainFragment.onAction(MainAction.downScrool);
                        }
                        break;
                    case channel:
                        if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
                            mainFragment.onAction(MainAction.downScrool);
                        }
                        break;
                }
            }
        }

        /**
         * don't send update status for own message
         */
        if (roomMessage.getAuthor().getUser() != null && roomMessage.getAuthor().getUser().getUserId() != userId) {
            // user has received the message, so I make a new delivered update status request
            if (roomType == ProtoGlobal.Room.Type.CHAT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            } else if (roomType == ProtoGlobal.Room.Type.GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            }
        }
    }

    @Override
    public void onMessageFailed(final long roomId, RealmRoomMessage roomMessage) {
        //empty
    }

    //************************
    @Override
    public void onClientCondition() {

        notifySubFragmentForCondition();
    }

    @Override
    public void onClientConditionError() {
        notifySubFragmentForCondition();
    }

    private void notifySubFragmentForCondition() {
        for (Fragment f : pages) {
            if (f instanceof FragmentMain) {
                ((FragmentMain) f).onAction(MainAction.clinetCondition);
            }
        }
    }

    public void lockNavigation() {

    }

    //*************************************************************

    public void openNavigation() {

    }

    public void designLayout(final chatLayoutMode mode) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.twoPaneMode) {
                    if (frameFragmentContainer != null) {
                        if (frameFragmentContainer.getChildCount() == 0) {
                            if (frameFragmentBack != null) {
                                frameFragmentBack.setVisibility(View.GONE);
                            }
                        } else disableSwipe = frameFragmentContainer.getChildCount() == 1;
                    } else {
                        if (frameFragmentBack != null) {
                            frameFragmentBack.setVisibility(View.GONE);
                        }
                    }

                    if (G.isLandscape) {
                        setWeight(frameChatContainer, 2);
                        setWeight(frameMainContainer, 1);
                        openNavigation();
                    } else {

                        if (mode == chatLayoutMode.show) {
                            setWeight(frameChatContainer, 1);
                            setWeight(frameMainContainer, 0);
                            lockNavigation();
                        } else if (mode == chatLayoutMode.hide) {
                            setWeight(frameChatContainer, 0);
                            setWeight(frameMainContainer, 1);
                            openNavigation();
                        } else {
                            if (frameChatContainer.getChildCount() > 0) {
                                setWeight(frameChatContainer, 1);
                                setWeight(frameMainContainer, 0);
                                lockNavigation();
                            } else {
                                setWeight(frameChatContainer, 0);
                                setWeight(frameMainContainer, 1);
                                openNavigation();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        oldTime = System.currentTimeMillis();
        G.onPayment = null;
    }

    /**
     *
     * bottom navigation new message badge counter
     * unReadCount get user all unread message count
     * change badge color if color = 0 get default badge color
     * badge counter for other bottom navigation item should add listener to OnBottomNavigationBadge
     *
     * */

    @Override
    public void onChange() {

        int unReadCount = RealmRoom.getAllUnreadCount();

        bottomNavigation.setOnBottomNavigationBadge(new OnBottomNavigationBadge() {
            @Override
            public int callCount() {
                return 0;
            }

            @Override
            public int messageCount() {
                return unReadCount;
            }

            @Override
            public int badgeColor() {
                return G.context.getResources().getColor(R.color.red);
            }
        });

        Log.i("aabolfazl", "onChange: " + RealmRoom.getAllUnreadCount());
    }

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
    public void onFinance(final boolean mplActive, final boolean walletActive) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (mplActive) {
//                    findViewById(R.id.lm_ll_payment).setVisibility(View.VISIBLE);
//                } else {
//                    findViewById(R.id.lm_ll_payment).setVisibility(View.GONE);
//                }
//
//                if (walletActive) {
//                    findViewById(R.id.lm_ll_wallet).setVisibility(View.VISIBLE);
//                } else {
//                    findViewById(R.id.lm_ll_wallet).setVisibility(View.GONE);
//                }

//                if (!G.isMplActive && !G.isWalletActive) {
//                    findViewById(R.id.lm_view_Line).setVisibility(View.GONE);
//                } else {
//                    findViewById(R.id.lm_view_Line).setVisibility(View.VISIBLE);
//                }

            }
        });
    }

    @Override
    public void setRefreshBalance() {
        try {
            getUserCredit();
        } catch (Exception e) {
        }
    }

    public void getUserCredit() {

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
    }

    @Override
    public void receivedMessage(int id, Object... message) {

        switch (id) {
            case EventManager.ON_ACCESS_TOKEN_RECIVE:
                int response = (int) message[0];
                switch (response) {
                    case socketMessages.SUCCESS:
                        new android.os.Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getUserCredit();
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


            new MaterialDialog.Builder(ActivityMain.this)
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
                    } catch (Exception ee) {
                    }


                }
            }).show();

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

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return pages.get(i);
        }

        @Override
        public int getCount() {
            return pages.size();
        }
    }
}
