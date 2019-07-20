package net.iGap.module.api.beepTunes;

public class ProgressSong {
    private int progress;
    private Long id;

    public ProgressSong(int progress, Long id) {
        this.progress = progress;
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
