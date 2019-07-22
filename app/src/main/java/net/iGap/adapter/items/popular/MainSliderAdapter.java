package net.iGap.adapter.items.popular;


import android.content.res.Resources;

import net.iGap.model.PopularChannel.Slide;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {
    private List<Slide> sliderList;
    private String scale;

    public MainSliderAdapter(List<Slide> sliderList, String scale) {
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
