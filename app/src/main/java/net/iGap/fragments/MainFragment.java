package net.iGap.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.RoomListAdapter;
import net.iGap.adapter.items.cells.RoomListCell;
import net.iGap.helper.AsyncTransaction;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.messenger.ui.toolBar.ToolBarMenuSubItem;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.model.MultiSelectStruct;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnChatDeleteInRoomList;
import net.iGap.observers.interfaces.OnChatSendMessageResponse;
import net.iGap.observers.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.observers.interfaces.OnDateChanged;
import net.iGap.observers.interfaces.OnGroupDeleteInRoomList;
import net.iGap.observers.interfaces.OnRemoveFragment;
import net.iGap.observers.interfaces.OnSetActionInRoom;
import net.iGap.observers.interfaces.OnVersionCallBack;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.Room;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.response.ClientGetRoomListResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.isAppRtl;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class MainFragment extends BaseMainFragments implements ToolbarListener, EventListener, OnVersionCallBack, OnSetActionInRoom, OnRemoveFragment, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    private ProgressBar progressBar;
    public static int mOffset = 0;
    private View viewById;
    private RecyclerView mRecyclerView;
    private long tagId;
    private ProgressBar pbLoading;

    private RoomListAdapter roomListAdapter;
    private boolean inMultiSelectMode;
    private onChatCellClick onChatCellClickedInEditMode;
    //    private CopyOnWriteArrayList<Room> mSelectedRoomList = new CopyOnWriteArrayList<>();
    private RealmResults<RealmRoom> results;
    private List<Long> selectedRoom = new ArrayList<>();
    private ConstraintLayout root;
    private View selectedItemView;
    private NumberTextView multiSelectCounter;

    private AnimatorSet progressAnimatorSet;

    private final int passCodeTag = 1;
    private final int leaveTag = 2;
    private final int muteTag = 3;
    private final int moreTag = 4;
    private final int pinTag = 5;
    private final int deleteTag = 6;
    private final int clearHistoryTag = 7;
    private final int selectCounter = 8;
    private final int markAsReadTag = 9;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ToolbarItem passCodeItem;
    private ToolbarItem pintItem;
    private ToolbarItem deleteItem;
    private ToolbarItem muteItem;
    private ToolbarItem moreItem;
    private ToolbarItems toolbarItems;
    private ToolBarMenuSubItem clearHistoryItem;
    private ToolBarMenuSubItem markAsReadItem;
    private ToolBarMenuSubItem leaveItem;

    public static MainFragment newInstance() {
        Bundle bundle = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedResume = true;
    }

    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.activity_main_rooms, null, false);
    }

    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(isAppRtl ? R.string.igap_fa_icon : R.string.igap_en_icon);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        toolbarItems.addItem(0, R.string.search_icon, Color.WHITE)
                .setIsSearchBox(true)
                .setActionBarMenuItemSearchListener(new ToolbarItem.ActionBarMenuItemSearchListener() {
                    @Override
                    public void onSearchExpand() {
                        toolbar.setBackIcon(new BackDrawable(false));
                    }

                    @Override
                    public boolean canCollapseSearch() {
                        return super.canCollapseSearch();
                    }

                    @Override
                    public void onSearchCollapse() {
                        toolbar.setBackIcon(null);
                    }

                    @Override
                    public void onTextChanged(EditText editText) {
                        super.onTextChanged(editText);
                    }
                });

        if (PassCode.getInstance().isPassCode()) {
            passCodeItem = toolbar.addItem(passCodeTag, R.string.unlock_icon, Color.WHITE);
        }

        createActionMode();

        toolbar.setListener(i -> {
            Log.i("abbasiMainFragment", "createToolBar: " + i);
            switch (i) {
                case -1:
                    if (toolbar.isInActionMode()) {
                        disableMultiSelect();
                    }
                    break;
                case passCodeTag:
                    if (passCodeItem == null) {
                        return;
                    }
                    if (ActivityMain.isLock) {
                        passCodeItem.setIcon(R.string.unlock_icon);
                        ActivityMain.isLock = false;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
                    } else {
                        passCodeItem.setIcon(R.string.lock_icon);
                        ActivityMain.isLock = true;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, true);
                    }

                    checkPassCodeVisibility();
                    break;

                case muteTag:
                    confirmActionForMuteNotification();
                    break;
                case pinTag:
                    confirmActionForPinToTop();
            }
        });

        return toolbar;
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

        selectedItemView = view.findViewById(R.id.amr_layout_selected_root);

        if (results == null)
            changeRoomListProgress(true);

        onChatCellClickedInEditMode = (item, position, status) -> {
//            Room temp = new Room(item.getId(), item.getType().name(), item.getTitle(), "", "");
//            if (item.getType() == GROUP) {
//                temp.setGroupRole(item.getGroupRoom().getRole().toString());
//            } else if (item.getType() == CHANNEL) {
//                temp.setChannelRole(item.getChannelRoom().getRole().toString());
//            }
//            if (!status) {
//                mSelectedRoomList.add(temp);
//            } else {
//                mSelectedRoomList.remove(temp);
//                if (mSelectedRoomList.size() > 0)
//                    item = DbManager.getInstance().doRealmTask(realm -> {
//                        return realm.where(RealmRoom.class)
//                                .equalTo("id", mSelectedRoomList.get(mSelectedRoomList.size() - 1).getId()).findFirst();
//                    });
//            }
//
//            if (mSelectedRoomList.size() == 0) {
//                disableMultiSelect();
//                return;
//            }
//
//            multiSelectAdapter.setItemsList(setMultiSelectAdapterItem(item, mSelectedRoomList.size() == 1));
//
//            RealmRoom finalItem = item;
//            multiSelectAdapter.setCallBack(action -> {
//                switch (action) {
//                    case 0:
//                        confirmActionForPinToTop(finalItem.getId(), finalItem.isPinned());
//                        break;
//                    case 1:
//                        confirmActionForMuteNotification(finalItem.getId(), finalItem.getMute());
//                        break;
//                    case 2:
//                        confirmActionForClearHistory(finalItem.getId());
//                        break;
//                    case 3:
//                        confirmActionForRemoveItem(finalItem);
//                        break;
//                    case 4:
//                        confirmActionForReadAllRoom();
//                        break;
//                    case 5:
//                        confirmActionForMarkAsRead();
//                        break;
//                    case 6:
//                        if (mSelectedRoomList.size() > 0) {
//                            confirmActionForClearHistoryOfSelected();
//                        }
//                        break;
//                    case 7:
//                        if (mSelectedRoomList.size() > 0) {
//                            confirmActionForRemoveSelected();
//                        }
//                        break;
//
//                }
//            });
//
//            refreshChatList(position, false);
        };

        if (MusicPlayer.playerStateChangeListener != null) {
            MusicPlayer.playerStateChangeListener.observe(getViewLifecycleOwner(), isVisible -> {
//                if (!mHelperToolbar.getmSearchBox().isShown()) {
//                    mHelperToolbar.animateSearchBox(false, 0, 0);
//                }
            });
        }

        EventManager.getInstance().addEventListener(EventManager.CALL_STATE_CHANGED, this);
        EventManager.getInstance().addEventListener(EventManager.EMOJI_LOADED, this);
        EventManager.getInstance().addEventListener(EventManager.ROOM_LIST_CHANGED, this);

        mRecyclerView = view.findViewById(R.id.cl_recycler_view_contact);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        initRecycleView();

        setForwardMessage(true);
        checkHasSharedData(true);
        checkMultiSelectState();
    }

    private void createActionMode() {
        if (toolbar.isInActionMode(null)) {
            return;
        }

        toolbarItems = toolbar.createActionToolbar(null);
        toolbarItems.setBackground(null);

        moreItem = toolbarItems.addItemWithWidth(moreTag, R.string.more_icon, 52);
        clearHistoryItem = moreItem.addSubItem(0, R.string.lock_icon, getResources().getString(R.string.clear_history));
        markAsReadItem = moreItem.addSubItem(0, R.string.lock_icon, getResources().getString(R.string.mark_as_unread));
        leaveItem = moreItem.addSubItem(0, R.string.lock_icon, getResources().getString(R.string.leave_channel));

        deleteItem = toolbarItems.addItemWithWidth(leaveTag, R.string.delete_icon, 52);
        muteItem = toolbarItems.addItemWithWidth(muteTag, R.string.mute_icon, 52);
        pintItem = toolbarItems.addItemWithWidth(pinTag, R.string.location_pin_icon, 52);

        multiSelectCounter = new NumberTextView(toolbarItems.getContext());
        multiSelectCounter.setTextSize(18);
        multiSelectCounter.setTypeface(ResourcesCompat.getFont(toolbarItems.getContext(), R.font.main_font_bold));
        multiSelectCounter.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        multiSelectCounter.setTag(selectCounter);
        toolbarItems.addView(multiSelectCounter, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1.0f, 72, 0, 0, 0));

        actionModeViews.add(moreItem);
        actionModeViews.add(deleteItem);
        actionModeViews.add(muteItem);
        actionModeViews.add(pintItem);
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
                        return realm.where(RealmRoom.class).equalTo("isPinned", true).findAll().size();
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

    private void checkMultiSelectState() {
        if (inMultiSelectMode) {
            enableMultiSelect();
            toolbar.showActionToolbar();
//            selectedItemCountTv.setVisibility(View.VISIBLE);
//            selectedItemCountTv.setText(isAppRtl ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(mSelectedRoomList.size())) : String.valueOf(mSelectedRoomList.size()));
            multiSelectCounter.setNumber(selectedRoom.size(), false);
        }
    }

    private void confirmActionForMarkAsRead() {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    markAsRead();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void markAsRead() {
        DbManager.getInstance().doRealmTask(realm -> {
            AsyncTransaction.executeTransactionWithLoading(getActivity(), realm, new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (selectedRoom.size() > 0) {
                        for (int i = 0; i < selectedRoom.size(); i++) {
//                            markAsRead(realm, selectedRoom.get(i).getType(), selectedRoom.get(i).getId());
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
    }

    private void confirmActionForReadAllRoom() {
        List<RealmRoom> unreadList = DbManager.getInstance().doRealmTask(realm -> {
            return realm.copyFromRealm(realm.where(RealmRoom.class).greaterThan("unreadCount", 0).equalTo("isDeleted", false).findAll());
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
                return realm.where(RealmRoom.class).equalTo("keepRoom", false).equalTo("isDeleted", false).sort(new String[]{"isPinned", "pinId", "updatedTime"}, new Sort[]{Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING}).findAllAsync();
            });
            roomListAdapter = new RoomListAdapter(results, viewById, pbLoading, avatarHandler, selectedRoom, this::disableMultiSelect);
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
                onItemClick(roomListCell, realmRoom, adapterPosition);
            }

            @Override
            public boolean onLongClick(RoomListCell roomListCell, RealmRoom realmRoom, int position) {
                if (!inMultiSelectMode && FragmentChat.mForwardMessages == null && !HelperGetDataFromOtherApp.hasSharedData) {
                    enableMultiSelect();
                    updateSelectedRoom(realmRoom, position);
                    createActionMode();
                    multiSelectCounter.setNumber(selectedRoom.size(), selectedRoom.size() != 1);
                    toolbar.showActionToolbar();
                    BackDrawable backDrawable = new BackDrawable(true);
                    backDrawable.setRotation(1, true);
                    backDrawable.setRotatedColor(Theme.getInstance().getPrimaryTextColor(getContext()));
                    toolbar.setBackIcon(backDrawable);

                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList<Animator> animators = new ArrayList<>();
                    for (int a = 0; a < actionModeViews.size(); a++) {
                        View view = actionModeViews.get(a);
                        view.setPivotY(Toolbar.getCurrentActionBarHeight() / 2);
                        animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 1.0f));
                    }
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration(180);
                    animatorSet.start();
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

    private void onItemClick(RoomListCell roomCell, RealmRoom room, int position) {
        if (inMultiSelectMode) {
            updateSelectedRoom(room, position);
        } else {
            if (room.isValid() && G.fragmentActivity != null) {
                boolean openChat = true;
                if (G.twoPaneMode) {
                    if (getActivity() != null) {
                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                        if (fragment != null) {

                            FragmentChat fm = (FragmentChat) fragment;
                            if (fm.isAdded() && fm.mRoomId == room.getId()) {
                                openChat = false;
                            } else {
                                removeFromBaseFragment(fragment);
                            }
                        }
                    }
                }
                if (openChat) {
                    new GoToChatActivity(room.getId()).startActivity(getActivity());
                }
            }
        }
    }

    private void updateSelectedRoom(RealmRoom room, int position) {
        Long roomId = room.getId();

        if (selectedRoom.contains(roomId)) {
            selectedRoom.remove(roomId);
        } else {
            selectedRoom.add(roomId);
        }

        if (selectedRoom.size() == 0) {
            disableMultiSelect();
            return;
        }

        checkSelectedRoom();

        multiSelectCounter.setNumber(selectedRoom.size(), selectedRoom.size() != 1);
        roomListAdapter.notifyItemChanged(position);
    }

    private void checkSelectedRoom() {
        boolean hasMute = false;
        boolean hasUnMute = false;

        boolean hasPinned = false;
        boolean hasUnPin = false;
        for (int i = 0; i < selectedRoom.size(); i++) {
            Long roomId = selectedRoom.get(i);
            RealmRoom room = getMessageDataStorage().getRoom(roomId);

            if (room == null) {
                continue;
            }
            if (room.mute) {
                hasMute = true;
            } else {
                hasUnMute = true;
            }

            if (room.isPinned) {
                hasPinned = true;
            } else {
                hasUnPin = true;
            }
        }

        if (hasMute && hasUnMute) {
            muteItem.setVisibility(View.GONE);
        } else if (hasMute) {
            muteItem.setVisibility(View.VISIBLE);
            muteItem.setIcon(R.string.unmute_icon);
        } else if (hasUnMute) {
            muteItem.setVisibility(View.VISIBLE);
            muteItem.setIcon(R.string.mute_icon);
        }

        if (hasPinned && hasUnPin) {
            pintItem.setVisibility(View.GONE);
        } else if (hasPinned) {
            pintItem.setVisibility(View.VISIBLE);
            pintItem.setIcon(R.string.location_pin_icon);
        } else {
            pintItem.setVisibility(View.VISIBLE);
            pintItem.setIcon(R.string.check_icon);
        }
    }


    private void invalidateViews() {
        if (roomListAdapter != null) {
            roomListAdapter.notifyDataSetChanged();
        }
    }

    private void deleteChat(Room item, boolean exit) {

        if (item.getType() == CHAT) {
            getRoomController().chatDeleteRoom(item.getId());
        } else if (item.getType() == GROUP) {
            if (item.getGroupRole() == GroupChatRole.OWNER) {
                getRoomController().groupDeleteRoom(item.getId());
            } else {
                getRoomController().groupLeft(item.getId());
            }
        } else if (item.getType() == CHANNEL) {

            if (MusicPlayer.mainLayout != null) {
                if (item.getId() == MusicPlayer.roomId) {
                    MusicPlayer.closeLayoutMediaPlayer();
                }
            }


            if (item.getChannelRole() == ChannelChatRole.OWNER) {
                getMessageController().deleteChannel(item.getId());
            } else {
                getRoomController().channelLeft(item.getId());
            }
        }
        if (exit)
            disableMultiSelect();
    }

    private void deleteChatWithRealm(RealmRoom item, boolean exit) {

        if (item.getType() == CHAT) {
            getRoomController().chatDeleteRoom(item.getId());
        } else if (item.getType() == GROUP) {
            if (item.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                getRoomController().groupDeleteRoom(item.getId());
            } else {
                getRoomController().groupLeft(item.getId());
            }
        } else if (item.getType() == CHANNEL) {

            if (MusicPlayer.mainLayout != null) {
                if (item.getId() == MusicPlayer.roomId) {
                    MusicPlayer.closeLayoutMediaPlayer();
                }
            }


            if (item.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                getMessageController().deleteChannel(item.getId());
            } else {
                getRoomController().channelLeft(item.getId());
            }
        }
        if (exit)
            disableMultiSelect();
    }

    private void confirmActionForMuteNotification() {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    for (int i = 0; i < selectedRoom.size(); i++) {
                        long roomId = selectedRoom.get(i);
                        boolean mute = getMessageDataStorage().getRoom(roomId).mute;
                        getRoomController().clientMuteRoom(roomId, !mute);
                    }
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void muteNotification(final long roomId, final boolean mute) {

    }

    private void confirmActionForClearHistory(long id) {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.clear_history))
                .content(getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    clearHistory(id, true);
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearHistory(final long roomId, boolean exit) {
        getMessageController().clearHistoryMessage(roomId);
        if (exit)
            disableMultiSelect();
    }

    private void confirmActionForPinToTop() {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.are_you_sure))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    int pinCount = DbManager.getInstance().doRealmTask(realm -> {
                        return realm.where(RealmRoom.class).equalTo("isPinned", true).findAll().size();
                    });
                    for (int i = 0; i < selectedRoom.size(); i++) {
                        long roomId = selectedRoom.get(i);
                        RealmRoom room = getMessageDataStorage().getRoom(roomId);
                        if (pinCount < 5 || room.isPinned) {
                            pinToTop(roomId, room.isPinned);
                        }
                        pinCount++;
                    }
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void pinToTop(final long roomId, final boolean isPinned) {
        getRoomController().clientPinRoom(roomId, !isPinned);
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

        EventManager.getInstance().removeEventListener(EventManager.CALL_STATE_CHANGED, this);
        EventManager.getInstance().removeEventListener(EventManager.EMOJI_LOADED, this);
        EventManager.getInstance().removeEventListener(EventManager.ROOM_LIST_CHANGED, this);
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

        checkPassCodeVisibility();

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

    public void checkPassCodeVisibility() {
        if (PassCode.getInstance().isPassCode()) {
            if (passCodeItem == null) {
                passCodeItem = toolbar.addItem(passCodeTag, R.string.unlock_icon, Color.WHITE);
            }

            ActivityMain.isLock = HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE);
            if (ActivityMain.isLock) {
                passCodeItem.setIcon(R.string.lock_icon);
            } else {
                passCodeItem.setIcon(R.string.unlock_icon);
            }
        } else if (passCodeItem != null) {
            passCodeItem.setVisibility(View.GONE);
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
                                String url = BuildConfig.UPDATE_LINK;
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
            HelperLog.getInstance().setErrorLog(e);
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
                                        String url = BuildConfig.UPDATE_LINK;
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
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (!(G.isLandscape && G.twoPaneMode)) {
            if (FragmentChat.mForwardMessages != null || HelperGetDataFromOtherApp.hasSharedData)
                revertToolbarFromForwardMode();
        }

        if (inMultiSelectMode) {
            disableMultiSelect();
        }
    }

    private void enableMultiSelect() {
        inMultiSelectMode = true;
        roomListAdapter.setMultiSelect(true);
        roomListAdapter.notifyDataSetChanged();
    }

    private void disableMultiSelect() {
        if (inMultiSelectMode) {
            inMultiSelectMode = false;
            selectedRoom.clear();
            roomListAdapter.setMultiSelect(false);
            roomListAdapter.notifyDataSetChanged();
            toolbar.setBackIcon(null);
            toolbar.hideActionToolbar();
        }
    }

    private boolean checkHasChannelInSelectedItems() {
//        for (RealmRoom room : selectedRoom)
//            if (room != null && room.getType() == CHANNEL) return true;
        return false;
    }

    public void revertToolbarFromForwardMode() {
        FragmentChat.mForwardMessages = null;
        HelperGetDataFromOtherApp.hasSharedData = false;
        HelperGetDataFromOtherApp.sharedList.clear();
        checkConnectionStateAndSetToolbarTitle();
    }

    private void checkConnectionStateAndSetToolbarTitle() {

        //check first time state then for every changes observer will change title
        if (G.connectionState != null) {
            if (G.connectionState == ConnectionState.CONNECTING) {
//                mHelperToolbar.getTextViewLogo().setText(getString(R.string.connecting));
//                mHelperToolbar.changeDefaultTitle(getString(R.string.app_name));
//                mHelperToolbar.checkIGapFont();
            } else if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
//                mHelperToolbar.getTextViewLogo().setText(getString(R.string.waiting_for_network));
//                mHelperToolbar.changeDefaultTitle(getString(R.string.app_name));
//                mHelperToolbar.checkIGapFont();
            } else {
//                mHelperToolbar.setDefaultTitle(getString(R.string.app_name));
            }
        } else {
//            mHelperToolbar.setDefaultTitle(getString(R.string.app_name));
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

//    private boolean isItemAvailableOnSelectedList(RealmRoom mInfo) {
//        return mSelectedRoomList.contains(mInfo);
//    }

//    private void confirmActionForRemoveSelected() {
//        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.delete_chat))
//                .content(getString(R.string.do_you_want_delete_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
//                .onPositive((dialog, which) -> {
//                    dialog.dismiss();
//
//                    if (mSelectedRoomList.size() > 0) {
//
//                        for (Room item : mSelectedRoomList) {
//                            deleteChat(item, false);
//                        }
//                        disableMultiSelect();
//
//                    }
//                })
//                .onNegative((dialog, which) -> dialog.dismiss())
//                .show();
//    }

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

//    private void confirmActionForClearHistoryOfSelected() {
//        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.clear_history))
//                .content(getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
//                .onPositive((dialog, which) -> {
//                    dialog.dismiss();
//                    for (Room item : mSelectedRoomList) {
//                        clearHistory(item.getId(), false);
//                    }
//                    disableMultiSelect();
//                })
//                .onNegative((dialog, which) -> dialog.dismiss())
//                .show();
//    }

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
        if (inMultiSelectMode) {
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

        if (id == EventManager.CALL_STATE_CHANGED) {
            if (message == null || message.length == 0) return;
            boolean state = (boolean) message[0];
            G.handler.post(() -> {
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
//                mHelperToolbar.setDefaultTitle(getString(R.string.send_message_to) + "...");
//                mHelperToolbar.getRightButton().setVisibility(View.GONE);
//                mHelperToolbar.getScannerButton().setVisibility(View.GONE);
//                mHelperToolbar.getLeftButton().setVisibility(View.VISIBLE);
//                mHelperToolbar.setLeftIcon(R.string.back_icon);
            } else {
                revertToolbarFromForwardMode();
            }
        }

    }

    public void checkHasSharedData(boolean enable) {

        if (!(G.isLandscape && G.twoPaneMode) && HelperGetDataFromOtherApp.hasSharedData) {
            if (enable) {
//                mHelperToolbar.setDefaultTitle(getString(R.string.send_message_to) + "...");
//                mHelperToolbar.getRightButton().setVisibility(View.GONE);
//                mHelperToolbar.getScannerButton().setVisibility(View.GONE);
//                mHelperToolbar.getLeftButton().setVisibility(View.VISIBLE);
//                mHelperToolbar.setLeftIcon(R.string.back_icon);
            } else {
                revertToolbarFromForwardMode();
            }
        }
    }
}
