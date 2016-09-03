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
import com.iGap.adapter.AdapterChatsList;
import com.iGap.helper.HelperRealm;
import com.iGap.interface_package.IActionClick;
import com.iGap.interface_package.IOpenDrawer;
import com.iGap.interface_package.OnClientGetRoomListResponse;
import com.iGap.interface_package.OnUserContactGetList;
import com.iGap.interface_package.OnUserContactImport;
import com.iGap.interface_package.OnUserLogin;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.flowingdrawer.FlowingView;
import com.iGap.libs.flowingdrawer.LeftDrawerLayout;
import com.iGap.libs.flowingdrawer.ResizeWidthAnimation;
import com.iGap.module.ListOfContact;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructContactInfo;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.request.RequestClientGetRoomList;
import com.iGap.request.RequestUserContactsGetList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ActivityMain extends ActivityEnhanced implements IOpenDrawer, IActionClick {

    private LeftDrawerLayout mLeftDrawerLayout;
    private RecyclerView recyclerView;
    private AdapterChatsList mAdapter;
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
    }

    FlowingView mFlowingView;
    FragmentDrawerMenu mMenuFragment;

    public void userLogin() {

//        G.onSecuring = new OnSecuring() {
//            @Override
//            public void onSecure() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        RealmUserInfo userInfo = G.realm.where(RealmUserInfo.class).findFirst();
//                        Log.i("SOC", "Login Start userInfo : " + userInfo);
//                        if (!G.userLogin && userInfo != null) { //  need login //TODO [Saeed Mozaffari] [2016-08-29 11:51 AM] - check for securing
//                            Log.i("SOC", "Login Start userInfo : " + userInfo);
//                            new RequestUserLogin().userLogin(userInfo.getToken());
//                        }
//                    }
//                });
//            }
//        };

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, "User Login!", Toast.LENGTH_SHORT).show();
                        importContact();
                        initRecycleView();
                    }
                });
            }
        };
    }

    private void importContact() {

        G.onContactImport = new OnUserContactImport() {
            @Override
            public void onContactImport() {
                getContactListFromServer();
            }
        };
        ListOfContact.getListOfContact();
    }

    private void getContactListFromServer() {
        G.onUserContactGetList = new OnUserContactGetList() {
            @Override
            public void onContactGetList() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, "Get Contact List!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        new RequestUserContactsGetList().userContactGetList();
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
                mLeftDrawerLayout.toggle(Utils.dpToPx(getApplicationContext(), R.dimen.dp280));
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
                Log.e("ddd", "crate new channel_white");
            }
        });

    }

    private void initRecycleView() {
        ArrayList<StructContactInfo> chats = new ArrayList<>(0);
        recyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mAdapter = new AdapterChatsList(chats, ActivityMain.this, new OnComplete() {
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

        getChatsList();
    }

    /**
     * put fetched chat to database
     *
     * @param room ProtoGlobal.Room
     */
    private void putChatToDatabase(final ProtoGlobal.Room room) {
        G.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(HelperRealm.convert(room));
            }
        });
    }

    private void getChatsList() {
        G.onClientGetRoomListResponse = new OnClientGetRoomListResponse() {
            @Override
            public void onClientGetRoomList(final List<ProtoGlobal.Room> roomList, ProtoResponse.Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityMain.this, "rooms list fetched: " + Integer.toString(roomList.size()), Toast.LENGTH_LONG).show();
                        Log.i(ActivityMain.class.getSimpleName(), "rooms list fetched: " + Integer.toString(roomList.size()));

                        // creating new struct for each room and add them to adapter
                        for (ProtoGlobal.Room room : roomList) {
                            StructContactInfo info = new StructContactInfo();
                            info.unreadMessag = room.getUnreadCount();
                            info.contactID = Long.toString(room.getId());
                            info.contactName = room.getTitle();
                            switch (room.getType()) {
                                case CHAT:
                                    info.contactType = MyType.ChatType.singleChat;
                                    info.memberCount = "1";
                                    break;
                                case CHANNEL:
                                    info.contactType = MyType.ChatType.channel;
                                    info.memberCount = room.getChannelRoom().getParticipantsCountLabel();
                                    break;
                                case GROUP:
                                    info.contactType = MyType.ChatType.groupChat;
                                    info.memberCount = room.getGroupRoom().getParticipantsCountLabel();
                                    break;
                            }
                            info.viewDistanceColor = room.getColor();
                            info.lastSeen = "lastSeen"; // FIXME
                            info.lastmessage = "lastMessage"; // FIXME
                            info.muteNotification = false; // FIXME
                            info.imageSource = ""; // FIXME
                            mAdapter.insert(info);

                            putChatToDatabase(room);
                        }

                        // FIXME clear later
                        // fake data set
                        StructContactInfo c = new StructContactInfo();
                        c.unreadMessag = 5256;
                        c.contactID = "user";
                        c.contactName = "mehdi hosiny";
                        c.contactType = MyType.ChatType.groupChat;
                        c.viewDistanceColor = "#ff3131";
                        c.memberCount = 122 + "";
                        c.lastSeen = "10:21";
                        c.lastmessage = "how are you jhjh hjh jhhhh";
                        c.muteNotification = true;
                        c.imageSource = "";
                        mAdapter.insert(c);

                        StructContactInfo c1 = new StructContactInfo();
                        c1.unreadMessag = 325515;
                        c1.contactID = "user1";
                        c1.contactName = "Valerie";
                        c1.contactType = MyType.ChatType.singleChat;
                        c1.viewDistanceColor = "#5c9dff";
                        c1.lastSeen = "10:21";
                        c1.lastmessage = "Valeri is typing...";
                        c1.muteNotification = false;
                        c1.imageSource = "";
                        mAdapter.insert(c1);


                        StructContactInfo c2 = new StructContactInfo();
                        c2.unreadMessag = 823;
                        c2.contactID = "user2";
                        c2.contactName = "ali";
                        c2.memberCount = "12k";
                        c2.contactType = MyType.ChatType.channel;
                        c2.viewDistanceColor = "#f1d900";
                        c2.lastSeen = "2:45";
                        c2.lastmessage = "where are you";
                        c2.muteNotification = false;
                        c2.imageSource = R.mipmap.d + "";
                        mAdapter.insert(c2);

                        StructContactInfo c3 = new StructContactInfo();
                        c3.unreadMessag = 65;
                        c3.contactID = "user3";
                        c3.contactName = "hiwa";
                        c3.contactType = MyType.ChatType.singleChat;
                        c3.viewDistanceColor = "#f75cff";
                        c3.lastSeen = "21:45";
                        c3.lastmessage = "iz typing how are you";
                        c3.muteNotification = true;
                        c3.imageSource = R.mipmap.h + "";
                        mAdapter.insert(c3);


                        StructContactInfo c4 = new StructContactInfo();
                        c4.unreadMessag = 0;
                        c4.contactID = "user4";
                        c4.contactName = "has";
                        c4.contactType = MyType.ChatType.groupChat;
                        c4.viewDistanceColor = "#4fb559";
                        c4.lastSeen = "21:30";
                        c4.lastmessage = "go to link";
                        c4.muteNotification = false;
                        c4.imageSource = "";
                        mAdapter.insert(c4);

                        StructContactInfo c5 = new StructContactInfo();
                        c5.unreadMessag = 50;
                        c5.contactID = "user5";
                        c5.contactName = "has";
                        c5.contactType = MyType.ChatType.channel;
                        c5.viewDistanceColor = "#f26d7d";
                        c5.lastSeen = "21:30";
                        c5.lastmessage = "go to link";
                        c5.muteNotification = false;
                        c5.imageSource = R.mipmap.e + "";
                        mAdapter.insert(c5);

                        StructContactInfo c6 = new StructContactInfo();
                        c6.unreadMessag = 0;
                        c6.contactID = "user6";
                        c6.contactName = "hasan";
                        c6.contactType = MyType.ChatType.groupChat;
                        c6.viewDistanceColor = "#ff8a00";
                        c6.lastSeen = "21:30";
                        c6.lastmessage = "go to link";
                        c6.muteNotification = false;
                        c6.imageSource = R.mipmap.c + "";
                        mAdapter.insert(c6);

                        StructContactInfo c7 = new StructContactInfo();
                        c7.unreadMessag = 55;
                        c7.contactID = "user7";
                        c7.contactName = "sorosh";
                        c7.contactType = MyType.ChatType.singleChat;
                        c7.viewDistanceColor = "#47dfff";
                        c7.lastSeen = "21:30";
                        c7.lastmessage = "go to link";
                        c7.muteNotification = false;
                        c7.imageSource = R.mipmap.g + "";
                        mAdapter.insert(c7);
                    }
                });
            }
        };

        new RequestClientGetRoomList().clientGetRoomList();

        /*ArrayList<StructContactInfo> list = new ArrayList<>();

        StructContactInfo c = new StructContactInfo();
        c.unreadMessag = 5256;
        c.contactID = "user";
        c.contactName = "mehdi hosiny";
        c.contactType = MyType.ChatType.groupChat;
        c.viewDistanceColor = "#ff3131";
        c.memberCount = 122 + "";
        c.lastSeen = "10:21";
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
        c1.lastSeen = "10:21";
        c1.lastmessage = "Valeri is typing...";
        c1.muteNotification = false;
        c1.imageSource = "";
        list.add(c1);


        StructContactInfo c2 = new StructContactInfo();
        c2.unreadMessag = 823;
        c2.contactID = "user2";
        c2.contactName = "ali";
        c2.memberCount = "12k";
        c2.contactType = MyType.ChatType.channel;
        c2.viewDistanceColor = "#f1d900";
        c2.lastSeen = "2:45";
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
        c3.lastSeen = "21:45";
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
        c4.lastSeen = "21:30";
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
        c5.lastSeen = "21:30";
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
        c6.lastSeen = "21:30";
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
        c7.lastSeen = "21:30";
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

        return list;*/
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
        if (fullWidth) {
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
