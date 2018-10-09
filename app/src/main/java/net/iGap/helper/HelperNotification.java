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
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.view.Display;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityPopUpNotification;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmNotificationSetting;
import net.iGap.realm.RealmRoom;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static net.iGap.G.context;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.PINNED_MESSAGE;


public class HelperNotification {

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
        public String time = "";
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

            inAppSound = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0));
            inAppVibration = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0));
            inAppPreview = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0));
            soundInChat = getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0));
        }

        private boolean getBoolean(int num) {
            return num != 0;
        }

    }

    private class ShowNotification {

        String CHANNEL_ID = "iGap_channel_01";

        private NotificationManager notificationManager;
        private Notification notification;
        private int notificationId = 20;
        private String mHeader = "";
        private String mContent = "";
        private Bitmap mBitmapIcon = null;
        private int unreadMessageCount = 0;
        private int countUniqueChat = 0;
        private int delayAlarm = 5000;
        private long currentAlarm;

        int vibrator;
        int sound;
        int led;
        boolean messagePreview;
        Realm realm;

        ShowNotification() {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = G.context.getString(R.string.channel_name_notification);// The user-visible name of the channel.
                @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        private void show(int vibrator, int sound, int led, boolean messagePreview, Realm realm) {
            int[] result = AppUtils.updateBadgeOnly(realm, -1);
            unreadMessageCount = result[0];
            countUniqueChat = result[1];

            this.vibrator = vibrator;
            this.sound = sound;
            this.led = led;
            this.messagePreview = messagePreview;
            this.realm = realm;

            setNotification();
        }

        private void setNotification() {

            PendingIntent pi;
            Intent intent = new Intent(context, ActivityMain.class);

            if (countUniqueChat == 1) {
                intent.putExtra(ActivityMain.openChat, messageList.get(0).roomId);
            }

            pi = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            getNotificationSmallInfo();

            String messageToShow = messageList.get(0).message;
            if (messageToShow.length() > 40) {
                messageToShow = messageToShow.substring(0, 40);
            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(mBitmapIcon)
                    .setContentTitle(mHeader)
                    .setContentText(mContent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setStyle(getBigStyle())
                    .setContentIntent(pi).build();

            if (currentAlarm + delayAlarm < System.currentTimeMillis()) {
                alarmNotification(messageToShow);
            }

            notificationManager.notify(notificationId, notification);
        }


        private NotificationCompat.InboxStyle getBigStyle() {

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
            if (unreadMessageCount == 1) {

                mHeader = messageList.get(0).name;
                mContent = messageList.get(0).message;
            } else {
                mHeader = context.getString(R.string.igap);
                mContent = messageList.get(0).message;

                String s = "";
                if (countUniqueChat == 1) {
                    s = " " + context.getString(R.string.chat);
                } else if (countUniqueChat > 1) {
                    s = " " + context.getString(R.string.chats);
                }

                mContent = String.format(" %d " + context.getString(R.string.new_messages_from) + " %d " + s, unreadMessageCount, countUniqueChat);
            }

            mBitmapIcon = BitmapFactory.decodeResource(null, R.mipmap.icon);

            if (countUniqueChat == 1) {

                RealmAvatar realmAvatarPath = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, messageList.get(0).senderId).findFirst();
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
            boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
            return useWhiteIcon ? R.mipmap.white_icon : R.mipmap.iconsmal;
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


        void checkPopUp(int popUpMod) {

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


            if (result) {
                if (getForegroundApp() || ActivityPopUpNotification.isPopUpVisible) { //check that any other program is in background

                    if (ActivityPopUpNotification.isPopUpVisible) {
                        if (ActivityPopUpNotification.popUpListener != null) {
                            ActivityPopUpNotification.popUpListener.onMessageReceive();
                        }
                    } else {
                        Intent intent = new Intent(context, ActivityPopUpNotification.class);
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

            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

            try {
                if (taskInfo.get(0).topActivity.getClassName().toString().toLowerCase().contains("launcher"))
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

    void addMessage(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType, RealmRoom room, Realm realm) {

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
                showNotification.show(vibrator, sound, led, messagePreview, realm);
            }

            if (!G.isAppInFg && !AttachFile.isInAttach) {
                showPopUp.checkPopUp(popUpMode);
            }


        }


    }

    private void add(long roomId, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType, RealmRoom room) {

        StructNotification sn = new StructNotification();
        sn.roomId = roomId;
        sn.roomMessage = roomMessage;
        sn.roomType = roomType;
        sn.time = TimeUtils.toLocal(roomMessage.getUpdateTime(), G.CHAT_MESSAGE_TIME);
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
        String text = "";
        try {
            if (roomMessage.hasLog()) {
                if (roomMessage.getLog().getType() == PINNED_MESSAGE) {
                    text = roomMessage.getReplyTo().getMessage();
                } else if (roomMessage.getReplyTo() != null) {
                    text = AppUtils.conversionMessageType(roomMessage.getReplyTo().getMessageType());
                }
            } else {
                text = roomMessage.hasForwardFrom() ? roomMessage.getForwardFrom().getMessage() : roomMessage.getMessage();
            }

            if (text.length() < 1) {
                if (roomMessage.hasReplyTo())
                    text = roomMessage.getReplyTo().getMessage();
            }

            if (text.length() < 1) {
                text = getTextOfMessageType(roomMessage.getMessageType());
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
        showNotification.notificationManager.cancel(showNotification.notificationId);
    }

    public static class RemoteActionReceiver extends BroadcastReceiver {

        public RemoteActionReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            HelperNotification.getInstance().cancelNotification();
        }
    }

}