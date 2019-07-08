package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

public class AlbumTrack {

    @Expose
    private List<Track> data;

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }

}
