package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

import net.iGap.helper.HelperCalander;

import java.util.List;

public class BillData {

    @SerializedName("billdata")
    private List<BillDataModel> billData;
    @SerializedName("email")
    private String email;
    @SerializedName("nationalcode")
    private int NID;

    public class BillDataModel {
        @SerializedName("billtitle")
        private String billTitle;
        @SerializedName("viasms")
        private boolean isViaSMS;
        @SerializedName("viaprint")
        private boolean isViaPrint;
        @SerializedName("viaemail")
        private boolean isViaEmail;
        @SerializedName("viaapp")
        private String isViaApp;
        @SerializedName("bill_identifier")
        private String billID;
        @SerializedName("exdata")
        private Extra extraData;

        public String getBillTitle() {
            return billTitle;
        }

        public void setBillTitle(String billTitle) {
            this.billTitle = billTitle;
        }

        public boolean isViaSMS() {
            return isViaSMS;
        }

        public void setViaSMS(boolean viaSMS) {
            isViaSMS = viaSMS;
        }

        public boolean isViaPrint() {
            return isViaPrint;
        }

        public void setViaPrint(boolean viaPrint) {
            isViaPrint = viaPrint;
        }

        public boolean isViaEmail() {
            return isViaEmail;
        }

        public void setViaEmail(boolean viaEmail) {
            isViaEmail = viaEmail;
        }

        public String getIsViaApp() {
            return isViaApp;
        }

        public void setIsViaApp(String isViaApp) {
            this.isViaApp = isViaApp;
        }

        public String getBillID() {
            return billID;
        }

        public void setBillID(String billID) {
            this.billID = billID;
        }

        public Extra getExtraData() {
            return extraData;
        }

        public void setExtraData(Extra extraData) {
            this.extraData = extraData;
        }
    }

    public class Extra {
        @SerializedName("firstName")
        private String firstName;
        @SerializedName("lastName")
        private String lastName;
        @SerializedName("fabricNumber")
        private String fabricNumber;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFabricNumber() {
            return fabricNumber;
        }

        public void setFabricNumber(String fabricNumber) {
            this.fabricNumber = fabricNumber;
        }
    }

    public List<BillDataModel> getBillData() {
        return billData;
    }

    public void setBillData(List<BillDataModel> billData) {
        this.billData = billData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNID() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(NID));
        }
        return String.valueOf(NID);
    }

    public void setNID(int NID) {
        this.NID = NID;
    }
}
