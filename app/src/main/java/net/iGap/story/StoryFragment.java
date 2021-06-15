package net.iGap.story;

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
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.ToolbarListener;

public class StoryFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener {

    private RecyclerListView recyclerListView;

    public static StoryFragment getIncense() {
        return new StoryFragment();
    }

    public StoryFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();

        String title =  getResources().getString(R.string.stories);

        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setRightIcons(R.string.check_icon)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.new_channel))
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
    public void onRightIconClickListener(View view) { }

    @Override
    public void onClick(View view, int position) { }

    private class ListAdapter extends RecyclerListView.ItemAdapter {
        private int recentListNumber = 3;
        private int mutedListNumber = 4;
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView = new View(parent.getContext());
            return new RecyclerListView.ItemViewHolder(cellView, StoryFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
        }

        @Override
        public int getItemCount() {
            return 3 + recentListNumber + mutedListNumber;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            else if (position == 1)
                return 1;
            else if (position == recentListNumber + 2)
                return  2;
            else
                return  3;
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return viewType != 1;
        }
    }
}
