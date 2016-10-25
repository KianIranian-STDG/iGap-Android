package com.iGap;

public class Config {

    public static String urlWebsocket = "wss://secure.igap.im/connect/"; // wss://secure.igap.im/wss/ , ws://nano.igap.im:7755 // ws://10.10.10.102:6708

    public static final int ACCEPT = 1;
    public static final int REJECT = 0;
    public static final int TIME_OUT_DELAY_MS = 1000;
    public static final int FAKE_PM_DELAY = 10000;
    public static final int TIME_OUT_MS = 10000;
    public static final int LOOKUP_MAP_RESPONSE_OFFSET = 30000;
    public static final int ALLOW_RECONNECT_AGAIN = 10000;
    public static final int REPEAT_CONNECTION_CHECKING = 1000;
    public static final int INSTANCE_SUCCESSFULLY_CHECKING = 10000;


    public enum ConnectionState {
        WAITING_FOR_NETWORK, CONNECTING, UPDATING, IGAP
    }

}
