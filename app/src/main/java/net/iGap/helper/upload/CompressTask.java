package net.iGap.helper.upload;

import android.media.MediaMetadataRetriever;

import net.iGap.helper.HelperLog;
import net.igap.video.compress.OnCompress;
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

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(originalPath);
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }

        boolean finish = MediaController.getInstance().convertVideo(originalPath, newPath, this);
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
