package com.iGap.realm;

import io.realm.RealmObject;

/**
 * draft file just work in own client
 */
public class RealmDraftFile extends RealmObject {

    private String uri;
    private String filePath;
    private int requestCode;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
