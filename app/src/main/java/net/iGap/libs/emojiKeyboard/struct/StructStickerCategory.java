package net.iGap.libs.emojiKeyboard.struct;

import androidx.annotation.DrawableRes;

import net.iGap.fragments.emoji.struct.StructIGStickerGroup;

public class StructStickerCategory {
    public static final int DRAWABLE = 1001;

    private int type;
    private StructIGStickerGroup structIGStickerGroup;
    @DrawableRes
    private int resId;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public StructIGStickerGroup getStructIGStickerGroup() {
        return structIGStickerGroup;
    }

    public void setStructIGStickerGroup(StructIGStickerGroup structIGStickerGroup) {
        this.structIGStickerGroup = structIGStickerGroup;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}