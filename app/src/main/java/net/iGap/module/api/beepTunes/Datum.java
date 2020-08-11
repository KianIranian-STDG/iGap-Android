package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("data_count")
    private Long dataCount;
    @Expose
    private String id;
    @Expose
    private Info info;
    @Expose
    private List<Slide> slides;
    @SerializedName("albums")
    private List<Album> albums;
    @Expose
    private String type;

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Album> getAlbums() {
        return albums;
    }
}
