package net.iGap.adapter.beepTunes;

import net.iGap.libs.bannerslider.adapters.SliderAdapter;
import net.iGap.libs.bannerslider.viewholder.ImageSlideViewHolder;
import net.iGap.module.api.beepTunes.Slide;

import java.util.List;

class BeepTunesBannerSliderAdapter extends SliderAdapter {
    private List<Slide> slides;

    public BeepTunesBannerSliderAdapter(List<Slide> slides) {
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
