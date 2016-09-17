package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatDeleteMessage;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineDelete;

import io.realm.Realm;

public class ChatDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        final ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder chatDeleteMessage = (ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", chatDeleteMessage.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    Log.i("SOC_CONDITION", "ChatDeleteMessageResponse 1 getOfflineDeleted() : " + realmClientCondition.getOfflineDeleted());
                    for (RealmOfflineDelete realmOfflineDeleted : realmClientCondition.getOfflineDeleted()) {
                        Log.i("SOC_CONDITION", "ChatDeleteMessageResponse realmClientCondition 2");
                        if (realmOfflineDeleted.getOfflineDelete() == chatDeleteMessage.getMessageId()) {
                            Log.i("SOC_CONDITION", "ChatDeleteMessageResponse realmClientCondition 3");
                            realmOfflineDeleted.deleteFromRealm();
                            realmClientCondition.setDeleteVersion(chatDeleteMessage.getMessageId());
                        }
                    }
                }

                G.onChatDeleteMessageResponse.onChatDeleteMessage(chatDeleteMessage.getDeleteVersion(), chatDeleteMessage.getMessageId(), chatDeleteMessage.getRoomId(), chatDeleteMessage.getResponse());
            }
        });
        realm.close();
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ChatDeleteMessageResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ChatDeleteMessageResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ChatDeleteMessageResponse response.minorCode() : " + minorCode);
    }
}


