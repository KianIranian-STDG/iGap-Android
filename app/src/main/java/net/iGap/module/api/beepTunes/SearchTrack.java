package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

public class SearchTrack {

    @Expose
    private List<Track> data;
    @Expose
    private Pagination pagination;

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
