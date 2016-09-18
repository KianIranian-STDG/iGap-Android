package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatEditMessage;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmRoomMessage;

import io.realm.Realm;

public class ChatEditMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatEditMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        final ProtoChatEditMessage.ChatEditMessageResponse.Builder chatEditMessageResponse = (ProtoChatEditMessage.ChatEditMessageResponse.Builder) message;


        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (!chatEditMessageResponse.getResponse().getId().isEmpty()) {
                    // set info for clientCondition
                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", chatEditMessageResponse.getRoomId()).findFirst();

                    Log.i("SOC_CONDITION", "RoomId : " + chatEditMessageResponse.getRoomId());
                    for (RealmOfflineEdited edited : realmClientCondition.getOfflineEdited()) {
                        Log.i("SOC_CONDITION", "Edit Response 1 realmClientCondition : " + edited.getMessage());
                    }

                    if (realmClientCondition != null) {//TODO [Saeed Mozaffari] [2016-09-17 3:18 PM] - FORCE - client condition checking
                        realmClientCondition.setMessageVersion(chatEditMessageResponse.getMessageVersion());
                        Log.i("SOC_CONDITION", "Edit Response 2");
                        for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) { // contains
                            Log.i("SOC_CONDITION", "Edit Response 3");
                            if (realmOfflineEdited.getMessageId() == chatEditMessageResponse.getMessageId()) {
                                Log.i("SOC_CONDITION", "Edit Response 4 realmOfflineEdited : " + realmOfflineEdited);
                                realmOfflineEdited.deleteFromRealm();
                                Log.i("SOC_CONDITION", "Edit Response 5  : " + realmOfflineEdited);
                            }
                        }
                        for (RealmOfflineEdited edited : realmClientCondition.getOfflineEdited()) {
                            Log.i("SOC_CONDITION", "Edit Response last realmClientCondition : " + edited.getMessage());
                        }
                    }
                } else {
                    Log.i("SOC_CONDITION", "I'm Recipient 1");
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", chatEditMessageResponse.getMessageId()).findFirst();
                    Log.i("SOC_CONDITION", "I'm Recipient 2 roomMessage : " + roomMessage);
                    Log.i("SOC_CONDITION", "I'm Recipient 3 chatEditMessageResponse.getMessage() : " + chatEditMessageResponse.getMessage());
                    if (roomMessage != null) {
                        // update message text in database
                        roomMessage.setMessage(chatEditMessageResponse.getMessage());
                        roomMessage.setEdited(true);

                        G.onChatEditMessageResponse.onChatEditMessage(chatEditMessageResponse.getRoomId(), chatEditMessageResponse.getMessageId(), chatEditMessageResponse.getMessageVersion(), chatEditMessageResponse.getMessage(), chatEditMessageResponse.getResponse());
                    }
                }
            }
        });
        realm.close();
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatEditMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatEditMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatEditMessageResponse response.minorCode() : " + minorCode);
    }
}


