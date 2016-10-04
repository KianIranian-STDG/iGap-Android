package com.iGap.realm;

import io.realm.RealmObject;

public class RealmEmail extends RealmObject {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
