package net.iGap.adapter.items.popularChannel;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.model.popularChannel.Category;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.Datum;
import net.iGap.model.popularChannel.ParentChannel;
import net.iGap.model.popularChannel.Slide;

import java.util.ArrayList;
import java.util.List;

public class PopularChannelHomeAdapter extends RecyclerView.Adapter {
    private static final int TYPE_POPULAR_CHANNEL_SLIDE = 0;
    private static final int TYPE_POPULAR_CHANNEL_FEATURED_CATEGORY = 1;
    private static final int TYPE_POPULAR_CHANNEL_NORMAL_CATEGORY = 2;

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
            case TYPE_POPULAR_CHANNEL_SLIDE:
                View sliderViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popular_channel_slider, viewGroup, false);
                viewHolder = new SliderViewHolder(sliderViewHolder);
                break;
            case TYPE_POPULAR_CHANNEL_FEATURED_CATEGORY:
                View channelViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popular_channel_feature, viewGroup, false);
                viewHolder = new FeaturedViewHolder(channelViewHolder);
                break;
            case TYPE_POPULAR_CHANNEL_NORMAL_CATEGORY:
                View categoryViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popular_channel_normal, viewGroup, false);
                viewHolder = new NormalViewHolder(categoryViewHolder);
                break;
            default:
                View defaultViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popular_channel_normal, viewGroup, false);
                viewHolder = new NormalViewHolder(defaultViewHolder);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = viewHolder.getItemViewType();
        if (data != null)
            switch (viewType) {
                case TYPE_POPULAR_CHANNEL_SLIDE:
                    SliderViewHolder sliderViewHolder = (SliderViewHolder) viewHolder;
                    String[] scales = data.get(i).getInfo().getScale().split(":");
                    float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                    sliderViewHolder.itemView.getLayoutParams().height = Math.round(height);
                    sliderViewHolder.bindSlid(data.get(i).getSlides(), data.get(i).getInfo().getPlaybackTime());
                    break;
                case TYPE_POPULAR_CHANNEL_FEATURED_CATEGORY:
                    FeaturedViewHolder channelViewHolder = (FeaturedViewHolder) viewHolder;
                    channelViewHolder.bindChannel(data.get(i).getChannels(), data.get(i).getId(),
                            G.isAppRtl ? data.get(i).getInfo().getTitle() : data.get(i).getInfo().getTitleEn());
                    channelViewHolder.headerTv.setText(data.get(i).getInfo().getTitle());
                    break;
                case TYPE_POPULAR_CHANNEL_NORMAL_CATEGORY:
                    NormalViewHolder categoryViewHolder = (NormalViewHolder) viewHolder;
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
                return TYPE_POPULAR_CHANNEL_NORMAL_CATEGORY;
            case ParentChannel.CHANNEL_FEATURED_CATEGORY:
                return TYPE_POPULAR_CHANNEL_FEATURED_CATEGORY;
            case ParentChannel.TYPE_SLIDE:
                return TYPE_POPULAR_CHANNEL_SLIDE;
            default:
                return 4;
        }
    }

    public interface OnFavoriteChannelCallBack {
        void onCategoryClick(Category category);

        void onChannelClick(Channel channel);

        void onSlideClick(int position);

        void onMoreClick(String moreId, String title);
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private BannerSlider slider;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            slider = itemView.findViewById(R.id.fc_advertisementItem);
        }

        void bindSlid(List<Slide> slides, long interval) {
            slider.postDelayed(() -> {
                slider.setAdapter(new PopularChannelSliderAdapter(slides));
                slider.setSelectedSlide(0);
                slider.setInterval((int) interval);
            }, 100);

            slider.setOnSlideClickListener(position -> {
                if (callBack != null)
                    callBack.onSlideClick(position);
            });
        }

    }

    public class FeaturedViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private FeatureCategoryAdapter adapter;
        private TextView headerTv;
        private View moreFl;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_item_popular_row);
            headerTv = itemView.findViewById(R.id.tv_item_popular_title);
            moreFl = itemView.findViewById(R.id.frame_more_one);
        }

        public void bindChannel(List<Channel> channels, String moreId, String title) {
            adapter = new FeatureCategoryAdapter(channels);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);

            adapter.setOnClickedChannelEventCallBack(channel -> {
                if (callBack != null)
                    callBack.onChannelClick(channel);
            });

            moreFl.setOnClickListener(v -> {
                if (callBack != null)
                    callBack.onMoreClick(moreId, title);
            });
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private NormalCategoryAdapter adapter;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_channelRow);
        }

        public void bindChannel(List<Category> categories) {
            adapter = new NormalCategoryAdapter(categories);
            recyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 4, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
            adapter.setOnClickedItemEventCallBack(category -> {
                if (callBack != null)
                    callBack.onCategoryClick(category);
            });
        }

    }
}
