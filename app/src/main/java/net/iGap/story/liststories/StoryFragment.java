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

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentSetting;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.AttachFile;
import net.iGap.module.Theme;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.dialog.ChatAttachmentPopup;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.story.PhotoViewer;
import net.iGap.story.StoryPagerFragment;
import net.iGap.story.liststories.cells.AddStoryCell;
import net.iGap.story.liststories.cells.HeaderCell;
import net.iGap.story.liststories.cells.StoryCell;
import net.iGap.story.viewPager.StoryViewFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class StoryFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener {

    private RecyclerListView recyclerListView;

    public static StoryFragment getIncense() {
        return new StoryFragment();
    }

    public StoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();

        String title = getResources().getString(R.string.stories);
        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.stories))
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
        if (position == 0) {
            new HelperFragment(getActivity().getSupportFragmentManager(), new StoryPagerFragment()).setReplace(false).load();
        }else {
            AttachFile.getAllShownImagesPath(getActivity(), 3, new ChatAttachmentPopup.OnImagesGalleryPrepared() {
                @Override
                public void imagesList(ArrayList<StructBottomSheet> listOfAllImages) {
                    G.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StoryViewFragment storyViewFragment = StoryViewFragment.newInstance(listOfAllImages);
                            storyViewFragment.setItemGalleryList(listOfAllImages);
                            new HelperFragment(getActivity().getSupportFragmentManager(), storyViewFragment).setReplace(true).load();
                        }
                    });
                }
            });
        }
    }

    private class ListAdapter extends RecyclerListView.ItemAdapter {
        private int recentListNumber = 3;
        private int mutedListNumber = 4;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            switch (viewType) {
                case 0:
                    AddStoryCell addStoryCell = new AddStoryCell(context);
                    addStoryCell.setBackgroundColor(Color.WHITE);
                    cellView = addStoryCell;
                    break;
                case 1:
                    HeaderCell headerCell = new HeaderCell(context);
                    cellView = headerCell;
                    break;
                case 2:
                    StoryCell storyCell = new StoryCell(context);
                    storyCell.setBackgroundColor(Color.WHITE);
                    cellView = storyCell;
                    break;
                default:
                    cellView = new View(parent.getContext());
                    break;
            }
            return new RecyclerListView.ItemViewHolder(cellView, StoryFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            if (viewType == 1) {
                HeaderCell headerCell = (HeaderCell) holder.itemView;
                if (position == 1)
                    headerCell.setText("به روز رسانی های اخیر");
                else
                    headerCell.setText("به روزرسانی های بی صدا");
            }
        }

        @Override
        public int getItemCount() {
            return 3 + recentListNumber + mutedListNumber;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            else if (position == 1 || position == recentListNumber + 2)
                return 1;
            else
                return 2;
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return viewType != 1;
        }


    }
}
