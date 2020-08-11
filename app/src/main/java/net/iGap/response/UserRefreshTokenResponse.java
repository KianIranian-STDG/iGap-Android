package net.iGap.response;

import net.iGap.observers.interfaces.OnRefreshToken;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserRefreshToken;

public class UserRefreshTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserRefreshTokenResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() throws NullPointerException {
        super.handler();

        ProtoUserRefreshToken.UserRefreshTokenResponse.Builder builder = (ProtoUserRefreshToken.UserRefreshTokenResponse.Builder) message;
        if (identity instanceof OnRefreshToken) {
            ((OnRefreshToken) identity).onRefreshToken(builder.getAccessToken());
        } else {
            throw new ClassCastException("identity must be : " + OnRefreshToken.class.getName());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        if (identity instanceof OnRefreshToken) {
            ((OnRefreshToken) identity).onError(majorCode, minorCode);
        } else {
            throw new ClassCastException("identity must be : " + OnRefreshToken.class.getName());
        }
    }
}
