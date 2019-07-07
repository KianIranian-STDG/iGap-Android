package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class SearchAlbum {

    @Expose
    private List<Datum> data;
    @Expose
    private Pagination pagination;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
