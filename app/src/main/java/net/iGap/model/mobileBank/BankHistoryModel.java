package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankHistoryModel {

    @SerializedName("agent_branch_code")
    private String agentBranchCode;
    @SerializedName("agent_branch_name")
    private String agentBranchName;
    @SerializedName("balance")
    private String balance;
    @SerializedName("branch_code")
    private String branchCode;
    @SerializedName("branch_name")
    private String branchName;
    @SerializedName("customer_desc")
    private String customerDesc;
    @SerializedName("date")
    private String date;
    @SerializedName("description")
    private String description;
    @SerializedName("reference_number")
    private String refrenceNumber;
    @SerializedName("registration_number")
    private String registerNumber;
    @SerializedName("sequence")
    private int sequence;
    @SerializedName("serial")
    private String serial;
    @SerializedName("serial_number")
    private String serialNum;
    @SerializedName("transfer_amount")
    private int transferAmount;

    public String getAgentBranchCode() {
        return agentBranchCode;
    }

    public void setAgentBranchCode(String agentBranchCode) {
        this.agentBranchCode = agentBranchCode;
    }

    public String getAgentBranchName() {
        return agentBranchName;
    }

    public void setAgentBranchName(String agentBranchName) {
        this.agentBranchName = agentBranchName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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

    public String getCustomerDesc() {
        return customerDesc;
    }

    public void setCustomerDesc(String customerDesc) {
        this.customerDesc = customerDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRefrenceNumber() {
        return refrenceNumber;
    }

    public void setRefrenceNumber(String refrenceNumber) {
        this.refrenceNumber = refrenceNumber;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public int getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(int transferAmount) {
        this.transferAmount = transferAmount;
    }
}
