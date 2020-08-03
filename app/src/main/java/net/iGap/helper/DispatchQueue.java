/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package net.iGap.helper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {

    private volatile Handler handler = null;
    private CountDownLatch syncLatch = new CountDownLatch(1);

    public DispatchQueue(final String threadName) {
        setName(threadName);
        start();
    }

    public void sendMessage(Message msg, int delay) {
        try {
            syncLatch.await();
            if (delay <= 0) {
                handler.sendMessage(msg);
            } else {
                handler.sendMessageDelayed(msg, delay);
            }
        } catch (Exception ignore) {

        }
    }

    public void cancelRunnable(Runnable runnable) {
        try {
            syncLatch.await();
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void cancelRunnables(Runnable[] runnables) {
        try {
            syncLatch.await();
            for (int i = 0; i < runnables.length; i++) {
                handler.removeCallbacks(runnables[i]);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void postRunnable(Runnable runnable) {
        postRunnable(runnable, 0);
    }

    public void postRunnable(Runnable runnable, long delay) {
        try {
            syncLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (delay <= 0) {
            handler.post(runnable);
        } else {
            handler.postDelayed(runnable, delay);
        }
    }

    public void cleanupQueue() {
        try {
            syncLatch.await();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void handleMessage(Message inputMessage) {

    }

    public void recycle() {
        handler.getLooper().quit();
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                DispatchQueue.this.handleMessage(msg);
            }
        };
        syncLatch.countDown();
        Looper.loop();
    }
}
