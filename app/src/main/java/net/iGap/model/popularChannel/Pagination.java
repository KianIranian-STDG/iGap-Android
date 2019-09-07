package net.iGap.model.popularChannel;

import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("hasNextPage")
    private Boolean mHasNextPage;
    @SerializedName("hasPrevPage")
    private Boolean mHasPrevPage;
    @SerializedName("limit")
    private Long mLimit;
    @SerializedName("nextPage")
    private Object mNextPage;
    @SerializedName("page")
    private Long mPage;
    @SerializedName("pagingCounter")
    private Long mPagingCounter;
    @SerializedName("prevPage")
    private Long mPrevPage;
    @SerializedName("totalDocs")
    private Long mTotalDocs;
    @SerializedName("totalPages")
    private Long mTotalPages;

    public Boolean getHasNextPage() {
        return mHasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        mHasNextPage = hasNextPage;
    }

    public Boolean getHasPrevPage() {
        return mHasPrevPage;
    }

    public void setHasPrevPage(Boolean hasPrevPage) {
        mHasPrevPage = hasPrevPage;
    }

    public Long getLimit() {
        return mLimit;
    }

    public void setLimit(Long limit) {
        mLimit = limit;
    }

    public Object getNextPage() {
        return mNextPage;
    }

    public void setNextPage(Object nextPage) {
        mNextPage = nextPage;
    }

    public Long getPage() {
        return mPage;
    }

    public void setPage(Long page) {
        mPage = page;
    }

    public Long getPagingCounter() {
        return mPagingCounter;
    }

    public void setPagingCounter(Long pagingCounter) {
        mPagingCounter = pagingCounter;
    }

    public Long getPrevPage() {
        return mPrevPage;
    }

    public void setPrevPage(Long prevPage) {
        mPrevPage = prevPage;
    }

    public Long getTotalDocs() {
        return mTotalDocs;
    }

    public void setTotalDocs(Long totalDocs) {
        mTotalDocs = totalDocs;
    }

    public Long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Long totalPages) {
        mTotalPages = totalPages;
    }

}
