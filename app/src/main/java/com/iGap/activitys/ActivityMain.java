package com.iGap.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.ChatsFastAdapter;
import com.iGap.adapter.items.ChatItem;
import com.iGap.fragments.ContactsFragment;
import com.iGap.helper.HelperProtoBuilder;
import com.iGap.helper.HelperRealm;
import com.iGap.interface_package.IActionClick;
import com.iGap.interface_package.IOpenDrawer;
import com.iGap.interface_package.OnChatClearMessageResponse;
import com.iGap.interface_package.OnChatDelete;
import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.interface_package.OnClientGetRoomListResponse;
import com.iGap.interface_package.OnClientGetRoomResponse;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.flowingdrawer.FlowingView;
import com.iGap.libs.flowingdrawer.LeftDrawerLayout;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructChatInfo;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestClientGetRoomList;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ActivityMain extends ActivityEnhanced implements IOpenDrawer, IActionClick, OnComplete, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse {

    public static LeftDrawerLayout mLeftDrawerLayout;
    private RecyclerView recyclerView;
    private ChatsFastAdapter<ChatItem> mAdapter;
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
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(ProtoGlobal.Room room, final ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
                if (G.currentActivity == ActivityMain.this) {
                    if (mAdapter != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.add(new ChatItem().setInfo(HelperProtoBuilder.convert(builder)));
                            }
                        });
                    }
                }
            }
        };

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

                Fragment fragment = ContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();

                arcMenu.toggleMenu();

//                isMenuButtonAddShown = true;
//
//                if (!mLeftDrawerLayout.isShownMenu()) {
//                    mLeftDrawerLayout.toggle(Utils.dpToPx(getApplicationContext(), R.dimen.dp280));
//                }
            }
        });

        FloatingActionButton btnCreateNewGroup = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_group);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arcMenu.toggleMenu();
            }
        });

        FloatingActionButton btnCreateNewChannel = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_channel);
        btnCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arcMenu.toggleMenu();
            }
        });

    }

    private void initRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        mAdapter = new ChatsFastAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<ChatItem>() {
            @Override
            public boolean onClick(View v, IAdapter<ChatItem> adapter, ChatItem item, int position) {
                if (ActivityMain.isMenuButtonAddShown) {
                    item.mComplete.complete(true, "closeMenuButton", "");
                } else {
                    Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
                    intent.putExtra("RoomId", item.mInfo.chatId);
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


    private void muteNotification(final ChatItem item) {
        Realm realm = Realm.getDefaultInstance();

        item.mInfo.muteNotification = !item.mInfo.muteNotification;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmRoom.class).equalTo("id", item.getInfo().chatId).findFirst().setMute(item.mInfo.muteNotification);
            }
        });
        mAdapter.notifyAdapterItemChanged(mAdapter.getAdapterPosition(item));

        realm.close();
    }

    private void clearHistory(ChatItem item) {
        final ChatItem chatInfo = mAdapter.getAdapterItem(mAdapter.getPosition(item));
        final long chatId = chatInfo.mInfo.chatId;

        // make request for clearing messages
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", chatId).findAll();
        long lastMessageId = HelperRealm.findLastMessageId(realmChatHistories);
        if (lastMessageId != -1) {
            G.clearMessagesUtil.clearMessages(chatId, lastMessageId);
        }
        realm.close();
    }

    private void deleteChat(final ChatItem item) {
        G.onChatDelete = new OnChatDelete() {
            @Override
            public void onChatDelete(long roomId) {
                Log.i("RRR", "onChatDelete 1");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(mAdapter.getPosition(item));
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.where(RealmRoom.class).equalTo("id", item.getInfo().chatId).findFirst().deleteFromRealm();
                                realm.where(RealmChatHistory.class).equalTo("roomId", item.getInfo().chatId).findAll().deleteAllFromRealm();
                            }
                        });
                        realm.close();
                    }
                });

                Log.i("RRR", "onChatDelete 2");

//                for (ChatItem chatItem : mAdapter.getAdapterItems()) {
//                    Log.i("RRR", "onChatDelete 2");
//                    if (chatItem.getInfo().chatId == roomId) {
//                        Log.i("RRR", "onChatDelete 3");
//                        mAdapter.remove(position);
//                        Log.i("RRR", "onChatDelete 4");
//                        break;
//                    }
//                }
            }
        };
        Log.i("RRR", "onChatDelete 0 start delete");
        new RequestChatDelete().chatDelete(item.getInfo().chatId);
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
                muteNotification(item);
                break;
            case "txtClearHistory":
                clearHistory(item);
                break;
            case "txtDeleteChat":
                deleteChat(item);
                break;
        }
    }

    // FIXME: 9/6/2016 [Alireza Eskandarpour Shoferi] not to be on handler, but for fixing securing for testing purposes
    // TODO ghable pak kardan, request ro bear jaye jaee ke invoke kardi
    private void testIsSecure() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure && G.userLogin) {
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

                        // creating new struct for each room and add them to adapter

                        Realm realm = Realm.getDefaultInstance();
                        for (final ProtoGlobal.Room room : roomList) { //TODO [Saeed Mozaffari] [2016-09-07 9:56 AM] - manage mute state
                            if (realm.where(RealmRoom.class).equalTo("id", room.getId()).findFirst() == null) {
                                putChatToDatabase(room);

                                final ChatItem chatItem = new ChatItem();
                                StructChatInfo info = new StructChatInfo();
                                info.unreadMessagesCount = room.getUnreadCount();
                                info.chatId = room.getId();
                                info.chatTitle = room.getTitle();
                                info.initials = room.getInitials();
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
                                info.color = room.getColor();
                                info.muteNotification = false; // FIXME
                                info.imageSource = ""; // FIXME

                                // create item from info

                                Log.i("XXX", "UserContactsGetListResponse setInitials : " + room.getInitials());
                                Log.i("XXX", "UserContactsGetListResponse setColor : " + room.getColor());

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

//                        StructChatInfo a = new StructChatInfo();
//                        a.unreadMessagesCount = 5256;
//                        a.chatId = "123";
//                        a.chatTitle = "mehdi hosiny";
//                        a.chatType = RoomType.GROUP;
//                        a.color = "#ff3131";
//                        a.memberCount = 122 + "";
//                        a.lastMessageTime = "10:21";
//                        a.lastmessage = "how are you jhjh hjh jhhhh";
//                        a.muteNotification = true;
//                        a.imageSource = "";
//                        mAdapter.add(new ChatItem().setInfo(a).setComplete(ActivityMain.this));
//
//                        StructChatInfo a1 = new StructChatInfo();
//                        a1.unreadMessagesCount = 5256;
//                        a1.chatId = "123";
//                        a1.chatTitle = "mehdi hosiny";
//                        a1.chatType = RoomType.GROUP;
//                        a1.color = "#ff3131";
//                        a1.memberCount = 122 + "";
//                        a1.lastMessageTime = "10:21";
//                        a1.lastmessage = "how are you jhjh hjh jhhhh";
//                        a1.muteNotification = true;
//                        a1.imageSource = "";
//                        mAdapter.add(new ChatItem().setInfo(a1).setComplete(ActivityMain.this));
//
//
//                        StructChatInfo a2 = new StructChatInfo();
//                        a2.unreadMessagesCount = 5256;
//                        a2.chatId = "123";
//                        a2.chatTitle = "mehdi hosiny";
//                        a2.chatType = RoomType.GROUP;
//                        a2.color = "#ff3131";
//                        a2.memberCount = 122 + "";
//                        a2.lastMessageTime = "10:21";
//                        a2.lastmessage = "how are you jhjh hjh jhhhh";
//                        a2.muteNotification = true;
//                        a2.imageSource = "";
//                        mAdapter.add(new ChatItem().setInfo(a2).setComplete(ActivityMain.this));
//
//                        //===
//                        StructChatInfo c = new StructChatInfo();
//                        c.unreadMessagesCount = 5256;
//                        c.chatId = "123";
//                        c.chatTitle = "mehdi hosiny";
//                        c.chatType = RoomType.GROUP;
//                        c.color = "#ff3131";
//                        c.memberCount = 122 + "";
//                        c.lastMessageTime = "10:21";
//                        c.lastmessage = "how are you jhjh hjh jhhhh";
//                        c.muteNotification = true;
//                        c.imageSource = "";
//                        mAdapter.add(new ChatItem().setInfo(c).setComplete(ActivityMain.this));
//
//                        StructChatInfo c1 = new StructChatInfo();
//                        c1.unreadMessagesCount = 325515;
//                        c1.chatId = "123";
//                        c1.chatTitle = "Valerie";
//                        c1.chatType = RoomType.CHAT;
//                        c1.color = "#5c9dff";
//                        c1.lastMessageTime = "10:21";
//                        c1.lastmessage = "Valeri is typing...";
//                        c1.muteNotification = false;
//                        c1.imageSource = "";
//                        mAdapter.add(new ChatItem().setInfo(c1).setComplete(ActivityMain.this));
//
//
//                        StructChatInfo c2 = new StructChatInfo();
//                        c2.unreadMessagesCount = 823;
//                        c2.chatId = "123";
//                        c2.chatTitle = "ali";
//                        c2.memberCount = "12k";
//                        c2.chatType = RoomType.CHANNEL;
//                        c2.color = "#f1d900";
//                        c2.lastMessageTime = "2:45";
//                        c2.lastmessage = "where are you";
//                        c2.muteNotification = false;
//                        c2.imageSource = R.mipmap.d + "";
//                        mAdapter.add(new ChatItem().setInfo(c2).setComplete(ActivityMain.this));
//
//                        StructChatInfo c3 = new StructChatInfo();
//                        c3.unreadMessagesCount = 65;
//                        c3.chatId = "123";
//                        c3.chatTitle = "hiwa";
//                        c3.chatType = RoomType.CHAT;
//                        c3.color = "#f75cff";
//                        c3.lastMessageTime = "21:45";
//                        c3.lastmessage = "iz typing how are you";
//                        c3.muteNotification = true;
//                        c3.imageSource = R.mipmap.h + "";
//                        mAdapter.add(new ChatItem().setInfo(c3).setComplete(ActivityMain.this));
//
//
//                        StructChatInfo c4 = new StructChatInfo();
//                        c4.unreadMessagesCount = 0;
//                        c4.chatId = "123";
//                        c4.chatTitle = "has";
//                        c4.chatType = RoomType.GROUP;
//                        c4.color = "#4fb559";
//                        c4.lastMessageTime = "21:30";
//                        c4.lastmessage = "go to link";
//                        c4.muteNotification = false;
//                        c4.imageSource = "";
//                        mAdapter.add(new ChatItem().setInfo(c4).setComplete(ActivityMain.this));
//
//                        StructChatInfo c5 = new StructChatInfo();
//                        c5.unreadMessagesCount = 50;
//                        c5.chatId = "123";
//                        c5.chatTitle = "has";
//                        c5.chatType = RoomType.CHANNEL;
//                        c5.color = "#f26d7d";
//                        c5.lastMessageTime = "21:30";
//                        c5.lastmessage = "go to link";
//                        c5.muteNotification = false;
//                        c5.imageSource = R.mipmap.e + "";
//                        mAdapter.add(new ChatItem().setInfo(c5).setComplete(ActivityMain.this));
//
//                        StructChatInfo c6 = new StructChatInfo();
//                        c6.unreadMessagesCount = 0;
//                        c6.chatId = "123";
//                        c6.chatTitle = "hasan";
//                        c6.chatType = RoomType.GROUP;
//                        c6.color = "#ff8a00";
//                        c6.lastMessageTime = "21:30";
//                        c6.lastmessage = "go to link";
//                        c6.muteNotification = false;
//                        c6.imageSource = R.mipmap.c + "";
//                        mAdapter.add(new ChatItem().setInfo(c6).setComplete(ActivityMain.this));
//
//                        StructChatInfo c7 = new StructChatInfo();
//                        c7.unreadMessagesCount = 55;
//                        c7.chatId = "123";
//                        c7.chatTitle = "sorosh";
//                        c7.chatType = RoomType.CHAT;
//                        c7.color = "#47dfff";
//                        c7.lastMessageTime = "21:30";
//                        c7.lastmessage = "go to link";
//                        c7.muteNotification = false;
//                        c7.imageSource = R.mipmap.g + "";
//                        mAdapter.add(new ChatItem().setInfo(c7).setComplete(ActivityMain.this));
                    }
                });
            }
        };

        testIsSecure();

        /*ArrayList<StructChatInfo> list = new ArrayList<>();

        StructChatInfo c = new StructChatInfo();
        c.unreadMessagesCount = 5256;
        c.chatId = "user";
        c.chatTitle = "mehdi hosiny";
        c.chatType = MyType.ChatType.groupChat;
        c.color = "#ff3131";
        c.memberCount = 122 + "";
        c.lastMessageTime = "10:21";
        c.lastmessage = "how are you jhjh hjh jhhhh";
        c.muteNotification = true;
        c.imageSource = "";
        list.add(c);


        StructChatInfo c1 = new StructChatInfo();
        c1.unreadMessagesCount = 325515;
        c1.chatId = "user1";
        c1.chatTitle = "Valerie";
        c1.chatType = MyType.ChatType.singleChat;
        c1.color = "#5c9dff";
        c1.lastMessageTime = "10:21";
        c1.lastmessage = "Valeri is typing...";
        c1.muteNotification = false;
        c1.imageSource = "";
        list.add(c1);


        StructChatInfo c2 = new StructChatInfo();
        c2.unreadMessagesCount = 823;
        c2.chatId = "user2";
        c2.chatTitle = "ali";
        c2.memberCount = "12k";
        c2.chatType = MyType.ChatType.channel;
        c2.color = "#f1d900";
        c2.lastMessageTime = "2:45";
        c2.lastmessage = "where are you";
        c2.muteNotification = false;
        c2.imageSource = R.mipmap.d + "";
        list.add(c2);

        StructChatInfo c3 = new StructChatInfo();
        c3.unreadMessagesCount = 65;
        c3.chatId = "user3";
        c3.chatTitle = "hiwa";
        c3.chatType = MyType.ChatType.singleChat;
        c3.color = "#f75cff";
        c3.lastMessageTime = "21:45";
        c3.lastmessage = "iz typing how are you";
        c3.muteNotification = true;
        c3.imageSource = R.mipmap.h + "";
        list.add(c3);


        StructChatInfo c4 = new StructChatInfo();
        c4.unreadMessagesCount = 0;
        c4.chatId = "user4";
        c4.chatTitle = "has";
        c4.chatType = MyType.ChatType.groupChat;
        c4.color = "#4fb559";
        c4.lastMessageTime = "21:30";
        c4.lastmessage = "go to link";
        c4.muteNotification = false;
        c4.imageSource = "";
        list.add(c4);

        StructChatInfo c5 = new StructChatInfo();
        c5.unreadMessagesCount = 50;
        c5.chatId = "user5";
        c5.chatTitle = "has";
        c5.chatType = MyType.ChatType.channel;
        c5.color = "#f26d7d";
        c5.lastMessageTime = "21:30";
        c5.lastmessage = "go to link";
        c5.muteNotification = false;
        c5.imageSource = R.mipmap.e + "";
        list.add(c5);

        StructChatInfo c6 = new StructChatInfo();
        c6.unreadMessagesCount = 0;
        c6.chatId = "user6";
        c6.chatTitle = "hasan";
        c6.chatType = MyType.ChatType.groupChat;
        c6.color = "#ff8a00";
        c6.lastMessageTime = "21:30";
        c6.lastmessage = "go to link";
        c6.muteNotification = false;
        c6.imageSource = R.mipmap.c + "";
        list.add(c6);

        StructChatInfo c7 = new StructChatInfo();
        c7.unreadMessagesCount = 55;
        c7.chatId = "user7";
        c7.chatTitle = "sorosh";
        c7.chatType = MyType.ChatType.singleChat;
        c7.color = "#47dfff";
        c7.lastMessageTime = "21:30";
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
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAllSorted("lastMessageTime", Sort.DESCENDING)) {
            final ChatItem chatItem = new ChatItem();
            StructChatInfo info = new StructChatInfo();
            info.unreadMessagesCount = realmRoom.getUnreadCount();
            info.chatId = realmRoom.getId();
            info.chatTitle = realmRoom.getTitle();
            info.initials = realmRoom.getInitials();
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
            info.color = realmRoom.getColor();
            RealmRoomMessage lastMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", realmRoom.getLastMessageId()).findFirst();
            if (lastMessage != null) {
                info.lastMessageTime = lastMessage.getUpdateTime();
                info.lastmessage = lastMessage.getMessage();
            }
            info.muteNotification = realmRoom.getMute(); // FIXME
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
        Log.i(ActivityMain.class.getSimpleName(), "onOpenDrawer");
    }

    @Override
    public void onCloseDrawer() {
        Log.i(ActivityMain.class.getSimpleName(), "onCloseDrawer");
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
        // close drawer and replace contacts fragment with main activity layout
        Fragment fragment = ContactsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "Contacts");
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
        mLeftDrawerLayout.closeDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        // adapter may be null because it's initializing async
        if (mAdapter != null) {
            mAdapter.clear();
            // check if new rooms exist, add to adapter
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RealmRoom> rooms = realm.where(RealmRoom.class).findAllSorted("lastMessageTime", Sort.DESCENDING);
                    for (final RealmRoom room : rooms) {
                        mAdapter.add(convertToChatItem(room.getId()));
                    }
                }
            });

            realm.close();
        }
    }

    /**
     * convert RealmRoom to ChatItem. needed for adding items to adapter.
     *
     * @param roomId room id
     * @return ChatItem
     */
    private ChatItem convertToChatItem(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        ChatItem chatItem = new ChatItem();
        StructChatInfo chatInfo = new StructChatInfo();
        chatInfo.chatId = room.getId();
        chatInfo.chatTitle = room.getTitle();
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", room.getLastMessageId()).findFirst();
        if (roomMessage != null) {
            chatInfo.lastMessageTime = roomMessage.getUpdateTime();
            chatInfo.lastmessage = roomMessage.getMessage();
        }
        chatInfo.chatType = room.getType();
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
        chatInfo.unreadMessagesCount = room.getUnreadCount();
        chatInfo.color = room.getColor();

        chatItem.mInfo = chatInfo;
        chatItem.mComplete = ActivityMain.this;

        realm.close();

        return chatItem;
    }

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            arcMenu.toggleMenu();
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, long clearId, final ProtoResponse.Response response) {
        Log.i(ActivityMain.class.getSimpleName(), "onChatClearMessage called");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.updateChat(roomId, convertToChatItem(roomId));
                }
            }
        });
    }

    @Override
    public void onMessageUpdated(long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        // TODO
    }

    @Override
    public void onReceiveChatMessage(String message, String messageType, final ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        // I'm not in the room, so I have to add 1 to the unread messages count
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomMessage.getRoomId()).findFirst();
                if (room != null) {
                    final int updatedUnreadCount = room.getUnreadCount() + 1;
                    room.setUnreadCount(updatedUnreadCount);
                    realm.copyToRealmOrUpdate(room);
                }
            }
        });
        realm.close();

        if (mAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // mAdapter.updateChat(roomMessage.getRoomId(), convertToChatItem(roomMessage.getRoomId()));
                    mAdapter.clear();
                    // check if new rooms exist, add to adapter
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<RealmRoom> rooms = realm.where(RealmRoom.class).findAllSorted("lastMessageTime", Sort.DESCENDING);
                            for (final RealmRoom room : rooms) {
                                mAdapter.add(convertToChatItem(room.getId()));
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, int statusVersion) {
        // TODO
    }
}
