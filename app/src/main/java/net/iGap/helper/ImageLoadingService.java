package net.iGap.helper;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.R;

public class ImageLoadingService {

    public static void load(String url, ImageView imageView) {
        Picasso.get().load(url)
                .error(R.drawable.ic_error)
                .into(imageView);
    }
}
