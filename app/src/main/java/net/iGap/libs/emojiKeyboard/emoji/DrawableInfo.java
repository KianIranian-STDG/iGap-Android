package net.iGap.libs.emojiKeyboard.emoji;

public class DrawableInfo {
    byte categoryIndex;
    short emojiCategoryIndex;
    int emojiIndex;

    DrawableInfo(byte categoryIndex, short emojiCategoryIndex, int emojiIndex) {
        this.categoryIndex = categoryIndex;
        this.emojiCategoryIndex = emojiCategoryIndex;
        this.emojiIndex = emojiIndex;
    }
}