package net.iGap.story.liststories;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageController;
import net.iGap.fragments.BaseMainFragments;
import net.iGap.fragments.qrCodePayment.fragments.ScanCodeQRCodePaymentFragment;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SUID;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmContacts;
import net.iGap.story.MainStoryObject;
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
    private List<MainStoryObject> stories;
    private List<MainStoryObject> otherUserRealmStory;
    private ProgressBar progressBar;
    private int firstVisibleItemPosition;
    private int firstVisibleItemPositionOffset;
    private int counter = 0;
    private int recentStoryCounter = 0;
    private List<ProtoGlobal.Story> storyListFromProto = new ArrayList<>();
    private List<StoryObject> storyInLocal = new ArrayList<>();
    boolean isAddedUserStory = false;
    private int userStoryIndex = 0;
    private LinearLayout actionButtonsRootView;
    private FrameLayout floatActionLayout;
    private boolean floatingHidden;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private FrameLayout customStatusActionLayout;
    int objectsCounter = 0;
    boolean isHaveFailedUpload = false;
    private int myStoryCount = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int codeScannerTag = 1;


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
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getEventManager().removeObserver(EventManager.STORY_LIST_FETCHED, this);
        getEventManager().removeObserver(EventManager.STORY_DELETED, this);
        getEventManager().removeObserver(EventManager.STORY_ALL_SEEN, this);
        getEventManager().removeObserver(EventManager.STORY_USER_ADD_NEW, this);
        getEventManager().removeObserver(EventManager.STORY_SENDING, this);
        getEventManager().removeObserver(EventManager.STORY_UPLOAD, this);
        getEventManager().removeObserver(EventManager.STORY_UPLOADED_FAILED, this);
        getEventManager().removeObserver(EventManager.STORY_USER_INFO, this);
        getEventManager().removeObserver(EventManager.STORY_STATUS_UPLOAD, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        Toolbar storyToolbar = new Toolbar(getContext());
        storyToolbar.setTitle(isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        ToolbarItems toolbarItems = storyToolbar.createToolbarItems();
        toolbarItems.addItemWithWidth(codeScannerTag, R.string.icon_QR_code, 54);

        storyToolbar.setListener(i -> {
            switch (i) {
                case codeScannerTag:
                    onCodeScannerClickListener();
                    break;
            }
        });

        FrameLayout rootView = new FrameLayout(new ContextThemeWrapper(context, R.style.IGapRootViewStyle));
        rootView.addView(storyToolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));

        swipeRefreshLayout = new SwipeRefreshLayout(context);
        swipeRefreshLayout.setRefreshing(false);
        rootView.addView(swipeRefreshLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));
        recyclerListView = new RecyclerListView(getContext());
        adapter = new ListAdapter();
        recyclerListView.setAdapter(adapter);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerListView.setClipToPadding(false);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(30));
        swipeRefreshLayout.addView(recyclerListView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 0, 0, 0, 0));

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.GONE);
        rootView.addView(progressBar, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        actionButtonsRootView = new LinearLayout(context);
        actionButtonsRootView.setOrientation(LinearLayout.VERTICAL);
        rootView.addView(actionButtonsRootView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, 16, 0, 16, 16));


        customStatusActionLayout = new FrameLayout(context);
        Drawable customStatusDrawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getInstance().getToolbarBackgroundColor(context), Theme.getInstance().getAccentColor(context));
        customStatusActionLayout.setBackground(customStatusDrawable);
        IconView customStatusAddButton = new IconView(context);
        customStatusAddButton.setIcon(R.string.icon_edit);
        customStatusAddButton.setIconColor(Color.WHITE);

        customStatusActionLayout.addView(customStatusAddButton);
        actionButtonsRootView.addView(customStatusActionLayout, LayoutCreator.createLinear(42, 42, Gravity.CENTER, 0, 0, 0, 0));


        floatActionLayout = new FrameLayout(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getInstance().getToolbarBackgroundColor(context), Theme.getInstance().getAccentColor(context));
        floatActionLayout.setBackground(drawable);
        IconView addButton = new IconView(context);
        addButton.setIcon(R.string.icon_camera);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.addView(addButton);
        actionButtonsRootView.addView(floatActionLayout, LayoutCreator.createLinear(52, 52, Gravity.CENTER, 0, 10, 0, 0));
        return rootView;
    }

    private void onCodeScannerClickListener() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFrame, ScanCodeQRCodePaymentFragment.newInstance())
                .addToBackStack(null)
                .commit();
//        DbManager.getInstance().doRealmTask(realm -> {
//            String phoneNumber = "";
//            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
//            try {
//                if (userInfo != null) {
//                    phoneNumber = userInfo.getUserInfo().getPhoneNumber().substring(2);
//                } else {
//                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
//                }
//            } catch (Exception e) {
//                //maybe exception was for realm substring
//                try {
//                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
//                } catch (Exception ex) {
//                    //nothing
//                }
//            }
//
//            if (userInfo == null || !userInfo.isWalletRegister()) {
//                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber)).load();
//            } else {
//                getActivity().startActivityForResult(new HelperWallet().goToWallet(getContext(), new Intent(getActivity(), WalletActivity.class), "0" + phoneNumber, true), WALLET_REQUEST_CODE);
//            }
//
//        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getEventManager().addObserver(EventManager.STORY_LIST_FETCHED, this);
        getEventManager().addObserver(EventManager.STORY_DELETED, this);
        getEventManager().addObserver(EventManager.STORY_ALL_SEEN, this);
        getEventManager().addObserver(EventManager.STORY_USER_ADD_NEW, this);
        getEventManager().addObserver(EventManager.STORY_SENDING, this);
        getEventManager().addObserver(EventManager.STORY_UPLOAD, this);
        getEventManager().addObserver(EventManager.STORY_UPLOADED_FAILED, this);
        getEventManager().addObserver(EventManager.STORY_USER_INFO, this);
        getEventManager().addObserver(EventManager.STORY_STATUS_UPLOAD, this);
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
        customStatusActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new StatusTextFragment()).setReplace(false).load();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                DbManager.getInstance().doRealmTransaction(realm -> {
                    MessageController.getInstance(AccountManager.selectedAccount).getStories(realm.where(RealmContacts.class).findAll().size());
                });
            }
        });
        recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean scrollUpdated;
            private boolean scrollingManually;
            private int prevTop;
            private int prevPosition;
            private int currentScrollPosition = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                scrollingManually = newState == RecyclerView.SCROLL_STATE_DRAGGING;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentScrollPosition += dy;
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
//                if (currentScrollPosition == 0) {
//                    swipeRefreshLayout.setRefreshing(true);
//                }
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
        AbstractObject req = null;
        IG_RPC.Story_Get_Own_Story_Views story_get_own_story_views = new IG_RPC.Story_Get_Own_Story_Views();
        story_get_own_story_views.offset = 0;
        story_get_own_story_views.limit = myStoryCount;
        req = story_get_own_story_views;
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Story_Get_Own_Story_Views res = (IG_RPC.Res_Story_Get_Own_Story_Views) response;
                getMessageDataStorage().updateOwnViews(res.groupedViews);
            } else {

            }
        });
    }


    private void loadStories() {
        try {

            getMessageDataStorage().deleteExpiredStories();
            stories = getMessageDataStorage().getAllStories(null);
            Log.e("mmd", "loadStories: " + stories.size());
            myStoryCount = getMessageDataStorage().getCurrentUserStories().size();
            otherUserRealmStory = getMessageDataStorage().getOtherUsersStories();


        } catch (Exception e) {
            FileLog.e(e);
        }

        isAddedUserStory = false;
        if (userIdList.size() > 0) {
            userIdList = new ArrayList<>();
        }
        for (int i = 0; i < stories.size(); i++) {
            if (stories.get(i).userId != AccountManager.getInstance().getCurrentUser().getId()) {
                userIdList.add(stories.get(i).userId);
            }
        }
        if (displayNameList.size() > 0) {
            displayNameList = new ArrayList<>();
        }
        //  displayNameList = getMessageDataStorage().getDisplayNameWithUserId(userIdList);
        if (stories != null && stories.size() > 0) {
            Log.e("fdajhfjshf", "loadStories " + stories.size());
        }
        progressBar.setVisibility(View.GONE);
        adapter = new ListAdapter();
        recyclerListView.setAdapter(adapter);
        recyclerListView.setVisibility(View.VISIBLE);
        adapter.addRow();
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
        swipeRefreshLayout.setRefreshing(false);
        if (id == EventManager.STORY_LIST_FETCHED || id == EventManager.STORY_DELETED || id == EventManager.STORY_ALL_SEEN || id == EventManager.STORY_USER_ADD_NEW) {
            G.runOnUiThread(() -> loadStories());
        } else if (id == EventManager.STORY_UPLOAD) {
            G.runOnUiThread(() -> {
                actionButtonsRootView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            });
            objectsCounter = 0;
            List<String> paths = (List<String>) args[0];
            ArrayList<StructBottomSheet> itemGalleryList = (ArrayList<StructBottomSheet>) args[1];
            if (paths.size() > 1) {
                HttpUploader.isMultiUpload = true;
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
                storyObject.sessionId = AccountManager.getInstance().getCurrentUser().getId();
                storyObject.displayName = AccountManager.getInstance().getCurrentUser().getName();
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
                getMessageDataStorage().putStoriesToDatabaseOffline(false, storyObject.userId, storyInLocal, storyObject.displayName);
                storyInLocal.remove(0);
                HttpUploader.isStoryUploading = true;
                Uploader.getInstance().upload(UploadObject.createForStory(lastUploadedStoryId, paths.get(i), null, itemGalleryList.get(objectsCounter).getText(), ProtoGlobal.RoomMessageType.STORY));

                objectsCounter++;
                if (objectsCounter == itemGalleryList.size()) {
                    G.runOnUiThread(() -> loadStories());
                    storyInLocal = new ArrayList<>();
                }

            }

        } else if (id == EventManager.STORY_STATUS_UPLOAD) {


            String path = (String) args[0];
            long storyId = SUID.id().get();
            long lastUploadedStoryId = storyId + 1L;
            int[] imageDimens = {0, 0};
            long attachementId = SUID.id().get();
            imageDimens = AndroidUtils.getImageDimens(path);

            RealmAttachment realmAttachment = getMessageDataStorage().createRealmObject(path, imageDimens, attachementId);

            StoryObject storyObject = new StoryObject();
            storyObject.isSeen = false;
            storyObject.realmAttachment = realmAttachment;
            storyObject.userId = AccountManager.getInstance().getCurrentUser().getId();
            storyObject.sessionId = AccountManager.getInstance().getCurrentUser().getId();
            storyObject.displayName = AccountManager.getInstance().getCurrentUser().getName();
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

            getMessageDataStorage().putStoriesToDatabaseOffline(false, storyObject.userId, storyInLocal, storyObject.displayName);
            storyInLocal.remove(0);
            HttpUploader.isStoryUploading = true;
            Uploader.getInstance().upload(UploadObject.createForStory(lastUploadedStoryId, path, null, "", ProtoGlobal.RoomMessageType.STORY));

            objectsCounter++;
            G.runOnUiThread(() -> loadStories());
            storyInLocal = new ArrayList<>();


        } else if (id == EventManager.STORY_SENDING) {
            if (isAdded() && isVisible()) {
                progressBar.setVisibility(View.GONE);
                actionButtonsRootView.setVisibility(View.GONE);
                G.runOnUiThread(() -> loadStories());
            }
        } else if (id == EventManager.STORY_UPLOADED_FAILED) {
            if (isAdded()) {
                progressBar.setVisibility(View.GONE);
                actionButtonsRootView.setVisibility(View.VISIBLE);
            }
            G.runOnUiThread(() -> loadStories());

        } else if (id == EventManager.STORY_USER_INFO) {
            long userId = (long) args[0];
            Integer position = adapter.getFailedMessages(userId);
            if (position != null) {
                adapter.addRow();
            }
        }
    }

    @Override
    public boolean isAllowToBackPressed() {
        return true;
    }

    @Override
    public void scrollToTopOfList() {

    }


    private class ListAdapter extends RecyclerListView.ItemAdapter<RecyclerListView.ItemViewHolder> {

        public void addRow() {
            rowSize = 0;
            recentStoryCounter = 0;
            userStoryIndex = 0;
            addStoryRow = rowSize++;
            if (stories != null && stories.size() > 1) {
                recentHeaderRow = rowSize++;
                for (int i = 0; i < stories.size(); i++) {
                    if (stories.get(i).userId != AccountManager.getInstance().getCurrentUser().getId()) {
                        recentStoryRow = rowSize++;
                    }
                }
            } else if (stories != null && stories.size() == 1) {
                if (stories.get(0).userId != AccountManager.getInstance().getCurrentUser().getId()) {
                    recentHeaderRow = rowSize++;
                    recentStoryRow = rowSize++;
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerListView.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            return new RecyclerListView.ItemViewHolder(cellView, StoryFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerListView.ItemViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                case 1:
                    StoryCell storyCell = (StoryCell) holder.itemView;
                    if (position == addStoryRow) {

                        MainStoryObject mainStoryObject = getMessageDataStorage().getStoryById(AccountManager.getInstance().getCurrentUser().getId(), true);
                        if (mainStoryObject != null && mainStoryObject.storyObjects.size() > 0) {
                            if (mainStoryObject.isSentAll) {
                                G.runOnUiThread(() -> {
                                    actionButtonsRootView.setVisibility(View.VISIBLE);
                                });
                                storyCell.setStoryId(mainStoryObject.storyObjects.get(0).storyId);
                                storyCell.setData(mainStoryObject, mainStoryObject.profileColor, context, false, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.CLICKED, null);
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                                storyCell.deleteIconVisibility(true);
                                storyCell.addIconVisibility(false);

                            } else {
                                List<StoryObject> storyByStatusSending = getMessageDataStorage().getStoryByStatus(AccountManager.getInstance().getCurrentUser().getId(), MessageObject.STATUS_SENDING, false, null);
                                if (storyByStatusSending.size() > 0) {
                                    for (int i = 0; i < storyByStatusSending.size(); i++) {
                                        if (Uploader.getInstance().isCompressingOrUploading(String.valueOf(storyByStatusSending.get(i).id)) || MessageController.isSendingStory || HttpUploader.isStoryUploading) {
                                            actionButtonsRootView.setVisibility(View.GONE);
                                            isHaveFailedUpload = false;
                                            break;
                                        } else {
                                            getMessageDataStorage().updateStoryStatus(storyByStatusSending.get(i).id, MessageObject.STATUS_FAILED);
                                            isHaveFailedUpload = true;
                                        }
                                    }

                                } else if (getMessageDataStorage().getStoryByStatus(AccountManager.getInstance().getCurrentUser().getId(), MessageObject.STATUS_FAILED, false, null).size() > 0) {
                                    isHaveFailedUpload = true;
                                }


                                if (isHaveFailedUpload) {
                                    G.runOnUiThread(() -> {
                                        actionButtonsRootView.setVisibility(View.VISIBLE);
                                        storyCell.setStoryId(mainStoryObject.storyObjects.get(0).storyId);
                                        storyCell.setData(mainStoryObject, mainStoryObject.profileColor, context, false, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                                        storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                                        storyCell.deleteIconVisibility(true);
                                        storyCell.addIconVisibility(true);
                                    });

                                } else {
                                    G.runOnUiThread(() -> {
                                        actionButtonsRootView.setVisibility(View.GONE);
                                        storyCell.setStoryId(mainStoryObject.storyObjects.get(0).storyId);
                                        storyCell.setData(mainStoryObject, mainStoryObject.profileColor, context, false, StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null);
                                        storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);
                                        storyCell.deleteIconVisibility(true);
                                        storyCell.addIconVisibility(false);
                                    });
                                }

                            }


                            isAddedUserStory = true;

                        } else {
                            G.runOnUiThread(() -> {
                                storyCell.initView(context, false, StoryCell.CircleStatus.CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null, 0);
                                storyCell.setStatus(StoryCell.CircleStatus.CIRCLE_IMAGE);
                                storyCell.setText(getString(R.string.my_status), getString(R.string.tap_to_add_status_update));
                                storyCell.setImage(R.drawable.avatar, avatarHandler);
                                storyCell.addIconVisibility(true);
                                storyCell.deleteIconVisibility(false);
                                actionButtonsRootView.setVisibility(View.VISIBLE);
                            });
                            isAddedUserStory = false;
                        }

                    } else if (((recentHeaderRow < position) && (position <= recentStoryRow))) {
                        if (otherUserRealmStory != null && otherUserRealmStory.size() > 0 && recentStoryCounter < otherUserRealmStory.size() && otherUserRealmStory.get(recentStoryCounter).storyObjects.size() > 0) {
                            storyCell.setStoryId(otherUserRealmStory.get(recentStoryCounter).storyObjects.get(0).storyId);
                            storyCell.setData(otherUserRealmStory.get(recentStoryCounter), otherUserRealmStory.get(recentStoryCounter).profileColor, context, (recentStoryCounter + 1) != otherUserRealmStory.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, null, null);
                            if (!otherUserRealmStory.get(recentStoryCounter).isSeenAll) {
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
                    headerCell.setTextColor(Theme.getInstance().getSendMessageTextColor(headerCell.getContext()));
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

        public Integer getFailedMessages(long userId) {
            for (int i = 0; i < otherUserRealmStory.size(); i++) {
                if (otherUserRealmStory.get(i) != null && otherUserRealmStory.get(i).userId == userId) {
                    return i;
                }
            }
            return null;
        }

    }


}
