package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.model.PopularChannel.Category;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCategoryItem extends RecyclerView.Adapter<AdapterCategoryItem.FragmentGridViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private OnClickedItemEventCallBack onClickedItemEventCallBack;
    public boolean clickable;

    public AdapterCategoryItem(Context context, boolean clickable, List<Category> categoryList) {
        this.context = context;
        this.clickable = clickable;
        this.categoryList = categoryList;

    }

    @NonNull
    @Override
    public FragmentGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_category, viewGroup, false);
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

    public class FragmentGridViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView channelImageGrid;
        private TextView channelTitleGrid;

        public FragmentGridViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImageGrid = itemView.findViewById(R.id.circle_item_popular_rv_grid);
            channelTitleGrid = itemView.findViewById(R.id.tv_item_popular_rv_grid);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickable)
                        onClickedItemEventCallBack.onClickedItem();
                }
            });
        }

        public void bindChannel(Category category) {
            ImageLoadingService.load(category.getIcon(), channelImageGrid);
            channelTitleGrid.setText(category.getTitle());
        }
    }

    public void setOnClickedItemEventCallBack(OnClickedItemEventCallBack onClickedItemEventCallBack) {
        this.onClickedItemEventCallBack = onClickedItemEventCallBack;
    }

    public interface OnClickedItemEventCallBack {
        void onClickedItem();
    }
}
