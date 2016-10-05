package com.iGap.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.activitys.ActivityMain;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;

import io.realm.Realm;
import io.realm.RealmResults;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by android3 on 10/1/2016.
 */
public class HelperNotificationAndBadge {

    private int unreadMessageCount = 0;
    private String message = "";
    private boolean isFromOnRoom = true;
    private long roomId = 0;
    private long senderId = 0;
    private Context mContext;


    private NotificationManager notificationManager;
    private Notification notification;
    private int notificationId = 20;
    private RemoteViews remoteViews;


    public HelperNotificationAndBadge(Context context) {
        mContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
    }

    //*****************************************************************************************   notification ***********************

    private void setRemoteViews() {

        String avatarPath = null;
        if (unreadMessageCount == 1) {
            remoteViews.setTextViewText(R.id.ln_txt_message_notification, message);
        } else {
            remoteViews.setTextViewText(R.id.ln_txt_message_notification, " you have " + unreadMessageCount + " unread message");
        }

        if (isFromOnRoom) {
            Realm realm = Realm.getDefaultInstance();
            RealmAvatarPath realmAvatarPath = realm.where(RealmAvatarPath.class).equalTo("id", senderId).findFirst();
            if (realmAvatarPath != null)
                avatarPath = realmAvatarPath.getPathImage();
            if (avatarPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                if (bitmap != null) {
                    remoteViews.setImageViewBitmap(R.id.ln_imv_avatar_notification, bitmap);
                } else {
                    remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification, R.mipmap.logo);
                }
            } else {
                remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification, R.mipmap.logo);
            }
        } else {
            remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification, R.mipmap.logo);
        }

    }

    private void setNotification() {

        PendingIntent pi;

        if (isFromOnRoom) {
            Intent intent = new Intent(mContext, ActivityChat.class);
            intent.putExtra("RoomId", roomId);
            pi = PendingIntent.getActivity(mContext, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        } else {
            pi = PendingIntent.getActivity(mContext, 10, new Intent(mContext, ActivityMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        setRemoteViews();

        notification = new NotificationCompat.Builder(mContext)
                .setTicker(" you have New Message ")
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle("my notification")
                //  .setContentText(place)
                .setContent(remoteViews)
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();


        notificationManager.notify(notificationId, notification);

    }

    public void updateNotificationAndBadge(boolean updateNotification) {

        unreadMessageCount = 0;
        isFromOnRoom = true;

        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).findAll();

        if (chatHistories != null) {
            for (RealmChatHistory realmChatHistory : chatHistories) {
                RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
                if (roomMessage != null) {
                    if (roomMessage.getUserId() != userId) {
                        if (roomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENT.toString()) || roomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.DELIVERED.toString())) {
                            unreadMessageCount++;
                            message = roomMessage.getMessage();
                            senderId = roomMessage.getUserId();

                            if (unreadMessageCount == 1)
                                roomId = realmChatHistory.getRoomId();

                            if (roomId != realmChatHistory.getRoomId()) {
                                isFromOnRoom = false;
                            }
                        }
                    }
                }
            }
        }

        realm.close();

        if (unreadMessageCount == 0) {
            if (updateNotification) {
                notificationManager.cancel(notificationId);
            }
            ShortcutBadger.applyCount(mContext, 0);
        } else {
            if (updateNotification) {
                setNotification();
            }
            ShortcutBadger.applyCount(mContext, unreadMessageCount);
        }
    }


    public void cancelNotification() {
        notificationManager.cancel(notificationId);
    }

}
