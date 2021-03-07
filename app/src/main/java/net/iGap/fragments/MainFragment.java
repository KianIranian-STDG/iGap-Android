package net.iGap.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import net.iGap.messenger.ui.components.IconView;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.messenger.ui.toolBar.ToolBarMenuSubItem;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
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
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.response.ClientGetRoomListResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static net.iGap.G.isAppRtl;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class MainFragment extends BaseMainFragments implements ToolbarListener, EventManager.EventDelegate, OnVersionCallBack, OnSetActionInRoom, OnRemoveFragment, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {
    public static int mOffset = 0;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar loadMoreProgress;
    private ProgressBar loadingProgress;
    private LinearLayout emptyView;
    private FrameLayout floatActionLayout;

    private RoomListAdapter roomListAdapter;
    private boolean inMultiSelectMode;
    private List<Long> selectedRoom = new ArrayList<>();
    private NumberTextView multiSelectCounter;

    private final int passCodeTag = 1;
    private final int leaveTag = 2;
    private final int muteTag = 3;
    private final int moreTag = 4;
    private final int pinTag = 5;
    private final int deleteTag = 6;
    private final int clearHistoryTag = 7;
    private final int selectCounter = 8;
    private final int markAsReadTag = 9;
    private final int readAllTag = 10;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ToolbarItem passCodeItem;
    private ToolbarItem pintItem;
    private ToolbarItem deleteItem;
    private ToolbarItem muteItem;
    private ToolbarItem moreItem;
    private ToolbarItem searchItem;
    private ToolbarItems toolbarItems;
    private ToolBarMenuSubItem clearHistoryItem;
    private ToolBarMenuSubItem markAsReadItem;
    private ToolBarMenuSubItem leaveItem;

    private boolean floatingHidden;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

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
        fragmentView = new FrameLayout(context);
        FrameLayout layout = (FrameLayout) fragmentView;

        recyclerView = new RecyclerView(context);
        recyclerView.setItemAnimator(null);
        recyclerView.setItemViewCacheSize(0);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(context));
        recyclerView.setAdapter(roomListAdapter = new RoomListAdapter(getRoomController().getLiveRoomList(), avatarHandler, selectedRoom));
        layout.addView(recyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollUpdated;
            private boolean scrollingManually;
            private int prevTop;
            private int prevPosition;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                scrollingManually = newState == RecyclerView.SCROLL_STATE_DRAGGING;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!ClientGetRoomListResponse.roomListFetched) {
                    if (mOffset > 0) {
                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastVisiblePosition + 10 >= mOffset) {
                            new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, String.valueOf(System.currentTimeMillis()));
                        }
                    }
                }

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem != RecyclerView.NO_POSITION) {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                    if (holder != null && holder.getAdapterPosition() != 0) {
                        int firstViewTop = holder.itemView.getTop();
                        boolean goingDown;
                        boolean changed = true;
                        if (prevPosition == firstVisibleItem) {
                            final int topDelta = prevTop - firstViewTop;
                            goingDown = firstViewTop < prevTop;
                            changed = Math.abs(topDelta) > 1;
                        } else {
                            goingDown = firstVisibleItem > prevPosition;
                        }
                        if (changed && scrollUpdated && (goingDown || scrollingManually)) {
                            hideFloatingButton(goingDown);
                        }
                        prevPosition = firstVisibleItem;
                        prevTop = firstViewTop;
                        scrollUpdated = true;
                    }
                }
            }
        });

        loadMoreProgress = new ProgressBar(context);
        loadMoreProgress.setVisibility(View.GONE);
        AppUtils.setProgresColler(loadMoreProgress);
        layout.addView(loadMoreProgress, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER, 0, 0, 0, 8));

        loadingProgress = new ProgressBar(context);
        AppUtils.setProgresColler(loadingProgress);
        layout.addView(loadingProgress, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));


        emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.VERTICAL);
        emptyView.setVisibility(View.VISIBLE);
        layout.addView(emptyView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        ImageView emptyImageView = new ImageView(context);
        emptyImageView.setImageResource(R.drawable.empty_chat);
        emptyView.addView(emptyImageView, LayoutCreator.createLinear(160, 160, Gravity.CENTER));

        TextView emptyTextView = new TextView(context);
        emptyTextView.setText(getResources().getString(R.string.empty_room));
        emptyTextView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        emptyTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        emptyTextView.setSingleLine();
        emptyTextView.setGravity(Gravity.CENTER);
        emptyView.addView(emptyTextView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 16, 0, 8));

        floatActionLayout = new FrameLayout(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getInstance().getToolbarBackgroundColor(context), Theme.getInstance().getAccentColor(context));
        floatActionLayout.setBackground(drawable);
        floatActionLayout.setOnClickListener(v -> onFloatActionClick());
        layout.addView(floatActionLayout, LayoutCreator.createFrame(52, 52, (isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, 16, 0, 16, 16));

        IconView addButton = new IconView(context);
        addButton.setIcon(R.string.add_icon_without_circle_font);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.addView(addButton);

        return fragmentView;
    }

    private void onFloatActionClick() {
        Fragment fragment = RegisteredContactsFragment.newInstance(true, false, RegisteredContactsFragment.ADD);
        addFragment(fragment);
    }

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;

        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(floatingButtonHideProgress, floatingHidden ? 1f : 0f);
        valueAnimator.addUpdateListener(animation -> {
            floatingButtonHideProgress = (float) animation.getAnimatedValue();
            floatingButtonTranslation = (LayoutCreator.dp(100) * floatingButtonHideProgress);
            floatActionLayout.setTranslationY(floatingButtonTranslation - 0 * (1f - floatingButtonHideProgress));
        });
        animatorSet.playTogether(valueAnimator);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(floatingInterpolator);
        floatActionLayout.setClickable(!hide);
        animatorSet.start();
    }

    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(isAppRtl ? R.string.igap_fa_icon : R.string.igap_en_icon);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        searchItem = toolbarItems.addItem(0, R.string.search_icon, Color.WHITE)
                .setIsSearchBox(true)
                .setActionBarMenuItemSearchListener(new ToolbarItem.ActionBarMenuItemSearchListener() {
                    SearchFragment searchFragment;

                    @Override
                    public void onSearchExpand() {
                        toolbar.setBackIcon(new BackDrawable(false));
                        if (getActivity() != null) {
                            searchFragment = SearchFragment.newInstance();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                            fragmentView.setId(R.id.mainFragmentView);
                            fragmentTransaction.replace(fragmentView.getId(), searchFragment).commit();
                        }
                    }

                    @Override
                    public boolean canCollapseSearch() {
                        return super.canCollapseSearch();
                    }

                    @Override
                    public void onSearchCollapse() {
                        toolbar.setBackIcon(null);
                        searchFragment.onSearchCollapsed();
                    }

                    @Override
                    public void onTextChanged(EditText editText) {
                        super.onTextChanged(editText);
                        searchFragment.onTextChanged(editText.getText().toString());

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
                    break;
                case leaveTag:
                    confirmActionForRemoveSelected();
                    break;
                case clearHistoryTag:
                    confirmActionForClearHistoryOfSelected();
                    break;
                case markAsReadTag:
                    confirmActionForMarkAsRead();
                    break;
                case readAllTag:
                    confirmActionForReadAllRoom();
                    break;
            }
        });

        return toolbar;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_ROOM_PAGE);

        getEventManager().addObserver(EventManager.CALL_STATE_CHANGED, this);
        getEventManager().addObserver(EventManager.EMOJI_LOADED, this);
        getEventManager().addObserver(EventManager.ROOM_LIST_CHANGED, this);
        getEventManager().addObserver(EventManager.CONNECTION_STATE_CHANGED, this);

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

            @Override
            public void needShowLoadProgress(boolean show) {
                loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            @Override
            public void needShowEmptyView(boolean show) {
                emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            @Override
            public void needCheckMultiSelect() {
                disableMultiSelect();
            }
        });

        G.onNotifyTime = () -> G.handler.post(() -> {
            if (recyclerView != null) {
                if (recyclerView.getAdapter() != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });

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
        clearHistoryItem = moreItem.addSubItem(clearHistoryTag, R.string.lock_icon, getResources().getString(R.string.clear_history));
        markAsReadItem = moreItem.addSubItem(markAsReadTag, R.string.lock_icon, getResources().getString(R.string.mark_as_unread));
        leaveItem = moreItem.addSubItem(readAllTag, R.string.lock_icon, getResources().getString(R.string.read_all));

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

    private void checkMultiSelectState() {
        if (inMultiSelectMode) {
            enableMultiSelect();
            toolbar.showActionToolbar();
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
            AsyncTransaction.executeTransactionWithLoading(getActivity(), realm, realm1 -> {
                if (selectedRoom.size() > 0) {
                    for (int i = 0; i < selectedRoom.size(); i++) {
                        RealmRoom room = getMessageDataStorage().getRoom(selectedRoom.get(i));
                        markAsRead(realm1, room.getType(), room.getId());
                    }
                }
            }, this::disableMultiSelect);
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

    private void deleteChat(RealmRoom item, boolean exit) {

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
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() <= 1) {
                    recyclerView.smoothScrollToPosition(0);
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
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
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

        getEventManager().removeObserver(EventManager.CALL_STATE_CHANGED, this);
        getEventManager().removeObserver(EventManager.EMOJI_LOADED, this);
        getEventManager().removeObserver(EventManager.ROOM_LIST_CHANGED, this);
        getEventManager().removeObserver(EventManager.CONNECTION_STATE_CHANGED, this);
    }

    private void onConnectionStateChange(final ConnectionState connectionState) {
        if (connectionState == null) {
            return;
        }

        if (connectionState == ConnectionState.WAITING_FOR_NETWORK) {
            toolbar.setTitle(getResources().getString(R.string.waiting_for_network));
        } else if (connectionState == ConnectionState.CONNECTING) {
            toolbar.setTitle(getResources().getString(R.string.connecting));
        } else if (connectionState == ConnectionState.UPDATING) {
            toolbar.setTitle(getResources().getString(R.string.updating));
        } else if (connectionState == ConnectionState.IGAP) {
            toolbar.setTitle(isAppRtl ? R.string.igap_fa_icon : R.string.igap_en_icon);
        } else {
            toolbar.setTitle(isAppRtl ? R.string.igap_fa_icon : R.string.igap_en_icon);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        G.onSetActionInRoom = this;
        G.onDateChanged = this;
        G.onVersionCallBack = this;
        if (G.isDepricatedApp)
            isDeprecated();

        checkPassCodeVisibility();

        boolean canUpdate = false;

        if (G.isUpdateNotificaionColorMain) {
            canUpdate = true;
            G.isUpdateNotificaionColorMain = false;
        }

        if (canUpdate) {

            if (recyclerView != null) {
                if (recyclerView.getAdapter() != null) {
                    recyclerView.getAdapter().notifyDataSetChanged();
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

    public void revertToolbarFromForwardMode() {
        FragmentChat.mForwardMessages = null;
        HelperGetDataFromOtherApp.hasSharedData = false;
        HelperGetDataFromOtherApp.sharedList.clear();

        if (passCodeItem != null) {
            passCodeItem.setVisibility(View.VISIBLE);
        }

        onConnectionStateChange(ConnectionState.IGAP);
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

    private void confirmActionForRemoveSelected() {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.delete_chat))
                .content(getString(R.string.do_you_want_delete_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    if (selectedRoom.size() > 0) {
                        for (int i = 0; i < selectedRoom.size(); i++) {
                            RealmRoom item = getMessageDataStorage().getRoom(selectedRoom.get(i));
                            deleteChat(item, false);
                        }
                    }
                    disableMultiSelect();
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

    private void confirmActionForClearHistoryOfSelected() {
        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.clear_history))
                .content(getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    for (long roomId : selectedRoom) {
                        clearHistory(roomId, false);
                    }
                    disableMultiSelect();
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
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

    @Override
    public void scrollToTopOfList() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    public void setForwardMessage(boolean enable) {
        if (!(G.isLandscape && G.twoPaneMode) && (FragmentChat.mForwardMessages != null || FragmentChat.structIGSticker != null)) {
            if (enable) {
                toolbar.setTitle(getResources().getString(R.string.send_message_to) + "...");
                if (passCodeItem != null) {
                    passCodeItem.setVisibility(View.INVISIBLE);
                }
            } else {
                revertToolbarFromForwardMode();
            }
        }
    }

    public void checkHasSharedData(boolean enable) {
        if (!(G.isLandscape && G.twoPaneMode) && HelperGetDataFromOtherApp.hasSharedData) {
            if (enable) {
                toolbar.setTitle(getResources().getString(R.string.send_message_to) + "...");
                if (passCodeItem != null) {
                    passCodeItem.setVisibility(View.INVISIBLE);
                }
            } else {
                revertToolbarFromForwardMode();
            }
        }
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.CALL_STATE_CHANGED) {
            if (args == null || args.length == 0) {
                return;
            }

            if (MusicPlayer.chatLayout != null) {
                MusicPlayer.chatLayout.setVisibility(View.GONE);
            }
            if (MusicPlayer.mainLayout != null) {
                MusicPlayer.mainLayout.setVisibility(View.GONE);
            }
        } else if (id == EventManager.EMOJI_LOADED) {
            invalidateViews();
        } else if (id == EventManager.ROOM_LIST_CHANGED) {
            boolean show = (boolean) args[0];
            loadMoreProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        } else if (id == EventManager.CONNECTION_STATE_CHANGED) {
            ConnectionState state = (ConnectionState) args[0];
            onConnectionStateChange(state);
        }
    }
}
