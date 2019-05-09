package net.iGap.adapter;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.module.AndroidUtils;

public class BindingAdapter {

    @android.databinding.BindingAdapter("app:imagePath")
    public static void setAddedAvatarImage(ImageView imageView, String imagePath) {
        if (imagePath != null) {
            Picasso.get().load(AndroidUtils.suitablePath(imagePath)).into(imageView);
        }
    }
}
