package com.iGap.realm;

import io.realm.RealmObject;


public class RealmFileAvatar extends RealmObject {

    private String token;
    private String name;
    private long size;
    private RealmThumbnail largeThumbnail;
    private RealmThumbnail smallThumbnail;
    private int width;
    private int height;
    private double duration;
    private String catchId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public RealmThumbnail getLargeThumbnail() {
        return largeThumbnail;
    }

    public void setLargeThumbnail(RealmThumbnail largeThumbnail) {
        this.largeThumbnail = largeThumbnail;
    }

    public RealmThumbnail getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(RealmThumbnail smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getCatchId() {
        return catchId;
    }

    public void setCatchId(String catchId) {
        this.catchId = catchId;
    }
}
