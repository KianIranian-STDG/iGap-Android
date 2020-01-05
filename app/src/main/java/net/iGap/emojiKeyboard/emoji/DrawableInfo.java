package net.iGap.emojiKeyboard.emoji;

public class DrawableInfo {
    byte categoryIndex;
    short emojiIndexInCategory;
    int emojiIndex;

    DrawableInfo(byte categoryIndex, short emojiIndexInCategory, int emojiIndex) {
        this.categoryIndex = categoryIndex;
        this.emojiIndexInCategory = emojiIndexInCategory;
        this.emojiIndex = emojiIndex;
    }
}