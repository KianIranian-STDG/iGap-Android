package com.iGap.interfaces;

/**
 * call this interface after read from RealmAvatar
 */

public interface OnAvatarGet {
    /**
     * call this method if avatarPath is exist
     *
     * @param avatarPath path for show image from that
     */
    void onAvatarGet(String avatarPath);

    /**
     * call this method if avatarPath not exist
     *
     * @param initials letters for show in imageView
     * @param color    color imageView background
     */
    void onShowInitials(String initials, String color);
}
