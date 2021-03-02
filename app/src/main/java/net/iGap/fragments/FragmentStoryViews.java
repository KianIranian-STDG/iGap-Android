package net.iGap.fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.items.cells.StoryUserCell;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.customView.RecyclerListView;


public class FragmentStoryViews extends BaseFragment implements RecyclerListView.OnItemClickListener {

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
    public void onClick(View view, int position) {

    }

    public class ListAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = new StoryUserCell(parent.getContext());
            return new RecyclerListView.ItemViewHolder(view, FragmentStoryViews.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StoryUserCell storyUserCell = (StoryUserCell) holder.itemView;
/*            storyUserCell.setText("nazanin-om92","llllllllllllll",true);
            storyUserCell.setIcons(R.string.back_icon,R.string.back_icon);
            storyUserCell.setTextColor(R.color.red,R.color.red);*/

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

}
