package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

public class Pagination {

    @Expose
    private Long numPages;

    public Long getNumPages() {
        return numPages;
    }

    public void setNumPages(Long numPages) {
        this.numPages = numPages;
    }

}
