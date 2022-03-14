package net.iGap.firebase1;


public interface NotificationCenterDelegate {

    void onMessageReceived(ModuleRemoteMessage remoteMessage);

    void onNewToken(String token);
}
