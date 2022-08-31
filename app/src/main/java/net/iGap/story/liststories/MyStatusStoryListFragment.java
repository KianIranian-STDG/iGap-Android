package net.iGap.story.liststories;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageController;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.story.StatusTextFragment;
import net.iGap.story.StoryObject;
import net.iGap.story.StoryPagerFragment;
import net.iGap.story.liststories.cells.HeaderCell;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.story.viewPager.StoryViewFragment;
import net.iGap.structs.MessageObject;


import java.util.ArrayList;
import java.util.List;

import static net.iGap.G.isAppRtl;

public class MyStatusStoryListFragment extends BaseFragment implements StoryCell.DeleteStory, EventManager.EventDelegate {
    private RecyclerListView recyclerListView;
    private ListAdapter adapter;
    List<StoryObject> storyProto;
    List<StoryObject> storyRoomProto;
    private FrameLayout floatActionLayout;
    private FrameLayout customStatusActionLayout;
    private LinearLayout actionButtonsRootView;
    private int firstVisibleItemPosition;
    private int firstVisibleItemPositionOffset;
    private boolean floatingHidden;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    public static boolean storyListFetched = false;
    public int mOffset = 0;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    private ProgressBar progressBar;
    int counter = 0;
    private int rowSize;
    private int recentHeaderRow;
    private int recentRoomHeaderRow;
    private int recentRoomStoryCounter = 0;
    private int recentStoryRow;
    private int recentًRoomStoryRow;
    private boolean isFromRoom;
    private boolean isForRoomImage;
    private String roomImagePath;
    private List<StoryObject> storyInLocal = new ArrayList<>();
    private List<String> paths;
    private ArrayList<StructBottomSheet> itemGalleryList;
    private long roomId;
    private String roomTitle;
    private int listMode = 0;
    private int objectsCounter = 0;
    private boolean isFromRoomMode = false;
    private boolean isHaveFailedUpload = false;

    public MyStatusStoryListFragment() {

    }

    public MyStatusStoryListFragment(boolean isFromRoom, String imagePath, long roomId, String roomTitle, int listMode) {
        this.isFromRoom = isFromRoom;
        this.roomImagePath = imagePath;
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.listMode = listMode;
    }

    public MyStatusStoryListFragment(long roomId, int listMode) {
        this.listMode = listMode;
        this.roomId = roomId;
    }

    public MyStatusStoryListFragment(List<String> paths, ArrayList<StructBottomSheet> itemGalleryList, long roomId, int listMode, String roomTitle) {
        this.paths = paths;
        this.itemGalleryList = itemGalleryList;
        this.roomId = roomId;
        this.listMode = listMode;
        this.roomTitle = roomTitle;
        this.isFromRoom = true;
        this.isForRoomImage = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getEventManager().removeObserver(EventManager.STORY_LIST_FETCHED, this);
        getEventManager().removeObserver(EventManager.STORY_USER_ADD_NEW, this);
        getEventManager().removeObserver(EventManager.STORY_USER_ADD_VIEW, this);
        getEventManager().removeObserver(EventManager.STORY_DELETED, this);
        getEventManager().removeObserver(EventManager.STORY_UPLOADED_FAILED, this);
        getEventManager().removeObserver(EventManager.STORY_UPLOAD, this);
        getEventManager().removeObserver(EventManager.STORY_SENDING, this);
        getEventManager().removeObserver(EventManager.STORY_USER_INFO, this);
        getEventManager().removeObserver(EventManager.STORY_STATUS_UPLOAD, this);
        getEventManager().removeObserver(EventManager.STORY_ROOM_UPLOAD, this);
        getEventManager().removeObserver(EventManager.STORY_ROOM_IMAGE_UPLOAD, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        Toolbar myStoryToolbar = new Toolbar(getContext());
        myStoryToolbar.setTitle(getString(R.string.my_status));
        myStoryToolbar.setBackIcon(new BackDrawable(false));
        myStoryToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    requireActivity().onBackPressed();
                    break;
            }
        });


        FrameLayout rootView = new FrameLayout(new ContextThemeWrapper(context, R.style.IGapRootViewStyle));
        rootView.addView(myStoryToolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));
        rootView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        recyclerListView = new RecyclerListView(getContext());
        adapter = new ListAdapter();
        recyclerListView.setAdapter(adapter);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerListView.setClipToPadding(false);
        recyclerListView.setVisibility(View.GONE);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(30));

        rootView.addView(recyclerListView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.GONE);
        rootView.addView(progressBar, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        actionButtonsRootView = new LinearLayout(context);
        actionButtonsRootView.setOrientation(LinearLayout.VERTICAL);
        rootView.addView(actionButtonsRootView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, 16, 0, 16, 16));

        customStatusActionLayout = new FrameLayout(context);
        Drawable customStatusDrawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getColor(Theme.key_toolbar_background), Theme.getColor(Theme.key_theme_color));
        customStatusActionLayout.setBackground(customStatusDrawable);
        IconView customStatusAddButton = new IconView(context);
        customStatusAddButton.setIcon(R.string.icon_edit);
        customStatusAddButton.setIconColor(Color.WHITE);
        customStatusActionLayout.addView(customStatusAddButton);
        actionButtonsRootView.addView(customStatusActionLayout, LayoutCreator.createLinear(42, 42, Gravity.CENTER, 0, 0, 0, 0));


        floatActionLayout = new FrameLayout(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getColor(Theme.key_toolbar_background), Theme.getColor(Theme.key_theme_color));
        floatActionLayout.setBackground(drawable);
        IconView addButton = new IconView(context);
        addButton.setIcon(R.string.icon_camera);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.addView(addButton);
        actionButtonsRootView.addView(floatActionLayout, LayoutCreator.createLinear(52, 52, Gravity.CENTER, 0, 10, 0, 0));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getEventManager().addObserver(EventManager.STORY_LIST_FETCHED, this);
        getEventManager().addObserver(EventManager.STORY_USER_ADD_NEW, this);
        getEventManager().addObserver(EventManager.STORY_USER_ADD_VIEW, this);
        getEventManager().addObserver(EventManager.STORY_DELETED, this);
        getEventManager().addObserver(EventManager.STORY_UPLOADED_FAILED, this);
        getEventManager().addObserver(EventManager.STORY_UPLOAD, this);
        getEventManager().addObserver(EventManager.STORY_SENDING, this);
        getEventManager().addObserver(EventManager.STORY_USER_INFO, this);
        getEventManager().addObserver(EventManager.STORY_STATUS_UPLOAD, this);
        getEventManager().addObserver(EventManager.STORY_ROOM_IMAGE_UPLOAD, this);
        getEventManager().addObserver(EventManager.STORY_ROOM_UPLOAD, this);
        progressBar.setVisibility(View.VISIBLE);
        sendRoomStory();
        loadStories();
        AbstractObject req = null;
        IG_RPC.Story_Get_Own_Story_Views story_get_own_story_views = new IG_RPC.Story_Get_Own_Story_Views();
        story_get_own_story_views.offset = 0;
        story_get_own_story_views.limit = 50;
        req = story_get_own_story_views;
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Story_Get_Own_Story_Views res = (IG_RPC.Res_Story_Get_Own_Story_Views) response;
                getMessageDataStorage().updateOwnViews(res.groupedViews);
                //G.runOnUiThread(() -> loadStories());

            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        floatActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listMode == 1) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment(true, roomId, 1, roomTitle)).setReplace(false).load();
                } else {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment(false)).setReplace(false).load();
                }

            }
        });

        customStatusActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listMode == 1) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new StatusTextFragment(true, roomId, 1, roomTitle)).setReplace(false).load();
                } else {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new StatusTextFragment(false)).setReplace(false).load();
                }

            }
        });

        mOffset = 0;
        recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                if (!storyListFetched) {
                    if (mOffset > 0) {
                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastVisiblePosition + 10 >= mOffset) {
//                            GetStoryList(mOffset, 50);
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
    }

    private void sendRoomStory() {
        if (isFromRoom && !isForRoomImage) {

            long storyId = SUID.id().get();
            long lastUploadedStoryId = storyId + 1L;
            int[] imageDimens = {0, 0};
            long attachementId = SUID.id().get();
            imageDimens = AndroidUtils.getImageDimens(roomImagePath);

            RealmAttachment realmAttachment = getMessageDataStorage().createRealmObject(roomImagePath, imageDimens, attachementId);

            StoryObject storyObject = new StoryObject();
            storyObject.isSeen = false;
            storyObject.realmAttachment = realmAttachment;
            storyObject.isForRoom = isFromRoom;
            storyObject.userId = AccountManager.getInstance().getCurrentUser().getId();
            storyObject.roomId = this.roomId;
            storyObject.sessionId = AccountManager.getInstance().getCurrentUser().getId();
            storyObject.displayName = this.roomTitle;
            storyObject.createdAt = System.currentTimeMillis();
            storyObject.caption = "";
            storyObject.status = MessageObject.STATUS_SENDING;
            storyObject.id = lastUploadedStoryId;
            List<StoryObject> realmStories = getMessageDataStorage().getStoryWithIndexSort(storyObject.userId);
            if (realmStories != null && realmStories.size() > 0) {
                storyObject.index = realmStories.get(0).index + 1;
            } else {
                storyObject.index = 0;
            }
            storyInLocal.add(storyObject);

            getMessageDataStorage().putStoriesToDatabaseOffline(false, storyObject.userId, storyObject.roomId, storyInLocal, storyObject.displayName, isFromRoom);
            storyInLocal.remove(0);
            HttpUploader.isStoryUploading = true;
            Uploader.getInstance().upload(UploadObject.createForStory(lastUploadedStoryId, roomImagePath, null, "", ProtoGlobal.RoomMessageType.STORY));

            storyInLocal = new ArrayList<>();
        } else if (isForRoomImage) {
            sendRoomStory(paths, itemGalleryList, roomId, listMode, roomTitle);
        }
    }

    private void loadStories() {

        getMessageDataStorage().deleteExpiredStories();
        if (listMode == 0) {
            storyProto = getMessageDataStorage().getCurrentUserStories(true);
            storyRoomProto = getMessageDataStorage().getCurrentUserRoomStories(0, listMode);
        } else {
            storyRoomProto = getMessageDataStorage().getCurrentUserRoomStories(roomId, listMode);
        }


        if (storyProto != null && storyProto.size() == 0 && storyRoomProto != null && storyRoomProto.size() == 0) {
            getMessageDataStorage().deleteStoryByUserId(AccountManager.getInstance().getCurrentUser().getId());
            storyProto = getMessageDataStorage().getCurrentUserStories(true);
        }


        List<Long> userIdList = new ArrayList<>();
        userIdList.add(AccountManager.getInstance().getCurrentUser().getId());

        if ((storyProto != null && storyProto.size() > 0) || storyRoomProto != null && storyRoomProto.size() > 0) {
            progressBar.setVisibility(View.GONE);
            adapter = new ListAdapter();
            recyclerListView.setAdapter(adapter);
            recyclerListView.setVisibility(View.VISIBLE);
            adapter.addRow();
        } else {
            progressBar.setVisibility(View.GONE);
            if (isFromRoomMode && listMode == 1 && getMessageDataStorage().getCurrentUserStories(true).size() == 0) {
                new HelperFragment(getActivity().getSupportFragmentManager(), MyStatusStoryListFragment.this).popBackStack(2);
            } else if (isFromRoomMode && listMode == 1 && getMessageDataStorage().getCurrentUserStories(true).size() > 0) {
                new HelperFragment(getActivity().getSupportFragmentManager(), MyStatusStoryListFragment.this).popBackStack(1);
            } else if (!isFromRoomMode && listMode == 0) {
                new HelperFragment(getActivity().getSupportFragmentManager(), MyStatusStoryListFragment.this).popBackStack(1);
            }

        }


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
            floatingButtonTranslation = (LayoutCreator.dp(200) * floatingButtonHideProgress);
            actionButtonsRootView.setTranslationY(floatingButtonTranslation - 0 * (1f - floatingButtonHideProgress));
        });
        animatorSet.playTogether(valueAnimator);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(floatingInterpolator);
        actionButtonsRootView.setClickable(!hide);
        animatorSet.start();
    }

    @Override
    public void deleteStory(StoryCell storyCell, long storyId, long roomId, boolean isRoom) {
        if (isRoom && listMode == 0) {
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.mainFrame, new MyStatusStoryListFragment(roomId, 1)).commit();
            //  new HelperFragment(getActivity().getSupportFragmentManager(), new MyStatusStoryListFragment(1)).setReplace(false).load();
        } else {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(getResources().getString(R.string.delete_status_update))
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                    .positiveText(R.string.ok)
                    .onNegative((dialog1, which) -> dialog1.dismiss()).show();

            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
                progressBar.setVisibility(View.VISIBLE);
                recyclerListView.setVisibility(View.GONE);
                if (storyCell.getSendStatus() == MessageObject.STATUS_FAILED) {
                    getMessageDataStorage().deleteUserStoryWithUploadId(storyCell.getUploadId(), storyCell.getUserId());
                } else {
                    AbstractObject req = null;
                    IG_RPC.Story_Delete story_delete = new IG_RPC.Story_Delete();
                    story_delete.storyId = storyId;
                    req = story_delete;
                    getRequestManager().sendRequest(req, (response, error) -> {
                        if (error == null) {
                            IG_RPC.Res_Story_Delete res = (IG_RPC.Res_Story_Delete) response;
                            getMessageDataStorage().deleteUserStoryWithStoryId(res.storyId, res.userId);
                        } else {

                        }
                    });
                }
                dialog.dismiss();
            });
        }

    }

    @Override
    public void onStoryClick(StoryCell storyCell) {
        new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment(storyCell.getUserId(), storyCell.getRoomId(), true, (!storyCell.isRoom() && listMode == 0) || (storyCell.isRoom() && listMode == 1), storyCell.isRoom(), false, storyCell.getStoryId() != 0 ? storyCell.getStoryId() : storyCell.getStoryIndex(), 0)).setReplace(false).load();
    }

    @Override
    public void onStoryLongClick(StoryCell storyCell) {

        if ((storyCell.getSendStatus() == MessageObject.STATUS_FAILED ||
                storyCell.getSendStatus() == MessageObject.STATUS_SENT) && (!storyCell.isRoom() || listMode == 1)) {
            storyCell.setLongClicked(true);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.delete_status_update))
                    .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                    .positiveText(R.string.ok)
                    .onNegative((dialog1, which) -> {
                        dialog1.dismiss();
                        storyCell.setLongClicked(false);
                    }).show();
            dialog.setOnDismissListener(dialogInterface -> {
                storyCell.setLongClicked(false);
            });
            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
                progressBar.setVisibility(View.VISIBLE);
                recyclerListView.setVisibility(View.GONE);
                storyCell.setLongClicked(false);
                if (storyCell.getSendStatus() == MessageObject.STATUS_FAILED) {
                    getMessageDataStorage().deleteUserStoryWithUploadId(storyCell.getUploadId(), storyCell.getUserId());
                } else {
                    AbstractObject req = null;
                    IG_RPC.Story_Delete story_delete = new IG_RPC.Story_Delete();
                    story_delete.storyId = storyCell.getStoryId();
                    req = story_delete;
                    getRequestManager().sendRequest(req, (response, error) -> {
                        if (error == null) {
                            IG_RPC.Res_Story_Delete res = (IG_RPC.Res_Story_Delete) response;
                            getMessageDataStorage().deleteUserStoryWithStoryId(res.storyId, res.userId);
                        } else {

                        }
                    });
                }


                dialog.dismiss();

            });
        }
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.STORY_LIST_FETCHED || id == EventManager.STORY_USER_ADD_NEW ||
                id == EventManager.STORY_USER_ADD_VIEW || id == EventManager.STORY_DELETED ||
                id == EventManager.STORY_UPLOADED_FAILED || id == EventManager.STORY_USER_INFO || id == EventManager.STORY_ROOM_INFO) {
            G.runOnUiThread(() -> {
                actionButtonsRootView.setVisibility(View.VISIBLE);
                if (id == EventManager.STORY_DELETED) {
                    this.isFromRoomMode = (boolean) args[0];
                }
                loadStories();
            });
        } else if (id == EventManager.STORY_UPLOAD || id == EventManager.STORY_STATUS_UPLOAD) {
            G.runOnUiThread(() -> {
                actionButtonsRootView.setVisibility(View.GONE);
                loadStories();
            });
        } else if (id == EventManager.STORY_SENDING) {
            G.runOnUiThread(() -> {
                actionButtonsRootView.setVisibility(View.GONE);
                if (listMode == 0) {
                    loadStories();
                }
            });
        } else if (id == EventManager.STORY_ROOM_UPLOAD) {
            if (listMode == 0) {
                actionButtonsRootView.setVisibility(View.GONE);
                loadStories();
            } else {
                String path = (String) args[0];
                long roomId = (long) args[1];
                String roomTitle = (String) args[2];
                this.isFromRoom = true;
                this.roomImagePath = path;
                this.roomId = roomId;
                this.roomTitle = roomTitle;
                sendRoomStory();
                G.runOnUiThread(() -> loadStories());
            }

        } else if (id == EventManager.STORY_ROOM_IMAGE_UPLOAD) {
            if (listMode == 0) {
                actionButtonsRootView.setVisibility(View.GONE);
                loadStories();
            } else {
                List<String> paths = (List<String>) args[0];
                ArrayList<StructBottomSheet> itemGalleryList = (ArrayList<StructBottomSheet>) args[1];
                long roomId = (long) args[2];
                int listMode = (int) args[3];
                String roomTitle = (String) args[4];
                sendRoomStory(paths, itemGalleryList, roomId, listMode, roomTitle);
                G.runOnUiThread(() -> loadStories());
            }

        }
    }

    private void sendRoomStory(List<String> paths, ArrayList<StructBottomSheet> itemGalleryList, long roomId, int listMode, String roomTitle) {
        G.runOnUiThread(() -> {
            actionButtonsRootView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        });
        objectsCounter = 0;

        if (paths.size() > 1) {
            HttpUploader.isRoomMultiUpload = true;
        }

        for (int i = 0; i < paths.size(); i++) {
            long storyId = SUID.id().get();
            long lastUploadedStoryId = storyId + 1L;
            int[] imageDimens = {0, 0};
            long attachementId = SUID.id().get();
            imageDimens = AndroidUtils.getImageDimens(paths.get(i));

            RealmAttachment realmAttachment = getMessageDataStorage().createRealmObject(paths.get(i), imageDimens, attachementId);

            StoryObject storyObject = new StoryObject();
            storyObject.isSeen = false;
            storyObject.realmAttachment = realmAttachment;
            storyObject.userId = AccountManager.getInstance().getCurrentUser().getId();
            storyObject.roomId = roomId;
            storyObject.isForRoom = true;
            storyObject.sessionId = AccountManager.getInstance().getCurrentUser().getId();
            storyObject.displayName = roomTitle;
            storyObject.createdAt = System.currentTimeMillis();
            storyObject.caption = itemGalleryList.get(objectsCounter).getText();
            storyObject.status = MessageObject.STATUS_SENDING;
            storyObject.id = lastUploadedStoryId;
            List<StoryObject> realmStories = getMessageDataStorage().getStoryWithIndexSort(storyObject.userId);
            if (realmStories != null && realmStories.size() > 0) {
                storyObject.index = realmStories.get(0).index + 1;
            } else {
                storyObject.index = i;
            }
            storyInLocal.add(storyObject);
            getMessageDataStorage().putStoriesToDatabaseOffline(false, storyObject.userId, storyObject.roomId, storyInLocal, storyObject.displayName, true);
            storyInLocal.remove(0);
            HttpUploader.isStoryUploading = true;
            Uploader.getInstance().upload(UploadObject.createForStory(lastUploadedStoryId, paths.get(i), null, itemGalleryList.get(objectsCounter).getText(), ProtoGlobal.RoomMessageType.STORY));

            objectsCounter++;
            if (objectsCounter == itemGalleryList.size()) {
                G.runOnUiThread(() -> loadStories());
                storyInLocal = new ArrayList<>();
            }

        }
    }

    private void removeEvents() {
        getEventManager().removeObserver(EventManager.STORY_ROOM_UPLOAD, this);
    }

    private void addEvents() {
        getEventManager().addObserver(EventManager.STORY_ROOM_UPLOAD, this);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        public void addRow() {
            rowSize = 0;
            long lastAddedRoomId = 0;
            recentRoomStoryCounter = 0;
            if (storyProto != null && storyProto.size() > 0 && storyRoomProto != null && storyRoomProto.size() > 0) {
                for (int i = rowSize; i < storyProto.size(); i++) {
                    recentStoryRow = rowSize++;
                }
                recentRoomHeaderRow = rowSize++;

                for (int i = 0; i < storyRoomProto.size(); i++) {
                    if (lastAddedRoomId != storyRoomProto.get(i).roomId) {
                        recentًRoomStoryRow = rowSize++;
                        lastAddedRoomId = storyRoomProto.get(i).roomId;
                    }


                }

                recentHeaderRow = rowSize++;
            } else if (storyProto != null && storyProto.size() > 0) {
                for (int i = rowSize; i < storyProto.size(); i++) {
                    recentStoryRow = rowSize++;
                }
                recentHeaderRow = rowSize++;
            } else if (storyRoomProto != null && storyRoomProto.size() > 0 && listMode == 0) {
                recentRoomHeaderRow = rowSize++;
                for (int i = 0; i < storyRoomProto.size(); i++) {
                    if (lastAddedRoomId != storyRoomProto.get(i).roomId) {
                        recentًRoomStoryRow = rowSize++;
                        lastAddedRoomId = storyRoomProto.get(i).roomId;
                    }

                }
                recentHeaderRow = rowSize++;
            } else if (storyRoomProto != null && storyRoomProto.size() > 0 && listMode == 1) {
                for (int i = 0; i < storyRoomProto.size(); i++) {
                    recentًRoomStoryRow = ++rowSize;
                }
                recentHeaderRow = ++rowSize;
            }

            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            switch (viewType) {
                case 0:
                case 1:
                    cellView = new StoryCell(context);
                    break;
                case 2:
                    cellView = new HeaderCell(context);
                    break;
                default:
                    cellView = new View(parent.getContext());
            }
            return new RecyclerListView.Holder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                case 1:

                    if (position <= recentStoryRow && listMode == 0) {
                        StoryCell storyCell = (StoryCell) holder.itemView;
                        if (position < storyProto.size()) {
                            setDataForStoryCell(storyCell, false, position, storyProto);
                            storyCell.addIconVisibility(false);
                        }


                    } else if (((recentRoomHeaderRow < position) && (position <= recentًRoomStoryRow)) || listMode == 1) {
                        if (storyRoomProto != null && storyRoomProto.size() > 0 && recentRoomStoryCounter < storyRoomProto.size()) {
                            StoryCell storyCell = (StoryCell) holder.itemView;
                            setDataForStoryCell(storyCell, true, recentRoomStoryCounter, storyRoomProto);
                            storyCell.addIconVisibility(false);
                            if (storyRoomProto.get(recentRoomStoryCounter).status == MessageObject.STATUS_FAILED) {
                                storyCell.deleteIconVisibility(true, listMode == 0 ? R.string.icon_other_horizontal_dots : R.string.icon_Delete);
                            } else if (storyRoomProto.get(recentRoomStoryCounter).status == MessageObject.STATUS_SENT) {
                                storyCell.deleteIconVisibility(true, listMode == 0 ? R.string.icon_other_horizontal_dots : R.string.icon_Delete);
                            } else {
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);
                                if (listMode == 0) {
                                    storyCell.deleteIconVisibility(true, R.string.icon_other_horizontal_dots);
                                } else {
                                    storyCell.deleteIconVisibility(false);
                                }

                            }
                            recentRoomStoryCounter++;
                        }
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setTextColor(Theme.getColor(Theme.key_default_text));
                    headerCell.setGravity(Gravity.CENTER);
                    headerCell.setTextSize(12);
                    if (position == recentHeaderRow) {
                        headerCell.setText(getString(R.string.your_status_updates_will_disappear_after_24_hours));
                    } else if (position == recentRoomHeaderRow) {
                        headerCell.setText(getString(R.string.my_channel_moments));
                    }
                    break;

            }
        }

        private void setDataForStoryCell(StoryCell storyCell, boolean isRoom, int position, List<StoryObject> storyProto) {
            storyCell.setStoryId(storyProto.get(position).storyId);
            storyCell.setUploadId(storyProto.get(position).id);
            storyCell.setFileToken(storyProto.get(position).fileToken);
            storyCell.setSendStatus(storyProto.get(position).status);
            storyCell.setStoryIndex(storyProto.get(position).index);
            storyCell.setRoomId(storyProto.get(position).roomId);
            storyCell.setMode(listMode);
            storyCell.setRoom(isRoom);
            if (isRoom && listMode == 0) {
                List<StoryObject> storyByStatusSending = getMessageDataStorage().getStoryByStatus(AccountManager.getInstance().getCurrentUser().getId(), storyProto.get(position).roomId, MessageObject.STATUS_SENDING, false, true, null);
                if (storyByStatusSending.size() > 0) {
                    for (int i = 0; i < storyByStatusSending.size(); i++) {
                        if (Uploader.getInstance().isCompressingOrUploading(String.valueOf(storyByStatusSending.get(i).id)) || MessageController.isSendingRoomStory || HttpUploader.isStoryUploading) {
                            actionButtonsRootView.setVisibility(View.GONE);
                            isHaveFailedUpload = false;
                            actionButtonsRootView.setVisibility(View.GONE);
                            storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null);
                            storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);
                            break;
                        } else {
                            getMessageDataStorage().updateStoryStatus(storyByStatusSending.get(i).id, MessageObject.STATUS_FAILED);
                            isHaveFailedUpload = true;
                            actionButtonsRootView.setVisibility(View.VISIBLE);
                            storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                            storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                        }
                    }

                } else if (getMessageDataStorage().getStoryByStatus(AccountManager.getInstance().getCurrentUser().getId(), storyProto.get(position).roomId, MessageObject.STATUS_FAILED, false, true, null).size() > 0) {
                    isHaveFailedUpload = true;
                    actionButtonsRootView.setVisibility(View.VISIBLE);
                    storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                } else {
                    storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.CLICKED, null);
                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                    storyCell.deleteIconVisibility(true, R.string.icon_Delete);
                }


            } else {
                if (storyProto.get(position).status == MessageObject.STATUS_FAILED) {
                    actionButtonsRootView.setVisibility(View.VISIBLE);
                    storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                } else if (storyProto.get(position).status == MessageObject.STATUS_SENDING) {
                    if (!Uploader.getInstance().isCompressingOrUploading(String.valueOf(storyProto.get(position).id)) && !MessageController.isSendingStory && !HttpUploader.isStoryUploading) {
                        actionButtonsRootView.setVisibility(View.VISIBLE);
                        long failedStoryId = storyProto.get(position).id;


                        getMessageDataStorage().updateStoryStatus(failedStoryId, MessageObject.STATUS_FAILED);

                        storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                        storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                    } else {
                        actionButtonsRootView.setVisibility(View.GONE);
                        storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null);
                        storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);

                    }

                } else {
                    storyCell.setData(storyProto.get(position), isRoom, storyProto.get(position).displayName, storyProto.get(position).profileColor, context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.CLICKED, null);
                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                    storyCell.deleteIconVisibility(true, R.string.icon_Delete);
                }
            }


            storyCell.setDeleteStory(MyStatusStoryListFragment.this);

            storyCell.setStatus(StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE);


        }

        @Override
        public int getItemCount() {
            return rowSize;
        }

        @Override
        public int getItemViewType(int position) {
            if ((position == recentStoryRow) && storyProto != null && storyProto.size() > 0) {
                return 0;
            } else if ((position == recentًRoomStoryRow) && storyRoomProto != null && storyRoomProto.size() > 0) {
                return 0;
            } else if (((recentHeaderRow < position) && (position <= recentStoryRow) && storyProto != null && storyProto.size() > 0)) {
                return 1;
            } else if (((recentRoomHeaderRow < position) && (position <= recentًRoomStoryRow) && storyRoomProto != null && storyRoomProto.size() > 0)) {
                return 1;
            } else if (position == recentHeaderRow && storyProto != null && storyProto.size() > 0) {
                return 2;
            } else if (position == recentRoomHeaderRow && storyRoomProto != null && storyRoomProto.size() > 0) {
                return 2;
            } else if (position == recentHeaderRow && storyRoomProto != null && storyRoomProto.size() > 0) {
                return 2;
            }
            return super.getItemViewType(position);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType != 2;
        }
    }
}
