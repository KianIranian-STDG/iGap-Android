/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmChannelExtraRealmProxy;
import io.realm.RealmObject;
import net.iGap.G;
import net.iGap.module.structs.StructChannelExtra;
import net.iGap.proto.ProtoGlobal;
import org.parceler.Parcel;

@Parcel(implementations = {RealmChannelExtraRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmChannelExtra.class}) public class RealmChannelExtra extends RealmObject {

    private long messageId;
    private String signature;
    private String viewsLabel;
    private String thumbsUp;
    private String thumbsDown;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getViewsLabel() {
        return viewsLabel;
    }

    public void setViewsLabel(String viewsLabel) {
        this.viewsLabel = viewsLabel;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(String thumbsDown) {
        this.thumbsDown = thumbsDown;
    }

    public static RealmChannelExtra convert(Realm realm, StructChannelExtra structChannelExtra) {
        RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
        realmChannelExtra.setMessageId(structChannelExtra.messageId);
        realmChannelExtra.setSignature(structChannelExtra.signature);
        realmChannelExtra.setThumbsUp(structChannelExtra.thumbsUp);
        realmChannelExtra.setThumbsDown(structChannelExtra.thumbsDown);
        realmChannelExtra.setViewsLabel(structChannelExtra.viewsLabel);
        return realmChannelExtra;
    }

    public static RealmChannelExtra putOrUpdate(Realm realm, long messageId, ProtoGlobal.RoomMessage.ChannelExtra channelExtra) {
        RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
        realmChannelExtra.setMessageId(messageId);
        realmChannelExtra.setSignature(channelExtra.getSignature());
        realmChannelExtra.setThumbsUp(channelExtra.getThumbsUpLabel());
        realmChannelExtra.setThumbsDown(channelExtra.getThumbsDownLabel());
        realmChannelExtra.setViewsLabel(channelExtra.getViewsLabel());
        return realmChannelExtra;
    }

    public static void putDefault(final long roomId, final long messageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
                realmChannelExtra.setMessageId(messageId);
                realmChannelExtra.setThumbsUp("0");
                realmChannelExtra.setThumbsDown("0");
                if (RealmChannelRoom.isSignature(roomId)) {
                    realmChannelExtra.setSignature(G.displayName);
                } else {
                    realmChannelExtra.setSignature("");
                }
                realmChannelExtra.setViewsLabel("1");
            }
        });
        realm.close();
    }

    public static void setVote(final long messageId, final ProtoGlobal.RoomMessageReaction messageReaction, final String counterLabel) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, messageId).findFirst();
                if (realmChannelExtra != null) {
                    if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                        realmChannelExtra.setThumbsUp(counterLabel);
                    } else {
                        realmChannelExtra.setThumbsDown(counterLabel);
                    }
                }
            }
        });
        realm.close();
    }
}
