package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserContactsImport;
import com.iGap.proto.ProtoUserLogin;

public class RequestContactImport {

    public void contactImport(String token) {

        ProtoUserContactsImport.UserContactsImport.Builder userContactsImport = ProtoUserContactsImport.UserContactsImport.newBuilder();
        userContactsImport.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        ProtoUserLogin.UserLogin.Builder userLogin = ProtoUserLogin.UserLogin.newBuilder();
        userLogin.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userLogin.setToken(token);

        RequestWrapper requestWrapper = new RequestWrapper(102, userLogin);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}