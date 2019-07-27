package net.iGap.module;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.iGap.module.api.beepTunes.PlayingSong;

public class BeepTunesPlayerService extends Service {
    private static final String TAG = "aabolfazlService";
    private static boolean serviceRunning = false;
    private BeepTunesBinder binder = new BeepTunesBinder();
    private ServiceUpdate serviceUpdate;

    public static boolean isServiceRunning() {
        return serviceRunning;
    }

    public void setServiceUpdate(ServiceUpdate serviceUpdate) {
        this.serviceUpdate = serviceUpdate;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        serviceRunning = true;
        Log.i(TAG, "onCreate call");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
        Log.i(TAG, "onDestroy call");
        super.onDestroy();
    }

    public interface ServiceUpdate {
        void playingSong(PlayingSong playingSong);
    }

    public class BeepTunesBinder extends Binder {
        public BeepTunesPlayerService getService() {
            return BeepTunesPlayerService.this;
        }
    }

}
