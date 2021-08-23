/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the kianiranian Company - http://www.kianiranian.com/
 * All rights reserved.
 */

package net.iGap;

import android.text.format.DateUtils;

public class Config {
    public static boolean FILE_LOG_ENABLE;

    static {
        try {
            FILE_LOG_ENABLE = BuildConfig.VERSION_NAME.contains("alpha") || BuildConfig.DEBUG;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final int ACCEPT = 1;
    public static final int REJECT = 0;
    public static final int REALM_SCHEMA_VERSION = 51;
    public static final int LOOKUP_MAP_RESPONSE_OFFSET = 30000;
    public static final int MAX_TEXT_ATTACHMENT_LENGTH = 1024;
    public static final int MAX_TEXT_LENGTH = 4096;
    public static final int ACTION_CHECKING = 500;
    public static final int GROUP_SHOW_ACTIONS_COUNT = 3;
    public static final int LIMIT_GET_HISTORY_LOW = 10;
    public static final int LIMIT_GET_HISTORY_NORMAL = 50;
    public static final int LIMIT_LOAD_ROOM = 50;
    public static final int STORE_MESSAGE_POSITION_LIMIT = 2; // count of message from end of list
    public static final int LOW_START_PAGE_TIME = 200;
    public static final long drIgapPeerId = 2297310;

    public static final int TIME_OUT_DELAY_MS = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int ACTION_EXPIRE_LOOP = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int FAKE_PM_DELAY = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int TIME_OUT_MS = (int) (20 * DateUtils.SECOND_IN_MILLIS);
    public static final int DEFAULT_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int COUNTER_TIMER_DELAY = (int) (DateUtils.SECOND_IN_MILLIS);
    public static final int ACTION_TIME_OUT = (int) (2 * DateUtils.SECOND_IN_MILLIS);
    public static final int CLIENT_ACTION_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int GET_MESSAGE_STATE_TIME_OUT = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int LAST_SEEN_DELAY_CHECKING = (int) (60 * DateUtils.SECOND_IN_MILLIS);
    public static final int HEART_BEAT_CHECKING_TIME_OUT = (int) (10 * DateUtils.SECOND_IN_MILLIS);
    public static final int UPDATING_TIME_SHOWING = (int) (2 * DateUtils.SECOND_IN_MILLIS);
    public static final int FETCH_CONTACT_TIME_OUT = (int) (5 * DateUtils.SECOND_IN_MILLIS);
    public static final int LAST_SEEN_TIME_OUT = (int) (60 * DateUtils.MINUTE_IN_MILLIS); // after this time show exactly time instead of minutes
    public static final int DEFAULT_BOTH_CHAT_DELETE_TIME = (int) (2 * DateUtils.HOUR_IN_MILLIS);

    public static final String IGAP_ACCOUNT = "iGap";
    public static final String URL_WEB_SOCKET = "wss://secure.igap.net/hybrid/";
    public static final String URL_MAP = "https://c.tile.openstreetmap.org/";
    public static final String IGAP_LINK_PREFIX = "https://iGap.net/";

    public static final String PUBLIC_KEY_CLIENT = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo+inlAfd8Qior8IMKaJ+\n"
            + "BREJcEc9J9RhHgh6g/LvHKsnMaiEbAL70jQBQTLpCRu5Cnpj20+isOi++Wtf/pIP\n"
            + "FdJbD/1H+5jS+ja0RA6unp93DnBuYZ2JjV60vF3Ynj6F4Vr1ts5Xg5dJlEaOcOO2\n"
            + "YzOU97ZGP0ozrXIT5S+Y0BC4M9ieQmlGREzt3UZlTBbyUYPS4mMFh88YcT3QTiTA\n"
            + "k897qlJLxkYxVyAgwAD/0ihmWEkBQe9IxwVT/x5/QbixGSl4Zvd+5d+9sTZcSZQS\n"
            + "iJInT4E6DcmgAVYu5jFMWJDTEuurOQZ1W4nbmGyoY1bZXaFoiMPfzy72VIddkoHg\n"
            + "mwIDAQAB\n"
            + "-----END PUBLIC KEY-----\n";

    public static final String PUBLIC_PARSIAN_KEY_CLIENT = "-----BEGIN PUBLIC KEY-----\n"
            + "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAydD3W4bmOaInos80eMOj\n"
            + "qcEJc/P4tdSJCnQg5Snfl3LZ3iMUHV5ly+cpJKUtWPrTo4afEQdTtZhlsa465GET\n"
            + "HWuQGMGg45NpieM75hOP4B7U7ZNlyaIzJzdIbkIYzdRinELhC0XbRLLG5hNRT/E3\n"
            + "Iy+Odxp6XXui4dk7pMA7DnSeGsFMWf61pbXt06ue/nSC41VJKS5KXkNbPtafzGq6\n"
            + "BjKnQxlvKH9O9RClZXr8ymxj4F7Ja3PxW//lDGGwgd3Zfq1aJMDaqmeXKEagOLfn\n"
            + "eoKJr2TzH/fUp0N6p3dWo7tw/E50CgHaO7Cqrzk2h9bgnsbQiIyVLMwAtW3TBEQf\n"
            + "b+bwafeVKAbMAj9wauJZuREGo0fNCpSo/MQWe4bH3AZmVH4b00idD04obuHDyDSx\n"
            + "E9ID20SdTEAEv98/zqTXscOZz0WdPSJHlkkiO+Kmc92mJRAn+lSTlPsepUyG0qCt\n"
            + "uJgm87qHQSyQXI0LqCaUm0WqsIIO1yFNZjJuQMO5V/i2SBzV6yRFehFWH8vfrfIG\n"
            + "FX7bj+cYugj6FTZogns8/ISHTR2njWcxSj3scKvbgvpOIpA7ZAtiyf2RRt9Ove9z\n"
            + "yUjIvBbfDBzvd4D4mAnF96OtRLJmgIZWQrgiKv7LjCyV3r/uaYV3wXuvIWHxcsXY\n"
            + "+IAhoaxUiu3JEBGeOx4xs3sCAwEAAQ==\n"
            + "-----END PUBLIC KEY-----\n";
}
