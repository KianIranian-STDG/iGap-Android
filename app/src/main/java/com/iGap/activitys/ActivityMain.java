package com.iGap.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.iGap.adapter.items.ChatItem;
import com.iGap.helper.HelperRealm;
import com.iGap.interface_package.IActionClick;
import com.iGap.interface_package.IOpenDrawer;
import com.iGap.interface_package.OnChatClearMessageResponse;
import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.interface_package.OnClientGetRoomListResponse;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.flowingdrawer.FlowingView;
import com.iGap.libs.flowingdrawer.LeftDrawerLayout;
import com.iGap.libs.flowingdrawer.ResizeWidthAnimation;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructChatInfo;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestClientGetRoomList;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityMain extends ActivityEnhanced implements IOpenDrawer, IActionClick, OnComplete, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse {

    private LeftDrawerLayout mLeftDrawerLayout;
    private RecyclerView recyclerView;
    private FastItemAdapter<ChatItem> mAdapter;
    private FloatingActionButton floatingActionButton;
    private ArcMenu arcMenu;

    public static boolean isMenuButtonAddShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        initRecycleView();
        initFloatingButtonCreateNew();
        initDrawerMenu();
        initComponent();
    }

    FlowingView mFlowingView;
    FragmentDrawerMenu mMenuFragment;

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

                if (!mLeftDrawerLayout.isShownMenu()) {
                    mLeftDrawerLayout.toggle(Utils.getWindowWidth(ActivityMain.this));
                }
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
        recyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mAdapter = new FastItemAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<ChatItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ChatItem> adapter, ChatItem item, int position) {
                if (ActivityMain.isMenuButtonAddShown) {
                    item.mComplete.complete(true, "closeMenuButton", "");
                } else {
                    Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
//                    intent.putExtra("ChatType", mInfo.chatType);
                    intent.putExtra("ChatType", "CHAT");
                    intent.putExtra("ContactID", item.mInfo.chatId);
                    intent.putExtra("IsMute", item.mInfo.muteNotification);
                    intent.putExtra("OwnerShip", item.mInfo.ownerShip);
                    intent.putExtra("ContactName", item.mInfo.chatTitle);
                    intent.putExtra("MemberCount", item.mInfo.memberCount);
                    intent.putExtra("LastSeen", Long.parseLong(item.mInfo.lastSeen));
                    intent.putExtra("RoomId", Long.parseLong(item.mInfo.chatId));

                    startActivity(intent);
                }
                return false;
            }
        });

        mAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<ChatItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<ChatItem> adapter, final ChatItem item, final int position) {
                if (ActivityMain.isMenuButtonAddShown) {
                    item.mComplete.complete(true, "closeMenuButton", "");
                } else {
                    MyDialog.showDialogMenuItemRooms(ActivityMain.this, item.mInfo.chatType, item.mInfo.muteNotification, new OnComplete() {
                        @Override
                        public void complete(boolean result, String messageOne, String MessageTow) {
                            onSelectRoomMenu(messageOne, position, item);
                        }
                    });
                }
                return true;
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

        loadLocalChatList();
        getChatsList();
    }

    /**
     * put fetched chat to database
     *
     * @param room ProtoGlobal.Room
     */
    private void putChatToDatabase(final ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(HelperRealm.convert(room));
            }
        });
        realm.close();
    }


    private void muteNotification(int position, ChatItem item) {
        Log.e("fff", " txtMuteNotification " + position);

        item.mInfo.muteNotification = !item.mInfo.muteNotification;
    }

    private void clearHistory(int position) {
        Log.e("fff", " txtClearHistory " + position);
        final ChatItem chatInfo = mAdapter.getAdapterItem(position);
        final String chatId = chatInfo.mInfo.chatId;

        // make request for clearing messages
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", Long.parseLong(chatId)).findAll();
        long lastMessageId = HelperRealm.findLastMessageId(realmChatHistories);
        if (lastMessageId != -1) {
            G.clearMessagesUtil.clearMessages(Long.parseLong(chatId), lastMessageId);
        }
        realm.close();
    }

    private void deleteChat(int position) {
        Log.e("fff", " txtDeleteChat " + position);
    }

    /**
     * on select room menu
     *
     * @param message  message text
     * @param position position dfdfdfdf
     */
    private void onSelectRoomMenu(String message, int position, ChatItem item) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(position, item);
                break;
            case "txtClearHistory":
                clearHistory(position);
                break;
            case "txtDeleteChat":
                deleteChat(position);
                break;
        }
    }

    // FIXME: 9/6/2016 [Alireza Eskandarpour Shoferi] not to be on handler, but for fixing securing for testing purposes
    // TODO ghable pak kardan, request ro bear jaye jaee ke invoke kardi
    private void testIsSecure() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure) {
                    new RequestClientGetRoomList().clientGetRoomList();
                } else {
                    testIsSecure();
                }
            }
        }, 1000);
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
                    }
                });
                // creating new struct for each room and add them to adapter

                Realm realm = Realm.getDefaultInstance();
                for (ProtoGlobal.Room room : roomList) {
                    if (realm.where(RealmRoom.class).equalTo("id", room.getId()).findFirst() == null) {
                        putChatToDatabase(room);

                        final ChatItem chatItem = new ChatItem();
                        StructChatInfo info = new StructChatInfo();
                        info.unreadMessag = room.getUnreadCount();
                        info.chatId = Long.toString(room.getId());
                        info.chatTitle = room.getTitle();
                        switch (room.getType()) {
                            case CHAT:
                                info.chatType = RoomType.CHAT;
                                info.memberCount = "1";
                                break;
                            case CHANNEL:
                                info.chatType = RoomType.CHANNEL;
                                info.memberCount = room.getChannelRoom().getParticipantsCountLabel();
                                break;
                            case GROUP:
                                info.chatType = RoomType.GROUP;
                                info.memberCount = room.getGroupRoom().getParticipantsCountLabel();
                                break;
                        }
                        info.viewDistanceColor = room.getColor();
                        info.lastSeen = Long.toString(System.currentTimeMillis()); // FIXME
                        info.lastmessage = "lastMessage"; // FIXME
                        info.muteNotification = false; // FIXME
                        info.imageSource = ""; // FIXME

                        // create item from info
                        chatItem.setInfo(info);
                        chatItem.setComplete(ActivityMain.this);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.add(chatItem);
                            }
                        });
                    }
                }

                // FIXME clear later
                // fake data set
                /*StructChatInfo c = new StructChatInfo();
                c.unreadMessag = 5256;
                c.chatId = "123";
                c.chatTitle = "mehdi hosiny";
                c.chatType = MyType.ChatType.groupChat;
                c.viewDistanceColor = "#ff3131";
                c.memberCount = 122 + "";
                c.lastSeen = "10:21";
                c.lastmessage = "how are you jhjh hjh jhhhh";
                c.muteNotification = true;
                c.imageSource = "";
                mAdapter.add(new ChatItem().setInfo(c).setComplete(ActivityMain.this));

                StructChatInfo c1 = new StructChatInfo();
                c1.unreadMessag = 325515;
                c1.chatId = "123";
                c1.chatTitle = "Valerie";
                c1.chatType = MyType.ChatType.singleChat;
                c1.viewDistanceColor = "#5c9dff";
                c1.lastSeen = "10:21";
                c1.lastmessage = "Valeri is typing...";
                c1.muteNotification = false;
                c1.imageSource = "";
                mAdapter.add(new ChatItem().setInfo(c1).setComplete(ActivityMain.this));


                StructChatInfo c2 = new StructChatInfo();
                c2.unreadMessag = 823;
                c2.chatId = "123";
                c2.chatTitle = "ali";
                c2.memberCount = "12k";
                c2.chatType = MyType.ChatType.channel;
                c2.viewDistanceColor = "#f1d900";
                c2.lastSeen = "2:45";
                c2.lastmessage = "where are you";
                c2.muteNotification = false;
                c2.imageSource = R.mipmap.d + "";
                mAdapter.add(new ChatItem().setInfo(c2).setComplete(ActivityMain.this));

                StructChatInfo c3 = new StructChatInfo();
                c3.unreadMessag = 65;
                c3.chatId = "123";
                c3.chatTitle = "hiwa";
                c3.chatType = MyType.ChatType.singleChat;
                c3.viewDistanceColor = "#f75cff";
                c3.lastSeen = "21:45";
                c3.lastmessage = "iz typing how are you";
                c3.muteNotification = true;
                c3.imageSource = R.mipmap.h + "";
                mAdapter.add(new ChatItem().setInfo(c3).setComplete(ActivityMain.this));


                StructChatInfo c4 = new StructChatInfo();
                c4.unreadMessag = 0;
                c4.chatId = "123";
                c4.chatTitle = "has";
                c4.chatType = MyType.ChatType.groupChat;
                c4.viewDistanceColor = "#4fb559";
                c4.lastSeen = "21:30";
                c4.lastmessage = "go to link";
                c4.muteNotification = false;
                c4.imageSource = "";
                mAdapter.add(new ChatItem().setInfo(c4).setComplete(ActivityMain.this));

                StructChatInfo c5 = new StructChatInfo();
                c5.unreadMessag = 50;
                c5.chatId = "123";
                c5.chatTitle = "has";
                c5.chatType = MyType.ChatType.channel;
                c5.viewDistanceColor = "#f26d7d";
                c5.lastSeen = "21:30";
                c5.lastmessage = "go to link";
                c5.muteNotification = false;
                c5.imageSource = R.mipmap.e + "";
                mAdapter.add(new ChatItem().setInfo(c5).setComplete(ActivityMain.this));

                StructChatInfo c6 = new StructChatInfo();
                c6.unreadMessag = 0;
                c6.chatId = "123";
                c6.chatTitle = "hasan";
                c6.chatType = MyType.ChatType.groupChat;
                c6.viewDistanceColor = "#ff8a00";
                c6.lastSeen = "21:30";
                c6.lastmessage = "go to link";
                c6.muteNotification = false;
                c6.imageSource = R.mipmap.c + "";
                mAdapter.add(new ChatItem().setInfo(c6).setComplete(ActivityMain.this));

                StructChatInfo c7 = new StructChatInfo();
                c7.unreadMessag = 55;
                c7.chatId = "123";
                c7.chatTitle = "sorosh";
                c7.chatType = MyType.ChatType.singleChat;
                c7.viewDistanceColor = "#47dfff";
                c7.lastSeen = "21:30";
                c7.lastmessage = "go to link";
                c7.muteNotification = false;
                c7.imageSource = R.mipmap.g + "";
                mAdapter.add(new ChatItem().setInfo(c7).setComplete(ActivityMain.this));*/
            }
        };

        testIsSecure();

        /*ArrayList<StructChatInfo> list = new ArrayList<>();

        StructChatInfo c = new StructChatInfo();
        c.unreadMessag = 5256;
        c.chatId = "user";
        c.chatTitle = "mehdi hosiny";
        c.chatType = MyType.ChatType.groupChat;
        c.viewDistanceColor = "#ff3131";
        c.memberCount = 122 + "";
        c.lastSeen = "10:21";
        c.lastmessage = "how are you jhjh hjh jhhhh";
        c.muteNotification = true;
        c.imageSource = "";
        list.add(c);


        StructChatInfo c1 = new StructChatInfo();
        c1.unreadMessag = 325515;
        c1.chatId = "user1";
        c1.chatTitle = "Valerie";
        c1.chatType = MyType.ChatType.singleChat;
        c1.viewDistanceColor = "#5c9dff";
        c1.lastSeen = "10:21";
        c1.lastmessage = "Valeri is typing...";
        c1.muteNotification = false;
        c1.imageSource = "";
        list.add(c1);


        StructChatInfo c2 = new StructChatInfo();
        c2.unreadMessag = 823;
        c2.chatId = "user2";
        c2.chatTitle = "ali";
        c2.memberCount = "12k";
        c2.chatType = MyType.ChatType.channel;
        c2.viewDistanceColor = "#f1d900";
        c2.lastSeen = "2:45";
        c2.lastmessage = "where are you";
        c2.muteNotification = false;
        c2.imageSource = R.mipmap.d + "";
        list.add(c2);

        StructChatInfo c3 = new StructChatInfo();
        c3.unreadMessag = 65;
        c3.chatId = "user3";
        c3.chatTitle = "hiwa";
        c3.chatType = MyType.ChatType.singleChat;
        c3.viewDistanceColor = "#f75cff";
        c3.lastSeen = "21:45";
        c3.lastmessage = "iz typing how are you";
        c3.muteNotification = true;
        c3.imageSource = R.mipmap.h + "";
        list.add(c3);


        StructChatInfo c4 = new StructChatInfo();
        c4.unreadMessag = 0;
        c4.chatId = "user4";
        c4.chatTitle = "has";
        c4.chatType = MyType.ChatType.groupChat;
        c4.viewDistanceColor = "#4fb559";
        c4.lastSeen = "21:30";
        c4.lastmessage = "go to link";
        c4.muteNotification = false;
        c4.imageSource = "";
        list.add(c4);

        StructChatInfo c5 = new StructChatInfo();
        c5.unreadMessag = 50;
        c5.chatId = "user5";
        c5.chatTitle = "has";
        c5.chatType = MyType.ChatType.channel;
        c5.viewDistanceColor = "#f26d7d";
        c5.lastSeen = "21:30";
        c5.lastmessage = "go to link";
        c5.muteNotification = false;
        c5.imageSource = R.mipmap.e + "";
        list.add(c5);

        StructChatInfo c6 = new StructChatInfo();
        c6.unreadMessag = 0;
        c6.chatId = "user6";
        c6.chatTitle = "hasan";
        c6.chatType = MyType.ChatType.groupChat;
        c6.viewDistanceColor = "#ff8a00";
        c6.lastSeen = "21:30";
        c6.lastmessage = "go to link";
        c6.muteNotification = false;
        c6.imageSource = R.mipmap.c + "";
        list.add(c6);

        StructChatInfo c7 = new StructChatInfo();
        c7.unreadMessag = 55;
        c7.chatId = "user7";
        c7.chatTitle = "sorosh";
        c7.chatType = MyType.ChatType.singleChat;
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

    private void loadLocalChatList() {
        Realm realm = Realm.getDefaultInstance();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
            final ChatItem chatItem = new ChatItem();
            StructChatInfo info = new StructChatInfo();
            info.unreadMessag = realmRoom.getUnreadCount();
            info.chatId = Long.toString(realmRoom.getId()); //TODO [Saeed Mozaffari] [2016-09-05 4:31 PM] - convert chat id to long
            info.chatTitle = realmRoom.getTitle();
            switch (realmRoom.getType()) {
                case CHAT:
                    info.chatType = RoomType.CHAT;
                    info.memberCount = "1";
                    break;
                case CHANNEL:
                    info.chatType = RoomType.CHANNEL;
                    info.memberCount = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    break;
                case GROUP:
                    info.chatType = RoomType.GROUP;
                    info.memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    break;
            }
            info.viewDistanceColor = realmRoom.getColor();
            info.lastSeen = Long.toString(System.currentTimeMillis()); // FIXME
            info.lastmessage = "lastMessage"; // FIXME
            info.muteNotification = false; // FIXME
            info.imageSource = ""; // FIXME

            chatItem.setInfo(info);
            chatItem.setComplete(ActivityMain.this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(chatItem);
                }
            });
        }

        realm.close();
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

    @Override
    protected void onResume() {
        super.onResume();

        // adapter may be null because it's initializing async
        if (mAdapter != null) {
            // check if new rooms exist, add to adapter
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RealmRoom> rooms = realm.where(RealmRoom.class).findAll();
                    for (final RealmRoom room : rooms) {
                        boolean exists = false;
                        for (ChatItem chat : mAdapter.getAdapterItems()) {
                            if (room.getId() == Long.parseLong(chat.mInfo.chatId)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            mAdapter.add(convertToChatItem(room));
                        }
                    }
                }
            });
            realm.close();
        }
    }

    /**
     * convert RealmRoom to ChatItem. needed for adding items to adapter.
     *
     * @param room RealmRoom
     * @return ChatItem
     */
    private ChatItem convertToChatItem(RealmRoom room) {
        ChatItem chatItem = new ChatItem();
        StructChatInfo chatInfo = new StructChatInfo();
        chatInfo.chatId = Long.toString(room.getId());
        chatInfo.chatTitle = room.getTitle();
        chatInfo.chatType = room.getType();
        chatInfo.imageSource = "";
        chatInfo.lastmessage = "lastMessage";
        chatInfo.lastSeen = "lastSeen";
        switch (room.getType()) {
            case CHANNEL:
                chatInfo.memberCount = room.getChannelRoom().getParticipantsCountLabel();
                break;
            case CHAT:
                chatInfo.memberCount = "1";
                break;
            case GROUP:
                chatInfo.memberCount = room.getGroupRoom().getParticipantsCountLabel();
                break;
        }
        chatInfo.muteNotification = false;
        chatInfo.ownerShip = MyType.OwnerShip.member;
        chatInfo.unreadMessag = room.getUnreadCount();
        chatInfo.viewDistanceColor = room.getColor();

        chatItem.mInfo = chatInfo;
        chatItem.mComplete = ActivityMain.this;

        return chatItem;
    }

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            arcMenu.toggleMenu();
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, long clearId, ProtoResponse.Response response) {
        Log.i(ActivityMain.class.getSimpleName(), "onChatClearMessage called");
    }

    @Override
    public void onMessageUpdated(long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        // TODO
    }

    @Override
    public void onReceiveChatMessage(String message, String messageType, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        // TODO
    }

    @Override
    public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, int statusVersion) {
        // TODO
    }
}
