package net.iGap.libs.notification;



import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.firebase1.ModuleRemoteMessage;
import net.iGap.firebase1.NotificationCenterDelegate;
import net.iGap.helper.HelperNotification;
import net.iGap.model.AccountUser;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmNotificationRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.viewmodel.controllers.CallManager;

import org.json.JSONArray;

import ir.metrix.Metrix;


public class NotificationService implements NotificationCenterDelegate {

    private final static String ROOM_ID = "roomId";
    private final static String MESSAGE_ID = "messageId";
    private final static String MESSAGE_TYPE = "loc_key";
    private final static String USER_ID = "userId";

    private static final String TYPE = "type";
    private static final String SIGNALING_OFFER = "SIGNALING_OFFER";

    @Override
    public void onNewToken(String mToken) {
        if (G.ISRealmOK) {
            RealmUserInfo.setPushNotification(mToken);
            Metrix.setPushToken(mToken);
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
    public void onMessageReceived(ModuleRemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null && remoteMessage.getData().containsKey(ActivityMain.DEEP_LINK)) {
            HelperNotification.sendDeepLink(remoteMessage.getData(), remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().containsKey(TYPE) && remoteMessage.getData().containsKey(USER_ID)) {
            String type = remoteMessage.getData().get(TYPE);
            if (type != null && type.equals(SIGNALING_OFFER)) {
                CallManager.getInstance();
            }
        }

        if (remoteMessage.getData().containsKey(MESSAGE_ID)) {

            final long messageId = Long.valueOf(remoteMessage.getData().get(MESSAGE_ID));
            final long roomId = Long.valueOf(remoteMessage.getData().get(ROOM_ID));
            final long userId = Long.valueOf(remoteMessage.getData().get(USER_ID));

            AccountUser accountUser = AccountManager.getInstance().getUser(userId);
            if (accountUser == null)
                return;

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
                            String text = "";
                            if (roomType == ProtoGlobal.Room.Type.GROUP) {
                                text = loc_args.getString(2);
                            } else {
                                text = loc_args.getString(1);
                            }

                            if (roomType == ProtoGlobal.Room.Type.CHANNEL || roomType == ProtoGlobal.Room.Type.CHAT) {
                                if (loc_args.length() == 2) {
                                    text = loc_args.getString(1);
                                }
                            } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
                                if (loc_args.length() == 3) {
                                    text = loc_args.getString(2);
                                }
                            }
                            RealmNotificationRoomMessage.putToDataBase(realm1, messageId, roomId);
                            ProtoGlobal.RoomMessage roomMessage = ProtoGlobal.RoomMessage.newBuilder()
                                    .setMessage(text)
                                    .setMessageType(getMessageType(loc_key, roomType))
                                    .setUpdateTime((int) (remoteMessage.getSentTime() / 1000))
                                    .build();
                            HelperNotification.getInstance().addMessage(realm1, roomId, roomMessage, roomType, accountUser);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }, userId)).start();
        }
    }

    private ProtoGlobal.RoomMessageType getMessageType(String messageType, ProtoGlobal.Room.Type roomType) {
        if (roomType == ProtoGlobal.Room.Type.GROUP) {
            messageType = messageType.replace("GROUP_", "");
        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
            messageType = messageType.replace("CHANNEL_", "");
        }
        switch (messageType) {
            case "MESSAGE_TEXT":
                return ProtoGlobal.RoomMessageType.TEXT;
            case "MESSAGE_IMAGE":
                return ProtoGlobal.RoomMessageType.IMAGE;
            case "MESSAGE_VIDEO":
                return ProtoGlobal.RoomMessageType.VIDEO;
            case "MESSAGE_AUDIO":
                return ProtoGlobal.RoomMessageType.AUDIO;
            case "MESSAGE_VOICE":
                return ProtoGlobal.RoomMessageType.VOICE;
            case "MESSAGE_GIF":
                return ProtoGlobal.RoomMessageType.GIF;
            case "MESSAGE_FILE":
                return ProtoGlobal.RoomMessageType.FILE;
            case "MESSAGE_STICKER":
                return ProtoGlobal.RoomMessageType.STICKER;
            case "MESSAGE_LOCATION":
                return ProtoGlobal.RoomMessageType.LOCATION;
            case "MESSAGE_CONTACT":
                return ProtoGlobal.RoomMessageType.CONTACT;
//            case "MESSAGE_NOTEXT":
//                return
            default:
                return ProtoGlobal.RoomMessageType.TEXT;
        }
    }
}