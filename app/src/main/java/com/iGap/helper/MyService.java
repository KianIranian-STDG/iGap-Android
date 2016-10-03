package com.iGap.helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.iGap.G;

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        repeat();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 24) {
            return Service.START_NOT_STICKY;
        } else {
            return Service.START_STICKY;

        }
    }

    private void repeat() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyService.this, "test services", Toast.LENGTH_SHORT).show();
                repeat();
            }
        }, 7000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent i = new Intent("stop");
        onStartCommand(i, 12, 24);
        stopSelf();

    }
}
