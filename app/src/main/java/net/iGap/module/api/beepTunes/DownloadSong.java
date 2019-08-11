package net.iGap.module.api.beepTunes;

public class DownloadSong {
    public static final int STATUS_START = 0;
    public static final int STATUS_CANCEL = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_COMPLETE = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_DOWNLOADING = 5;

    private String url;
    private Long id;
    private int downloadId;
    private String savedName;
    private int downloadStatus = STATUS_START;
    private int downloadProgress;
    private String path;
    private Track track;
    private Long artistId;
    private Long albumId;

    public DownloadSong(String url, Track track, String savedName, String path) {
        this.url = url;
        this.id = track.getId();
        this.track = track;
        this.savedName = savedName;
        this.path = path;
        this.artistId = track.getArtists().get(0).getId();
        this.albumId = track.getAlbumId();
    }

    public DownloadSong() {

    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String path) {
        this.url = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSavedName() {
        return savedName;
    }

    public void setSavedName(String savedName) {
        this.savedName = savedName;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
