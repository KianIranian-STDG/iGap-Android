package net.iGap.module.downloader;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FileExecutors {
    private final Executor executor;
    private static FileExecutors instance;

    private FileExecutors() {
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(NUMBER_OF_CORES);
    }

    public static FileExecutors getInstance() {
        FileExecutors localInstance = instance;
        if (localInstance == null) {
            synchronized (FileExecutors.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FileExecutors();
                }
            }
        }
        return localInstance;
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}