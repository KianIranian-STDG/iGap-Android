package net.iGap.adapter.items.popularChannel;

import net.iGap.libs.bannerslider.adapters.SliderAdapter;
import net.iGap.libs.bannerslider.viewholder.ImageSlideViewHolder;
import net.iGap.model.popularChannel.Slide;

import java.util.List;

class PopularChannelSliderAdapter extends SliderAdapter {
    private List<Slide> slides;

    public PopularChannelSliderAdapter(List<Slide> slides) {
        this.slides = slides;
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        for (int i = 0; i < slides.size(); i++) {
            imageSlideViewHolder.bindImageSlide(slides.get(position).getImageUrl());
        }
    }
}
