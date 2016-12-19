package com.iGap.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.RoomsAdapter;
import com.iGap.adapter.items.RoomItem;
import com.iGap.fragments.FragmentNewGroup;
import com.iGap.fragments.RegisteredContactsFragment;
import com.iGap.fragments.SearchFragment;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperGetAction;
import com.iGap.helper.HelperGetDataFromOtherApp;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.ServiceContact;
import com.iGap.interfaces.OnActivityMainStart;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChannelDelete;
import com.iGap.interfaces.OnChannelLeft;
import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.interfaces.OnChatDelete;
import com.iGap.interfaces.OnChatSendMessageResponse;
import com.iGap.interfaces.OnChatUpdateStatusResponse;
import com.iGap.interfaces.OnClientGetRoomListResponse;
import com.iGap.interfaces.OnClientGetRoomResponse;
import com.iGap.interfaces.OnConnectionChangeState;
import com.iGap.interfaces.OnDraftMessage;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnGroupDelete;
import com.iGap.interfaces.OnGroupLeft;
import com.iGap.interfaces.OnRefreshActivity;
import com.iGap.interfaces.OnSetActionInRoom;
import com.iGap.interfaces.OnUpdateAvatar;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OpenFragment;
import com.iGap.libs.floatingAddButton.ArcMenu;
import com.iGap.libs.floatingAddButton.StateChangeListener;
import com.iGap.libs.flowingdrawer.FlowingView;
import com.iGap.libs.flowingdrawer.LeftDrawerLayout;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MusicPlayer;
import com.iGap.module.MyAppBarLayout;
import com.iGap.module.OnComplete;
import com.iGap.module.SUID;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.module.SortRooms;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.request.RequestChannelDelete;
import com.iGap.request.RequestChannelLeft;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestClientGetRoomList;
import com.iGap.request.RequestGroupDelete;
import com.iGap.request.RequestGroupLeft;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.R.string.updating;

public class ActivityMain extends ActivityEnhanced implements OnComplete, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnUserInfoResponse, OnDraftMessage, OnSetActionInRoom, OnGroupAvatarResponse, OnUpdateAvatar {

    public static LeftDrawerLayout mLeftDrawerLayout;
    public static boolean isMenuButtonAddShown = false;
    private static int drawerWith = 0;
    FloatingActionButton btnStartNewChat;
    FloatingActionButton btnCreateNewGroup;
    FloatingActionButton btnCreateNewChannel;
    LinearLayout mediaLayout;
    MusicPlayer musicPlayer;
    public static MyAppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private RoomsAdapter<RoomItem> mAdapter;
    private ArcMenu arcMenu;
    private MaterialDesignTextView btnSearchAll;
    private int clickPosition = 0;

    private SharedPreferences sharedPreferences;
    private String cLanguage;


    private void scrollToTop() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(0);
            }
        }, 300);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HelperGetDataFromOtherApp getShearedData = new HelperGetDataFromOtherApp(getIntent());

        mediaLayout = (LinearLayout) findViewById(R.id.amr_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

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
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                                .replace(R.id.fragmentContainer, fragmentNewGroup)
                                .commitAllowingStateLoss();
                    }
                });

            }
        };

//       =======> after change language in ActivitySetting this part refresh Activity main
        G.onRefreshActivity = new OnRefreshActivity() {
            @Override
            public void refresh(String changeLanguage) {

                ActivityMain.this.recreate();

            }
        };


        G.onClientGetRoomListResponse = new OnClientGetRoomListResponse() {
            @Override
            public void onClientGetRoomList(final List<ProtoGlobal.Room> roomList, ProtoResponse.Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contentLoading.hide();
                        mAdapter.clear();

                        putChatToDatabase(roomList);
                    }
                });
            }

            @Override
            public void onTimeout() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getChatsList(false);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (majorCode == 610) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_610), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 611) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_611), Snackbar.LENGTH_LONG);

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
            }
        };

        //G.onFileDownloadResponse = this;
        G.onUserInfoResponse = this;
        G.onDraftMessage = this;
        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);
        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(final ProtoGlobal.Room room, final ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
                if (G.currentActivity == ActivityMain.this) {
                    if (mAdapter != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm = Realm.getDefaultInstance();

                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

                                if (!mAdapter.existRoom(room.getId())) {
                                    mAdapter.add(0, new RoomItem().setInfo(realmRoom).withIdentifier(SUID.id().get()));
                                    scrollToTop();
                                }

                                //realm.close(); //TODO [Saeed Mozaffari] [2016-11-27 1:43 PM] - Check Close Realm
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (majorCode == 612) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_612), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 613) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_613), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 614) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_614), Snackbar.LENGTH_LONG);

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
            }

            @Override
            public void onTimeOut() {

            }
        };


        initComponent();
        initRecycleView();
        initFloatingButtonCreateNew();
        initDrawerMenu();
        //onDraftMessage();

        HelperPermision.getContactPermision(ActivityMain.this, new OnGetPermision() {
            @Override
            public void Allow() {
                Contacts.FillRealmInviteFriend();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        RealmRoomMessage.fetchNotDeliveredMessages(new OnActivityMainStart() {
            @Override
            public void sendDeliveredStatus(RealmRoom room, RealmRoomMessage message) {
                G.chatUpdateStatusUtil.sendUpdateStatus(room.getType(), message.getRoomId(), message.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            }
        });
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
        drawerWith = (int) getResources().getDimension(R.dimen.dp200);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            clickPosition = (int) ev.getX();
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (ev.getX() > drawerWith) {
                if (ev.getX() <= clickPosition + 20) mLeftDrawerLayout.closeDrawer();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void initComponent() {

        contentLoading = (ContentLoadingProgressBar) findViewById(R.id.loadingContent);
        contentLoading.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.toolbar_background),
                        android.graphics.PorterDuff.Mode.MULTIPLY);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.fragmentContainer);

        final MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.cl_btn_menu);
        RippleView rippleMenu = (RippleView) findViewById(R.id.cl_ripple_menu);

        btnSearchAll = (MaterialDesignTextView) findViewById(R.id.amr_btn_search);
        btnSearchAll.setTypeface(G.flaticon);

        RippleView rippleSearch = (RippleView) findViewById(R.id.amr_ripple_search);
        rippleSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Fragment fragment = SearchFragment.newInstance();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragmentContainer, fragment, "Search_fragment")
                        .commit();
            }
        });

        final TextView txtIgap = (TextView) findViewById(R.id.cl_txt_igap);
        txtIgap.setTypeface(G.neuroplp);

        txtIgap.setTypeface(null, Typeface.BOLD);
        if (G.connectionState == Config.ConnectionState.WAITING_FOR_NETWORK) {
            txtIgap.setText(R.string.waiting_for_network);
        } else if (G.connectionState == Config.ConnectionState.CONNECTING) {
            txtIgap.setText(R.string.connecting);
        } else if (G.connectionState == Config.ConnectionState.UPDATING) {
            txtIgap.setText(updating);
        } else {
            txtIgap.setText(R.string.igap);
            txtIgap.setTypeface(G.neuroplp, Typeface.NORMAL);
        }

        G.onConnectionChangeState = new OnConnectionChangeState() {
            @Override
            public void onChangeState(final Config.ConnectionState connectionState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtIgap.setTypeface(null, Typeface.BOLD);
                        if (connectionState == Config.ConnectionState.WAITING_FOR_NETWORK) {
                            txtIgap.setText(R.string.waiting_for_network);
                        } else if (connectionState == Config.ConnectionState.CONNECTING) {
                            txtIgap.setText(R.string.connecting);
                        } else if (connectionState == Config.ConnectionState.UPDATING) {
                            txtIgap.setText(updating);
                        } else {
                            txtIgap.setText(R.string.igap);
                            txtIgap.setTypeface(G.neuroplp, Typeface.NORMAL);
                        }
                    }
                });
            }
        };

        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                mLeftDrawerLayout.toggle();
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

        btnStartNewChat = (FloatingActionButton) findViewById(R.id.ac_fab_start_new_chat);
        btnStartNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "New Chat");
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();

                arcMenu.toggleMenu();

                //                isMenuButtonAddShown = true;
                //

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
                ActivityMain.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                arcMenu.toggleMenu();
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
                ActivityMain.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                arcMenu.toggleMenu();
            }
        });
    }

    private void initRecycleView() {
        recyclerView = (RecyclerView) findViewById(R.id.cl_recycler_view_contact);
        // remove notifying fancy animation
        recyclerView.setItemAnimator(null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemViewCacheSize(100);
        mAdapter = new RoomsAdapter<>();
        mAdapter.withOnClickListener(new FastAdapter.OnClickListener<RoomItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RoomItem> adapter, RoomItem item, int position) {
                if (ActivityMain.isMenuButtonAddShown) {
                    item.mComplete.complete(true, "closeMenuButton", "");
                } else {
                    if (item.mInfo.isValid()) {
                        Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
                        intent.putExtra("RoomId", item.mInfo.getId());
                        intent.putExtra("MUT", item.mInfo.getMute());
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        mAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<RoomItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<RoomItem> adapter, final RoomItem item, final int position) {
                if (ActivityMain.isMenuButtonAddShown) {
                    item.mComplete.complete(true, "closeMenuButton", "");
                } else {
                    if (item.mInfo.isValid()) {
                        String role = null;
                        if (item.mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
                            role = item.mInfo.getGroupRoom().getRole().toString();
                        } else if (item.mInfo.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                            role = item.mInfo.getChannelRoom().getRole().toString();
                        }

                        MyDialog.showDialogMenuItemRooms(ActivityMain.this, item.mInfo.getType(), item.mInfo.getMute(), role, new OnComplete() {
                            @Override
                            public void complete(boolean result, String messageOne, String MessageTow) {
                                onSelectRoomMenu(messageOne, position, item);
                            }
                        });
                    }
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

        appBarLayout = (MyAppBarLayout) findViewById(R.id.appBarLayout);
        final RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolbar);
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

                if (arcMenu.isMenuOpened()) arcMenu.toggleMenu();

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
        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<RoomItem>() {
            @Override
            public boolean filter(RoomItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return !item.mInfo.getTitle().toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
    }

    /**
     * put fetched chat to database
     *
     * @param rooms ProtoGlobal.Room
     */
    private void putChatToDatabase(final List<ProtoGlobal.Room> rooms) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ProtoGlobal.Room room : rooms) {
                    RealmRoom.putOrUpdate(room);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                final List<ProtoGlobal.Room> newRooms = new ArrayList<>();
                for (ProtoGlobal.Room room : rooms) {
                    if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).count() == 0) {
                        newRooms.add(room);
                        break;
                    }
                }
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // remove deleted rooms
                        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                            boolean existed = false;
                            for (ProtoGlobal.Room room : rooms) {
                                if (room.getId() == realmRoom.getId()) {
                                    existed = true;
                                    break;
                                }
                            }
                            if (!existed) {
                                realmRoom.deleteFromRealm();
                            }
                        }

                        for (ProtoGlobal.Room room : newRooms) {
                            RealmRoom.putOrUpdate(room);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        List<RoomItem> roomItems = new ArrayList<>();
                        for (ProtoGlobal.Room room : rooms) {
                            roomItems.add(new RoomItem().setInfo(realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst()).withIdentifier(SUID.id().get()));
                        }
                        Collections.sort(roomItems, SortRooms.DESC);
                        mAdapter.add(roomItems);

                        // realm.close();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mLeftDrawerLayout.toggle();
        return false;
    }

    private void muteNotification(final RoomItem item) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, item.getInfo().getId()).findFirst().setMute(!item.mInfo.getMute());
            }
        });
        mAdapter.notifyAdapterItemChanged(mAdapter.getAdapterPosition(item));

        realm.close();
    }

    private void clearHistory(RoomItem item) {
        final RoomItem chatInfo = mAdapter.getAdapterItem(mAdapter.getPosition(item));
        final long chatId = chatInfo.mInfo.getId();

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst();

                        if (realmRoom.getLastMessage() != null) {
                            element.setClearId(realmRoom.getLastMessage().getMessageId());

                            G.clearMessagesUtil.clearMessages(realmRoom.getType(), chatId, realmRoom.getLastMessage().getMessageId());
                        }

                        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, chatId).findAll();
                        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                            if (realmRoomMessage != null) {
                                // delete chat history message
                                realmRoomMessage.deleteFromRealm();
                            }
                        }

                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessage(null);
                        }
                        // finally delete whole chat history
                        realmRoomMessages.deleteAllFromRealm();

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

    private void deleteChat(final RoomItem item) {
        G.onChatDelete = new OnChatDelete() {
            @Override
            public void onChatDelete(long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            mAdapter.remove(mAdapter.getPosition(item));
                        }
                    }
                });
            }

            @Override
            public void onChatDeleteError(int majorCode, int minorCode) {

                if (majorCode == 218 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_218), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }
                    });
                } else if (majorCode == 219) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_219), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }
                    });
                } else if (majorCode == 220) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.E_220), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        };

        new RequestChatDelete().chatDelete(item.getInfo().getId());
    }

    public void deleteGroup(final RoomItem item) {

        G.onGroupDelete = new OnGroupDelete() {
            @Override
            public void onGroupDelete(final long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if (mAdapter != null) {
                            int pos = mAdapter.getPosition(roomId);
                            if (pos != -1) {
                                mAdapter.remove(pos);
                            }
                        }*/
                        mAdapter.remove(mAdapter.getPosition(item));
                    }
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
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

            }
        };

        new RequestGroupDelete().groupDelete(item.getInfo().getId());
    }

    private void lefGroup(final RoomItem item, final int position) {

        G.onGroupLeft = new OnGroupLeft() {
            @Override
            public void onGroupLeft(final long roomId, long memberId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(position);
                        /*if (mAdapter != null) {
                            int pos = mAdapter.getPosition(roomId);
                            if (pos != -1) {
                                mAdapter.remove(pos);
                            }
                        }*/
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "lefGroup", Snackbar.LENGTH_LONG);
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

            }
        };

        new RequestGroupLeft().groupLeft(item.getInfo().getId());
    }

    /**
     * on select room menu
     *
     * @param message  message text
     * @param position position dfdfdfdf
     */
    private void onSelectRoomMenu(String message, int position, RoomItem item) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(item);
                break;
            case "txtClearHistory":
                clearHistory(item);
                break;
            case "txtDeleteChat":

                if (item.mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {

                    deleteChat(item);
                } else if (item.mInfo.getType() == ProtoGlobal.Room.Type.GROUP) {
                    if (item.mInfo.getGroupRoom().getRole() == GroupChatRole.OWNER) {

                        deleteGroup(item);
                    } else {

                        lefGroup(item, position);
                    }
                } else if (item.mInfo.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                    // TODO: 11/22/2016 [Alireza] delete channel room

                    if (item.mInfo.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                        deleteChannel(item, position);
                    } else {
                        leftChannel(item, position);
                    }
                }

                break;
        }
    }

    private void deleteChannel(RoomItem item, final int position) {

        G.onChannelDelete = new OnChannelDelete() {
            @Override
            public void onChannelDelete(final long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(position);
                        /*if (mAdapter != null) {
                            int pos = mAdapter.getPosition(roomId);
                            if (pos != -1) {
                                mAdapter.remove(pos);
                            }
                        }*/
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
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
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
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
        new RequestChannelDelete().channelDelete(item.getInfo().getId());
    }

    private void leftChannel(RoomItem item, final int position) {

        G.onChannelLeft = new OnChannelLeft() {
            @Override
            public void onChannelLeft(final long roomId, long memberId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(position);
                       /* if (mAdapter != null) {
                            int pos = mAdapter.getPosition(roomId);
                            if (pos != -1) {
                                mAdapter.remove(pos);
                            }
                        }*/
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
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
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
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


        new RequestChannelLeft().channelLeft(item.getInfo().getId());
    }


    // FIXME: 9/6/2016 [Alireza Eskandarpour Shoferi] not to be on handler, but for fixing
    // securing for testing purposes
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

    private ContentLoadingProgressBar contentLoading;

    private void getChatsList(boolean fromServer) {
        if (fromServer && G.socketConnection) {
            contentLoading.show();
            testIsSecure();
        } else {
            contentLoading.hide();
            mAdapter.clear();
            Realm realm = Realm.getDefaultInstance();
            List<RoomItem> roomItems = new ArrayList<>();
            for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAllSorted(RealmRoomFields.ID, Sort.DESCENDING)) {
                roomItems.add(new RoomItem().setInfo(realmRoom).setComplete(ActivityMain.this).withIdentifier(SUID.id().get()));
            }

            Collections.sort(roomItems, SortRooms.DESC);

            mAdapter.add(roomItems);
            //realm.close(); //TODO [Saeed Mozaffari] [2016-11-27 1:43 PM] - Check Close Realm
        }
    }

    @Override
    public void onBackPressed() {
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("Search_fragment");
        if (myFragment != null && myFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
        } else if (mLeftDrawerLayout.isShownMenu()) {
            mLeftDrawerLayout.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private static boolean mFirstRun = true;

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(reciverOnGroupChangeName, new IntentFilter("Intent_filter_on_change_group_name"));

        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }
        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);
        G.onSetActionInRoom = this;

        getChatsList(mFirstRun);

        startService(new Intent(this, ServiceContact.class));

        mFirstRun = false;
    }


    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(ActivityMain.this).unregisterReceiver(reciverOnGroupChangeName);
    }


    private BroadcastReceiver reciverOnGroupChangeName = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

//            final String name = intent.getExtras().getString("Name");
//            String description = intent.getExtras().getString("Description");
//            Long _roomid = intent.getExtras().getLong("RoomId");
//
//            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
//                if (mAdapter.getItem(i).getInfo().getOwnerId() == _roomid) {
//                    Realm realm = Realm.getDefaultInstance();
//                    final int finalI = i;
//
//                         mAdapter.notifyAdapterItemChanged(i);
//
//                    break;
//                }
//            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            }, 500);


        }
    };


    /**
     * convert RealmRoom to RoomItem. needed for adding items to adapter.
     *
     * @param roomId room id
     * @return RoomItem
     */
    private RoomItem convertToChatItem(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RoomItem roomItem = new RoomItem();
        roomItem.mInfo = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        roomItem.mComplete = ActivityMain.this;
        roomItem.withIdentifier(SUID.id().get());
        //realm.close(); //TODO [Saeed Mozaffari] [2016-11-27 1:43 PM] - Check Close Realm

        return roomItem;
    }

   /* @Override
    public void onFileDownload(final String token, final long offset, final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        if (selector != ProtoFileDownload.FileDownload.Selector.FILE) {
            // requested thumbnail
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Realm realm = Realm.getDefaultInstance();
                    long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                    for (RealmAvatar realmAvatar : realm.where(RealmAvatar.class).findAll()) {
                        if (realmAvatar.getId() == userId) {
                            RealmAvatar avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, token).findFirst();
                            if (avatar != null) {
                                requestDownloadAvatar(true, token, avatar.getFile().getName(), (int) avatar.getFile().getSmallThumbnail().getSize());
                            }
                            realm.close();

                            mAdapter.downloadingAvatarThumbnail(token);
                        }
                    }
                }
            });
        } else {
            // TODO: 11/22/2016 [Alireza] implement
        }
    }

    @Override
    public void onAvatarDownload(final String token, final long offset, final ProtoFileDownload.FileDownload.Selector selector, final int progress, final long userId, final RoomType roomType) {
        // empty
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        //if (majorCode == 713 && minorCode == 1) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //} else if (majorCode == 713 && minorCode == 2) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //} else if (majorCode == 713 && minorCode == 3) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //} else if (majorCode == 713 && minorCode == 4) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //} else if (majorCode == 713 && minorCode == 5) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //} else if (majorCode == 714) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //} else if (majorCode == 715) {
        //    runOnUiThread(new Runnable() {
        //        @Override public void run() {
        //            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
        //                getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);
        //
        //            snack.setActionTyping("CANCEL", new View.OnClickListener() {
        //                @Override public void onClick(View view) {
        //                    snack.dismiss();
        //                }
        //            });
        //            snack.show();
        //        }
        //    });
        //}
    }

    @Override
    public void onBadDownload(String token) {
        // empty
    }*/

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            arcMenu.toggleMenu();
        }
    }

    @Override
    public void onChatClearMessage(final long roomId, long clearId, final ProtoResponse.Response response) {
        if (response.getId().isEmpty()) {// another account cleared message
            // if have message show last message otherwise clear item from message and time and
            // last seen state
            Realm realm = Realm.getDefaultInstance();

            boolean clearMessage = false;

            RealmResults<RealmRoomMessage> realmRoomMessages =
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
            for (final RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                if (!clearMessage && realmRoomMessage.getMessageId() == clearId) {
                    clearMessage = true;
                }

                if (clearMessage) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (realmRoomMessage != null) {
                                realmRoomMessage.deleteFromRealm();
                            }
                        }
                    });
                }
            }
            List<RealmRoomMessage> allItems = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
            long latestMessageId = 0;
            for (RealmRoomMessage item : allItems) {
                if (item != null) {
                    latestMessageId = item.getMessageId();
                    break;
                }
            }

            if (latestMessageId == 0) { // if cleared from latest message

                // clear item
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessage(null);
                        }
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            mAdapter.updateChat(roomId, convertToChatItem(roomId));
                        }

                        scrollToTop();
                    }
                });
            }
            realm.close();
        }
    }

    @Override
    public void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        // TODO
    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        // I'm not in the room, so I have to add 1 to the unread messages count
        Realm realm = Realm.getDefaultInstance();

        if (roomMessage.getAuthor().getUser() != null) {

            // check if another account send message don't update unread count

            if (roomMessage.getAuthor().getUser().getUserId() != realm.where(RealmUserInfo.class).findFirst().getUserId()) {
                //if another account not send this message , and really i'm recipient not sender
                // update unread count
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        if (room != null) {
                            final int updatedUnreadCount = room.getUnreadCount() + 1;
                            room.setUnreadCount(updatedUnreadCount);
                            realm.copyToRealmOrUpdate(room);
                        }
                    }
                });
            }
        } else {

            /*
             * when i have roomMessage.getAuthor().getRoom(); how detect
             * another account send message or not for updating unread count !
             */

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

        // user has received the message, so I make a new delivered update status request
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
            G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
        }
    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage roomMessage) {
        mAdapter.updateChatStatus(roomId, ProtoGlobal.RoomMessageStatus.FAILED.toString());
    }

    @Override
    public void onChatUpdateStatus(final long roomId, long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateChatStatus(roomId, status.toString());
            }
        });
    }

    /*private static void requestDownloadAvatar(boolean done, final String token, String name, int smallSize) {
        final String fileName = "thumb_" + token + "_" + name;
        if (done) {
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, token).findFirst().getFile().setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    String filePath = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, token).findFirst().getFile().getLocalThumbnailPath();
                    G.onChangeUserPhotoListener.onChangePhoto(filePath);
                    realm.close();
                }
            });

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity =
                token + '*' + selector.toString() + '*' + smallSize + '*' + fileName + '*' + 0;

        new RequestFileDownload().download(token, 0, smallSize,
                selector, identity);
    }*/

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
        /*Realm realm1 = Realm.getDefaultInstance();
        final long userId = realm1.where(RealmUserInfo.class).findFirst().getUserId();
        realm1.close();
        if (userId == user.getId()) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmAvatar avatar = RealmAvatar.put(user.getId(), user.getAvatar());

                    RealmRegisteredInfo.putOrUpdate(user);

                    G.onChangeUserPhotoListener.onChangeInitials(user.getInitials(), user.getColor());

                    if (avatar != null && avatar.isValid()) {
                        if (!avatar.getFile().isFileExistsOnLocal() && !avatar.getFile().isThumbnailExistsOnLocal()) {
                            requestDownloadAvatar(false, avatar.getFile().getToken(), avatar.getFile().getName(), (int) avatar.getFile().getSmallThumbnail().getSize());
                        } else {
                            if (avatar.getFile().isFileExistsOnLocal()) {
                                G.onChangeUserPhotoListener.onChangePhoto(avatar.getFile().getLocalFilePath());
                            } else if (avatar.getFile().isThumbnailExistsOnLocal()) {
                                G.onChangeUserPhotoListener.onChangePhoto(avatar.getFile().getLocalThumbnailPath());
                            }
                        }
                    }
                }
            });

            realm.close();
        }*/
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }

    @Override
    public void onDraftMessage(long roomId, String draftMessage) {
        mAdapter.notifyDraft(roomId, draftMessage);
        mAdapter.goToTop(roomId);
    }

    ProtoGlobal.Room.Type type = null;

    @Override
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    Realm realm = Realm.getDefaultInstance();

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(final Realm realm) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<RoomItem> items = mAdapter.getAdapterItems();
                                    type = null;
                                    for (int i = 0; i < items.size(); i++) {
                                        if (items.get(i).getInfo().getId() == roomId) {
                                            type = items.get(i).getInfo().getType();
                                        }
                                    }
                                }
                            });
                            if (type != null) {
                                String action = HelperGetAction.getAction(roomId, type, clientAction);
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                if (realmRoom != null) {
                                    realmRoom.setActionState(action);
                                }

                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            mAdapter.notifyWithRoomId(roomId);
                        }
                    });
                }
            }
        });

    }

    //******* GroupAvatar and ChannelAvatar

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyWithRoomId(roomId);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                //empty
            }
        });

    }

    @Override
    public void onAvatarAddError() {

    }

    @Override
    public void onUpdateAvatar(final long roomId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("XXX", "roomId : " + roomId);
                mAdapter.notifyWithRoomId(roomId);
            }
        });
    }
}
