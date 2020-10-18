package net.iGap.controllers;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import net.iGap.G;
import net.iGap.helper.DispatchQueue;
import net.iGap.helper.ImageObserver;

import java.util.HashMap;

public class ImageLoader {
    private static volatile ImageLoader instance;
    private LruCache<String, BitmapDrawable> memCache;
    private HashMap<String, ImageObserver> observerMap = new HashMap<>();

    private DispatchQueue loadImageQueue = new DispatchQueue("loadImageQueue");


    public ImageLoader() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        memCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull BitmapDrawable value) {
                return value.getBitmap().getByteCount() / 1024;
            }
        };
    }

    public static ImageLoader getInstance() {
        ImageLoader localInstance = instance;
        if (localInstance == null) {
            synchronized (ImageLoader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ImageLoader();
                }
            }
        }
        return localInstance;
    }

    public void loadImageForImageObserver(ImageObserver imageObserver) {

        loadImageQueue.postRunnable(new LoadImageTask());

    }

    public void cancelLoadImageForImageObserver(ImageObserver imageObserver) {

    }

    public static class LoadImageTask implements Runnable {
        private final Object sync = new Object();

        private Thread currentThread;
        private boolean isCancelled;

        @Override
        public void run() {

            synchronized (sync) {
                currentThread = Thread.currentThread();
                boolean b = Thread.interrupted();

                if (isCancelled) {
                    return;
                }
            }

            BitmapDrawable bitmapDrawable = new BitmapDrawable();
            onPostExecute(bitmapDrawable);


        }

        private void onPostExecute(final Drawable drawable) {
            G.runOnUiThread(() -> {

            });
        }


        public void cancel() {
            synchronized (sync) {
                try {
                    isCancelled = true;
                    if (currentThread != null) {
                        currentThread.interrupt();
                    }
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
}
