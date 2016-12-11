package com.iGap.interfaces;

public interface OnChannelUpdateUsername {
    void onChannelUpdateUsername(long roomId, String username);

    void onError(int majorCode, int minorCode);
}
