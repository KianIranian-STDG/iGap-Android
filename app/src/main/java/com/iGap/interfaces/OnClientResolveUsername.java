package com.iGap.interfaces;

import com.iGap.proto.ProtoClientResolveUsername;
import com.iGap.proto.ProtoGlobal;

public interface OnClientResolveUsername {
    void onClientResolveUsername(ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room);

    void onError(int majorCode, int minorCode);
}
