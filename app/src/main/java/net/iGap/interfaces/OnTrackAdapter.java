package net.iGap.interfaces;

import net.iGap.adapter.beepTunes.AlbumTrackAdapter;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.realm.RealmDownloadSong;

public interface OnTrackAdapter {
    void onDownloadClick(Track track, AlbumTrackAdapter.OnSongProgress onSongProgress);

    void onPlayClick(RealmDownloadSong realmDownloadSong, AlbumTrackAdapter.OnSongPlay onSongPlay);
}
