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
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.USER_DELETED;
import static com.iGap.proto.ProtoGlobal.RoomMessageLog.Type.USER_JOINED;

/**
 * return correct log message with author and target
 */
public class HelperLogMessage {

    public static String logMessage(ProtoGlobal.RoomMessage.Author author, ProtoGlobal.RoomMessageLog messageLog) {

        String authorName = "";
        String targetName = "";
        String logMessage;
        String finalMessage;
        Realm realm = Realm.getDefaultInstance();


        /**
         * detect authorName
         */
        if (author.hasUser()) {
            HelperUserInfo.needUpdateUser(author.getUser().getUserId(), author.getUser().getCacheId());
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


        /**
         * final message
         */
        finalMessage = authorName + " " + logMessage + " " + targetName;

        realm.close();
        return finalMessage;
    }


    private static String logMessageString(ProtoGlobal.RoomMessageLog.Type type) {

        String message = "";

        if (type == USER_JOINED) {
            message = G.context.getResources().getString(R.string.USER_JOINED);
        } else if (type == USER_DELETED) {
            message = G.context.getResources().getString(R.string.USER_DELETED);
        } else if (type == ROOM_CREATED) {
            message = G.context.getResources().getString(R.string.ROOM_CREATED);
        } else if (type == MEMBER_ADDED) {
            //message = "member added";
            message = G.context.getResources().getString(R.string.MEMBER_ADDED);
        } else if (type == MEMBER_KICKED) {
            //message = "member kicked";
            message = G.context.getResources().getString(R.string.MEMBER_KICKED);
        } else if (type == MEMBER_LEFT) {
            //message = "member left";
            message = G.context.getResources().getString(R.string.MEMBER_LEFT);
        } else if (type == ROOM_CONVERTED_TO_PUBLIC) {
            message = G.context.getResources().getString(R.string.ROOM_CONVERTED_TO_PUBLIC);
        } else if (type == ROOM_CONVERTED_TO_PRIVATE) {
            message = G.context.getResources().getString(R.string.ROOM_CONVERTED_TO_PRIVATE);
        } else if (type == MEMBER_JOINED_BY_INVITE_LINK) {
            message = G.context.getResources().getString(R.string.MEMBER_JOINED_BY_INVITE_LINK);
        }

        return message;
    }


}
