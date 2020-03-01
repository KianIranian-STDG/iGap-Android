package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankServiceLoanDetailModel {

    @SerializedName("amount")
    private int amount;
    @SerializedName("automatic_payment_account_number")
    private String automaticPaymentAccountNumber;
    @SerializedName("cb_loan_number")
    private String loanNumber;
    @SerializedName("ch_customers_info")
    private List<Customer> customersInfo;
    @SerializedName("count_of_matured_unpaid")
    private int countOfMaturedUnpaid;
    @SerializedName("count_of_paid")
    private int countOfPaid;
    @SerializedName("count_of_unpaid")
    private int countOfUnpaid;
    @SerializedName("discount")
    private int discount;
    @SerializedName("loan_rows")
    private List<LoanItem> loanItems;
    @SerializedName("penalty")
    private int penalty;
    @SerializedName("total_matured_unpaid_amount")
    private int totalMaturedUnpaidAmount;
    @SerializedName("total_paid_amount")
    private int totalPaidAmount;
    @SerializedName("total_record")
    private int totalRecord;
    @SerializedName("total_unpaid_amount")
    private int totalUnpaidAmount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAutomaticPaymentAccountNumber() {
        return automaticPaymentAccountNumber;
    }

    public void setAutomaticPaymentAccountNumber(String automaticPaymentAccountNumber) {
        this.automaticPaymentAccountNumber = automaticPaymentAccountNumber;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public List<Customer> getCustomersInfo() {
        return customersInfo;
    }

    public void setCustomersInfo(List<Customer> customersInfo) {
        this.customersInfo = customersInfo;
    }

    public int getCountOfMaturedUnpaid() {
        return countOfMaturedUnpaid;
    }

    public void setCountOfMaturedUnpaid(int countOfMaturedUnpaid) {
        this.countOfMaturedUnpaid = countOfMaturedUnpaid;
    }

    public int getCountOfPaid() {
        return countOfPaid;
    }

    public void setCountOfPaid(int countOfPaid) {
        this.countOfPaid = countOfPaid;
    }

    public int getCountOfUnpaid() {
        return countOfUnpaid;
    }

    public void setCountOfUnpaid(int countOfUnpaid) {
        this.countOfUnpaid = countOfUnpaid;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public List<LoanItem> getLoanItems() {
        return loanItems;
    }

    public void setLoanItems(List<LoanItem> loanItems) {
        this.loanItems = loanItems;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int getTotalMaturedUnpaidAmount() {
        return totalMaturedUnpaidAmount;
    }

    public void setTotalMaturedUnpaidAmount(int totalMaturedUnpaidAmount) {
        this.totalMaturedUnpaidAmount = totalMaturedUnpaidAmount;
    }

    public int getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(int totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getTotalUnpaidAmount() {
        return totalUnpaidAmount;
    }

    public void setTotalUnpaidAmount(int totalUnpaidAmount) {
        this.totalUnpaidAmount = totalUnpaidAmount;
    }

    public class Customer {
        @SerializedName("cif")
        private String cif;
        @SerializedName("code")
        private String bankCode;
        @SerializedName("foreign_name")
        private String englishName;
        @SerializedName("gender")
        private String gender;
        @SerializedName("name")
        private String name;
        @SerializedName("title")
        private String title;

        public String getCif() {
            return cif;
        }

        public void setCif(String cif) {
            this.cif = cif;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public class LoanItem {
        @SerializedName("delay_day")
        private int delayDay;
        @SerializedName("pay_date")
        private String payDate;
        @SerializedName("pay_status")
        private String payStatus;
        @SerializedName("payed_amount")
        private int payedAmount;
        @SerializedName("penalty_amount")
        private int penaltyAmount;
        @SerializedName("unpaid_amount")
        private int unpaidAmount;

        public int getDelayDay() {
            return delayDay;
        }

        public void setDelayDay(int delayDay) {
            this.delayDay = delayDay;
        }

        public String getPayDate() {
            return payDate;
        }

        public void setPayDate(String payDate) {
            this.payDate = payDate;
        }

        public String getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(String payStatus) {
            this.payStatus = payStatus;
        }

        public int getPayedAmount() {
            return payedAmount;
        }

        public void setPayedAmount(int payedAmount) {
            this.payedAmount = payedAmount;
        }

        public int getPenaltyAmount() {
            return penaltyAmount;
        }

        public void setPenaltyAmount(int penaltyAmount) {
            this.penaltyAmount = penaltyAmount;
        }

        public int getUnpaidAmount() {
            return unpaidAmount;
        }

        public void setUnpaidAmount(int unpaidAmount) {
            this.unpaidAmount = unpaidAmount;
        }
    }
}
