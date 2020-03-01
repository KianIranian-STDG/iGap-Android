package net.iGap.libs.emojiKeyboard.struct;

import androidx.annotation.IdRes;

import java.util.ArrayList;
import java.util.List;

public class StructIGEmojiGroup {
    @IdRes
    private int categoryName;
    private List<String> strings = new ArrayList<>();

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public int getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(int categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getStrings() {
        return strings;
    }
}
