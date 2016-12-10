package com.iGap.interfaces;

/**
 * Created by Rahmani on 11/29/2016.
 */

public interface OnChannelDelete {

    void onChannelDelete(long roomId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
