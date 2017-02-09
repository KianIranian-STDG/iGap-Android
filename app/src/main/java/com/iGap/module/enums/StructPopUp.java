package com.iGap.module.enums;

/**
 * Created by android3 on 2/9/2017.
 */

public class StructPopUp {

    private Long roomId;
    private String message;
    private String roomType;

    public StructPopUp(Long roomId) {
        this.roomId = roomId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
