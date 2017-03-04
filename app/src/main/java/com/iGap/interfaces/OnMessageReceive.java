package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import java.util.List;

public interface OnMessageReceive {

    /**
     * message that reached from server
     */
    void onMessage(long roomId, List<ProtoGlobal.RoomMessage> roomMessages, boolean gapReached);

    void onError(int majorCode, int minorCode);

}
