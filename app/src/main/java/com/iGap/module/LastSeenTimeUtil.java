package com.iGap.module;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LastSeenTimeUtil {
    private LastSeenTimeUtil() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    private static HashMap<Long, Long> hashMapLastSeen = new HashMap<>();

    /**
     * if last seen bigger than 10 minutes return local time otherwise
     * return minutes from latest user seen
     *
     * @param userId
     * @param lastSeen
     * @return
     */
    public static String computeTime(long userId, long lastSeen) {
        if (timeOut(lastSeen)) {
            return TimeUtils.toLocal(lastSeen, G.ROOM_LAST_MESSAGE_TIME);
        } else {
            updateLastSeenTime();
            hashMapLastSeen.put(userId, lastSeen);

            return Integer.toString((int) ((lastSeen / (1000 * 60)) % 60));
        }

    }

    /**
     * check lastSeen time and send to callback
     */

    private static synchronized void updateLastSeenTime() {

        ArrayList<Long> userIdList = new ArrayList<>();

        for (Iterator<Map.Entry<Long, Long>> it = hashMapLastSeen.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, Long> entry = it.next();
            long userId = entry.getKey();
            long value = entry.getValue();

            String showLastSeen;
            if (timeOut(value)) {
                showLastSeen = TimeUtils.toLocal(value, G.ROOM_LAST_MESSAGE_TIME);
                userIdList.add(userId);
            } else {
                showLastSeen = Integer.toString((int) ((value / (1000 * 60)) % 60));
            }

            G.onLastSeenUpdateTiming.onLastSeenUpdate(userId, G.context.getResources().getString(R.string.last_seen_at) + " " + showLastSeen);
        }

        // i separate hashMap remove from iterator , because i guess remove in that iterator make bug in app , but i'm not insuring
        for (long userId : userIdList) {
            hashMapLastSeen.remove(userId);
        }

        userIdList.clear();

        if (hashMapLastSeen.size() > 0) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateLastSeenTime();
                }
            }, Config.LAST_SEEN_DELAY_CHECKING);
        }
    }

    private static boolean timeOut(long lastSeen) {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - lastSeen);

        if (difference >= Config.LAST_SEEN_TIME_OUT) {
            return true;
        }

        return false;
    }
}
