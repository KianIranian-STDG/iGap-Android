package net.iGap.module.webrtc;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.viewmodel.controllers.CallManager;

public class CallService extends Service implements EventListener {
    private static final int ID_NOTIFICATION = 102;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("WrongConstant")
    private void createNotification() {
        CallerInfo callerInfo = CallManager.getInstance().getCurrentCallerInfo();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean isAlive = CallManager.getInstance().isCallAlive();

            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.igap_flat_icon)
                    .setOngoing(isAlive)
                    .setContentTitle(callerInfo.name)
                    .setShowWhen(false)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setCategory(Notification.CATEGORY_CALL)
                    .setPriority(Notification.PRIORITY_MAX);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel("iGapCall", "iGapCall", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(mChannel);
                builder.setChannelId(mChannel.getId());
            }

            if (callerInfo.getAvatar() != null) {
                builder.setLargeIcon(callerInfo.getAvatar());
            }

            Notification notification = builder.build();

            if (isAlive) {
                stopForeground(false);
                notificationManager.notify(ID_NOTIFICATION, notification);
            } else {
                startForeground(ID_NOTIFICATION, notification);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

}
