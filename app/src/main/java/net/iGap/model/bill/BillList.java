package net.iGap.model.bill;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BillList {

    @SerializedName("docs")
    private List<Bill> bills;

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public class Bill {
        @SerializedName("id")
        private String id;
        @SerializedName("bill_type")
        private String billType;
        @SerializedName("bill_title")
        private String billTitle;
        @SerializedName("mobile_number")
        private String mobileNumber;
        @SerializedName("phone_number")
        private String phoneNumber;
        @SerializedName("area_code")
        private String areaCode;
        @SerializedName("bill_identifier")
        private String billID;
        @SerializedName("subscription_code")
        private String subscriptionCode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBillType() {
            return billType;
        }

        public void setBillType(String billType) {
            this.billType = billType;
        }

        public String getBillTitle() {
            return billTitle;
        }

        public void setBillTitle(String billTitle) {
            this.billTitle = billTitle;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public String getBillID() {
            return billID;
        }

        public void setBillID(String billID) {
            this.billID = billID;
        }

        public String getSubscriptionCode() {
            return subscriptionCode;
        }

        public void setSubscriptionCode(String subscriptionCode) {
            this.subscriptionCode = subscriptionCode;
        }
    }
}
