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

import net.iGap.R;
import net.iGap.adapter.items.cells.EmptyCell;
import net.iGap.adapter.items.cells.ToggleButtonCell;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.story.liststories.cells.CheckBoxCell;
import net.iGap.story.liststories.cells.TextCell;
import net.iGap.story.liststories.cells.HeaderCell;

public class StorySettingFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener {

    private RecyclerListView recyclerListView;

    public static StorySettingFragment getIncense() {
        return new StorySettingFragment();
    }

    public StorySettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();

        String title = getResources().getString(R.string.stories_setting);

        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.stories_setting))
                .setDefaultTitle(title)
                .getView();

        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));
        rootView.addView(recyclerListView = new RecyclerListView(getContext()), LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerListView.setAdapter(new ListAdapter());
        recyclerListView.setClipToPadding(false);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(30));

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
    }

    private class ListAdapter extends RecyclerListView.ItemAdapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            switch (viewType) {
                case 0:
                    HeaderCell headerCell = new HeaderCell(context);
                    headerCell.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    cellView = headerCell;
                    break;
                case 1:
                    TextCell textCell = new TextCell(parent.getContext());
                    textCell.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 24));
                    textCell.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    cellView = textCell;
                    break;
                case 2:
                    ToggleButtonCell toggleButtonCell = new ToggleButtonCell(context, false);
                    toggleButtonCell.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
                    toggleButtonCell.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    cellView = toggleButtonCell;
                    break;
                case 3:
                    CheckBoxCell checkBoxCell = new CheckBoxCell(context, false);
                    checkBoxCell.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    checkBoxCell.setChecked(true);
                    cellView = checkBoxCell;
                    break;
                case 4:
                    EmptyCell emptyCell = new EmptyCell(context);
                    cellView = emptyCell;
                    break;
                default:
                    cellView = new View(parent.getContext());
                    break;
            }
            return new RecyclerListView.ItemViewHolder(cellView, StorySettingFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            if (viewType == 0) {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                switch (position) {
                    case 0:
                        headerCell.setText(R.string.hide_story_from);
                        break;
                    case 4:
                        headerCell.setText(R.string.story_replies);
                        break;
                    case 10:
                        headerCell.setText(R.string.story_saving);
                        break;
                    case 13:
                        headerCell.setText(R.string.story_sharing);
                        break;
                }
            } else if (viewType == 1) {
                TextCell textCell = (TextCell) holder.itemView;
                textCell.setTextSize(10);
                textCell.setTextColor(Theme.getInstance().getSubTitleColor(context));
                switch (position) {
                    case 1:
                        textCell.setValue("2  نفر");
                        textCell.setTextSize(14);
                        textCell.setTextColor(Theme.getInstance().getTitleTextColor(context));
                        break;
                    case 2:
                        textCell.setValue(getString(R.string.hide_specific_people));
                        break;
                    case 8:
                        textCell.setValue(getString(R.string.react_story));
                        break;
                    case 15:
                        textCell.setValue(getString(R.string.people_feed_post));
                        break;
                    case 16:
                        textCell.setValue(getString(R.string.show_username));
                        break;
                    case 18:
                        textCell.setValue(getString(R.string.allow_share_story));
                        break;
                }
            } else if (viewType == 2) {
                ToggleButtonCell toggleButtonCell = (ToggleButtonCell) holder.itemView;
                switch (position) {
                    case 11:
                        toggleButtonCell.setText(getString(R.string.save_gallery), false);
                        break;
                    case 14:
                        toggleButtonCell.setText(getString(R.string.resharing_story), false);
                        break;
                    case 17:
                        toggleButtonCell.setText(getString(R.string.allow_sharing), false);
                        break;
                }
            } else if (viewType == 3) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) holder.itemView;
                switch (position) {
                    case 5:
                        checkBoxCell.setText(getString(R.string.everyone));
                        break;
                    case 6:
                        checkBoxCell.setText(getString(R.string.follower));
                        break;
                    case 7:
                        checkBoxCell.setText(getString(R.string.off));
                        break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return 19;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == 4 || position == 10 || position == 13)
                return 0;
            else if (position == 1 || position == 2 || position == 8 || position == 15 || position == 16 || position == 18){
                return  1;
            }
            else if (position == 11 || position == 14 || position == 17){
                return  2;
            }
            else if(position == 5 || position == 6 || position == 7){
                return  3;
            }
            else{
                return 4;
            }
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return true;
        }
    }
}
