package net.iGap.module.imageLoaderService;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import net.iGap.G;

public class GlideImageLoader implements ImageLoaderService {
    private static final int FADE_TIME_MS = 200;

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl, int placeHolder) {
        load(targetImageView, imageUrl, placeHolder);
    }

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl) {
        load(targetImageView, imageUrl, 0);
    }

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl, boolean clear) {
        if (clear)
            clear(targetImageView);
        load(targetImageView, imageUrl, 0);
    }

    @Override
    public void clear(ImageView targetImageView) {
        Glide.with(targetImageView.getContext()).clear(targetImageView);
    }

    public void load(ImageView targetImageView, String imageUrl, int placeHolder) {
        if (targetImageView == null)
            throw new NullPointerException("targetImageView NULL");

        if (imageUrl != null) {
            targetImageView.setImageBitmap(null);
            if (placeHolder == 0) {
                Glide.with(G.context).load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(FADE_TIME_MS))
                        .into(targetImageView);
            } else {
                Glide.with(G.context).load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade(FADE_TIME_MS))
                        .placeholder(placeHolder)
                        .into(targetImageView);
            }
        }
    }

}
