package net.iGap.adapter.items.popularChannel;

import android.content.res.Resources;

import net.iGap.libs.bannerslider.viewholder.ImageSlideViewHolder;
import net.iGap.model.popularChannel.Slide;

import java.util.List;

public class PopularChannelMoreSliderAdapter extends net.iGap.libs.bannerslider.adapters.SliderAdapter {
    private List<Slide> sliderList;
    private String scale;

    public PopularChannelMoreSliderAdapter(List<Slide> sliderList, String scale) {
        this.sliderList = sliderList;
        this.scale = scale;
    }


    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        String[] scales = scale.split(":");
        float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
        imageSlideViewHolder.itemView.getLayoutParams().height = Math.round(height);
        imageSlideViewHolder.bindImageSlide(sliderList.get(position).getImageUrl());
    }
}
