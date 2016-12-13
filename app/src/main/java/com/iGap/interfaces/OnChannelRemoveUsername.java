package com.iGap.interfaces;

public interface OnChannelRemoveUsername {
    void onChannelRemoveUsername(long roomId);

    void onError(int majorCode, int minorCode);
}
