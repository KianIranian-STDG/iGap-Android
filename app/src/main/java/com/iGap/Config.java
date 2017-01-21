package com.iGap;

import android.text.format.DateUtils;

public class Config {

    public static final int ACCEPT = 1;
    public static final int REJECT = 0;
    public static final int TIME_OUT_DELAY_MS = 1000;
    public static final int FAKE_PM_DELAY = 10000;
    public static final int TIME_OUT_MS = (int) (20 * DateUtils.SECOND_IN_MILLIS);
    public static final int LOOKUP_MAP_RESPONSE_OFFSET = 30000;
    public static final int ALLOW_RECONNECT_AGAIN = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int REPEAT_CONNECTION_CHECKING = 1000;
    public static final int INSTANCE_SUCCESSFULLY_CHECKING = 10000;
    public static final int COUNTER_TIMER = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int COUNTER_TIMER_DELAY = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int ACTION_CHECKING = 500;
    public static int UPDATE_STATUS_TIME = 2000; // after this time check that program is in background
    public static final int ACTION_TIME_OUT = 2000;
    public static final int GET_MESSAGE_STATE_TIME_OUT = 5000;
    public static final int GET_MESSAGE_STATE_TIME_OUT_CHECKING = 1000;
    public static final long LAST_SEEN_TIME_OUT = (60 * DateUtils.MINUTE_IN_MILLIS); // after this time show exactly time instead of minutes
    public static final long LAST_SEEN_DELAY_CHECKING = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final long GROUP_SHOW_ACTIONS_COUNT = 3;
    public static final long IMAGE_CORNER = 15;
    public static String urlWebsocket = "wss://secure.igap.net/hybrid/";

    // Generate 20 random bytes, and put them here.
    public static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64, 89
    };
    public static final String BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsm4sNLgDVqPf0ZxLWH3vkB1mPzHIkGWIJtNelibcTtzhipRv0iHeS3Z0wzeQpwYcMbkWQ81+WtgJwxUujitPOZnHvBex8qQLJ2JH33DvevWOgLDWPKEnKlfdi3Qg09pfO/Bx7eoWznWhRR6ZNjRgzY+P/2AaW77/f3wq3XHbHldM3jUrqwValwrWrkigIR0MFTkaGkg11T9JCFvO/L/FaZCAybuutje+H1nmNav3r8Xv6eBYS0nSVEm0dm5h46ECQi9PIxOCSMJ1McZMRkb8UaCScCAxh6lkD9fgZrOT5XQa8EOSWOwHx"
                    + "+uQWdR0efHyYbdC3A8zoJZjxBVtvVnDYwIDAQAB";

    public enum ConnectionState {
        WAITING_FOR_NETWORK, CONNECTING, UPDATING, IGAP
    }

    public enum PutExtraKeys {
        CHANNEL_PROFILE_ROOM_ID_LONG
    }
}
