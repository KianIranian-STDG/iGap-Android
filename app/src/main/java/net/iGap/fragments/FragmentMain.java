package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vanniktech.emoji.EmojiTextView;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.ChatCell;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnChannelDeleteInRoomList;
import net.iGap.interfaces.OnChatDeleteInRoomList;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnDateChanged;
import net.iGap.interfaces.OnGroupDeleteInRoomList;
import net.iGap.interfaces.OnRemoveFragment;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnVersionCallBack;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.MyRealmRecyclerViewAdapter;
import net.iGap.libs.Tuple;
import net.iGap.module.AppUtils;
import net.iGap.module.BotInit;
import net.iGap.module.FontIconTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyDialog;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.CARD_TO_CARD;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.MONEY_TRANSFER;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.PAYMENT;
import static net.iGap.realm.RealmRoom.putChatToDatabase;

public class FragmentMain extends BaseMainFragments implements ToolbarListener, EventListener, OnClientGetRoomListResponse, OnVersionCallBack, OnComplete, OnSetActionInRoom, OnRemoveFragment, OnChatUpdateStatusResponse, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChannelDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    private static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";

    private boolean isThereAnyMoreItemToLoad = true;
    private ProgressBar progressBar;
    public static int mOffset = 0;
    private View viewById;
    private RecyclerView mRecyclerView;
    private long tagId;
    private Realm realmFragmentMain;
    private ProgressBar pbLoading;

    private RoomAdapter roomAdapter;
    private HelperToolbar mHelperToolbar;
    private boolean isChatMultiSelectEnable = false;
    private onChatCellClick onChatCellClickedInEditMode;
    private List<RealmRoom> mSelectedRoomList = new ArrayList<>();
    private ViewGroup mLayoutMultiSelectedActions;
    private TextView mBtnRemoveSelected;
    private RealmResults<RealmRoom> results;
    private ConstraintLayout root;
    private ConstraintSet constraintSet;
    private ViewGroup selectLayoutRoot;

    public static FragmentMain newInstance(MainType mainType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(STR_MAIN_TYPE, mainType);
        FragmentMain fragment = new FragmentMain();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realmFragmentMain = Realm.getDefaultInstance();
        return inflater.inflate(R.layout.activity_main_rooms, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedResume = true;
        G.onVersionCallBack = this;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_ROOM_PAGE);
        tagId = System.currentTimeMillis();

        selectLayoutRoot = view.findViewById(R.id.amr_layout_selected_root);
        root = view.findViewById(R.id.amr_layout_root);
        constraintSet = new ConstraintSet();
        constraintSet.clone(root);

        progressBar = view.findViewById(R.id.ac_progress_bar_waiting);
        viewById = view.findViewById(R.id.empty_icon);
        mLayoutMultiSelectedActions = view.findViewById(R.id.amr_layout_selected_actions);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        viewById.setVisibility(View.GONE);

        ViewGroup layoutToolbar = view.findViewById(R.id.amr_layout_toolbar);

        /*if (G.twoPaneMode && G.isLandscape) {
            mHelperToolbar = HelperToolbar.create()
                    .setContext(getContext())
                    .setTabletIcons(R.string.add_icon, R.string.edit_icon, R.string.search_icon)
                    .setTabletMode(true)
                    .setListener(this);
            layoutToolbar.addView(mHelperToolbar.getView());
            RealmUserInfo userInfo = getRealmFragmentMain().where(RealmUserInfo.class).findFirst();
            mHelperToolbar.getTabletUserName().setText(userInfo.getUserInfo().getDisplayName());
            mHelperToolbar.getTabletUserPhone().setText(userInfo.getUserInfo().getPhoneNumber());
            avatarHandler.getAvatar(new ParamWithAvatarType(mHelperToolbar.getTabletUserAvatar(), userInfo.getUserId()).avatarType(AvatarHandler.AvatarType.USER).showMain());
        } else {*/
        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.edit_icon)
                .setRightIcons(R.string.add_icon_without_circle_font)
                .setFragmentActivity(getActivity())
                .setPassCodeVisibility(true, R.string.unlock_icon)
                .setScannerVisibility(true, R.string.scan_qr_code_icon)
                .setLogoShown(true)
                .setPlayerEnable(true)
                .setSearchBoxShown(true, false)
                .setListener(this);
        layoutToolbar.addView(mHelperToolbar.getView());
        mHelperToolbar.registerTimerBroadcast();
        /*}*/

        mBtnRemoveSelected = view.findViewById(R.id.amr_btn_delete_selected);
        TextView mBtnClearCacheSelected = view.findViewById(R.id.amr_btn_clear_cache_selected);
        TextView mBtnMakeAsReadSelected = view.findViewById(R.id.amr_btn_make_as_read_selected);
        TextView mBtnReadAllSelected = view.findViewById(R.id.amr_btn_read_all_selected);


        mBtnRemoveSelected.setOnClickListener(v -> {
            if (mSelectedRoomList.size() > 0)
                confirmActionForRemoveSelected();
        });

        mBtnClearCacheSelected.setOnClickListener(v -> {
            if (mSelectedRoomList.size() > 0) {
                confirmActionForClearHistoryOfSelected();
            }
        });

        mBtnMakeAsReadSelected.setOnClickListener(v -> {

            if (mSelectedRoomList.size() > 0) {
                for (int i = 0; i < mSelectedRoomList.size(); i++) {
                    markAsRead(mSelectedRoomList.get(i).getType(), mSelectedRoomList.get(i).getId());
                }
                onLeftIconClickListener(v);
            }
        });

        mBtnReadAllSelected.setOnClickListener(v -> {

            RealmResults<RealmRoom> unreadList = getRealmFragmentMain().where(RealmRoom.class).greaterThan(RealmRoomFields.UNREAD_COUNT, 0).equalTo(RealmRoomFields.IS_DELETED, false).findAll();

            if (unreadList.size() == 0) {
                Toast.makeText(getContext(), getString(R.string.no_item), Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                    .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                    .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();

                        for (RealmRoom room : unreadList) {
                            markAsRead(room.getType(), room.getId());
                        }

                        onLeftIconClickListener(v);
                    })
                    .onNegative((dialog, which) -> dialog.dismiss())
                    .show();

        });

        if (G.isDarkTheme) {
            setColorToDarkMode(mBtnRemoveSelected);
            setColorToDarkMode(mBtnClearCacheSelected);
            setColorToDarkMode(mBtnMakeAsReadSelected);
            setColorToDarkMode(mBtnReadAllSelected);
        } else {
            setColorToLightMode(mBtnRemoveSelected);
            setColorToLightMode(mBtnClearCacheSelected);
            setColorToLightMode(mBtnMakeAsReadSelected);
            setColorToLightMode(mBtnReadAllSelected);
        }

        onChatCellClickedInEditMode = (v, item, position, status) -> {

            if (!status) {
                mSelectedRoomList.add(item);
            } else {
                mSelectedRoomList.remove(item);
            }
            refreshChatList(position, false);
            //setVisiblityForSelectedActionsInEverySelection();
        };

        if (MusicPlayer.playerStateChangeListener != null) {
            MusicPlayer.playerStateChangeListener.observe(this, isVisible -> {
                notifyChatRoomsList();

                if (!mHelperToolbar.getmSearchBox().isShown()) {
                    mHelperToolbar.animateSearchBox(false, 0, 0);
                }
            });
        }

        EventManager.getInstance().addEventListener(ActivityCall.CALL_EVENT, this);

        mRecyclerView = view.findViewById(R.id.cl_recycler_view_contact);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        initRecycleView();

        //check is available forward message
        setForwardMessage(true);

        //just check at first time page loaded
        notifyChatRoomsList();

    }

    private void notifyChatRoomsList() {

        try {
            if (mRecyclerView != null) {

                if (MusicPlayer.mainLayout != null && MusicPlayer.mainLayout.isShown() && isChatMultiSelectEnable) {
                    setMargin(R.dimen.margin_for_below_layouts_of_toolbar_with_music_player);
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp4), 0, 0);
                    return;
                }

                if (G.isInCall && isChatMultiSelectEnable) {
                    setMargin(R.dimen.margin_for_below_layouts_of_toolbar_with_call_layout);
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp4), 0, 0);
                    return;
                }

                setMargin(R.dimen.margin_for_below_layouts_of_toolbar_with_search);

                if (MusicPlayer.mainLayout != null && MusicPlayer.mainLayout.isShown()) {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp68), 0, 0);
                } else if (G.isInCall) {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp60), 0, 0);
                } else if (isChatMultiSelectEnable) {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp1), 0, 0);
                } else if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp68), 0, 0);
                } else {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp24), 0, 0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setColorToDarkMode(TextView textView) {
        textView.setBackground(getResources().getDrawable(R.drawable.round_button_enabled_bg));
        textView.setTextColor(getResources().getColor(R.color.white));
    }

    private void setColorToLightMode(TextView textView) {
        textView.setBackground(getResources().getDrawable(R.drawable.round_button_disabled_bg));
        textView.setTextColor(getResources().getColor(R.color.gray_4c));
    }

    private void refreshChatList(int pos, boolean isRefreshAll) {
        if (mRecyclerView.getAdapter() != null) {
            if (isRefreshAll) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                mRecyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void initRecycleView() {

        if (results == null) {
            String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME};
            Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
            RealmQuery<RealmRoom> temp = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false);
            results = temp.sort(fieldNames, sort).findAllAsync();
            roomAdapter = new RoomAdapter(results, this, viewById, pbLoading);
            getChatLists();
        } else {
            pbLoading.setVisibility(View.GONE);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                }

                //check if music player was enable disable scroll detecting for search box
                if (G.isInCall || isChatMultiSelectEnable || (MusicPlayer.mainLayout != null && MusicPlayer.mainLayout.isShown())) {
                    if (mHelperToolbar.getmSearchBox() != null) {
                        if (!mHelperToolbar.getmSearchBox().isShown()) {
                            mHelperToolbar.animateSearchBox(false, 0, 0);

                        }

                        return;
                    }
                }

                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                //check recycler scroll for search box animation
                if (dy <= 0) {
                    // Scrolling up
                    mHelperToolbar.animateSearchBox(false, position, -3);
                } else {
                    // Scrolling down
                    mHelperToolbar.animateSearchBox(true, position, -3);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mRecyclerView.setAdapter(roomAdapter);

        G.onNotifyTime = () -> G.handler.post(() -> {
            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    //***************************************************************************************************************************


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
        if (G.isSecure && G.userLogin && mOffset == 0) {
            boolean send = new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
            if (send)
                progressBar.setVisibility(View.VISIBLE);
        } else {
            G.handler.postDelayed(this::getChatLists, 1000);
        }
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
        new RequestClientMuteRoom().muteRoom(roomId, !mute);
    }

    private void clearHistory(final long roomId) {
        RealmRoomMessage.clearHistoryMessage(roomId);
    }

    private void pinToTop(final long roomId, final boolean isPinned) {

        new RequestClientPinRoom().pinRoom(roomId, !isPinned);
        if (!isPinned) {
            goToTop();
        }
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

    private boolean checkValidationForRealm(RealmRoom realmRoom) {
        return realmRoom != null && realmRoom.isManaged() && realmRoom.isValid() && realmRoom.isLoaded();
    }


    /**
     * ************************************ Callbacks ************************************
     */
    @Override
    public void onChange() {
        G.handler.post(() -> {
            if (mRecyclerView.getAdapter() != null) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
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

    }

    @Override
    public void onChatDelete(final long roomId) {

    }

    @Override
    public void onGroupDelete(long roomId) {

    }

    @Override
    public void onGroupDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onGroupDeleteTimeOut() {

    }

    @Override
    public void onChannelDelete(long roomId) {

    }

    @Override
    public void onChannelDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChannelDeleteTimeOut() {

    }

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {

    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {

    }

    @Override
    public void onMessageFailed(long roomId, long messageId) {

    }

    @Override
    public void onClientGetRoomResponse(final long roomId) {

    }

    @Override
    public synchronized void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, RequestClientGetRoomList.IdentityGetRoomList identity) {
        // todo : we must change roomList with the change of out client condition. merge roomList with clientCondition.
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
        /*if (majorCode == 9) {
            if (G.currentActivity != null) {
                G.currentActivity.finish();
            }
            Intent intent = new Intent(context, ActivityRegistration.class);
            intent.putExtra(ActivityRegistration.showProfile, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }*/

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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        realmFragmentMain.close();
        EventManager.getInstance().removeEventListener(ActivityCall.CALL_EVENT, this);
        mHelperToolbar.unRegisterTimerBroadcast();

    }

    @Override
    public void onResume() {
        super.onResume();

        G.onSetActionInRoom = this;
        G.onDateChanged = this;
        if (G.isDepricatedApp)
            isDeprecated();

        G.onClientGetRoomListResponse = this;

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }

        try {
            mHelperToolbar.checkIsAvailableOnGoingCall();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mHelperToolbar != null) {
            mHelperToolbar.checkPassCodeVisibility();
        }

        boolean canUpdate = false;

        if (G.isUpdateNotificaionColorMain) {
            canUpdate = true;
            G.isUpdateNotificaionColorMain = false;
        }

        if (canUpdate) {

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();

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
                                    .positiveText(R.string.startUpdate).itemsGravity(GravityEnum.START).onPositive((dialog, which) -> {
                                String url = "http://d.igap.net/update";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
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
                getActivity().runOnUiThread(() -> {
                    if (getActivity().hasWindowFocus()) {
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.igap_update).titleColor(Color.parseColor("#1DE9B6"))
                                .titleGravity(GravityEnum.CENTER)
                                .buttonsGravity(GravityEnum.CENTER)
                                .content(R.string.new_version_avilable).contentGravity(GravityEnum.CENTER)
                                .negativeText(R.string.ignore).negativeColor(Color.parseColor("#798e89")).onNegative((dialog, which) -> dialog.dismiss()).positiveText(R.string.startUpdate).onPositive((dialog, which) -> {
                            String url = "http://d.igap.net/update";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            dialog.dismiss();
                        })
                                .show();
                    }
                });
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {

        if (!(G.isLandscape && G.twoPaneMode) && FragmentChat.mForwardMessages != null) {
            revertToolbarFromForwardMode();
            return;
        }

        if (isChatMultiSelectEnable) {
            mLayoutMultiSelectedActions.setVisibility(View.GONE);
            isChatMultiSelectEnable = false;
            refreshChatList(0, true);
            mHelperToolbar.getRightButton().setVisibility(View.VISIBLE);
            mHelperToolbar.getScannerButton().setVisibility(View.VISIBLE);
            if (G.isPassCode) mHelperToolbar.getPassCodeButton().setVisibility(View.VISIBLE);
            mHelperToolbar.setLeftIcon(R.string.edit_icon);
            mSelectedRoomList.clear();
        } else {
            mLayoutMultiSelectedActions.setVisibility(View.VISIBLE);
            isChatMultiSelectEnable = true;
            refreshChatList(0, true);
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
            mHelperToolbar.getScannerButton().setVisibility(View.GONE);
            mHelperToolbar.getPassCodeButton().setVisibility(View.GONE);
            if (!mHelperToolbar.getmSearchBox().isShown()) {
                mHelperToolbar.animateSearchBox(false, 0, 0);
            }
            mHelperToolbar.setLeftIcon(R.string.back_icon);

        }

        notifyChatRoomsList();
    }

    public void revertToolbarFromForwardMode() {
        FragmentChat.mForwardMessages = null;
        mHelperToolbar.setDefaultTitle(getString(R.string.app_name));
        mHelperToolbar.getRightButton().setVisibility(View.VISIBLE);
        mHelperToolbar.getScannerButton().setVisibility(View.VISIBLE);
        if (G.isPassCode) mHelperToolbar.getPassCodeButton().setVisibility(View.VISIBLE);
        mHelperToolbar.setLeftIcon(R.string.edit_icon);
    }

    @Override
    public void onSearchClickListener(View view) {
        if (getActivity() != null) {
            Fragment fragment = SearchFragment.newInstance();
            try {
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment)
                        .setAnimated(true)
                        .setReplace(false)
                        .setAnimation(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .load();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onToolbarTitleClickListener(View view) {
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onRightIconClickListener(View view) {
        Fragment fragment = RegisteredContactsFragment.newInstance(true, false, RegisteredContactsFragment.ADD);
        try {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment)
                        .setReplace(false).setReplace(false).load();
            }
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
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    if (mSelectedRoomList.size() > 0) {

                        for (RealmRoom item : mSelectedRoomList) {
                            deleteChat(item);
                        }

                        onLeftIconClickListener(null);
                    }
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    public void checkPassCodeIconVisibility() {

        if (mHelperToolbar != null) {
            mHelperToolbar.checkPassCodeVisibility();
        }

    }

    private void confirmActionForClearHistoryOfSelected() {

        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.clear_history))
                .content(getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    for (RealmRoom item : mSelectedRoomList) {
                        clearHistory(item.getId());
                    }
                    onLeftIconClickListener(null);

                })
                .onNegative((dialog, which) -> dialog.dismiss())
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
                    break;
                }

            } else {
                mBtnRemoveSelected.setVisibility(View.GONE);
                break;
            }

        }
    }

    private void setMargin(int mTop) {
        constraintSet.setMargin(selectLayoutRoot.getId(), ConstraintSet.TOP, i_Dp(mTop));
        constraintSet.applyTo(root);
    }

    private void markAsRead(ProtoGlobal.Room.Type chatType, long roomId) {

        G.handler.postDelayed(() -> {
            Realm realm = Realm.getDefaultInstance();
            if (chatType == ProtoGlobal.Room.Type.CHAT || chatType == ProtoGlobal.Room.Type.GROUP) {
                RealmRoomMessage.fetchMessages(realm, roomId, new OnActivityChatStart() {
                    @Override
                    public void sendSeenStatus(RealmRoomMessage message) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, message.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                    }

                    @Override
                    public void resendMessage(RealmRoomMessage message) {

                    }

                    @Override
                    public void resendMessageNeedsUpload(RealmRoomMessage message, long messageId) {

                    }
                });
            }

            RealmRoom.setCount(roomId, 0);

            G.handler.postDelayed(() -> {
                AppUtils.updateBadgeOnly(realm, roomId);
                realm.close();
            }, 250);
        }, 5);
    }

    @Override
    public boolean isAllowToBackPressed() {
        if (isChatMultiSelectEnable) {
            onLeftIconClickListener(null);
            return false;
        } else if (FragmentChat.mForwardMessages != null) {
            revertToolbarFromForwardMode();
            return false;
        } else {
            return true;
        }
    }

    /**
     * receive call state from event bus
     * and change visibility of toolbar layout
     */
    @Override
    public void receivedMessage(int id, Object... message) {

        if (id == ActivityCall.CALL_EVENT) {
            if (message == null || message.length == 0) return;
            boolean state = (boolean) message[0];
            G.handler.post(() -> {
                notifyChatRoomsList();
                if (!mHelperToolbar.getmSearchBox().isShown())
                    mHelperToolbar.animateSearchBox(false, 0, 0);
                mHelperToolbar.getCallLayout().setVisibility(state ? View.VISIBLE : View.GONE);
                if (MusicPlayer.chatLayout != null) MusicPlayer.chatLayout.setVisibility(View.GONE);
                if (MusicPlayer.mainLayout != null) MusicPlayer.mainLayout.setVisibility(View.GONE);
            });
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

        public RoomAdapter(@Nullable OrderedRealmCollection<RealmRoom> data, OnComplete complete, View emptyView, View loadingView) {
            super(data, true);
            this.mComplete = complete;
            this.emptyView = emptyView;
            this.loadingView = loadingView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout rootView = new ChatCell(getContext());
            rootView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp70)));
            return new ViewHolder(rootView);
        }

        @Override
        protected OrderedRealmCollectionChangeListener createListener() {

            return new OrderedRealmCollectionChangeListener<RealmResults<RealmRoom>>() {
                @Override
                public void onChange(RealmResults<RealmRoom> collection, OrderedCollectionChangeSet changeSet) {
                    if (G.onUnreadChange != null) {
                        int unreadCount = 0;
                        for (RealmRoom room : collection) {
                            if (!room.getMute() && !room.isDeleted() && room.getUnreadCount() > 0) {
                                unreadCount += room.getUnreadCount();
                            }
                        }
                        G.onUnreadChange.onChange(unreadCount);
                    }

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

            if (isChatMultiSelectEnable) {
                holder.getRootView().getCellCb().setVisibility(View.VISIBLE);

                if (isItemAvailableOnSelectedList(mInfo)) {
                    holder.getRootView().getCellCb().setChecked(true);
                } else {
                    holder.getRootView().getCellCb().setChecked(false);
                }

            } else {
                holder.getRootView().getCellCb().setVisibility(View.GONE);
                holder.getRootView().getCellCb().setChecked(false);
            }
            final boolean isMyCloud;

            isMyCloud = mInfo.getChatRoom() != null && mInfo.getChatRoom().getPeerId() > 0 && mInfo.getChatRoom().getPeerId() == userId;

            if (mInfo.isValid()) {

                setLastMessage(mInfo, holder);
                getLastMessage(mInfo, holder.getRootView().getLastMessage());

                if (isMyCloud) {
                    avatarHandler.removeImageViewFromHandler(holder.getRootView().getAvatarImageView());
                    holder.getRootView().getAvatarImageView().setImageResource(R.drawable.ic_cloud_space_blue);

                } else {
                    if (holder.getRootView().getAvatarImageView().getVisibility() == View.GONE) {
                        holder.getRootView().getAvatarImageView().setVisibility(View.VISIBLE);
                    }
                    setAvatar(mInfo, holder);
                }

                setChatIcon(mInfo, holder.getRootView().getChatIconTv());
                setMuteIcon(mInfo, holder.getRootView().getMuteIconTv());

                holder.getRootView().getRoomNameTv().setText(mInfo.getTitle());

                if ((mInfo.getType() == CHAT) && mInfo.getChatRoom().isVerified()) {
                    holder.getRootView().getVerifyIconTv().setVisibility(View.VISIBLE);
                } else if ((mInfo.getType() == CHANNEL) && mInfo.getChannelRoom().isVerified()) {
                    holder.getRootView().getVerifyIconTv().setVisibility(View.VISIBLE);
                } else {
                    holder.getRootView().getVerifyIconTv().setVisibility(View.INVISIBLE);
                }

                if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
                    holder.getRootView().getLastMessageDate().setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
                } else {
                    holder.getRootView().getLastMessageDate().setVisibility(View.GONE);
                }

                /**
                 * ********************* unread *********************
                 */

                if (mInfo.isPinned()) {
                    if (mInfo.isFromPromote()) {
                        holder.getRootView().getPinView().setVisibility(View.GONE);
                    } else {
                        holder.getRootView().getPinView().setVisibility(View.VISIBLE);
                    }

                } else {
                    holder.getRootView().getPinView().setVisibility(View.GONE);
                }

                if (mInfo.getUnreadCount() < 1) {
                    holder.getRootView().getBadgeView().setVisibility(View.GONE);
                } else {
                    holder.getRootView().getBadgeView().setVisibility(View.VISIBLE);
                    holder.getRootView().getBadgeView().setText(mInfo.getUnreadCount() > 999 ? "+999" : String.valueOf(mInfo.getUnreadCount()));
                    if (mInfo.getMute()) {
                        holder.getRootView().getBadgeView().setBadgeColor(getResources().getColor(R.color.gray_9d));
                    } else {
                        if (G.isDarkTheme) {
                            holder.getRootView().getBadgeView().setBadgeColor(getResources().getColor(R.color.md_blue_500));
                        } else {
                            holder.getRootView().getBadgeView().setBadgeColor(getResources().getColor(R.color.notification_badge));
                        }
                    }
                }


            }
        }

        private void setMuteIcon(RealmRoom mInfo, FontIconTextView muteTv) {
            if (mInfo.getMute())
                muteTv.setVisibility(View.VISIBLE);
            else
                muteTv.setVisibility(View.GONE);
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
        private void setLastMessage(RealmRoom mInfo, ViewHolder holder) {
            holder.getRootView().getLastMessageStatusTv().setVisibility(View.INVISIBLE);
            if (mInfo.getLastMessage() != null) {
                holder.getRootView().getLastMessageDate().setVisibility(View.VISIBLE);
                if (mInfo.getLastMessage().isAuthorMe()) {
                    holder.getRootView().getLastMessageStatusTv().setVisibility(View.VISIBLE);
                    ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
                    if (mInfo.getLastMessage().getStatus() != null) {
                        try {
                            status = ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus());
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                    AppUtils.rightMessageStatus(holder.getRootView().getLastMessageStatusTv(), status, mInfo.getLastMessage().isAuthorMe());
                }
            } else {
                holder.getRootView().getLastMessageDate().setVisibility(View.GONE);
            }
        }

        private void getLastMessage(RealmRoom room, EmojiTextView lastMessageTv) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            if (room.getActionState() != null && room.getActionStateUserId() != userId) {

                SpannableString typingSpannableString = new SpannableString(room.getActionState());
                typingSpannableString.setSpan(new ForegroundColorSpan(ChatCell.TYPING_COLOR), 0, room.getActionState().length(), 0);

                builder.append(typingSpannableString);

            } else if (room.getDraft() != null && !TextUtils.isEmpty(room.getDraft().getMessage())) {
                String draft = getResources().getString(R.string.txt_draft) + " ";

                SpannableString redSpannable = new SpannableString(draft);
                redSpannable.setSpan(new ForegroundColorSpan(ChatCell.DRAFT_COLOR), 0, draft.length(), 0);

                String draftMessage = room.getDraft().getMessage();
                SpannableString message = new SpannableString(draftMessage);
                message.setSpan(new ForegroundColorSpan(ChatCell.messageColor()), 0, message.length(), 0);

                builder.append(redSpannable);
                builder.append(message);

            } else {
                boolean haveAttachment = false;
                boolean haveSenderName = false;
                boolean appIsRtl = G.isAppRtl;
                boolean nameIsPersian = false;


                RealmRoomMessage lastMessage;
                if (room.getLastMessage() != null) {
                    if (room.getLastMessage().getForwardMessage() != null) {
                        lastMessage = room.getLastMessage().getForwardMessage();
                    } else {
                        lastMessage = room.getLastMessage();
                    }

                    if (lastMessage.isDeleted()) {
                        String deletedMessage = getResources().getString(R.string.deleted_message);
                        SpannableString deletedSpannable = new SpannableString(deletedMessage);
                        deletedSpannable.setSpan(new ForegroundColorSpan(ChatCell.DELETED_COLOR), 0, deletedMessage.length(), 0);
                        builder.append(deletedSpannable);
                        lastMessageTv.setText(builder, TextView.BufferType.SPANNABLE);
                        return;
                    }

                    if (lastMessage.getMessage() != null) {
                        String attachmentTag = null;
                        String senderNameTag = null;
                        SpannableString attachmentSpannable = null;
                        SpannableString senderNameSpannable = null;
                        SpannableString lastMessageSpannable;
                        SpannableString senderNameQuoteSpannable = null;


                        if (room.getType() == GROUP) {
                            if (lastMessage.isAuthorMe() && lastMessage.getMessageType() != ProtoGlobal.RoomMessageType.LOG) {
                                senderNameTag = getResources().getString(R.string.txt_you);
                                senderNameSpannable = new SpannableString(senderNameTag);
                            } else {
                                if (lastMessage.getMessageType() != ProtoGlobal.RoomMessageType.LOG) {
                                    RealmRegisteredInfo realmRegisteredInfo;
                                    if (room.getLastMessage().getForwardMessage() != null)
                                        realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmFragmentMain(), room.getLastMessage().getUserId());
                                    else
                                        realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmFragmentMain(), lastMessage.getUserId());

                                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
                                        senderNameTag = realmRegisteredInfo.getDisplayName();
                                        senderNameSpannable = new SpannableString(senderNameTag);
                                        nameIsPersian = Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
                                    }
                                }
                            }

                            if (senderNameSpannable != null) {
                                haveSenderName = true;
                                senderNameSpannable.setSpan(new ForegroundColorSpan(ChatCell.SENDER_COLOR), 0, senderNameTag.length(), 0);
                            }
                        }

                        switch (lastMessage.getMessageType()) {
                            case IMAGE_TEXT:
                                attachmentTag = AppUtils.getEmojiByUnicode(ChatCell.IMAGE);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case GIF_TEXT:
                                attachmentTag = AppUtils.getEmojiByUnicode(ChatCell.GIF);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case FILE_TEXT:
                                attachmentTag = AppUtils.getEmojiByUnicode(ChatCell.FILE);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case VIDEO_TEXT:
                                attachmentTag = AppUtils.getEmojiByUnicode(ChatCell.VIDEO);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case AUDIO_TEXT:
                                attachmentTag = AppUtils.getEmojiByUnicode(ChatCell.MUSIC);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case GIF:
                                attachmentTag = getResources().getString(R.string.gif_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case VOICE:
                                attachmentTag = getResources().getString(R.string.voice_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case LOG:
                                attachmentTag = HelperLogMessage.deserializeLog(lastMessage.getLogs(), false).toString();
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case AUDIO:
                                attachmentTag = AppUtils.getEmojiByUnicode(ChatCell.MUSIC) + lastMessage.getAttachment().getName();
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case FILE:
                                attachmentTag = getResources().getString(R.string.file_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case IMAGE:
                                attachmentTag = getResources().getString(R.string.image_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case VIDEO:
                                attachmentTag = getResources().getString(R.string.video_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case WALLET:
                                builder.append(AppUtils.getEmojiByUnicode(ChatCell.WALLET));
                                if (lastMessage.getRoomMessageWallet() != null) {
                                    String type = lastMessage.getRoomMessageWallet().getType();
                                    if (type.equals(CARD_TO_CARD.toString())) {
                                        attachmentTag = getResources().getString(R.string.card_to_card_message);
                                    } else if (type.equals(PAYMENT.toString())) {
                                        attachmentTag = getResources().getString(R.string.payment_message);
                                    } else if (type.equals(MONEY_TRANSFER.toString())) {
                                        attachmentTag = getResources().getString(R.string.wallet_message);
                                    } else {
                                        attachmentTag = getResources().getString(R.string.unknown_message);
                                    }
                                } else
                                    attachmentTag = getResources().getString(R.string.wallet_message);

                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case CONTACT:
                                attachmentTag = getResources().getString(R.string.contact_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case STICKER:
                                attachmentTag = getResources().getString(R.string.sticker_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                            case LOCATION:
                                attachmentTag = getResources().getString(R.string.location_message);
                                attachmentSpannable = new SpannableString(attachmentTag);
                                break;
                        }

                        if (attachmentSpannable != null) {
                            haveAttachment = true;
                            attachmentSpannable.setSpan(new ForegroundColorSpan(ChatCell.attachmentColor()), 0, attachmentTag.length(), 0);
                        }

                        if (haveSenderName) {
                            senderNameQuoteSpannable = new SpannableString(haveAttachment ? ":" : nameIsPersian ? ": " : ": ");
                            senderNameQuoteSpannable.setSpan(new ForegroundColorSpan(ChatCell.SENDER_COLOR), 0, senderNameQuoteSpannable.length(), 0);
                        }
                        String message;
                        if (lastMessage.getMessage().length() > 70) {
                            message = lastMessage.getMessage().substring(0, 70) + "...";
                        } else
                            message = lastMessage.getMessage();

                        if (HelperCalander.isPersianUnicode)
                            message = HelperCalander.convertToUnicodeFarsiNumber(message);

                        lastMessageSpannable = new SpannableString(subStringInternal(message));
                        lastMessageSpannable.setSpan(new ForegroundColorSpan(ChatCell.messageColor()), 0, lastMessageSpannable.length(), 0);

                        if (haveSenderName) {
                            if (haveAttachment) {
                                builder.append(senderNameSpannable).append(senderNameQuoteSpannable).append(attachmentSpannable).append(lastMessageSpannable);
                            } else
                                builder.append(senderNameSpannable).append(senderNameQuoteSpannable).append(lastMessageSpannable);
                        } else {
                            if (haveAttachment) {
                                builder.append(attachmentSpannable).append(lastMessageSpannable);
                            } else
                                builder.append(lastMessageSpannable);
                        }

                    }
                }
            }
            lastMessageTv.setText(builder, TextView.BufferType.SPANNABLE);
        }

        private void setAvatar(final RealmRoom mInfo, ViewHolder holder) {
            long idForGetAvatar;
            if (mInfo.getType() == CHAT) {
                idForGetAvatar = mInfo.getChatRoom().getPeerId();
            } else {
                idForGetAvatar = mInfo.getId();
            }

            Bitmap init = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp52), mInfo.getInitials(), mInfo.getColor());
            avatarHandler.getAvatar(new ParamWithInitBitmap(holder.getRootView().getAvatarImageView(), idForGetAvatar).initBitmap(init));
        }

        /**
         * change muteRoomTv icon for channel pv and group chats
         * font type face FontIconTextView
         */
        private void setChatIcon(RealmRoom mInfo, FontIconTextView chatIconTv) {
            if (mInfo.getType() == CHAT) {
                chatIconTv.setVisibility(View.GONE);
                chatIconTv.setText("");
            } else if (mInfo.getType() == GROUP) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText(R.string.group_icon);
            } else if (mInfo.getType() == CHANNEL) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText(R.string.channel_main_icon);
            } else {
                chatIconTv.setVisibility(View.GONE);
            }
        }

        //*******************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {
            private RealmRoom mInfo;
            private ChatCell rootView;

            public ViewHolder(View view) {
                super(view);
                rootView = (ChatCell) view;

                rootView.setOnClickListener(v -> {
                    if (isChatMultiSelectEnable) {
                        onChatCellClickedInEditMode.onClicked(rootView.getCellCb(), mInfo, getAdapterPosition(), rootView.getCellCb().isChecked());
                    } else {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {

                                boolean openChat = true;

                                if (G.twoPaneMode) {
                                    if (getActivity() != null) {
                                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                                        if (fragment != null) {

                                            FragmentChat fm = (FragmentChat) fragment;
                                            if (fm.isAdded() && fm.mRoomId == mInfo.getId()) {
                                                openChat = false;
                                            } else {
                                                removeFromBaseFragment(fragment);
                                            }
                                        }
                                    }
                                }

                                if (openChat) {
                                    new GoToChatActivity(mInfo.getId()).startActivity(getActivity());
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
                                    boolean isCloud = peerId > 0 && peerId == G.userId;
                                    MyDialog.showDialogMenuItemRooms(G.fragmentActivity, mInfo.getTitle(), mInfo.getType(), mInfo.getMute(), role, peerId, isCloud, mInfo, new OnComplete() {
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

            public ChatCell getRootView() {
                return rootView;
            }
        }
    }

    //check state of forward message from chat room and show on toolbar
    public void setForwardMessage(boolean enable) {

        if (!(G.isLandscape && G.twoPaneMode) && FragmentChat.mForwardMessages != null) {
            if (enable) {
                mHelperToolbar.setDefaultTitle(getString(R.string.send_message_to) + "...");
                mHelperToolbar.getRightButton().setVisibility(View.GONE);
                mHelperToolbar.getScannerButton().setVisibility(View.GONE);
                if (G.isPassCode) mHelperToolbar.getPassCodeButton().setVisibility(View.GONE);
                mHelperToolbar.setLeftIcon(R.string.back_icon);
            } else {
                revertToolbarFromForwardMode();
            }
        }

    }
}
