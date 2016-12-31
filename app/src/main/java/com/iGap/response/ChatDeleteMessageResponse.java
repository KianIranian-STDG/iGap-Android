package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChatDeleteMessage;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmShearedMedia;
import com.iGap.realm.RealmShearedMediaFields;
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
        super.handler();
        final ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder chatDeleteMessage = (ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatDeleteMessage.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setDeleteVersion(chatDeleteMessage.getDeleteVersion());
                    for (RealmOfflineDelete realmOfflineDeleted : realmClientCondition.getOfflineDeleted()) {
                        if (realmOfflineDeleted.getOfflineDelete() == chatDeleteMessage.getMessageId()) {
                            realmOfflineDeleted.deleteFromRealm();
                            break;
                        }
                    }
                }
                G.onChatDeleteMessageResponse.onChatDeleteMessage(chatDeleteMessage.getDeleteVersion(), chatDeleteMessage.getMessageId(), chatDeleteMessage.getRoomId(),
                    chatDeleteMessage.getResponse());

                // delte  file from realm sheared media
                RealmShearedMedia rs = realm.where(RealmShearedMedia.class).equalTo(RealmShearedMediaFields.MESSAGE_ID, chatDeleteMessage.getMessageId()).
                    equalTo(RealmShearedMediaFields.ROOM_ID, chatDeleteMessage.getRoomId()).findFirst();

                if (rs != null) rs.deleteFromRealm();

            }
        });

        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


