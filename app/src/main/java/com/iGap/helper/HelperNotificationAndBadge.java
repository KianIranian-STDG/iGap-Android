package com.iGap.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.activities.ActivityMain;
import com.iGap.module.SHP_SETTING;
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
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.ArrayList;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.iGap.G.context;

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

    private SharedPreferences sharedPreferences;
    private int led;
    private String vibrator;
    private int popupNotification;
    private int sound;
    private int messagePeriview;

    private int inAppSound;
    private int inVibrator;
    private int inAppPreview;
    private int inChat_Sound;

    private int countUnicChat = 0;

    private static final String strClose = "close";

    public static class RemoteActionReciver extends BroadcastReceiver {

        public RemoteActionReciver() {
        }

        @Override public void onReceive(Context context, Intent intent) {

            G.helperNotificationAndBadge.cancelNotification();
        }
    }

    public HelperNotificationAndBadge() {
        notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification_small);
        remoteViewsLarge = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

        Intent intentClose = new Intent(context, RemoteActionReciver.class);
        intentClose.putExtra("Action", "strClose");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(context, 1, intentClose, 0);
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

            remoteViews.setTextViewText(R.id.ln_txt_header, context.getString(R.string.igap));
            remoteViews.setTextViewText(R.id.ln_txt_time, list.get(list.size() - 1).time);

            String s = "";
            if (countUnicChat == 1) {
                s = " " + context.getString(R.string.chat);
            } else if (countUnicChat > 1) {
                s = " " + context.getString(R.string.chats);
            }

            remoteViews.setTextViewText(R.id.ln_txt_message_notification, unreadMessageCount
                + context.getString(R.string.new_messages_from)
                + countUnicChat
                + s);
        }

        if (isFromOnRoom) {
            Realm realm = Realm.getDefaultInstance();
            RealmAvatarPath realmAvatarPath = realm.where(RealmAvatarPath.class)
                .equalTo(RealmAvatarPathFields.ID, senderId)
                .findFirst();
            if (realmAvatarPath != null) avatarPath = realmAvatarPath.getPathImage();
            if (avatarPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                if (bitmap != null) {
                    remoteViews.setImageViewBitmap(R.id.ln_imv_avatar_notification, bitmap);
                } else {
                    remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification,
                        R.mipmap.logo);
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
            chatCount =
                context.getString(R.string.from) + " " + countUnicChat + " " + context.getString(
                    R.string.chat);
        } else if (countUnicChat > 1) {
            chatCount =
                context.getString(R.string.from) + " " + countUnicChat + " " + context.getString(
                    R.string.chats);
        }

        String newmess = "";
        if (unreadMessageCount == 1) {
            newmess = context.getString(R.string.new_message);
            chatCount = "";
        } else {
            newmess = context.getString(R.string.new_messages);
        }

        remoteViewsLarge.setTextViewText(R.id.ln_txt_unread_message,
            unreadMessageCount + newmess + chatCount);

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
            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", roomId);
            pi = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pi = PendingIntent.getActivity(context, 10, new Intent(context, ActivityMain.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        }

        setRemoteViewsNormal();

        String messageToshow = list.get(0).message;
        if (list.get(0).message.length() > 40) {
            messageToshow = messageToshow.substring(0, 40);
        }

        notification = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.logo)
            .setContentTitle(context.getString(R.string.new_message_recicve))
            .setContent(remoteViews)
            .setContentIntent(pi)
            .setAutoCancel(false)
            .setOngoing(true)
            .build();

        if (inAppPreview == 0 && G.isAppInFg) {

            if (messagePeriview == 0) {

                notification.tickerText = "";
            }
        } else {
            if (messagePeriview == 0) {
                notification.tickerText = "";
            } else {
                notification.tickerText = list.get(0).name + " " + messageToshow;
            }
        }
        //=======================================================
        if (inAppSound == 0 && G.isAppInFg) {
            notification.sound =
                Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + R.raw.none);
        } else {
            notification.sound = Uri.parse(
                "android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
        }
        //=======================================================
        if (inVibrator == 0 && G.isAppInFg) {
            notification.vibrate = new long[] { 0, 0, 0 };
        } else {
            notification.vibrate = setVibrator(vibrator);
        }
        //=======================================================
        if (inChat_Sound == 0 && G.isAppInFg) {

        } else {

        }
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = led;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setRemoteViewsLarge();
            notification.bigContentView = remoteViewsLarge;
        }

        notificationManager.notify(notificationId, notification);
    }

    public void updateNotificationAndBadge(boolean updateNotification, int type) {

        sharedPreferences =
            context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

        switch (type) {
            case 0:

                led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                vibrator =
                    sharedPreferences.getString(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, "Default");
                popupNotification =
                    sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 3);
                sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 3);
                messagePeriview =
                    sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                break;

            case 1:
                led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                vibrator =
                    sharedPreferences.getString(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, "Default");
                popupNotification =
                    sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 3);
                sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 3);
                messagePeriview =
                    sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);

                break;
            case 2:

                break;
        }

        inAppSound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 1);
        inVibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 1);
        inAppPreview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 1);
        inChat_Sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 1);

        unreadMessageCount = 0;
        isFromOnRoom = true;
        countUnicChat = 0;

        list.clear();
        senderList.clear();

        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class)
            .findAllSorted(RealmChatHistoryFields.ID, Sort.DESCENDING);

        if (chatHistories != null) {
            for (RealmChatHistory realmChatHistory : chatHistories) {
                RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
                if (roomMessage != null) {
                    if (roomMessage.getUserId() != userId) {
                        if (roomMessage.getStatus()
                            .equals(ProtoGlobal.RoomMessageStatus.SENT.toString())
                            || roomMessage.getStatus()
                            .equals(ProtoGlobal.RoomMessageStatus.DELIVERED.toString())) {
                            unreadMessageCount++;
                            messageOne = roomMessage.getMessage();
                            senderId = roomMessage.getUserId();

                            if (unreadMessageCount == 1
                                || unreadMessageCount == 2
                                || unreadMessageCount == 3) {
                                Item item = new Item();
                                RealmRoom room = realm.where(RealmRoom.class)
                                    .equalTo(RealmRoomFields.ID, realmChatHistory.getRoomId())
                                    .findFirst();
                                if (room != null) {
                                    item.name = room.getTitle() + " : ";
                                }
                                item.message = roomMessage.getMessage();
                                item.time = TimeUtils.toLocal(roomMessage.getUpdateTime(),
                                    G.CHAT_MESSAGE_TIME);
                                list.add(item);
                            }

                            if (unreadMessageCount == 1) roomId = realmChatHistory.getRoomId();

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

                            if (isAdd) senderList.add(realmChatHistory.getRoomId());
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
                ShortcutBadger.applyCount(context, 0);
            } catch (RuntimeException e) {
            }
        } else {
            if (updateNotification) {
                setNotification();
            }
            try {
                ShortcutBadger.applyCount(context, unreadMessageCount);
            } catch (RuntimeException e) {
            }
        }
    }

    public void cancelNotification() {
        notificationManager.cancel(notificationId);
    }

    public int setSound(int which) {
        int sound = R.raw.igap;
        switch (which) {
            case 0:

                sound = R.raw.igap;
                break;
            case 1:
                sound = R.raw.aooow;
                break;
            case 2:
                sound = R.raw.bbalert;
                break;
            case 3:
                sound = R.raw.boom;
                break;
            case 4:
                sound = R.raw.bounce;
                break;
            case 5:
                sound = R.raw.doodoo;
                break;
            case 6:
                sound = R.raw.igap;
                break;
            case 7:
                sound = R.raw.jing;
                break;
            case 8:
                sound = R.raw.lili;
                break;
            case 9:
                sound = R.raw.msg;
                break;
            case 10:
                sound = R.raw.newa;
                break;
            case 11:
                sound = R.raw.none;
                break;
            case 12:
                sound = R.raw.onelime;
                break;
            case 13:
                sound = R.raw.tone;
                break;
            case 14:
                sound = R.raw.woow;
                break;
        }
        return sound;
    }

    public long[] setVibrator(String vibre) {
        long[] intVibrator = new long[] {};

        switch (vibre) {
            case "Disable":
                intVibrator = new long[] { 0, 0, 0 };
                break;
            case "Default":
                intVibrator = new long[] { 0, 500, 0 };
                break;
            case "Short":
                intVibrator = new long[] { 0, 200, 0 };
                break;
            case "Long":
                intVibrator = new long[] { 0, 1000, 0 };
                break;
            case "Only if silent":
                AudioManager am2 = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                switch (am2.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        intVibrator = new long[] { 0, 500, 0 };
                        break;
                }
                break;
        }
        return intVibrator;
    }
}
