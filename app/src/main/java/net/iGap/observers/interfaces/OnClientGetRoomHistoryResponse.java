/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.observers.interfaces;

import net.iGap.proto.ProtoClientGetRoomHistory;

public interface OnClientGetRoomHistoryResponse {
    void onGetRoomHistory(long roomId, long startMessageId, long startDocumentId, long endMessageId, long endDocumentId, long reachMessageId, long messageIdGetHistory, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction);

    void onGetRoomHistoryError(int majorCode, int minorCode, long messageIdGetHistory, long documentIdGetHistory, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction);
}
