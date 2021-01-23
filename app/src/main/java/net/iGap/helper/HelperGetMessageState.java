/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.os.Handler;

import net.iGap.Config;
import net.iGap.fragments.FragmentChat;
import net.iGap.request.RequestChannelGetMessagesStats;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class HelperGetMessageState {

    private static ConcurrentHashMap<Long, HashSet<Long>> roomIds = new ConcurrentHashMap<>();
    private static HashSet<Long> messageIds = new HashSet<>();
    private static Thread thread;
    private static final Object object = new Object();

    public static void getMessageState(long roomId, long messageId) {

        synchronized (object) {
            if (thread == null) {
                thread = new Thread(() -> {
                    final Handler handler = new Handler();
                    sendMessageStateRequest();
                    handler.postDelayed(thread, Config.GET_MESSAGE_STATE_TIME_OUT);
                });
                thread.start();
            }
        }

        if (messageIds.contains(messageId)) {
            return;
        }

        messageIds.add(messageId);

        synchronized (object) {
            if (!roomIds.containsKey(roomId)) {
                HashSet<Long> messageIdsForRoom = new HashSet<>();
                roomIds.put(roomId, messageIdsForRoom);
            }

            HashSet<Long> messageIdsForRoom = roomIds.get(roomId);
            messageIdsForRoom.add(messageId);
        }
    }

    private static void sendMessageStateRequest() {
        synchronized (object) {
            for (long roomId : roomIds.keySet()) {
                HashSet<Long> messageIds = roomIds.get(roomId);
                roomIds.remove(roomId);
                if (messageIds.size() > 0) {

                    new RequestChannelGetMessagesStats().channelGetMessagesStats(roomId, messageIds);
                }
            }
        }
    }

    public static void clearMessageViews() {
        messageIds.clear();
    }


}
