package net.iGap.fragments.emoji.struct;

import com.google.gson.annotations.SerializedName;

import net.iGap.fragments.emoji.apiModels.StickerDataModel;
import net.iGap.realm.RealmStickerItem;
import net.iGap.repository.StickerRepository;

import java.io.File;
import java.util.List;

public class StructIGSticker {
    //use @SerializedName for message additional data

    public static final int NORMAL_STICKER = 0;
    public static final int ANIMATED_STICKER = 1;

    @SerializedName("path")
    private String path;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private int type;
    @SerializedName("id")
    private String id;
    @SerializedName("token")
    private String token;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("fileName")
    private String fileName;
    @SerializedName("fileSize")
    private long fileSize;
    @SerializedName("isFavorite")
    private boolean isFavorite;
    @SerializedName("giftAmount")
    private long giftAmount;
    @SerializedName("giftId")
    private String giftId; // just use in gift sticker :| i can not change server data model :\
    private List<String> tags;

    public StructIGSticker() {
    }

    public StructIGSticker(StickerDataModel stickerDataModel) {
        setName(stickerDataModel.getName());
        setFileName(stickerDataModel.getFileName());
        setFileSize(stickerDataModel.getFileSize());
        setGroupId(stickerDataModel.getGroupId());
        setId(stickerDataModel.getId());
        setPath(StickerRepository.getInstance().getStickerPath(stickerDataModel.getToken(), stickerDataModel.getFileName()));
        setToken(stickerDataModel.getToken());
        setFavorite(stickerDataModel.isFavorite());
        setTags(stickerDataModel.getTags());
        setGiftAmount(stickerDataModel.getGiftAmount());
    }

    public StructIGSticker(RealmStickerItem stickerItem) {
        setName(stickerItem.getName());
        setFileName(stickerItem.getFileName());
        setFileSize(stickerItem.getFileSize());
        setGroupId(stickerItem.getGroupId());
        setId(stickerItem.getId());
        setPath(StickerRepository.getInstance().getStickerPath(stickerItem.getToken(), stickerItem.getFileName()));
        setToken(stickerItem.getToken());
        setFavorite(stickerItem.isFavorite());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        if (path == null || path.equals(""))
            type = 100;
        else if (path.endsWith(".json")) {
            type = ANIMATED_STICKER;
        } else {
            type = NORMAL_STICKER;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean hasFileOnLocal() {
        return new File(path).exists() && new File(path).canRead();
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setGiftAmount(long giftAmount) {
        this.giftAmount = giftAmount;
    }

    public long getGiftAmount() {
        return giftAmount;
    }

    public boolean isGiftSticker() {
        return giftAmount > 0;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftId() {
        return giftId;
    }
}