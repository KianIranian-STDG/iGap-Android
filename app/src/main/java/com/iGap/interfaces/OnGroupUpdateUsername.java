package com.iGap.interfaces;

public interface OnGroupUpdateUsername {
    void onGroupUpdateUsername(long roomId, String username);

    void onError(int majorCode, int minorCode, int time);
}
