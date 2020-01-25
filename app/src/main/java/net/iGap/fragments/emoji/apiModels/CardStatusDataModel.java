
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class CardStatusDataModel {

    @Expose
    private Activation activation;
    @Expose
    private String id;
    @Expose
    private StickerDataModel sticker;

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

}
