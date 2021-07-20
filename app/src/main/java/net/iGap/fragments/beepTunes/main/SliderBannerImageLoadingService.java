package net.iGap.fragments.beepTunes.main;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.bannerslider.BannerImageLoadingService;

public class SliderBannerImageLoadingService implements BannerImageLoadingService {

    @Override
    public void loadImage(String url, ImageView imageView) {
        Glide.with(G.context)
                .load(url).fitCenter().centerInside().error(R.drawable.ic_error).into(imageView);
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
