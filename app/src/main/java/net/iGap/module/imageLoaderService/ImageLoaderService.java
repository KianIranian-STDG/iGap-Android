package net.iGap.module.imageLoaderService;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;

public interface ImageLoaderService {
    void loadImage(ImageView targetImageView, String imageUrl, @DrawableRes int placeHolder);

    void loadImage(ImageView targetImageView, String imageUrl);

    void loadImage(ImageView targetImageView, String imageUrl, boolean clear);

    void loadImage(ImageView targetImageView, String imageUrl, boolean clear, boolean isGif);

    void clear(ImageView targetImageView);
}
