package net.iGap.kuknos.service.model;

public class KuknosRestoreM {

    private String keys;

    public KuknosRestoreM() {
    }

    public KuknosRestoreM(String keys) {
        this.keys = keys;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public boolean isValid() {
        if (keys.length() < 3)
            return false;
        else
            return true;
    }
}
