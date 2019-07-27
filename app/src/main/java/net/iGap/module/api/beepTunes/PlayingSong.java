package net.iGap.module.api.beepTunes;

import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetBehavior;

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
    private int behaviorStatus = BottomSheetBehavior.STATE_HIDDEN;
    private boolean play = false;
    private boolean fromPlayer = false;

    public boolean isPlay() {
        return play;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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

    public int getBehaviorStatus() {
        if (fromPlayer)
            return BottomSheetBehavior.STATE_EXPANDED;
        else
            return behaviorStatus;
    }

    public void setBehaviorStatus(int behaviorStatus) {
        this.behaviorStatus = behaviorStatus;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(boolean fromPlayer) {
        this.fromPlayer = fromPlayer;
    }
}
