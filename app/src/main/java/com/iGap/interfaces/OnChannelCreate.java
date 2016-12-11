package com.iGap.interfaces;

/**
 * Created by Rahmani on 11/29/2016.
 */

public interface OnChannelCreate {

    void onChannelCreate(long roomId, String inviteLink);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
