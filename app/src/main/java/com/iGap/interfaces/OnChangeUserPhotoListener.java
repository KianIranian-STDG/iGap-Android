package com.iGap.interfaces;

public interface OnChangeUserPhotoListener {
    /**
     * set null , imagePath . when user don't have image .
     */

    void onChangePhoto(String imagePath);

    /**
     * use this callback when user don't have avatar and after changed
     * nickname client have new initials and color for set instead of avatar
     */
    void onChangeInitials(String initials, String color);
}
