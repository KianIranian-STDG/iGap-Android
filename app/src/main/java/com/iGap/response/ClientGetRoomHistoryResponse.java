package com.iGap.response;

import android.os.Handler;
import com.iGap.G;
import com.iGap.helper.HelperUserInfo;
import com.iGap.proto.ProtoClientGetRoomHistory;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class ClientGetRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomHistoryResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final int[] i = {0};

        new Handler(G.currentActivity.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                final Realm realm = Realm.getDefaultInstance();

                final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();

                        for (ProtoGlobal.RoomMessage roomMessage : builder.getMessageList()) {

                            if (roomMessage.getAuthor().hasUser()) {
                                HelperUserInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                            }

                            // set info for clientCondition
                            /*RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, Long.parseLong(identity)).findFirst();

                            long latestMessageId = 0;
                            RealmRoomMessage realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, Long.parseLong(identity)).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING).last();
                            if (realmRoomMessages != null) {
                                latestMessageId = realmRoomMessages.getMessageId();
                            }

                            if (realmClientCondition != null) {
                                if (roomMessage.getMessageId() > latestMessageId) {
                                    realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
                                    realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
                                }
                            }*/

                            RealmRoomMessage rm = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();
                            if (rm == null) {
                                i[0]++;
                            }

                            RealmRoomMessage.putOrUpdate(roomMessage, Long.parseLong(identity));

                            if (roomMessage.getAuthor().getUser().getUserId() != userId) { // show notification if this message isn't for another account
                                if (!G.isAppInFg) {
                                    G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.CHAT, Long.parseLong(identity));
                                }
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        G.onClientGetRoomHistoryResponse.onGetRoomHistory(Long.parseLong(identity), builder.getMessageList(), i[0]);

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onClientGetRoomHistoryResponse != null) {
            G.onClientGetRoomHistoryResponse.onGetRoomHistoryError(majorCode, minorCode);
        }
    }
}


