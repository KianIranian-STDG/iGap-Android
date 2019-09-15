package net.iGap.libs.bannerslider.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.libs.bannerslider.BannerSlider;

public class ImageSlideViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public ImageSlideViewHolder(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView;
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void bindImageSlide(String imageUrl) {
        if (imageUrl != null) {
            BannerSlider.getBannerImageLoadingService().loadImage(imageUrl, imageView);
        }
    }

    public void bindImageSlide(@DrawableRes int imageResourceId) {
        BannerSlider.getBannerImageLoadingService().loadImage(imageResourceId, imageView);
    }

    public void bindImageSlide(String url, @DrawableRes int placeHolder, @DrawableRes int error) {
        BannerSlider.getBannerImageLoadingService().loadImage(url, placeHolder, error, imageView);
    }

}