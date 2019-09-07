package net.iGap.libs.bannerslider;

import androidx.annotation.DrawableRes;
import android.widget.ImageView;

/**
 * @author S.Shahini
 * @since 4/4/18
 */

public interface BannerImageLoadingService {
    void loadImage(String url, ImageView imageView);

    void loadImage(@DrawableRes int resource, ImageView imageView);

    void loadImage(String url, @DrawableRes int placeHolder, @DrawableRes int errorDrawable, ImageView imageView);
}
