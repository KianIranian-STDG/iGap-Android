package net.iGap.adapter.items.popularChannel;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bottomNavigation.Util.Utils;
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
        View view = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.item_favorite_channel_category, viewGroup, false);
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
        private CardView root;
        private LinearLayout linearLayout;

        public FragmentGridViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImageGrid = itemView.findViewById(R.id.iv_item_popular_rv_grid);
            channelTitleGrid = itemView.findViewById(R.id.tv_item_popular_rv_grid);

            linearLayout = itemView.findViewById(R.id.ll_item_pop_card_category);
            if (G.isDarkTheme) {
                linearLayout.setBackgroundResource(R.drawable.shape_favorite_channel_dark_item_them);
            }
            root = itemView.findViewById(R.id.card_item_pop_category);
            Utils.setCardsBackground(root, R.color.white, R.color.gray_6c);
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
