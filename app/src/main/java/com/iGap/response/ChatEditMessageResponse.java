package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatEditMessage;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;

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
        String nickname = realm.where(RealmUserInfo.class).findFirst().getNickName();
        Log.i("CLI_EDIT", "ChatEditMessageResponse for " + nickname + " : " + message);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatEditMessageResponse.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setMessageVersion(chatEditMessageResponse.getMessageVersion());
                }

                if (!chatEditMessageResponse.getResponse().getId().isEmpty()) {
                    Log.i("CLI_EDIT", "Edit message version : " + chatEditMessageResponse.getMessageVersion());
                    Log.i("CLI_EDIT", "Edit message ID : " + chatEditMessageResponse.getMessageId());

                    for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) {
                        if (realmOfflineEdited.getMessageId() == chatEditMessageResponse.getMessageId()) {
                            realmOfflineEdited.deleteFromRealm();
                            Log.i("SOC_CONDITION", "Edit deleteFromRealm  : " + realmOfflineEdited);
                            break;
                        }
                    }

                } else {
                    Log.i("SOC_CONDITION", "I'm Recipient 1");
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, chatEditMessageResponse.getMessageId()).findFirst();
                    if (roomMessage != null) {
                        // update message text in database
                        roomMessage.setMessage(chatEditMessageResponse.getMessage());
                        roomMessage.setMessageVersion(chatEditMessageResponse.getMessageVersion());
                        roomMessage.setEdited(true);
                        Log.i("CLI_EDIT", "I'm Recipient 2 roomMessage : " + roomMessage);
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


