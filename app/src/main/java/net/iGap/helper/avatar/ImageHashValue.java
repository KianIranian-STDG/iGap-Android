package net.iGap.helper.avatar;

class ImageHashValue {
    long avatarId;
    OnAvatarChange onChangeAvatar;

    ImageHashValue(long avatarId, OnAvatarChange onChangeAvatar) {
        this.avatarId = avatarId;
        this.onChangeAvatar = onChangeAvatar;
    }

}
