package net.iGap;

import net.iGap.helper.HelperLogout;

import static org.paygear.utils.Utils.signOutWallet;

public class AccountHelper {

    public void changeAccount(){
        baseBefore();
        AccountManager.getInstance().setCurrentUser();
        baseAfter();
    }

    public void changeAccount(long id){
        baseBefore();
        AccountManager.getInstance().changeCurrentUserAccount(id);
        baseAfter();
    }

    public void addAccount(){
        baseBefore();
        AccountManager.getInstance().changeCurrentUserForAddAccount();
        baseAfter();
    }

    public boolean logoutAccount(){
        baseBefore();
        boolean t = new HelperLogout().logoutUser(AccountManager.getInstance().getCurrentUser());
        baseAfter();
        return t;
    }

    private void baseBefore(){
        WebSocketClient.getInstance().disconnectSocket(false);
        G.handler.removeCallbacksAndMessages(null);
        signOutWallet();
        AccountManager.getInstance().clearSomeStaticValue();
        //close realm ui
        DbManager.getInstance().closeUiRealm();
    }

    private void baseAfter(){
        DbManager.getInstance().changeRealmConfiguration();
        WebSocketClient.getInstance().connect(true);
    }
}
