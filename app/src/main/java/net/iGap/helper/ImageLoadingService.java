package net.iGap.helper;

import android.os.Build;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.api.apiService.RetrofitFactory;

public class ImageLoadingService {
    private static Picasso picasso;

    public static void load(String url, ImageView imageView) {
        if (picasso == null)
            if (Build.VERSION.SDK_INT < 22) {
                picasso = new Picasso.Builder(imageView.getContext())
                        .downloader(new OkHttp3Downloader(new RetrofitFactory().getHttpClient())).build();
            } else {
                picasso = new Picasso.Builder(imageView.getContext()).build();
            }

        if (picasso != null)
            picasso.load(url).fit().centerInside().error(R.drawable.ic_error).into(imageView);
    }
}
