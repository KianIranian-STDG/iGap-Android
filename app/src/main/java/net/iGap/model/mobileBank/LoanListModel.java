package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class LoanListModel {

    @SerializedName("amount")
    private long amount;
    @SerializedName("begin_date")
    private String beginDate;
    @SerializedName("branch_code")
    private String branchCode;
    @SerializedName("branch_name")
    private String branchName;
    @SerializedName("cbLoan_number")
    private String cbLoanNumber;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("loan_number")
    private String loanNumber;
    @SerializedName("loan_remainder")
    private int loanRemainder;
    @SerializedName("pay_number")
    private int payNumber;
    @SerializedName("pre_amount")
    private long preAmount;
    @SerializedName("status")
    private String status;
    @SerializedName("type")
    private String type;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCbLoanNumber() {
        return cbLoanNumber;
    }

    public void setCbLoanNumber(String cbLoanNumber) {
        this.cbLoanNumber = cbLoanNumber;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }

    public int getLoanRemainder() {
        return loanRemainder;
    }

    public void setLoanRemainder(int loanRemainder) {
        this.loanRemainder = loanRemainder;
    }

    public int getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(int payNumber) {
        this.payNumber = payNumber;
    }

    public long getPreAmount() {
        return preAmount;
    }

    public void setPreAmount(long preAmount) {
        this.preAmount = preAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}