package net.iGap.adapter.items.popular;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.radsense.raadcore.widget.RoundedCornersTransform;

public class ImageLoadingService implements ss.com.bannerslider.ImageLoadingService {
    public Context context;

    public ImageLoadingService(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url)
                .transform(new RoundedCornersTransform(16, 0))
                .into(imageView);
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