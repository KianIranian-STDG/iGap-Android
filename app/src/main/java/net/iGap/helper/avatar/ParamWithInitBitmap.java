package net.iGap.helper.avatar;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ParamWithInitBitmap extends BaseParam {
    Bitmap initAvatar = null;


    public ParamWithInitBitmap(ImageView imageView, Long avatarId) {
        super(imageView, avatarId);
    }

    public ParamWithInitBitmap initBitmap(Bitmap initAvatar) {
        this.initAvatar = initAvatar;
        return this;
    }
}