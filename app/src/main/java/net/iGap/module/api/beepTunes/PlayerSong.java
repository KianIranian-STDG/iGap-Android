package net.iGap.module.api.beepTunes;

import net.iGap.realm.RealmDownloadSong;

public class PlayerSong {
    public static final int PLAY = 0;
    public static final int PAUSE = 0;
    public static final int STOP = 0;

    private int Status;
    private RealmDownloadSong song;

    public PlayerSong(int status, RealmDownloadSong song) {
        Status = status;
        this.song = song;
    }

    public RealmDownloadSong getSong() {
        return song;
    }

    public void setSong(RealmDownloadSong song) {
        this.song = song;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
