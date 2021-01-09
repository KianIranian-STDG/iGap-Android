package net.iGap.module.accountManager;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperLogout;

import static org.paygear.utils.Utils.signOutWallet;

public class AccountHelper {

    public void changeAccount() {
        baseBefore();
        AccountManager.getInstance().setCurrentUser();
        baseAfter();
    }

    public void changeAccount(long id) {
        baseBefore();
        AccountManager.getInstance().changeCurrentUserAccount(id);
        baseAfter();
    }

    public void addAccount() {
        baseBefore();
        AccountManager.getInstance().changeCurrentUserForAddAccount();
        baseAfter();
    }

    public boolean logoutAccount() {
        baseBefore();
        boolean t = new HelperLogout().logoutUser(AccountManager.getInstance().getCurrentUser());
        baseAfter();
        return t;
    }

    private void baseBefore() {
        clearSharedDataAndForward();
        clearCookies(G.context);
        WebSocketClient.getInstance().disconnectSocket(false);
        G.handler.removeCallbacksAndMessages(null);
        signOutWallet();
        AccountManager.getInstance().clearSomeStaticValue();
        //close realm ui
        DbManager.getInstance().closeUiRealm();
    }

    private void baseAfter() {
        DbManager.getInstance().openUiRealm();
        WebSocketClient.getInstance().connect(true);
    }

    @SuppressWarnings("deprecation")
    public void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void clearSharedDataAndForward() {
        FragmentChat.mForwardMessages = null;
        HelperGetDataFromOtherApp.hasSharedData = false;
        HelperGetDataFromOtherApp.sharedList.clear();
    }
}
