package net.iGap.model;

public class GoToWalletPage {
    private String phone;
    private boolean isScan;

    public GoToWalletPage(String phone, boolean isScan) {
        this.phone = phone;
        this.isScan = isScan;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isScan() {
        return isScan;
    }
}
