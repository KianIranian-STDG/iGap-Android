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
import io.realm.RealmList;
import io.realm.Sort;

public class RequestClientCondition {
    //TODO [Saeed Mozaffari] [2016-09-29 12:45 PM] - after get response for client condition delete all data (hint : just clear data in room don't delete room)
    public void clientCondition() {
        Realm realm = Realm.getDefaultInstance();

        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        for (RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
            ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
            if (realmClientCondition.getRoomId() == 94) {
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

                room.setClearId(realmClientCondition.getClearId());

                RealmChatHistory realmChatHistory = realm.where(RealmChatHistory.class).equalTo("roomId", realmClientCondition.getRoomId()).findFirst();
                Log.i("CLI1", "realmChatHistory : " + realmChatHistory);
                if (realmChatHistory != null && realmChatHistory.getRoomMessage() != null) {
                    Log.i("CLI1", "start ID : " + realmChatHistory.getRoomMessage().getMessageId());
                    room.setCacheStartId(realmChatHistory.getRoomMessage().getMessageId());

                    List<RealmChatHistory> allItems = realm.where(RealmChatHistory.class).equalTo("roomId", realmClientCondition.getRoomId()).findAll().sort("id", Sort.DESCENDING);

                    for (RealmChatHistory item : allItems) {
                        Log.i("CLI1", "End 1");
                        if (item.getRoomMessage() != null) {
                            Log.i("CLI1", "End ID : " + item.getRoomMessage().getMessageId());
                            room.setCacheEndId(item.getRoomMessage().getMessageId());
                            break;
                        }
                    }
                }

                if (realmClientCondition.getOfflineMute() != null) {
                    if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString()) {
                        room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
                    } else {
                        room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
                    }
                } else {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
                }

                clientCondition.addRooms(room);

                clearOffline(realmClientCondition, realm);
            }
        }

        Log.i("CLI1", "clientCondition.build() : " + clientCondition.build());

        realm.close();

//        final RealmResults<RealmOfflineEdited> result = realm.where(RealmOfflineEdited.class).findAll();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                result.deleteAllFromRealm();
//            }
//        });

        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void clearOffline(final RealmClientCondition realmClientCondition, Realm realm) {
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