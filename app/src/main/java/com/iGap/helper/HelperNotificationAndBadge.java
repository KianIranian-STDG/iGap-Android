package com.iGap.helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RemoteViews;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.activities.ActivityMain;
import com.iGap.activities.ActivityPopUpNotification;
import com.iGap.module.AttachFile;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;

import static com.iGap.G.context;

/**
 * Created by android3 on 10/1/2016.
 */
public class HelperNotificationAndBadge {

    private static final String strClose = "close";
    private static final int DEFAULT = 0;
    private static final int ENABLE = 1;
    private static final int DISABLE = 2;
    public static boolean isChatRoomNow = false;
    private int unreadMessageCount = 0;
    private String messageOne = "";
    private boolean isFromOnRoom = true;
    int countChannelMessage = 0;
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
    private int vibrator;
    private int popupNotification;
    private int sound;
    private int messagePeriview;
    private boolean isMute;
    private String inRoomVibrator;  //specially for each room
    private int inRoomSound;        //specially for each room
    private int inRoomLedColor;     //specially for each room
    private int inAppSound;
    private int inAppVibrator;
    private int inAppPreview;
    private int inChat_Sound;
    private int countUnicChat = 0;
    private long idRoom;
    private int delayAlarm = 5000;
    private long currentAlarm;

    public HelperNotificationAndBadge() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification_small);
        remoteViewsLarge = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

        Intent intentClose = new Intent(context, RemoteActionReciver.class);
        intentClose.putExtra("Action", "strClose");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(context, 1, intentClose, 0);
        remoteViewsLarge.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);
    }

    private void setRemoteViewsNormal() {

        String avatarPath = null;
        if (unreadMessageCount == 1) {
            remoteViews.setTextViewText(R.id.ln_txt_header, list.get(0).name);
            remoteViews.setTextViewText(R.id.ln_txt_time, list.get(0).time);
            remoteViews.setTextViewText(R.id.ln_txt_message_notification, list.get(0).message);
        } else {

            remoteViews.setTextViewText(R.id.ln_txt_header, context.getString(R.string.igap));
            if ((list.size() - 1) > 0) {
                remoteViews.setTextViewText(R.id.ln_txt_time, list.get(list.size() - 1).time);
            }

            String s = "";
            if (countUnicChat == 1) {
                s = " " + context.getString(R.string.chat);
            } else if (countUnicChat > 1) {
                s = " " + context.getString(R.string.chats);
            }

            String str = String.format(" %d " + context.getString(R.string.new_messages_from) + " %d " + s, unreadMessageCount + countChannelMessage, countUnicChat);

            remoteViews.setTextViewText(R.id.ln_txt_message_notification, str);
        }

        if (isFromOnRoom) {
            Realm realm = Realm.getDefaultInstance();
            RealmAvatar realmAvatarPath = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, senderId).findFirst();
            if (realmAvatarPath != null) {
                if (realmAvatarPath.getFile().isFileExistsOnLocal()) {
                    avatarPath = realmAvatarPath.getFile().getLocalFilePath();
                } else if (realmAvatarPath.getFile().isThumbnailExistsOnLocal()) {
                    avatarPath = realmAvatarPath.getFile().getLocalThumbnailPath();
                }
            }
            if (avatarPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                if (bitmap != null) {
                    remoteViews.setImageViewBitmap(R.id.ln_imv_avatar_notification, bitmap);
                } else {
                    remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification, R.mipmap.icon);
                }
            } else {
                remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification, R.mipmap.icon);
            }
        } else {
            remoteViews.setImageViewResource(R.id.ln_imv_avatar_notification, R.mipmap.icon);
        }
    }

    private void setOnTextClick(int resLayot, int indexItem) {

        Intent intent = new Intent(context, ActivityChat.class);
        intent.putExtra("RoomId", list.get(indexItem).roomId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10 + indexItem, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityPopUpNotification.isGoingToChatFromPopUp = true;
        remoteViewsLarge.setOnClickPendingIntent(resLayot, pendingIntent);
    }

    private void setRemoteViewsLarge() {

        if (unreadMessageCount == 1) {

            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.GONE);
            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.GONE);
            remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.GONE);

            setOnTextClick(R.id.ln_ll_message_a, 0);

            remoteViewsLarge.setTextViewText(R.id.ln_txt_a1, list.get(0).name);
            remoteViewsLarge.setTextViewText(R.id.ln_txt_a2, list.get(0).message);
            remoteViewsLarge.setTextViewText(R.id.ln_txt_a3, list.get(0).time);
        } else if (unreadMessageCount == 2) {

            if (isFromOnRoom) {
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_a, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_b, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_c, View.VISIBLE);
                remoteViewsLarge.setViewVisibility(R.id.ln_ll_message_d, View.GONE);

                setOnTextClick(R.id.ln_ll_message_a, 0);
                setOnTextClick(R.id.ln_ll_message_b, 0);
                setOnTextClick(R.id.ln_ll_message_c, 0);

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

                setOnTextClick(R.id.ln_ll_message_a, 0);
                setOnTextClick(R.id.ln_ll_message_b, 1);

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
                setOnTextClick(R.id.ln_ll_message_a, 0);
                setOnTextClick(R.id.ln_ll_message_b, 0);
                setOnTextClick(R.id.ln_ll_message_c, 0);
                setOnTextClick(R.id.ln_ll_message_d, 0);

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

                setOnTextClick(R.id.ln_ll_message_a, 0);
                setOnTextClick(R.id.ln_ll_message_b, 1);
                setOnTextClick(R.id.ln_ll_message_c, 2);

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
            chatCount = context.getString(R.string.from) + " " + countUnicChat + " " + context.getString(R.string.chat);
        } else if (countUnicChat > 1) {
            chatCount = context.getString(R.string.from) + " " + countUnicChat + " " + context.getString(R.string.chats);
        }

        String newmess = "";
        if (unreadMessageCount + countChannelMessage == 1) {
            newmess = context.getString(R.string.new_message);
            chatCount = "";
        } else {
            newmess = context.getString(R.string.new_messages);
        }

        remoteViewsLarge.setTextViewText(R.id.ln_txt_unread_message, unreadMessageCount + countChannelMessage + " " + newmess + " " + chatCount);

        if (unreadMessageCount + countChannelMessage == 1) {
            remoteViewsLarge.setViewVisibility(R.id.mln_btn_replay, View.VISIBLE);
            remoteViewsLarge.setViewVisibility(R.id.ln_txt_replay, View.VISIBLE);
        } else {
            remoteViewsLarge.setViewVisibility(R.id.mln_btn_replay, View.GONE);
            remoteViewsLarge.setViewVisibility(R.id.ln_txt_replay, View.GONE);
        }
    }

    //*****************************************************************************************
    // notification ***********************

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.white_icon : R.mipmap.iconsmal;
    }

    private void setNotification() {

        PendingIntent pi;

        if (isFromOnRoom) {
            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", roomId);
            pi = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityPopUpNotification.isGoingToChatFromPopUp = true;
        } else {
            pi = PendingIntent.getActivity(context, 10, new Intent(context, ActivityMain.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }


        setRemoteViewsNormal();

        String messageToshow = list.get(0).message;
        if (list.get(0).message.length() > 40) {
            messageToshow = messageToshow.substring(0, 40);
        }

        notification = new NotificationCompat.Builder(context).setSmallIcon(getNotificationIcon()).setContentTitle(context.getString(R.string.new_message_recicve)).setContent(remoteViews).setContentIntent(pi).build();

        if (currentAlarm + delayAlarm < System.currentTimeMillis()) {

            alarmNotification(messageToshow);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setRemoteViewsLarge();
            notification.bigContentView = remoteViewsLarge;
        }

        notificationManager.notify(notificationId, notification);
    }

    private void alarmNotification(String messageToShow) {
        if (isMute) {

            Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + R.raw.none);
            notification.vibrate = new long[]{0, 0, 0};
        } else {

            if (G.isAppInFg) {
                if (!isChatRoomNow) {

                    if (inAppVibrator == 1) {
                        notification.vibrate = setVibrator(vibrator);
                    }
                    if (inAppSound == 1) {
                        notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
                    }
                    if (inAppPreview == 1) {
                        notification.tickerText = list.get(0).name + " " + messageToShow;
                    }
                } else if (inChat_Sound == 1) {
                    notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
                }
            } else {
                notification.vibrate = setVibrator(vibrator);
                notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));

                if (messagePeriview == 1) {
                    notification.tickerText = list.get(0).name + " " + messageToShow;
                } else {
                    notification.tickerText = "";
                }
            }
        }

        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = led;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 2000;

        currentAlarm = System.currentTimeMillis();
    }

    public void checkAlert(boolean updateNotification, ProtoGlobal.Room.Type type, long roomId) {


        // TODO: 12/7/2016 (molareza) must set G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.GROUP, builder.getRoomId()) on  ChannelSendMessage for receive notification and check it

        idRoom = roomId;

        int vipCheck = checkSpecialNotification(updateNotification, type, roomId);
        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        int checkAlert = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
        if (vipCheck == ENABLE) {
            startActivityPopUpNotification(type);
            updateNotificationAndBadge(updateNotification, type);
        } else if (vipCheck == DEFAULT) {
            if (checkAlert == 1) {
                startActivityPopUpNotification(type);
                updateNotificationAndBadge(updateNotification, type);
            }
        } else if (vipCheck == DISABLE) {
            return;
        }
    }

    private void updateNotificationAndBadge(boolean updateNotification, ProtoGlobal.Room.Type type) {

        sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, idRoom).findFirst();
        switch (type) {
            case CHAT:

                if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getRealmNotificationSetting() != null && realmRoom.getChatRoom().getRealmNotificationSetting().getLedColor() != -1) {

                    led = realmRoom.getChatRoom().getRealmNotificationSetting().getLedColor();

                } else {
                    led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                }
                if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getRealmNotificationSetting() != null && realmRoom.getChatRoom().getRealmNotificationSetting().getVibrate() != -1) {

                    vibrator = realmRoom.getChatRoom().getRealmNotificationSetting().getVibrate();
                } else {
                    vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
                }
                //    popupNotification = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 3);

                if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getRealmNotificationSetting() != null && realmRoom.getChatRoom().getRealmNotificationSetting().getIdRadioButtonSound() != -1) {

                    sound = realmRoom.getChatRoom().getRealmNotificationSetting().getIdRadioButtonSound();
                } else {
                    sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                }
                messagePeriview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);

                break;

            case GROUP:
                if (realmRoom != null && realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRealmNotificationSetting() != null && realmRoom.getGroupRoom().getRealmNotificationSetting().getLedColor() != -1) {
                    led = realmRoom.getGroupRoom().getRealmNotificationSetting().getLedColor();
                } else {
                    led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                }

                if (realmRoom != null && realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRealmNotificationSetting() != null && realmRoom.getGroupRoom().getRealmNotificationSetting().getVibrate() != -1) {

                    vibrator = realmRoom.getGroupRoom().getRealmNotificationSetting().getVibrate();

                } else {
                    vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 1);
                }

                if (realmRoom != null && realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRealmNotificationSetting() != null && realmRoom.getGroupRoom().getRealmNotificationSetting().getIdRadioButtonSound() != -1) {

                    sound = realmRoom.getGroupRoom().getRealmNotificationSetting().getIdRadioButtonSound();
                } else {
                    sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
                }

                messagePeriview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);


                break;
            case CHANNEL:

                if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null && realmRoom.getChannelRoom().getRealmNotificationSetting().getLedColor() != -1) {
                    led = realmRoom.getChannelRoom().getRealmNotificationSetting().getLedColor();
                } else {
                    led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                }

                if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null && realmRoom.getChannelRoom().getRealmNotificationSetting().getVibrate() != -1) {

                    vibrator = realmRoom.getChannelRoom().getRealmNotificationSetting().getVibrate();

                } else {
                    vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 1);
                }

                if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null && realmRoom.getChannelRoom().getRealmNotificationSetting().getIdRadioButtonSound() != -1) {

                    sound = realmRoom.getChannelRoom().getRealmNotificationSetting().getIdRadioButtonSound();
                } else {
                    sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
                }

                messagePeriview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                break;
        }

        if (realmRoom != null) {
            isMute = realmRoom.getMute();
        }
        inAppSound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
        inAppVibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
        inAppPreview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
        inChat_Sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);

        unreadMessageCount = 0;
        isFromOnRoom = true;
        countUnicChat = 0;

        list.clear();
        senderList.clear();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        long userId = realmUserInfo.getUserId();
        String authorHash = realmUserInfo.getAuthorHash();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

        if (!realmRoomMessages.isEmpty()) {
            for (RealmRoomMessage roomMessage : realmRoomMessages) {
                if (roomMessage != null) {
                    if (roomMessage.getUserId() != userId) {
                        if (!roomMessage.getAuthorHash().equals(authorHash)) {// for channel message
                            RealmRoom realmRoom1 = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                            if (realmRoom1 != null && realmRoom1.getType() != null && realmRoom1.getType() != ProtoGlobal.Room.Type.CHANNEL) {
                                if (roomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENT.toString()) || roomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.DELIVERED.toString())) {
                                    unreadMessageCount++;
                                    messageOne = roomMessage.getMessage();
                                    senderId = roomMessage.getUserId();

                                    if (unreadMessageCount == 1 || unreadMessageCount == 2 || unreadMessageCount == 3) {
                                        Item item = new Item();
                                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                                        if (room != null) {
                                            item.name = room.getTitle() + " : ";
                                            item.roomId = room.getId();
                                        }

                                        String text = "";
                                        try {
                                            if (roomMessage.getLogMessage() != null) {
                                                text = roomMessage.getLogMessage();
                                            } else {
                                                text = roomMessage.getMessage();
                                            }

                                            if (text.length() < 1) if (roomMessage.getForwardMessage() != null) text = roomMessage.getForwardMessage().getMessage();
                                            if (text.length() < 1) if (roomMessage.getReplyTo() != null) text = roomMessage.getReplyTo().getMessage();
                                            if (text.length() < 1) text = ActivityPopUpNotification.getTextOfMessageType(roomMessage.getMessageType());
                                        } catch (NullPointerException e) {

                                        }

                                        item.message = text;
                                        item.time = TimeUtils.toLocal(roomMessage.getUpdateTime(), G.CHAT_MESSAGE_TIME);
                                        list.add(item);
                                    }

                                    if (unreadMessageCount == 1) roomId = roomMessage.getRoomId();

                                    if (roomId != roomMessage.getRoomId()) {
                                        isFromOnRoom = false;
                                    }

                                    boolean isAdd = true;
                                    for (int k = 0; k < senderList.size(); k++) {
                                        if (senderList.get(k) == roomMessage.getRoomId()) {
                                            isAdd = false;
                                            break;
                                        }
                                    }

                                    if (isAdd) senderList.add(roomMessage.getRoomId());
                                }
                            } else {
                                Log.i("CCCC", "IS CHANNEL");
                            }
                        }
                    }
                }
            }

            countChannelMessage = 0;
            int countChat = 0;
            RealmResults<RealmRoom> realmRooms = realm.where(RealmRoom.class).findAll();
            for (RealmRoom realmRoom1 : realmRooms) {
                if (realmRoom1.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom1.getUnreadCount() > 0) {
                    countChannelMessage += realmRoom1.getUnreadCount();
                    countChat++;
                }
            }
            countUnicChat += countChat;

            countUnicChat += senderList.size();
        }

        realm.close();

        if (unreadMessageCount + countChannelMessage == 0) {
            if (updateNotification) {
                //  notificationManager.cancel(notificationId);
            }
            try {
                //   ShortcutBadger.applyCount(context, 0);
            } catch (RuntimeException e) {
            }
        } else {
            if (updateNotification) {
                setNotification();

            }
            try {
                // ShortcutBadger.applyCount(context, unreadMessageCount);
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
                sound = R.raw.jing;
                break;
            case 7:
                sound = R.raw.lili;
                break;
            case 8:
                sound = R.raw.msg;
                break;
            case 9:
                sound = R.raw.newa;
                break;
            case 10:
                sound = R.raw.none;
                break;
            case 11:
                sound = R.raw.onelime;
                break;
            case 12:
                sound = R.raw.tone;
                break;
            case 13:
                sound = R.raw.woow;
                break;
        }
        return sound;
    }

    public long[] setVibrator(int vb) {
        long[] intVibrator = new long[]{};

        switch (vb) {
            case 0:
                intVibrator = new long[]{0, 0, 0};
                break;
            case 1:
                intVibrator = new long[]{0, 350, 0};
                break;
            case 2:
                intVibrator = new long[]{0, 200, 0};
                break;
            case 3:
                intVibrator = new long[]{0, 1000, 0};
                break;
            case 4:
                AudioManager am2 = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                switch (am2.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        intVibrator = new long[]{0, 350, 0};
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.i("MyApp", "Vibrate mode");
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.i("MyApp", "Normal mode");
                        break;
                }
                break;
        }
        return intVibrator;
    }

    private void startActivityPopUpNotification(ProtoGlobal.Room.Type type) {


        SharedPreferences sharedPreferences;
        boolean popUpSetting = false;
        int mode = 0;

        switch (type) {
            case CHAT:
                sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, context.MODE_PRIVATE);
                mode = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                break;
            case GROUP:
                sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, context.MODE_PRIVATE);
                String _setting = sharedPreferences.getString(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, context.getResources().getString(R.string.array_No_popup));

                if (_setting.equals(context.getResources().getString(R.string.array_No_popup))) {
                    mode = 0;
                } else if (_setting.equals(context.getResources().getString(R.string.array_Only_when_screen_on))) {
                    mode = 1;
                } else if (_setting.equals(context.getResources().getString(R.string.array_Only_when_screen_off))) {
                    mode = 2;
                } else if (_setting.equals(context.getResources().getString(R.string.array_Always_show_popup))) {
                    mode = 3;
                }

                break;
            case CHANNEL:
                break;
        }

        switch (mode) {

            case 0:
                // no popup
                break;
            case 1:
                //only when screen on
                if (isScreenOn(context)) popUpSetting = true;
                break;
            case 2:
                //only when screen off
                if (!isScreenOn(context)) popUpSetting = true;
                break;
            case 3:
                //always
                popUpSetting = true;
                break;
        }


        if (!G.isAppInFg) {
            if (!AttachFile.isInAttach) {
                if (popUpSetting) {
                    if (getForegroundApp() || ActivityPopUpNotification.isPopUpVisible) { //check that any other program is in background

                        goToPopUpActivity();

                    }
                }
            }
        }


    }

    private boolean getForegroundApp() {

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        try {
            if (taskInfo.get(0).topActivity.getClassName().toString().contains("com.android.launcher")) return true;
        } catch (Exception e) {
        }

        return false;
    }


    private void goToPopUpActivity() {

        if (ActivityPopUpNotification.isPopUpVisible) {
            if (ActivityPopUpNotification.onComplete != null) {
                ActivityPopUpNotification.onComplete.complete(true, "", "");
            }
        } else {
            Intent popUpActivityIntent = new Intent(context, ActivityPopUpNotification.class);
            popUpActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.getApplicationContext().startActivity(popUpActivityIntent);
        }

    }


    /**
     * Is the screen of the device on.
     *
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }


    public static class RemoteActionReciver extends BroadcastReceiver {

        public RemoteActionReciver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            G.helperNotificationAndBadge.cancelNotification();
        }
    }

    private class Item {

        String name = "";
        String message = "";
        String time = "";
        long roomId;
    }

    private int checkSpecialNotification(boolean updateNotification, ProtoGlobal.Room.Type type, long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        switch (type) {
            case CHAT:
                if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getRealmNotificationSetting() != null) {
                    switch (realmRoom.getChatRoom().getRealmNotificationSetting().getNotification()) {
                        case DEFAULT:
                            return DEFAULT;
                        case ENABLE:
                            return ENABLE;
                        case DISABLE:
                            return DISABLE;
                    }
                } else {
                    return DEFAULT;
                }
                break;
            case GROUP:

                if (realmRoom != null && realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRealmNotificationSetting() != null) {

                    switch (realmRoom.getGroupRoom().getRealmNotificationSetting().getNotification()) {
                        case DEFAULT:
                            return DEFAULT;
                        case ENABLE:
                            return ENABLE;
                        case DISABLE:
                            return DISABLE;
                    }
                } else {
                    return DEFAULT;
                }
            case CHANNEL:
                if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null) {

                    switch (realmRoom.getChannelRoom().getRealmNotificationSetting().getNotification()) {
                        case DEFAULT:
                            return DEFAULT;
                        case ENABLE:
                            return ENABLE;
                        case DISABLE:
                            return DISABLE;
                    }
                } else {
                    return DEFAULT;
                }

            default:
                return DEFAULT;
        }
        realm.close();
        return 0;
    }
}
