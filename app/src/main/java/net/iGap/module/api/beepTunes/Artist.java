package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class Artist {

    @Expose
    private Long id;
    @Expose
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
