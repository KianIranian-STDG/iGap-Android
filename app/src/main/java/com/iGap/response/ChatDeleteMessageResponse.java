package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChatDeleteMessage;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmUserInfo;

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
        String nickName = realm.where(RealmUserInfo.class).findFirst().getNickName();
        Log.i("CLI_DELETE", "ChatDeleteMessageResponse handler for " + nickName + " : " + message);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", chatDeleteMessage.getRoomId()).findFirst();
                Log.i("CLI_DELETE", "Delete 1 : " + chatDeleteMessage.getMessageId());
                if (realmClientCondition != null) {
                    realmClientCondition.setDeleteVersion(chatDeleteMessage.getDeleteVersion());
                    Log.i("CLI_DELETE", "Delete 2");
                    for (RealmOfflineDelete realmOfflineDeleted : realmClientCondition.getOfflineDeleted()) {
                        Log.i("CLI_DELETE", "Delete 3");
                        if (realmOfflineDeleted.getOfflineDelete() == chatDeleteMessage.getMessageId()) {
                            realmOfflineDeleted.deleteFromRealm();
                            Log.i("CLI_DELETE", "realmClientCondition : " + realmClientCondition);
                            break;
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


