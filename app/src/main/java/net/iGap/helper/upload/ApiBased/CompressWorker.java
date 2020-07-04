package net.iGap.helper.upload.ApiBased;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.iGap.helper.HelperLog;
import net.iGap.observers.eventbus.EventManager;
import net.igap.video.compress.video.MediaController;

public class CompressWorker extends Worker implements MediaController.OnPercentCompress {

    private String id;

    private static final String PROGRESS = "PROGRESS";
    static final String COMPRESS_ID = "COMPRESS_ID";
    static final String COMPRESS_ORIGINAL_PATH = "COMPRESS_ORIGINAL_PATH";
    static final String COMPRESS_NEW_PATH = "COMPRESS_NEW_PATH";

    private static final String TAG = "Compress Worker http";

    public CompressWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //  fetch data from caller
        id = getInputData().getString(COMPRESS_ID);
        String originalPath = getInputData().getString(COMPRESS_ORIGINAL_PATH);
        String newPath = getInputData().getString(COMPRESS_NEW_PATH);
        // start progress
        setProgressAsync(new Data.Builder()
                .putString(COMPRESS_ID, id)
                .putInt(PROGRESS, 0)
                .build());
        // start compress process
        Data outputData = new Data.Builder()
                .putString(COMPRESS_ID, id)
                .build();

        if (originalPath == null || originalPath.length() == 0)
            return Result.failure(outputData);

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(originalPath);
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }

        boolean finish = MediaController.getInstance().convertVideo(originalPath, newPath, this);
        if (finish) {
            return Result.success(outputData);
        } else
            return Result.failure(outputData);
    }

    @Override
    public void onProgress(int percent) {
        EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPRESS, id, percent);
        setProgressAsync(new Data.Builder()
                .putString(COMPRESS_ID, id)
                .putInt(PROGRESS, percent)
                .build());
    }

}
