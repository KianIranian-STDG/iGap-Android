package net.iGap.module.enums;

public enum Additional {


    NONE(0),
    UNDER_KEYBOARD_BUTTON(1),
    UNDER_MESSAGE_BUTTON(2),
    BUTTON_CLICK_ACTION(3),
    STICKER(4),
    GIF(5),
    STREAM_TYPE(6),
    GET_VALUE_BY_KEYBOARD_TYPE(7),
    FORM_BUILDER(8),
    WEB_VIEW(9);

    private final int shortCode;

    Additional(int code) {
        this.shortCode = code;
    }

    public int getAdditional() {
        return this.shortCode;
    }

}
