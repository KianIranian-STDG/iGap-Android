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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.CallActivity;
import net.iGap.adapter.RoomListAdapter;
import net.iGap.adapter.items.cells.RoomListCell;
import net.iGap.helper.AsyncTransaction;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.ui.components.FragmentMediaContainer;
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
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnDateChanged;
import net.iGap.observers.interfaces.OnRemoveFragment;
import net.iGap.observers.interfaces.OnSetActionInRoom;
import net.iGap.observers.interfaces.OnVersionCallBack;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.response.ClientGetRoomListResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

import static net.iGap.G.isAppRtl;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class MainFragment extends BaseMainFragments implements EventManager.EventDelegate, OnVersionCallBack, OnSetActionInRoom, OnRemoveFragment, OnDateChanged {
    public static int mOffset = 0;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar loadMoreProgress;
    private ProgressBar loadingProgress;
    private LinearLayout emptyView;
    private FrameLayout floatActionLayout;
    private FragmentMediaContainer mediaContainer;

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
    private ToolBarMenuSubItem readAllItem;
    private SearchFragment searchFragment;

    private boolean floatingHidden;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private int firstVisibleItemPosition;
    private int firstVisibleItemPositionOffset;

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

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        fragmentView = new FrameLayout(context);
        FrameLayout layout = (FrameLayout) fragmentView;

        initRecyclerView(context);
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

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                View view = layoutManager.getChildAt(0);
                if (firstVisibleItemPosition > 0 && view != null) {
                    firstVisibleItemPositionOffset = view.getTop();
                }

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
        addButton.setIcon(R.string.icon_add);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.addView(addButton);

        mediaContainer = new FragmentMediaContainer(context, this);
        mediaContainer.setListener(i -> {
            switch (i) {
                case FragmentMediaContainer.CALL_TAG:
                    getActivity().startActivity(new Intent(getContext(), CallActivity.class));
                    break;
                case FragmentMediaContainer.MEDIA_TAG:
                    if (!MusicPlayer.isVoice) {
                        Intent intent = new Intent(context, ActivityMain.class);
                        intent.putExtra(ActivityMain.openMediaPlyer, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                    break;
                case FragmentMediaContainer.PLAY_TAG:
                    break;
            }
        });
        layout.addView(mediaContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -40, 0, 0));

        return fragmentView;
    }

    private void initRecyclerView(Context context) {
        recyclerView = new RecyclerView(context);
        recyclerView.setItemAnimator(null);
        recyclerView.setItemViewCacheSize(0);
        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(context));
        recyclerView.setAdapter(roomListAdapter = new RoomListAdapter(getRoomController().getLiveRoomList(), avatarHandler, selectedRoom));
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, firstVisibleItemPositionOffset);
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
        toolbar.setTitle(isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        toolbar.setOnClickListener(view -> recyclerView.smoothScrollToPosition(0));
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        searchItem = toolbarItems.addItem(0, R.string.icon_search, Color.WHITE)
                .setIsSearchBox(true)
                .setActionBarMenuItemSearchListener(new ToolbarItem.ActionBarMenuItemSearchListener() {

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
            passCodeItem = toolbar.addItem(passCodeTag, R.string.icon_unlock, Color.WHITE);
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
                        passCodeItem.setIcon(R.string.icon_unlock);
                        ActivityMain.isLock = false;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
                    } else {
                        passCodeItem.setIcon(R.string.icon_lock);
                        ActivityMain.isLock = true;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, true);
                    }

                    checkPassCodeVisibility();
                    break;
                case muteTag:
                    muteNotification();
                    break;
                case pinTag:
                    setPinToTop();
                    break;
                case leaveTag:
                    confirmActionForRemoveSelected();
                    break;
                case clearHistoryTag:
                    confirmActionForClearHistoryOfSelected();
                    break;
                case markAsReadTag:
                    markAsRead();
                    break;
                case readAllTag:
                    readAllRoom();
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

        moreItem = toolbarItems.addItemWithWidth(moreTag, R.string.icon_other_vertical_dots, 52);
        clearHistoryItem = moreItem.addSubItem(clearHistoryTag, R.string.icon_clear_history, getResources().getString(R.string.clear_history));
        markAsReadItem = moreItem.addSubItem(markAsReadTag, R.string.icon_mark_as_read, getResources().getString(R.string.mark_as_unread));
        readAllItem = moreItem.addSubItem(readAllTag, R.string.icon_mark_all_read, getResources().getString(R.string.read_all));

        deleteItem = toolbarItems.addItemWithWidth(leaveTag, R.string.icon_delete, 52);
        muteItem = toolbarItems.addItemWithWidth(muteTag, R.string.icon_mute, 52);
        pintItem = toolbarItems.addItemWithWidth(pinTag, R.string.icon_pin_to_top, 52).setCustomTypeFace(ResourcesCompat.getFont(context, R.font.font_icons));

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

    private void readAllRoom() {
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
                        AsyncTransaction.executeTransactionWithLoading(getActivity(), realm, realm1 -> {
                            if (unreadList.size() > 0) {
                                for (RealmRoom room : unreadList) {
                                    markAsRead(realm1, room.getType(), room.getId());
                                }
                            }
                        }, () -> disableMultiSelect());
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
        boolean hasChannel = false;

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
            if(room.getType() == CHANNEL){
                hasChannel = true;
            }
        }

        if(hasChannel){
            clearHistoryItem.setVisibility(View.GONE);
        } else {
            clearHistoryItem.setVisibility(View.VISIBLE);
        }

        if (hasMute && hasUnMute) {
            muteItem.setVisibility(View.GONE);
        } else if (hasMute) {
            muteItem.setVisibility(View.VISIBLE);
            muteItem.setIcon(R.string.icon_speaker);
        } else if (hasUnMute) {
            muteItem.setVisibility(View.VISIBLE);
            muteItem.setIcon(R.string.icon_mute);
        }

        if (hasPinned && hasUnPin) {
            pintItem.setVisibility(View.GONE);
        } else if (hasPinned) {
            pintItem.setVisibility(View.VISIBLE);
            pintItem.setIcon(R.string.icon_unpin);
        } else {
            pintItem.setVisibility(View.VISIBLE);
            pintItem.setIcon(R.string.icon_pin_to_top);
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

    private void muteNotification() {

        long roomId = selectedRoom.get(0);
        boolean mute = getMessageDataStorage().getRoom(roomId).mute;

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.drawable_rounded_corners));

        IconView iconView = new IconView(context);
        iconView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        if (mute)
            iconView.setIcon(R.string.icon_speaker);
        else
            iconView.setIcon(R.string.icon_mute);
        iconView.setIconColor(Theme.getInstance().getPrimaryTextIconColor(context));
        frameLayout.addView(iconView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 20, 16, 20, 20));

        TextView textView = new TextView(context);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        if (mute)
            textView.setText(R.string.unmuted);
        else
            textView.setText(R.string.muted);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        frameLayout.addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, isAppRtl ? 5 : 50, 15, isAppRtl ? 50 : 5, 15));

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        iconView.setAnimation(fadeIn);

        Snackbar snackbar = Snackbar.make(Objects.requireNonNull(getView()), "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.addView(frameLayout, 0);

        snackbar.show();

        for (int i = 0; i < selectedRoom.size(); i++) {
            roomId = selectedRoom.get(i);
            mute = getMessageDataStorage().getRoom(roomId).mute;
            getRoomController().clientMuteRoom(roomId, !mute);
        }
        disableMultiSelect();
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

    private void setPinToTop() {
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
            toolbar.setTitle(isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        } else {
            toolbar.setTitle(isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
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
        if (mediaContainer != null) {
            mediaContainer.didLayoutChanged();
        }
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
                passCodeItem = toolbar.addItem(passCodeTag, R.string.icon_unlock, Color.WHITE);
            }

            ActivityMain.isLock = HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE);
            if (ActivityMain.isLock) {
                passCodeItem.setIcon(R.string.icon_lock);
            } else {
                passCodeItem.setIcon(R.string.icon_unlock);
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

    private void confirmActionForRemoveSelected() {
        int selectedRoomCount = selectedRoom.size();
        FrameLayout frameLayout = new FrameLayout(context);

        if (selectedRoomCount == 1) {
            CircleImageView imageView = new CircleImageView(context);
            AvatarHandler handler = new AvatarHandler();
            RealmRoom room = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("id", selectedRoom.get(0)).findFirst();
            });
            handler.getAvatar(new ParamWithAvatarType(imageView, room.getOwnerId()).avatarType(room.getType() != ProtoGlobal.Room.Type.CHAT ? AvatarHandler.AvatarType.ROOM : AvatarHandler.AvatarType.USER).showMain(), true);
            frameLayout.addView(imageView, LayoutCreator.createFrame(55, 55, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 8, 8, 8, 8));
        }

        TextView titleTextView = new TextView(context);
        if (selectedRoomCount == 1) {
            titleTextView.setText(getString(R.string.left));
        } else {
            titleTextView.setText(String.format(Locale.US, "%s %d %s", getString(R.string.delete), selectedRoomCount, getString(R.string.chat)));
        }

        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        titleTextView.setTextColor(Theme.getInstance().getTitleTextColor(context));

        int leftMargin = 20;
        int rightMargin = 20;
        if (selectedRoomCount == 1) {
            if (isAppRtl)
                rightMargin = 70;
            else
                leftMargin = 70;
        }
        frameLayout.addView(titleTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, leftMargin, 20, rightMargin, 20));

        TextView confirmTextView = new TextView(context);
        if (selectedRoomCount == 1) {
            RealmRoom realmRoom = getMessageDataStorage().getRoom(selectedRoom.get(0));
            String channelName = realmRoom.title;
            if (realmRoom.getType() == CHAT) {
                confirmTextView.setText(getString(R.string.delete_chat_content));
            } else {
                confirmTextView.setText(String.format(getString(R.string.leave_confirm), channelName));
            }

        } else {
            confirmTextView.setText(R.string.delete_selected_chat);
        }
        confirmTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        confirmTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        confirmTextView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        frameLayout.addView(confirmTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 20, 70, 20, 8));

        MaterialDialog.Builder builder = new MaterialDialog.Builder(G.fragmentActivity);
        builder.customView(frameLayout, false);
        builder.positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .negativeColor(Color.GRAY)
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

    private void confirmActionForClearHistoryOfSelected() {
        int selectedRoomCount = selectedRoom.size();
        FrameLayout frameLayout = new FrameLayout(context);

        if (selectedRoomCount == 1) {
            RealmRoom room = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("id", selectedRoom.get(0)).findFirst();
            });
            CircleImageView imageView = new CircleImageView(context);
            AvatarHandler handler = new AvatarHandler();
            handler.getAvatar(new ParamWithAvatarType(imageView, room.getOwnerId()).avatarType(AvatarHandler.AvatarType.ROOM).showMain(), true);
            frameLayout.addView(imageView, LayoutCreator.createFrame(55, 55, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 8, 8, 8, 8));
        }

        TextView titleTextView = new TextView(context);
        titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        if (selectedRoomCount == 1)
            titleTextView.setText(getString(R.string.clear_history));
        else
            titleTextView.setText(String.format(Locale.US, "%s %d %s", getString(R.string.clear_history), selectedRoomCount, getString(R.string.chat)));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleTextView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        if (selectedRoomCount == 1) {
            frameLayout.addView(titleTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, isAppRtl ? 20 : 70, 20, isAppRtl ? 70 : 20, 20));
        } else {
            frameLayout.addView(titleTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 20, 20, 20, 20));
        }

        TextView confirmTextView = new TextView(context);
        confirmTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        if (selectedRoomCount == 1) {
            RealmRoom realmRoom = getMessageDataStorage().getRoom(selectedRoom.get(0));
            String channelName = realmRoom.title;
            confirmTextView.setText(String.format(getString(R.string.clear_selected_history), channelName));
        } else {
            confirmTextView.setText(R.string.do_you_want_clear_history_this);
        }
        confirmTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        confirmTextView.setTextColor(Theme.getInstance().getTitleTextColor(context));
        frameLayout.addView(confirmTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, isAppRtl ? Gravity.RIGHT : Gravity.LEFT, 20, 70, 20, 8));

        MaterialDialog.Builder builder = new MaterialDialog.Builder(G.fragmentActivity);
        builder.customView(frameLayout, false);
        builder.positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .negativeColor(Color.GRAY)
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
            if (!(G.isLandscape && G.twoPaneMode)) {
                if (FragmentChat.mForwardMessages != null || HelperGetDataFromOtherApp.hasSharedData)
                    revertToolbarFromForwardMode();
            }

            if (inMultiSelectMode) {
                disableMultiSelect();
            }
            return false;
        } else if (FragmentChat.mForwardMessages != null || HelperGetDataFromOtherApp.hasSharedData) {
            revertToolbarFromForwardMode();
            return false;
        } else if (toolbar.isSearchFieldVisible()) {
            if (searchFragment != null) {
                searchFragment.onSearchCollapsed();
                toolbar.closeSearchBox(true);
            }
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
