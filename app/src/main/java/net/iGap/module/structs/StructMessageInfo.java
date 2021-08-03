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

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.module.MyType;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.enums.LocalFileType;
import net.iGap.observers.interfaces.IChatItemAttachment;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;

import org.parceler.Parcels;

import javax.annotation.Nullable;

import io.realm.ObjectChangeSet;
import io.realm.RealmObjectChangeListener;

/**
 * chat message struct info
 * used for chat messages
 */

@Deprecated
public class StructMessageInfo implements Parcelable {

    public boolean isSelected = false;
    public String username = "";

    public StructMessageAttachment senderAvatar;
    public String senderColor = "";
    public String initials;

    public RealmRoomMessage realmRoomMessage;

    private RealmAttachment liverRealmAttachment;
    private RealmObjectChangeListener<RealmAttachment> realmAttachmentRealmChangeListener;

    public String songArtist;
    public long songLength;

    public RealmRoomMessage getRealmRoomMessage() {
        return realmRoomMessage.getForwardMessage() == null ? realmRoomMessage : realmRoomMessage.getForwardMessage();
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setSongLength(long songLength) {
        this.songLength = songLength;
    }

    public MyType.SendType getSendType() {
        if (this.realmRoomMessage.getUserId() == AccountManager.getInstance().getCurrentUser().getId()) {
            return MyType.SendType.send;
        } else {
            return MyType.SendType.recvive;
        }
    }

    public StructMessageInfo(RealmRoomMessage realmRoomMessage) {
        DbManager.getInstance().doRealmTask(realm -> {
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
        });
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

    public void setAttachment(RealmAttachment attachment) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmAttachment unManagedAttachment;
            if (attachment.isManaged()) {
                unManagedAttachment = realm.copyFromRealm(attachment);
            } else {
                unManagedAttachment = attachment;
            }
            if (realmRoomMessage.getForwardMessage() != null) {
                realmRoomMessage.getForwardMessage().setAttachment(unManagedAttachment);
            } else {
                realmRoomMessage.setAttachment(unManagedAttachment);
            }
        });
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
        if (realmRoomMessage.getRealmAdditional() != null) {
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

    private boolean isEqualTwoString(String a, String b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    public <VH extends RecyclerView.ViewHolder> void addAttachmentChangeListener(AbstractMessage abstractMessage, long identifier, IChatItemAttachment<VH> itemVHAbstractMessage, VH holder, ProtoGlobal.RoomMessageType messageType) {
        removeAttachmentChangeListener();

        if (getAttachment() == null) {
            return;
        }

        DbManager.getInstance().doRealmTask(realm -> {
            liverRealmAttachment = realm.where(RealmAttachment.class).equalTo("id", getAttachment().getId()).findFirstAsync();

            realmAttachmentRealmChangeListener = (realmAttachment, changeSet) -> {
                if (realmAttachment.isValid() && realmAttachment.isManaged()) {
                    if (isEqualTwoString(getAttachment().getLocalFilePath(), realmAttachment.getLocalFilePath()) &&
                            isEqualTwoString(getAttachment().getLocalThumbnailPath(), realmAttachment.getLocalThumbnailPath())
                    ) {
                        setAttachment(realm.copyFromRealm(realmAttachment));
                        return;
                    }
                    setAttachment(realm.copyFromRealm(realmAttachment));
//                    abstractMessage.onProgressFinish(holder, messageType);

                    if (realmAttachment.isFileExistsOnLocalAndIsImage()) {
                        itemVHAbstractMessage.onLoadThumbnailFromLocal(holder, realmAttachment.getCacheId(), realmAttachment.getLocalFilePath(), LocalFileType.FILE);
                    } else if (messageType == ProtoGlobal.RoomMessageType.VOICE || messageType == ProtoGlobal.RoomMessageType.AUDIO || messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
                        itemVHAbstractMessage.onLoadThumbnailFromLocal(holder, realmAttachment.getCacheId(), realmAttachment.getLocalFilePath(), LocalFileType.FILE);
                    } else if (messageType.toString().toLowerCase().contains("image") || messageType.toString().toLowerCase().contains("video") || messageType.toString().toLowerCase().contains("gif")) {
                        if (realmAttachment.isThumbnailExistsOnLocal()) {
                            itemVHAbstractMessage.onLoadThumbnailFromLocal(holder, realmAttachment.getCacheId(), realmAttachment.getLocalThumbnailPath(), LocalFileType.THUMBNAIL);
                        }
                    }

                    //mAdapter.notifyItemChanged(mAdapter.getPosition(identifier));
                }

            };
            liverRealmAttachment.addChangeListener(realmAttachmentRealmChangeListener);
        });
    }

    private void removeAttachmentChangeListener() {
        if (liverRealmAttachment != null && realmAttachmentRealmChangeListener != null) {
            liverRealmAttachment.removeChangeListener(realmAttachmentRealmChangeListener);
            liverRealmAttachment = null;
            realmAttachmentRealmChangeListener = null;
        }
    }

    public RealmChannelExtra getChannelExtra() {
        if (realmRoomMessage.getForwardMessage() != null) {
            return getRealmChannelExtraOfMessage(realmRoomMessage.getForwardMessage());
        } else {
            return getRealmChannelExtraOfMessage(realmRoomMessage);
        }
    }

    public RealmChannelExtra getChannelExtraWithoutForward() {
        return getRealmChannelExtraOfMessage(realmRoomMessage);
    }

    private RealmChannelExtra getRealmChannelExtraOfMessage(RealmRoomMessage message) {
        if (message.getChannelExtra() != null) {
            return message.getChannelExtra();
        } else {
            DbManager.getInstance().doRealmTransactionLowPriorityAsync(realm -> {
                RealmRoomMessage newMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", message.getMessageId()).findFirst();
                RealmChannelExtra channelExtra = realm.where(RealmChannelExtra.class).equalTo("messageId", message.getMessageId()).findFirst();
                if (newMessage != null && channelExtra != null) {
                    newMessage.setChannelExtra(channelExtra);
                }
            });

            return DbManager.getInstance().doRealmTask(realm -> {
                RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo("messageId", message.getMessageId()).findFirst();
                if (realmChannelExtra != null) {
                    realmChannelExtra = realm.copyFromRealm(realmChannelExtra);
                }

                return realmChannelExtra;
            });
        }
    }

    public boolean isSenderMe() {
        return realmRoomMessage.isSenderMe();
    }

    public boolean isTimeOrLogMessage() {
        return realmRoomMessage.getUserId() == -1L;
        // or roomMessage.getLogs() != null
    }

    public boolean isGiftSticker() {
        return getRealmRoomMessage().getMessageType() == ProtoGlobal.RoomMessageType.STICKER && getAdditional() != null && getAdditional().getAdditionalType() == AdditionalType.GIFT_STICKER;
    }

    public boolean hasAttachment() {
        return getAttachment() != null;
    }

    public boolean hasAdditional() {
        return getAdditional() != null;
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
