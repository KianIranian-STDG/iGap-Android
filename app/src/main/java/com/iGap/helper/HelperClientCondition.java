package com.iGap.helper;

import android.util.Log;
import com.iGap.proto.ProtoClientCondition;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;
import java.util.List;

import static com.iGap.helper.HelperMessageResponse.computeLastMessageId;

/**
 * helper client condition for set info to RealmClientCondition
 */
public class HelperClientCondition {

    public static ProtoClientCondition.ClientCondition.Builder computeClientCondition() {

        Realm realm = Realm.getDefaultInstance();
        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        for (RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
            ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
            room.setRoomId(realmClientCondition.getRoomId());
            room.setMessageVersion(realmClientCondition.getMessageVersion());//Done
            room.setStatusVersion(realmClientCondition.getStatusVersion());//Done
            room.setDeleteVersion(realmClientCondition.getDeleteVersion());//DONE

            for (RealmOfflineDelete offlineDeleted : realmClientCondition.getOfflineDeleted()) { //DONE
                room.addOfflineDeleted(offlineDeleted.getOfflineDelete());
            }

            for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) { // server have problem
                ProtoClientCondition.ClientCondition.Room.OfflineEdited.Builder offlineEdited = ProtoClientCondition.ClientCondition.Room.OfflineEdited.newBuilder();
                offlineEdited.setMessageId(realmOfflineEdited.getMessageId());
                offlineEdited.setMessage(realmOfflineEdited.getMessage());
                room.addOfflineEdited(offlineEdited);
            }

            for (RealmOfflineSeen offlineSeen : realmClientCondition.getOfflineSeen()) { // DONE
                room.addOfflineSeen(offlineSeen.getOfflineSeen());
            }

            room.setClearId(realmClientCondition.getClearId()); //DONE

            RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findFirst();
            if (realmRoomMessage != null) {
                room.setCacheStartId(realmRoomMessage.getMessageId());//Done
                List<RealmRoomMessage> allItems = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

                for (RealmRoomMessage item : allItems) {
                    if (item != null) {
                        room.setCacheEndId(item.getMessageId());//Done
                        break;
                    }
                }
            }

            if (realmClientCondition.getOfflineMute() != null) {
                if (realmClientCondition.getOfflineMute().equals(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString())) {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
                } else {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
                }
            } else {
                room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
            }

            clientCondition.addRooms(room);
            Log.i("CLI", "Condition : " + realmClientCondition);
            clearOffline(realmClientCondition, realm);
        }
        realm.close();

        return clientCondition;
    }

    public static void setMessageAndStatusVersion(Realm realm, long roomId, ProtoGlobal.RoomMessage roomMessage) {
        RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();

        long latestMessageId = computeLastMessageId(realm, roomId);
        /**
         * if received new message set info to RealmClientCondition
         */
        if (realmClientCondition != null) {
            if (roomMessage.getMessageId() > latestMessageId) {
                Log.i("CLI", "getMessageVersion() : " + roomMessage.getMessageVersion());
                Log.i("CLI", "getStatusVersion() : " + roomMessage.getStatusVersion());
                realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
            }
        }
    }

    private static void clearOffline(final RealmClientCondition realmClientCondition, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmClientCondition.setOfflineEdited(new RealmList<RealmOfflineEdited>());
                realmClientCondition.setOfflineDeleted(new RealmList<RealmOfflineDelete>());
                realmClientCondition.setOfflineSeen(new RealmList<RealmOfflineSeen>());
            }
        });
    }

}
