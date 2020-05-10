package net.iGap.module;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.iGap.R;
import net.iGap.activities.CallActivity;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.webrtc.CallerInfo;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAvatar;
import net.iGap.viewmodel.controllers.CallManager;

public class CallService extends Service implements EventListener {
    private static final int ID_SERVICE_NOTIFICATION = 102;

    public static final String USER_ID = "userId";
    public static final String CALL_TYPE = "callType";
    public static final String IS_INCOMING = "isIncoming";

    private long userId;
    private boolean isIncoming;
    private ProtoSignalingOffer.SignalingOffer.Type callType;
    private boolean isVoiceCall;
    private AvatarHandler avatarHandler;

    private static CallService instance;

    private NotificationManager notificationManager;

    public static CallService getInstance() {
        return instance;
    }

    public CallService() {
        avatarHandler = new AvatarHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (instance != null) {
            Log.e(getClass().getSimpleName(), "try to restart live service");
            return START_NOT_STICKY;
        }

        if (intent != null) {
            callType = ProtoSignalingOffer.SignalingOffer.Type.valueOf(intent.getStringExtra(CALL_TYPE));
            userId = intent.getLongExtra(USER_ID, -1);
            isVoiceCall = callType.equals(ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
            isIncoming = intent.getBooleanExtra(IS_INCOMING, false);
        }

        instance = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showIncomingNotification();
        } else {
            try {
                PendingIntent.getActivity(CallService.this, 2214, new Intent(CallService.this, CallActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0).send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
    }

    protected void showIncomingNotification() {

    }


    @SuppressLint("WrongConstant")
    protected void showNotification() {
        CallerInfo callerInfo = CallManager.getInstance().getCurrentCallerInfo();

        Intent intent = new Intent(this, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "iGapCall")
                .setContentTitle("iGap secure call")
                .setContentText("in " + (getResources().getString(isVoiceCall ? R.string.voice_calls : R.string.video_calls)) + " with " + callerInfo.getName())
                .setSmallIcon(R.drawable.igap_flat_icon)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_MAX);
            Intent endIntent = new Intent(this, CallActionsReceiver.class);
            endIntent.setAction("net.iGap.END_CALL");
            NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_call_notif_decline, getResources().getString(isVoiceCall ? R.string.end_voice_call_icon : R.string.end_video_call_icon), PendingIntent.getBroadcast(this, 0, endIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            builder.addAction(action);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(false);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("iGapCall", "iGapCall", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        if (userId > 0) {
            builder.setLargeIcon(getAvatarBitmap());
        }

        startForeground(ID_SERVICE_NOTIFICATION, builder.build());
    }

    private Bitmap getAvatarBitmap() {
        return DbManager.getInstance().doRealmTask(realm -> {
            String avatarPath = null;
            RealmAvatar realmAvatarPath = RealmAvatar.getLastAvatar(userId, realm);
            if (realmAvatarPath != null) {
                if (realmAvatarPath.getFile().isFileExistsOnLocal()) {
                    avatarPath = realmAvatarPath.getFile().getLocalFilePath();
                } else if (realmAvatarPath.getFile().isThumbnailExistsOnLocal()) {
                    avatarPath = realmAvatarPath.getFile().getLocalThumbnailPath();
                }
            }
            if (avatarPath != null) {
                try {
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.outHeight = 64;
                    option.outWidth = 64;
                    return BitmapFactory.decodeFile(avatarPath, option);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        CallManager.getInstance().cleanUp();
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

}
