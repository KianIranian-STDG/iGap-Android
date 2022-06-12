package net.iGap.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import net.iGap.G;
import net.iGap.helper.HelperPermission;
import net.iGap.observers.interfaces.OnGetPermission;

import java.io.IOException;

/**
 * Network utility
 */
public class NetworkUtility {
    public static final String NETWORK_2G = "2G";
    public static final String NETWORK_3G = "3G";
    public static final String NETWORK_4G = "4G";
    public static final String NETWORK_5G = "5G";

    /**
     * Get maximum concurrency for upload/download
     *
     * @param context
     * @return the max number of concurrent upload
     */
    public static int getMaxConcurrency(Context context) {
        if (isWifi(context)) {
            return 5;
        }

        int concurrencyNum = 3; // default value (3G)
        switch (getNetworkClass(context)) {
            case NETWORK_2G:
                concurrencyNum = 2;
                break;
            case NETWORK_3G:
                concurrencyNum = 3;
                break;
            case NETWORK_4G:
                concurrencyNum = 4;
                break;
            case NETWORK_5G:
                concurrencyNum = 5;
                break;
            default:
                break;
        }
        return concurrencyNum;
    }

    private static int networkType;

    private static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            try {
                HelperPermission.getPhonePermision(G.currentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            networkType = mTelephonyManager.getNetworkType();
                        }

                    }

                    @Override
                    public void deny() {
                        networkType = 0;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                networkType = mTelephonyManager.getNetworkType();
            }
        }


        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Unknown";
        }
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
