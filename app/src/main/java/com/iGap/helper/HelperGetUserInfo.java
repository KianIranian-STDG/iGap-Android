package com.iGap.helper;

import com.iGap.G;
import com.iGap.interfaces.OnGetUserInfo;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestUserInfo;

public class HelperGetUserInfo implements OnUserInfoResponse {
    OnGetUserInfo onGetUserInfo;

    public HelperGetUserInfo(OnGetUserInfo onGetUserInfo) {
        this.onGetUserInfo = onGetUserInfo;
        G.onUserInfoResponse = this;
    }

    public void getUserInfo(long userId) {
        new RequestUserInfo().userInfo(userId);
    }

    @Override
    public void onUserInfo(ProtoGlobal.RegisteredUser user, String identity) {
        onGetUserInfo.onGetUserInfo(user);
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }
}
