package com.iGap.realm;

import io.realm.RealmObject;

public class RealmDraftFile extends RealmObject {

    private String uri;
    private int requestCode;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
