package net.iGap.libs.emojiKeyboard.emoji.category;

public abstract class EmojiCategory {
    public String[] emojies;
    public String[] coloredEmojies;

    public abstract int getName();

    public abstract String[] getEmojies();

    public abstract String[] getColoredEmojies();

    public int getCategorySize() {
        return getEmojies().length;
    }

    public int getColoredEmojiSize() {
        return hasColored() ? getColoredEmojies().length : 0;
    }

    public boolean hasColored() {
        return getColoredEmojies() != null;
    }
}
