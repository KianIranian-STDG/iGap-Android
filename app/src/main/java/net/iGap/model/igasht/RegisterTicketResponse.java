package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class RegisterTicketResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("voucher")
    private String voucher;
    @SerializedName("amount")
    private int amount;
    @SerializedName("token")
    private String token;

    public String getStatus() {
        return status;
    }

    public String getVoucher() {
        return voucher;
    }

    public int getAmount() {
        return amount;
    }

    public String getToken() {
        return token;
    }
}
