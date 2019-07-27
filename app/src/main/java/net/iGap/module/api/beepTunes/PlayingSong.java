package net.iGap.module.api.beepTunes;

import android.graphics.Bitmap;

public class PlayingSong {
    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int STOP = 2;

    private long songId;
    private int status;
    private String artistName;
    private String albumName;
    private String title;
    private String songPath;
    private Bitmap bitmap;
    private boolean play = false;

    public boolean isPlay() {
        return play;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setImageData(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        play = status == PLAY;
        this.status = status;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumeName() {
        return albumName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
