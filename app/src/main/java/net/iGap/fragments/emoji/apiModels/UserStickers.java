
package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

public class UserStickers {

    @SerializedName("activation")
    private Activation activation;
    @SerializedName("amount")
    private Long amount;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("creation")
    private Creation creation;
    @SerializedName("id")
    private String id;
    @SerializedName("requestCount")
    private Long requestCount;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("sticker")
    private StickerDataModel sticker;
    @SerializedName("rrn")
    private String rrn;
    @SerializedName("toUserId")
    private String toUserId;
    @SerializedName("fromUserId")
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
