package net.iGap.module.webrtc;

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
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.iGap.BuildConfig;
import net.iGap.R;
import net.iGap.activities.CallActivity;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.model.AccountUser;
import net.iGap.module.CallActionsReceiver;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.CallState;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAvatar;
import net.iGap.viewmodel.controllers.CallManager;

public class CallService extends Service implements EventListener, CallManager.CallStateChange {
    private final int ID_SERVICE_NOTIFICATION = 2213;
    private final int ID_INCOMING_NOTIFICATION = 2214;
    private final String CALL_CHANNEL = "iGapCall";

    private final String ACTION_END_CALL = "net.igap.call.end";
    private final String ACTION_ANSWER_CALL = "net.igap.call.answer";
    private final String ACTION_DECLINE_CALL = "net.igap.call.decline";

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

    private CallManager.CallStateChange callStateChange;
    private String TAG = "abbasiCall" + " Service";

    public static CallService getInstance() {
        return instance;
    }

    public CallService() {
        avatarHandler = new AvatarHandler();
    }

    public void setCallStateChange(CallManager.CallStateChange callStateChange) {
        this.callStateChange = callStateChange;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (instance != null) {
            Log.e(TAG, "try to restart live service");
            return START_NOT_STICKY;
        }

        if (intent != null) {
            callType = ProtoSignalingOffer.SignalingOffer.Type.valueOf(intent.getStringExtra(CALL_TYPE));
            userId = intent.getLongExtra(USER_ID, -1);
            isVoiceCall = callType.equals(ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
            isIncoming = intent.getBooleanExtra(IS_INCOMING, false);
        }

        instance = this;
        CallManager.getInstance().setOnCallStateChanged(this);

        if (isIncoming) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showIncomingNotification();
            } else {
                try {
                    PendingIntent.getActivity(CallService.this, 2214, new Intent(CallService.this, CallActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0).send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            CallManager.getInstance().startCall(userId, callType);
            showNotification();
            Intent activityIntent = new Intent(this, CallActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activityIntent);
        }

        return START_STICKY;
    }

    @SuppressLint("WrongConstant")
    private void showIncomingNotification() {
        CallerInfo callerInfo = CallManager.getInstance().getCurrentCallerInfo();
        Intent intent = new Intent(this, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2218, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CALL_CHANNEL)
                .setSmallIcon(R.drawable.igap_flat_icon)
                .setContentTitle(getResources().getString(isVoiceCall ? R.string.voice_calls : R.string.video_calls))
                .setContentText(callerInfo.getName())
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Uri soundProviderUri = Uri.parse("content://" + BuildConfig.APPLICATION_ID + "/igap_signaling");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CALL_CHANNEL, CALL_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
            channel.setSound(soundProviderUri, attrs);
            channel.enableVibration(false);
            channel.enableLights(false);
            builder.setColor(0x4CAF50);
            builder.setVibrate(new long[]{200, 200, 200, 200, 200});
            builder.setLights(Color.RED, 1000, 1000);
            builder.setFullScreenIntent(PendingIntent.getActivity(this, 0, intent, 0), true);
            builder.setCategory(Notification.CATEGORY_CALL);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        Intent endIntent = new Intent(this, CallActionsReceiver.class);
        endIntent.setAction(ACTION_DECLINE_CALL);
        endIntent.putExtra("callerId", callerInfo.getUserId());
        CharSequence endTitle = getResources().getString(isVoiceCall ? R.string.end_voice_call_icon : R.string.end_video_call_icon);
        PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, 0, endIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.addAction(R.drawable.ic_call_notif_decline, endTitle, endPendingIntent);

        Intent answerIntent = new Intent(this, CallActionsReceiver.class);
        answerIntent.setAction(ACTION_ANSWER_CALL);
        answerIntent.putExtra("callerId", callerInfo.getUserId());
        CharSequence answerTitle = getResources().getString(isVoiceCall ? R.string.end_voice_call_icon : R.string.end_video_call_icon);
        PendingIntent answerPendingIntent = PendingIntent.getBroadcast(this, 0, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.addAction(R.drawable.ic_call_notif_answer, answerTitle, answerPendingIntent);


        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.call_notification);

            notificationView.setTextViewText(R.id.tv_call_callerName, callerInfo.getName());
            notificationView.setTextViewText(R.id.tv_call_type, getResources().getString(isVoiceCall ? R.string.voice_calls : R.string.video_calls));

            AccountUser currentUser = AccountManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                notificationView.setTextViewText(R.id.tv_call_account, currentUser.getName());
            }

            notificationView.setImageViewBitmap(R.id.iv_call_callerAvatar, getAvatarBitmap());

            notificationView.setOnClickPendingIntent(R.id.btn_call_answer, answerPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.btn_call_decline, endPendingIntent);

            notification.headsUpContentView = notification.bigContentView = notificationView;
        }

        startForeground(ID_INCOMING_NOTIFICATION, notification);
    }

    @SuppressLint("WrongConstant")
    private void showNotification() {
        CallerInfo callerInfo = CallManager.getInstance().getCurrentCallerInfo();

        Log.i(TAG, "showNotification: " + callerInfo.toString());

        Intent intent = new Intent(this, CallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CALL_CHANNEL)
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
            NotificationChannel channel = new NotificationChannel(CALL_CHANNEL, CALL_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");

        stopForeground(true);

        callStateChange = null;
        instance = null;

        CallManager.getInstance().cleanUp();

        Log.i(TAG, "-----------------------------------------------------");
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

    public void onBroadcastReceived(Intent intent) {
        if (instance != null && intent.getAction() != null)
            Log.i(TAG, "onBroadcastReceived: " + intent.getAction());
        if (intent.getAction().equals(ACTION_ANSWER_CALL)) {
            CallManager.getInstance().acceptCall();
        } else if (intent.getAction().equals(ACTION_DECLINE_CALL) || intent.getAction().equals(ACTION_END_CALL)) {
            CallManager.getInstance().endCall();
            Log.i(getClass().getSimpleName(), "onBroadcastReceived ACTION_DECLINE_CALL");
            onDestroy();
        }
    }

    @Override
    public void onCallStateChanged(CallState callState) {
        Log.i(TAG, "onCallStateChanged: " + callState);

        if (callStateChange != null)
            callStateChange.onCallStateChanged(callState);

        if (callState == CallState.REJECT) {
            onDestroy();
        } else if (callState == CallState.LEAVE_CALL) {
            onDestroy();
        }
    }

    @Override
    public void onError(int messageID, int major, int minor) {
        if (callStateChange != null)
            callStateChange.onError(messageID, major, minor);
    }
}
