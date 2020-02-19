package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class TicketQRCodeResponse {
    @SerializedName("qrCode")
    private String qrCode;

    public String getQrCode() {
        return qrCode;
    }
}
