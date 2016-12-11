package com.iGap.interfaces;

/**
 * call this interface after delete Avatar from RealmAvatar
 */
public interface OnAvatarDelete {
    /**
     * latest avatarPath after delete an avatar if exist
     *
     * @param avatarPath
     */
    void latestAvatarPath(String avatarPath);

    /**
     * return initials and color if after delete avatar not
     * anymore exist avatar
     *
     * @param initials
     * @param color
     */
    void showInitials(String initials, String color);
}
