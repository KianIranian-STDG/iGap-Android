/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module.structs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.iGap.adapter.MessagesAdapter;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.interfaces.IChatItemAttachment;
import net.iGap.module.MyType;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelExtraFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import org.parceler.Parcels;

import io.realm.Realm;
import io.realm.RealmChangeListener;

import static net.iGap.G.userId;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {

    public boolean isSelected = false;
    public String username = "";

    public StructMessageAttachment senderAvatar;
    public String senderColor = "";
    public String initials;

    public RealmRoomMessage realmRoomMessage;

    private RealmAttachment liverRealmAttachment;
    private RealmChangeListener<RealmAttachment> realmAttachmentRealmChangeListener;

    public String songArtist;
    public long songLength;


    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setSongLength(long songLength) {
        this.songLength = songLength;
    }

    public MyType.SendType getSendType() {
        if (this.realmRoomMessage.getUserId() == userId) {
            return MyType.SendType.send;
        } else {
           return MyType.SendType.recvive;
        }
    }

    public StructMessageInfo(RealmRoomMessage realmRoomMessage) {
        try (Realm realm = Realm.getDefaultInstance()) {
            if (realmRoomMessage.isManaged()) {
                this.realmRoomMessage = realm.copyFromRealm(realmRoomMessage);
            } else {
                this.realmRoomMessage = realmRoomMessage;
            }

            if (!realmRoomMessage.isSenderMe()) {
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, realmRoomMessage.getUserId());
                if (realmRegisteredInfo != null) {
                    senderAvatar = StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar(realm));
                    senderColor = realmRegisteredInfo.getColor();
                    initials = realmRegisteredInfo.getInitials();
                }
            }
        }
    }

    public void setContactValues(String firstName, String lastName, String number) {
        this.realmRoomMessage.setMessage(firstName + " " + number);

    }

    public RealmAttachment getAttachment() {
        if (realmRoomMessage.getForwardMessage() != null) {
            return realmRoomMessage.getForwardMessage().getAttachment();
        }
        return realmRoomMessage.getAttachment();
    }

    public int getUploadProgress() {
        RealmAttachment attachment = getAttachment();
        return attachment != null && attachment.getToken() != null && !attachment.getToken().isEmpty() ? 100 : 0;
    }

    public boolean hasLinkInMessage() {
        return realmRoomMessage.getForwardMessage() != null ? realmRoomMessage.getForwardMessage().getHasMessageLink() : realmRoomMessage.getHasMessageLink();
    }

    public String getLinkInfo() {
        return realmRoomMessage.getForwardMessage() != null ? realmRoomMessage.getForwardMessage().getLinkInfo() : realmRoomMessage.getLinkInfo();
    }

    public boolean hasEmojiInText() {
        return realmRoomMessage.getForwardMessage() != null ? realmRoomMessage.getForwardMessage().isHasEmojiInText() : realmRoomMessage.isHasEmojiInText();
    }

    public RealmAdditional getAdditional() {
        if (realmRoomMessage.getRealmAdditional() != null){
            return realmRoomMessage.getRealmAdditional();
        } else if (realmRoomMessage.getForwardMessage() != null && realmRoomMessage.getForwardMessage().getRealmAdditional() != null) {
            return realmRoomMessage.getForwardMessage().getRealmAdditional();
        }
        return null;
    }

    public static long getReplyMessageId(StructMessageInfo structMessageInfo) {
        if (structMessageInfo != null && structMessageInfo.realmRoomMessage != null && structMessageInfo.realmRoomMessage.getReplyTo() != null) {
            if (structMessageInfo.realmRoomMessage.getReplyTo().getMessageId() < 0) {
                return (structMessageInfo.realmRoomMessage.getReplyTo().getMessageId() * (-1));
            } else {
                return structMessageInfo.realmRoomMessage.getReplyTo().getMessageId();
            }
        }
        return 0;
    }

    public  <VH extends RecyclerView.ViewHolder> void addAttachmentChangeListener(Realm realm, MessagesAdapter<AbstractMessage> mAdapter, long identifier, IChatItemAttachment<VH> itemVHAbstractMessage, VH holder, ProtoGlobal.RoomMessageType messageType) {
        removeAttachmentChangeListener();

        if (getAttachment() == null) {
            return;
        }

        liverRealmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, getAttachment().getId()).findFirst();
        if (liverRealmAttachment != null) {
            realmAttachmentRealmChangeListener = realmAttachment -> {
                realmRoomMessage.setAttachment(realm.copyFromRealm(realmAttachment));

                if (realmAttachment.isFileExistsOnLocalAndIsThumbnail()) {
                    itemVHAbstractMessage.onLoadThumbnailFromLocal(holder, realmAttachment.getCacheId(), realmAttachment.getLocalFilePath(), LocalFileType.FILE);
                } else if (messageType == ProtoGlobal.RoomMessageType.VOICE || messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
                    itemVHAbstractMessage.onLoadThumbnailFromLocal(holder, realmAttachment.getCacheId(), realmAttachment.getLocalFilePath(), LocalFileType.FILE);
                } else if (messageType.toString().toLowerCase().contains("image") || messageType.toString().toLowerCase().contains("video") || messageType.toString().toLowerCase().contains("gif")) {
                    if (realmAttachment.isThumbnailExistsOnLocal()) {
                        itemVHAbstractMessage.onLoadThumbnailFromLocal(holder, realmAttachment.getCacheId(), realmAttachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                    }
                }

                //mAdapter.notifyItemChanged(mAdapter.getPosition(identifier));
            };
            liverRealmAttachment.addChangeListener(realmAttachmentRealmChangeListener);
        }
    }

    private void removeAttachmentChangeListener() {
        if (liverRealmAttachment != null && realmAttachmentRealmChangeListener!= null) {
            liverRealmAttachment.removeChangeListener(realmAttachmentRealmChangeListener);
            liverRealmAttachment = null;
            realmAttachmentRealmChangeListener = null;
        }
    }

    public RealmChannelExtra getChannelExtra() {
        if (realmRoomMessage.getChannelExtra() != null) {
            return realmRoomMessage.getChannelExtra();
        } else {
            new Thread(() -> {
                try (Realm realm = Realm.getDefaultInstance()) {
                    realm.executeTransaction(realm1 -> {
                        RealmRoomMessage newMessage = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getMessageId()).findFirst();
                        RealmChannelExtra channelExtra = realm1.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, realmRoomMessage.getMessageId()).findFirst();
                        if (newMessage != null && channelExtra != null) {
                            newMessage.setChannelExtra(channelExtra);
                        }
                    });
                }
            }).start();

            try (Realm realm = Realm.getDefaultInstance()) {
                RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, realmRoomMessage.getMessageId()).findFirst();
                if (realmChannelExtra != null) {
                    realmChannelExtra = realm.copyFromRealm(realmChannelExtra);
                }

                return realmChannelExtra;
            }
        }
    }

    public boolean isSenderMe() {
        return realmRoomMessage.isSenderMe();
    }

    public boolean isTimeOrLogMessage() {
        return realmRoomMessage.getUserId() == -1L;
        // or roomMessage.getLogs() != null
    }

    public boolean hasAttachment() {
        return getAttachment() != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected StructMessageInfo(Parcel in) {
        this.senderColor = in.readString();
        this.initials = in.readString();
        this.songArtist = in.readString();
        this.songLength = in.readLong();
        this.realmRoomMessage = Parcels.unwrap(in.readParcelable(RealmRoomMessage.class.getClassLoader()));
        this.senderAvatar = in.readParcelable(StructMessageAttachment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.senderColor);
        dest.writeString(this.initials);
        dest.writeString(this.songArtist);
        dest.writeLong(this.songLength);
        dest.writeParcelable(Parcels.wrap(this.realmRoomMessage), flags);
        dest.writeParcelable(this.senderAvatar, flags);
    }

    public static final Parcelable.Creator<StructMessageInfo> CREATOR = new Parcelable.Creator<StructMessageInfo>() {
        @Override
        public StructMessageInfo createFromParcel(Parcel source) {
            return new StructMessageInfo(source);
        }

        @Override
        public StructMessageInfo[] newArray(int size) {
            return new StructMessageInfo[size];
        }
    };
}
