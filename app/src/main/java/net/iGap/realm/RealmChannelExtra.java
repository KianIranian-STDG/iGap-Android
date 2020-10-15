/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructChannelExtra;
import net.iGap.proto.ProtoChannelGetMessagesStats;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.net_iGap_realm_RealmChannelExtraRealmProxy;

@Parcel(implementations = {net_iGap_realm_RealmChannelExtraRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmChannelExtra.class})
public class RealmChannelExtra extends RealmObject {

    private long messageId;
    private String signature;
    private String viewsLabel;
    private String thumbsUp;
    private String thumbsDown;

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
        RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo("messageId", messageId).findFirst();
        if (realmChannelExtra == null) {
            realmChannelExtra = realm.createObject(RealmChannelExtra.class);
        }
        realmChannelExtra.setMessageId(messageId);
        realmChannelExtra.setSignature(channelExtra.getSignature());
        realmChannelExtra.setThumbsUp(channelExtra.getThumbsUpLabel());
        realmChannelExtra.setThumbsDown(channelExtra.getThumbsDownLabel());
        realmChannelExtra.setViewsLabel(channelExtra.getViewsLabel());
        return realmChannelExtra;
    }

    public static void putDefault(final Realm realm, final long roomId, final long messageId) {
        RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
        realmChannelExtra.setMessageId(messageId);
        realmChannelExtra.setThumbsUp("0");
        realmChannelExtra.setThumbsDown("0");
        if (RealmRoom.showSignature(roomId)) {
            realmChannelExtra.setSignature(AccountManager.getInstance().getCurrentUser().getName());
        } else {
            realmChannelExtra.setSignature("");
        }
        realmChannelExtra.setViewsLabel("1");
    }

    public static void setVote(final long messageId, final ProtoGlobal.RoomMessageReaction messageReaction, final String counterLabel) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo("messageId", messageId).findFirst();
            if (realmChannelExtra != null) {
                if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                    realmChannelExtra.setThumbsUp(counterLabel);
                } else {
                    realmChannelExtra.setThumbsDown(counterLabel);
                }
            }
        });
    }

    public static void updateMessageStats(final List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> statsArrayList) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            for (ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats stats : statsArrayList) {
                RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo("messageId", stats.getMessageId()).findFirst();
                if (realmChannelExtra != null) {
                    realmChannelExtra.setThumbsUp(stats.getThumbsUpLabel());
                    realmChannelExtra.setThumbsDown(stats.getThumbsDownLabel());
                    realmChannelExtra.setViewsLabel(stats.getViewsLabel());
                }
            }
        });
    }

    public static boolean hasChannelExtra(long messageId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo("messageId", messageId).findFirst();
            return realmChannelExtra != null;
        });
    }

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
}
