package net.iGap.model.mobileBank;

import android.view.View;

public class MobileBankHomeItemsModel {
    private int title;
    private int icon;
    private int progressVisibility = View.GONE;

    public MobileBankHomeItemsModel(int title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public MobileBankHomeItemsModel(int title, int icon, int progressVisibility) {
        this.title = title;
        this.icon = icon;
        this.progressVisibility = progressVisibility;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(int progressVisibility) {
        this.progressVisibility = progressVisibility;
    }
}