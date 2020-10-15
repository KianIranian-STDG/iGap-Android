package net.iGap.module.downloader;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FileIOExecutor {
    private final Executor executor;
    private static FileIOExecutor instance;

    private FileIOExecutor() {
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(NUMBER_OF_CORES);
    }

    public static FileIOExecutor getInstance() {
        FileIOExecutor localInstance = instance;
        if (localInstance == null) {
            synchronized (FileIOExecutor.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FileIOExecutor();
                }
            }
        }
        return localInstance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}