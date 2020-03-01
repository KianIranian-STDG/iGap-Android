package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class IGashtTicketDetail {

    @SerializedName("status")
    private String status;
    @SerializedName("_id")
    private String _id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("issuingId")
    private int issuingId;
    @SerializedName("locationId")
    private int locationId;
    @SerializedName("amount")
    private int amount;
    @SerializedName("createdTime")
    private int createdTime;
    @SerializedName("voucher")
    private String voucher;
    @SerializedName("terminalId")
    private String terminalId;
    @SerializedName("ticketInfo")
    private TicketInfo ticketInfo;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("__v")
    private int v;

    public String getStatus() {
        return status;
    }

    public String get_id() {
        return _id;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getIssuingId() {
        return issuingId;
    }

    public int getLocationId() {
        return locationId;
    }

    public int getAmount() {
        return amount;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public String getVoucher() {
        return voucher;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public TicketInfo getTicketInfo() {
        return ticketInfo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getV() {
        return v;
    }
}
