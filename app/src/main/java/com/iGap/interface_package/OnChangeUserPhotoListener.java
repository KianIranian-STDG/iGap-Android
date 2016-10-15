package com.iGap.interface_package;

public interface OnChangeUserPhotoListener {
    /**
     * set null , imagePath . when user don't have image .
     *
     * @param imagePath
     */
    void onChangePhoto(String imagePath);
}
