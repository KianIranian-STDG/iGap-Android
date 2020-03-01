package net.iGap.observers.interfaces;

import com.downloader.Error;
import com.downloader.Progress;

import net.iGap.module.api.beepTunes.DownloadSong;

public interface OnSongDownload {
    void progressDownload(DownloadSong downloadSong, Progress progress);

    void completeDownload(DownloadSong downloadSong);

    void downloadError(DownloadSong downloadSong, Error error);

    void pauseDownload(DownloadSong downloadSong);

    void startOrResume(DownloadSong downloadSong);

    void cancelDownload(DownloadSong downloadSong);
}