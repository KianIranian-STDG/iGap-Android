package com.iGap.request;

import android.util.Log;

import com.iGap.proto.ProtoClientCondition;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmOfflineSeen;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class RequestClientCondition {

    public void clientCondition() {
        Realm realm = Realm.getDefaultInstance();

        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        for (RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
            ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
            room.setRoomId(realmClientCondition.getRoomId());
            room.setMessageVersion(realmClientCondition.getMessageVersion());
            room.setStatusVersion(realmClientCondition.getStatusVersion());
            room.setDeleteVersion(realmClientCondition.getDeleteVersion()); // latest receive response from delete message

            for (RealmOfflineDelete offlineDeleted : realmClientCondition.getOfflineDeleted()) {
                room.addOfflineDeleted(offlineDeleted.getOfflineDelete());
                Log.i("SOC_CONDITION", "*** offlineDeleted : " + offlineDeleted.getOfflineDelete());
            }

            for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) {
                ProtoClientCondition.ClientCondition.Room.OfflineEdited.Builder offlineEdited = ProtoClientCondition.ClientCondition.Room.OfflineEdited.newBuilder();
                offlineEdited.setMessageId(realmOfflineEdited.getMessageId());
                offlineEdited.setMessage(realmOfflineEdited.getMessage());
                room.addOfflineEdited(offlineEdited);
                Log.i("SOC_CONDITION", "*** offlineEdited roomId : " + realmClientCondition.getRoomId());
                Log.i("SOC_CONDITION", "*** offlineEdited : " + realmOfflineEdited.getMessage());
            }

            for (RealmOfflineSeen offlineSeen : realmClientCondition.getOfflineSeen()) {
                room.addOfflineSeen(offlineSeen.getOfflineSeen());
            }

            //room.setClearId(realmClientCondition.getClearId()); //TODO [Saeed Mozaffari] [2016-09-17 4:25 PM] - after make clear history get clear Id

            RealmChatHistory realmChatHistory = realm.where(RealmChatHistory.class).equalTo("roomId", realmClientCondition.getRoomId()).findFirst();
            if (realmChatHistory != null && realmChatHistory.getRoomMessage() != null) {
                room.setCacheStartId(realmChatHistory.getRoomMessage().getMessageId());

                List<RealmChatHistory> allItems = realm.where(RealmChatHistory.class).equalTo("roomId", realmClientCondition.getRoomId()).findAll().sort("id", Sort.DESCENDING);

                for (RealmChatHistory item : allItems) {
                    if (item.getRoomMessage() != null) {
                        room.setCacheEndId(item.getRoomMessage().getMessageId());
                        break;
                    }
                }
            }

            if (realmClientCondition.getOfflineMute() != null) {
                if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString()) {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
                } else if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED.toString()) {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
                }
            } else {
                room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
            }

            clientCondition.addRooms(room);
        }

        Log.i("SOC_CONDITION", "clientCondition.build() : " + clientCondition.build());

        realm.close();

//        final RealmResults<RealmOfflineEdited> result = realm.where(RealmOfflineEdited.class).findAll();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                result.deleteAllFromRealm();
//            }
//        });

//        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
//        try {
//            RequestQueue.sendRequest(requestWrapper);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

}