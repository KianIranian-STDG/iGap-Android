package com.iGap.interfaces;

public interface OnGroupRemoveUsername {
    void onGroupRemoveUsername(long roomId);

    void onError(int majorCode, int minorCode);
}
