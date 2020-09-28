package net.iGap.helper;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityPopUpNotification;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.fragments.FragmentChat;
import net.iGap.libs.Tuple;
import net.iGap.model.AccountUser;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmNotificationSetting;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static android.content.Context.NOTIFICATION_SERVICE;
import static net.iGap.G.context;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.PINNED_MESSAGE;


public class HelperNotification {

    static final String KEY_ROOM_ID = "KEY_ROOM_ID";
    static final String KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID";
    static final String REPLY_ACTION = "REPLY_ACTION";
    static final String UNREAD_ACTION = "UNREAD_ACTION";
    static final String KEY_REPLY = "KEY_REPLY";
    static final String KEY_CHAT_TYPE = "KEY_CHAT_TYPE";
    static final String KEY_MESSAGE_ID = "KEY_MESSAGE_ID";

    private static HelperNotification _HelperNotification;

    public boolean isChatRoomNow = false;

    private int listSize = 20;
    private ArrayList<StructNotification> messageList = new ArrayList<>(listSize + 1);
    private SettingValue settingValue;
    private ShowNotification showNotification;
    private ShowPopUp showPopUp;

    //**************************************************************

    public class StructNotification {
        public long roomId;
        public long senderId;
        public ProtoGlobal.RoomMessage roomMessage;
        public ProtoGlobal.Room.Type roomType;
        public String name = "";
        public String message = "";
        public long time;
        public String initialize;
        public String color;
    }

    private class SettingValue {

        boolean m_alert;
        boolean m_preview;
        int m_ledColor;
        int m_vibration;
        int m_popUp;
        int m_sound;

        boolean g_alert;
        boolean g_preview;
        int g_ledColor;
        int g_vibration;
        int g_popUp;
        int g_sound;

        boolean separateNotification;

        boolean inAppSound;
        boolean inAppVibration;
        boolean inAppPreview;
        boolean soundInChat;


        SettingValue() {

            SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

            m_alert = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1));
            m_preview = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1));
            m_ledColor = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
            m_vibration = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0);
            m_popUp = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
            m_sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);

            g_alert = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1));
            g_preview = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1));
            g_ledColor = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
            g_vibration = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0);
            g_popUp = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
            g_sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);

            separateNotification = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SEPARATE_NOTIFICATION, 1));

            inAppSound = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND_NEW, 1));
            inAppVibration = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE_NEW, 1));
            inAppPreview = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW_NEW, 1));
            soundInChat = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND_NEW, 1));
        }

        private boolean getBoolean(int num) {
            return num != 0;
        }

    }

    private class ShowNotification {

        String CHANNEL_ID = "iGap_channel_01";

        private NotificationManager notificationManager;
        private Notification notification;
        private String mHeader = "";
        private String mContent = "";
        private Bitmap mBitmapIcon = null;
        private int unreadMessageCount = 0;
        private int countUniqueChat = 0;
        private int delayAlarm = 5000;
        private long currentAlarm;
        private int notificationIconSrc;
        private long mTime = 0;

        int vibrator;
        int sound;
        int led;
        boolean messagePreview;
        Realm realm;

        ShowNotification() {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = G.context.getString(R.string.channel_name_notification);// The user-visible name of the channel.
                @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        private void show(int vibrator, int sound, int led, boolean messagePreview, Realm realm, AccountUser accountUser) {
            int[] result = AppUtils.updateBadgeOnly(realm, -1);
            unreadMessageCount = result[0];
            countUniqueChat = result[1];

            this.vibrator = vibrator;
            this.sound = sound;
            this.led = led;
            this.messagePreview = messagePreview;
            this.realm = realm;

            setNotification(accountUser);
        }

        private void setNotification(AccountUser accountUser) {
            int notificationId = 21;
            if (settingValue.separateNotification) {
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.KEY_NOTIF_KEYS, Context.MODE_PRIVATE);
                notificationId = sharedPreferences.getInt("NotifNextId", 22);
                int roomNotifId = sharedPreferences.getInt(String.valueOf(messageList.get(0).roomId), -1);
                if (roomNotifId == -1) {
                    sharedPreferences.edit().putInt(String.valueOf(messageList.get(0).roomId), notificationId).apply();
                    sharedPreferences.edit().putInt("NotifNextId", notificationId + 1).apply();
                } else {
                    notificationId = roomNotifId;
                }
            }

            PendingIntent pi;
            Intent intent = new Intent(context, ActivityMain.class);

            if (countUniqueChat == 1 || settingValue.separateNotification) {
                intent.putExtra(ActivityMain.openChat, messageList.get(0).roomId);
                intent.putExtra(ActivityMain.userId, accountUser.getId());
            }

            pi = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            getNotificationSmallInfo();

            String messageToShow = messageList.get(0).message;
            if (messageToShow.length() > 40) {
                messageToShow = messageToShow.substring(0, 40);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(mBitmapIcon)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle(mHeader)
                    .setContentText(mContent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(getBigStyle())
                    .setContentIntent(pi);

            if (AccountManager.getInstance().getUserAccountList().size() > 2) {
                builder.setSubText(accountUser.getName());
            }

            //maybe time not set , this if not work and time of notify will set .
            if (mTime != 0) {
                builder.setWhen(mTime * 1000);
            }

            if (settingValue.separateNotification && AccountManager.getInstance().getCurrentUser().equals(accountUser)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && messageList.get(0).roomType != ProtoGlobal.Room.Type.CHANNEL) {
                    builder.addAction(getReplayAction(notificationId));
                }
                builder.addAction(getUnreadAction(notificationId));
            }

            notification = builder.build();

            if (currentAlarm + delayAlarm < System.currentTimeMillis()) {
                alarmNotification(messageToShow);
            }

            notificationManager.notify(notificationId, notification);
        }

        private NotificationCompat.Action getReplayAction(int notificationId) {
            String label = G.context.getResources().getString(R.string.replay);
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY).setLabel(label).build();
            Intent intent = new Intent(context, RemoteActionReceiver.class);
            intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
            intent.putExtra(KEY_ROOM_ID, messageList.get(0).roomId);
            intent.putExtra(KEY_CHAT_TYPE, messageList.get(0).roomType);
            intent.setAction(REPLY_ACTION);
            PendingIntent replyPendingIntent = PendingIntent.getBroadcast(G.context, notificationId + 5000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            return new NotificationCompat.Action.Builder(notificationIconSrc, label, replyPendingIntent).addRemoteInput(remoteInput).build();
        }

        private NotificationCompat.Action getUnreadAction(int notificationId) {

            Intent intent = new Intent(context, RemoteActionReceiver.class);
            intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
            intent.putExtra(KEY_ROOM_ID, messageList.get(0).roomId);
            intent.putExtra(KEY_MESSAGE_ID, messageList.get(0).roomMessage.getMessageId());
            intent.putExtra(KEY_CHAT_TYPE, messageList.get(0).roomType);
            intent.setAction(UNREAD_ACTION);
            PendingIntent unreadIntent = PendingIntent.getBroadcast(G.context, notificationId + 10000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            return new NotificationCompat.Action.Builder(notificationIconSrc, G.context.getString(R.string.mark_as_unread), unreadIntent).build();
        }

        private NotificationCompat.InboxStyle getBigStyle() {

            if (settingValue.separateNotification) {
                return null;
            }

            NotificationCompat.InboxStyle _style = new NotificationCompat.InboxStyle();

            if (countUniqueChat == 1) {
                _style.setBigContentTitle(messageList.get(0).name);

                for (int i = 0; i < unreadMessageCount && i < 3; i++) {
                    _style.addLine(messageList.get(i).message);
                }
            } else {
                for (int i = 0; i < unreadMessageCount && i < 3; i++) {
                    _style.addLine(messageList.get(i).name + " " + messageList.get(i).message);
                }
            }

            if (unreadMessageCount > 3) {
                _style.addLine("....");
            }

            String chatCount = "";

            if (countUniqueChat == 1) {
                chatCount = context.getString(R.string.from) + " " + countUniqueChat + " " + context.getString(R.string.chat);
            } else if (countUniqueChat > 1) {
                chatCount = context.getString(R.string.from) + " " + countUniqueChat + " " + context.getString(R.string.chats);
            }

            String newMessage = "";
            if (unreadMessageCount == 1) {
                newMessage = context.getString(R.string.new_message);
                chatCount = "";
            } else {
                newMessage = context.getString(R.string.new_messages);
            }

            String _summary = unreadMessageCount + " " + newMessage + " " + chatCount;
            _style.setSummaryText(_summary);

            return _style;
        }

        private void getNotificationSmallInfo() {

            String avatarPath = null;
            if (unreadMessageCount == 1 || settingValue.separateNotification) {

                mHeader = messageList.get(0).name;
                mContent = messageList.get(0).message;
                mTime = messageList.get(0).time;
            } else {
                mHeader = context.getString(R.string.igap);
                mContent = messageList.get(0).message;
                mTime = messageList.get(0).time;

                String s = "";
                if (countUniqueChat == 1) {
                    s = " " + context.getString(R.string.chat);
                } else if (countUniqueChat > 1) {
                    s = " " + context.getString(R.string.chats);
                }

                mContent = String.format(" %d " + context.getString(R.string.new_messages_from) + " %d " + s, unreadMessageCount, countUniqueChat);
            }

            mBitmapIcon = BitmapFactory.decodeResource(null, R.mipmap.icon);

            if (countUniqueChat == 1 || settingValue.separateNotification) {
                RealmAvatar realmAvatarPath = RealmAvatar.getLastAvatar(messageList.get(0).senderId, realm);
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

                        Bitmap bitmap = BitmapFactory.decodeFile(avatarPath, option);
                        if (bitmap != null) {
                            mBitmapIcon = bitmap;
                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        private int getNotificationIcon() {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                notificationIconSrc = R.mipmap.white_icon;
            } else {
                notificationIconSrc = R.mipmap.iconsmal;
            }

            return notificationIconSrc;
        }

        private void alarmNotification(String messageToShow) {

            notification.tickerText = "";
            notification.vibrate = new long[]{0, 0, 0};
            notification.sound = null;

            if (G.isAppInFg) {
                if (!isChatRoomNow) {

                    if (settingValue.inAppVibration) {
                        notification.vibrate = setVibrator(vibrator);
                    }
                    if (settingValue.inAppSound) {
                        notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
                    }
                    if (settingValue.inAppPreview) {
                        notification.tickerText = messageList.get(0).name + " " + messageToShow;
                    }
                } else if (settingValue.soundInChat) {
                    notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
                }
            } else {
                notification.vibrate = setVibrator(vibrator);
                notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));

                if (messagePreview) {
                    notification.tickerText = messageList.get(0).name + " " + messageToShow;
                }
            }

            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification.ledARGB = led;
            notification.ledOnMS = 1000;
            notification.ledOffMS = 2000;

            currentAlarm = System.currentTimeMillis();
        }

        private int setSound(int which) {
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

        private long[] setVibrator(int vb) {

            long[] intVibrator = new long[]{};

            AudioManager am2 = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);

            if (am2 != null && am2.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return new long[]{0, 0, 0};
            }

            switch (vb) {
                case 0:
                    intVibrator = new long[]{0, 300, 0};
                    break;
                case 1:
                    intVibrator = new long[]{0, 200, 0};
                    break;
                case 2:
                    intVibrator = new long[]{0, 700, 0};
                    break;
                case 3:

                    switch (am2.getRingerMode()) {
                        case AudioManager.RINGER_MODE_SILENT:
                            intVibrator = new long[]{0, 0, 0};
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            intVibrator = new long[]{0, 300, 0};
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            intVibrator = new long[]{0, 0, 0};
                            break;
                    }
                    break;
                case 4:
                    intVibrator = new long[]{0, 0, 0};
                    break;
            }
            return intVibrator;
        }
    }

    private class ShowPopUp {


        void checkPopUp(int popUpMod, long id) {

            boolean result = false;

            switch (popUpMod) {

                case 0:
                    // no popup
                    break;
                case 1:
                    //only when screen on
                    if (isScreenOn(context)) {
                        result = true;
                    }
                    break;
                case 2:
                    //only when screen off
                    if (!isScreenOn(context)) {
                        result = true;
                    }
                    break;
                case 3:
                    //always
                    result = true;
                    break;
            }


            if (result && id == AccountManager.getInstance().getCurrentUser().getId()) {
                if (getForegroundApp() || ActivityPopUpNotification.isPopUpVisible) { //check that any other program is in background

                    if (ActivityPopUpNotification.isPopUpVisible) {
                        if (ActivityPopUpNotification.popUpListener != null) {
                            ActivityPopUpNotification.popUpListener.onMessageReceive();
                        }
                    } else {
                        Intent intent = new Intent(context, ActivityPopUpNotification.class);
                        intent.putExtra(ActivityMain.userId, id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }

                }
            }


        }


        private boolean isScreenOn(Context context) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                @SuppressLint("WrongConstant") DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
                boolean screenOn = false;
                for (Display display : dm.getDisplays()) {
                    if (display.getState() != Display.STATE_OFF) {
                        screenOn = true;
                    }
                }
                return screenOn;
            } else {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                return pm.isScreenOn();
            }
        }

        private boolean getForegroundApp() {

            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

            try {
                if (taskInfo.get(0).topActivity.getClassName().toLowerCase().contains("launcher"))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }


    }

    //**************************************************************

    public static HelperNotification getInstance() {
        if (_HelperNotification == null) {
            _HelperNotification = new HelperNotification();
            _HelperNotification.updateSettingValue();
            _HelperNotification.init();
        }
        return _HelperNotification;
    }

    public void updateSettingValue() {
        settingValue = new SettingValue();
    }

    private void init() {
        showNotification = new ShowNotification();
        showPopUp = new ShowPopUp();
    }

    public void addMessage(Realm realm, long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType, AccountUser accountUser) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (room != null) {
            addMessage(roomId, roomMessage, roomType, room, realm, accountUser);
        }
    }

    public void addMessage(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType, RealmRoom room, Realm realm, AccountUser accountUser) {

        if (roomId == FragmentChat.lastChatRoomId) {
            return;
        }

        if (!room.getMute()) {

            RealmNotificationSetting notificationSetting = null;
            boolean defaultAlert = false;
            int popUpMode = 0;
            int vibrator = 0;
            int sound = 0;
            int led = 0;
            boolean messagePreview = false;

            switch (roomType) {
                case CHAT:
                    if (room.getChatRoom() != null) {
                        notificationSetting = room.getChatRoom().getRealmNotificationSetting();
                    }
                    defaultAlert = settingValue.m_alert;
                    vibrator = settingValue.m_vibration;
                    sound = settingValue.m_sound;
                    led = settingValue.m_ledColor;
                    messagePreview = settingValue.m_preview;
                    popUpMode = settingValue.m_popUp;
                    break;
                case GROUP:
                    if (room.getGroupRoom() != null) {
                        notificationSetting = room.getGroupRoom().getRealmNotificationSetting();
                    }
                    defaultAlert = settingValue.g_alert;
                    vibrator = settingValue.g_vibration;
                    sound = settingValue.g_sound;
                    led = settingValue.g_ledColor;
                    messagePreview = settingValue.g_preview;
                    popUpMode = settingValue.g_popUp;
                    break;
                case CHANNEL:
                    if (room.getChannelRoom() != null) {
                        notificationSetting = room.getChannelRoom().getRealmNotificationSetting();
                    }
                    defaultAlert = settingValue.g_alert;
                    vibrator = settingValue.g_vibration;
                    sound = settingValue.g_sound;
                    led = settingValue.g_ledColor;
                    messagePreview = settingValue.g_preview;
                    popUpMode = settingValue.g_popUp;
                    break;
            }

            if (notificationSetting != null) {
                if (notificationSetting.getNotification() == 2 || (notificationSetting.getNotification() == 0 && !defaultAlert)) { // notification in selected room is disable
                    return;
                }

                if (notificationSetting.getVibrate() != -1) {
                    vibrator = notificationSetting.getVibrate();
                }
                if (notificationSetting.getIdRadioButtonSound() != -1) {
                    sound = notificationSetting.getIdRadioButtonSound();
                }
                if (notificationSetting.getLedColor() != -1) {
                    led = notificationSetting.getLedColor();
                }
            } else if (!defaultAlert) {
                return;
            }


            add(roomId, roomMessage, roomType, room);


            if ((!G.isAppInFg && !AttachFile.isInAttach) || settingValue.inAppPreview || settingValue.inAppSound || settingValue.inAppVibration || settingValue.soundInChat) {
                showNotification.show(vibrator, sound, led, messagePreview, realm, accountUser);
            }

            if (!G.isAppInFg && !AttachFile.isInAttach) {
                showPopUp.checkPopUp(popUpMode, accountUser.getId());
            }

        }


    }

    private void add(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType, RealmRoom room) {

        StructNotification sn = new StructNotification();
        sn.roomId = roomId;
        sn.roomMessage = roomMessage;
        sn.roomType = roomType;
        sn.time = roomMessage.getUpdateTime();
        sn.message = parseMessage(roomMessage);
        sn.name = room.getTitle() + ":";
        sn.senderId = room.getType() == ProtoGlobal.Room.Type.CHAT ? room.getChatRoom().getPeerId() : room.getId();
        sn.initialize = room.getInitials();
        sn.color = room.getColor();

        messageList.add(0, sn);

        if (messageList.size() > listSize) {
            messageList.remove(listSize);
        }

    }

    private String getTextOfMessageType(ProtoGlobal.RoomMessageType messageType) {

        switch (messageType) {
            case VOICE:
                return G.context.getString(R.string.voice_message);
            case VIDEO:
                return G.context.getString(R.string.video_message);
            case FILE:
                return G.context.getString(R.string.file_message);
            case AUDIO:
                return G.context.getString(R.string.audio_message);
            case IMAGE:
                return G.context.getString(R.string.image_message);
            case CONTACT:
                return G.context.getString(R.string.contact_message);
            case GIF:
                return G.context.getString(R.string.gif_message);
            case LOCATION:
                return G.context.getString(R.string.location_message);
        }

        return "";
    }

    private String parseMessage(ProtoGlobal.RoomMessage roomMessage) {
        if (PassCode.getInstance().isPassCode() && ActivityMain.isLock) {
            return context.getString(R.string.new_message_notif);
        }

        String text = "";
        try {
            if (roomMessage.hasLog()) {
                if (roomMessage.getLog().getType() == PINNED_MESSAGE) {
                    text = roomMessage.getReplyTo().getMessage();
                } else if (roomMessage.getLog().getType() == ProtoGlobal.RoomMessageLog.Type.MEMBER_JOINED_BY_INVITE_LINK) {
                    text = HelperLogMessage.getLogTypeString(roomMessage.getLog().getType(), roomMessage.getAuthor());
                    // after showing notification we must update it after getting user name by bellow line
                    //roomMessage.getLog().getTargetUser().getId()
                } else if (roomMessage.getLog().getType() == ProtoGlobal.RoomMessageLog.Type.MEMBER_ADDED) {
                    text = HelperLogMessage.getLogTypeString(roomMessage.getLog().getType(), roomMessage.getAuthor());
                } else if (roomMessage.getLog().getType() == ProtoGlobal.RoomMessageLog.Type.MEMBER_KICKED) {
                    text = HelperLogMessage.getLogTypeString(roomMessage.getLog().getType(), roomMessage.getAuthor());
                } else if (roomMessage.getLog().getType() == ProtoGlobal.RoomMessageLog.Type.MEMBER_LEFT) {
                    text = HelperLogMessage.getLogTypeString(roomMessage.getLog().getType(), roomMessage.getAuthor());
                } else if (roomMessage.getLog().getType() == ProtoGlobal.RoomMessageLog.Type.MISSED_VOICE_CALL) {
                    text = HelperLogMessage.getLogTypeString(roomMessage.getLog().getType(), roomMessage.getAuthor());
                } else if (roomMessage.getLog().getType() == ProtoGlobal.RoomMessageLog.Type.MISSED_VIDEO_CALL) {
                    text = HelperLogMessage.getLogTypeString(roomMessage.getLog().getType(), roomMessage.getAuthor());
                } else if (roomMessage.getReplyTo() != null) {
                    text = AppUtils.conversionMessageType(roomMessage.getReplyTo().getMessageType());
                }
            } else {
                ProtoGlobal.RoomMessage rm = roomMessage;
                if (roomMessage.hasForwardFrom()) {
                    rm = roomMessage.getForwardFrom();
                } else if (roomMessage.hasReplyTo()) {
                    rm = roomMessage;
                }

                text = rm.getMessage();
                ArrayList<Tuple<Integer, Integer>> a = AbstractMessage.getBoldPlaces(text);
                text = AbstractMessage.removeBoldMark(text, a);

                if (rm.getMessageType() == ProtoGlobal.RoomMessageType.STICKER) {
                    text = G.context.getString(R.string.sticker_message) + " : " + text;
                } else if (text.length() < 1) {
                    text = getTextOfMessageType(rm.getMessageType());
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return text;
    }

    public ArrayList<StructNotification> getMessageList() {
        return messageList;
    }

    public void cancelNotification() {
        showNotification.notificationManager.cancelAll();
    }

    public void cancelNotification(long roomId) {
        if (settingValue.separateNotification) {
            SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.KEY_NOTIF_KEYS, Context.MODE_PRIVATE);
            int roomNotifId = sharedPreferences.getInt(String.valueOf(roomId), -1);
            if (roomNotifId == -1) {
                return;
            }
            showNotification.notificationManager.cancel(roomNotifId);
        }
    }

    public static class RemoteActionReceiver extends BroadcastReceiver {

        public RemoteActionReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || intent.getAction() == null) {
                return;
            }

            String message = "";
            int notificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);
            long roomId = intent.getLongExtra(KEY_ROOM_ID, 0);
            ProtoGlobal.Room.Type chatType = (ProtoGlobal.Room.Type) intent.getSerializableExtra(KEY_CHAT_TYPE);

            switch (intent.getAction()) {
                case REPLY_ACTION:

                    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                    if (remoteInput != null) {
                        message = (String) remoteInput.getCharSequence(KEY_REPLY);
                    }

                    if (notificationId > 0) {
                        HelperNotification.getInstance().showNotification.notificationManager.cancel(notificationId);
                    }

                    if (message != null && message.length() > 0 && roomId > 0) {
                        RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(roomId, message);
                        HelperRealm.copyOrUpdateToRealm(roomMessage);
                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).newBuilder(chatType, ProtoGlobal.RoomMessageType.TEXT, roomId).message(message).sendMessage(roomMessage.getMessageId() + "");
                    }

                    break;

                case UNREAD_ACTION:
                    long messageId = intent.getLongExtra(KEY_MESSAGE_ID, 0);

                    if (notificationId > 0) {
                        HelperNotification.getInstance().showNotification.notificationManager.cancel(notificationId);
                    }

                    if (roomId > 0 && messageId > 0) {

                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DbManager.getInstance().doRealmTask(realm -> {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmRoom.setCount(realm, roomId, 0);
                                        }
                                    }, () -> {

                                        DbManager.getInstance().doRealmTask(realm2 -> {
                                            if (chatType == ProtoGlobal.Room.Type.CHAT || chatType == ProtoGlobal.Room.Type.GROUP) {
                                                RealmRoomMessage.makeSeenAllMessageOfRoom(roomId);
                                            }
                                            AppUtils.updateBadgeOnly(realm2, roomId);
                                        });

                                    });
                                });
                            }
                        }, 5);
                    }
                    break;
            }
        }
    }


    @SuppressLint("WrongConstant")
    public static void sendDeepLink(Map<String, String> data, String title, String body) {

        String CHANNEL_ID = "1032";
        String deepLink = data.get(ActivityMain.DEEP_LINK);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, ActivityMain.class);
        intent.setAction(ActivityMain.OPEN_DEEP_LINK);
        intent.putExtra(ActivityMain.DEEP_LINK, deepLink);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        notificationManager.notify(1032, notification);
    }
}