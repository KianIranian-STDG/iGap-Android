package net.iGap.interfaces;

import net.iGap.module.api.beepTunes.Track;

public interface OnTrackClick {
    void onDownloadClick(Track track, OnSongProgress onSongProgress);

    void onPlayClick();
}
