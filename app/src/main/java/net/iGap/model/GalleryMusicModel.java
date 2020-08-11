package net.iGap.model;

import androidx.annotation.Nullable;

public class GalleryMusicModel {

    private int id;
    private String title;
    private String artist;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryMusicModel) {
            return ((GalleryMusicModel) obj).getId() == this.id;
        }
        return super.equals(obj);
    }
}
