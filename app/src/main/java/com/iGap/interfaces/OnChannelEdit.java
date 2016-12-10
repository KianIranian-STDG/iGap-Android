package com.iGap.interfaces;

public interface OnChannelEdit {

    void onChannelEdit(long roomId, String name, String description);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
