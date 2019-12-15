package net.iGap.fragments.emoji.struct;

import java.io.File;

public class StructIGSticker {
    public static final int NORMAL_STICKER = 0;
    public static final int ANIMATED_STICKER = 1;

    private String path;
    private String name;
    private int type;
    private String id;
    private String token;
    private String groupId;
    private String fileName;
    private int fileSize;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        if (path == null || path.equals(""))
            type = 100;
        else if (path.endsWith(".json")) {
            type = ANIMATED_STICKER;
        } else {
            type = NORMAL_STICKER;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public boolean hasFileOnLocal() {
        return new File(path).exists() && new File(path).canRead();
    }
}