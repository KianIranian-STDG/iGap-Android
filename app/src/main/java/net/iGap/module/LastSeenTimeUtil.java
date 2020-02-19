/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.content.Context;
import android.text.format.DateUtils;

import net.iGap.Config;
import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LastSeenTimeUtil {
    private static HashMap<Long, Long> hashMapLastSeen = new HashMap<>();

    private LastSeenTimeUtil() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    /**
     * if last seen bigger than 10 minutes return local time otherwise
     * return minutes from latest user seen
     *
     * @param lastSeen time in second state(not millis)
     * @param update   if set true time updating after each Config.LAST_SEEN_DELAY_CHECKING time and send callback to onLastSeenUpdateTiming
     */
    public static String computeTime(Context context, long userId, long lastSeen, boolean update, boolean ltr) {
        if (timeOut(lastSeen * DateUtils.SECOND_IN_MILLIS)) {
            return computeDays(context, lastSeen, ltr);
        } else {
            if (update) {
                hashMapLastSeen.put(userId, lastSeen);
                updateLastSeenTime(context);
            }
            return getMinute(lastSeen);
        }
    }

    public static String computeTime(Context context, long userId, long lastSeen, boolean update) {
        if (timeOut(lastSeen * DateUtils.SECOND_IN_MILLIS)) {
            return computeDays(context, lastSeen, true);
        } else {
            if (update) {
                hashMapLastSeen.put(userId, lastSeen);
                updateLastSeenTime(context);
            }
            return getMinute(lastSeen);
        }
    }

    /**
     * get millis and return days
     *
     * @param beforeMillis before time that user was online
     * @return exactly time if is lower than one days otherwise return days
     */

    private static String computeDays(Context context, long beforeMillis, boolean ltr) {

        String time = "";

        String exactlyTime = String.valueOf(HelperCalander.getClocktime(beforeMillis * DateUtils.SECOND_IN_MILLIS, ltr));

        long currentMillis = System.currentTimeMillis();
        int days = (int) ((currentMillis - (beforeMillis * DateUtils.SECOND_IN_MILLIS)) / DateUtils.DAY_IN_MILLIS);
        if (days <= 7) {
            switch (days) {
                case 0:
                    time = HelperCalander.getClocktime(beforeMillis * DateUtils.SECOND_IN_MILLIS, ltr);

                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(beforeMillis * DateUtils.SECOND_IN_MILLIS);

                    if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != date.get(Calendar.DAY_OF_YEAR)) {
                        time = context.getResources().getString(R.string.yesterday) + " " + time;
                    }

                    break;
                case 1:
                    time = context.getResources().getString(R.string.yesterday) + " " + exactlyTime;
                    break;
                case 2:
                    time = context.getResources().getString(R.string.two_day);//+ exactlyTime
                    break;
                case 3:
                    time = context.getResources().getString(R.string.three_day);//+ exactlyTime
                    break;
                case 4:
                    time = context.getResources().getString(R.string.four_day);// + exactlyTime
                    break;
                case 5:
                    time = context.getResources().getString(R.string.five_day);// + exactlyTime
                    break;
                case 6:
                    time = context.getResources().getString(R.string.six_day);// + exactlyTime
                    break;
                case 7:
                    time = context.getResources().getString(R.string.last_week);
                    break;
            }
        } else {
            if (beforeMillis == 0) {
                time = context.getResources().getString(R.string.last_seen_recently);
            } else {
                time = HelperCalander.checkHijriAndReturnTime(beforeMillis) + " " + exactlyTime;
            }
        }

        if (HelperCalander.isPersianUnicode) {
            time = HelperCalander.convertToUnicodeFarsiNumber(time);
        }

        return time;
    }

    /**
     * check lastSeen time and send to callback
     */

    private static synchronized void updateLastSeenTime(Context context) {
        DbManager.getInstance().doRealmTask(realm -> {
            ArrayList<Long> userIdList = new ArrayList<>();
            for (Iterator<Map.Entry<Long, Long>> it = hashMapLastSeen.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Long, Long> entry = it.next();
                long userId = entry.getKey();
                long value = entry.getValue();
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
                if (realmRegisteredInfo != null) {
                    if (realmRegisteredInfo.getStatus() != null && realmRegisteredInfo.getMainStatus() != null && !realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString()) && !realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.LONG_TIME_AGO.toString())) {
                        String showLastSeen;
                        if (timeOut(realmRegisteredInfo.getLastSeen() * DateUtils.SECOND_IN_MILLIS)) {
                            showLastSeen = computeDays(context, realmRegisteredInfo.getLastSeen(), true);
                            userIdList.add(userId);
                        } else {
                            showLastSeen = getMinute(realmRegisteredInfo.getLastSeen());
                        }
                        if (G.onLastSeenUpdateTiming != null) {
                            G.onLastSeenUpdateTiming.onLastSeenUpdate(userId, showLastSeen);
                        }
                    } else {
                        userIdList.add(userId);
                    }
                }
            }

            // i separate hashMap remove from iterator , because i guess remove in that iterator make bug in app , but i'm not insuring
            for (long userId : userIdList) {
                hashMapLastSeen.remove(userId);
            }
            userIdList.clear();
        });

        if (hashMapLastSeen.size() > 0) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateLastSeenTime(context);
                }
            }, Config.LAST_SEEN_DELAY_CHECKING);
        }
    }

    private static boolean timeOut(long lastSeen) {

        long difference;
        long currentTime = System.currentTimeMillis();
        difference = (currentTime - lastSeen);
        return difference >= Config.LAST_SEEN_TIME_OUT;
    }

    private static String getMinute(long time) {

        long currentTime = System.currentTimeMillis();
        long difference = (currentTime - (time * DateUtils.SECOND_IN_MILLIS));
        //difference = -(70 * DateUtils.MINUTE_IN_MILLIS);
        if (TimeUnit.MILLISECONDS.toMinutes(difference) <= 0) {
            return G.context.getResources().getString(R.string.last_seen_recently);
        }

        /*else if (TimeUnit.MILLISECONDS.toMinutes(difference) >= 61) {
            return "*"+G.fragmentActivity.getResources().getString(R.string.last_seen_recently);
        }*/
        String str;
        if (HelperCalander.isPersianUnicode) {
            str = TimeUnit.MILLISECONDS.toMinutes(difference) + " " + "\u200F" + G.context.getResources().getString(R.string.minute_ago);
        } else {

            long minute = TimeUnit.MILLISECONDS.toMinutes(difference);
            String minuteAgo = G.context.getResources().getString(R.string.minute_ago);

            if (minute >= 2) {
                minuteAgo = G.context.getResources().getString(R.string.minutes_ago);
            }

            str = minute + " " + minuteAgo;
        }

        if (HelperCalander.isPersianUnicode) {
            str = HelperCalander.convertToUnicodeFarsiNumber(str);
        }
        return str;
    }
}
