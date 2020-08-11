package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Albums {

    @Expose
    private List<Album> data;
    @Expose
    private Pagination pagination;

    public List<Album> getData() {
        return data;
    }

    public void setData(List<Album> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

}
