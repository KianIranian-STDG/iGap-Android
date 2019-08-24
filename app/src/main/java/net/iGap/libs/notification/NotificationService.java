package net.iGap.libs.notification;


import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperNotification;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmNotificationRoomMessage;
import net.iGap.realm.RealmUserInfo;

import org.json.JSONArray;

import io.realm.Realm;


public class NotificationService extends FirebaseMessagingService {

    private final static String ROOM_ID = "roomId";
    private final static String MESSAGE_ID = "messageId";
    private final static String MESSAGE_TYPE = "loc_key";

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        if (G.ISOK) {
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
        WebSocketClient.reconnect(false);

        if (remoteMessage.getData().containsKey("deep")){
            HelperNotification.sendDeepLink(remoteMessage.getData());
        }

        if (remoteMessage.getData().containsKey(MESSAGE_ID)) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            final long messageId = Long.valueOf(remoteMessage.getData().get(MESSAGE_ID));
                            final long roomId = Long.valueOf(remoteMessage.getData().get(ROOM_ID));

                            if (RealmNotificationRoomMessage.canShowNotif(realm, messageId, roomId)) {

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

                                RealmNotificationRoomMessage.putToDataBase(realm, messageId, roomId);

                                ProtoGlobal.RoomMessage roomMessage = ProtoGlobal.RoomMessage.newBuilder()
                                        .setMessage(text)
                                        .setUpdateTime((int) (remoteMessage.getSentTime() / 1000))
                                        .build();

                                Log.d("bagi", "FcmSHOWNOTIF" + remoteMessage.getData() + "");
                                HelperNotification.getInstance().addMessage(roomId, roomMessage, roomType);

                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


}