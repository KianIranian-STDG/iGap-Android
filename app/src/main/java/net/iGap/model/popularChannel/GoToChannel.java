package net.iGap.model.popularChannel;

public class GoToChannel {
    private String slug;
    private boolean isPrivate;

    public GoToChannel(String slug, boolean isPrivate) {
        this.slug = slug;
        this.isPrivate = isPrivate;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
