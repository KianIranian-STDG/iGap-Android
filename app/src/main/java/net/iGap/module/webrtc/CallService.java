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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.CallActivity;
import net.iGap.helper.HelperLog;
import net.iGap.model.AccountUser;
import net.iGap.module.AttachFile;
import net.iGap.module.CallActionsReceiver;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.CallState;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAvatar;
import net.iGap.viewmodel.controllers.CallManager;

import java.util.Set;

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
    private MediaPlayer player;
    private Vibrator vibrator;

    private CallAudioManager appRTCAudioManager = null;
    private CallAudioManager.AudioManagerEvents audioManagerEvents;

    private static CallService instance;

    private NotificationManager notificationManager;

    private CallManager.CallStateChange callStateChange;

    private String TAG = "iGapCall " + getClass().getSimpleName();

    public static CallService getInstance() {
        return instance;
    }

    public CallService() {

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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand: " + intent + flags + startId);

        if (instance != null) {
            Log.e(TAG, "try to restart live service");
            return START_NOT_STICKY;
        }

        if (intent == null) {
            Log.i(TAG, "onStartCommand intent null");
            stopSelf();
        }

        if (intent != null) {
            callType = ProtoSignalingOffer.SignalingOffer.Type.valueOf(intent.getStringExtra(CALL_TYPE));
            userId = intent.getLongExtra(USER_ID, -1);
            isVoiceCall = callType.equals(ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
            isIncoming = intent.getBooleanExtra(IS_INCOMING, false);
        }

        instance = this;
        CallManager.getInstance().setOnCallStateChanged(this);
        initialAudioManager();

        if (G.currentActivity instanceof ActivityMain) {
            Intent activityIntent = new Intent(this, CallActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activityIntent);
        }

        if (isIncoming) {
            playSoundAndVibration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showIncomingNotification();
            } else {
                try {
                    PendingIntent.getActivity(CallService.this, 2215, new Intent(CallService.this, CallActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0).send();
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

    // functions for playing audio files in different stages of call

    private void initialAudioManager() {
        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        appRTCAudioManager = CallAudioManager.create(getApplicationContext());
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        // This method will be called each time the number of available audio
        // devices has changed.
        appRTCAudioManager.start(new CallAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(CallAudioManager.AudioDevice selectedAudioDevice, Set<CallAudioManager.AudioDevice> availableAudioDevices) {
                if (audioManagerEvents != null)
                    audioManagerEvents.onAudioDeviceChanged(selectedAudioDevice, availableAudioDevices);
                CallManager.getInstance().setActiveAudioDevice(selectedAudioDevice);
            }
        });
        if (isVoiceCall)
            setAudioDevice(CallAudioManager.AudioDevice.EARPIECE);
    }

    private void playSoundAndVibration() {
        boolean canPlay = false;
        switch (appRTCAudioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                canPlay = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                canPlay = false;

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 100, 1000};
                vibrator.vibrate(pattern, 0);

                break;
            case AudioManager.RINGER_MODE_NORMAL:
                canPlay = true;
                break;
        }

        if (appRTCAudioManager.hasWiredHeadset()) {
            canPlay = true;
        }

        if (canPlay) {

            try {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                String path = null;

                try {
                    path = AttachFile.getFilePathFromUri(alert);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

                if (player == null) {
                    player = new MediaPlayer();
                } else {
                    if (player.isPlaying()) {
                        player.stop();
                        player.reset();
                    }
                }

                if (path == null) {
                    player.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tone));
                } else {
                    player.setDataSource(this, alert);
                }

                if (appRTCAudioManager.hasWiredHeadset()) {
                    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                } else {
                    player.setAudioStreamType(AudioManager.STREAM_RING);
                }

                player.setLooping(true);
                player.prepare();
                player.start();
            } catch (Exception e) {
                HelperLog.setErrorLog(e);
            }
        }
    }

    public void stopSoundAndVibrate() {
        try {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (vibrator != null) {
                vibrator.cancel();
                vibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundWithRes(int sound, boolean isLoopActive) {
        playSoundWithUri(Uri.parse("android.resource://" + getPackageName() + "/" + sound), isLoopActive);
    }

    public void playSoundWithUri(Uri sound, boolean isLoopActive) {
        if (player == null) {
            player = new MediaPlayer();
        } else {
            if (player.isPlaying()) {
                player.stop();
                player.reset();
            }
        }
        try {
            player.setDataSource(this, sound);
            player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            if (isLoopActive)
                player.setLooping(true);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        Uri soundProviderUri = Uri.parse("content://" + getPackageName() + "/" + R.raw.tone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CALL_CHANNEL, CALL_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build();
            channel.setSound(soundProviderUri, attrs);
            channel.enableVibration(false);
            channel.enableLights(false);
            builder.setLights(Color.RED, 1000, 1000);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSound(soundProviderUri, AudioManager.STREAM_RING);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(0xff4CAF50);
            builder.setVibrate(new long[]{200, 200, 200, 200, 200});
            builder.setCategory(Notification.CATEGORY_CALL);
            builder.setFullScreenIntent(PendingIntent.getActivity(this, 0, intent, 0), true);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(false);
        }

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

        Log.i(TAG, "showNotification: " + callerInfo.getName());

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
            endIntent.setAction(ACTION_END_CALL);
            NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_call_notif_decline, getResources().getString(isVoiceCall ? R.string.end_voice_call_icon : R.string.end_video_call_icon), PendingIntent.getBroadcast(this, 0, endIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            builder.addAction(action);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(false);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CALL_CHANNEL, CALL_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(notificationChannel.getId());
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

        if (appRTCAudioManager != null) {
            appRTCAudioManager.stop();
            appRTCAudioManager = null;
        }

        stopForeground(true);

        stopSoundAndVibrate();

        callStateChange = null;
        instance = null;

        CallManager.getInstance().cleanUp();

        Log.i(TAG, "-----------------------------------------------------");
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

    public void onBroadcastReceived(Intent intent) {
        if (instance != null && intent != null && intent.getAction() != null) {
            Log.i(TAG, "onBroadcastReceived: " + intent.getAction());

            stopSoundAndVibrate();

            if (intent.getAction().equals(ACTION_ANSWER_CALL)) {
                CallManager.getInstance().acceptCall();

                showNotification();

                Intent activityIntent = new Intent(this, CallActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(activityIntent);

            } else if (intent.getAction().equals(ACTION_DECLINE_CALL) || intent.getAction().equals(ACTION_END_CALL)) {
                CallManager.getInstance().endCall();
                Log.i(getClass().getSimpleName(), "onBroadcastReceived ACTION_DECLINE_CALL");
                onDestroy();
            }
        }
    }

    @Override
    public void onCallStateChanged(CallState state) {
        Log.i(TAG, "onCallStateChanged: " + state);

        if (callStateChange != null)
            callStateChange.onCallStateChanged(state);

        if (state == CallState.CONNECTED) {
            stopSoundAndVibrate();
        }

        if (state == CallState.DISCONNECTED) {
            playSoundWithRes(R.raw.igap_discounect, false);
        }

        if (state == CallState.REJECT || state == CallState.FAILD || state == CallState.TOO_LONG || state == CallState.LEAVE_CALL || state == CallState.UNAVAILABLE || state == CallState.DISCONNECTED || state == CallState.NOT_ANSWERED) {
            stopSelf();
        }
    }

    @Override
    public void onError(int messageID, int major, int minor) {
        if (callStateChange != null)
            callStateChange.onError(messageID, major, minor);
    }

    // related functions for controlling audio device within call

    public void setAudioDevice(CallAudioManager.AudioDevice selectedAudioDevice) {
        appRTCAudioManager.selectAudioDevice(selectedAudioDevice);
    }

    public CallAudioManager.AudioDevice getActiveAudioDevice() {
        return appRTCAudioManager.getSelectedAudioDevice();
    }

    public void toggleSpeaker() {
        appRTCAudioManager.toggleSpeakerPhone();
    }

    public boolean isSpeakerEnable() {
        return appRTCAudioManager.isSpeakerOn();
    }

    public void setAudioManagerEvents(CallAudioManager.AudioManagerEvents audioManagerEvents) {
        this.audioManagerEvents = audioManagerEvents;
    }
}
