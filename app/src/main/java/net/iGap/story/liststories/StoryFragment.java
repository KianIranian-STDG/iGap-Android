package net.iGap.story.liststories;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageController;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.BaseMainFragments;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.helper.upload.UploadTask;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SUID;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.upload.UploadHttpRequest;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoStoryGetStories;
import net.iGap.proto.ProtoStoryUserAddNew;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.response.ClientGetRoomListResponse;
import net.iGap.story.StoryObject;
import net.iGap.story.StoryPagerFragment;
import net.iGap.story.liststories.cells.HeaderCell;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.story.viewPager.StoryViewFragment;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static net.iGap.G.forcePriorityActionId;
import static net.iGap.G.isAppRtl;

public class StoryFragment extends BaseMainFragments implements ToolbarListener, RecyclerListView.OnItemClickListener, StoryCell.DeleteStory, EventManager.EventDelegate {

    private RecyclerListView recyclerListView;
    private ListAdapter adapter;
    private int rowSize;
    private int addStoryRow;
    private int recentHeaderRow;
    private int recentStoryRow;
    private int muteHeaderRow;
    private int muteStoryRow;
    public int mOffset = 0;
    public static boolean storyListFetched = false;
    private List<Long> userIdList;
    private List<List<String>> displayNameList;
    private List<RealmStory> stories;
    private List<RealmStory> otherUserRealmStory;
    private ProgressBar progressBar;
    private int firstVisibleItemPosition;
    private int firstVisibleItemPositionOffset;
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    private int counter = 0;
    private int recentStoryCounter = 0;
    private List<ProtoGlobal.Story> storyListFromProto = new ArrayList<>();
    private List<StoryObject> storyInLocal = new ArrayList<>();
    boolean isAddedUserStory = false;
    private int userStoryIndex = 0;
    private FrameLayout floatActionLayout;
    int objectsCounter = 0;
    boolean isHaveFailedUpload = false;


    @Override
    public void deleteStory(long storyId) {
        new HelperFragment(getActivity().getSupportFragmentManager(), new MyStatusStoryListFragment()).setReplace(false).load();
    }

    @Override
    public void onStoryClick(StoryCell storyCell) {


        if (storyCell.getStatus() == StoryCell.CircleStatus.CIRCLE_IMAGE) {
            new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment()).setReplace(false).load();
        } else if (storyCell.getStatus() == StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE) {
            if (storyCell.getUserId() == AccountManager.getInstance().getCurrentUser().getId()) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment(storyCell.getUserId(), true)).setReplace(false).load();
            } else {
                StoryViewFragment storyViewFragment;
                storyViewFragment = new StoryViewFragment(storyCell.getUserId(), false);
                new HelperFragment(getActivity().getSupportFragmentManager(), storyViewFragment).setReplace(false).load();
            }
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_LIST_FETCHED, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_DELETED, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_ALL_SEEN, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_USER_ADD_NEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_SENDING, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_UPLOAD, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_UPLOADED_FAILED, this);
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();
        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setDefaultTitle(getString(R.string.status))
                .getView();


        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));
        recyclerListView = new RecyclerListView(getContext());
        adapter = new ListAdapter();
        recyclerListView.setAdapter(adapter);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerListView.setClipToPadding(false);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(30));
        rootView.addView(recyclerListView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.GONE);
        rootView.addView(progressBar, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));


        floatActionLayout = new FrameLayout(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getInstance().getToolbarBackgroundColor(context), Theme.getInstance().getAccentColor(context));
        floatActionLayout.setBackground(drawable);
        IconView addButton = new IconView(context);
        addButton.setIcon(R.string.add_icon_without_circle_font);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.setVisibility(View.GONE);
        floatActionLayout.addView(addButton);
        rootView.addView(floatActionLayout, LayoutCreator.createFrame(52, 52, (isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, 16, 0, 16, 16));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_LIST_FETCHED, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_DELETED, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_ALL_SEEN, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_USER_ADD_NEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_SENDING, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_UPLOAD, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_UPLOADED_FAILED, this);
        mOffset = 0;
        userIdList = new ArrayList<>();
        displayNameList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        loadStories();
        floatActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment()).setReplace(false).load();
            }
        });
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
                        }
                        prevPosition = firstVisibleItem;
                        prevTop = firstViewTop;
                        scrollUpdated = true;
                    }
                }
            }
        });

    }


    private void loadStories() {

        DbManager.getInstance().doRealmTransaction(realmDB -> {
            realmDB.where(RealmStory.class).lessThan("realmStoryProtos.createdAt", System.currentTimeMillis() - MILLIS_PER_DAY).findAll().deleteAllFromRealm();
            stories = realmDB.copyFromRealm(realmDB.where(RealmStory.class).findAll());
            otherUserRealmStory = realmDB.where(RealmStory.class).notEqualTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findAll();
        });

        isAddedUserStory = false;
        if (userIdList.size() > 0) {
            userIdList = new ArrayList<>();
        }
        for (int i = 0; i < stories.size(); i++) {
            userIdList.add(stories.get(i).getUserId());

        }
        if (displayNameList.size() > 0) {
            displayNameList = new ArrayList<>();
        }
        displayNameList = getMessageDataStorage().getDisplayNameWithUserId(userIdList);
        if (stories != null && stories.size() > 0) {
            Log.e("fdajhfjshf", "loadStories ");
            progressBar.setVisibility(View.GONE);
            adapter = new ListAdapter();
            recyclerListView.setAdapter(adapter);
            recyclerListView.setVisibility(View.VISIBLE);
            adapter.addRow();
        } else {
            progressBar.setVisibility(View.GONE);
            adapter = new ListAdapter();
            recyclerListView.setAdapter(adapter);
            recyclerListView.setVisibility(View.VISIBLE);
            adapter.addRow();
        }
    }


    @Override
    public void onClick(View view, int position) {
        StoryCell storyCell = (StoryCell) view;
        if (position == addStoryRow) {
            if (storyCell.getStatus() == StoryCell.CircleStatus.CIRCLE_IMAGE) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment()).setReplace(false).load();
            } else if (storyCell.getStatus() == StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment(storyCell.getUserId(), true)).setReplace(false).load();
            }

        } else if (position > recentHeaderRow && position <= recentStoryRow || position > muteHeaderRow && position <= muteStoryRow) {
            StoryViewFragment storyViewFragment;
            storyViewFragment = new StoryViewFragment(storyCell.getUserId(), false);
            new HelperFragment(getActivity().getSupportFragmentManager(), storyViewFragment).setReplace(false).load();
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.STORY_LIST_FETCHED || id == EventManager.STORY_DELETED || id == EventManager.STORY_ALL_SEEN || id == EventManager.STORY_USER_ADD_NEW) {
            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadStories();
                }
            });
        } else if (id == EventManager.STORY_UPLOAD) {
            G.runOnUiThread(() -> {
                floatActionLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            });
            objectsCounter = 0;
            List<String> paths = (List<String>) args[0];
            ArrayList<StructBottomSheet> itemGalleryList = (ArrayList<StructBottomSheet>) args[1];
            DbManager.getInstance().doRealmTransaction(realm -> {
                for (int i = 0; i < paths.size(); i++) {
                    long storyId = SUID.id().get();
                    long lastUploadedStoryId = storyId + 1L;
                    int[] imageDimens = {0, 0};
                    long attachementId = SUID.id().get();
                    imageDimens = AndroidUtils.getImageDimens(paths.get(i));

                    RealmAttachment realmAttachment = realm.createObject(RealmAttachment.class, attachementId);
                    realmAttachment.setLocalFilePath(paths.get(i));
                    realmAttachment.setWidth(imageDimens[0]);
                    realmAttachment.setHeight(imageDimens[1]);
                    realmAttachment.setSize(new File(paths.get(i)).length());
                    realmAttachment.setName(new File(paths.get(i)).getName());

                    StoryObject storyObject = new StoryObject();
                    storyObject.isSeen = false;
                    storyObject.realmAttachment = realmAttachment;
                    storyObject.userId = AccountManager.getInstance().getCurrentUser().getId();
                    storyObject.createdAt = System.currentTimeMillis();
                    storyObject.caption = itemGalleryList.get(objectsCounter).getText();
                    storyObject.status = MessageObject.STATUS_SENDING;
                    storyObject.id = lastUploadedStoryId;
                    storyInLocal.add(storyObject);
                    RealmStory.putOrUpdate(realm, false, storyObject.userId, storyInLocal);
                    storyInLocal.remove(0);

                    Uploader.getInstance().upload(UploadObject.createForStory(lastUploadedStoryId, paths.get(i), null, itemGalleryList.get(objectsCounter).getText(), ProtoGlobal.RoomMessageType.IMAGE));

                    objectsCounter++;
                    if (objectsCounter == itemGalleryList.size()) {
                        G.runOnUiThread(() -> loadStories());
                        storyInLocal = new ArrayList<>();
                    }

                }
            });

        } else if (id == EventManager.STORY_SENDING) {
            if (isAdded() && isVisible()) {
                progressBar.setVisibility(View.GONE);
                floatActionLayout.setVisibility(View.GONE);
                G.runOnUiThread(() -> loadStories());
            }
        } else if (id == EventManager.STORY_UPLOADED_FAILED) {
            if (isAdded()) {
                progressBar.setVisibility(View.GONE);
                floatActionLayout.setVisibility(View.VISIBLE);
            }
            G.runOnUiThread(() -> loadStories());

        }
    }

    @Override
    public boolean isAllowToBackPressed() {
        return false;
    }

    @Override
    public void scrollToTopOfList() {

    }


    private class ListAdapter extends RecyclerListView.ItemAdapter {

        public void addRow() {
            rowSize = 0;
            recentStoryCounter = 0;
            userStoryIndex = 0;
            addStoryRow = rowSize++;
            if (stories != null && stories.size() > 1) {
                recentHeaderRow = rowSize++;
                for (int i = 0; i < stories.size(); i++) {
                    if (stories.get(i).getUserId() != AccountManager.getInstance().getCurrentUser().getId()) {
                        recentStoryRow = rowSize++;
                    }
                }
            } else if (stories != null && stories.size() == 1) {
                if (stories.get(0).getUserId() != AccountManager.getInstance().getCurrentUser().getId()) {
                    recentHeaderRow = rowSize++;
                    recentStoryRow = rowSize++;
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            switch (viewType) {
                case 0:
                    cellView = new StoryCell(context);
                    break;
                case 1:
                    cellView = new StoryCell(context);
                    break;
                case 2:
                    cellView = new HeaderCell(context);
                    break;
                default:
                    cellView = new View(parent.getContext());
            }
            return new RecyclerListView.ItemViewHolder(cellView, StoryFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                case 1:
                    StoryCell storyCell = (StoryCell) holder.itemView;
                    if (position == addStoryRow) {
                        DbManager.getInstance().doRealmTransaction(realm -> {
                            RealmStory realmStory = realm.where(RealmStory.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findFirst();
                            if (realmStory != null) {
                                if (realmStory.isSentAll()) {
                                    storyCell.setData(realmStory, displayNameList.get(position).get(0), displayNameList.get(position).get(1), context, true, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.CLICKED, null);
                                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                                    storyCell.deleteIconVisibility(true);
                                    storyCell.addIconVisibility(false);

                                } else {
                                    if (realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("status", MessageObject.STATUS_SENDING).findAll().size() > 0) {
                                        List<RealmStoryProto> realmStoryProtos;
                                        realmStoryProtos = realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("status", MessageObject.STATUS_SENDING).findAll();
                                        for (int i = 0; i < realmStoryProtos.size(); i++) {
                                            if (Uploader.getInstance().isCompressingOrUploading(String.valueOf(realmStoryProtos.get(i).getId()))) {
                                                floatActionLayout.setVisibility(View.GONE);
                                                isHaveFailedUpload = false;
                                                break;
                                            } else if (MessageController.isSendingStory) {
                                                isHaveFailedUpload = false;
                                                break;
                                            } else if (!MessageController.isSendingStory) {
                                                realm.where(RealmStoryProto.class).equalTo("id", realmStoryProtos.get(i).getId()).findFirst().setStatus(MessageObject.STATUS_FAILED);
                                                isHaveFailedUpload = true;
                                            }
                                        }

                                    } else if (realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("status", MessageObject.STATUS_FAILED).findAll().size() > 0) {
                                        isHaveFailedUpload = true;
                                    }


                                    if (isHaveFailedUpload) {
                                        G.runOnUiThread(() -> {
                                            floatActionLayout.setVisibility(View.VISIBLE);
                                            storyCell.setData(realmStory, displayNameList.get(position).get(0), displayNameList.get(position).get(1), context, true, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                                            storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                                            storyCell.deleteIconVisibility(true);
                                            storyCell.addIconVisibility(true);
                                        });

                                    } else {
                                        G.runOnUiThread(() -> {
                                            floatActionLayout.setVisibility(View.GONE);
                                            storyCell.setData(realmStory, displayNameList.get(position).get(0), displayNameList.get(position).get(1), context, true, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null);
                                            storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);
                                            storyCell.deleteIconVisibility(true);
                                            storyCell.addIconVisibility(false);
                                        });
                                    }

                                }


                                isAddedUserStory = true;

                            } else {
                                G.runOnUiThread(() -> {
                                    storyCell.initView(context, true, StoryCell.CircleStatus.CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null, 0);
                                    storyCell.setStatus(StoryCell.CircleStatus.CIRCLE_IMAGE);
                                    storyCell.setText(getString(R.string.my_status), getString(R.string.tap_to_add_status_update));
                                    storyCell.setImage(R.drawable.avatar, avatarHandler);
                                    storyCell.addIconVisibility(true);
                                    storyCell.deleteIconVisibility(false);
                                    floatActionLayout.setVisibility(View.VISIBLE);
                                });
                                isAddedUserStory = false;
                            }
                        });
                    } else if (((recentHeaderRow < position) && (position <= recentStoryRow))) {
                        if (otherUserRealmStory != null && otherUserRealmStory.size() > 0 && recentStoryCounter < otherUserRealmStory.size()) {

                            storyCell.setData(otherUserRealmStory.get(recentStoryCounter), "testi", "#FF4081", context, true, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, null, null);
                            if (!otherUserRealmStory.get(recentStoryCounter).isSeenAll()) {
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);
                            } else {
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                            }
                            storyCell.deleteIconVisibility(false);
                            storyCell.addIconVisibility(false);
                            recentStoryCounter++;

                        }
                    }
                    storyCell.setDeleteStory(StoryFragment.this);
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setTextColor(getResources().getColor(R.color.ou_background_crop));
                    if (position == recentHeaderRow) {
                        headerCell.setText(getString(R.string.recent_updates));
                    } else if (position == muteHeaderRow) {
                        headerCell.setText("به روزرسانی های بی صدا");
                    }
                    break;

            }
        }

        @Override
        public int getItemCount() {
            return rowSize;
        }

        @Override
        public int getItemViewType(int position) {
            if ((position == addStoryRow)) {
                return 0;
            } else if (((recentHeaderRow < position) && (position <= recentStoryRow))) {
                return 1;
            } else if (position == recentHeaderRow || position == muteHeaderRow) {
                return 2;
            }
            return super.getItemViewType(position);
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return viewType != 2;
        }
    }

}
