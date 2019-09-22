package net.iGap.model;

public class ThemeModel {
    private int themeId;
    private int themeNameRes;

    public ThemeModel(int themeId, int themeName) {
        this.themeId = themeId;
        this.themeNameRes = themeName;
    }

    public int getThemeId() {
        return themeId;
    }

    public int getThemeNameRes() {
        return themeNameRes;
    }
}
