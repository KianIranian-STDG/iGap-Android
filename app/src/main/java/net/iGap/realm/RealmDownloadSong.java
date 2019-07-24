package net.iGap.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmDownloadSong extends RealmObject {
    @PrimaryKey
    private Long id;
    private String path;
    private String displayName;
    private String englishDisplayName;
    private String savedName;
    private boolean playStatus = false;

    public boolean isPlay() {
        return playStatus;
    }

    public void setPlay(boolean playStatus) {
        this.playStatus = playStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSavedName() {
        return savedName + ".mp3";
    }

    public void setSavedName(String savedName) {
        this.savedName = savedName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEnglishDisplayName() {
        return englishDisplayName;
    }

    public void setEnglishDisplayName(String englishDisplayName) {
        this.englishDisplayName = englishDisplayName;
    }
}
