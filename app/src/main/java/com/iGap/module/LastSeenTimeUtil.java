package com.iGap.module;

import android.text.format.DateUtils;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

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
        if (timeOut(lastSeen * DateUtils.SECOND_IN_MILLIS)) {
            return TimeUtils.toLocal(lastSeen * DateUtils.SECOND_IN_MILLIS, G.ROOM_LAST_MESSAGE_TIME);
        } else {
            hashMapLastSeen.put(userId, lastSeen);
            updateLastSeenTime();
            return getMinute(lastSeen);
        }
    }

    /**
     * check lastSeen time and send to callback
     */

    private static synchronized void updateLastSeenTime() {

        Realm realm = Realm.getDefaultInstance();

        ArrayList<Long> userIdList = new ArrayList<>();
        for (Iterator<Map.Entry<Long, Long>> it = hashMapLastSeen.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, Long> entry = it.next();
            long userId = entry.getKey();
            long value = entry.getValue();

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();


            String showLastSeen;
            if (timeOut(value * DateUtils.SECOND_IN_MILLIS)) {
                showLastSeen = TimeUtils.toLocal(value * DateUtils.SECOND_IN_MILLIS, G.ROOM_LAST_MESSAGE_TIME);
                userIdList.add(userId);
            } else {
                showLastSeen = getMinute(value);
            }
            if (realmRegisteredInfo != null && !realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
                if (G.onLastSeenUpdateTiming != null) {
                    G.onLastSeenUpdateTiming.onLastSeenUpdate(userId, showLastSeen);
                }
            }

        }

        // i separate hashMap remove from iterator , because i guess remove in that iterator make bug in app , but i'm not insuring
        for (long userId : userIdList) {
            hashMapLastSeen.remove(userId);
        }

        realm.close();

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

    private static String getMinute(long time) {

        long currentTime = System.currentTimeMillis();
        long difference = (currentTime - (time * DateUtils.SECOND_IN_MILLIS));

        if (TimeUnit.MILLISECONDS.toMinutes(difference) == 0) {
            return G.context.getResources().getString(R.string.last_seen_recently);
        }

        return TimeUnit.MILLISECONDS.toMinutes(difference) + " " + G.context.getResources().getString(R.string.minute_ago);
    }
}
