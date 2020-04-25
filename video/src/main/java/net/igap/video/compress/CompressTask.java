package net.igap.video.compress;

import android.media.MediaMetadataRetriever;

import net.igap.video.compress.video.MediaController;


public class CompressTask extends Thread implements MediaController.OnPercentCompress {
    private String id;
    private String originalPath;
    private String newPath;
    private OnCompress onCompress;
    private int progress;

    public CompressTask(String id, String originalPath, String newPath, OnCompress onCompress) {
        this.id = id;
        this.originalPath = originalPath;
        this.newPath = newPath;
        this.onCompress = onCompress;
        this.progress = 0;
    }

    @Override
    public void run() {
        super.run();

        if (originalPath == null || originalPath.length() == 0)
            return;

        long endTime;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(originalPath);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            endTime = Long.parseLong(durationStr);
        } catch (Exception e) {
            e.printStackTrace();
            endTime = 1;
        }

        boolean finish = MediaController.getInstance().convertVideo(originalPath, newPath, endTime, this);
        onCompress.onCompressFinish(id, finish);
    }

    @Override
    public void onProgress(int progress) {
        this.progress = progress;
        onCompress.onCompressProgress(id, progress);
    }

    public int getProgress() {
        return progress;
    }
}
