package net.iGap.story.liststories;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.network.IG_RPC;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.story.StoryPagerFragment;
import net.iGap.story.liststories.cells.HeaderCell;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.story.viewPager.StoryViewFragment;

import java.util.ArrayList;
import java.util.List;

public class StoryFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener {

    private RecyclerListView recyclerListView;
    private ListAdapter adapter;
    private int rowSize;
    private int addStoryRow;
    private int recentHeaderRow;
    private int recentStoryRow;
    private int muteHeaderRow;
    private int muteStoryRow;
    private List<Story> storyCount;

    public class Story {
//   ->     sample model for storyListSize
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
                .setDefaultTitle(getString(R.string.stories))
                .getView();


        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));
        recyclerListView = new RecyclerListView(getContext());
        adapter = new ListAdapter();
        adapter.addRow();
        recyclerListView.setAdapter(adapter);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerListView.setClipToPadding(false);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(30));
        rootView.addView(recyclerListView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        //request for get story list
        IG_RPC.InfoConfig req = new IG_RPC.InfoConfig();
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                storyCount = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Story story = new Story();
                    storyCount.add(story);
                }
                G.runOnUiThread(() -> adapter.addRow());
            } else {
                // handel error
            }

        });

        return rootView;
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onClick(View view, int position) {
        if (position == addStoryRow) {
            new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment()).setReplace(false).load();
        } else if (position > recentHeaderRow && position <= recentStoryRow || position > muteHeaderRow && position <= muteStoryRow) {
            new HelperFragment(getActivity().getSupportFragmentManager(), new StoryViewFragment()).setReplace(false).load();
        }
    }

    private class ListAdapter extends RecyclerListView.ItemAdapter {

        public void addRow() {
            rowSize = 0;
            addStoryRow = rowSize++;
            if (storyCount != null) {
                recentHeaderRow = rowSize++;
                for (int i = rowSize; i < (storyCount.size()) + 2; i++) {
                    recentStoryRow = rowSize++;
                }
                muteHeaderRow = rowSize++;
                for (int i = rowSize; i < storyCount.size() + storyCount.size() + 3; i++) {
                    muteStoryRow = rowSize++;
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
                    cellView = new StoryCell(context, true, StoryCell.Status.CIRCLE_IMAGE);
                    break;
                case 1:
                    cellView = new StoryCell(context, true, StoryCell.Status.LOADING_CIRCLE_IMAGE);
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
                    storyCell.setBackgroundColor(Color.WHITE);
                    storyCell.setTextColor(Color.BLACK, Color.GRAY);
                    if (position == addStoryRow) {
                        storyCell.setText("My status", "Today  00:00");
                        storyCell.setImage(R.drawable.avatar);
                        storyCell.addIconVisibility(true);
                    } else if (((recentHeaderRow < position) && (position <= recentStoryRow)) || ((position > muteHeaderRow) && (position <= muteStoryRow))) {
                        storyCell.setText("Nazanin", "Today  00:00");
                        storyCell.setImage(R.drawable.image_igasht_province);
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setTextColor(getResources().getColor(R.color.ou_background_crop));
                    if (position == recentHeaderRow) {
                        headerCell.setText("Viewed updates");
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
            } else if (((recentHeaderRow < position) && (position <= recentStoryRow)) || ((muteHeaderRow < position) && (position <= muteStoryRow))) {
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
