package com.iGap.helper;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.request.RequestChannelGetMessagesStats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * helper for manage count of message and latest time
 * that scrolled chat for get message states from server
 */
public class HelperGetMessageState {

    private static ConcurrentHashMap<Long, Long> getViewsMessage = new ConcurrentHashMap<>();
    private static ArrayList<Long> roomIds = new ArrayList<>();
    private static ArrayList<Long> getViews = new ArrayList<>();
    private static long latestTime;

    /**
     * check limit and timeout for sending getMessageState
     *
     * @param roomId    currentRoomId
     * @param messageId messageId that show in view
     */

    public static void getMessageState(long roomId, long messageId) {

        if (!getViews.contains(messageId)) {
            getViews.add(messageId);

            getViewsMessage.put(messageId, roomId);
            if (!roomIds.contains(roomId)) {
                roomIds.add(roomId);
            }
            latestTime = System.currentTimeMillis();

            if (getViewsMessage.size() > 50) {
                sendMessageStateRequest();
            }

            checkLoop();
        }

    }

    /**
     * send request for get message state for each room
     */
    private static void sendMessageStateRequest() {

        for (long roomId : roomIds) {
            ArrayList messageIds = new ArrayList();
            for (Iterator<Map.Entry<Long, Long>> it = getViewsMessage.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Long, Long> entry = it.next();
                if (roomId == entry.getValue()) {
                    messageIds.add(entry.getKey());
                }
                getViewsMessage.remove(entry.getKey());
            }
            if (messageIds.size() > 0) {
                new RequestChannelGetMessagesStats().channelGetMessagesStats(roomId, messageIds);
            }
        }
    }

    /**
     * clear getViews(ArrayList) . when getViews contain a messageId
     * not allow that messageId send for getState. client clear this
     * array in enter to chat for allow message to get new state
     */
    public static void clearMessageViews() {
        getViews.clear();
    }

    /**
     * loop for check time out for sending request get message state
     */
    private static void checkLoop() {
        if (getViewsMessage.size() > 0 && timeOut()) {
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
