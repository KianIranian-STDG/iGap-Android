package com.iGap.activitys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterContact;
import com.iGap.interface_package.IActionClick;
import com.iGap.interface_package.IOpenDrawer;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.flowingdrawer.FlowingView;
import com.iGap.libs.flowingdrawer.LeftDrawerLayout;
import com.iGap.libs.flowingdrawer.ResizeWidthAnimation;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructContactInfo;
import com.iGap.module.Utils;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserLogin;

import java.util.ArrayList;

public class ActivityMain extends ActivityEnhanced implements IOpenDrawer, IActionClick {

    private LeftDrawerLayout mLeftDrawerLayout;
    private RecyclerView recyclerView;
    private AdapterContact mAdapter;
    private FloatingActionButton floatingActionButton;
    private ArcMenu arcMenu;

    public static boolean isMenuButtonAddShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLogin();
        initFloatingButtonCreateNew();
        initDrawerMenu();
        initComponent();
        initRecycleView();
    }

    FlowingView mFlowingView;
    FragmentDrawerMenu mMenuFragment;

    private void userLogin() {

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, "User Login!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        RealmUserInfo userInfo = G.realm.where(RealmUserInfo.class).findFirst();
        if (!G.userLogin && userInfo != null) { //  need login
            new RequestUserLogin().userLogin(userInfo.getToken());
        }
    }

    /**
     * init floating menu drawer
     */
    private void initDrawerMenu() {

        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        mLeftDrawerLayout.setActivityWidth(Utils.getWindowWidth(this));

        mFlowingView = (FlowingView) findViewById(R.id.sv);

        FragmentManager fm = getSupportFragmentManager();

        mMenuFragment = (FragmentDrawerMenu) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new FragmentDrawerMenu()).commit();
        }
        mMenuFragment.setOpenDrawerListener(this);
        mMenuFragment.setActionClickListener(this);
        mMenuFragment.setMaxActivityWidth(Utils.getWindowWidth(this));
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);


        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

    }

    private void initComponent() {

        Button btnMenu = (Button) findViewById(R.id.cl_btn_menu);
        btnMenu.setTypeface(G.fontawesome);

        Button btnSearch = (Button) findViewById(R.id.cl_btn_search);
        btnSearch.setTypeface(G.fontawesome);

        TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);
        txtIgap.setTypeface(G.neuroplp);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("fff", "search");
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLeftDrawerLayout.toggle(Utils.dpToPx(getApplicationContext(),R.dimen.dp280));
            }
        });


    }

    private void initFloatingButtonCreateNew() {


        arcMenu = (ArcMenu) findViewById(R.id.ac_arc_button_add);


        arcMenu.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {

                isMenuButtonAddShown = true;
            }

            @Override
            public void onMenuClosed() {

                isMenuButtonAddShown = false;
            }
        });


        FloatingActionButton btnStartNewChat = (FloatingActionButton) findViewById(R.id.ac_fab_start_new_chat);
        btnStartNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "start new chat");
            }
        });

        FloatingActionButton btnCreateNewGroup = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_group);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "crate new group");
            }
        });

        FloatingActionButton btnCreateNewChannel = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_channel);
        btnCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "crate new channel");
            }
        });

    }

    private void initRecycleView() {

        recyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mAdapter = new AdapterContact(getContactList(), ActivityMain.this, new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {
                if (messageOne.equals("closeMenuButton")) {
                    arcMenu.toggleMenu();
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityMain.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (arcMenu.isMenuOpened())
                    arcMenu.toggleMenu();


                if (dy > 0) {
                    // Scroll Down
                    if (arcMenu.fabMenu.isShown()) {
                        arcMenu.fabMenu.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!arcMenu.fabMenu.isShown()) {
                        arcMenu.fabMenu.show();
                    }
                }
            }
        });


    }


    private ArrayList<StructContactInfo> getContactList() {

        ArrayList<StructContactInfo> list = new ArrayList<>();

        StructContactInfo c = new StructContactInfo();
        c.unreadMessag = 5256;
        c.contactID = "user";
        c.contactName = "mehdi hosiny";
        c.contactType = MyType.ChatType.groupChat;
        c.viewDistanceColor = "#ff3131";
        c.contactTime = "10:21";
        c.lastmessage = "how are you jhjh hjh jhhhh";
        c.muteNotification = true;
        c.imageSource = "";
        list.add(c);


        StructContactInfo c1 = new StructContactInfo();
        c1.unreadMessag = 325515;
        c1.contactID = "user1";
        c1.contactName = "Valerie";
        c1.contactType = MyType.ChatType.singleChat;
        c1.viewDistanceColor = "#5c9dff";
        c1.contactTime = "10:21";
        c1.lastmessage = "Valeri is typing...";
        c1.muteNotification = false;
        c1.imageSource = "";
        list.add(c1);


        StructContactInfo c2 = new StructContactInfo();
        c2.unreadMessag = 823;
        c2.contactID = "user2";
        c2.contactName = "ali";
        c2.contactType = MyType.ChatType.channel;
        c2.viewDistanceColor = "#f1d900";
        c2.contactTime = "2:45";
        c2.lastmessage = "where are you";
        c2.muteNotification = false;
        c2.imageSource = R.mipmap.d + "";
        list.add(c2);

        StructContactInfo c3 = new StructContactInfo();
        c3.unreadMessag = 65;
        c3.contactID = "user3";
        c3.contactName = "hiwa";
        c3.contactType = MyType.ChatType.singleChat;
        c3.viewDistanceColor = "#f75cff";
        c3.contactTime = "21:45";
        c3.lastmessage = "iz typing how are you";
        c3.muteNotification = true;
        c3.imageSource = R.mipmap.h + "";
        list.add(c3);


        StructContactInfo c4 = new StructContactInfo();
        c4.unreadMessag = 0;
        c4.contactID = "user4";
        c4.contactName = "has";
        c4.contactType = MyType.ChatType.groupChat;
        c4.viewDistanceColor = "#4fb559";
        c4.contactTime = "21:30";
        c4.lastmessage = "go to link";
        c4.muteNotification = false;
        c4.imageSource = "";
        list.add(c4);

        StructContactInfo c5 = new StructContactInfo();
        c5.unreadMessag = 50;
        c5.contactID = "user5";
        c5.contactName = "has";
        c5.contactType = MyType.ChatType.channel;
        c5.viewDistanceColor = "#f26d7d";
        c5.contactTime = "21:30";
        c5.lastmessage = "go to link";
        c5.muteNotification = false;
        c5.imageSource = R.mipmap.e + "";
        list.add(c5);

        StructContactInfo c6 = new StructContactInfo();
        c6.unreadMessag = 0;
        c6.contactID = "user6";
        c6.contactName = "hasan";
        c6.contactType = MyType.ChatType.groupChat;
        c6.viewDistanceColor = "#ff8a00";
        c6.contactTime = "21:30";
        c6.lastmessage = "go to link";
        c6.muteNotification = false;
        c6.imageSource = R.mipmap.c + "";
        list.add(c6);

        StructContactInfo c7 = new StructContactInfo();
        c7.unreadMessag = 55;
        c7.contactID = "user7";
        c7.contactName = "sorosh";
        c7.contactType = MyType.ChatType.singleChat;
        c7.viewDistanceColor = "#47dfff";
        c7.contactTime = "21:30";
        c7.lastmessage = "go to link";
        c7.muteNotification = false;
        c7.imageSource = R.mipmap.g + "";
        list.add(c7);

        list.add(c);
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);
        list.add(c7);
        list.add(c);
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);
        list.add(c7);
        list.add(c);
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);
        list.add(c7);
        list.add(c);
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);
        list.add(c7);
        list.add(c);
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);
        list.add(c7);
        list.add(c);
        list.add(c1);
        list.add(c2);
        list.add(c3);
        list.add(c4);
        list.add(c5);
        list.add(c6);
        list.add(c7);

        return list;
    }

    @Override
    public void onBackPressed() {
        if (mLeftDrawerLayout.isShownMenu()) {
            mLeftDrawerLayout.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onOpenDrawer(boolean fullWidth) {
        if (fullWidth){
            // replace new menu fragment
            FragmentManager fm = getSupportFragmentManager();
            ContactsFragmentDrawerMenu sc;
            fm.beginTransaction().replace(R.id.id_container_menu, sc = new ContactsFragmentDrawerMenu()).commit();
            sc.setOpenDrawerListener(ActivityMain.this);
            sc.setMaxActivityWidth(Utils.getWindowWidth(ActivityMain.this));
            mLeftDrawerLayout.setMenuFragment(sc);
        }
    }

    @Override
    public void onCloseDrawer() {
        mFlowingView.downing();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.id_container_menu, mMenuFragment = new FragmentDrawerMenu()).commit();
        mMenuFragment.setActionClickListener(this);
        mMenuFragment.setOpenDrawerListener(this);
        mMenuFragment.setMaxActivityWidth(Utils.getWindowWidth(this));
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
    }

    @Override
    public void onActionSearchClick() {
        RelativeLayout parent = (RelativeLayout) mFlowingView.getParent();

        ResizeWidthAnimation anim = new ResizeWidthAnimation(parent, Utils.getWindowWidth(this));
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                int windowWidth = Utils.getWindowWidth(ActivityMain.this);
                mFlowingView.invalidate(windowWidth);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // replace new menu fragment
                int windowWidth = Utils.getWindowWidth(ActivityMain.this);
                FragmentManager fm = getSupportFragmentManager();
                ContactsFragmentDrawerMenu sc;
                fm.beginTransaction().replace(R.id.id_container_menu, sc = new ContactsFragmentDrawerMenu()).commit();
                sc.setOpenDrawerListener(ActivityMain.this);
                sc.setMaxActivityWidth(windowWidth);
                mLeftDrawerLayout.setMenuFragment(sc);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        parent.startAnimation(anim);
    }

}
