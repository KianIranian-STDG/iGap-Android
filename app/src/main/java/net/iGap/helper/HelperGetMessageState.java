/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.request.RequestChannelGetMessagesStats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * helper for manage count of message and latest time
 * that scrolled chat for get message states from server
 */
public class HelperGetMessageState {

    private static ConcurrentHashMap<Long, HashSet<Long>> getViewsMessage = new ConcurrentHashMap<>();
    private static long latestTime;

    /**
     * check limit and timeout for sending getMessageState
     *
     * @param roomId    currentRoomId
     * @param messageId messageId that show in view
     */

    public static void getMessageState(long roomId, long messageId) {

        if (!getViewsMessage.containsKey(roomId)) {
            HashSet<Long> messageIdsForRoom = new HashSet<>();
            getViewsMessage.put(roomId, messageIdsForRoom);
        }

        HashSet<Long> messageIdsForRoom = getViewsMessage.get(roomId);
        if (!messageIdsForRoom.contains(messageId)){
            messageIdsForRoom.add(messageId);
            latestTime = System.currentTimeMillis();

            if (messageIdsForRoom.size() > 50) {
                sendMessageStateRequest();
            }

            checkLoop();
        }
    }

    /**
     * send request for get message state for each room
     */
    private static void sendMessageStateRequest() {

        for (long roomId : getViewsMessage.keySet()) {
            HashSet<Long> messageIds = getViewsMessage.get(roomId);
            getViewsMessage.remove(roomId);
            if (messageIds.size() > 0) {
                new RequestChannelGetMessagesStats().channelGetMessagesStats(roomId, messageIds);
            }
        }
    }

    /**
     * clear getViews(ArrayList) .reason : when getViews contain a messageId
     * not allow that messageId send for getState. client clear this
     * array in enter to chat for allow message to get new state
     */
    public static void clearMessageViews() {
        getViewsMessage.clear();
    }

    /**
     * loop for check time out for sending request get message state
     */
    private static void checkLoop() { //this have very bad result in performance
        if (timeOut()) { // getViewsMessage.size() > 0 &&
            sendMessageStateRequest();
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkLoop();
                }
            }, Config.GET_MESSAGE_STATE_TIME_OUT_CHECKING);
        }
    }

    /**
     * check time in each Config.GET_MESSAGE_STATE_TIME_OUT second
     *
     * @return true in timed out
     */
    private static boolean timeOut() {
        long currentTime = System.currentTimeMillis();
        long difference = (currentTime - latestTime);
        if (difference >= Config.GET_MESSAGE_STATE_TIME_OUT) {
            return true;
        }
        return false;
    }
}
