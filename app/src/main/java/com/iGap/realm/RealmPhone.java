package com.iGap.realm;

import io.realm.RealmObject;

public class RealmPhone extends RealmObject {

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
