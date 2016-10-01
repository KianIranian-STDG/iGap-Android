package com.iGap.realm;

import android.support.annotation.Nullable;

import java.io.File;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/26/2016.
 */
public class RealmMessageAttachment extends RealmObject {
    @PrimaryKey
    private long messageId;
    private String token;
    private String name;
    private long size;
    private int width;
    private int height;
    private double duration;
    private String cacheId;

    public RealmMessageThumbnail getLargeThumbnail() {
        return largeThumbnail;
    }

    public void setLargeThumbnail(RealmMessageThumbnail largeThumbnail) {
        this.largeThumbnail = largeThumbnail;
    }

    public RealmMessageThumbnail getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(RealmMessageThumbnail smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    private RealmMessageThumbnail largeThumbnail;
    private RealmMessageThumbnail smallThumbnail;

    @Nullable
    public String getLocalThumbnailPath() {
        return localThumbnailPath;
    }

    public boolean thumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }

    public void setLocalThumbnailPath(@Nullable String localThumbnailPath) {
        this.localThumbnailPath = localThumbnailPath;
    }

    @Nullable
    private String localThumbnailPath;
    @Nullable
    private String localFilePath;

    @Nullable
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(@Nullable String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

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

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }
}
