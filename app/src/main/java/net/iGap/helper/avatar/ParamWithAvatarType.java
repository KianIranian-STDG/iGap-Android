package net.iGap.helper.avatar;

import android.widget.ImageView;

import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

public class ParamWithAvatarType extends BaseParam {
    AvatarHandler.AvatarType avatarType = null;
    int avatarSize = R.dimen.dp60;
    ProtoGlobal.RegisteredUser registeredUser = null;


    public ParamWithAvatarType(ImageView imageView, Long avatarId) {
        super(imageView, avatarId);
    }

    public ParamWithAvatarType avatarType(AvatarHandler.AvatarType avatarType) {
        this.avatarType = avatarType;
        return this;
    }

    public ParamWithAvatarType registeredUser(ProtoGlobal.RegisteredUser registeredUser) {
        this.registeredUser = registeredUser;
        return this;
    }

    public ParamWithAvatarType avatarSize(int avatarSize) {
        this.avatarSize = avatarSize;
        return this;
    }
}