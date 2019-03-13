package net.iGap.helper;

import android.os.Handler;
import android.os.Looper;

public class LooperThreadHelper {
    private static final LooperThreadHelper ourInstance = new LooperThreadHelper();

    public static LooperThreadHelper getInstance() {
        return ourInstance;
    }

    private Handler handler;

    private LooperThreadHelper() {
        initHandler();
    }

    public Handler getHandler(){
        return handler;
    }

    private void initHandler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                if (handler == null) {
                    handler = new Handler();
                }

                Looper.loop();
            }
        }).start();

    }
}
