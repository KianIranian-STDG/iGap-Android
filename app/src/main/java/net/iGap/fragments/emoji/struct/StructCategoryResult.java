package net.iGap.fragments.emoji.struct;

public class StructCategoryResult {
    private StickerCategory[] data;

    private boolean ok;

    public StickerCategory[] getStickerCategories() {
        return data;
    }

    public void setStickerCategories(StickerCategory[] stickerCategories) {
        this.data = stickerCategories;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    @Override
    public String toString() {
        return "StructCategoryResult [stickerCategories = , ok = " + ok + "]";
    }

}
