package org.paygear.wallet;

import android.app.Application;
import android.content.Context;

import org.paygear.wallet.fragment.PaymentHistoryFragment;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.web.Web;

import java.util.ArrayList;

import ir.radsense.raadcore.OnWebResponseListener;
import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Response;

/**
 * Created by Software1 on 8/29/2017.
 */

public class RaadApp extends Application {

    public static final int NOTIFICATION_TYPE_LIKE = 1;
    public static final int NOTIFICATION_TYPE_COMMENT = 2;
    public static final int NOTIFICATION_TYPE_NEW_MESSAGE = 3;
    public static final int NOTIFICATION_TYPE_PAY_COMPLETE = 4;
    public static final int NOTIFICATION_TYPE_COUPON = 5;
    public static final int NOTIFICATION_TYPE_DELIVERY = 6;
    public static final int NOTIFICATION_TYPE_FOLLOW = 7;
    public static final int NOTIFICATION_TYPE_CLUB = 9;
    public static PaymentHistoryFragment.PaygearHistoryOpenChat paygearHistoryOpenChat;
    public static PaymentHistoryFragment.PaygearHistoryCloseWallet paygearHistoryCloseWallet;

    public static final String BROADCAST_INTENT_NEW_MESSAGE = "NEW_MESSAGE";

    public static String appVersion;

    public static Account me;
    public static Card paygearCard;
    public static ArrayList<Card> cards;

    @Override
    public void onCreate() {
        super.onCreate();
        WebBase.apiKey = Web.API_KEY;
        WebBase.isDebug = true;
        WebBase.onResponseListener = new OnWebResponseListener() {
            @Override
            public boolean onResponse(Context context, Response response) {
                /*if (context instanceof NavigationBarActivity) {
                    if (response.code() == 401 || response.code() == 403) {
                        Utils.signOutAndGoLogin((NavigationBarActivity) context);
                        return false;
                    }
                }*/
                return true;
            }
        };

        Raad.init(getApplicationContext());

        appVersion = RaadCommonUtils.getAppVersionName(getApplicationContext());
        //Raad.deviceToken = SettingHelper.getString(getApplicationContext(), SettingHelper.DEVICE_TOKEN, null);
    }

}
