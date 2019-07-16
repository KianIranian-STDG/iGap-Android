package net.iGap.libs.bannerslider.viewholder;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.libs.bannerslider.BannerSlider;

public class ImageSlideViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public ImageSlideViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView;
    }

    public void bindImageSlide(String imageUrl) {
        if (imageUrl != null) {
            BannerSlider.getImageLoadingService().loadImage(imageUrl, imageView);
        }
    }

    public void bindImageSlide(@DrawableRes int imageResourceId) {
        BannerSlider.getImageLoadingService().loadImage(imageResourceId, imageView);
    }

    public void bindImageSlide(String url, @DrawableRes int placeHolder, @DrawableRes int error) {
        BannerSlider.getImageLoadingService().loadImage(url, placeHolder, error, imageView);
    }

}