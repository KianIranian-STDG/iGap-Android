
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class CardStatusDataModel {

    @SerializedName("activation")
    private Activation activation;
    @SerializedName("id")
    private String id;
    @SerializedName("sticker")
    private StickerDataModel sticker;
    @SerializedName("isForwarded")
    private boolean isForwarded;
    @SerializedName("isCardOwner")
    private boolean isCardOwner;

    public void setForwarded(boolean forwarded) {
        isForwarded = forwarded;
    }

    public boolean isForwarded() {
        return isForwarded;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StickerDataModel getSticker() {
        return sticker;
    }

    public void setSticker(StickerDataModel sticker) {
        this.sticker = sticker;
    }

    public void setCardOwner(boolean cardOwner) {
        isCardOwner = cardOwner;
    }

    public boolean isCardOwner() {
        return isCardOwner;
    }
}
