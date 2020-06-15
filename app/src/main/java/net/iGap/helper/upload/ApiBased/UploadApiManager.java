package net.iGap.helper.upload.ApiBased;

import androidx.collection.ArrayMap;

import net.iGap.helper.upload.CompressTask;
import net.iGap.viewmodel.controllers.CallManager;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UploadApiManager {

    private static volatile UploadApiManager instance = null;

    private ThreadPoolExecutor mThreadPoolExecutor;

    private ArrayMap<String, UploadApiTask> pendingUploadTasks;
    private ArrayMap<String, CompressTask> pendingCompressTasks;

    public static UploadApiManager getInstance() {
        UploadApiManager localInstance = instance;
        if (localInstance == null) {
            synchronized (CallManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UploadApiManager();
                }
            }
        }
        return localInstance;
    }

    public UploadApiManager() {
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        mThreadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,   // Initial pool size
                NUMBER_OF_CORES + 3,   // Max pool size
                3,       // Time idle thread waits before terminating
                TimeUnit.SECONDS,  // Sets the Time Unit for KEEP_ALIVE_TIME
                new LinkedBlockingDeque<>());  // Work Queue

        pendingUploadTasks = new ArrayMap<>();
        pendingCompressTasks = new ArrayMap<>();
    }

}
