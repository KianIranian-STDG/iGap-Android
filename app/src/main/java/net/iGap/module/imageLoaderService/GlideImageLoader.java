package net.iGap.module.imageLoaderService;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import net.iGap.G;

public class GlideImageLoader implements ImageLoaderService {

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl, int placeHolder) {
        load(targetImageView, imageUrl, placeHolder, false);
    }

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl) {
        load(targetImageView, imageUrl, 0, false);
    }

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl, boolean clear) {
        if (clear)
            clear(targetImageView);
        load(targetImageView, imageUrl, 0, false);
    }

    @Override
    public void loadImage(ImageView targetImageView, String imageUrl, boolean clear, boolean isGif) {
        if (clear) {
            clear(targetImageView);
        }
        load(targetImageView, imageUrl, 0, isGif);
    }

    @Override
    public void clear(ImageView targetImageView) {
        Glide.with(G.context).clear(targetImageView);
    }

    public void load(ImageView targetImageView, String imageUrl, int placeHolder, boolean isGif) {
        if (targetImageView == null)
            throw new NullPointerException("targetImageView NULL");

        RequestManager requestManager = Glide.with(G.context);
        RequestBuilder requestBuilder = requestManager.load(imageUrl).placeholder(placeHolder);

        if (isGif) {
            requestBuilder = requestManager.asGif();
        }

        targetImageView.setImageBitmap(null);
        requestBuilder.into(targetImageView);
    }

}
