package com.iGap.interfaces;

/**
 * Created by Rahmani on 11/29/2016.
 */

public interface OnChannelLeft {

    void onChannelLeft(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
