package net.iGap.model.popularChannel;

import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("hasNextPage")
    private boolean mHasNextPage;
    @SerializedName("hasPrevPage")
    private boolean mHasPrevPage;
    @SerializedName("limit")
    private Long mLimit;
    @SerializedName("nextPage")
    private Object mNextPage;
    @SerializedName("page")
    private long mPage;
    @SerializedName("pagingCounter")
    private long mPagingCounter;
    @SerializedName("prevPage")
    private long mPrevPage;
    @SerializedName("totalDocs")
    private long mTotalDocs;
    @SerializedName("totalPages")
    private long mTotalPages;

    public boolean getHasNextPage() {
        return mHasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        mHasNextPage = hasNextPage;
    }

    public boolean getHasPrevPage() {
        return mHasPrevPage;
    }

    public void setHasPrevPage(Boolean hasPrevPage) {
        mHasPrevPage = hasPrevPage;
    }

    public long getLimit() {
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

    public long getPage() {
        return mPage;
    }

    public void setPage(Long page) {
        mPage = page;
    }

    public long getPagingCounter() {
        return mPagingCounter;
    }

    public void setPagingCounter(Long pagingCounter) {
        mPagingCounter = pagingCounter;
    }

    public long getPrevPage() {
        return mPrevPage;
    }

    public void setPrevPage(Long prevPage) {
        mPrevPage = prevPage;
    }

    public long getTotalDocs() {
        return mTotalDocs;
    }

    public void setTotalDocs(Long totalDocs) {
        mTotalDocs = totalDocs;
    }

    public long getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Long totalPages) {
        mTotalPages = totalPages;
    }

}
