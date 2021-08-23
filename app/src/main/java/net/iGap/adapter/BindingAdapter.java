package net.iGap.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.StringRes;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.AndroidUtils;

import static net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter(value = {"avatarImage"})
    public static void setAddedAvatarImage(ImageView imageView, AvatarImage avatarImage) {
        if (avatarImage != null && avatarImage.imagePath != null) {
            if (avatarImage.showCharacterImage) {
                imageView.setImageBitmap(drawAlphabetOnPicture((int) imageView.getContext().getResources().getDimension(R.dimen.dp60), avatarImage.imagePath, avatarImage.backgroundColor));
            } else {
                Glide.with(G.context).load(AndroidUtils.suitablePath(avatarImage.imagePath)).into(imageView);
            }
        }
    }

    @androidx.databinding.BindingAdapter(value = {"imageUrl"})
    public static void setAddedAvatarImage(ImageView imageView, String url) {
        if (url != null && url.length() > 0) {
            Glide.with(G.context).load(url).error(R.drawable.ic_error).into(imageView);

        } else {
            Glide.with(G.context).load(R.mipmap.logo).into(imageView);
        }
    }

    @androidx.databinding.BindingAdapter(value = {"imageUrl"})
    public static void setImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null) {
            Glide.with(G.context).load(imageUrl).fitCenter().centerInside().error(R.drawable.ic_error).into(imageView);
        } else {
            Glide.with(G.context).load(R.drawable.logo_igap).centerInside().into(imageView);
        }
    }

    public static class AvatarImage {

        public AvatarImage(String imagePath, boolean showCharacterImage, String backgroundColor) {
            this.imagePath = imagePath;
            this.showCharacterImage = showCharacterImage;
            this.backgroundColor = backgroundColor;
        }

        String imagePath;
        boolean showCharacterImage;
        String backgroundColor;
    }

    @androidx.databinding.BindingAdapter("errorText")
    public static void setErrorMessage(TextInputLayout view, @StringRes int errorMessage) {
        if (errorMessage != 0) {
            view.setErrorEnabled(true);
            view.setError(view.getContext().getString(errorMessage));
        } else {
            view.setError("");
            view.setErrorEnabled(false);
        }
    }

    @androidx.databinding.BindingAdapter("setSelected")
    public static void setErrorMessage(View view, boolean isSelected) {
        view.setSelected(isSelected);
    }
}
