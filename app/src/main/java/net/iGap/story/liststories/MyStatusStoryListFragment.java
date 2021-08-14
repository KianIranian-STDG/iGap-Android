package net.iGap.story.liststories;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.downloader.HttpRequest;
import net.iGap.module.upload.Uploader;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoStoryGetStories;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.story.StoryPagerFragment;
import net.iGap.story.liststories.cells.HeaderCell;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.story.viewPager.StoryViewFragment;
import net.iGap.structs.MessageObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Sort;

import static android.content.Context.EUICC_SERVICE;
import static net.iGap.G.bothChatDeleteTime;
import static net.iGap.G.isAppRtl;

public class MyStatusStoryListFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener, StoryCell.DeleteStory, EventManager.EventDelegate {
    private RecyclerListView recyclerListView;
    private ListAdapter adapter;
    List<RealmStoryProto> storyProto;
    private FrameLayout floatActionLayout;
    private List<List<String>> displayNameList;
    public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
    private ProgressBar progressBar;
    int counter = 0;
    private int rowSize;
    private int recentHeaderRow;
    private int recentStoryRow;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_LIST_FETCHED, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_USER_ADD_NEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_USER_ADD_VIEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_DELETED, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_UPLOADED_NEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.STORY_UPLOADED_FAILED, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();
        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.my_status))
                .getView();


        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));
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


        floatActionLayout = new FrameLayout(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(LayoutCreator.dp(56), Theme.getInstance().getToolbarBackgroundColor(context), Theme.getInstance().getAccentColor(context));
        floatActionLayout.setBackground(drawable);
        IconView addButton = new IconView(context);
        addButton.setIcon(R.string.add_icon_without_circle_font);
        addButton.setIconColor(Color.WHITE);
        floatActionLayout.addView(addButton);
        rootView.addView(floatActionLayout, LayoutCreator.createFrame(52, 52, (isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, 16, 0, 16, 16));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_LIST_FETCHED, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_USER_ADD_NEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_USER_ADD_VIEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_DELETED, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_UPLOADED_NEW, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STORY_UPLOADED_FAILED, this);
        progressBar.setVisibility(View.VISIBLE);
        displayNameList = new ArrayList<>();
        loadStories();
        AbstractObject req = null;
        IG_RPC.Story_Get_Own_Story_Views story_get_own_story_views = new IG_RPC.Story_Get_Own_Story_Views();
        story_get_own_story_views.offset = 0;
        story_get_own_story_views.limit = storyProto.size();
        req = story_get_own_story_views;
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Story_Get_Own_Story_Views res = (IG_RPC.Res_Story_Get_Own_Story_Views) response;
                DbManager.getInstance().doRealmTransaction(realm -> {
                    int counter = 0;
                    for (int i = 0; i < res.groupedViews.size(); i++) {
                        for (int j = 0; j < res.groupedViews.get(i).getUserIdsList().size(); j++) {
                            if (res.groupedViews.get(i).getUserIdsList().get(j) != AccountManager.getInstance().getCurrentUser().getId()) {
                                counter++;
                            }
                        }
                        realm.where(RealmStoryProto.class).equalTo("storyId", res.groupedViews.get(i).getStoryId()).findFirst().setViewCount(counter);
                        counter = 0;
                    }


                });

                G.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadStories();
                    }
                });

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

    }

    private void loadStories() {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.where(RealmStory.class).lessThan("realmStoryProtos.createdAt", System.currentTimeMillis() - MILLIS_PER_DAY).findAll().deleteAllFromRealm();
            storyProto = realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findAll().sort("storyId", Sort.DESCENDING);
        });
        if (storyProto != null && storyProto.size() == 0) {
            DbManager.getInstance().doRealmTransaction(realm -> {
                realm.where(RealmStory.class).equalTo("id", AccountManager.getInstance().getCurrentUser().getId()).findAll().deleteAllFromRealm();
                storyProto = realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findAll().sort("storyId", Sort.DESCENDING);
            });
        }
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(AccountManager.getInstance().getCurrentUser().getId());
        displayNameList = getMessageDataStorage().getDisplayNameWithUserId(userIdList);
        if (storyProto != null && storyProto.size() > 0) {
            progressBar.setVisibility(View.GONE);
            recyclerListView.setVisibility(View.VISIBLE);
            adapter.addRow();
        } else {
            progressBar.setVisibility(View.GONE);

            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }


    }

    @Override
    public void onClick(View view, int position) {
        StoryCell storyCell = (StoryCell) view;
        new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment(storyCell.getUserId(), true, true, storyCell.getStoryId())).setReplace(false).load();
    }

    @Override
    public void onLongClick(View itemView, int position) {
        StoryCell storyCell = (StoryCell) itemView;
        boolean isAbleToSendToServer = false;
        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.delete_status_update))
                .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                .positiveText(R.string.ok)
                .onNegative((dialog1, which) -> dialog1.dismiss()).show();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            recyclerListView.setVisibility(View.GONE);
            DbManager.getInstance().doRealmTransaction(realm -> {
                if (realm.where(RealmStoryProto.class).equalTo("id", storyCell.getUploadId()).findFirst().getStatus() == MessageObject.STATUS_FAILED) {
                    realm.where(RealmStoryProto.class).equalTo("id", storyCell.getUploadId()).findFirst().deleteFromRealm();
                } else {
                    AbstractObject req = null;
                    IG_RPC.Story_Delete story_delete = new IG_RPC.Story_Delete();
                    story_delete.storyId = String.valueOf(storyCell.getStoryId());
                    req = story_delete;
                    getRequestManager().sendRequest(req, (response, error) -> {
                        if (error == null) {
                            IG_RPC.Res_Story_Delete res = (IG_RPC.Res_Story_Delete) response;
                            DbManager.getInstance().doRealmTransaction(realmDB -> {
                                realmDB.where(RealmStory.class).lessThan("realmStoryProtos.createdAt", System.currentTimeMillis() - MILLIS_PER_DAY).findAll().deleteAllFromRealm();
                                realmDB.where((RealmStoryProto.class)).equalTo("storyId", Long.valueOf(res.storyId)).findAll().deleteAllFromRealm();
                                storyProto = realmDB.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findAll().sort("storyId", Sort.DESCENDING);
                            });
                            G.refreshRealmUi();


                        } else {

                        }
                    });
                }

                G.runOnUiThread(() -> {
                    loadStories();
                    EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_DELETED);
                });
            });

            dialog.dismiss();
        });


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
            story_delete.storyId = String.valueOf(storyId);
            req = story_delete;
            getRequestManager().sendRequest(req, (response, error) -> {
                if (error == null) {
                    IG_RPC.Res_Story_Delete res = (IG_RPC.Res_Story_Delete) response;
                    DbManager.getInstance().doRealmTransaction(realm -> {
                        realm.where(RealmStory.class).lessThan("realmStoryProtos.createdAt", System.currentTimeMillis() - MILLIS_PER_DAY).findAll().deleteAllFromRealm();
                        realm.where((RealmStoryProto.class)).equalTo("storyId", Long.valueOf(res.storyId)).findAll().deleteAllFromRealm();
                        storyProto = realm.where(RealmStoryProto.class).equalTo("userId", AccountManager.getInstance().getCurrentUser().getId()).findAll().sort("storyId", Sort.DESCENDING);
                    });
                    G.refreshRealmUi();
                    G.runOnUiThread(() -> {
                        loadStories();
                        EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_DELETED);
                    });


                } else {

                }
            });
            dialog.dismiss();
        });


    }

    @Override
    public void openMyStory() {

    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.STORY_LIST_FETCHED || id == EventManager.STORY_USER_ADD_NEW || id == EventManager.STORY_USER_ADD_VIEW || id == EventManager.STORY_DELETED || id == EventManager.STORY_UPLOADED_FAILED || id == EventManager.STORY_UPLOADED_NEW) {
            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadStories();
                }
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
                    cellView = new StoryCell(context, true, StoryCell.CircleStatus.CIRCLE_IMAGE);
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
                        storyCell.setBackgroundColor(Color.WHITE);
                        storyCell.setTextColor(Color.BLACK, Color.GRAY);
                        storyCell.setDeleteStory(MyStatusStoryListFragment.this);

                        storyCell.setStatus(StoryCell.CircleStatus.LOADING_CIRCLE_IMAGE);
                        if (position < storyProto.size()) {
                            if (storyProto.get(position).getStatus() == MessageObject.STATUS_FAILED) {
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                                storyCell.setData(true, storyProto.get(position).getUserId(), storyProto.get(position).getCreatedAt(), storyProto.get(position).getViewCount(), displayNameList.get(0).get(0), displayNameList.get(0).get(1), null, storyProto.get(position).getImagePath());
                            } else if (storyProto.get(position).getStatus() == MessageObject.STATUS_SENDING) {
                                if (!Uploader.getInstance().isCompressingOrUploading(String.valueOf(storyProto.get(position).getId()))) {
                                    long failedStoryId = storyProto.get(position).getId();
                                    DbManager.getInstance().doRealmTransaction(realm -> {
                                        realm.where(RealmStoryProto.class).equalTo("id", failedStoryId).findFirst().setStatus(MessageObject.STATUS_FAILED);
                                    });
                                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                                } else {
                                    storyCell.setImageLoadingStatus(ImageLoadingView.Status.LOADING);

                                }

                                storyCell.setData(true, storyProto.get(position).getUserId(), storyProto.get(position).getCreatedAt(), storyProto.get(position).getViewCount(), displayNameList.get(0).get(0), displayNameList.get(0).get(1), null, storyProto.get(position).getImagePath());
                            } else if (storyProto.get(position).getStatus() == MessageObject.STATUS_UPLOADED) {
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.FAILED);
                                storyCell.setData(true, storyProto.get(position).getUserId(), storyProto.get(position).getCreatedAt(), storyProto.get(position).getViewCount(), displayNameList.get(0).get(0), displayNameList.get(0).get(1), null, storyProto.get(position).getImagePath());
                            } else {
                                storyCell.setImageLoadingStatus(ImageLoadingView.Status.CLICKED);
                                storyCell.setData(true, storyProto.get(position).getUserId(), storyProto.get(position).getCreatedAt(), storyProto.get(position).getViewCount(), displayNameList.get(0).get(0), displayNameList.get(0).get(1), storyProto.get(position).getFile(), null);
                                storyCell.deleteIconVisibility(true, R.string.delete_icon);
                            }

                            storyCell.setStoryId(storyProto.get(position).getStoryId());
                            storyCell.setUploadId(storyProto.get(position).getId());
                        }

                        storyCell.addIconVisibility(false);
                    }
                    break;
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setTextColor(getResources().getColor(R.color.ou_background_crop));
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
