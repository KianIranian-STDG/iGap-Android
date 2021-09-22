package net.iGap.model.qrCodePayment;

import com.google.gson.annotations.SerializedName;

public class MerchantInfo {

    @SerializedName("pcqr")
    private boolean mPcqr;
    @SerializedName("qr_code")
    private String mQrCode;
    @SerializedName("merchant_name")
    private String mMerchantName;

    public boolean isPcqr() {
        return mPcqr;
    }

    public void setPcqr(boolean pcqr) {
        mPcqr = pcqr;
    }

    public String getQrCode() {
        return mQrCode;
    }

    public void setQrCode(String qrCode) {
        mQrCode = qrCode;
    }

    public String getMerchantName() {
        return mMerchantName;
    }

    public void setMerchantName(String merchantName) {
        mMerchantName = merchantName;
    }
}
