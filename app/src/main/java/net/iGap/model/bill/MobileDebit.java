package net.iGap.model.bill;

import com.google.gson.annotations.SerializedName;

public class MobileDebit {

    @SerializedName("mid_term_bill_info")
    private MobileInquiryObject midTerm;
    @SerializedName("last_term_bill_info")
    private MobileInquiryObject lastTerm;

    private boolean loading;

    public MobileDebit() {
        loading = true;
    }

    public MobileInquiryObject getMidTerm() {
        return midTerm;
    }

    public void setMidTerm(MobileInquiryObject midTerm) {
        this.midTerm = midTerm;
    }

    public MobileInquiryObject getLastTerm() {
        return lastTerm;
    }

    public void setLastTerm(MobileInquiryObject lastTerm) {
        this.lastTerm = lastTerm;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public static class MobileInquiryObject {
        @SerializedName("bill_id")
        public String billID;
        @SerializedName("pay_id")
        public String payID;
        @SerializedName("amount")
        public String amount;

        public MobileInquiryObject() {
        }

        public MobileInquiryObject(String billID, String payID, String amount) {
            this.billID = billID;
            this.payID = payID;
            this.amount = amount;
        }

        public String getBillID() {
            return billID;
        }

        public void setBillID(String billID) {
            this.billID = billID;
        }

        public String getPayID() {
            return payID;
        }

        public void setPayID(String payID) {
            this.payID = payID;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
