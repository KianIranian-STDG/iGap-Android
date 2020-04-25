package net.iGap.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.RoomListAdapter;
import net.iGap.adapter.SelectedItemAdapter;
import net.iGap.adapter.items.cells.RoomListCell;
import net.iGap.helper.AsyncTransaction;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.model.MultiSelectStruct;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnChannelDeleteInRoomList;
import net.iGap.observers.interfaces.OnChatDeleteInRoomList;
import net.iGap.observers.interfaces.OnChatSendMessageResponse;
import net.iGap.observers.interfaces.OnChatUpdateStatusResponse;
import net.iGap.observers.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.observers.interfaces.OnDateChanged;
import net.iGap.observers.interfaces.OnGroupDeleteInRoomList;
import net.iGap.observers.interfaces.OnRemoveFragment;
import net.iGap.observers.interfaces.OnSetActionInRoom;
import net.iGap.observers.interfaces.OnVersionCallBack;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.Room;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientPinRoom;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupLeft;
import net.iGap.response.ClientGetRoomListResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.isAppRtl;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class FragmentMain extends BaseMainFragments implements ToolbarListener, EventListener, OnVersionCallBack, OnSetActionInRoom, OnRemoveFragment, OnChatUpdateStatusResponse, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChannelDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    private static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";

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
    private CopyOnWriteArrayList<Room> mSelectedRoomList = new CopyOnWriteArrayList<>();
    //    private TextView mBtnRemoveSelected;
    private RealmResults<RealmRoom> results;
    private ConstraintLayout root;
    private TextView selectedItemCountTv;
    private RecyclerView multiSelectRv;
    private SelectedItemAdapter multiSelectAdapter;
    private View selectedItemView;

    private AnimatorSet progressAnimatorSet;

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
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_ROOM_PAGE);
        tagId = System.currentTimeMillis();

        root = view.findViewById(R.id.amr_layout_root);

        progressBar = view.findViewById(R.id.ac_progress_bar_waiting);
        viewById = view.findViewById(R.id.empty_icon);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        viewById.setVisibility(View.GONE);

        ViewGroup layoutToolbar = view.findViewById(R.id.amr_layout_toolbar);

        selectedItemCountTv = view.findViewById(R.id.tv_main_selectedItemCount);
        multiSelectRv = view.findViewById(R.id.rv_main_selectedItem);
        selectedItemView = view.findViewById(R.id.amr_layout_selected_root);

        multiSelectRv.setLayoutManager(new LinearLayoutManager(multiSelectRv.getContext(), RecyclerView.HORIZONTAL, false));

        if (multiSelectAdapter == null) multiSelectAdapter = new SelectedItemAdapter();
        multiSelectRv.setAdapter(multiSelectAdapter);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
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

        if (results == null)
            changeRoomListProgress(true);

        onChatCellClickedInEditMode = (item, position, status) -> {
            Room temp = new Room(item.getId(), item.getType().name(), item.getTitle(), "", "");
            if (item.getType() == GROUP) {
                temp.setGroupRole(item.getGroupRoom().getRole().toString());
            } else if (item.getType() == CHANNEL) {
                temp.setChannelRole(item.getChannelRoom().getRole().toString());
            }
            if (!status) {
                mSelectedRoomList.add(temp);
            } else {
                mSelectedRoomList.remove(temp);
                if (mSelectedRoomList.size() > 0)
                    item = DbManager.getInstance().doRealmTask(realm -> {
                        return realm.where(RealmRoom.class)
                                .equalTo(RealmRoomFields.ID, mSelectedRoomList.get(mSelectedRoomList.size() - 1).getId()).findFirst();
                    });
            }

            if (mSelectedRoomList.size() == 0) {
                disableMultiSelect();
                mHelperToolbar.getLeftButton().setVisibility(View.GONE);
                return;
            }

            multiSelectAdapter.setItemsList(setMultiSelectAdapterItem(item, mSelectedRoomList.size() == 1));

            RealmRoom finalItem = item;
            multiSelectAdapter.setCallBack(action -> {
                switch (action) {
                    case 0:
                        confirmActionForPinToTop(finalItem.getId(), finalItem.isPinned());
                        break;
                    case 1:
                        muteNotification(finalItem.getId(), finalItem.getMute());
                        break;
                    case 2:
                        clearHistory(finalItem.getId(), true);
                        break;
                    case 3:
                        confirmActionForRemoveItem(finalItem);
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
            MusicPlayer.playerStateChangeListener.observe(getViewLifecycleOwner(), isVisible -> {
                notifyChatRoomsList();

                if (!mHelperToolbar.getmSearchBox().isShown()) {
                    mHelperToolbar.animateSearchBox(false, 0, 0);
                }
            });
        }

        EventManager.getInstance().addEventListener(EventManager.CALL_EVENT, this);
        EventManager.getInstance().addEventListener(EventManager.EMOJI_LOADED, this);
        EventManager.getInstance().addEventListener(EventManager.ROOM_LIST_CHANGED, this);

        mRecyclerView = view.findViewById(R.id.cl_recycler_view_contact);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        initRecycleView();

        //check is available forward,shared message
        setForwardMessage(true);
        checkHasSharedData(true);
        checkMultiSelectState();

        //just check at first time page loaded
        notifyChatRoomsList();

    }

    private void checkMultiSelectState() {
        if (isChatMultiSelectEnable) {
            enableMultiSelect();
            selectedItemCountTv.setVisibility(View.VISIBLE);
            multiSelectRv.setVisibility(View.VISIBLE);
            multiSelectAdapter.notifyDataSetChanged();
            selectedItemCountTv.setText(isAppRtl ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(mSelectedRoomList.size())) : String.valueOf(mSelectedRoomList.size()));
        }
    }

    private void markAsRead() {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    DbManager.getInstance().doRealmTask(realm -> {
                        AsyncTransaction.executeTransactionWithLoading(getActivity(), realm, new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (mSelectedRoomList.size() > 0) {
                                    for (int i = 0; i < mSelectedRoomList.size(); i++) {
                                        markAsRead(realm, mSelectedRoomList.get(i).getType(), mSelectedRoomList.get(i).getId());
                                    }
                                }
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                disableMultiSelect();
                            }
                        });
                    });
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void readAllRoom() {
        List<RealmRoom> unreadList = DbManager.getInstance().doRealmTask(realm -> {
            return realm.copyFromRealm(realm.where(RealmRoom.class).greaterThan(RealmRoomFields.UNREAD_COUNT, 0).equalTo(RealmRoomFields.IS_DELETED, false).findAll());
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
                    DbManager.getInstance().doRealmTask(realm -> {
                        AsyncTransaction.executeTransactionWithLoading(getActivity(), realm, new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (unreadList.size() > 0) {
                                    for (RealmRoom room : unreadList) {
                                        markAsRead(realm, room.getType(), room.getId());
                                    }
                                }
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                disableMultiSelect();
                            }
                        });
                    });

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
            results = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false).sort(new String[]{RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME}, new Sort[]{Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING}).findAllAsync();
            });
            roomListAdapter = new RoomListAdapter(results, viewById, pbLoading, avatarHandler, mSelectedRoomList, this::disableMultiSelect);
        } else {
            pbLoading.setVisibility(View.GONE);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!ClientGetRoomListResponse.roomListFetched)
                    if (mOffset > 0) {
                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastVisiblePosition + 10 >= mOffset) {
                            new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
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

                if (!isChatMultiSelectEnable && FragmentChat.mForwardMessages == null && !HelperGetDataFromOtherApp.hasSharedData) {
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

    private void invalidateViews() {
//        int count = mRecyclerView.getChildCount();
//        for (int i = 0; i < count; i++) {
//            mRecyclerView.getChildAt(i).invalidate();
//        }
        if (roomListAdapter != null) {
            roomListAdapter.notifyDataSetChanged();
        }
    }

    //***************************************************************************************************************************


    //***************************************************************************************************************************

    private void deleteChat(Room item, boolean exit) {

        if (item.getType() == CHAT) {
            new RequestChatDelete().chatDelete(item.getId());
        } else if (item.getType() == GROUP) {
            if (item.getGroupRole() == GroupChatRole.OWNER) {
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


            if (item.getChannelRole() == ChannelChatRole.OWNER) {
                new RequestChannelDelete().channelDelete(item.getId());
            } else {
                new RequestChannelLeft().channelLeft(item.getId());
            }
        }
        if (exit)
            disableMultiSelect();
    }

    private void deleteChatWithRealm(RealmRoom item, boolean exit) {

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
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    new RequestClientMuteRoom().muteRoom(roomId, !mute);
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearHistory(final long roomId, boolean exit) {
        RealmRoomMessage.clearHistoryMessage(roomId);
        if (exit)
            disableMultiSelect();
    }

    private void confirmActionForPinToTop(final long roomId, final boolean isPinned) {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    pinToTop(roomId, isPinned);
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
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
        RealmRoom.setAction(roomId, userId, HelperGetAction.getAction(roomId, userId, RealmRoom.detectType(roomId), clientAction));
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
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    //**************************************************************************************************************************************

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventManager.getInstance().removeEventListener(EventManager.CALL_EVENT, this);
        EventManager.getInstance().removeEventListener(EventManager.EMOJI_LOADED, this);
        EventManager.getInstance().removeEventListener(EventManager.ROOM_LIST_CHANGED, this);
        mHelperToolbar.unRegisterTimerBroadcast();

    }

    @Override
    public void onResume() {
        super.onResume();

        G.onSetActionInRoom = this;
        G.onDateChanged = this;
        G.onVersionCallBack = this;
        if (G.isDepricatedApp)
            isDeprecated();

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
    public void onStop() {
        super.onStop();
        G.onClientGetRoomListResponse = null;
        G.onSetActionInRoom = null;
        G.onDateChanged = null;
        G.onVersionCallBack = null;
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
                                    .titleColor(new Theme().getAccentColor(getActivity()))
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
                                .title(R.string.igap_update)
                                .titleColor(new Theme().getAccentColor(getActivity()))
                                .titleGravity(GravityEnum.CENTER)
                                .buttonsGravity(GravityEnum.CENTER)
                                .content(R.string.new_version_avilable)
                                .contentGravity(GravityEnum.CENTER)
                                .negativeText(R.string.ignore)
                                .negativeColor(new Theme().getAccentColor(getActivity()))
                                .onNegative((dialog, which) -> dialog.dismiss())
                                .positiveText(R.string.startUpdate)
                                .onPositive((dialog, which) -> {
                                    try {
                                        String url = "http://d.igap.net/update";
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                        dialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), R.string.need_browser, Toast.LENGTH_SHORT).show();
                                    }
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
        if (!(G.isLandscape && G.twoPaneMode)) {
            if (FragmentChat.mForwardMessages != null || HelperGetDataFromOtherApp.hasSharedData)
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
            mHelperToolbar.getLeftButton().setVisibility(View.GONE);
            if (PassCode.getInstance().isPassCode())
                mHelperToolbar.getPassCodeButton().setVisibility(View.VISIBLE);
            mSelectedRoomList.clear();
            roomListAdapter.setMultiSelect(false);
            roomListAdapter.notifyDataSetChanged();
            notifyChatRoomsList();
        }
    }

    private List<MultiSelectStruct> setMultiSelectAdapterItem(RealmRoom realmRoom, boolean first) {
        List<MultiSelectStruct> items = new ArrayList<>();

        if (realmRoom.isValid() && G.fragmentActivity != null) {

            boolean isHasChannelInList = checkHasChannelInSelectedItems();
            String role = null;
            if (realmRoom.getType() == GROUP) {
                role = realmRoom.getGroupRoom().getRole().toString();
            } else if (realmRoom.getType() == CHANNEL) {
                role = realmRoom.getChannelRoom().getRole().toString();
            }

            if (first) {
                if (!G.fragmentActivity.isFinishing()) {
                    long peerId = realmRoom.getChatRoom() != null ? realmRoom.getChatRoom().getPeerId() : 0;
                    boolean isCloud = peerId > 0 && peerId == AccountManager.getInstance().getCurrentUser().getId();

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

            if (isHasChannelInList) {
                items.remove(new MultiSelectStruct(6, R.string.clear_history));
            }
        }
        return items;
    }

    private boolean checkHasChannelInSelectedItems() {
        for (Room room : mSelectedRoomList)
            if (room != null && room.getType() == CHANNEL) return true;
        return false;
    }

    public void revertToolbarFromForwardMode() {
        FragmentChat.mForwardMessages = null;
        HelperGetDataFromOtherApp.hasSharedData = false;
        HelperGetDataFromOtherApp.sharedList.clear();
        checkConnectionStateAndSetToolbarTitle();
        mHelperToolbar.getRightButton().setVisibility(View.VISIBLE);
        mHelperToolbar.getScannerButton().setVisibility(View.VISIBLE);
        if (PassCode.getInstance().isPassCode())
            mHelperToolbar.getPassCodeButton().setVisibility(View.VISIBLE);
        mHelperToolbar.getLeftButton().setVisibility(View.GONE);
    }

    private void checkConnectionStateAndSetToolbarTitle() {

        //check first time state then for every changes observer will change title
        if (G.connectionState != null) {
            if (G.connectionState == ConnectionState.CONNECTING) {
                mHelperToolbar.getTextViewLogo().setText(getString(R.string.connecting));
                mHelperToolbar.changeDefaultTitle(getString(R.string.app_name));
                mHelperToolbar.checkIGapFont();
            } else if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                mHelperToolbar.getTextViewLogo().setText(getString(R.string.waiting_for_network));
                mHelperToolbar.changeDefaultTitle(getString(R.string.app_name));
                mHelperToolbar.checkIGapFont();
            } else {
                mHelperToolbar.setDefaultTitle(getString(R.string.app_name));
            }
        } else {
            mHelperToolbar.setDefaultTitle(getString(R.string.app_name));
        }
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
        scrollToTopOfList();
    }

    @Override
    public void onRightIconClickListener(View view) {
        Fragment fragment = RegisteredContactsFragment.newInstance(true, false, RegisteredContactsFragment.ADD);
        try {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
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

                        for (Room item : mSelectedRoomList) {
                            deleteChat(item, false);
                        }
                        disableMultiSelect();

                    }
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void confirmActionForRemoveItem(RealmRoom item) {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.delete_chat))
                .content(getString(R.string.are_you_sure_request)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    deleteChatWithRealm(item, true);
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
                    for (Room item : mSelectedRoomList) {
                        clearHistory(item.getId(), false);
                    }
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setMargin(int mTop) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(root);
        constraintSet.setMargin(selectedItemView.getId(), ConstraintSet.TOP, i_Dp(mTop));
        constraintSet.applyTo(root);
    }

    private void markAsRead(Realm realm, ProtoGlobal.Room.Type chatType, long roomId) {
        //ToDo: Check it for update badge after update sen status in db
        if (chatType == ProtoGlobal.Room.Type.CHAT || chatType == ProtoGlobal.Room.Type.GROUP) {
            RealmRoomMessage.makeSeenAllMessageOfRoom(roomId);
        }

        RealmRoom.setCount(realm, roomId, 0);

        G.handler.postDelayed(() -> DbManager.getInstance().doRealmTask(realm1 -> {
            AppUtils.updateBadgeOnly(realm1, roomId);
        }), 250);
    }

    @Override
    public boolean isAllowToBackPressed() {
        if (isChatMultiSelectEnable) {
            onLeftIconClickListener(null);
            return false;
        } else if (FragmentChat.mForwardMessages != null || HelperGetDataFromOtherApp.hasSharedData) {
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

        if (id == EventManager.CALL_EVENT) {
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
        } else if (id == EventManager.EMOJI_LOADED) {
            G.handler.post(this::invalidateViews);
        } else if (id == EventManager.ROOM_LIST_CHANGED) {
            G.runOnUiThread(() -> {
                boolean show = (boolean) message[0];
                changeRoomListProgress(show);
            });
        }
    }

    public enum MainType {
        all, chat, group, channel
    }

    private interface onChatCellClick {
        void onClicked(RealmRoom item, int pos, boolean status);
    }

    private void changeRoomListProgress(boolean show) {

        if (show && progressBar.getTag() == null || !show && progressBar.getTag() != null) {
            return;
        }

        if (progressAnimatorSet != null) {
            progressAnimatorSet.cancel();
            progressAnimatorSet = null;
        }

        if (show)
            progressBar.setVisibility(View.VISIBLE);

        progressBar.setTag(show ? null : 1);

        progressAnimatorSet = new AnimatorSet();
        progressAnimatorSet.playTogether(ObjectAnimator.ofFloat(progressBar, View.TRANSLATION_Y, show ? 0 : LayoutCreator.dp(100)));
        progressAnimatorSet.setDuration(200);
        progressAnimatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        progressAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progressBar.setVisibility(View.GONE);
            }
        });
        progressAnimatorSet.start();
    }

    @Override
    public void scrollToTopOfList() {
        if (mRecyclerView != null) mRecyclerView.smoothScrollToPosition(0);
    }

    //check state of forward message from chat room and show on toolbar
    public void setForwardMessage(boolean enable) {

        if (!(G.isLandscape && G.twoPaneMode) && (FragmentChat.mForwardMessages != null || FragmentChat.structIGSticker != null)) {
            if (enable) {
                mHelperToolbar.setDefaultTitle(getString(R.string.send_message_to) + "...");
                mHelperToolbar.getRightButton().setVisibility(View.GONE);
                mHelperToolbar.getScannerButton().setVisibility(View.GONE);
                if (PassCode.getInstance().isPassCode())
                    mHelperToolbar.getPassCodeButton().setVisibility(View.GONE);
                mHelperToolbar.getLeftButton().setVisibility(View.VISIBLE);
                mHelperToolbar.setLeftIcon(R.string.back_icon);
            } else {
                revertToolbarFromForwardMode();
            }
        }

    }

    public void checkHasSharedData(boolean enable) {

        if (!(G.isLandscape && G.twoPaneMode) && HelperGetDataFromOtherApp.hasSharedData) {
            if (enable) {
                mHelperToolbar.setDefaultTitle(getString(R.string.send_message_to) + "...");
                mHelperToolbar.getRightButton().setVisibility(View.GONE);
                mHelperToolbar.getScannerButton().setVisibility(View.GONE);
                if (PassCode.getInstance().isPassCode())
                    mHelperToolbar.getPassCodeButton().setVisibility(View.GONE);
                mHelperToolbar.getLeftButton().setVisibility(View.VISIBLE);
                mHelperToolbar.setLeftIcon(R.string.back_icon);
            } else {
                revertToolbarFromForwardMode();
            }
        }
    }
}
