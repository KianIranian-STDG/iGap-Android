package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserLogin;

public class RequestUserLogin {

    public void userLogin(String token) {
        ProtoUserLogin.UserLogin.Builder userLogin = ProtoUserLogin.UserLogin.newBuilder();
        userLogin.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userLogin.setToken(token);
        userLogin.setAppName("iGap Android");
        userLogin.setAppId(2);
        userLogin.setAppBuildVersion(1);

        userLogin.setAppVersion("1.0.3");
        userLogin.setPlatform(ProtoGlobal.Platform.ANDROID);
        userLogin.setPlatformVersion(Integer.toString(android.os.Build.VERSION.SDK_INT));//api number


        userLogin.setDevice(ProtoGlobal.Device.MOBILE); //
        userLogin.setDeviceName("Huawei");

        userLogin.setLanguage(ProtoGlobal.Language.FA_IR);


        RequestWrapper requestWrapper = new RequestWrapper(102, userLogin);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
