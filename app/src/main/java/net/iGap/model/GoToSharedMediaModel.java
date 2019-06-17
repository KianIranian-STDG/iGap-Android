package net.iGap.model;

public class GoToSharedMediaModel {
    private long roomId;
    private int type;

    public GoToSharedMediaModel(long roomId, int type) {
        this.roomId = roomId;
        this.type = type;
    }

    public long getRoomId() {
        return roomId;
    }

    public int getType() {
        return type;
    }
}
