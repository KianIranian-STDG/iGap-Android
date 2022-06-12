package net.iGap.adapter.items.popularChannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.popularChannel.Category;

import java.util.List;

public class NormalCategoryAdapter extends RecyclerView.Adapter<NormalCategoryAdapter.FragmentGridViewHolder> {
    private List<Category> categoryList;
    private OnClickedItemEventCallBack onClickedItemEventCallBack;
    private Context context;

    public NormalCategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setOnClickedItemEventCallBack(OnClickedItemEventCallBack onClickedItemEventCallBack) {
        this.onClickedItemEventCallBack = onClickedItemEventCallBack;
    }

    @NonNull
    @Override
    public FragmentGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_channel_category, viewGroup, false);
        view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        return new FragmentGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentGridViewHolder holder, int i) {
        holder.bottomSheet.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.bottom_sheet_background), context, Theme.getColor(Theme.key_theme_color)));
        holder.bindChannel(categoryList.get(i));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public interface OnClickedItemEventCallBack {
        void onClickedItem(Category category);
    }

    public class FragmentGridViewHolder extends RecyclerView.ViewHolder {
        private ImageView channelImageGrid;
        private TextView channelTitleGrid;
        private LinearLayout bottomSheet;

        public FragmentGridViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImageGrid = itemView.findViewById(R.id.iv_item_popular_rv_grid);
            channelTitleGrid = itemView.findViewById(R.id.tv_item_popular_rv_grid);
            channelTitleGrid.setTextColor(Theme.getColor(Theme.key_title_text));
            bottomSheet = itemView.findViewById(R.id.bottomSheet);
        }

        public void bindChannel(Category category) {
            Glide.with(G.context)
                    .load(category.getIcon())
                    .fitCenter()
                    .centerInside()
                    .error(R.drawable.ic_error)
                    .into(channelImageGrid);

            if (G.isAppRtl)
                channelTitleGrid.setText(category.getTitle());
            else
                channelTitleGrid.setText(category.getTitleEn());

            itemView.setOnClickListener(view -> {
                if (onClickedItemEventCallBack != null)
                    onClickedItemEventCallBack.onClickedItem(category);
            });


        }
    }
}
