package net.iGap.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmIceProto extends RealmObject {

    private String Url;
    private String Username;
    private String Credential;

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getCredential() {
        return Credential;
    }

    public void setCredential(String credential) {
        Credential = credential;
    }
}
