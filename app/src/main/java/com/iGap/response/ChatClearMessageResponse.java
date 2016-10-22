package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatClearMessage;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;

import io.realm.Realm;

public class ChatClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        Log.i("CLI_CLEAR", "ChatClearMessageResponse handler : " + message);
        final ProtoChatClearMessage.ChatClearMessageResponse.Builder chatClearMessage = (ProtoChatClearMessage.ChatClearMessageResponse.Builder) message;

        if (chatClearMessage.getResponse().getId().isEmpty()) { // another account cleared message
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatClearMessage.getRoomId()).findFirst();
                    realmClientCondition.setClearId(chatClearMessage.getClearId());
                }
            });
            realm.close();
            G.clearMessagesUtil.onChatClearMessage(chatClearMessage.getRoomId(), chatClearMessage.getClearId(), chatClearMessage.getResponse());
        }
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatClearMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatClearMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatClearMessageResponse response.minorCode() : " + minorCode);
    }
}


