package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

public class Genre {

    @Expose
    private String key;
    @Expose
    private String name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
