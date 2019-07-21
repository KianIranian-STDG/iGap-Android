package net.iGap.module.api.beepTunes;

public class DownloadSong {
    public static final int STATUS_START = 0;
    public static final int STATUS_STOP = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_COMPLETE = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_DOWNLOADING = 5;

    private String url;
    private Long id;
    private String name;
    private int downloadStatus;

    public DownloadSong(String url, Long id, String name) {
        this.url = url;
        this.id = id;
        this.name = name;
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
}
