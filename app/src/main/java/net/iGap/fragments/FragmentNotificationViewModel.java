/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.databinding.ObservableField;
import android.view.View;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import java.util.Observable;
import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmChatRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmNotificationSetting;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class FragmentNotificationViewModel extends Observable {

    private Realm realm;
    private long roomId;
    private ProtoGlobal.Room.Type roomType;

    private static final int DEFAULT = 0;
    private static final int ENABLE = 1;
    private static final int DISABLE = 2;

    private RealmNotificationSetting realmNotificationSetting;
    private int realmNotification = 0;

    public ObservableField<String> notificationState;

    public FragmentNotificationViewModel(long roomId) {
        this.roomId = roomId;

        realm = Realm.getDefaultInstance();
        roomType = RealmRoom.detectType(roomId);
    }

    public String getNotificationState() {
        notificationState = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.array_Default));

        String notificationState = G.fragmentActivity.getResources().getString(R.string.array_Default);

        int popupNotification = realmNotification;
        switch (popupNotification) {
            case DEFAULT:
                notificationState = G.fragmentActivity.getResources().getString(R.string.array_Default);
                break;
            case ENABLE:
                notificationState = G.fragmentActivity.getResources().getString(R.string.array_enable);
                break;
            case DISABLE:
                notificationState = G.fragmentActivity.getResources().getString(R.string.array_Disable);
                break;
        }
        return notificationState;
    }

    public void onNotificationStateClick(View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.notifications_notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                switch (which) {
                    case DEFAULT: {
                        realmNotification = DEFAULT;
                        notificationState.set(G.context.getString(R.string.array_Default));
                        RealmNotificationSetting.popupNotification(roomId, roomType, DEFAULT);
                        break;
                    }
                    case ENABLE: {
                        realmNotification = ENABLE;
                        notificationState.set(G.context.getString(R.string.array_enable));
                        RealmNotificationSetting.popupNotification(roomId, roomType, ENABLE);
                        break;
                    }
                    case DISABLE: {
                        realmNotification = DISABLE;
                        notificationState.set(G.context.getString(R.string.array_Disable));
                        RealmNotificationSetting.popupNotification(roomId, roomType, DISABLE);
                        break;
                    }
                }
            }
        }).show();
    }

    private void getInfos() {
        switch (roomType) {
            case GROUP: {

                Realm realm = Realm.getDefaultInstance();

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        if (realmGroupRoom.getRealmNotificationSetting() == null) {
                            setRealm(realm, realmGroupRoom, null, null);
                        } else {
                            realmNotificationSetting = realmGroupRoom.getRealmNotificationSetting();
                        }
                        getRealm();
                    }
                }

                realm.close();
            }

            break;
            case CHANNEL: {
                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        if (realmChannelRoom.getRealmNotificationSetting() == null) {
                            setRealm(realm, null, realmChannelRoom, null);
                        } else {
                            realmNotificationSetting = realmChannelRoom.getRealmNotificationSetting();
                        }
                        getRealm();
                    }
                }

                realm.close();
                break;
            }
            case CHAT: {

                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null && realmRoom.getChatRoom() != null) {
                    RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                    if (realmChatRoom != null) {
                        if (realmChatRoom.getRealmNotificationSetting() == null) {
                            setRealm(realm, null, null, realmChatRoom);
                        } else {
                            realmNotificationSetting = realmChatRoom.getRealmNotificationSetting();
                        }
                        getRealm();
                    }
                }

                realm.close();

                break;
            }
        }
    }

    private void setRealm(Realm realm, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom, final RealmChatRoom realmChatRoom) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmNotificationSetting = RealmNotificationSetting.put(realm, realmChatRoom, realmGroupRoom, realmChannelRoom);
            }
        });
    }

    private void getRealm() {
        realmNotification = realmNotificationSetting.getNotification();
    }

    public void destroy() {
        realm.close();
    }
}
