
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.Expose;

public class UserStickers {

    @Expose
    private Activation activation;
    @Expose
    private Long amount;
    @Expose
    private String createdAt;
    @Expose
    private Creation creation;
    @Expose
    private String id;
    @Expose
    private Long requestCount;
    @Expose
    private String updatedAt;
    @Expose
    private StickerDataModel sticker;
    @Expose
    private String rrn;
    @Expose
    private String toUserId;
    @Expose
    private String fromUserId;

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getRrn() {
        return rrn;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Creation getCreation() {
        return creation;
    }

    public void setCreation(Creation creation) {
        this.creation = creation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StickerDataModel getSticker() {
        return sticker;
    }

    public void setSticker(StickerDataModel sticker) {
        this.sticker = sticker;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
}
