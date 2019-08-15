package net.iGap.fragments.beepTunes.main;

import android.widget.ImageView;

import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bannerslider.BannerImageLoadingService;

public class SliderBannerImageLoadingService implements BannerImageLoadingService {

    @Override
    public void loadImage(String url, ImageView imageView) {
        ImageLoadingService.load(url, imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
//        Picasso.get().load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
//        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
    }
}
