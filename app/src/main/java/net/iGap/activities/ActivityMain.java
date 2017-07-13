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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.fragments.ContactGroupFragment;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentCreateChannel;
import net.iGap.fragments.FragmentIgapSearch;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentNewGroup;
import net.iGap.fragments.RegisteredContactsFragment;
import net.iGap.fragments.SearchFragment;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperCalculateKeepMedia;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperNotificationAndBadge;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ServiceContact;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.IMainFinish;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChatClearMessageResponse;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClientCondition;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnConnectionChangeState;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnRefreshActivity;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnUpdateAvatar;
import net.iGap.interfaces.OnUpdating;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.interfaces.OnVerifyNewDevice;
import net.iGap.interfaces.OpenFragment;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.libs.floatingAddButton.StateChangeListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.LoginActions;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyAppBarLayout;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserSessionLogout;

import static net.iGap.G.context;
import static net.iGap.G.isSendContact;
import static net.iGap.G.userId;
import static net.iGap.R.string.updating;

public class ActivityMain extends ActivityEnhanced implements OnUserInfoMyClient, OnClientGetRoomListResponse, OnChatClearMessageResponse, OnChatUpdateStatusResponse, OnChatSendMessageResponse, OnClientCondition, OnSetActionInRoom, OnGroupAvatarResponse, OnUpdateAvatar, DrawerLayout.DrawerListener {

    public static boolean isMenuButtonAddShown = false;
    LinearLayout mediaLayout;
    MusicPlayer musicPlayer;
    FragmentCall fragmentCall;



    public static MyAppBarLayout appBarLayout;
    private Typeface titleTypeface;
    private SharedPreferences sharedPreferences;
    private ImageView imgNavImage;
    private DrawerLayout drawer;
    public static int currentMainRoomListPosition = 0;
    private int pageDrawer = 0;
    private ProgressBar contentLoading;

    public MainInterface mainActionApp;
    public MainInterface mainActionChat;
    public MainInterface mainActionGroup;
    public MainInterface mainActionChannel;

    public MainInterfaceGetRoomList mainInterfaceGetRoomList;

    public ArcMenu arcMenu;
    FloatingActionButton btnStartNewChat;
    FloatingActionButton btnCreateNewGroup;
    FloatingActionButton btnCreateNewChannel;

    private ViewPager mViewPager;
    private ArrayList<Fragment> pages = new ArrayList<Fragment>();
    SampleFragmentPagerAdapter sampleFragmentPagerAdapter;

    public enum MainAction {
        downScrool, clinetCondition
    }

    public interface MainInterface {
        void onAction(MainAction action);
    }

    public interface MainInterfaceGetRoomList {

        void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, boolean fromLogin);

        void onError(int majorCode, int minorCode);

        void onTimeout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (G.getRealm() != null) {
            G.getRealm().close();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        new HelperGetDataFromOtherApp(getIntent());

        mediaLayout = (LinearLayout) findViewById(R.id.amr_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

        appBarLayout = (MyAppBarLayout) findViewById(R.id.appBarLayout);
        final ViewGroup toolbar = (ViewGroup) findViewById(R.id.rootToolbar);

        appBarLayout.addOnMoveListener(new MyAppBarLayout.OnMoveListener() {
            @Override
            public void onAppBarLayoutMove(AppBarLayout appBarLayout, int verticalOffset, boolean moveUp) {
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

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean isGetContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT, false);
        /**
         * just do this action once
         */
        if (!isGetContactList) {
            try {
                HelperPermision.getContactPermision(ActivityMain.this, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        /**
                         * set G.isSendContact = false to permitted user
                         * for import contacts
                         */
                        G.isSendContact = false;
                        LoginActions.importContact();

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

        G.helperNotificationAndBadge.cancelNotification();
        G.onGroupAvatarResponse = this;
        G.onUpdateAvatar = this;

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
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragmentNewGroup, "newGroup_fragment").commitAllowingStateLoss();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        lockNavigation();
                    }
                });
            }
        };

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);

        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        initComponent();
        connectionState();

        initDrawerMenu();

        boolean keepMedia = sharedPreferences.getBoolean(SHP_SETTING.KEY_KEEP_MEDIA, false);
        if (keepMedia && G.isCalculatKeepMedia) {// if Was selected keep media at 1week
            G.isCalculatKeepMedia = false;
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long last;
                    long currentTime = G.currentTime;
                    long saveTime = sharedPreferences.getLong(SHP_SETTING.KEY_KEEP_MEDIA_TIME, -1);
                    if (saveTime == -1) {
                        last = 7;
                    } else {
                        long oneWeeks = (24L * 60L * 60L * 1000L);

                        long b = currentTime - saveTime;
                        last = b / oneWeeks;
                    }
                    if (last >= 7) {
                        new HelperCalculateKeepMedia().calculateTime();
                    }
                }
            }, 5000);
        }

        G.onVerifyNewDevice = new OnVerifyNewDevice() {
            @Override
            public void verifyNewDevice(String appName, int appId, int appBuildVersion, String appVersion, ProtoGlobal.Platform platform, String platformVersion, ProtoGlobal.Device device, String deviceName, boolean twoStepVerification) {

                final String content = "" + "App name: " + appName + "\n" + "Build version: " + appBuildVersion + "\n" + "App version: " + appVersion + "\n" + "Platform: " + platform + "\n" + "Platform version: " + platformVersion + "\n" + "Device: " + device + "\n" + "Device name: " + deviceName;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (HelperCalander.isLanguagePersian) {
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
    }


    //*******************************************************************************************************************************************

    private void initFloatingButtonCreateNew() {
        arcMenu = (ArcMenu) findViewById(R.id.ac_arc_button_add);
        arcMenu.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
                isMenuButtonAddShown = false;
            }
        });

        btnStartNewChat = (FloatingActionButton) findViewById(R.id.ac_fab_start_new_chat);
        btnStartNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);

                try {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }

                lockNavigation();
            }
        });

        btnCreateNewGroup = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_group);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);

                try {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();

                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
            }
        });

        btnCreateNewChannel = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_channel);
        btnCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                try {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
            }
        });

        arcMenu.fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentStatePagerAdapter adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();
                if (adapter.getItem(mViewPager.getCurrentItem()) instanceof FragmentMain) {

                    FragmentMain fm = (FragmentMain) adapter.getItem(mViewPager.getCurrentItem());
                    switch (fm.mainType) {

                        case all:
                            arcMenu.toggleMenu();
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
            }
        });
    }

    //*******************************************************************************************************************************************

    private void setFabIcon(int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arcMenu.fabMenu.setImageDrawable(getResources().getDrawable(res, context.getTheme()));
        } else {
            arcMenu.fabMenu.setImageDrawable(getResources().getDrawable(res));
        }
    }



    private void initTabStrip() {

        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
        navigationTabStrip.setBackgroundColor(Color.parseColor(G.appBarColor));

        if (HelperCalander.isLanguagePersian) {
            navigationTabStrip.setTitles(getString(R.string.md_phone), getString(R.string.md_channel_icon), getString(R.string.md_users_social_symbol), getString(R.string.md_user_account_box),
                getString(R.string.md_apps));
            navigationTabStrip.setTabIndex(4);
        } else {
            navigationTabStrip.setTitles(getString(R.string.md_apps), getString(R.string.md_user_account_box), getString(R.string.md_users_social_symbol), getString(R.string.md_channel_icon),
                getString(R.string.md_phone));
        }

        navigationTabStrip.setTitleSize(getResources().getDimension(R.dimen.dp20));
        navigationTabStrip.setStripColor(Color.WHITE);

        navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {

            }

            @Override
            public void onEndTabSelected(String title, int index) {

                FragmentStatePagerAdapter adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();

                if (adapter.getItem(index) instanceof FragmentMain) {

                    findViewById(R.id.amr_ripple_search).setVisibility(View.VISIBLE);
                    findViewById(R.id.am_btn_menu).setVisibility(View.GONE);
                    setFabIcon(R.mipmap.plus);
                } else if (adapter.getItem(index) instanceof FragmentCall) {

                    findViewById(R.id.amr_ripple_search).setVisibility(View.GONE);
                    findViewById(R.id.am_btn_menu).setVisibility(View.VISIBLE);
                    setFabIcon(R.drawable.ic_call_black_24dp);
                }

                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }

                arcMenu.fabMenu.show();


            }
        });

        if (getSupportFragmentManager().getFragments() != null) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != null && (fragment instanceof FragmentCall || fragment instanceof FragmentMain)) getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }


        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        pages.add(FragmentMain.newInstance(FragmentMain.MainType.all));

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                fragmentCall = FragmentCall.newInstance(true);

                if (HelperCalander.isLanguagePersian) {
                    pages.add(0, FragmentMain.newInstance(FragmentMain.MainType.chat));
                    pages.add(0, FragmentMain.newInstance(FragmentMain.MainType.group));
                    pages.add(0, FragmentMain.newInstance(FragmentMain.MainType.channel));
                    pages.add(0, fragmentCall);
                } else {
                    pages.add(FragmentMain.newInstance(FragmentMain.MainType.chat));
                    pages.add(FragmentMain.newInstance(FragmentMain.MainType.group));
                    pages.add(FragmentMain.newInstance(FragmentMain.MainType.channel));
                    pages.add(fragmentCall);
                }

                if (HelperCalander.isLanguagePersian) {
                    mViewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), pages));
                    mViewPager.setCurrentItem(pages.size() - 1);
                    mViewPager.setOffscreenPageLimit(pages.size());
                } else {

                    mViewPager.getAdapter().notifyDataSetChanged();
                    mViewPager.setOffscreenPageLimit(pages.size());
                }


            }
        }, 2000);




        sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(), pages);
        mViewPager.setAdapter(sampleFragmentPagerAdapter);

        navigationTabStrip.setViewPager(mViewPager);

        mViewPager.setCurrentItem(0);

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) findViewById(R.id.am_btn_menu);

        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentCall.openDialogMenu();
            }
        });

        if (HelperCalander.isLanguagePersian) {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //    mViewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //    //  navigationTabStrip.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //}
            ViewMaker.setLayoutDirection(mViewPager, View.LAYOUT_DIRECTION_RTL);
        }

    }

    class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> pagesFragment;

        @Override
        public Parcelable saveState() {
            return null;
        }

        SampleFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> pagesFragment) {
            super(fm);
            this.pagesFragment = pagesFragment;
        }

        @Override
        public Fragment getItem(int i) {

            return pagesFragment.get(i);
        }

        @Override
        public int getCount() {
            return pagesFragment.size();
        }
    }

    //******************************************************************************************************************************

    /**
     * send client condition
     */

    @Override
    protected void onStart() {
        super.onStart();
        //RealmRoomMessage.fetchNotDeliveredMessages(new OnActivityMainStart() {
        //    @Override
        //    public void sendDeliveredStatus(RealmRoom room, RealmRoomMessage message) {
        //        G.chatUpdateStatusUtil.sendUpdateStatus(room.getType(), message.getRoomId(), message.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
        //    }
        //});
    }

    /**
     * init  menu drawer
     */

    private void initDrawerMenu() {

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here

                //if (arcMenu.isMenuOpened()) {
                //    arcMenu.toggleMenu();
                //}

            }
        };

        final ViewGroup drawerButton = (ViewGroup) findViewById(R.id.amr_ripple_menu);
        drawerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        toggle.setDrawerIndicatorEnabled(false);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        setDrawerInfo(true);

        findViewById(R.id.lm_layout_header).setBackgroundColor(Color.parseColor(G.appBarColor));

        final ViewGroup navBackGround = (ViewGroup) findViewById(R.id.lm_layout_user_picture);
        navBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.closeDrawer(GravityCompat.START);
                pageDrawer = 1;
            }
        });

        TextView txtCloud = (TextView) findViewById(R.id.lm_txt_cloud);
        txtCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navBackGround.performClick();
            }
        });

        ViewGroup itemNavChat = (ViewGroup) findViewById(R.id.lm_ll_new_chat);
        itemNavChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
                pageDrawer = 2;
                lockNavigation();
            }
        });

        ViewGroup itemNavGroup = (ViewGroup) findViewById(R.id.lm_ll_new_group);
        itemNavGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 3;

                lockNavigation();
            }
        });

        ViewGroup itemNavChanel = (ViewGroup) findViewById(R.id.lm_ll_new_channle);
        itemNavChanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
                pageDrawer = 4;

                lockNavigation();
            }
        });

        ViewGroup igapSearch = (ViewGroup) findViewById(R.id.lm_ll_igap_search);
        igapSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 5;

                lockNavigation();
            }
        });

        ViewGroup itemNavContacts = (ViewGroup) findViewById(R.id.lm_ll_contacts);
        itemNavContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 6;

                lockNavigation();
            }
        });

        ViewGroup itemNavCall = (ViewGroup) findViewById(R.id.lm_ll_call);

        // gone or visible view call
        RealmCallConfig callConfig = G.getRealm().where(RealmCallConfig.class).findFirst();
        if (callConfig != null) {
            if (callConfig.isVoice_calling()) {
                itemNavCall.setVisibility(View.VISIBLE);

                itemNavCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        });

                        pageDrawer = 7;
                    }
                });
            } else {
                itemNavCall.setVisibility(View.GONE);
            }
        } else {
            new RequestSignalingGetConfiguration().signalingGetConfiguration();
        }

        ViewGroup itemNavSend = (ViewGroup) findViewById(R.id.lm_ll_invite_friends);
        itemNavSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 8;
            }
        });
        ViewGroup itemNavSetting = (ViewGroup) findViewById(R.id.lm_ll_setting);
        itemNavSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 9;
            }
        });
        ViewGroup itemQrCode = (ViewGroup) findViewById(R.id.lm_ll_qrCode);
        itemQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 10;
            }
        });

        ViewGroup itemNavOut = (ViewGroup) findViewById(R.id.lm_ll_igap_faq);
        itemNavOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });

                pageDrawer = 11;
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                switch (pageDrawer) {
                    case 1:
                        chatGetRoom(userId);
                        pageDrawer = 0;
                        break;
                    case 2: {
                        final Fragment fragment = RegisteredContactsFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "New Chat");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        pageDrawer = 0;
                        break;
                    }
                    case 3: {
                        FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "NewGroup");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                        pageDrawer = 0;
                        break;
                    }
                    case 4: {
                        FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TYPE", "NewChanel");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer, fragment, "newGroup_fragment").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 5: {
                        final Fragment fragment = FragmentIgapSearch.newInstance();
                        try {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "Search_fragment_igap").commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 6: {
                        Fragment fragment = RegisteredContactsFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("TITLE", "Contacts");
                        fragment.setArguments(bundle);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 7: {
                        Fragment fragment = FragmentCall.newInstance(false);
                        try {
                            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 8: {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net/ I'm waiting for you !");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        pageDrawer = 0;
                        break;
                    }
                    case 9: {
                        try {
                            HelperPermision.getStoragePermision(ActivityMain.this, new OnGetPermission() {
                                @Override
                                public void Allow() {
                                    Intent intent = new Intent(G.context, ActivitySetting.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    //ActivityMain.mLeftDrawerLayout.closeDrawer();
                                }

                                @Override
                                public void deny() {
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        pageDrawer = 0;
                        break;
                    }
                    case 10: {


                        try {
                            HelperPermision.getCameraPermission(ActivityMain.this, new OnGetPermission() {
                                @Override
                                public void Allow() throws IOException {
                                    startActivity(new Intent(ActivityMain.this, ActivityQrCodeNewDevice.class));
                                }

                                @Override
                                public void deny() {
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pageDrawer = 0;
                    }
                    break;

                    case 11: {
                        new MaterialDialog.Builder(ActivityMain.this).title(getResources().getString(R.string.log_out))
                            .content(R.string.content_log_out)
                            .positiveText(getResources().getString(R.string.B_ok))
                            .negativeText(getResources().getString(R.string.B_cancel))
                            .iconRes(R.mipmap.exit_to_app_button)
                            .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    G.onUserSessionLogout = new OnUserSessionLogout() {
                                        @Override
                                        public void onUserSessionLogout() {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    HelperLogout.logout();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                    snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            snack.dismiss();
                                                        }
                                                    });
                                                    snack.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onTimeOut() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                                                    snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            snack.dismiss();
                                                        }
                                                    });
                                                    snack.show();
                                                }
                                            });
                                        }
                                    };
                                    new RequestUserSessionLogout().userSessionLogout();
                                }
                            })
                            .show();
                        pageDrawer = 0;
                    }
                    break;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initComponent() {

        contentLoading = (ProgressBar) findViewById(R.id.loadingContent);
        AppUtils.setProgresColler(contentLoading);

        RippleView rippleSearch = (RippleView) findViewById(R.id.amr_ripple_search);
        rippleSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Fragment fragment = SearchFragment.newInstance();

                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment, "Search_fragment").commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                lockNavigation();
            }
        });

        if (!HelperCalander.isLanguagePersian) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }
    }

    private void connectionState() {
        final TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);

        Typeface typeface = null;
        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
            typeface = titleTypeface;
        }
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
        RealmUserInfo realmUserInfo = G.getRealm().where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            String username = realmUserInfo.getUserInfo().getDisplayName();
            String phoneNumber = realmUserInfo.getUserInfo().getPhoneNumber();

            imgNavImage = (ImageView) findViewById(R.id.lm_imv_user_picture);
            EmojiTextViewE txtNavName = (EmojiTextViewE) findViewById(R.id.lm_txt_user_name);
            TextView txtNavPhone = (TextView) findViewById(R.id.lm_txt_phone_number);
            txtNavName.setText(username);
            txtNavPhone.setText(phoneNumber);

            if (HelperCalander.isLanguagePersian) {
                txtNavPhone.setText(HelperCalander.convertToUnicodeFarsiNumber(txtNavPhone.getText().toString()));
                txtNavName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtNavName.getText().toString()));
            }
            if (updateFromServer) {
                new RequestUserInfo().userInfo(realmUserInfo.getUserId());
            }
            setImage(realmUserInfo.getUserId());
        }
    }

    @Override
    public void onBackPressed() {
        openNavigation();
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("Search_fragment");
        FragmentNewGroup fragmentNeGroup = (FragmentNewGroup) getSupportFragmentManager().findFragmentByTag("newGroup_fragment");
        FragmentCreateChannel fragmentCreateChannel = (FragmentCreateChannel) getSupportFragmentManager().findFragmentByTag("createChannel_fragment");
        ContactGroupFragment fragmentContactGroup = (ContactGroupFragment) getSupportFragmentManager().findFragmentByTag("contactGroup_fragment");
        FragmentIgapSearch fragmentIgapSearch = (FragmentIgapSearch) getSupportFragmentManager().findFragmentByTag("Search_fragment_igap");

        if (fragmentNeGroup != null && fragmentNeGroup.isVisible()) {

            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentNeGroup).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else if (fragmentCreateChannel != null && fragmentCreateChannel.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentCreateChannel).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (fragmentContactGroup != null && fragmentContactGroup.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentContactGroup).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (fragmentIgapSearch != null && fragmentIgapSearch.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(fragmentIgapSearch).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (myFragment != null && myFragment.isVisible()) {
            try {
                getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * after change language in ActivitySetting this part refresh Activity main
         */
        G.onRefreshActivity = new OnRefreshActivity() {
            @Override
            public void refresh(String changeLanguage) {
                ActivityMain.this.recreate();
            }
        };

        if (ActivityCall.isConnected) {

            findViewById(R.id.ac_ll_strip_call).setVisibility(View.VISIBLE);

            ActivityCall.txtTimerMain = (TextView) findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = (TextView) findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            G.iCallFinish = new ICallFinish() {
                @Override
                public void onFinish() {
                    try {
                        findViewById(R.id.ac_ll_strip_call).setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            G.iMainFinish = new IMainFinish() {
                @Override
                public void onFinish() {
                    finish();
                }
            };


        } else {
            findViewById(R.id.ac_ll_strip_call).setVisibility(View.GONE);
        }

        if (drawer != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }
        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.onSetActionInRoom = this;
        G.onClientCondition = this;
        G.onClientGetRoomListResponse = this;

        startService(new Intent(this, ServiceContact.class));

        HelperUrl.getLinkinfo(getIntent(), ActivityMain.this);
        getIntent().setData(null);
        setDrawerInfo(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HelperNotificationAndBadge.updateBadgeOnly();
    }

    @Override
    public void onChatClearMessage(final long roomId, long clearId, final ProtoResponse.Response response) {
        //empty
    }

    @Override
    public void onChatUpdateStatus(final long roomId, long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {
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
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null && realmRoom.isValid() && !realmRoom.isDeleted() && realmRoom.getType() != null) {
                    String action = HelperGetAction.getAction(roomId, realmRoom.getType(), clientAction);
                    realmRoom.setActionState(action, userId);
                }
            }
        });
        realm.close();
    }

    //******* GroupAvatar and ChannelAvatar

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

    }

    @Override
    public void onAvatarAddError() {

    }

    @Override
    public void onUpdateAvatar(final long roomId) {

    }

    public void setImage(long userId) {
        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imgNavImage);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });

        G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
            @Override
            public void onChangePhoto(final String imagePath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imagePath == null || !new File(imagePath).exists()) {
                            Realm realm1 = Realm.getDefaultInstance();
                            RealmUserInfo realmUserInfo = realm1.where(RealmUserInfo.class).findFirst();
                            imgNavImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgNavImage.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
                            realm1.close();
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
        };
    }

    @Override
    public void onUserInfoMyClient(ProtoGlobal.RegisteredUser user, String identity) {
        setImage(user.getId());
    }

    private void chatGetRoom(final long peerId) {
        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {

            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //getActivity().getSupportFragmentManager().popBackStack();
        } else {

            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    Intent intent = new Intent(context, ActivityChat.class);
                    intent.putExtra("peerId", peerId);
                    intent.putExtra("RoomId", roomId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    G.onChatGetRoom = null;
                }

                @Override
                public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                }

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {

                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    //*****************************************************************************************************************************

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //empty
    }

    @Override
    public void onMessageReceive(final long roomId, final String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {

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

        switch (roomType) {

            case CHAT:
                if (mainActionChat != null) {
                    mainActionChat.onAction(MainAction.downScrool);
                }
                break;
            case GROUP:
                if (mainActionGroup != null) {
                    mainActionGroup.onAction(MainAction.downScrool);
                }
                break;
            case CHANNEL:
                if (mainActionChannel != null) {
                    mainActionChannel.onAction(MainAction.downScrool);
                }
                break;
        }

        if (mainActionApp != null) {
            mainActionApp.onAction(MainAction.downScrool);
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

        if (mainActionApp != null) {
            mainActionApp.onAction(MainAction.clinetCondition);
        }

        if (mainActionChat != null) {
            mainActionChat.onAction(MainAction.clinetCondition);
        }

        if (mainActionGroup != null) {
            mainActionGroup.onAction(MainAction.clinetCondition);
        }

        if (mainActionChannel != null) {
            mainActionChannel.onAction(MainAction.clinetCondition);
        }
    }

    //************************

    @Override
    public void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, boolean fromLogin) {

        if (mainInterfaceGetRoomList != null) {
            mainInterfaceGetRoomList.onClientGetRoomList(roomList, response, fromLogin);
        }
    }

    @Override
    public void onError(int majorCode, int minorCode) {

        if (mainInterfaceGetRoomList != null) {
            mainInterfaceGetRoomList.onError(majorCode, minorCode);
        }
    }

    @Override
    public void onTimeout() {

        if (mainInterfaceGetRoomList != null) {
            mainInterfaceGetRoomList.onTimeout();
        }
    }

    //*************************************************************

    public void lockNavigation() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void openNavigation() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}
