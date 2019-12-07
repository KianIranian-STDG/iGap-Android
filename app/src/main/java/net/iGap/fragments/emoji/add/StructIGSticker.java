package net.iGap.fragments.emoji.add;

public class StructIGSticker {
    public static final int NORMAL_STICKER = 0;
    public static final int ANIMATED_STICKER = 1;

    private String path;
    private String name;
    private int type;
    private String id;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        if (path == null || path.equals(""))
            type = 100;
        else if (path.endsWith(".json")) {
            type = ANIMATED_STICKER;
            this.path = "/storage/emulated/0/iGap/iGap Images/791d9a011707a22caf2564be154d1803586ca9fa.json";
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
}