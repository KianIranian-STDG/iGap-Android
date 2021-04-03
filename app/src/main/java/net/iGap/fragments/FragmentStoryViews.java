package net.iGap.fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.items.BottomSheetStory;
import net.iGap.adapter.items.cells.StoryUserCell;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.module.customView.RecyclerListView;

public class FragmentStoryViews extends BaseFragment implements StoryUserCell.IconClicked {

    RecyclerView recyclerListView;
    ListAdapter adapter;

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        recyclerListView = new RecyclerListView(context);
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerListView.setBackgroundColor(Color.WHITE);
        recyclerListView.setAdapter(adapter = new ListAdapter());
        frameLayout.addView(recyclerListView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        return fragmentView;
    }

    @Override
    public void clickedIcon(View icon, View icon2) {
        icon.setOnClickListener(view1 -> {
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
        });

        icon2.setOnClickListener(view21 -> {
            BottomSheetStory addPhotoBottomDialogFragment = new BottomSheetStory();
            addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),"BottomSheetStory");
        });
    }

    public class ListAdapter extends RecyclerListView.ItemAdapter {
        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return true;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerListView.ItemViewHolder(new StoryUserCell(parent.getContext(), true,FragmentStoryViews.this::clickedIcon), null);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StoryUserCell storyUserCell = (StoryUserCell) holder.itemView;
            storyUserCell.setText("topppppppppp", "bottommmm");
            storyUserCell.setIconsValue(R.string.md_send_button, R.string.more_icon);
            storyUserCell.setImage(R.color.red);
        }

        @Override
        public int getItemCount() {
            return 15;
        }

        @Override
        public int getItemViewType(int position) {

            return super.getItemViewType(position);
        }
    }

}
