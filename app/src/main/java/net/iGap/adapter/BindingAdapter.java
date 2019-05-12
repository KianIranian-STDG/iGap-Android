package net.iGap.adapter;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.module.AndroidUtils;

import static net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture;

public class BindingAdapter {

    @android.databinding.BindingAdapter(value = {"app:avatarImage"})
    public static void setAddedAvatarImage(ImageView imageView, AvatarImage avatarImage) {
        if (avatarImage != null && avatarImage.imagePath != null) {
            if (avatarImage.showCharacterImage) {
                imageView.setImageBitmap(drawAlphabetOnPicture((int) imageView.getContext().getResources().getDimension(R.dimen.dp60), avatarImage.imagePath, avatarImage.backgroundColor));
            } else {
                Picasso.get().load(AndroidUtils.suitablePath(avatarImage.imagePath)).into(imageView);
            }
        }
    }

    public static class AvatarImage{

        public AvatarImage(String imagePath, boolean showCharacterImage, String backgroundColor) {
            this.imagePath = imagePath;
            this.showCharacterImage = showCharacterImage;
            this.backgroundColor = backgroundColor;
        }
        String imagePath;
        boolean showCharacterImage;
        String backgroundColor;
    }
}
