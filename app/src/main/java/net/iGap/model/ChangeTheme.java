package net.iGap.model;

public class ChangeTheme {
    private int config;
    private boolean isDark;

    public ChangeTheme(int config, boolean isDark) {
        this.config = config;
        this.isDark = isDark;
    }

    public int getConfig() {
        return config;
    }

    public boolean isDark() {
        return isDark;
    }
}
