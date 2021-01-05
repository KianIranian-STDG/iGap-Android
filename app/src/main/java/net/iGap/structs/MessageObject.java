package net.iGap.structs;

import android.text.format.DateUtils;

import net.iGap.helper.HelperUrl;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGlobal.RoomMessage;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

public class MessageObject {
    public static final int STATUS_FAILED = 0;
    public static final int STATUS_SENDING = 1;
    public static final int STATUS_SENT = 2;
    public static final int STATUS_DELIVERED = 3;
    public static final int STATUS_SEEN = 4;
    public static final int STATUS_LISTENED = 5;

    public MessageObject forwardedMessage;
    public MessageObject replayToMessage;
    public LocationObject location;
    public RoomContactObject contact;
    public LogObject log;
    public WalletObject wallet;
    public AttachmentObject attachment;
    public AdditionalObject additional;
    public String additionalData;
    public int additionalType;
    public long id;
    public String message;
    public boolean needToShow = true;
    public boolean hasLink;
    public String linkInfo;
    public int status;
    public long userId;
    public long roomId;
    public String authorHash;
    public boolean deleted;
    public boolean edited;
    public int messageType;
    public long messageVersion;
    public long statusVersion;
    public long previousMessageId;
    public RoomMessage.ChannelExtra channelExtra;
    public long updateTime;
    public long createTime;
    public long futureMessageId;

    public boolean isSelected;

    private MessageObject() {

    }

    public static MessageObject create(RoomMessage roomMessage) {
        return create(roomMessage, false, false);
    }

    public static MessageObject create(RoomMessage roomMessage, boolean fromShareMedia, boolean isGap) {
        MessageObject messageObject = new MessageObject();
        boolean isForwardOrReplay = roomMessage.getReplyTo() != null || roomMessage.getForwardFrom() != null;

        if (fromShareMedia) {
            messageObject.previousMessageId = roomMessage.getMessageId();
            messageObject.futureMessageId = roomMessage.getMessageId();
        }

        if (roomMessage.getAuthor().hasUser()) {
            messageObject.userId = roomMessage.getAuthor().getUser().getUserId();
        } else {
            messageObject.roomId = roomMessage.getAuthor().getRoom().getRoomId();
            if (isForwardOrReplay) {// FIXME: 12/15/20
                RealmRoom.needGetRoom(roomMessage.getAuthor().getRoom().getRoomId());
            }
        }

        if (!isForwardOrReplay) {
            messageObject.deleted = roomMessage.getDeleted();
        }

        messageObject.id = isForwardOrReplay ? roomMessage.getMessageId() * (-1) : roomMessage.getMessageId();
        messageObject.forwardedMessage = create(roomMessage.getForwardFrom());
        messageObject.replayToMessage = create(roomMessage.getReplyTo());
        messageObject.message = roomMessage.getMessage();
        messageObject.status = roomMessage.getStatusValue();
        messageObject.authorHash = roomMessage.getAuthor().getHash();
        messageObject.edited = roomMessage.getEdited();
        messageObject.attachment = AttachmentObject.create(roomMessage.getAttachment());
        messageObject.location = LocationObject.create(roomMessage.getLocation());
        messageObject.log = LogObject.create(roomMessage.getLog());
        messageObject.contact = RoomContactObject.create(roomMessage.getContact());
        messageObject.wallet = WalletObject.create(roomMessage.getWallet());
        messageObject.additionalData = roomMessage.getAdditionalData();
        messageObject.additionalType = roomMessage.getAdditionalType();
        messageObject.channelExtra = roomMessage.getChannelExtra();
        messageObject.messageType = roomMessage.getMessageTypeValue();
        messageObject.messageVersion = roomMessage.getMessageVersion();
        messageObject.statusVersion = roomMessage.getStatusVersion();
        messageObject.updateTime = roomMessage.getUpdateTime() == 0 ? roomMessage.getCreateTime() * DateUtils.SECOND_IN_MILLIS : roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS;
        messageObject.createTime = roomMessage.getCreateTime() * DateUtils.SECOND_IN_MILLIS;

        if (isGap) {
            messageObject.previousMessageId = roomMessage.getPreviousMessageId();
        }

        return messageObject;
    }

    public static MessageObject create(RealmRoomMessage roomMessage) {
        return create(roomMessage, false, false);
    }

    public static MessageObject create(RealmRoomMessage roomMessage, boolean fromShareMedia, boolean isGap) {
        if (roomMessage == null) {
            return null;
        }

        MessageObject messageObject = new MessageObject();
        boolean isForwardOrReplay = roomMessage.replyTo != null || roomMessage.forwardMessage != null;

        if (fromShareMedia) {
            messageObject.previousMessageId = roomMessage.getMessageId();
            messageObject.futureMessageId = roomMessage.getMessageId();
        }

        if (roomMessage.userId != 0) {
            messageObject.userId = roomMessage.userId;
        } else {
            messageObject.roomId = roomMessage.roomId;
            if (isForwardOrReplay) {// FIXME: 12/15/20
                RealmRoom.needGetRoom(roomMessage.roomId);
            }
        }

        if (!isForwardOrReplay) {
            messageObject.deleted = roomMessage.isDeleted();
        }

        messageObject.setMessageText(roomMessage.getMessage());

        messageObject.id = isForwardOrReplay ? roomMessage.getMessageId() * (-1) : roomMessage.getMessageId();
        messageObject.forwardedMessage = create(roomMessage.forwardMessage);
        messageObject.replayToMessage = create(roomMessage.getReplyTo());
        messageObject.status = readStatus(roomMessage.getStatus());
        messageObject.authorHash = roomMessage.getAuthorHash();
        messageObject.edited = roomMessage.isEdited();

        if (roomMessage.getAttachment() != null) {
            messageObject.attachment = AttachmentObject.create(roomMessage.getAttachment());
        }
//        messageObject.location = LocationObject.create(roomMessage.getLocation());
//        messageObject.log = LogObject.create(roomMessage.getLog());
        if (roomMessage.getRoomMessageContact() != null) {
            messageObject.contact = RoomContactObject.create(roomMessage.getRoomMessageContact());
        }
//        messageObject.wallet = WalletObject.create(roomMessage.getWallet());
        if (roomMessage.getRealmAdditional()!=null){
            messageObject.additionalData = roomMessage.getRealmAdditional().getAdditionalData();
        }
//        messageObject.additionalType = roomMessage.getAdditionalType();
//        messageObject.channelExtra = roomMessage.getChannelExtra();
        messageObject.messageType = roomMessage.getMessageType().getNumber();
        messageObject.messageVersion = roomMessage.getMessageVersion();
        messageObject.statusVersion = roomMessage.getStatusVersion();
        messageObject.updateTime = roomMessage.getUpdateTime() == 0 ? roomMessage.getCreateTime() * DateUtils.SECOND_IN_MILLIS : roomMessage.getUpdateTime() * DateUtils.SECOND_IN_MILLIS;
        messageObject.createTime = roomMessage.getCreateTime() * DateUtils.SECOND_IN_MILLIS;

        if (isGap) {
            messageObject.previousMessageId = roomMessage.getPreviousMessageId();
        }

        return messageObject;
    }

    public AttachmentObject getAttachment() {
        if (forwardedMessage != null) {
            return forwardedMessage.attachment;
        }
        return attachment;
    }

    public static int readStatus(String status) {
        return ProtoGlobal.RoomMessageStatus.valueOf(status).getNumber();
    }

    public void setMessageText(String messageText) {
        message = messageText.replaceAll("[\\u2063]", "");

        String messageLink = HelperUrl.getLinkInfo(message);
        if (messageLink.length() > 0) {
            hasLink = true;
            linkInfo = messageLink;
        } else {
            hasLink = false;
        }
    }

    public boolean isForwarded() {
        return forwardedMessage != null;
    }

    public boolean isSenderMe() {
        return userId == AccountManager.getInstance().getCurrentUser().getId();
    }

    public static boolean canSharePublic(RealmRoomMessage message) {
        return message != null && message.attachment != null && (!message.messageType.equals(ProtoGlobal.RoomMessageType.VOICE.toString()) || !message.messageType.equals(ProtoGlobal.RoomMessageType.STICKER.toString()));
    }

    public static boolean canSharePublic(MessageObject message) {
        return message != null && message.attachment != null && (message.messageType != ProtoGlobal.RoomMessageType.VOICE_VALUE || message.messageType != ProtoGlobal.RoomMessageType.STICKER_VALUE);
    }

    public boolean allowToForward() {
        return status != MessageObject.STATUS_SENDING && status != MessageObject.STATUS_FAILED;
    }

    public boolean isGiftSticker() {
        return messageType == ProtoGlobal.RoomMessageType.STICKER_VALUE && additionalData != null && additionalType == AdditionalType.GIFT_STICKER;
    }

    public long getUpdateOrCreateTime() {
        return Math.max(updateTime, createTime);
    }
}
