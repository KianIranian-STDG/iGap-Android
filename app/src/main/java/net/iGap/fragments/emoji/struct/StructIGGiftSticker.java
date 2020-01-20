package net.iGap.fragments.emoji.struct;

public class StructIGGiftSticker {
    private StructIGSticker structIGSticker;
    private String status;

    public StructIGGiftSticker(StructIGSticker structIGSticker, String status) {
        this.structIGSticker = structIGSticker;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public StructIGSticker getStructIGSticker() {
        return structIGSticker;
    }
}
