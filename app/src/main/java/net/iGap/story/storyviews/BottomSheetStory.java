package net.iGap.story.storyviews;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.dialog.BaseBottomSheet;

public class BottomSheetStory extends BaseBottomSheet {

    private RecyclerListView listView;
    private Adapter adapter;

    private int rowSize;
    private int blockRow;
    private int removeFollowerRow;
    private int HideStoryRow;
    private int ViewProfileRow;

    private int padding = 16;
    private View lineView;
    private View lineView2;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FrameLayout frameLayout = new FrameLayout(getContext());

        lineView = new View(getContext());
        lineView.setBackgroundColor(Color.GRAY);
        lineView.setBackground(getContext().getResources().getDrawable(R.drawable.bottom_sheet_dialog_line_dark));
        frameLayout.addView(lineView, LayoutCreator.createFrame(30, 3, Gravity.TOP | Gravity.CENTER_HORIZONTAL, padding, padding, padding, padding));

        textView = new TextView(getContext());
        textView.setText("Nazanin_Omrani");
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        frameLayout.addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, padding, padding * 2, padding, padding));

        lineView2 = new View(getContext());
        lineView2.setBackgroundColor(Color.GRAY);
        frameLayout.addView(lineView2, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 1, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, padding * 4, 0, padding));

        listView = new RecyclerListView(getContext());
        listView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        listView.setAdapter(adapter = new Adapter());
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, padding, padding * 4, padding, padding));

        adapter.setRow();
        return frameLayout;
    }

    public class Adapter extends RecyclerListView.ItemAdapter {

        public void setRow() {
            rowSize = 0;
            blockRow = rowSize++;
            removeFollowerRow = rowSize++;
            HideStoryRow = rowSize++;
            ViewProfileRow = rowSize++;
            notifyDataSetChanged();
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return true;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            TextCell textCell = (TextCell) holder.itemView;
            switch (position) {
                case 0:
                    textCell.setText(getContext().getResources().getString(R.string.block));
                    textCell.setTextColor(getContext().getResources().getColor(R.color.red));
                    break;
                case 1:
                    textCell.setText(getContext().getResources().getString(R.string.remove_follower));
                    break;
                case 2:
                    textCell.setText(getContext().getResources().getString(R.string.hide_story));
                    break;
                case 3:
                    textCell.setText(getContext().getResources().getString(R.string.view_profile));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return rowSize;
        }
    }
}
