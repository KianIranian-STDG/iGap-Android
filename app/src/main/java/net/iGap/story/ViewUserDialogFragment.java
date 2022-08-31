package net.iGap.story;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.story.storyviews.StoryCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewUserDialogFragment extends BottomSheetDialogFragment implements RecyclerListView.OnItemClickListener, EventManager.EventDelegate, ToolbarListener, StoryCell.DeleteStory {

    private int rowSize = 0;
    private int userRow = 0;
    private int count = 0;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FrameLayout rootView;
    private LinearLayout dataRootView;
    private FrameLayout toolbarView;
    private AppCompatTextView toolbarTitleTextView;
    private Typeface tfMain;
    private List<StoryViewInfoObject> userIdList;
    private List<String> displayNameList;
    private List<Long> userId;
    private List<Long> storyIdList;
    public AvatarHandler avatarHandler;
    private List<Long> createdAtList;
    private ListAdapter listAdapter;
    private long storyId = 0;
    private ViewUserDialogState viewUserDialogState;
    private CoordinatorLayout coordinatorLayout;
    public static boolean isInShowViewUser = false;
    private int firstVisibleItemPosition;
    private int firstVisibleItemPositionOffset;
    public static boolean storyViewListFetched = false;
    public int mOffset = 0;

    public void setViewUserDialogState(ViewUserDialogState viewUserDialogState) {
        this.viewUserDialogState = viewUserDialogState;
    }

    public ViewUserDialogFragment(int count, List<StoryViewInfoObject> userIdList) {
        this.count = count;
        this.userIdList = userIdList;
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
        isInShowViewUser = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avatarHandler = new AvatarHandler();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_USER_INFO, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_VIEWS_FETCHED, this);
        isInShowViewUser = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInShowViewUser = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isInShowViewUser = true;
        if (tfMain == null)
            tfMain = ResourcesCompat.getFont(getContext(), R.font.main_font);

        rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getColor(Theme.key_window_background));


        dataRootView = new LinearLayout(getContext());
        dataRootView.setOrientation(LinearLayout.VERTICAL);
        rootView.addView(dataRootView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        HelperToolbar helperToolbar = HelperToolbar.create();
        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setDefaultTitle(G.selectedLanguage.equals("fa") ? getString(R.string.story_viewed_by) + " " + HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(count)) + " " + getString(R.string.story_person) : getString(R.string.story_viewed_by) + " " + count)
                .getView();

        dataRootView.addView(toolBar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP, 0, 0, 0, 0));

        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataRootView.addView(recyclerView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));

        progressBar = new ProgressBar(getContext());
        progressBar.setVisibility(View.GONE);
        rootView.addView(progressBar, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER | Gravity.BOTTOM));

        listAdapter = new ListAdapter();
        listAdapter.setContext(getContext());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_USER_INFO, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_VIEWS_FETCHED, this);
        userId = new ArrayList<>();
        storyIdList = new ArrayList<>();
        createdAtList = new ArrayList<>();
        displayNameList = new ArrayList<>();

        Collections.sort(userIdList);

        for (int i = 0; i < userIdList.size(); i++) {
            userId.add(userIdList.get(i).userId);
            storyIdList.add(userIdList.get(i).id);
            createdAtList.add(userIdList.get(i).createdTime);
            displayNameList.add(userIdList.get(i).displayName);
        }
        mOffset = userId.size();
        this.storyId = userIdList.get(0).id;
        G.onUserInfoResponse = new OnUserInfoResponse() {

            @Override
            public void onUserInfo(ProtoGlobal.RegisteredUser user, String identity) {

            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
        recyclerView.setAdapter(listAdapter);
        listAdapter.addRow();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int iPage, int totalItemsCount, RecyclerView view) {
                Log.e("fkldsjfklsjjf", "onLoadMore: ");
                if (!storyViewListFetched && mOffset > 0 && userIdList.size() < count) {
                    progressBar.setVisibility(View.VISIBLE);
                    AbstractObject req = null;
                    IG_RPC.Story_Get_Own_Story_Views story_get_own_story_views = new IG_RPC.Story_Get_Own_Story_Views();
                    story_get_own_story_views.offset = mOffset;
                    story_get_own_story_views.limit = 50;
                    req = story_get_own_story_views;
                    MessageDataStorage.getInstance(AccountManager.selectedAccount).getRequestManager().sendRequest(req, (response, error) -> {
                        if (error == null) {
                            IG_RPC.Res_Story_Get_Own_Story_Views res = (IG_RPC.Res_Story_Get_Own_Story_Views) response;
                            mOffset += 50;
                            if (res.groupedViews.size() > 0) {
                                MessageDataStorage.getInstance(AccountManager.selectedAccount).updateOwnViews(res.groupedViews);
                            } else if (userIdList.size() >= count) {
                                storyViewListFetched = true;
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }


                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            private boolean scrollUpdated;
//            private boolean scrollingManually;
//            private int prevTop;
//            private int prevPosition;
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                scrollingManually = newState == RecyclerView.SCROLL_STATE_DRAGGING;
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                View view = layoutManager.getChildAt(0);
//                if (firstVisibleItemPosition > 0 && view != null) {
//                    firstVisibleItemPositionOffset = view.getTop();
//                }
//
//                if (!storyListFetched) {
//                    if (mOffset > 0) {
//                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//                        if (lastVisiblePosition + 10 >= mOffset) {
//                            AbstractObject req = null;
//                            IG_RPC.Story_Get_Own_Story_Views story_get_own_story_views = new IG_RPC.Story_Get_Own_Story_Views();
//                            story_get_own_story_views.offset = mOffset;
//                            story_get_own_story_views.limit = 50;
//                            req = story_get_own_story_views;
//                            MessageDataStorage.getInstance(AccountManager.selectedAccount).getRequestManager().sendRequest(req, (response, error) -> {
//                                if (error == null) {
//                                    IG_RPC.Res_Story_Get_Own_Story_Views res = (IG_RPC.Res_Story_Get_Own_Story_Views) response;
//                                    mOffset += res.groupedViews.size();
//                                    MessageDataStorage.getInstance(AccountManager.selectedAccount).updateOwnViews(res.groupedViews);
//
//                                } else {
//                                }
//                            });
//                        }
//                    }
//                }
//
//                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//                if (firstVisibleItem != RecyclerView.NO_POSITION) {
//                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
//                    if (holder != null && holder.getAdapterPosition() != 0) {
//                        int firstViewTop = holder.itemView.getTop();
//                        boolean goingDown;
//                        boolean changed = true;
//                        if (prevPosition == firstVisibleItem) {
//                            final int topDelta = prevTop - firstViewTop;
//                            goingDown = firstViewTop < prevTop;
//                            changed = Math.abs(topDelta) > 1;
//                        } else {
//                            goingDown = firstVisibleItem > prevPosition;
//                        }
//                        prevPosition = firstVisibleItem;
//                        prevTop = firstViewTop;
//                        scrollUpdated = true;
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        Log.e("fdksdhfksdfhs", "onCancel: ");
        viewUserDialogState.onCancel();
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.STORY_USER_INFO) {
            userIdList = MessageDataStorage.getInstance(AccountManager.selectedAccount).getCurrentUserStoryById(userIdList.get(0).id).storyViewInfoObjects;
            userId = new ArrayList<>();
            createdAtList = new ArrayList<>();
            displayNameList = new ArrayList<>();
            for (int i = 0; i < userIdList.size(); i++) {
                userId.add(userIdList.get(i).userId);
                createdAtList.add(userIdList.get(i).createdTime);
                displayNameList.add(userIdList.get(i).displayName);
            }
            G.runOnUiThread(() -> {
                listAdapter.addRow();
            });

        } else if (id == EventManager.STORY_VIEWS_FETCHED) {
            userIdList = MessageDataStorage.getInstance(AccountManager.selectedAccount).getCurrentUserStoryById(userIdList.get(0).id).storyViewInfoObjects;
            userId = new ArrayList<>();
            createdAtList = new ArrayList<>();
            displayNameList = new ArrayList<>();
            for (int i = 0; i < userIdList.size(); i++) {
                userId.add(userIdList.get(i).userId);
                createdAtList.add(userIdList.get(i).createdTime);
                displayNameList.add(userIdList.get(i).displayName);
            }
            G.runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                listAdapter.addRow();
            });
        }

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void deleteStory(StoryCell storyCell, long storyId, long roomId, boolean isRoom) {

    }

    @Override
    public void onStoryClick(StoryCell storyCell) {

    }

    @Override
    public void onStoryLongClick(StoryCell storyCell) {

    }

    public interface ViewUserDialogState {
        void onCancel();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        Context context;

        public void addRow() {
            rowSize = 0;

            for (int i = 0; i < userIdList.size(); i++) {
                userRow = rowSize++;
            }
            notifyDataSetChanged();
        }

        public void addNewRow() {
            rowSize = 0;
            for (int i = 0; i < 50; i++) {
                userRow = rowSize++;
            }
            notifyDataSetChanged();
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            cellView = new StoryCell(parent.getContext());
            return new RecyclerListView.Holder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StoryCell storyCell = (StoryCell) holder.itemView;
            storyCell.setDeleteStory(ViewUserDialogFragment.this);
            storyCell.initView(context, (position + 1) != userIdList.size(), StoryCell.CircleStatus.CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null, 0);
            storyCell.setStatus(StoryCell.CircleStatus.CIRCLE_IMAGE);
            if (position < displayNameList.size()) {
                storyCell.setText(displayNameList.get(position) != null ? displayNameList.get(position) : "", LastSeenTimeUtil.computeTime(context, userId.get(position), createdAtList.get(position), false, false));
                storyCell.setUserColorId("#4aca69", displayNameList.get(position) != null ? displayNameList.get(position) : "");
                storyCell.setImage(avatarHandler, userId.get(position));
            }

            storyCell.addIconVisibility(false);
            storyCell.deleteIconVisibility(false);

        }

        @Override
        public int getItemCount() {
            return rowSize;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType != 2;
        }
    }
}
