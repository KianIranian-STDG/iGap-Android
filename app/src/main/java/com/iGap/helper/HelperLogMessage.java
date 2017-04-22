/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import com.iGap.G;
import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;

import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_ADDED;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_JOINED_BY_INVITE_LINK;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_KICKED;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_LEFT;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_CONVERTED_TO_PRIVATE;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_CONVERTED_TO_PUBLIC;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_CREATED;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_DELETED;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.USER_DELETED;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.USER_JOINED;

/**
 * return correct log message with author and target
 */
public class HelperLogMessage {

    public static String logMessage(long roomId, ProtoGlobal.RoomMessage.Author author, ProtoGlobal.RoomMessageLog messageLog) {

        String authorName = "";
        String targetName = "";
        String logMessage;
        String persianResult = null;
        Realm realm = Realm.getDefaultInstance();
        ProtoGlobal.Room.Type typeRoom = null;

        /**
         * detect authorName
         */
        if (author.hasUser()) {
            HelperInfo.needUpdateUser(author.getUser().getUserId(), author.getUser().getCacheId());
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, author.getUser().getUserId()).findFirst();
            if (realmRegisteredInfo != null) {
                authorName = realmRegisteredInfo.getDisplayName();
            }
        } else if (author.hasRoom()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, author.getRoom().getRoomId()).findFirst();
            if (realmRoom != null) {
                authorName = realmRoom.getTitle();
            }
        }

        /**
         * detect targetName
         */
        if (messageLog.hasTargetUser()) {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, messageLog.getTargetUser().getId()).findFirst();
            if (realmRegisteredInfo != null) {
                targetName = realmRegisteredInfo.getDisplayName();
            }
        }

        /**
         * detect log message
         */
        logMessage = logMessageString(messageLog.getType());
        String englishResult = "";

        /**
         * final message
         */
        String finalTypeRoom;
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null && realmRoom.getType() != null) {
            typeRoom = realmRoom.getType();

            if (typeRoom.toString().equals("CHANNEL")) {
                finalTypeRoom = G.context.getResources().getString(R.string.channel);
            } else if (typeRoom.toString().equals("GROUP")) {
                finalTypeRoom = G.context.getResources().getString(R.string.group);
            } else {
                finalTypeRoom = G.context.getResources().getString(R.string.conversation);
            }
        } else {
            finalTypeRoom = G.context.getResources().getString(R.string.conversation);
        }

        englishResult = authorName + " " + logMessage + " " + targetName;

        switch (messageLog.getType()) {
            case USER_JOINED:
                persianResult = authorName + " " + logMessage;
                break;
            case USER_DELETED:
                persianResult = authorName + " " + logMessage;
                break;
            case ROOM_CREATED:

                if ((typeRoom == null) || (typeRoom.toString().equals("CHANNEL"))) {
                    persianResult = logMessage + " " + finalTypeRoom + " " + authorName;
                } else {
                    persianResult = logMessage + " " + finalTypeRoom + " " + G.context.getResources().getString(R.string.prefix) + " " + authorName;
                }
                break;
            case MEMBER_ADDED:
                persianResult = logMessage + " " + targetName + " " + G.context.getResources().getString(R.string.prefix) + " " + authorName;
                break;
            case MEMBER_KICKED:
                persianResult = logMessage + " " + targetName + " " + G.context.getResources().getString(R.string.prefix) + " " + authorName;
                break;
            case MEMBER_LEFT:
                persianResult = authorName + " " + finalTypeRoom + " " + logMessage;
                break;
            case ROOM_CONVERTED_TO_PUBLIC:
                persianResult = finalTypeRoom + " " + authorName + " " + logMessage;
                break;
            case ROOM_CONVERTED_TO_PRIVATE:
                persianResult = finalTypeRoom + " " + authorName + " " + logMessage;
                break;
            case MEMBER_JOINED_BY_INVITE_LINK:
                persianResult = authorName + " " + logMessage + " " + finalTypeRoom + " " + G.context.getResources().getString(R.string.MEMBER_JOINED_BY_INVITE_LINK_2);

                break;
            case ROOM_DELETED:
                persianResult = logMessage + " " + finalTypeRoom + " " + G.context.getResources().getString(R.string.prefix) + " " + authorName;
                break;
        }

        realm.close();

        return (englishResult + "\n" + persianResult);
    }

    private static String logMessageString(ProtoGlobal.RoomMessageLog.Type type) {

        int message = 0;

        if (type == USER_JOINED) {
            message = R.string.USER_JOINED;
        } else if (type == USER_DELETED) {
            message = R.string.USER_DELETED;
        } else if (type == ROOM_CREATED) {
            message = R.string.ROOM_CREATED;
        } else if (type == MEMBER_ADDED) {
            //message = "member added";
            message = R.string.MEMBER_ADDED;
        } else if (type == MEMBER_KICKED) {
            //message = "member kicked";
            message = R.string.MEMBER_KICKED;
        } else if (type == MEMBER_LEFT) {
            //message = "member left";
            message = R.string.MEMBER_LEFT;
        } else if (type == ROOM_CONVERTED_TO_PUBLIC) {
            message = R.string.ROOM_CONVERTED_TO_PUBLIC;
        } else if (type == ROOM_CONVERTED_TO_PRIVATE) {
            message = R.string.ROOM_CONVERTED_TO_PRIVATE;
        } else if (type == MEMBER_JOINED_BY_INVITE_LINK) {
            message = R.string.MEMBER_JOINED_BY_INVITE_LINK;
        } else if (type == ROOM_DELETED) {
            message = R.string.Room_Deleted;
        }

        return "*" + message + "*";
    }

    public static String convertLogmessage(String message) {

        if (message == null || message.length() == 0) return null;

        String result = "";

        String str[] = message.split("\n");
        String tmp;

        try {
            if (HelperCalander.isLanguagePersian) {
                tmp = str[1];
            } else {
                tmp = str[0];
            }
            int indexFirst = tmp.indexOf("*");
            int indexLast = tmp.lastIndexOf("*");
            result = tmp.substring(0, indexFirst) + G.context.getString(Integer.parseInt(tmp.substring(indexFirst + 1, indexLast))) + tmp.substring(indexLast + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
