package net.iGap.adapter.items.popular;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import net.iGap.model.PopularChannel.Slide;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {
    private List<Slide> sliderList;
    private Context context;

    public MainSliderAdapter(Context context, List<Slide> sliderList) {
        this.sliderList = sliderList;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(sliderList.get(position).getImageUrl());
    }

}
