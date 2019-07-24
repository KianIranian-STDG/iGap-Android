package net.iGap.adapter.items.popular;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.libs.bannerslider.BannerImageLoadingService;

public class ImageLoadingService implements BannerImageLoadingService {


    @Override
    public void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url).into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        Picasso.get().load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
    }
}