package net.iGap.mobileBank.repository.model;

public class LoanListModel {

    private long amount;
    private String beginDate;
    private String branchCode;
    private String branchName;
    private String cbLoanNumber;
    private String endDate;
    private String loanNumber;
    private int loanRemainder;
    private int payNumber;
    private long preAmount;
    private String status;
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