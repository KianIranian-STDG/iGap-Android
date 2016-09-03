package com.iGap.module;

/**
 * Created by android3 on 8/2/2016.
 */
public class MyType {

    public static final int singleChat = 1;
    public static final int groupChat = 2;
    public static final int channel = 3;

    public static final int registered = 1;
    public static final int notRegistered = 2;
    public static final int line = 3;

    public static final int none = 0;
    public static final int message = 1;
    public static final int image = 2;
    public static final int video = 3;
    public static final int files = 4;
    public static final int position = 5;
    public static final int audio = 6;
    public static final int gif = 7;
    public static final int sticker = 8;

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

    public enum ChatType {

        singleChat(MyType.singleChat),
        groupChat(MyType.groupChat),
        channel(MyType.channel);

        private int value;

        ChatType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum MessageType { //TODO [Saeed Mozaffari] [2016-09-01 10:31 AM] - Change Type Message to ProtoGlobal.RoomMessageType

        none(MyType.none),
        message(MyType.message),
        image(MyType.image),
        video(MyType.video),
        files(MyType.files),
        position(MyType.position),
        audio(MyType.audio),
        gif(MyType.gif),
        sticker(MyType.sticker);

        private int value;

        MessageType(int value) {
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
