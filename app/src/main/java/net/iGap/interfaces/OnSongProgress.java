package net.iGap.interfaces;

import net.iGap.module.api.beepTunes.DownloadSong;

public interface OnSongProgress {
    void progress(DownloadSong downloadSong);
}
