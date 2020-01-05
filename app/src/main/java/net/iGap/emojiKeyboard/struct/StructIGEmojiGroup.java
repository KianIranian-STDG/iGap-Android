package net.iGap.emojiKeyboard.struct;

import java.util.ArrayList;
import java.util.List;

public class StructIGEmojiGroup {
    private String categoryName;
    private List<String> strings = new ArrayList<>();

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getStrings() {
        return strings;
    }
}
