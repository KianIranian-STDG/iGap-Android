package com.iGap.request;

import com.iGap.proto.ProtoClientCondition;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmOfflineSeen;

import io.realm.Realm;

public class RequestClientCondition {

    public void clientCondition() {
        Realm realm = Realm.getDefaultInstance();

        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        for (RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
            ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
            room.setRoomId(realmClientCondition.getRoomId());
            room.setMessageVersion(realmClientCondition.getMessageVersion());
            room.setStatusVersion(realmClientCondition.getStatusVersion());
            room.setDeleteVersion(realmClientCondition.getDeleteVersion());

            for (RealmOfflineDelete offlineDeleted : realmClientCondition.getOfflineDeleted()) {
                room.addOfflineDeleted(offlineDeleted.getOfflineDelete());
            }

            for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) {
                ProtoClientCondition.ClientCondition.Room.OfflineEdited.Builder offlineEdited = ProtoClientCondition.ClientCondition.Room.OfflineEdited.newBuilder();
                offlineEdited.setMessageId(realmOfflineEdited.getMessageId());
                offlineEdited.setMessage(realmOfflineEdited.getMessage());
                room.addOfflineEdited(offlineEdited);
            }

            for (RealmOfflineSeen offlineSeen : realmClientCondition.getOfflineSeen()) {
                room.addOfflineSeen(offlineSeen.getOfflineSeen());
            }

//            room.setClearId(realmClientCondition.getClearId()); //TODO [Saeed Mozaffari] [2016-09-17 4:25 PM] - after make clear history get clear Id

            RealmChatHistory firstRealmChatHistory = realm.where(RealmChatHistory.class).equalTo("roomId", realmClientCondition.getRoomId()).findFirst();
            room.setCacheStartId(firstRealmChatHistory.getRoomMessage().getMessageId());

            //TODO [Saeed Mozaffari] [2016-09-17 4:57 PM] - need primary key index for sorting and get latest item
//            List<RealmChatHistory> allItems = realm.where(RealmChatHistory.class).findAll().sort("id", Sort.DESCENDING);
//            long latestMessageId = allItems.get(0).getRoomMessage().getMessageId();
//            room.setCacheEndId(realmClientCondition.getCacheEndId());

            if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString()) {
                room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
            } else if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED.toString()) {
                room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
            } else if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED.toString()) {
                room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
            }

            clientCondition.addRooms(room);
        }

        realm.close();

        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}