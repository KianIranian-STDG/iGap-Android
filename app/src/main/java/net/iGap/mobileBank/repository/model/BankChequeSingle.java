package net.iGap.mobileBank.repository.model;

import com.google.gson.annotations.SerializedName;

public class BankChequeSingle {

    @SerializedName("balance")
    private Integer balance;
    @SerializedName("change_status_date")
    private String changeStatusDate;
    @SerializedName("description")
    private String description;
    @SerializedName("number")
    private String number;
    @SerializedName("payee_name")
    private String payeeName;
    @SerializedName("register_cheque_date")
    private String registerChequeDate;
    @SerializedName("registerable")
    private String registerable;
    @SerializedName("status")
    private String status;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getChangeStatusDate() {
        return changeStatusDate;
    }

    public void setChangeStatusDate(String changeStatusDate) {
        this.changeStatusDate = changeStatusDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getRegisterChequeDate() {
        return registerChequeDate;
    }

    public void setRegisterChequeDate(String registerChequeDate) {
        this.registerChequeDate = registerChequeDate;
    }

    public String getRegisterable() {
        return registerable;
    }

    public void setRegisterable(String registerable) {
        this.registerable = registerable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
