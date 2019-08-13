package net.iGap.fragments.favoritechannel;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.libs.bannerslider.event.OnSlideClickListener;
import net.iGap.model.FavoriteChannel.Category;
import net.iGap.model.FavoriteChannel.Channel;
import net.iGap.model.FavoriteChannel.Datum;
import net.iGap.model.FavoriteChannel.ParentChannel;
import net.iGap.model.FavoriteChannel.Slide;

import java.util.ArrayList;
import java.util.List;

public class PopularChannelHomeAdapter extends RecyclerView.Adapter {
    private static final int TYPE_SLIDE = 0;
    private static final int TYPE_CHANNEL_FEATURED_CATEGORY = 1;
    private static final int TYPE_CHANNEL_NORMAL_CATEGORY = 2;

    private List<Datum> data = new ArrayList<>();
    private OnFavoriteChannelCallBack callBack;

    public void setData(List<Datum> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setCallBack(OnFavoriteChannelCallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        RecyclerView.ViewHolder viewHolder;

        switch (type) {
            case TYPE_SLIDE:
                View sliderViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_slide, viewGroup, false);
                viewHolder = new SliderViewHolder(sliderViewHolder);
                break;
            case TYPE_CHANNEL_FEATURED_CATEGORY:
                View channelViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_channel_channelcountainer, viewGroup, false);
                viewHolder = new ChannelViewHolder(channelViewHolder);
                break;
            case TYPE_CHANNEL_NORMAL_CATEGORY:
                View categoryViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_channel_row, viewGroup, false);
                viewHolder = new CategoryViewHolder(categoryViewHolder);
                break;
            default:
                View defaultViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_channel_category, viewGroup, false);
                viewHolder = new CategoryViewHolder(defaultViewHolder);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = viewHolder.getItemViewType();
        if (data != null)
            switch (viewType) {
                case TYPE_SLIDE:
                    SliderViewHolder sliderViewHolder = (SliderViewHolder) viewHolder;
                    String[] scales = data.get(i).getInfo().getScale().split(":");
                    float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                    sliderViewHolder.itemView.getLayoutParams().height = Math.round(height);
                    sliderViewHolder.bindSlid(data.get(i).getSlides(), data.get(i).getInfo().getPlaybackTime());
                    break;
                case TYPE_CHANNEL_FEATURED_CATEGORY:
                    ChannelViewHolder channelViewHolder = (ChannelViewHolder) viewHolder;
                    channelViewHolder.bindChannel(data.get(i).getChannels());
                    break;
                case TYPE_CHANNEL_NORMAL_CATEGORY:
                    CategoryViewHolder categoryViewHolder = (CategoryViewHolder) viewHolder;
                    categoryViewHolder.bindChannel(data.get(i).getCategories());
                    break;
            }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public int getItemViewType(int position) {
        switch (data.get(position).getType()) {
            case ParentChannel.CHANNEL_NORMAL_CATEGORY:
                return TYPE_CHANNEL_NORMAL_CATEGORY;
            case ParentChannel.CHANNEL_FEATURED_CATEGORY:
                return TYPE_CHANNEL_FEATURED_CATEGORY;
            case ParentChannel.TYPE_SLIDE:
                return TYPE_SLIDE;
            default:
                return 4;
        }
    }

    public interface OnFavoriteChannelCallBack {
        void onCategoryClick(Category category);

        void onChannelClick(Channel channel);

        void onSlideClick(int position);
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private BannerSlider slider;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            slider = itemView.findViewById(R.id.fc_advertisementItem);
        }

        void bindSlid(List<Slide> slides, long interval) {
            slider.postDelayed(() -> {
                slider.setAdapter(new ChannelBannerSliderAdapter(slides));
                slider.setSelectedSlide(0);
                slider.setInterval((int) interval);
            }, 100);

            slider.setOnSlideClickListener(position -> {
                if (callBack != null)
                    callBack.onSlideClick(position);
            });
        }

    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private FeatureChannelAdapter adapter;

        ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_item_popular_row);
        }

        void bindChannel(List<Channel> channels) {
            adapter = new FeatureChannelAdapter(channels);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            adapter.setOnClickedChannelEventCallBack(channel -> {
                if (callBack != null)
                    callBack.onChannelClick(channel);
            });
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private FavoriteCategoryAdapter adapter;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_channelRow);
        }

        void bindChannel(List<Category> categories) {
            adapter = new FavoriteCategoryAdapter(categories);
            recyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 4, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            adapter.setOnClickedItemEventCallBack(category -> {
                if (callBack != null)
                    callBack.onCategoryClick(category);
            });
        }

    }
}
