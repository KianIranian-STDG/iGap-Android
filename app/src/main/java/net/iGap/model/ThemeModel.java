package net.iGap.model;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ThemeModel) {
            return this.themeId == ((ThemeModel) obj).getThemeId();
        }
        return super.equals(obj);
    }
}
