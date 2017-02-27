package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoClientSearchUsername;

public class ClientSearchUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientSearchUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder builder = (ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder) message;

        G.onClientSearchUserName.OnGetList(builder);

        //for (ProtoClientSearchUsername.ClientSearchUsernameResponse.Result result : builder.getResultList()) {
        //    result.getType();
        //    result.getExactMatch();
        //    result.getUser();
        //    result.getRoom();
        //}
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


