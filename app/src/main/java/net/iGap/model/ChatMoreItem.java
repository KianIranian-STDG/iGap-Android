package net.iGap.model;

import androidx.annotation.StringRes;

public class ChatMoreItem {
    private int itemTagNumber;
    private int icon;
    private int itemTitle;

    public ChatMoreItem(int itemTagNumber, int icon, @StringRes int itemTitle) {
        this.itemTagNumber = itemTagNumber;
        this.icon = icon;
        this.itemTitle = itemTitle;
    }

    public int getItemTagNumber() {
        return itemTagNumber;
    }

    public void setItemTagNumber(int itemTagNumber) {
        this.itemTagNumber = itemTagNumber;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(int itemTitle) {
        this.itemTitle = itemTitle;
    }
}
