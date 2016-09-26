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

    @Nullable
    public String getLocalPath() {
        return localPath;
    }

    public boolean existsOnLocal() {
        return localPath != null && new File(localPath).exists();
    }

    public void setLocalPath(@Nullable String localPath) {
        this.localPath = localPath;
    }

    @Nullable
    private String localPath;

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
