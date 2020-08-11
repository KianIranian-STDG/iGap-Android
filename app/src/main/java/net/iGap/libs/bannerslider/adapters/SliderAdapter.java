package net.iGap.libs.bannerslider.adapters;

import net.iGap.libs.bannerslider.SlideType;
import net.iGap.libs.bannerslider.viewholder.ImageSlideViewHolder;

public abstract class SliderAdapter {
    public abstract int getItemCount();

    public final SlideType getSlideType(int position) {
        return SlideType.IMAGE;
    }

    public abstract void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder);
}
