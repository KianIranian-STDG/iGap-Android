package net.iGap.helper.avatar;

class ImageHashValue {
    long avatarOwnerId;
    OnAvatarChange onChangeAvatar;

    ImageHashValue(long avatarOwnerId, OnAvatarChange onChangeAvatar) {
        this.avatarOwnerId = avatarOwnerId;
        this.onChangeAvatar = onChangeAvatar;
    }

}
