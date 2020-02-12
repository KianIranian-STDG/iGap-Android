package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankAccountModel {

    @SerializedName("deposit_number")
    private String accountNumber;

    @SerializedName("deposit_title")
    private String title;

    private transient boolean isSelected;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
