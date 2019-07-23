package net.iGap.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmDownloadSong extends RealmObject {
    @PrimaryKey
    private Long id;
    private String path;
    private String imagePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
