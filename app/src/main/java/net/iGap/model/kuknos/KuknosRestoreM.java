package net.iGap.model.kuknos;

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
        return keys.length() >= 3;
    }
}
