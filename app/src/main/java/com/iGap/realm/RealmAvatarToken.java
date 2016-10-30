package com.iGap.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmAvatarToken extends RealmObject {

    @PrimaryKey private int id;
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
