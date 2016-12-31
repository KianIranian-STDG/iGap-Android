package com.iGap.response;

import com.iGap.helper.HelperDeleteMessage;
import com.iGap.proto.ProtoChannelDeleteMessage;
import com.iGap.realm.RealmShearedMedia;
import com.iGap.realm.RealmShearedMediaFields;
import io.realm.Realm;

public class ChannelDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder builder = (ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder) message;
        HelperDeleteMessage.deleteMessage(builder.getRoomId(), builder.getMessageId(), builder.getDeleteVersion(), builder.getResponse());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                // delte  file from realm sheared media
                RealmShearedMedia rs = realm.where(RealmShearedMedia.class).equalTo(RealmShearedMediaFields.MESSAGE_ID, builder.getMessageId()).
                    equalTo(RealmShearedMediaFields.ROOM_ID, builder.getRoomId()).findFirst();

                if (rs != null) rs.deleteFromRealm();
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
    }
}


