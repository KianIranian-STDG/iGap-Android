package net.iGap.story.liststories;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import net.iGap.fragments.FragmentWalletAgrement;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperWallet;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.upload.ApiBased.HttpUploader;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.upload.Uploader;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmUserInfo;
import net.iGap.story.StatusTextFragment;
import net.iGap.story.StoryObject;
import net.iGap.story.StoryPagerFragment;
import net.iGap.story.liststories.cells.HeaderCell;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.story.viewPager.StoryViewFragment;
import net.iGap.structs.MessageObject;

import org.paygear.WalletActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.iGap.G.isAppRtl;
import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;

public class MyStatusStoryListFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener, StoryCell.DeleteStory, EventManager.EventDelegate {
    private RecyclerListView recyclerListView;
    private ListAdapter adapter;
    List<StoryObject> storyProto;
    private FrameLayout floatActionLayout;
    private List<List<String>> displayNameList;
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
    private int recentStoryRow;
    private final int qrWalletTag = 1;


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
    }

    private void onScannerClickListener() {
        DbManager.getInstance().doRealmTask(realm -> {
            String phoneNumber = "";
            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
            try {
                if (userInfo != null) {
                    phoneNumber = userInfo.getUserInfo().getPhoneNumber().substring(2);
                } else {
                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
                }
            } catch (Exception e) {
                //maybe exception was for realm substring
                try {
                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
                } catch (Exception ex) {
                    //nothing
                }
            }

            if (userInfo == null || !userInfo.isWalletRegister()) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber)).load();
            } else {
                getActivity().startActivityForResult(new HelperWallet().goToWallet(getContext(), new Intent(getActivity(), WalletActivity.class), "0" + phoneNumber, true), WALLET_REQUEST_CODE);
            }

        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        Toolbar myStoryToolbar = new Toolbar(getContext());
        myStoryToolbar.setTitle(getString(R.string.my_status));
        myStoryToolbar.setBackIcon(new BackDrawable(false));
        ToolbarItems toolbarItems = myStoryToolbar.createToolbarItems();
        toolbarItems.addItemWithWidth(qrWalletTag, R.string.icon_QR_code, 54);

        myStoryToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    requireActivity().onBackPressed();
                    break;
                case qrWalletTag:
                    onScannerClickListener();
                    break;
            }
        });


        FrameLayout rootView = new FrameLayout(new ContextThemeWrapper(context, R.style.IGapRootViewStyle));
        rootView.addView(myStoryToolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));

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
        addButton.setIcon(R.string.icon_add);
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
        progressBar.setVisibility(View.VISIBLE);
        displayNameList = new ArrayList<>();
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
                G.runOnUiThread(() -> loadStories());

            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

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

    private void loadStories() {

        getMessageDataStorage().deleteExpiredStories();
        storyProto = getMessageDataStorage().getCurrentUserStories();

        if (storyProto != null && storyProto.size() == 0) {
            getMessageDataStorage().deleteStoryByUserId(AccountManager.getInstance().getCurrentUser().getId());
            storyProto = getMessageDataStorage().getCurrentUserStories();
        }


        List<Long> userIdList = new ArrayList<>();
        userIdList.add(AccountManager.getInstance().getCurrentUser().getId());
        displayNameList = getMessageDataStorage().getDisplayNameWithUserId(userIdList);
        if (storyProto != null && storyProto.size() > 0) {
            progressBar.setVisibility(View.GONE);
            adapter = new ListAdapter();
            recyclerListView.setAdapter(adapter);
            recyclerListView.setVisibility(View.VISIBLE);
            adapter.addRow();
        } else {
            progressBar.setVisibility(View.GONE);

            if (getActivity() != null) {
                getActivity().onBackPressed();
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
    public void onClick(View view, int position) {
        StoryCell storyCell = (StoryCell) view;
        new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment(storyCell.getUserId(), true, true, storyCell.getStoryId())).setReplace(false).load();
    }

    @Override
    public void onLongClick(View itemView, int position) {
        StoryCell storyCell = (StoryCell) itemView;


        if (storyCell.getSendStatus() == MessageObject.STATUS_FAILED ||
                storyCell.getSendStatus() == MessageObject.STATUS_SENT) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.delete_status_update))
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
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void deleteStory(long storyId) {

        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.delete_status_update))
                .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                .positiveText(R.string.ok)
                .onNegative((dialog1, which) -> dialog1.dismiss()).show();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            recyclerListView.setVisibility(View.GONE);
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
            dialog.dismiss();
        });


    }

    @Override
    public void onStoryClick(StoryCell storyCell) {
        new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment(storyCell.getUserId(), true, true, storyCell.getStoryId() != 0 ? storyCell.getStoryId() : storyCell.getStoryIndex())).setReplace(false).load();
    }


    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.STORY_LIST_FETCHED || id == EventManager.STORY_USER_ADD_NEW ||
                id == EventManager.STORY_USER_ADD_VIEW || id == EventManager.STORY_DELETED ||
                id == EventManager.STORY_UPLOADED_FAILED || id == EventManager.STORY_USER_INFO) {
            G.runOnUiThread(() -> {
                actionButtonsRootView.setVisibility(View.VISIBLE);
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
            });
        }
    }


    private class ListAdapter extends RecyclerListView.ItemAdapter {

        public void addRow() {
            rowSize = 0;
            if (storyProto != null) {
                for (int i = rowSize; i < storyProto.size(); i++) {
                    recentStoryRow = rowSize++;
                }
                recentHeaderRow = rowSize++;
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
                    cellView = new HeaderCell(context);
                    break;
                default:
                    cellView = new View(parent.getContext());
            }
            return new RecyclerListView.ItemViewHolder(cellView, MyStatusStoryListFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    if (position <= recentStoryRow) {
                        StoryCell storyCell = (StoryCell) holder.itemView;

                        if (position < storyProto.size()) {

                            if (storyProto.get(position).status == MessageObject.STATUS_FAILED) {
                                actionButtonsRootView.setVisibility(View.VISIBLE);
                                storyCell.setData(storyProto.get(position), displayNameList.get(0).get(0), displayNameList.get(0).get(1), context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                            } else if (storyProto.get(position).status == MessageObject.STATUS_SENDING) {
                                if (!Uploader.getInstance().isCompressingOrUploading(String.valueOf(storyProto.get(position).id)) && !MessageController.isSendingStory && !HttpUploader.isStoryUploading) {
                                    actionButtonsRootView.setVisibility(View.VISIBLE);
                                    long failedStoryId = storyProto.get(position).id;


                                    getMessageDataStorage().updateStoryStatus(failedStoryId, MessageObject.STATUS_FAILED);

                                    storyCell.setData(storyProto.get(position), displayNameList.get(0).get(0), displayNameList.get(0).get(1), context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.FAILED, null);
                                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                                } else {
                                    actionButtonsRootView.setVisibility(View.GONE);
                                    storyCell.setData(storyProto.get(position), displayNameList.get(0).get(0), displayNameList.get(0).get(1), context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null);
                                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);

                                }

                            } else {
                                storyCell.setData(storyProto.get(position), displayNameList.get(0).get(0), displayNameList.get(0).get(1), context, (position + 1) != storyProto.size(), StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE, ImageLoadingView.Status.CLICKED, null);
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                                storyCell.deleteIconVisibility(true, R.string.icon_delete);
                            }

                            storyCell.setDeleteStory(MyStatusStoryListFragment.this);

                            storyCell.setStatus(StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE);
                            storyCell.setStoryId(storyProto.get(position).storyId);
                            storyCell.setUploadId(storyProto.get(position).id);
                            storyCell.setFileToken(storyProto.get(position).fileToken);
                            storyCell.setSendStatus(storyProto.get(position).status);
                            storyCell.setStoryIndex(storyProto.get(position).index);
                        }

                        storyCell.addIconVisibility(false);
                    }
                    break;
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setTextColor(Theme.getInstance().getSendMessageTextColor(headerCell.getContext()));
                    headerCell.setGravity(Gravity.CENTER);
                    headerCell.setTextSize(12);
                    if (position == recentHeaderRow) {
                        headerCell.setText(getString(R.string.your_status_updates_will_disappear_after_24_hours));
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
            if ((position <= recentStoryRow)) {
                return 0;
            } else if (position == recentHeaderRow) {
                return 1;
            }
            return super.getItemViewType(position);
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return viewType != 2;
        }
    }
}
