package com.iGap.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.RoomsAdapter;
import com.iGap.adapter.items.ChatItem;
import com.iGap.fragments.FragmentNewGroup;
import com.iGap.fragments.RegisteredContactsFragment;
import com.iGap.helper.ServiceContact;
import com.iGap.interface_package.OnChatClearMessageResponse;
import com.iGap.interface_package.OnChatDelete;
import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.interface_package.OnClientGetRoomListResponse;
import com.iGap.interface_package.OnClientGetRoomResponse;
import com.iGap.interface_package.OnConnectionChangeState;
import com.iGap.interface_package.OnFileDownloadResponse;
import com.iGap.interface_package.OnUserInfoResponse;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.flowingdrawer.FlowingView;
import com.iGap.libs.flowingdrawer.LeftDrawerLayout;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.MyAppBarLayout;
import com.iGap.module.OnComplete;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.module.SoftKeyboard;
import com.iGap.module.StructChatInfo;
import com.iGap.module.StructMessageAttachment;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestClientGetRoomList;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;

import java.lang.reflect.Field;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ActivityMain extends ActivityEnhanced implements OnComplete, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnFileDownloadResponse, OnUserInfoResponse {

    public static LeftDrawerLayout mLeftDrawerLayout;
    private RecyclerView recyclerView;
    private RoomsAdapter<ChatItem> mAdapter;
    private ArcMenu arcMenu;
    private SearchView btnSearch;

    public static boolean isMenuButtonAddShown = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        G.helperNotificationAndBadge.cancelNotification();

        G.onFileDownloadResponse = this;
        G.onUserInfoResponse = this;
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
                                mAdapter.add(new ChatItem().setInfo(StructChatInfo.convert(builder)));
                            }
                        });
                    }
                }
            }
        };

        initComponent();
        initRecycleView();
        initFloatingButtonCreateNew();
        initDrawerMenu();

        Contacts.FillRealmInviteFriend();
    }

    /**
     * init floating menu drawer
     */
    private void initDrawerMenu() {

        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        FragmentManager fm = getSupportFragmentManager();
        FragmentDrawerMenu mMenuFragment = (FragmentDrawerMenu) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new FragmentDrawerMenu()).commit();
        }

        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
    }

    private void initComponent() {
        final Button btnMenu = (Button) findViewById(R.id.cl_btn_menu);
        btnMenu.setTypeface(G.flaticon);
        RippleView rippleMenu = (RippleView) findViewById(R.id.cl_ripple_menu);
        final Button btnBackSearch = (Button) findViewById(R.id.cl_btn_backSearch);
        btnBackSearch.setTypeface(G.flaticon);

        btnSearch = (SearchView) findViewById(R.id.cl_btn_search);

        final TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);
        txtIgap.setTypeface(G.neuroplp);

        if (G.connectionState == Config.ConnectionState.WAITING_FOR_NETWORK) {
            txtIgap.setText("Waiting For Network...");
            txtIgap.setTypeface(null);
            // txtIgap.setTextSize(16);
        } else if (G.connectionState == Config.ConnectionState.CONNECTING) {
            txtIgap.setText("Connecting...");
            txtIgap.setTypeface(null);
            // txtIgap.setTextSize(16);
        } else if (G.connectionState == Config.ConnectionState.UPDATING) {
            txtIgap.setText("Updating...");
            txtIgap.setTypeface(null);
            // txtIgap.setTextSize(16);
        } else {
            txtIgap.setText("iGap");
            txtIgap.setTypeface(G.neuroplp);
            // txtIgap.setTextSize(20);
        }
        if (G.connectionState != null) {
            Log.i("XXX", "G.connectionState  : " + G.connectionState.toString());
        } else {
            Log.i("XXX", "G.connectionState is null");
        }

        G.onConnectionChangeState = new OnConnectionChangeState() {
            @Override
            public void onChangeState(final Config.ConnectionState connectionState) {
                Log.i("XXX", "onChangeState  : " + connectionState.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectionState == Config.ConnectionState.WAITING_FOR_NETWORK) {
                            txtIgap.setText("Waiting For Network...");
                            txtIgap.setTypeface(null);
                            //  txtIgap.setTextSize(16);
                        } else if (connectionState == Config.ConnectionState.CONNECTING) {
                            txtIgap.setText("Connecting...");
                            txtIgap.setTypeface(null);
                            // txtIgap.setTextSize(16);
                        } else if (connectionState == Config.ConnectionState.UPDATING) {
                            txtIgap.setText("Updating...");
                            txtIgap.setTypeface(null);
                            //  txtIgap.setTextSize(16);
                        } else {
                            txtIgap.setText("iGap");
                            txtIgap.setTypeface(G.neuroplp);
                            //   txtIgap.setTextSize(20);
                        }
                    }
                });
            }
        };

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arcMenu.setVisibility(View.GONE);
                txtIgap.setVisibility(View.GONE);
                btnBackSearch.setVisibility(View.VISIBLE);
                btnMenu.setVisibility(View.GONE);
            }
        });

        btnSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                txtIgap.setVisibility(View.VISIBLE);
                arcMenu.setVisibility(View.VISIBLE);
                btnBackSearch.setVisibility(View.GONE);
                btnMenu.setVisibility(View.VISIBLE);
                return false;
            }
        });

        ViewGroup root = (ViewGroup) findViewById(R.id.fragmentContainer);
        InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arcMenu.setVisibility(View.VISIBLE);
                        if (btnSearch.getQuery().toString().length() > 0) {
                            btnSearch.setIconified(false);
                            txtIgap.setVisibility(View.GONE);
                            btnBackSearch.setVisibility(View.VISIBLE);

                        } else {
                            btnSearch.setIconified(true);
                            txtIgap.setVisibility(View.VISIBLE);
                            btnBackSearch.setVisibility(View.GONE);
                            btnMenu.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arcMenu.setVisibility(View.GONE);
                        txtIgap.setVisibility(View.GONE);
                        btnBackSearch.setVisibility(View.VISIBLE);
                        btnMenu.setVisibility(View.GONE);

                    }
                });
            }
        });

        try {
            Field mDrawable = SearchView.class.getDeclaredField("mSearchHintIcon");
            mDrawable.setAccessible(true);
            Drawable drawable = (Drawable) mDrawable.get(btnSearch);
            drawable.setBounds(0, 0, -5, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }


        final EditText searchBox = ((EditText) btnSearch.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchBox.setTextColor(getResources().getColor(R.color.white));

        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                btnSearch.onActionViewCollapsed();
                txtIgap.setVisibility(View.VISIBLE);
                arcMenu.setVisibility(View.VISIBLE);
                mLeftDrawerLayout.toggle();
            }

        });

//        btnMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btnSearch.onActionViewCollapsed();
//                txtIgap.setVisibility(View.VISIBLE);
//                arcMenu.setVisibility(View.VISIBLE);
//                mLeftDrawerLayout.toggle();
//            }
//        });


        btnBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSearch.setIconified(true);
                txtIgap.setVisibility(View.VISIBLE);
                btnBackSearch.setVisibility(View.GONE);
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

                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();

                arcMenu.toggleMenu();

//                isMenuButtonAddShown = true;
//

            }
        });

        FloatingActionButton btnCreateNewGroup = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_group);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewGroup");
                fragment.setArguments(bundle);
                ActivityMain.this.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                arcMenu.toggleMenu();
            }
        });

        FloatingActionButton btnCreateNewChannel = (FloatingActionButton) findViewById(R.id.ac_fab_crate_new_channel);
        btnCreateNewChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Fragment fragment = FragmentNewGroup.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle);
                ActivityMain.this.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                arcMenu.toggleMenu();

            }
        });

    }

    private void initRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        // remove notifying fancy animation
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);
        mAdapter = new RoomsAdapter<>();
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
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityMain.this);
        recyclerView.setLayoutManager(mLayoutManager);
        // set behavior to RecyclerView
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior(mLayoutManager, mAdapter));
        recyclerView.setLayoutParams(params);
        recyclerView.setAdapter(mAdapter);

        MyAppBarLayout appBarLayout = (MyAppBarLayout) findViewById(R.id.appBarLayout);
        final LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar);
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
        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<ChatItem>() {
            @Override
            public boolean filter(ChatItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return !item.mInfo.chatTitle.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        btnSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                mAdapter.filter(s);
                mAdapter.notifyDataSetChanged();
                return false;
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
                realm.copyToRealmOrUpdate(RealmRoom.convert(room, realm));
            }
        });
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mLeftDrawerLayout.toggle();
        return false;
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
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", chatId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", chatId).findFirst();

                        if (realmRoom.getLastMessageId() != -1) {
                            Log.i("CLI1", "CLEAR RoomId : " + chatId + "  ||  realmRoom.getLastMessageId() : " + realmRoom.getLastMessageId());
                            element.setClearId(realmRoom.getLastMessageId());

                            G.clearMessagesUtil.clearMessages(chatId, realmRoom.getLastMessageId());
                        }

                        RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", chatId).findAll();
                        for (RealmChatHistory chatHistory : realmChatHistories) {
                            RealmRoomMessage roomMessage = chatHistory.getRoomMessage();
                            if (roomMessage != null) {
                                // delete chat history message
                                chatHistory.getRoomMessage().deleteFromRealm();
                            }
                        }

                        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", chatId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessageId(0);
                            room.setLastMessageTime(0);
                            room.setLastMessage("");

                            realm.copyToRealmOrUpdate(room);
                        }
                        // finally delete whole chat history
                        realmChatHistories.deleteAllFromRealm();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mAdapter != null) {
                                    mAdapter.updateChat(chatId, convertToChatItem(chatId));
                                }
                            }
                        });
                    }
                });

                element.removeChangeListeners();
                realm.close();
            }
        });
    }

    private void deleteChat(final ChatItem item) {
        G.onChatDelete = new OnChatDelete() {
            @Override
            public void onChatDelete(long roomId) {
                Log.i(ActivityMain.class.getSimpleName(), "chat delete response > " + roomId);
            }

            @Override
            public void onChatDeleteError(int majorCode, int minorCode) {

                if (majorCode == 218 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 218 - CHAT_DELETE_BAD_PAYLOAD
                            //Invalid roomId

                        }
                    });
                } else if (majorCode == 219) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 219 - CHAT_DELETE_INTERNAL_SERVER_ERROR
                            //Invalid roomId

                        }
                    });
                } else if (majorCode == 220) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 220 - CHAT_DELETE_FORBIDDEN
                            //Invalid roomId

                        }
                    });
                }
            }
        };
        Log.i("RRR", "onChatDelete 0 start delete");
        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", item.getInfo().chatId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {
                        if (realm.where(RealmOfflineDelete.class).equalTo("offlineDelete", item.getInfo().chatId).findFirst() == null) {
                            RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class);
                            realmOfflineDelete.setId(System.nanoTime());
                            realmOfflineDelete.setOfflineDelete(item.getInfo().chatId);

                            element.getOfflineDeleted().add(realmOfflineDelete);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.remove(mAdapter.getPosition(item));
                                }
                            });

                            realm.where(RealmRoom.class).equalTo("id", item.getInfo().chatId).findFirst().deleteFromRealm();
                            realm.where(RealmChatHistory.class).equalTo("roomId", item.getInfo().chatId).findAll().deleteAllFromRealm();

                            new RequestChatDelete().chatDelete(item.getInfo().chatId);
                        }
                    }
                });


                element.removeChangeListeners();
                realm.close();
            }
        });
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

    //TODO [Saeed Mozaffari] [2016-10-05 9:47 AM] - in execute Transaction for realmAvatar
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

                        mAdapter.clear();

                        for (final ProtoGlobal.Room room : roomList) { //TODO [Saeed Mozaffari] [2016-09-07 9:56 AM] - manage mute state
                            putChatToDatabase(room);

                            final ChatItem chatItem = new ChatItem();
                            StructChatInfo info = new StructChatInfo();
                            info.unreadMessagesCount = room.getUnreadCount();
                            info.chatId = room.getId();
                            info.chatTitle = room.getTitle();
                            info.initials = room.getInitials();
                            info.unreadMessagesCount = room.getUnreadCount();
                            info.readOnly = room.getReadOnly();
                            switch (room.getType()) {
                                case CHAT:
                                    info.chatType = RoomType.CHAT;
                                    Realm realm = Realm.getDefaultInstance();
                                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", room.getChatRoom().getPeer().getId()).findFirst();
                                    info.avatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getAvatar()) : new StructMessageAttachment();
                                    realm.close();
                                    info.ownerId = room.getChatRoom().getPeer().getId();
                                    break;
                                case CHANNEL:
                                    info.chatType = RoomType.CHANNEL;
                                    info.memberCount = room.getChannelRoom().getParticipantsCountLabel();
                                    info.description = room.getChannelRoom().getDescription();
                                    info.avatarCount = room.getChannelRoom().getAvatarCount();
                                    info.avatar = StructMessageAttachment.convert(room.getChannelRoom().getAvatar());
                                    break;
                                case GROUP:
                                    info.chatType = RoomType.GROUP;
                                    info.memberCount = room.getGroupRoom().getParticipantsCountLabel();
                                    info.description = room.getGroupRoom().getDescription();
                                    info.avatarCount = room.getGroupRoom().getAvatarCount();
                                    info.avatar = StructMessageAttachment.convert(room.getGroupRoom().getAvatar());
                                    break;
                            }

                            info.lastMessageTime = room.getLastMessage().getUpdateTime();
                            info.lastmessage = room.getLastMessage().getMessage();
                            Log.i("BBBA", "Server unreadMessagesCount : " + room.getUnreadCount());
                            info.lastMessageStatus = room.getLastMessage().getStatus().toString();
                            info.color = room.getColor();
                            info.muteNotification = false; // TODO: 9/14/2016 [Alireza Eskandarpour Shoferi] vaghti server mute ro implement kard inja get kon

                            // create item from info

                            chatItem.setInfo(info);
                            chatItem.setComplete(ActivityMain.this);


                        }
                        loadLocalChatList();
                    }
                });
            }
        };

        testIsSecure();
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
            info.readOnly = realmRoom.getReadOnly();
            switch (realmRoom.getType()) {
                case CHAT:
                    info.chatType = RoomType.CHAT;
                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", realmRoom.getChatRoom().getPeerId()).findFirst();
                    info.avatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getAvatar()) : new StructMessageAttachment();
                    info.ownerId = realmRoom.getChatRoom().getPeerId();
                    break;
                case CHANNEL:
                    info.chatType = RoomType.CHANNEL;
                    info.memberCount = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    info.description = realmRoom.getChannelRoom().getDescription();
                    info.avatarCount = realmRoom.getChannelRoom().getAvatarCount();
                    info.avatar = StructMessageAttachment.convert(realmRoom.getChannelRoom().getAvatar());
                    break;
                case GROUP:
                    info.chatType = RoomType.GROUP;
                    info.memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    info.description = realmRoom.getGroupRoom().getDescription();
                    info.avatarCount = realmRoom.getGroupRoom().getAvatarCount();
                    info.avatar = StructMessageAttachment.convert(realmRoom.getGroupRoom().getAvatar());
                    break;
            }
            info.color = realmRoom.getColor();
            info.lastmessage = realmRoom.getLastMessage();
            info.lastMessageTime = realmRoom.getLastMessageTime();
            info.lastMessageStatus = realmRoom.getLastMessageStatus();
            RealmRoomMessage lastMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", realmRoom.getLastMessageId()).findFirst();
            if (lastMessage != null) {
                info.lastmessage = lastMessage.getMessage();
                info.lastMessageTime = lastMessage.getUpdateTime();
                info.lastMessageSenderIsMe = lastMessage.isSenderMe();
                info.lastMessageStatus = lastMessage.getStatus();
            }
            info.muteNotification = realmRoom.getMute(); // FIXME

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
    protected void onResume() {
        super.onResume();

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        // adapter may be null because it's initializing async
        if (mAdapter != null) {
            mAdapter.clear();
            // check if new rooms exist, add to adapter
            // loadLocalChatList();
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

        startService(new Intent(this, ServiceContact.class));
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
        chatInfo.initials = room.getInitials();
        chatInfo.lastMessageTime = room.getLastMessageTime(); //TODO [Saeed Mozaffari] [2016-10-03 5:38 PM] -  see this code later for avoid from multiple calling lastMessageTime and lastMessage and lastMessageStatus
        chatInfo.lastmessage = room.getLastMessage();
        chatInfo.lastMessageStatus = room.getLastMessageStatus();
        chatInfo.readOnly = room.getReadOnly();
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", room.getLastMessageId()).findFirst();
        if (roomMessage != null) {
            chatInfo.lastMessageTime = roomMessage.getUpdateTime();
            chatInfo.lastmessage = roomMessage.getMessage();
            chatInfo.lastMessageStatus = roomMessage.getStatus();
            chatInfo.lastMessageSenderIsMe = roomMessage.isSenderMe();
        }

        chatInfo.chatType = room.getType();
        switch (room.getType()) {
            case CHAT:
                chatInfo.memberCount = "1";
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", room.getChatRoom().getPeerId()).findFirst();
                chatInfo.avatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getAvatar()) : null;
                chatInfo.ownerId = room.getChatRoom().getPeerId();
                break;
            case GROUP:
                chatInfo.memberCount = room.getGroupRoom().getParticipantsCountLabel();
                chatInfo.description = room.getGroupRoom().getDescription();
                chatInfo.avatarCount = room.getGroupRoom().getAvatarCount();
                chatInfo.avatar = StructMessageAttachment.convert(room.getGroupRoom().getAvatar());
                break;
            case CHANNEL:
                chatInfo.memberCount = room.getChannelRoom().getParticipantsCountLabel();
                chatInfo.description = room.getChannelRoom().getDescription();
                chatInfo.avatarCount = room.getChannelRoom().getAvatarCount();
                chatInfo.avatar = StructMessageAttachment.convert(room.getChannelRoom().getAvatar());
                break;
        }
        chatInfo.muteNotification = room.getMute();
        chatInfo.unreadMessagesCount = room.getUnreadCount();
        chatInfo.color = room.getColor();

        chatItem.mInfo = chatInfo;
        chatItem.mComplete = ActivityMain.this;

        realm.close();

        return chatItem;
    }

    @Override
    public void onFileDownload(final String token, final int offset, final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // if thumbnail
                if (selector != ProtoFileDownload.FileDownload.Selector.FILE) {
                    mAdapter.updateThumbnail(token);
                } else {
                    // else file
                    mAdapter.updateDownloadFields(token, progress, offset);
                }
            }
        });
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
    }

    @Override
    public void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        // TODO
    }

    @Override
    public void onMessageReceive(final long roomId, String message, String messageType, final ProtoGlobal.RoomMessage roomMessage) {
        // I'm not in the room, so I have to add 1 to the unread messages count

        Realm realm = Realm.getDefaultInstance();
        if (roomMessage.getUserId() != realm.where(RealmUserInfo.class).findFirst().getUserId()) {
            //if another account not send this message , and really i'm recipient not sender update unread count
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                    if (room != null) {
                        final int updatedUnreadCount = room.getUnreadCount() + 1;
                        room.setUnreadCount(updatedUnreadCount);
                        realm.copyToRealmOrUpdate(room);
                    }
                }
            });
        }
        realm.close();


        if (mAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateChat(roomId, convertToChatItem(roomId));
                }
            });
        }
    }

    @Override
    public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        // empty
    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, ProtoResponse.Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                mAdapter.updateChatAvatar(user.getId(), StructMessageAttachment.convert(realm.where(RealmRegisteredInfo.class).equalTo("id", user.getId()).findFirst().getAvatar()));
                realm.close();
            }
        });
    }
}
