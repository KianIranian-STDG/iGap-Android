package net.iGap.adapter.items.popular;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.model.PopularChannel.ParentChannel;

import ir.radsense.raadcore.widget.RoundedCornersTransform;

public class ImageLoadingService implements ss.com.bannerslider.ImageLoadingService {
    private Context context;
    private ParentChannel parentChannel;
    private int i;

    public ImageLoadingService(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {

        Picasso.get().load(url)
                .transform(new RoundedCornersTransform(12, 0))
                .placeholder(R.drawable.error_slider)
                .into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        Picasso.get().load(resource)
                .transform(new RoundedCornersTransform(12, 0))
                .into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable)
                .transform(new RoundedCornersTransform(12, 0))
                .into(imageView);
    }
}