package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import net.iGap.Config;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.adapter.RoomListAdapter;
import net.iGap.adapter.SelectedItemAdapter;
import net.iGap.adapter.items.cells.RoomListCell;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnChannelDeleteInRoomList;
import net.iGap.interfaces.OnChatDeleteInRoomList;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.interfaces.OnDateChanged;
import net.iGap.interfaces.OnGroupDeleteInRoomList;
import net.iGap.interfaces.OnRemoveFragment;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnVersionCallBack;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.MultiSelectStruct;
import net.iGap.module.AppUtils;
import net.iGap.module.BotInit;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
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

import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.clientConditionGlobal;
import static net.iGap.G.isAppRtl;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.realm.RealmRoom.putChatToDatabase;

public class FragmentMain extends BaseMainFragments implements ToolbarListener, EventListener, OnClientGetRoomListResponse, OnVersionCallBack, OnSetActionInRoom, OnRemoveFragment, OnChatUpdateStatusResponse, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChannelDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    private static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";

    private boolean isThereAnyMoreItemToLoad = true;
    private ProgressBar progressBar;
    public static int mOffset = 0;
    private View viewById;
    private RecyclerView mRecyclerView;
    private long tagId;
    private ProgressBar pbLoading;

    private RoomListAdapter roomListAdapter;
    private HelperToolbar mHelperToolbar;
    private boolean isChatMultiSelectEnable = false;
    private onChatCellClick onChatCellClickedInEditMode;
    private List<RealmRoom> mSelectedRoomList = new ArrayList<>();
    //    private TextView mBtnRemoveSelected;
    private RealmResults<RealmRoom> results;
    private ConstraintLayout root;
    private ConstraintSet constraintSet;
    private TextView selectedItemCountTv;
    private RecyclerView multiSelectRv;
    private SelectedItemAdapter selectedItemAdapter;
    private View selectedItemView;

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

        root = view.findViewById(R.id.amr_layout_root);
        constraintSet = new ConstraintSet();
        constraintSet.clone(root);

        progressBar = view.findViewById(R.id.ac_progress_bar_waiting);
        viewById = view.findViewById(R.id.empty_icon);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        viewById.setVisibility(View.GONE);

        ViewGroup layoutToolbar = view.findViewById(R.id.amr_layout_toolbar);

        selectedItemCountTv = view.findViewById(R.id.tv_main_selectedItemCount);
        multiSelectRv = view.findViewById(R.id.rv_main_selectedItem);
        selectedItemView = view.findViewById(R.id.amr_layout_selected_root);

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
        mHelperToolbar.getLeftButton().setVisibility(View.GONE);
        /*}*/


        onChatCellClickedInEditMode = (item, position, status) -> {
            if (!status) {
                mSelectedRoomList.add(item);
            } else {
                mSelectedRoomList.remove(item);
            }

            if (mSelectedRoomList.size() == 0) {
                disableMultiSelect();
                return;
            }

            if (selectedItemAdapter == null) {
                multiSelectRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                selectedItemAdapter = new SelectedItemAdapter();
                multiSelectRv.setAdapter(selectedItemAdapter);

            }

            if (mSelectedRoomList.size() == 1) {
                selectedItemAdapter.setItemsList(setMultiSelectAdapterItem(item, true));
            } else {
                selectedItemAdapter.setItemsList(setMultiSelectAdapterItem(item, false));
            }

            selectedItemAdapter.setCallBack(action -> {
                switch (action) {
                    case 0:
                        pinToTop(item.getId(), item.isPinned());
                        break;
                    case 1:
                        muteNotification(item.getId(), item.getMute());
                        disableMultiSelect();
                        break;
                    case 2:
                        clearHistory(item.getId(), true);
                        break;
                    case 3:
                        deleteChat(item, true);
                        break;
                    case 4:
                        readAllRoom();
                        break;
                    case 5:
                        markAsRead();
                        break;
                    case 6:
                        if (mSelectedRoomList.size() > 0) {
                            confirmActionForClearHistoryOfSelected();
                        }
                        break;
                    case 7:
                        if (mSelectedRoomList.size() > 0) {
                            confirmActionForRemoveSelected();
                        }
                        break;

                }
            });

            refreshChatList(position, false);
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

    private void markAsRead() {
        if (mSelectedRoomList.size() > 0) {
            for (int i = 0; i < mSelectedRoomList.size(); i++) {
                markAsRead(mSelectedRoomList.get(i).getType(), mSelectedRoomList.get(i).getId());
            }
            disableMultiSelect();
        }
    }

    private void readAllRoom() {
        RealmResults<RealmRoom> unreadList = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).greaterThan(RealmRoomFields.UNREAD_COUNT, 0).equalTo(RealmRoomFields.IS_DELETED, false).findAll();
        });

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
                    disableMultiSelect();

                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
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
            RealmQuery<RealmRoom> temp = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false);
            });
            results = temp.sort(fieldNames, sort).findAllAsync();
            roomListAdapter = new RoomListAdapter(results, viewById, pbLoading, avatarHandler, mSelectedRoomList);
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

        mRecyclerView.setAdapter(roomListAdapter);

        roomListAdapter.setCallBack(new RoomListAdapter.OnMainFragmentCallBack() {
            @Override
            public void onClick(RoomListCell roomListCell, RealmRoom realmRoom, int adapterPosition) {
                if (isChatMultiSelectEnable) {
                    onChatCellClickedInEditMode.onClicked(realmRoom, adapterPosition, roomListCell.getCheckBox().isChecked());
                    selectedItemCountTv.setText(isAppRtl ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(mSelectedRoomList.size())) : String.valueOf(mSelectedRoomList.size()));
                } else {
                    if (realmRoom.isValid() && G.fragmentActivity != null) {
                        boolean openChat = true;
                        if (G.twoPaneMode) {
                            if (getActivity() != null) {
                                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                                if (fragment != null) {

                                    FragmentChat fm = (FragmentChat) fragment;
                                    if (fm.isAdded() && fm.mRoomId == realmRoom.getId()) {
                                        openChat = false;
                                    } else {
                                        removeFromBaseFragment(fragment);
                                    }
                                }
                            }
                        }
                        if (openChat) {
                            new GoToChatActivity(realmRoom.getId()).startActivity(getActivity());
                        }
                    }
                }
            }

            @Override
            public boolean onLongClick(RoomListCell roomListCell, RealmRoom realmRoom, int position) {

                if (!isChatMultiSelectEnable) {
                    enableMultiSelect();
                    selectedItemCountTv.setVisibility(View.VISIBLE);
                    multiSelectRv.setVisibility(View.VISIBLE);
                    onChatCellClickedInEditMode.onClicked(realmRoom, position, false);
                    selectedItemCountTv.setText(isAppRtl ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(mSelectedRoomList.size())) : String.valueOf(mSelectedRoomList.size()));
                }
                return true;
            }
        });


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

    private void deleteChat(RealmRoom item, boolean exit) {

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
        if (exit)
            disableMultiSelect();
    }

    private void muteNotification(final long roomId, final boolean mute) {
        new RequestClientMuteRoom().muteRoom(roomId, !mute);
    }

    private void clearHistory(final long roomId, boolean exit) {
        RealmRoomMessage.clearHistoryMessage(roomId);
        if (exit)
            disableMultiSelect();
    }

    private void pinToTop(final long roomId, final boolean isPinned) {

        new RequestClientPinRoom().pinRoom(roomId, !isPinned);
        if (!isPinned) {
            goToTop();
        }
        disableMultiSelect();
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

    //**************************************************************************************************************************************

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
        }

        if (isChatMultiSelectEnable) {
            disableMultiSelect();
        }
    }

    private void enableMultiSelect() {
        isChatMultiSelectEnable = true;
        mHelperToolbar.getRightButton().setVisibility(View.GONE);
        mHelperToolbar.getScannerButton().setVisibility(View.GONE);
        mHelperToolbar.getPassCodeButton().setVisibility(View.GONE);
        roomListAdapter.setMultiSelect(true);
        roomListAdapter.notifyDataSetChanged();
        mHelperToolbar.getLeftButton().setVisibility(View.VISIBLE);
        mHelperToolbar.setLeftIcon(R.string.back_icon);
        notifyChatRoomsList();
    }

    private void disableMultiSelect() {
        if (isChatMultiSelectEnable) {
            isChatMultiSelectEnable = false;
            selectedItemCountTv.setVisibility(View.GONE);
            multiSelectRv.setVisibility(View.GONE);
            mHelperToolbar.getmSearchBox().setVisibility(View.VISIBLE);
            mHelperToolbar.getRightButton().setVisibility(View.VISIBLE);
            mHelperToolbar.getScannerButton().setVisibility(View.VISIBLE);
            if (G.isPassCode) mHelperToolbar.getPassCodeButton().setVisibility(View.VISIBLE);
            mSelectedRoomList.clear();
            roomListAdapter.setMultiSelect(false);
            roomListAdapter.notifyDataSetChanged();
            notifyChatRoomsList();
        }
    }

    private List<MultiSelectStruct> setMultiSelectAdapterItem(RealmRoom realmRoom, boolean first) {
        List<MultiSelectStruct> items = new ArrayList<>();

        if (realmRoom.isValid() && G.fragmentActivity != null) {
            if (first) {
                String role = null;
                if (realmRoom.getType() == GROUP) {
                    role = realmRoom.getGroupRoom().getRole().toString();
                } else if (realmRoom.getType() == CHANNEL) {
                    role = realmRoom.getChannelRoom().getRole().toString();
                }

                if (!G.fragmentActivity.isFinishing()) {
                    long peerId = realmRoom.getChatRoom() != null ? realmRoom.getChatRoom().getPeerId() : 0;
                    boolean isCloud = peerId > 0 && peerId == G.userId;

                    int pinCount = DbManager.getInstance().doRealmTask(realm -> {
                        return realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_PINNED, true).findAll().size();
                    });


                    if (realmRoom.isPinned()) {
                        items.add(new MultiSelectStruct(0, R.string.Unpin_to_top));
                    } else if (pinCount < 5) {
                        items.add(new MultiSelectStruct(0, R.string.pin_to_top));
                    }

                    if (!isCloud) {
                        if (realmRoom.getMute()) {
                            items.add(new MultiSelectStruct(1, R.string.unmute));
                        } else {
                            items.add(new MultiSelectStruct(1, R.string.mute));
                        }
                    }
                    items.add(new MultiSelectStruct(2, R.string.clear_history));
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT) {
                        items.add(new MultiSelectStruct(3, R.string.delete_item_dialog) /*+ R.string.chat*/);
                    } else if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                        if (role.equals("OWNER")) {
                            items.add(new MultiSelectStruct(3, R.string.delete_item_dialog) /*+ R.string.group*/);
                        } else {
                            items.add(new MultiSelectStruct(3, R.string.left)  /*+R.string.group*/);
                        }
                    } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                        if (role.equals("OWNER")) {
                            items.add(new MultiSelectStruct(3, R.string.delete_item_dialog) /*+ R.string.channel*/);
                        } else {
                            items.add(new MultiSelectStruct(3, R.string.left) /*+ R.string.channel*/);
                        }
                    }
                }
                items.add(new MultiSelectStruct(4, R.string.read_all));
                items.add(new MultiSelectStruct(5, R.string.make_as_read));
            } else {
                items.add(new MultiSelectStruct(4, R.string.read_all));
                items.add(new MultiSelectStruct(5, R.string.make_as_read));
                items.add(new MultiSelectStruct(6, R.string.clear_history));
                items.add(new MultiSelectStruct(7, R.string.delete));
            }

        }
        return items;
    }

    public void revertToolbarFromForwardMode() {
        FragmentChat.mForwardMessages = null;
        mHelperToolbar.setDefaultTitle(getString(R.string.app_name));
        mHelperToolbar.getRightButton().setVisibility(View.VISIBLE);
        mHelperToolbar.getScannerButton().setVisibility(View.VISIBLE);
        if (G.isPassCode) mHelperToolbar.getPassCodeButton().setVisibility(View.VISIBLE);
        mHelperToolbar.getLeftButton().setVisibility(View.GONE);
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
                            deleteChat(item, false);
                        }
                        disableMultiSelect();

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
                        clearHistory(item.getId(), false);
                    }
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setMargin(int mTop) {
        constraintSet.setMargin(selectedItemView.getId(), ConstraintSet.TOP, i_Dp(mTop));
        constraintSet.applyTo(root);
    }

    private void markAsRead(ProtoGlobal.Room.Type chatType, long roomId) {

        G.handler.postDelayed(() -> {
            //ToDo: Check it for update badge after update sen status in db
            DbManager.getInstance().doRealmTask(realm -> {
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

                RealmRoom.setCount(realm, roomId, 0);

                G.handler.postDelayed(() -> DbManager.getInstance().doRealmTask(realm1 -> {
                    AppUtils.updateBadgeOnly(realm1, roomId);
                }), 250);
            });
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
        void onClicked(RealmRoom item, int pos, boolean status);
    }

    //check state of forward message from chat room and show on toolbar
    public void setForwardMessage(boolean enable) {

        if (!(G.isLandscape && G.twoPaneMode) && FragmentChat.mForwardMessages != null) {
            if (enable) {
                mHelperToolbar.setDefaultTitle(getString(R.string.send_message_to) + "...");
                mHelperToolbar.getRightButton().setVisibility(View.GONE);
                mHelperToolbar.getScannerButton().setVisibility(View.GONE);
                if (G.isPassCode) mHelperToolbar.getPassCodeButton().setVisibility(View.GONE);
                mHelperToolbar.getLeftButton().setVisibility(View.VISIBLE);
                mHelperToolbar.setLeftIcon(R.string.back_icon);
            } else {
                revertToolbarFromForwardMode();
            }
        }

    }
}
