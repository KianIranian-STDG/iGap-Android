package net.iGap.adapter.items.popularChannel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.model.popularChannel.Category;

import java.util.List;

public class NormalCategoryAdapter extends RecyclerView.Adapter<NormalCategoryAdapter.FragmentGridViewHolder> {
    private List<Category> categoryList;
    private OnClickedItemEventCallBack onClickedItemEventCallBack;

    public NormalCategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setOnClickedItemEventCallBack(OnClickedItemEventCallBack onClickedItemEventCallBack) {
        this.onClickedItemEventCallBack = onClickedItemEventCallBack;
    }

    @NonNull
    @Override
    public FragmentGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_channel_category, viewGroup, false);
        return new FragmentGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentGridViewHolder holder, int i) {
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

        public FragmentGridViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImageGrid = itemView.findViewById(R.id.iv_item_popular_rv_grid);
            channelTitleGrid = itemView.findViewById(R.id.tv_item_popular_rv_grid);
        }

        public void bindChannel(Category category) {
            ImageLoadingService.load(category.getIcon(), channelImageGrid);
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
