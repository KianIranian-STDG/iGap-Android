package net.iGap.module.api.beepTunes;

public class DownloadSong {
    private String url;
    private Long id;
    private String name;

    public DownloadSong(String url, Long id, String name) {
        this.url = url;
        this.id = id;
        this.name = name;
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
