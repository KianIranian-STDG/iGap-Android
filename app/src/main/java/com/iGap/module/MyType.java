package com.iGap.module;

/**
 * Created by android3 on 8/2/2016.
 */
public class MyType {
    public static final int registered = 1;
    public static final int notRegistered = 2;
    public static final int line = 3;

    public static final int sendLayot = 1;
    public static final int reciveLayout = 2;
    public static final int timeLayout = 3;

    public static final int owner = 1;
    public static final int admin = 2;
    public static final int member = 3;

    public static final int notDownload = 1;
    public static final int downloading = 2;
    public static final int downloaded = 3;
    public static final int notUpload = 4;
    public static final int uploading = 5;
    public static final int uploaded = 6;


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

    public enum ContactState {
        registered(MyType.registered),
        notRegistered(MyType.notRegistered),
        line(MyType.line);

        private int value;

        ContactState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum OwnerShip {

        owner(MyType.owner),
        admin(MyType.admin),
        member(MyType.member);

        private int value;

        OwnerShip(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum FileState {

        notDownload(MyType.notDownload),
        downloading(MyType.downloading),
        downloaded(MyType.downloaded),
        notUpload(MyType.notUpload),
        uploading(MyType.uploading),
        uploaded(MyType.uploaded);


        private int value;

        FileState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
