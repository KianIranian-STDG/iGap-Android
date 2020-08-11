package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Purchase {

    @SerializedName("album")
    private Album mAlbum;
    @SerializedName("albumId")
    private Long mAlbumId;
    @SerializedName("artists")
    private List<Artist> mArtists;
    @SerializedName("created")
    private Long mCreated;
    @SerializedName("downloadLinks")
    private DownloadLinks mDownloadLinks;
    @SerializedName("durationMinutes")
    private Long mDurationMinutes;
    @SerializedName("durationSeconds")
    private Long mDurationSeconds;
    @SerializedName("englishName")
    private String mEnglishName;
    @SerializedName("id")
    private Long mId;
    @SerializedName("image")
    private String mImage;
    @SerializedName("lyrics")
    private String mLyrics;
    @SerializedName("name")
    private String mName;
    @SerializedName("previewUrl")
    private String mPreviewUrl;
    @SerializedName("price")
    private Long mPrice;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("trackNumber")
    private Long mTrackNumber;
    @SerializedName("type")
    private String mType;

    public Album getAlbum() {
        return mAlbum;
    }

    public void setAlbum(Album album) {
        mAlbum = album;
    }

    public Long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(Long albumId) {
        mAlbumId = albumId;
    }

    public List<Artist> getArtists() {
        return mArtists;
    }

    public void setArtists(List<Artist> artists) {
        mArtists = artists;
    }

    public Long getCreated() {
        return mCreated;
    }

    public void setCreated(Long created) {
        mCreated = created;
    }

    public DownloadLinks getDownloadLinks() {
        return mDownloadLinks;
    }

    public void setDownloadLinks(DownloadLinks downloadLinks) {
        mDownloadLinks = downloadLinks;
    }

    public Long getDurationMinutes() {
        return mDurationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        mDurationMinutes = durationMinutes;
    }

    public Long getDurationSeconds() {
        return mDurationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        mDurationSeconds = durationSeconds;
    }

    public String getEnglishName() {
        return mEnglishName;
    }

    public void setEnglishName(String englishName) {
        mEnglishName = englishName;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getLyrics() {
        return mLyrics;
    }

    public void setLyrics(String lyrics) {
        mLyrics = lyrics;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        mPreviewUrl = previewUrl;
    }

    public Long getPrice() {
        return mPrice;
    }

    public void setPrice(Long price) {
        mPrice = price;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public Long getTrackNumber() {
        return mTrackNumber;
    }

    public void setTrackNumber(Long trackNumber) {
        mTrackNumber = trackNumber;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
