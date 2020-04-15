package net.iGap.realm;

import io.realm.RealmObject;

public class RealmPostMessageRights extends RealmObject {
    private boolean canSendText;
    private boolean canSendMedia;
    private boolean canSendGif;
    private boolean canSendSticker;
    private boolean canSendLink;

    public void setPostMessage(boolean value) {
        setCanSendGif(value);
        setCanSendLink(value);
        setCanSendMedia(value);
        setCanSendSticker(value);
        setCanSendText(value);
    }

    public boolean canPostMessage() {
        return canSendGif && canSendMedia && canSendLink && canSendSticker && canSendText;
    }

    public boolean isCanSendText() {
        return canSendText;
    }

    public void setCanSendText(boolean canSendText) {
        this.canSendText = canSendText;
    }

    public boolean isCanSendMedia() {
        return canSendMedia;
    }

    public void setCanSendMedia(boolean canSendMedia) {
        this.canSendMedia = canSendMedia;
    }

    public boolean isCanSendGif() {
        return canSendGif;
    }

    public void setCanSendGif(boolean canSendGif) {
        this.canSendGif = canSendGif;
    }

    public boolean isCanSendSticker() {
        return canSendSticker;
    }

    public void setCanSendSticker(boolean canSendSticker) {
        this.canSendSticker = canSendSticker;
    }

    public boolean isCanSendLink() {
        return canSendLink;
    }

    public void setCanSendLink(boolean canSendLink) {
        this.canSendLink = canSendLink;
    }
}
