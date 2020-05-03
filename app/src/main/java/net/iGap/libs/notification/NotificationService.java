package net.iGap.libs.notification;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.IGLog;
import net.iGap.model.AccountUser;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmNotificationRoomMessage;
import net.iGap.realm.RealmUserInfo;

import org.json.JSONArray;


public class NotificationService extends FirebaseMessagingService {

    private final static String ROOM_ID = "roomId";
    private final static String MESSAGE_ID = "messageId";
    private final static String MESSAGE_TYPE = "loc_key";
    private final static String USER_ID = "userId";


    @Override
    public void onCreate() {
        super.onCreate();
        IGLog.e("NotificationService onCreate");
    }

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        if (G.ISRealmOK) {
            RealmUserInfo.setPushNotification(mToken);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNewToken(mToken);
                }
            }, 1000);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        IGLog.e("onMessageReceived " + remoteMessage.getData().toString());

        if (remoteMessage.getNotification() != null && remoteMessage.getData().containsKey(ActivityMain.DEEP_LINK)) {
            HelperNotification.sendDeepLink(remoteMessage.getData(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().containsKey(MESSAGE_ID)) {

            final long messageId = Long.valueOf(remoteMessage.getData().get(MESSAGE_ID));
            final long roomId = Long.valueOf(remoteMessage.getData().get(ROOM_ID));
            final long userId = Long.valueOf(remoteMessage.getData().get(USER_ID));

            IGLog.e("room id " + roomId + " message id " + messageId + " from user id " + userId);

            AccountUser accountUser = AccountManager.getInstance().getUser(userId);
            if (accountUser == null) {
                IGLog.e("++++++++++++ accountUser null ++++++++++++");
                return;
            }

            new Thread(() -> DbManager.getInstance().doRealmTask(realm -> {
                realm.executeTransaction(realm1 -> {
                    try {

                        if (RealmNotificationRoomMessage.canShowNotif(realm1, messageId, roomId)) {

                            String loc_key = remoteMessage.getData().get(MESSAGE_TYPE);
                            ProtoGlobal.Room.Type roomType;

                            if (loc_key.contains("CHANNEL")) {
                                roomType = ProtoGlobal.Room.Type.CHANNEL;
                            } else if (loc_key.contains("GROUP")) {
                                roomType = ProtoGlobal.Room.Type.GROUP;
                            } else {
                                roomType = ProtoGlobal.Room.Type.CHAT;
                            }

                            JSONArray loc_args = new JSONArray(remoteMessage.getData().get("loc_args"));
                            String text = loc_args.getString(1);

                            RealmNotificationRoomMessage.putToDataBase(realm1, messageId, roomId);

                            ProtoGlobal.RoomMessage roomMessage = ProtoGlobal.RoomMessage.newBuilder()
                                    .setMessage(text)
                                    .setUpdateTime((int) (remoteMessage.getSentTime() / 1000))
                                    .build();
                            IGLog.e(getClass().getSimpleName() + " add message from service ");
                            HelperNotification.getInstance().addMessage(realm1, roomId, roomMessage, roomType, accountUser);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        IGLog.e(e);
                    }
                });
            }, userId)).start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IGLog.e(getClass().getSimpleName() + " onDestroy");
    }
}