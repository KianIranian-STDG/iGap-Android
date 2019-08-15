package net.iGap.adapter.items.popularChannel;

import android.content.res.Resources;
import android.view.View;

import net.iGap.libs.bannerslider.adapters.SliderAdapter;
import net.iGap.libs.bannerslider.viewholder.ImageSlideViewHolder;
import net.iGap.model.popularChannel.Slide;

import java.util.List;

class PopularChannelSliderAdapter extends SliderAdapter {
    private List<Slide> slides;
    private View itemView;

    public PopularChannelSliderAdapter(List<Slide> slides) {
        this.slides = slides;
    }

    public void setScale(String scale) {
        String[] scales = scale.split(":");
        float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
        itemView.getLayoutParams().height = Math.round(height);
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        itemView = imageSlideViewHolder.itemView;
        for (int i = 0; i < slides.size(); i++) {
            imageSlideViewHolder.bindImageSlide(slides.get(position).getImageUrl());
        }
    }
}
