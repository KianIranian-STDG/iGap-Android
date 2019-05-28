package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegisteration;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.BadgeView;
import net.iGap.adapter.items.chat.ChatCell;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.interfaces.OnChannelDeleteInRoomList;
import net.iGap.interfaces.OnChatDeleteInRoomList;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnDateChanged;
import net.iGap.interfaces.OnGroupDeleteInRoomList;
import net.iGap.interfaces.OnNotifyTime;
import net.iGap.interfaces.OnRemoveFragment;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnVersionCallBack;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.MyRealmRecyclerViewAdapter;
import net.iGap.libs.Tuple;
import net.iGap.libs.floatingAddButton.ArcMenu;
import net.iGap.module.AppUtils;
import net.iGap.module.BotInit;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FontIconTextView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyDialog;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestClientCondition;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientPinRoom;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupLeft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.clientConditionGlobal;
import static net.iGap.G.context;
import static net.iGap.G.userId;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.fragments.FragmentMain.MainType.all;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.CARD_TO_CARD;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.MONEY_TRANSFER;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.PAYMENT;
import static net.iGap.realm.RealmRoom.putChatToDatabase;


public class FragmentMain extends BaseFragment implements ToolbarListener, ActivityMain.MainInterface, OnClientGetRoomListResponse, OnVersionCallBack, OnComplete, OnSetActionInRoom, OnRemoveFragment, OnChatUpdateStatusResponse, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChannelDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    public static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";
    public static HashMap<MainType, RoomAdapter> roomAdapterHashMap = new HashMap<>();
    public MainType mainType;
    boolean isThereAnyMoreItemToLoad = true;
    private ProgressBar progressBar;
    private int mOffset = 0;
    private View viewById;
    private RecyclerView mRecyclerView;
    private long tagId;
    private Realm realmFragmentMain;
    private RecyclerView.OnScrollListener onScrollListener;
    private View mView = null;
    private String switcher;
    private int channelSwitcher, allSwitcher, groupSwitcher, chatSwitcher = 0;
    private ProgressBar pbLoading;

    private HelperToolbar mHelperToolbar;
    private boolean isChatMultiSelectEnable = false;
    private onChatCellClick onChatCellClickedInEditMode;
    private RoomAdapter roomsAdapter;
    private List<RealmRoom> mSelectedRoomList = new ArrayList<>();
    private ViewGroup mLayoutMultiSelectedActions;
    private View mBtnRemoveSelected, mBtnClearCacheSelected, mBtnReadAllSelected, mBtnMakeAsReadSelected;

    public static FragmentMain newInstance(MainType mainType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(STR_MAIN_TYPE, mainType);
        FragmentMain fragment = new FragmentMain();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        return inflater.inflate(R.layout.activity_main_rooms, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.onVersionCallBack = this;
        realmFragmentMain = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("bagi", "FragmentMain:onViewCreated:start");

        //G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);
        this.mView = view;
        tagId = System.currentTimeMillis();

        mainType = (MainType) getArguments().getSerializable(STR_MAIN_TYPE);
        progressBar = view.findViewById(R.id.ac_progress_bar_waiting);
        viewById = view.findViewById(R.id.empty_icon);
        mLayoutMultiSelectedActions = view.findViewById(R.id.amr_layout_selected_actions);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        viewById.setVisibility(View.GONE);
        /*switcher = String.valueOf(this.toString().charAt(this.toString().lastIndexOf(":") + 1));
        if (switcher.equals("4") && allSwitcher == 0 && mView != null) {
            allSwitcher = 1;
            initRecycleView();
        } else if (switcher.equals("0") && allSwitcher == 0 && mView != null) {
            allSwitcher = 1;
            initRecycleView();
        }*/
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initRecycleView();
            }
        }, 10);

        mHelperToolbar = HelperToolbar.create()
                .setContext(context)
                .setLeftIcon(R.drawable.ic_edit_toolbar)
                .setRightIcons(R.drawable.ic_add_toolbar)
                .setLogoShown(true)
                .setSearchBoxShown(true)
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.amr_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        initMultiSelectActions();

        onChatCellClickedInEditMode = new onChatCellClick() {
            @Override
            public void onClicked(View v, RealmRoom item, int position, boolean status) {

                if (!status) {
                    mSelectedRoomList.add(item);
                } else {
                    mSelectedRoomList.remove(item);
                }
                refreshChatList(position, false);
                setVisiblityForSelectedActionsInEverySelection();
            }
        };

    }

    private void initMultiSelectActions() {

        mBtnRemoveSelected = mView.findViewById(R.id.amr_btn_delete_selected);
        mBtnClearCacheSelected = mView.findViewById(R.id.amr_btn_clear_cache_selected);
        mBtnMakeAsReadSelected = mView.findViewById(R.id.amr_btn_make_as_read_selected);
        mBtnReadAllSelected = mView.findViewById(R.id.amr_btn_read_all_selected);


        mBtnRemoveSelected.setOnClickListener(v -> {
            if (mSelectedRoomList.size() > 0)
                confirmActionForRemoveSelected();
        });

        mBtnClearCacheSelected.setOnClickListener(v -> {
            if (mSelectedRoomList.size() > 0) {
                confirmActionForClearHistoryOfSelected();
            }
        });
    }

    private void refreshChatList(int pos, boolean isRefreshAll) {

        if (isRefreshAll) {
            roomsAdapter.notifyDataSetChanged();
        } else {
            roomsAdapter.notifyItemChanged(pos);
        }

    }

    private void initRecycleView() {

        if (mView != null) {
            mRecyclerView = mView.findViewById(R.id.cl_recycler_view_contact);
            // mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0); // for avoid from show avatar and cloud view together
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setItemViewCacheSize(0);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
//            mRecyclerView.setLayoutManager(new PreCachingLayoutManager(G.fragmentActivity, 3000));
        }

        RealmResults<RealmRoom> results = null;
        String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
        RealmQuery<RealmRoom> temp = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false);

        switch (mainType) {
            case all:
                results = temp.sort(fieldNames, sort).findAllAsync();
                break;
            case chat:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.CHAT.toString()).sort(fieldNames, sort).findAllAsync();
                break;
            case group:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.GROUP.toString()).sort(fieldNames, sort).findAllAsync();
                break;
            case channel:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.CHANNEL.toString()).sort(fieldNames, sort).findAllAsync();
                break;
        }


        roomsAdapter = new RoomAdapter(results, this, viewById, pbLoading);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isThereAnyMoreItemToLoad) {
                    if (mOffset > 0) {
                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastVisiblePosition + 10 >= mOffset) {
                            boolean send = new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
                            if (send)
                                progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    mRecyclerView.removeOnScrollListener(onScrollListener);
                }
            }
        };
        mRecyclerView.addOnScrollListener(onScrollListener);

        mRecyclerView.setAdapter(roomsAdapter);

        if (roomAdapterHashMap == null) {
            roomAdapterHashMap = new HashMap<>();
        }
        roomAdapterHashMap.put(mainType, roomsAdapter);

        //fastAdapter
        //final RoomsAdapter roomsAdapter = new RoomsAdapter(getRealmFragmentMain());
        //for (RealmRoom realmRoom : results) {
        //    roomsAdapter.add(new RoomItem(this, mainType).setInfo(realmRoom).withIdentifier(realmRoom.getId()));
        //}
        //
        //// put adapters in hashMap
        //adapterHashMap.put(mainType, roomsAdapter);
        //
        //mRecyclerView.setAdapter(roomsAdapter);

        if (mainType == all) {
            getChatLists();
        }

        if (mView != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    try {
                        ArcMenu arcMenu = ((ActivityMain) G.fragmentActivity).arcMenu;
                        if (arcMenu.isMenuOpened()) {
                            arcMenu.toggleMenu();
                        }

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
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        G.onNotifyTime = new OnNotifyTime() {
            @Override
            public void notifyTime() {
                if (mRecyclerView != null) {
                    if (mRecyclerView.getAdapter() != null) {
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        };

    }

    //***************************************************************************************************************************

    @Override
    public void onAction(ActivityMain.MainAction action) {

        if (mRecyclerView == null) {
            return;
        }

        switch (action) {

            case downScrool:

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        if (firstVisibleItem < 5) {
                            mRecyclerView.scrollToPosition(0);
                        }
                    }
                });

                break;
            case clinetCondition:

                break;
        }
    }


    private boolean heartBeatTimeOut() {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - G.latestHearBeatTime);

        return difference >= Config.HEART_BEAT_CHECKING_TIME_OUT;

    }

    //***************************************************************************************************************************

    private void sendClientCondition() {
        if (clientConditionGlobal != null) {
            new RequestClientCondition().clientCondition(clientConditionGlobal);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendClientCondition();
                }
            }, 1000);
        }
    }

    private void getChatLists() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure && G.userLogin) {
                    boolean send = new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
                    if (send)
                        progressBar.setVisibility(View.VISIBLE);
                } else {
                    getChatLists();
                }
            }
        }, 1000);
    }

    private void onSelectRoomMenu(String message, RealmRoom item) {
        if (checkValidationForRealm(item)) {
            switch (message) {
                case "pinToTop":

                    pinToTop(item.getId(), item.isPinned());

                    break;
                case "txtMuteNotification":
                    muteNotification(item.getId(), item.getMute());
                    break;
                case "txtClearHistory":
                    clearHistory(item.getId());
                    break;
                case "txtDeleteChat":
                    deleteChat(item);
                    break;
            }
        }
    }

    private void deleteChat(RealmRoom item) {

        if (item.getType() == CHAT) {
            new RequestChatDelete().chatDelete(item.getId());
        } else if (item.getType() == GROUP) {
            if (item.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                new RequestGroupDelete().groupDelete(item.getId());
            } else {
                new RequestGroupLeft().groupLeft(item.getId());
            }
        } else if (item.getType() == CHANNEL) {

            if (MusicPlayer.mainLayout != null) {
                if (item.getId() == MusicPlayer.roomId) {
                    MusicPlayer.closeLayoutMediaPlayer();
                }
            }


            if (item.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                new RequestChannelDelete().channelDelete(item.getId());
            } else {
                new RequestChannelLeft().channelLeft(item.getId());
            }
        }

    }

    private void muteNotification(final long roomId, final boolean mute) {
        //+Realm realm = Realm.getDefaultInstance();
        new RequestClientMuteRoom().muteRoom(roomId, !mute);

        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).updateItem(roomId);
        //        getAdapterMain(roomId).updateItem(roomId);
        //    }
        //});
        //realm.close();
    }

    private void clearHistory(final long roomId) {
        RealmRoomMessage.clearHistoryMessage(roomId);
        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).updateItem(roomId);
        //        getAdapterMain(roomId).updateItem(roomId);
        //    }
        //});
    }

    private void pinToTop(final long roomId, final boolean isPinned) {
        //+Realm realm = Realm.getDefaultInstance();

        new RequestClientPinRoom().pinRoom(roomId, !isPinned);
        if (!isPinned) {
            goToTop();
        }

        //fastAdapter
        //if (!isPinned) {
        //    G.handler.post(new Runnable() {
        //        @Override
        //        public void run() {
        //            adapterHashMap.get(all).goToTop(roomId, true);
        //            getAdapterMain(roomId).goToTop(roomId, true);
        //        }
        //    });
        //} else {
        //    updateUnPin(roomId, all);
        //
        //    ProtoGlobal.Room.Type type = getRoomType(roomId);
        //    if (type == CHAT) {
        //        updateUnPin(roomId, chat);
        //    } else if (type == GROUP) {
        //        updateUnPin(roomId, group);
        //    } else if (type == CHANNEL) {
        //        updateUnPin(roomId, channel);
        //    }
        //}
        //realm.close();
    }

    private void goToTop() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition() <= 1) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        }, 50);
    }

    //fastAdapter
    //private void updateUnPin(final long roomId, final MainType type) {
    //    G.handler.post(new Runnable() {
    //        @Override
    //        public void run() {
    //            RealmResults<RealmRoom> results = null;
    //            String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.UPDATED_TIME};
    //            Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING};
    //            switch (type) {
    //                case all:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).findAll().sort(fieldNames, sort);
    //                    break;
    //                case chat:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.CHAT.toString()).findAll().sort(fieldNames, sort);
    //                    break;
    //                case group:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.GROUP.toString()).findAll().sort(fieldNames, sort);
    //                    break;
    //                case channel:
    //                    results = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.CHANNEL.toString()).findAll().sort(fieldNames, sort);
    //                    break;
    //            }
    //
    //            int position = 0;
    //            for (RealmRoom room : results) {
    //                if (room.getId() == roomId) {
    //                    break;
    //                }
    //                position++;
    //            }
    //            adapterHashMap.get(type).goToPosition(roomId, position);
    //        }
    //    });
    //}

    private boolean checkValidationForRealm(RealmRoom realmRoom) {
        return realmRoom != null && realmRoom.isManaged() && realmRoom.isValid() && realmRoom.isLoaded();
    }

    //fastAdapter
    //private ProtoGlobal.Room.Type getRoomType(long roomId) {
    //    RealmRoom realmRoom = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
    //    if (realmRoom != null) {
    //        return realmRoom.getType();
    //    }
    //    return null;
    //}
    //
    //private RoomsAdapter getAdapterMain(long roomId) {
    //    ProtoGlobal.Room.Type roomType = getRoomType(roomId);
    //    if (roomType != null) {
    //        if (roomType == CHAT) {
    //            return adapterHashMap.get(chat);
    //        } else if (roomType == GROUP) {
    //            return adapterHashMap.get(group);
    //        } else if (roomType == CHANNEL) {
    //            return adapterHashMap.get(channel);
    //        }
    //    }
    //    return adapterHashMap.get(all);
    //}

    /**
     * ************************************ Callbacks ************************************
     */
    @Override
    public void onChange() {
        for (Map.Entry<MainType, RoomAdapter> entry : roomAdapterHashMap.entrySet()) {
            RoomAdapter requestWrapper = entry.getValue();
            requestWrapper.notifyDataSetChanged();
        }
    }

    @Override
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {
        RealmRoom.setAction(roomId, userId, HelperGetAction.getAction(roomId, RealmRoom.detectType(roomId), clientAction));
    }

    @Override
    public void onRemoveFragment(Fragment fragment) {
        removeFromBaseFragment(fragment);
    }

    @Override
    public void onChatUpdateStatus(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        //fastAdapter
        //G.handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).updateItem(roomId);
        //        getAdapterMain(roomId).updateItem(roomId);
        //    }
        //}, 200);
    }

    @Override
    public void onChatDelete(final long roomId) {
        //fastAdapter
        //adapterHashMap.get(all).removeChat(roomId);
        //adapterHashMap.get(chat).removeChat(roomId);
    }

    @Override
    public void onGroupDelete(long roomId) {
        //fastAdapter
        //adapterHashMap.get(all).removeChat(roomId);
        //adapterHashMap.get(group).removeChat(roomId);
    }

    @Override
    public void onGroupDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onGroupDeleteTimeOut() {

    }

    @Override
    public void onChannelDelete(long roomId) {
        //fastAdapter
        //adapterHashMap.get(all).removeChat(roomId);
        //adapterHashMap.get(channel).removeChat(roomId);
    }

    @Override
    public void onChannelDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChannelDeleteTimeOut() {

    }

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        //fastAdapter
        //G.handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).goToTop(roomId, false);
        //        getAdapterMain(roomId).goToTop(roomId, false);
        //    }
        //}, 200);
    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        adapterHashMap.get(all).goToTop(roomId, false);
        //        getAdapterMain(roomId).goToTop(roomId, false);
        //    }
        //});
    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage roomMessage) {

    }

    @Override
    public void onClientGetRoomResponse(final long roomId) {
        //fastAdapter
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        RealmRoom realmRoom = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        //        if (realmRoom != null && getAdapterMain(roomId).getPosition(realmRoom.getId()) == -1) {
        //            adapterHashMap.get(all).add(0, new RoomItem(mComplete, all).setInfo(realmRoom).withIdentifier(realmRoom.getId()));
        //            MainType type = all;
        //            if (realmRoom.getType() == CHAT) {
        //                type = chat;
        //            } else if (realmRoom.getType() == GROUP) {
        //                type = group;
        //            } else if (realmRoom.getType() == CHANNEL) {
        //                type = channel;
        //            }
        //            getAdapterMain(roomId).add(0, new RoomItem(mComplete, type).setInfo(realmRoom).withIdentifier(realmRoom.getId()));
        //        }
        //    }
        //});
    }

    @Override
    public synchronized void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, RequestClientGetRoomList.IdentityGetRoomList identity) {

        boolean fromLogin = false;
        if (identity.isFromLogin) {
            mOffset = 0;
            fromLogin = true;
        } else if (Long.parseLong(identity.content) < tagId) {
            return;
        }

        if (mOffset == 0) {
            BotInit.checkDrIgap();
        }


        isThereAnyMoreItemToLoad = roomList.size() != 0;

        putChatToDatabase(roomList);

        //fastAdapter
        //G.handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        initRecycleView(null);
        //    }
        //}, 200);

        /**
         * to first enter to app , client first compute clientCondition then
         * getRoomList and finally send condition that before get clientCondition;
         * in else changeState compute new client condition with latest messaging changeState
         */
        if (!G.userLogin) {
            G.userLogin = true;
            sendClientCondition();
        } else if (fromLogin || mOffset == 0) {
            if (G.clientConditionGlobal != null) {
                new RequestClientCondition().clientCondition(G.clientConditionGlobal);
            } else {
                new RequestClientCondition().clientCondition(RealmClientCondition.computeClientCondition(null));
            }

        }

        mOffset += roomList.size();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        //else {
        //    mOffset = 0;
        //}


    }

    @Override
    public void onClientGetRoomListError(int majorCode, int minorCode) {
        if (majorCode == 9) {
            if (G.currentActivity != null) {
                G.currentActivity.finish();
            }
            Intent intent = new Intent(context, ActivityRegisteration.class);
            intent.putExtra(ActivityRegisteration.showProfile, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    @Override
    public void onClientGetRoomListTimeout() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                getChatLists();
            }
        });
    }


    @Override
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
        }
    }

    private Realm getRealmFragmentMain() {
        if (realmFragmentMain == null || realmFragmentMain.isClosed()) {
            realmFragmentMain = Realm.getDefaultInstance();
        }
        return realmFragmentMain;
    }

    //**************************************************************************************************************************************

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realmFragmentMain != null && !realmFragmentMain.isClosed()) {
            realmFragmentMain.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("bagi", "FragmentMain:onResume:start");

        G.onSetActionInRoom = this;
        G.onDateChanged = this;
        if (G.isDepricatedApp)
            isDeprecated();

        //G.onSelectMenu = this;
        //G.onRemoveFragment = this;
        //G.onChatDeleteInRoomList = this;
        //G.onGroupDeleteInRoomList = this;
        //G.onChannelDeleteInRoomList = this;
        G.onClientGetRoomListResponse = this;
        //onClientGetRoomResponseRoomList = this;
        //G.chatUpdateStatusUtil.setOnChatUpdateStatusResponseFragmentMain(this);
        //G.chatSendMessageUtil.setOnChatSendMessageResponseFragmentMainRoomList(this);

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }

        boolean canUpdate = false;

        if (mainType != null) {
            switch (mainType) {

                case all:
                    if (G.isUpdateNotificaionColorMain) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorMain = false;
                    }
                    break;
                case chat:
                    if (G.isUpdateNotificaionColorChat) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorChat = false;
                    }
                    break;
                case group:
                    if (G.isUpdateNotificaionColorGroup) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorGroup = false;
                    }
                    break;
                case channel:
                    if (G.isUpdateNotificaionColorChannel) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorChannel = false;
                    }
                    break;
            }
        }

        if (canUpdate) {

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }
//        BotInit.checkDrIgap();

        Log.d("bagi", "FragmentMain:onResume:end");
    }

    @Override
    public void isDeprecated() {
        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().hasWindowFocus()) {
                            new MaterialDialog.Builder(getActivity())
                                    .cancelable(false)
                                    .title(R.string.new_version_alert).titleGravity(GravityEnum.CENTER)
                                    .titleColor(Color.parseColor("#f44336"))
                                    .content(R.string.deprecated)
                                    .contentGravity(GravityEnum.CENTER)
                                    .positiveText(R.string.startUpdate).itemsGravity(GravityEnum.START).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    // HelperUrl.openBrowser("http://d.igap.net/update");
                                    String url = "http://d.igap.net/update";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            })
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
        }

    }

    @Override
    public void isUpdateAvailable() {
        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().hasWindowFocus()) {
                            new MaterialDialog.Builder(getActivity())
                                    .title(R.string.igap_update).titleColor(Color.parseColor("#1DE9B6"))
                                    .titleGravity(GravityEnum.CENTER)
                                    .buttonsGravity(GravityEnum.CENTER)
                                    .content(R.string.new_version_avilable).contentGravity(GravityEnum.CENTER)
                                    .negativeText(R.string.ignore).negativeColor(Color.parseColor("#798e89")).onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    dialog.dismiss();
                                }
                            }).positiveText(R.string.startUpdate).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    // HelperUrl.openBrowser("http://d.igap.net/update");
                                    String url = "http://d.igap.net/update";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            })
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {

        if (isChatMultiSelectEnable) {

            mLayoutMultiSelectedActions.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            marginLayoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.margin_for_below_layouts_of_toolbar_with_search), 0, 10);
            mRecyclerView.setLayoutParams(marginLayoutParams);
            isChatMultiSelectEnable = false;
            refreshChatList(0, true);
            mHelperToolbar.setLeftIcon(R.drawable.ic_edit_toolbar);
            mSelectedRoomList.clear();
            setVisiblityForSelectedActionsInEverySelection();

        } else {

            mLayoutMultiSelectedActions.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            marginLayoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.margin_for_below_layouts_of_toolbar_with_room_selected_mode), 0, 10);
            mRecyclerView.setLayoutParams(marginLayoutParams);
            isChatMultiSelectEnable = true;
            refreshChatList(0, true);
            mHelperToolbar.setLeftIcon(R.drawable.ic_cancel_toolbar);

        }
    }

    @Override
    public void onSearchClickListener(View view) {
        Fragment fragment = SearchFragment.newInstance();

        try {
            new HelperFragment(fragment).load();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void onRightIconClickListener(View view) {

        final Fragment fragment = RegisteredContactsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "ADD");
        fragment.setArguments(bundle);

        try {

            new HelperFragment(fragment).load();

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private boolean isItemAvailableOnSelectedList(RealmRoom mInfo) {

        return mSelectedRoomList.contains(mInfo);
    }

    private void confirmActionForRemoveSelected() {

        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.delete_chat))
                .content(getString(R.string.do_you_want_delete_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        if (mSelectedRoomList.size() > 0) {

                            for (RealmRoom item : mSelectedRoomList) {
                                deleteChat(item);
                            }

                            onLeftIconClickListener(null);
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void confirmActionForClearHistoryOfSelected() {

        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.clear_history))
                .content(getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        for (RealmRoom item : mSelectedRoomList) {
                            clearHistory(item.getId());
                        }
                        onLeftIconClickListener(null);

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void setVisiblityForSelectedActionsInEverySelection() {

        if (mSelectedRoomList.size() == 0) mBtnRemoveSelected.setVisibility(View.VISIBLE);

        for (RealmRoom item : mSelectedRoomList) {

            if (item != null && !RealmRoom.isPromote(item.getId())) {

                if (item.getType() == ProtoGlobal.Room.Type.CHAT || item.getType() == ProtoGlobal.Room.Type.GROUP
                        || item.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                    mBtnRemoveSelected.setVisibility(View.VISIBLE);
                } else {
                    mBtnRemoveSelected.setVisibility(View.GONE);
                }

            } else {
                mBtnRemoveSelected.setVisibility(View.GONE);
            }

        }
    }

    public enum MainType {
        all, chat, group, channel
    }

    private interface onChatCellClick {
        void onClicked(View v, RealmRoom item, int pos, boolean status);
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class RoomAdapter extends MyRealmRecyclerViewAdapter<RealmRoom, RoomAdapter.ViewHolder> {

        public OnComplete mComplete;
        public String action;
        private View emptyView;
        private View loadingView;
        private ConstraintLayout rootView;

        public RoomAdapter(@Nullable OrderedRealmCollection<RealmRoom> data, OnComplete complete, View emptyView, View loadingView) {
            super(data, true);
            this.mComplete = complete;
            this.emptyView = emptyView;
            this.loadingView = loadingView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // View v = inflater.inflate(R.layout.chat_sub_layout, parent, false);
            rootView = new ChatCell(getContext());
            rootView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp80)));
            return new ViewHolder(rootView);
        }

        @Override
        protected OrderedRealmCollectionChangeListener createListener() {
            return new OrderedRealmCollectionChangeListener() {
                @Override
                public void onChange(Object collection, OrderedCollectionChangeSet changeSet) {
                    if (getData() != null && getData().size() > 0) {
                        emptyView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                        loadingView.setVisibility(View.GONE);
                        notifyDataSetChanged();
                        return;
                    }
                    // For deletions, the adapter has to be notified in reverse order.
                    OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                    for (int i = deletions.length - 1; i >= 0; i--) {
                        OrderedCollectionChangeSet.Range range = deletions[i];
                        notifyItemRangeRemoved(range.startIndex, range.length);
                    }

                    OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                    for (OrderedCollectionChangeSet.Range range : insertions) {
                        notifyItemRangeInserted(range.startIndex, range.length);
                        //goToTop();
                    }

                    if (!updateOnModification) {
                        return;
                    }

                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    for (OrderedCollectionChangeSet.Range range : modifications) {
                        notifyItemRangeChanged(range.startIndex, range.length);
                    }
                }
            };
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int i) {


            final RealmRoom mInfo = holder.mInfo = getItem(i);
            if (mInfo == null) {
                return;
            }

            if (holder.getAdapterPosition() == 0) {
                ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp80));
                lp.setMargins(0, i_Dp(R.dimen.dp24), 0, 0);
                holder.root.setLayoutParams(lp);
            } else {
                ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp80));
                lp.setMargins(0, 0, 0, 0);
                holder.root.setLayoutParams(lp);
            }

            if (isChatMultiSelectEnable) {
                holder.chSelected.setVisibility(View.VISIBLE);

                if (isItemAvailableOnSelectedList(mInfo)) {
                    holder.chSelected.setChecked(true);
                } else {
                    holder.chSelected.setChecked(false);
                }

            } else {
                holder.chSelected.setVisibility(View.GONE);
                holder.chSelected.setChecked(false);
            }
            final boolean isMyCloud;

            isMyCloud = mInfo.getChatRoom() != null && mInfo.getChatRoom().getPeerId() > 0 && mInfo.getChatRoom().getPeerId() == userId;

            if (mInfo.isValid()) {

                setLastMessage(mInfo, holder, isMyCloud);

                if (isMyCloud) {

                    // TODO: 5/9/19 add cloud svg
                    holder.image.setImageDrawable(getResources().getDrawable(R.drawable.bank_dey_pec));

                } else {
                    if (holder.image.getVisibility() == View.GONE) {
                        holder.image.setVisibility(View.VISIBLE);
                    }
                    setAvatar(mInfo, holder);
                }

                setChatIcon(mInfo, holder.txtChatIcon);

                holder.name.setText(mInfo.getTitle());

                if ((mInfo.getType() == CHAT) && mInfo.getChatRoom().isVerified()) {
                    holder.imgVerifyRoom.setVisibility(View.VISIBLE);
                } else if ((mInfo.getType() == CHANNEL) && mInfo.getChannelRoom().isVerified()) {
                    holder.imgVerifyRoom.setVisibility(View.VISIBLE);
                } else {
                    holder.imgVerifyRoom.setVisibility(View.INVISIBLE);
                }

                if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
                    holder.txtTime.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
                } else {
                    holder.txtTime.setVisibility(View.GONE);
                }

                /**
                 * ********************* unread *********************
                 */

                if (mInfo.isPinned()) {
                    if (mInfo.isFromPromote()) {
                        holder.txtPinIcon.setVisibility(View.GONE);
                    } else {
                        holder.txtPinIcon.setVisibility(View.VISIBLE);
//                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.setMargins(16, 0, 16, 0);
//                        holder.txtTime.setLayoutParams(params);
                    }

                } else {
                    holder.txtPinIcon.setVisibility(View.GONE);
                }

                if (mInfo.getUnreadCount() < 1) {

                    holder.txtUnread.setVisibility(View.GONE);

                } else {
                    holder.txtUnread.setVisibility(View.VISIBLE);
                    holder.txtUnread.getTextView().setText(mInfo.getUnreadCount() + "");
                    holder.txtUnread.setBadgeColor(getResources().getColor(R.color.notification_badge));

                    if (mInfo.getMute()) {
                        holder.txtUnread.setBadgeColor(getResources().getColor(R.color.gray_9d));
                    } else {
                        if (G.isDarkTheme) {
                            holder.txtUnread.setBadgeColor(getResources().getColor(R.color.notification_badge));
                        } else {
                            holder.txtUnread.setBadgeColor(getResources().getColor(R.color.notification_badge));
                        }
                    }
                }


            }

            /**
             * for change english number to persian number
             */
            if (HelperCalander.isPersianUnicode) {
                holder.txtLastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtLastMessage.getText().toString()));
                holder.txtUnread.getTextView().setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtUnread.getTextView().getText().toString()));
            }

        }


        private String subStringInternal(String text) {
            if (text == null || text.length() == 0) {
                return "";
            }

            ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(text);
            text = AbstractMessage.removeBoldMark(text, boldPlaces);

            int subLength = 150;
            if (text.length() > subLength) {
                return text.substring(0, subLength);
            } else {
                return text;
            }
        }

        //*******************************************************************************************
        private void setLastMessage(RealmRoom mInfo, ViewHolder holder, boolean isMyCloud) {

            holder.txtTic.setVisibility(View.INVISIBLE);
            holder.txtLastMessageFileText.setVisibility(View.GONE);
            holder.txtLastMessage.setText("");

            if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((isMyCloud || (mInfo.getActionStateUserId() != G.userId))))) {

                holder.lastMessageSender.setVisibility(View.GONE);
                holder.txtLastMessage.setText(mInfo.getActionState());
                holder.txtLastMessage.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                holder.txtLastMessage.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
                ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(mInfo.getDraft().getMessage());
                holder.txtLastMessage.setText(subStringInternal(AbstractMessage.removeBoldMark(mInfo.getDraft().getMessage(), boldPlaces)));
                holder.txtLastMessage.setTextColor(Color.parseColor(G.textSubTheme));
                holder.lastMessageSender.setVisibility(View.VISIBLE);
                holder.lastMessageSender.setText(R.string.txt_draft);
                holder.lastMessageSender.setTextColor(getResources().getColor(R.color.red));
                holder.lastMessageSender.setTypeface(G.typeface_IRANSansMobile);
            } else {

                if (mInfo.getLastMessage() != null) {
                    holder.txtTime.setVisibility(View.VISIBLE);
                    String lastMessage = AppUtils.rightLastMessage(RealmRoomMessage.getFinalMessage(mInfo.getLastMessage()));

                    if (lastMessage == null) {
                        lastMessage = mInfo.getLastMessage().getMessage();
                    }

                    if (lastMessage == null || lastMessage.isEmpty()) {

                        holder.lastMessageSender.setVisibility(View.GONE);
                    } else {
                        if (mInfo.getLastMessage().isAuthorMe()) {

                            holder.txtTic.setVisibility(View.VISIBLE);

                            ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
                            if (mInfo.getLastMessage().getStatus() != null) {
                                try {
                                    status = ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus());
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                            AppUtils.rightMessageStatus(holder.txtTic, status, mInfo.getLastMessage().isAuthorMe());
                        }

                        if (mInfo.getType() == GROUP) {
                            /**
                             * here i get latest message from chat history with chatId and
                             * get DisplayName with that . when login app client get latest
                             * message for each group from server , if latest message that
                             * send server and latest message that exist in client for that
                             * room be different latest message sender showing will be wrong
                             */

                            if (mInfo.getLastMessage().isAuthorMe()) {
                                holder.lastMessageSender.setText(holder.itemView.getResources().getString(R.string.txt_you));
                            } else {
                                holder.lastMessageSender.setText("");
                                if (mInfo.getLastMessage() != null) {
                                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmFragmentMain(), mInfo.getLastMessage().getUserId());
                                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
                                        setSenderName(holder, realmRegisteredInfo.getDisplayName());
                                    }
                                }
                            }

                            holder.lastMessageSender.setVisibility(View.VISIBLE);
                            holder.lastMessageSender.setTextColor(Color.parseColor(G.roomSenderTextColor));
                        } else {
                            holder.lastMessageSender.setVisibility(View.GONE);
                        }

                        if (mInfo.getLastMessage() != null) {
                            ProtoGlobal.RoomMessageType _type, tmp;

                            _type = mInfo.getLastMessage().getMessageType();
                            String fileText = mInfo.getLastMessage().getMessage();

                            try {
                                if (mInfo.getLastMessage().getForwardMessage() != null) {
                                    tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
                                    if (tmp != null) {
                                        _type = tmp;
                                    }
                                    if (mInfo.getLastMessage().getForwardMessage().getMessage() != null) {
                                        fileText = mInfo.getLastMessage().getForwardMessage().getMessage();
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            String result = AppUtils.conversionMessageType(_type, holder.txtLastMessage, G.roomMessageTypeColor);
                            if (_type == ProtoGlobal.RoomMessageType.WALLET) {
                                String type;
                                if (mInfo.getLastMessage().getForwardMessage() != null) {
                                    type = mInfo.getLastMessage().getForwardMessage().getRoomMessageWallet().getType();
                                } else {
                                    type = mInfo.getLastMessage().getRoomMessageWallet().getType();
                                }

                                if (type.equals(CARD_TO_CARD.toString())) {
                                    result = G.fragmentActivity.getResources().getString(R.string.card_to_card_message);
                                } else if (type.equals(PAYMENT.toString())) {
                                    result = G.fragmentActivity.getResources().getString(R.string.payment_message);
                                } else if (type.equals(MONEY_TRANSFER.toString())) {
                                    result = G.fragmentActivity.getResources().getString(R.string.wallet_message);
                                } else {
                                    result = G.fragmentActivity.getResources().getString(R.string.unknown_message);
                                }
                                holder.txtLastMessage.setText(result);
                            }


                            if (result.isEmpty()) {
                                if (!HelperCalander.isPersianUnicode) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        holder.txtLastMessage.setTextDirection(View.TEXT_DIRECTION_LTR);
                                    }
                                }
                                holder.txtLastMessage.setTextColor(Color.parseColor(G.textSubTheme));
                                holder.txtLastMessage.setText(subStringInternal(lastMessage));
                                holder.txtLastMessage.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                if (fileText != null && !fileText.isEmpty()) {
                                    holder.txtLastMessageFileText.setVisibility(View.VISIBLE);
                                    holder.txtLastMessageFileText.setText(fileText);

                                    holder.txtLastMessage.setText(holder.txtLastMessage.getText() + " : ");
                                } else {
                                    holder.txtLastMessageFileText.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            holder.txtLastMessage.setText(subStringInternal(lastMessage));
                        }
                    }
                } else {

                    holder.lastMessageSender.setVisibility(View.GONE);
                    holder.txtTime.setVisibility(View.GONE);
                }
            }
        }

        private void setSenderName(ViewHolder holder, String _name) {
            if (_name.length() > 0) {
                String lastMessageSenderAsync;

                if (Character.getDirectionality(_name.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                    if (HelperCalander.isPersianUnicode) {
                        lastMessageSenderAsync = _name + ": ";
                    } else {
                        lastMessageSenderAsync = " :" + _name;
                    }
                } else {
                    if (HelperCalander.isPersianUnicode) {
                        lastMessageSenderAsync = " :" + _name;
                    } else {
                        lastMessageSenderAsync = _name + ": ";
                    }
                }
                holder.lastMessageSender.setText(lastMessageSenderAsync);
            }
        }

        private void setAvatar(final RealmRoom mInfo, ViewHolder holder) {
            long idForGetAvatar;
            if (mInfo.getType() == CHAT) {
                idForGetAvatar = mInfo.getChatRoom().getPeerId();
            } else {
                idForGetAvatar = mInfo.getId();
            }

            Bitmap init = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp52), mInfo.getInitials(), mInfo.getColor());
            avatarHandler.getAvatar(new ParamWithInitBitmap(holder.image, idForGetAvatar).initBitmap(init));
        }

        /**
         * change mute icon for channel pv and group chats
         * font type face FontIconTextView
         */
        private void setChatIcon(RealmRoom mInfo, FontIconTextView chatIconTv) {
            if (mInfo.getType() == CHAT && mInfo.getMute()) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText("K");
            } else if (mInfo.getType() == CHAT && !mInfo.getMute()) {
                chatIconTv.setVisibility(View.GONE);
                chatIconTv.setText("");
            }
            if (mInfo.getType() == GROUP && !mInfo.getMute()) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText(";");
            } else if (mInfo.getType() == GROUP && mInfo.getMute()) {
                chatIconTv.setText("K");
            }
            if (mInfo.getType() == CHANNEL && !mInfo.getMute()) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText(":");
            } else if (mInfo.getType() == CHANNEL && mInfo.getMute()) {
                chatIconTv.setText("K");
            }
        }

        /**
         * get string chat icon
         *
         * @param chatType chat type
         * @return String
         */
        private String getStringChatIcon(RoomType chatType) {
            switch (chatType) {
                case CHAT:
                    return "";
                case CHANNEL:
                    return context.getString(R.string.md_channel_icon);
                case GROUP:
                    return context.getString(R.string.md_users_social_symbol);
                default:
                    return null;
            }
        }

        //*******************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            protected CircleImageView image;
            protected EmojiTextViewE name;
            protected MaterialDesignTextView mute;
            protected RealmRoom mInfo;
            private EmojiTextViewE txtLastMessage;
            private EmojiTextViewE txtLastMessageFileText;
            private FontIconTextView txtChatIcon;
            private TextView txtTime;
            private View txtPinIcon;
            private FontIconTextView imgVerifyRoom;
            private BadgeView txtUnread;
            private EmojiTextViewE lastMessageSender;
            private FontIconTextView txtTic;
            private View root;
            private CheckBox chSelected;


            public ViewHolder(View view) {
                super(view);

                root = view;
                /**
                 * user avatar image
                 * */
                image = view.findViewById(R.id.iv_chatCell_userAvatar);

                chSelected = view.findViewById(R.id.iv_itemContactChat_checkBox);

                /**
                 * user name
                 * */
                name = view.findViewById(R.id.tv_chatCell_roomName);
                name.setTypeface(G.typeface_IRANSansMobile_Bold);

                txtLastMessage = view.findViewById(R.id.tv_chatCell_secondTextView);
                txtLastMessageFileText = view.findViewById(R.id.tv_chatCell_thirdTextView);

                /**
                 * channel or group icon
                 * */
                txtChatIcon = view.findViewById(R.id.tv_chatCell_chatIcon);

                /**
                 * sended message time
                 * */
                txtTime = view.findViewById(R.id.tv_chatCell_messageData);
                txtTime.setTypeface(G.typeface_IRANSansMobile);

                /**
                 * pin icon
                 * */
//                txtPinIcon = (MaterialDesignTextView) view.findViewById(R.id.iv_chatCell_pinnedMessage);
//                txtPinIcon.setTypeface(G.typeface_Fontico);

                txtPinIcon = view.findViewById(R.id.iv_iv_chatCell_pin);

                /**
                 * verify imageView
                 * */
                imgVerifyRoom = view.findViewById(R.id.tv_chatCell_verify);

                /**
                 * unread text counter
                 * */
                txtUnread = view.findViewById(R.id.iv_chatCell_messageCount);
                txtUnread.getTextView().setTypeface(G.typeface_IRANSansMobile);

                /**
                 * mute icon
                 * */
                mute = view.findViewById(R.id.iv_chatCell_mute);

                /**
                 * last message sender name
                 * */
                lastMessageSender = view.findViewById(R.id.tv_chatCell_firstTextView);
                lastMessageSender.setTypeface(G.typeface_IRANSansMobile);


                txtTic = view.findViewById(R.id.iv_chatCell_messageStatus);

                root.setOnClickListener(v -> {

                    if (isChatMultiSelectEnable) {

                        onChatCellClickedInEditMode.onClicked(chSelected, mInfo, getAdapterPosition(), chSelected.isChecked());


                    } else {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {

                                boolean openChat = true;

                                if (G.twoPaneMode) {
                                    Fragment fragment = G.fragmentManager.findFragmentByTag(FragmentChat.class.getName());
                                    if (fragment != null) {

                                        FragmentChat fm = (FragmentChat) fragment;
                                        if (fm.isAdded() && fm.mRoomId == mInfo.getId()) {
                                            openChat = false;
                                        } else {
                                            removeFromBaseFragment(fragment);
                                        }


                                    }
                                }

                                if (openChat) {
                                    new GoToChatActivity(mInfo.getId()).startActivity();

                                    if (((ActivityMain) G.fragmentActivity).arcMenu != null && ((ActivityMain) G.fragmentActivity).arcMenu.isMenuOpened()) {
                                        ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
                                    }
                                }
                            }
                        }

                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (isChatMultiSelectEnable) return false;

                        if (ActivityMain.isMenuButtonAddShown) {

                            if (mComplete != null) {
                                mComplete.complete(true, "closeMenuButton", "");
                            }

                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {
                                String role = null;
                                if (mInfo.getType() == GROUP) {
                                    role = mInfo.getGroupRoom().getRole().toString();
                                } else if (mInfo.getType() == CHANNEL) {
                                    role = mInfo.getChannelRoom().getRole().toString();
                                }

                                if (!G.fragmentActivity.isFinishing()) {
                                    long peerId = mInfo.getChatRoom() != null ? mInfo.getChatRoom().getPeerId() : 0;
                                    MyDialog.showDialogMenuItemRooms(G.fragmentActivity, mInfo.getTitle(), mInfo.getType(), mInfo.getMute(), role, peerId, mInfo, new OnComplete() {
                                        @Override
                                        public void complete(boolean result, String messageOne, String MessageTow) {
                                            onSelectRoomMenu(messageOne, mInfo);
                                        }
                                    }, mInfo.isPinned());
                                }
                            }
                        }
                        return true;
                    }
                });
            }

        }
    }

}
