package net.iGap.fragments.emoji.struct;

import net.iGap.fragments.emoji.apiModels.UserStickers;

public class StructIGGiftSticker {
    private StructIGSticker structIGSticker;
    private String status;
    private String activatedAt;
    private String rrn;

    public StructIGGiftSticker(UserStickers userStickers) {
        structIGSticker = new StructIGSticker(userStickers.getSticker());
        status = userStickers.getActivation().getStatus();
        rrn = userStickers.getRrn();
    }

    public String getStatus() {
        return status;
    }

    public StructIGSticker getStructIGSticker() {
        return structIGSticker;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public String getRrn() {
        return rrn;
    }
}
