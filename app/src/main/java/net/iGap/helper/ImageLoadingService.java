package net.iGap.helper;

import android.widget.ImageView;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.api.apiService.RetrofitFactory;

public class ImageLoadingService {

    public static void load(String url, ImageView imageView) {
        new Picasso.Builder(imageView.getContext())
                .downloader(new OkHttp3Downloader(new RetrofitFactory().httpClient))
                .build().load(url)
                .error(R.drawable.ic_error)
                .into(imageView);
    }
}
