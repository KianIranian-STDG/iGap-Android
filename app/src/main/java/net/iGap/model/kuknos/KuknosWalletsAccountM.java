package net.iGap.model.kuknos;

import java.util.ArrayList;

public class KuknosWalletsAccountM {

    private String id;
    private String accountID;
    private ArrayList<KuknosWalletBalanceInfoM> balanceInfo;

    public KuknosWalletsAccountM() {
        if (balanceInfo == null)
            balanceInfo = new ArrayList<KuknosWalletBalanceInfoM>();
    }

    public KuknosWalletsAccountM(String id, String accountID, ArrayList<KuknosWalletBalanceInfoM> balanceInfo) {
        this.id = id;
        this.accountID = accountID;
        this.balanceInfo = balanceInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public ArrayList<KuknosWalletBalanceInfoM> getBalanceInfo() {
        return balanceInfo;
    }

    public void setBalanceInfo(ArrayList<KuknosWalletBalanceInfoM> balanceInfo) {
        this.balanceInfo = balanceInfo;
    }
}
