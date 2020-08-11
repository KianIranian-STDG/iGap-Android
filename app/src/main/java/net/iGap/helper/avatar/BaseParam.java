package net.iGap.helper.avatar;

import android.widget.ImageView;

public class BaseParam {
    ImageView imageView;
    Long avatarOwnerId;
    boolean useCache = true;
    boolean showMain = false;
    OnAvatarChange onAvatarChange = null;
    OnInitSet onInitSet = null;


    BaseParam(ImageView imageView, Long avatarOwnerId) {
        this.imageView = imageView;
        this.avatarOwnerId = avatarOwnerId;
    }

    public BaseParam turnOffCache() {
        this.useCache = false;
        return this;
    }

    public BaseParam showMain() {
        this.showMain = true;
        return this;
    }

    public BaseParam onAvatarChange(OnAvatarChange onAvatarChange) {
        this.onAvatarChange = onAvatarChange;
        return this;
    }

    public BaseParam onInitSet(OnInitSet onInitSet) {
        this.onInitSet = onInitSet;
        return this;
    }

}