package com.iGap.interfaces;


public interface OnChannelAvatarDelete {

    void onChannelAvatarDelete(long roomId, long avatarId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
