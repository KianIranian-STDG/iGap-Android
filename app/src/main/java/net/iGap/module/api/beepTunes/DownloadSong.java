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
    private String name;
    private int downloadStatus = STATUS_START;
    private int downloadProgress;
    private String path;

    public DownloadSong(String url, Long id, String name, String path) {
        this.url = url;
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public DownloadSong() {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
