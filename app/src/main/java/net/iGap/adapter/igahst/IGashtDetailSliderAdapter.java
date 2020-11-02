package net.iGap.adapter.igahst;

import android.content.res.Resources;
import android.view.View;

import net.iGap.fragments.beepTunes.main.SliderBannerImageLoadingService;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.libs.bannerslider.adapters.SliderAdapter;
import net.iGap.libs.bannerslider.viewholder.ImageSlideViewHolder;
import net.iGap.model.igasht.Gallery;

import java.util.List;

public class IGashtDetailSliderAdapter extends SliderAdapter {
    private List<Gallery> slides;
    private View itemView;

    public IGashtDetailSliderAdapter(List<Gallery> slides) {
        this.slides = slides;
        BannerSlider.init(new SliderBannerImageLoadingService());
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
