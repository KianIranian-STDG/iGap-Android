package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TicketHistoryListResponse<T> extends BaseIGashtResponse<T> {

    @SerializedName("total")
    private int total;
    @SerializedName("offset")
    private int offset;
    @SerializedName("limit")
    private int limit;

    public TicketHistoryListResponse() {
        this.data = new ArrayList<>();
    }

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
