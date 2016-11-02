package com.iGap.module;

/**
 * Created by android3 on 8/2/2016.
 */
public class MyType {
    public static final int sendLayot = 1;
    public static final int reciveLayout = 2;
    public static final int timeLayout = 3;

    public enum SendType {

        send(MyType.sendLayot),
        recvive(MyType.reciveLayout),
        timeLayout(MyType.timeLayout);

        private int value;

        SendType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
