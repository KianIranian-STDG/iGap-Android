/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmNotificationSetting extends RealmObject {

    private int notification;
    private int vibrate;
    private String sound;
    private int idRadioButtonSound;
    private String smartNotification;
    private int minutes;
    private int times;
    private int ledColor;

    public static RealmNotificationSetting put(Realm realm, final RealmChatRoom realmChatRoom, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom) {
        RealmNotificationSetting realmNotificationSetting = realm.createObject(RealmNotificationSetting.class);
        realmNotificationSetting.setNotification(0);
        realmNotificationSetting.setVibrate(-1);
        realmNotificationSetting.sound(G.fragmentActivity.getResources().getString(R.string.Default_Notification_tone));
        realmNotificationSetting.setIdRadioButtonSound(-1);
        realmNotificationSetting.setSmartNotification(G.fragmentActivity.getResources().getString(R.string.array_Default));
        realmNotificationSetting.setTimes(-1);
        realmNotificationSetting.setMinutes(-1);
        realmNotificationSetting.setLedColor(-1);

        if (realmChatRoom != null) {
            realmChatRoom.setRealmNotificationSetting(realmNotificationSetting);
        } else if (realmGroupRoom != null) {
            realmGroupRoom.setRealmNotificationSetting(realmNotificationSetting);
        } else if (realmChannelRoom != null) {
            realmChannelRoom.setRealmNotificationSetting(realmNotificationSetting);
        }
        return realmNotificationSetting;
    }

    public static void sound(final long roomId, final String sound, final int which, final ProtoGlobal.Room.Type roomType) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                switch (roomType) {
                    case CHAT:
                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                        if (realmChatRoom != null) {
                            realmChatRoom.getRealmNotificationSetting().sound(sound);
                            realmChatRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                        }
                        break;
                    case GROUP:
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.getRealmNotificationSetting().sound(sound);
                            realmGroupRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                        }
                        break;
                    case CHANNEL:
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.getRealmNotificationSetting().sound(sound);
                            realmChannelRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                        }
                        break;
                }
            }
        });
    }

    public static void popupNotification(final long roomId, final ProtoGlobal.Room.Type roomType, final int notification) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                switch (roomType) {
                    case CHAT: {
                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                        if (realmChatRoom != null) {
                            realmChatRoom.getRealmNotificationSetting().setNotification(notification);
                        }
                        break;
                    }
                    case GROUP: {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.getRealmNotificationSetting().setNotification(notification);
                        }
                        break;
                    }
                    case CHANNEL: {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.getRealmNotificationSetting().setNotification(notification);
                        }
                        break;
                    }
                }
            }
        });
    }

    public static void vibrate(final long roomId, final ProtoGlobal.Room.Type roomType, final int vibrateLevel) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                switch (roomType) {
                    case CHAT: {
                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                        if (realmChatRoom != null) {
                            realmChatRoom.getRealmNotificationSetting().setVibrate(vibrateLevel);
                        }
                        break;
                    }
                    case GROUP: {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.getRealmNotificationSetting().setVibrate(vibrateLevel);
                        }
                        break;
                    }
                    case CHANNEL: {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.getRealmNotificationSetting().setVibrate(vibrateLevel);
                        }
                        break;
                    }

                }
            }
        });
    }

    public static void ledColor(final long roomId, final ProtoGlobal.Room.Type roomType, final int ledColor) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                switch (roomType) {
                    case CHAT: {
                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                        if (realmChatRoom != null) {
                            realmChatRoom.getRealmNotificationSetting().setLedColor(ledColor);
                        }
                        break;
                    }
                    case GROUP: {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.getRealmNotificationSetting().setLedColor(ledColor);
                        }
                        break;
                    }
                    case CHANNEL: {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.getRealmNotificationSetting().setLedColor(ledColor);
                        }
                        break;
                    }
                }
            }
        });
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public String getSound() {
        return sound;
    }

    public void sound(String sound) {
        this.sound = sound;
    }

    public int getIdRadioButtonSound() {
        return idRadioButtonSound;
    }

    public void setIdRadioButtonSound(int idRadioButtonSound) {
        this.idRadioButtonSound = idRadioButtonSound;
    }

    public String getSmartNotification() {
        return smartNotification;
    }

    public void setSmartNotification(String smartNotification) {
        this.smartNotification = smartNotification;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getLedColor() {
        return ledColor;
    }

    public void setLedColor(int ledColor) {
        this.ledColor = ledColor;
    }
}
