package net.iGap.interfaces;

import net.iGap.adapter.beepTunes.AlbumTrackAdapter;
import net.iGap.module.api.beepTunes.Track;

public interface OnTrackClick {
    void onDownloadClick(Track track, AlbumTrackAdapter.OnSongProgress onSongProgress);

    void onPlayClick();
}
