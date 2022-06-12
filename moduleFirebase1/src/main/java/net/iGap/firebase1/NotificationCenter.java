package net.iGap.firebase1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationCenter extends FirebaseMessagingService {

    private static final String TAG = "NotificationCenter";
    private OnTokenReceived onTokenReceived;
    private NotificationCenterDelegate notificationCenterDelegate;
    private static NotificationCenter instance;

    public static NotificationCenter getInstance() {
        if (instance == null) {
            synchronized (NotificationCenter.class) {
                if (instance == null) {
                    instance = new NotificationCenter();
                }
            }
        }
        return instance;
    }

    private NotificationCenter() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(installationTokenResult -> {
                    if (installationTokenResult != null) {
                        Log.e(TAG, "OnSuccessListener: " + installationTokenResult.getToken());
                        if (onTokenReceived != null) {
                            onTokenReceived.tokenReceived(installationTokenResult.getToken());
                        }
                    }

                }).addOnFailureListener(e -> {
            Log.e(TAG, "OnFailureListener: " + e.getMessage());
        });
    }

    public void setOnTokenReceived(OnTokenReceived tokenReceived) {
        this.onTokenReceived = tokenReceived;
    }

    public void setNotificationCenterDelegate(NotificationCenterDelegate delegate) {
        this.notificationCenterDelegate = delegate;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
        notificationCenterDelegate.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived: " + remoteMessage.getData());
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        ModuleNotification moduleNotification = new ModuleNotification(notification.getBody(), notification.getBodyLocalizationArgs(), notification.getBodyLocalizationKey(),
                notification.getChannelId(), notification.getClickAction(), notification.getColor(), notification.getDefaultLightSettings(), notification.getDefaultSound(),
                notification.getDefaultVibrateSettings(), notification.getEventTime(), notification.getIcon(), notification.getImageUrl(), notification.getLightSettings(),
                notification.getLink(), notification.getLocalOnly(), notification.getNotificationCount(), notification.getNotificationPriority(), notification.getSound(),
                notification.getSticky(), notification.getTag(), notification.getTicker(), notification.getTitle(), notification.getTitleLocalizationArgs(),
                notification.getTitleLocalizationKey(), notification.getVibrateTimings(), notification.getVisibility());

        ModuleRemoteMessage moduleRemoteMessage = new ModuleRemoteMessage(moduleNotification, remoteMessage.getData(), remoteMessage.getCollapseKey(), remoteMessage.getFrom(),
                remoteMessage.getMessageId(), remoteMessage.getMessageType(), remoteMessage.getOriginalPriority(), remoteMessage.getPriority(), remoteMessage.getSentTime(),
                remoteMessage.getTo(), remoteMessage.getTtl());
        notificationCenterDelegate.onMessageReceived(moduleRemoteMessage);
    }
}
