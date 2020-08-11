package net.iGap.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmDownloadSong extends RealmObject {
    @PrimaryKey
    private long id;
    private String path;
    private String displayName;
    private String englishDisplayName;
    private String savedName;
    private long artistId;
    private long albumId;
    private boolean isFavorite = false;

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
