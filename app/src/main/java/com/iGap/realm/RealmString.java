package com.iGap.realm;

import io.realm.RealmObject;

public class RealmString extends RealmObject {

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String phone) {
        this.string = phone;
    }
}
