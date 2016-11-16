package com.iGap.interfaces;

import com.iGap.proto.ProtoUserSessionGetActiveList;

import java.util.List;

public interface OnUserSessionGetActiveList {
    void onUserSessionGetActiveList(List<ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Session> session);
}
