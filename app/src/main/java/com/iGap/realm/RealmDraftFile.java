package com.iGap.realm;

import io.realm.RealmObject;

public class RealmDraftFile extends RealmObject {

    //message id create in second
    private String uri;
    private String fileName;
    private int requestCode;
    private long fileSize;
    private long duration;
    private int[] imageDimens;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int[] getImageDimens() {
        return imageDimens;
    }

    public void setImageDimens(int[] imageDimens) {
        this.imageDimens = imageDimens;
    }
}
