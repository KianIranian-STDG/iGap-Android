package net.iGap.model.bill;

import com.google.gson.annotations.SerializedName;

public class MobileDebit {

    @SerializedName("mid_term_bill_info")
    private MobileInquiryObject midTerm;
    @SerializedName("last_term_bill_info")
    private MobileInquiryObject lastTerm;

    private boolean loading;
    private boolean fail;

    public MobileDebit() {
        loading = true;
        fail = false;
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

    public boolean isFail() {
        return fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public static class MobileInquiryObject {
        @SerializedName("bill_id")
        private String billID;
        @SerializedName("pay_id")
        private String payID;
        @SerializedName("amount")
        private String amount;
        @SerializedName("status")
        private int status;

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
            if (status == 0)
                return amount == null ? "0" : amount;
            else
                return "0";
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
