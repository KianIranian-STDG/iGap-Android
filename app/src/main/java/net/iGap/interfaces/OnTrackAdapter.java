package net.iGap.interfaces;

import net.iGap.adapter.beepTunes.BeepTunesTrackAdapter;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.realm.RealmDownloadSong;

public interface OnTrackAdapter {
    void onDownloadClick(Track track, BeepTunesTrackAdapter.OnSongProgress onSongProgress);

    void onPlayClick(RealmDownloadSong realmDownloadSong, BeepTunesTrackAdapter.OnSongPlay onSongPlay);
}
