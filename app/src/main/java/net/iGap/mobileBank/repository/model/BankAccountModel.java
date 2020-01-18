package net.iGap.mobileBank.repository.model;

public class BankAccountModel {

    private String accountNumber ;
    private boolean isSelected ;

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
}
