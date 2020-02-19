package net.iGap.model.mobileBank;

public class MobileBankHomeItemsModel {
    private int title;
    private int icon;

    public MobileBankHomeItemsModel(int title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}