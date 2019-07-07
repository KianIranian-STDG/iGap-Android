package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

public class TrackInfo {

    @Expose
    private Album album;
    @Expose
    private Long albumId;
    @Expose
    private List<Artist> artists;
    @Expose
    private Long created;
    @Expose
    private DownloadLinks downloadLinks;
    @Expose
    private Long durationMinutes;
    @Expose
    private Long durationSeconds;
    @Expose
    private String englishName;
    @Expose
    private Long id;
    @Expose
    private String image;
    @Expose
    private String name;
    @Expose
    private String previewUrl;
    @Expose
    private Long price;
    @Expose
    private String status;
    @Expose
    private Long trackNumber;
    @Expose
    private String type;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public DownloadLinks getDownloadLinks() {
        return downloadLinks;
    }

    public void setDownloadLinks(DownloadLinks downloadLinks) {
        this.downloadLinks = downloadLinks;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Long trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
