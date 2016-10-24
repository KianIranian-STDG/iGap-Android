package com.iGap.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.activities.ActivityMain;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatarPath;
import com.iGap.realm.RealmAvatarPathFields;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatHistoryFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by android3 on 10/1/2016.
 */
public class HelperNotificationAndBadge {

    private int unreadMessageCount = 0;
    private String messageOne = "";
    private boolean isFromOnRoom = true;
    private long roomId = 0;
    private long senderId = 0;

    private ArrayList<Item> list = new ArrayList<>();
    private ArrayList<Long> senderList = new ArrayList<>();

    private NotificationManager notificationManager;
    private Notification notification;
    private int notificationId = 20;
    private RemoteViews remoteViews;
    private RemoteViews remoteViewsLarge;

    private int countUnicChat = 0;

    private static final String strClose = "close";


    public static class RemoteActionReciver extends BroadcastReceiver {

        public RemoteActionReciver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            G.helperNotificationAndBadge.cancelNotification();

        }
    }

    public HelperNotificationAndBadge() {
        notificationManager = (NotificationManager) G.context.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(G.context.getPackageName(), R.layout.layout_notification_small);
        remoteViewsLarge = new RemoteViews(G.context.getPackageName(), R.layout.layout_notification);

        Intent intentClose = new Intent(G.context, RemoteActionReciver.class);
        intentClose.putExtra("Action", "strClose");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(G.context, 1, intentClose, 0);
        remoteViewsLarge.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);
    }


    private class Item {

        String name = "";
        String message = "";
        String time = "";
    }

    //*****************************************************************************************   notification ***********************

    private void setRemoteViewsNormal() {

        String avatarPath = null;
        if (unreadMessageCount == 1) {
            remoteViews.setTextViewText(R.id.ln_txt_header, list.get(0).name);
            remoteViews.setTextViewText(R.id.ln_txt_time, list.get(0).time);
            remoteViews.setTextViewText(R.id.ln_txt_message_notification, list.get(0).message);

        } else {

            remoteViews.setTextViewText(R.id.ln_txt_header, G.context.getString(R.string.igap));
            remoteViews.setTextViewText(R.id.ln_txt_time, list.get(list.size() - 1).time);

            String s = "";
            if (countUnicChat == 1) {
                s = " " + G.context.getString(R.string.chat);
            } else if (countUnicChat > 1) {
                s = " " + G.context.getString(R.string.chats);
            }


            remoteViews.setTextViewText(R.id.ln_txt_message_notification, unreadMessageCount + G.context.getString(R.string.new_messages_from) + countUnicChat + s);
        }

        if (isFromOnRoom) {
            Realm realm = Realm.getDefaultInstance();
            RealmAvatarPath realmAvatarPath = realm.where(RealmAvatarPath.class).equalTo(RealmAvatarPathFields.ID, senderId).findFirst();
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

    private void setRemoteViewsLarge() {

        if (unreadMessageCount == 1) {

            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.GONE);
            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.GONE);
            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.GONE);

            remoteViewsLarge.setTextViewText(R.id.ln_txt_a1, list.get(0).name);
            remoteViewsLarge.setTextViewText(R.id.ln_txt_a2, list.get(0).message);
            remoteViewsLarge.setTextViewText(R.id.ln_txt_a3, list.get(0).time);

        } else if (unreadMessageCount == 2) {

            if (isFromOnRoom) {
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.GONE);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_a1, list.get(0).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a2, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a3, "");

                remoteViewsLarge.setTextViewText(R.id.ln_txt_b1, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b2, list.get(0).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b3, list.get(0).time);


                remoteViewsLarge.setTextViewText(R.id.ln_txt_c1, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_c2, list.get(1).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_c3, list.get(1).time);


            } else {
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.GONE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.GONE);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_a1, list.get(0).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a2, list.get(0).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a3, list.get(0).time);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_b1, list.get(1).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b2, list.get(1).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b3, list.get(1).time);
            }


        } else if (unreadMessageCount >= 3) {

            if (isFromOnRoom) {


                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.VISIBLE);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_a1, list.get(0).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a2, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a3, "");

                remoteViewsLarge.setTextViewText(R.id.ln_txt_b1, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b2, list.get(0).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b3, list.get(0).time);


                remoteViewsLarge.setTextViewText(R.id.ln_txt_c1, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_c2, list.get(1).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_c3, list.get(1).time);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_d1, "");
                remoteViewsLarge.setTextViewText(R.id.ln_txt_d2, list.get(2).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_d3, list.get(2).time);


            } else {
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.GONE);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_a1, list.get(0).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a2, list.get(0).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_a3, list.get(0).time);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_b1, list.get(1).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b2, list.get(1).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_b3, list.get(1).time);

                remoteViewsLarge.setTextViewText(R.id.ln_txt_c1, list.get(2).name);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_c2, list.get(2).message);
                remoteViewsLarge.setTextViewText(R.id.ln_txt_c3, list.get(2).time);
            }
        }


        if (unreadMessageCount >= 4) {
            remoteViewsLarge.setViewVisibility(R.id.ln_txt_more, View.VISIBLE);
        } else {
            remoteViewsLarge.setViewVisibility(R.id.ln_txt_more, View.GONE);
        }

        String chatCount = "";

        if (countUnicChat == 1) {
            chatCount = G.context.getString(R.string.from) + " " + countUnicChat + " " + G.context.getString(R.string.chat);
        } else if (countUnicChat > 1) {
            chatCount = G.context.getString(R.string.from) + " " + countUnicChat + " " + G.context.getString(R.string.chats);
        }


        String newmess = "";
        if (unreadMessageCount == 1) {
            newmess = G.context.getString(R.string.new_message);
            chatCount = "";
        } else {
            newmess = G.context.getString(R.string.new_messages);
        }

        remoteViewsLarge.setTextViewText(R.id.ln_txt_unread_message, unreadMessageCount + newmess + chatCount);

        if (unreadMessageCount == 1) {
            remoteViewsLarge.setViewVisibility(R.id.mln_btn_replay, View.VISIBLE);
            remoteViewsLarge.setViewVisibility(R.id.ln_txt_replay, View.VISIBLE);
        } else {
            remoteViewsLarge.setViewVisibility(R.id.mln_btn_replay, View.GONE);
            remoteViewsLarge.setViewVisibility(R.id.ln_txt_replay, View.GONE);
        }

    }

    private void setNotification() {

        PendingIntent pi;

        if (isFromOnRoom) {
            Intent intent = new Intent(G.context, ActivityChat.class);
            intent.putExtra("RoomId", roomId);
            pi = PendingIntent.getActivity(G.context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        } else {
            pi = PendingIntent.getActivity(G.context, 10, new Intent(G.context, ActivityMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        setRemoteViewsNormal();


        String messageToshow = list.get(0).message;
        if (list.get(0).message.length() > 40) {
            messageToshow = messageToshow.substring(0, 40);
        }

        notification = new NotificationCompat.Builder(G.context)
                .setTicker(list.get(0).name + " " + messageToshow)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(G.context.getString(R.string.new_message_recicve))
                .setContent(remoteViews)
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setRemoteViewsLarge();
            notification.bigContentView = remoteViewsLarge;
        }


        notificationManager.notify(notificationId, notification);

    }

    public void updateNotificationAndBadge(boolean updateNotification) {

        unreadMessageCount = 0;
        isFromOnRoom = true;
        countUnicChat = 0;

        list.clear();
        senderList.clear();

        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).findAllSorted(RealmChatHistoryFields.ID, Sort.DESCENDING);

        if (chatHistories != null) {
            for (RealmChatHistory realmChatHistory : chatHistories) {
                RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
                if (roomMessage != null) {
                    if (roomMessage.getUserId() != userId) {
                        if (roomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENT.toString()) || roomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.DELIVERED.toString())) {
                            unreadMessageCount++;
                            messageOne = roomMessage.getMessage();
                            senderId = roomMessage.getUserId();


                            if (unreadMessageCount == 1 || unreadMessageCount == 2 || unreadMessageCount == 3) {
                                Item item = new Item();
                                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, realmChatHistory.getRoomId()).findFirst();
                                if (room != null) {
                                    item.name = room.getTitle() + " : ";
                                }
                                item.message = roomMessage.getMessage();
                                item.time = TimeUtils.toLocal(roomMessage.getUpdateTime(), G.CHAT_MESSAGE_TIME);
                                list.add(item);
                            }


                            if (unreadMessageCount == 1)
                                roomId = realmChatHistory.getRoomId();

                            if (roomId != realmChatHistory.getRoomId()) {
                                isFromOnRoom = false;
                            }

                            boolean isAdd = true;
                            for (int k = 0; k < senderList.size(); k++) {
                                if (senderList.get(k) == realmChatHistory.getRoomId()) {
                                    isAdd = false;
                                    break;
                                }
                            }

                            if (isAdd)
                                senderList.add(realmChatHistory.getRoomId());
                        }
                    }
                }
            }

            countUnicChat = senderList.size();
        }

        realm.close();

        if (unreadMessageCount == 0) {
            if (updateNotification) {
                notificationManager.cancel(notificationId);
            }
            try {
                ShortcutBadger.applyCount(G.context, 0);
            } catch (RuntimeException e) {
            }
        } else {
            if (updateNotification) {
                setNotification();
            }
            try {
                ShortcutBadger.applyCount(G.context, unreadMessageCount);
            } catch (RuntimeException e) {
            }
        }
    }

    public void cancelNotification() {
        notificationManager.cancel(notificationId);
    }


}
