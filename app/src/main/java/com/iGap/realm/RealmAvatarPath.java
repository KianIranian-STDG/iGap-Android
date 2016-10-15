package com.iGap.realm;

import io.realm.RealmObject;

/**
 * RealmAvatarPath just for userInfo
 */

public class RealmAvatarPath extends RealmObject {

    private int id;
    private String pathImage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }
}
