package org.paygear;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import org.paygear.fragment.PaymentHistoryFragment;
import org.paygear.model.Card;
import org.paygear.model.SearchedAccount;
import org.paygear.utils.SettingHelper;
import org.paygear.utils.Utils;
import org.paygear.web.Web;

import java.util.ArrayList;

import ir.radsense.raadcore.OnWebResponseListener;
import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.socket.RaadWebSocket;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Response;

/**
 * Created by Software1 on 8/29/2017.
 */

public class RaadApp {

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
    public static ArrayList<SearchedAccount> merchants;
    public static SearchedAccount selectedMerchant;
    public static boolean hasNewVersion;
    public static int versionCode;
    public static boolean hasRefreshTokenRequest = false;
    public static boolean appIsInBackground = false;

    //    @Override
    public static void onCreate(Context applicationContext) {
//        if (Auth.getCurrentAuth() != null) {
//            Config config = new Config(Auth.getCurrentAuth().accessToken, "");
//            Tapstream.create(this, config);
//        }
//        Fabric.with(this, new Crashlytics());

        //don't remove two line below this value are temporarily
        Raad.language = "fa";
        Raad.isFA = true;
        WebBase.apiKey = Web.API_KEY;
        WebBase.isDebug = false;
        WebBase.onResponseListener = new OnWebResponseListener() {
            @Override
            public boolean onResponse(final Context context, Response response, final Fragment fragment) {
                if (context instanceof NavigationBarActivity) {
                    if (response.raw().request().url().url().getPath().contains("refresh") && response.code() == 403) {
                        Utils.signOutAndGoLogin(fragment.getActivity());
                        return true;
                    }
                    if (response.code() == 401 || response.code() == 403) {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("refresh_token", Auth.getCurrentAuth().refreshToken);
//                        map.put("appid", Web.APP_ID);
                        if (hasRefreshTokenRequest)
                            return true;

                        hasRefreshTokenRequest = true;
//                        Web.getInstance().getWebService().refreshToken(PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
//                            @Override
//                            public void onResponse(Call<Void> call, Response<Void> response) {
//                                hasRefreshTokenRequest = false;
//                                if (!response.isSuccessful()) {
//                                    Utils.signOutAndGoLogin(fragment.getActivity());
//
//                                } else {
//
//                                    String accessToken = response.headers().get("X-ACCESS-TOKEN");
//                                    String tokenType = response.headers().get("X-TOKEN-TYPE");
//                                    String refreshToken = response.headers().get("X-REFRESH-TOKEN");
//
//                                    Auth auth = new Auth(accessToken, tokenType, refreshToken);
//                                    auth.setPublicKey(Auth.getCurrentAuth().getPublicKey());
//
//                                    if (auth.getJWT() == null) {
//                                        Toast.makeText(applicationContext, R.string.authorization_not_valid, Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                    auth.save();
//                                    Web.getInstance().initWebService();
//                                    fragment.onCreate(null);
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Void> call, Throwable t) {
//                                hasRefreshTokenRequest = false;
//                                Utils.signOutAndGoLogin((NavigationBarActivity) context);
//
//
//                            }
//                        });

                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean onActivityResponse(final Context context, Response response, final AppCompatActivity appCompatActivity) {
                if (response.raw().request().url().url().getPath().contains("refresh") && response.code() == 403) {
                    Utils.signOutAndGoLogin(appCompatActivity);
                    return true;
                }

                if (response.code() == 401 || response.code() == 403) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("refresh_token", Auth.getCurrentAuth().refreshToken);
//                    map.put("appid", Web.APP_ID);
                    if (hasRefreshTokenRequest)
                        return true;

                    hasRefreshTokenRequest = true;
//                    Web.getInstance().getWebService().refreshToken(PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
//                        @Override
//                        public void onResponse(Call<Void> call, Response<Void> response) {
//                            hasRefreshTokenRequest = false;
//                            if (!response.isSuccessful()) {
//                                Utils.signOutAndGoLogin(appCompatActivity);
//
//                            } else {
//
//                                String accessToken = response.headers().get("X-ACCESS-TOKEN");
//                                String tokenType = response.headers().get("X-TOKEN-TYPE");
//                                String refreshToken = response.headers().get("X-REFRESH-TOKEN");
//
//                                Auth auth = new Auth(accessToken, tokenType, refreshToken);
//                                auth.setPublicKey(Auth.getCurrentAuth().getPublicKey());
//
//                                if (auth.getJWT() == null) {
//                                    Toast.makeText(applicationContext, R.string.authorization_not_valid, Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                auth.save();
//                                Web.getInstance().initWebService();
//                                appCompatActivity.recreate();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Void> call, Throwable t) {
//                            hasRefreshTokenRequest = false;
//                            Utils.signOutAndGoLogin((NavigationBarActivity) context);
//
//                        }
//                    });

                    return false;
                }
                return true;
            }
        };


        String url = SettingHelper.getString(applicationContext, SettingHelper.SERVER_ADDRESS, RaadWebSocket.SOCKET_URL);
        RaadWebSocket.SOCKET_URL = url;

        Raad.deviceToken = SettingHelper.getString(applicationContext, SettingHelper.DEVICE_TOKEN, null);

        Raad.init(applicationContext);

        RaadApp.me = SettingHelper.PrefsLoad(applicationContext, SettingHelper.USER_ACCOUNT, Account.class, null);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener(applicationContext));

    }

    public static class AppLifecycleListener implements LifecycleObserver {
        Context context;

        AppLifecycleListener(Context context) {
            this.context = context;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onMoveToForeground() {
            RaadApp.me = SettingHelper.PrefsLoad(context, SettingHelper.USER_ACCOUNT, Account.class, null);
            appIsInBackground = false;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onMoveToBackground() {

            appIsInBackground = true;

            // app moved to background
        }
    }
}
