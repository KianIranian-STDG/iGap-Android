package net.iGap.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.module.AndroidUtils;

import static net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter(value = {"avatarImage"})
    public static void setAddedAvatarImage(ImageView imageView, AvatarImage avatarImage) {
        if (avatarImage != null && avatarImage.imagePath != null) {
            if (avatarImage.showCharacterImage) {
                imageView.setImageBitmap(drawAlphabetOnPicture((int) imageView.getContext().getResources().getDimension(R.dimen.dp60), avatarImage.imagePath, avatarImage.backgroundColor));
            } else {
                Picasso.with(G.context).load(AndroidUtils.suitablePath(avatarImage.imagePath)).into(imageView);
            }
        }
    }

    @androidx.databinding.BindingAdapter(value = {"imageUrl"})
    public static void setAddedAvatarImage(ImageView imageView, String url) {
        if (url != null && url.length() > 0) {
            Picasso.with(G.context).load(url)
                    .error(R.drawable.ic_error)
                    .into(imageView);
        } else {
            Picasso.with(G.context).load(R.mipmap.logo).into(imageView);
        }
    }

    @androidx.databinding.BindingAdapter(value = {"imageUrl"})
    public static void setImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null) {
            ImageLoadingService.load(imageUrl, imageView);
        } else {
            Picasso.with(G.context).load(R.drawable.logo_igap).fit().centerInside().into(imageView);
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
