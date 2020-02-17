package net.iGap.fragments.emoji.struct;

import net.iGap.fragments.emoji.apiModels.CardStatusDataModel;
import net.iGap.fragments.emoji.apiModels.UserStickers;

public class StructIGGiftSticker {
    private static final String NEW = "NEW";
    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";
    private static final String DELETED = "DELETED";

    private String giftId;
    private StructIGSticker structIGSticker;
    private String status;
    private boolean isActive;
    private String rrn;
    private boolean isForward;
    private boolean isValid;
    private String toUserId;
    private String fromUserId;
    private String createdAt;
    private boolean isCardOwner;

    public StructIGGiftSticker(UserStickers userStickers) {
        structIGSticker = new StructIGSticker(userStickers.getSticker());
        status = userStickers.getActivation().getStatus();
        rrn = userStickers.getRrn();
        giftId = userStickers.getId();
        structIGSticker.setGiftId(giftId);
        toUserId = userStickers.getToUserId();
        fromUserId = userStickers.getFromUserId();
        createdAt = userStickers.getCreatedAt();
    }

    public StructIGGiftSticker(CardStatusDataModel dataModel) {
        structIGSticker = new StructIGSticker(dataModel.getSticker());
        isActive = dataModel.getActivation().getStatus().equals(ACTIVE);
        isValid = dataModel.getActivation().getStatus().equals(NEW);
        giftId = dataModel.getId();
        isForward = dataModel.isForwarded();
        isCardOwner = dataModel.isCardOwner();
    }

    public boolean isForward() {
        return isForward;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStatus() {
        return status;
    }

    public StructIGSticker getStructIGSticker() {
        return structIGSticker;
    }

    public String getRrn() {
        return rrn;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getGiftId() {
        return giftId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCardOwner() {
        return isCardOwner;
    }

    public void setCardOwner(boolean cardOwner) {
        isCardOwner = cardOwner;
    }
}
