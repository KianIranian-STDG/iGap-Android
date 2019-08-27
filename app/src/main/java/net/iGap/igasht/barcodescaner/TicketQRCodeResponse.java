package net.iGap.igasht.barcodescaner;

import com.google.gson.annotations.SerializedName;

public class TicketQRCodeResponse {
    @SerializedName("qrCode")
    private String qrCode;

    public String getQrCode() {
        return qrCode;
    }
}
