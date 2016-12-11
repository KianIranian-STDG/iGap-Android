package com.iGap.interfaces;

public interface OnGroupAvatarDelete {
    void onDeleteAvatar(long roomId, long avatarId);

    void onDeleteAvatarError();
}
