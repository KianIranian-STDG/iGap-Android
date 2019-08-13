package net.iGap.adapter.items.favoritechannel;

import android.widget.ImageView;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.libs.bannerslider.BannerImageLoadingService;

public class ImageLoadingService implements BannerImageLoadingService {


    @Override
    public void loadImage(String url, ImageView imageView) {
        new Picasso.Builder(imageView.getContext())
                .downloader(new OkHttp3Downloader(new RetrofitFactory().httpClient))
                .build().load(url).into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        Picasso.get().load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        new Picasso.Builder(imageView.getContext())
                .downloader(new OkHttp3Downloader(new RetrofitFactory().httpClient))
                .build().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
    }
}