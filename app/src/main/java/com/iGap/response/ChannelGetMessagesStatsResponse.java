package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelGetMessagesStats;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmChannelExtra;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

public class ChannelGetMessagesStatsResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelGetMessagesStatsResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Builder builder = (ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (final ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats stats : builder.getStatsList()) {
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, stats.getMessageId()).findFirst();
                    if (realmRoomMessage != null) {

                        RealmChannelExtra realmChannelExtra = realmRoomMessage.getChannelExtra();
                        if (realmRoomMessage.getChannelExtra() == null) {
                            realmChannelExtra = realm.createObject(RealmChannelExtra.class);
                        }
                        realmChannelExtra.setThumbsUp(stats.getThumbsUpLabel());
                        realmChannelExtra.setThumbsDown(stats.getThumbsDownLabel());
                        realmChannelExtra.setViewsLabel(stats.getViewsLabel());
                        /**
                         * if identity is exist message forwarded
                         */
                        if (identity != null) {
                            if (realmRoomMessage.getChannelExtra() != null) {
                                realmRoomMessage.getForwardMessage().setChannelExtra(realmChannelExtra);
                            }
                        } else {
                            realmRoomMessage.setChannelExtra(realmChannelExtra);
                        }
                    }
                }
            }
        });
        realm.close();

        if (G.onChannelGetMessagesStats != null) {
            G.onChannelGetMessagesStats.onChannelGetMessagesStats(builder.getStatsList());
        }
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
        if (G.onChannelGetMessagesStats != null) {
            G.onChannelGetMessagesStats.onError(majorCode, minorCode);
        }
    }
}


