package net.iGap.helper.upload.ApiBased;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ManagerWorker extends Worker {

    private String id;

    private static final String PROGRESS = "PROGRESS";
    static final String COMPRESS_ID = "COMPRESS_ID";
    static final String COMPRESS_ORIGINAL_PATH = "COMPRESS_ORIGINAL_PATH";
    static final String COMPRESS_NEW_PATH = "COMPRESS_NEW_PATH";

    private static final String TAG = "Compress Worker http";

    public ManagerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }

}
