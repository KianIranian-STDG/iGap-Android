package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

public class SearchArtist {

    @Expose
    private List<Artist> data;
    @Expose
    private Pagination pagination;

    public List<Artist> getData() {
        return data;
    }

    public void setData(List<Artist> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
