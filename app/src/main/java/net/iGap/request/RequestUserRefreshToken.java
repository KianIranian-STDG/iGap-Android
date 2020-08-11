package net.iGap.request;

import net.iGap.observers.interfaces.OnRefreshToken;
import net.iGap.proto.ProtoUserRefreshToken;

public class RequestUserRefreshToken {

    public void RefreshUserToken(OnRefreshToken refreshToken) {
        ProtoUserRefreshToken.UserRefreshToken.Builder builder = ProtoUserRefreshToken.UserRefreshToken.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(156, builder, refreshToken);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
