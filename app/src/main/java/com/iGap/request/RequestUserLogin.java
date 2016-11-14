package com.iGap.request;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserLogin;

import java.util.Locale;

import static com.iGap.G.context;

public class RequestUserLogin {

    private int AppBuildVersion;
    private String AppVersion;
    private String Device;
    private String Language;

    public void userLogin(String token) {

        infoApp();

        ProtoUserLogin.UserLogin.Builder userLogin = ProtoUserLogin.UserLogin.newBuilder();
        userLogin.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userLogin.setToken(token);
        userLogin.setAppName("iGap Android");
        userLogin.setAppId(2);
        userLogin.setAppBuildVersion(AppBuildVersion);

        userLogin.setAppVersion(AppVersion);
        userLogin.setPlatform(ProtoGlobal.Platform.ANDROID);
        userLogin.setPlatformVersion(Integer.toString(android.os.Build.VERSION.SDK_INT));//api number


        userLogin.setDevice(typeMobile()); //
        userLogin.setDeviceName(Device);

        userLogin.setLanguage(typeLanguage());


        RequestWrapper requestWrapper = new RequestWrapper(102, userLogin);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void infoApp() {

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pInfo != null) {
            AppVersion = pInfo.versionName;
            AppBuildVersion = pInfo.versionCode;

        }
        Device = Build.BRAND;
        Language = Locale.getDefault().getDisplayLanguage();

    }

    private ProtoGlobal.Language typeLanguage() {

        Language = Locale.getDefault().getDisplayLanguage();
        if (Language.equals("English")) {
            return ProtoGlobal.Language.FA_IR;
        } else {
            return ProtoGlobal.Language.EN_US;
        }
    }

    private ProtoGlobal.Device typeMobile() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            return ProtoGlobal.Device.TABLET;
        } else {
            // smaller device
            return ProtoGlobal.Device.MOBILE;
        }
    }

}
