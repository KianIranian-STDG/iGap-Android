package net.iGap.igasht.historylocation;

import com.google.gson.annotations.SerializedName;

import net.iGap.igasht.BaseIGashtResponse;

public class TicketHistoryListResponse<T> extends BaseIGashtResponse<T> {

    @SerializedName("total")
    private int total;
    @SerializedName("offset")
    private int offset;
    @SerializedName("limit")
    private int limit;

    public int getTotal() {
        return total;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
