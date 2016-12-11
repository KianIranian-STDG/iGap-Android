package com.iGap.interfaces;

import com.iGap.proto.ProtoChannelCheckUsername;

public interface OnChannelCheckUsername {
    void onChannelCheckUsername(ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status);

    void onError(int majorCode, int minorCode);
}
