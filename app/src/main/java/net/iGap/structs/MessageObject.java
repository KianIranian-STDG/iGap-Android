package net.iGap.structs;

import net.iGap.G;
import net.iGap.helper.HelperUrl;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGlobal.RoomMessage;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.story.StoryObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static net.iGap.module.AndroidUtils.atSignLink;
import static net.iGap.module.AndroidUtils.botLink;
import static net.iGap.module.AndroidUtils.deepLink;
import static net.iGap.module.AndroidUtils.digitLink;
import static net.iGap.module.AndroidUtils.hashTagLink;
import static net.iGap.module.AndroidUtils.igapLink;
import static net.iGap.module.AndroidUtils.igapResolve;
import static net.iGap.module.AndroidUtils.webLink;
import static net.iGap.module.AndroidUtils.webLink_with_port;
import static net.iGap.proto.ProtoGlobal.RoomMessageType.*;

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
    public StoryObject storyObject;
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
    public long documentId;
    public RoomMessage.ChannelExtra channelExtra;
    public ChannelExtraObject channelExtraObject;
    public ProtoGlobal.Room.Type roomType;
    public long updateTime;
    public long createTime;
    public long futureMessageId;

    public boolean isSelected;
    public String username;
    private HashMap<Integer, String> stringMap = new HashMap<>();
    public int storyStatus;
    private String textConvertToVoice;
    private String messageBeforeEdited;

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
        messageObject.documentId = roomMessage.getDocumentId();

        if (roomMessage.getWallet() != null) {
            messageObject.wallet = WalletObject.create(roomMessage.getWallet());
        }

        messageObject.additionalData = roomMessage.getAdditionalData();
        messageObject.additionalType = roomMessage.getAdditionalType();
        if (roomMessage.getChannelExtra() != null) {
            messageObject.channelExtraObject = ChannelExtraObject.create(roomMessage.getChannelExtra(), messageObject.id);
        }
        messageObject.messageType = roomMessage.getMessageTypeValue();
        messageObject.messageVersion = roomMessage.getMessageVersion();
        messageObject.statusVersion = roomMessage.getStatusVersion();
        messageObject.updateTime = roomMessage.getUpdateTime() == 0 ? roomMessage.getCreateTime() : roomMessage.getUpdateTime();
        messageObject.createTime = roomMessage.getCreateTime();

        if (isGap) {
            messageObject.previousMessageId = roomMessage.getPreviousMessageId();
        }

        return messageObject;
    }

    public static MessageObject create(RealmRoomMessage roomMessage) {
        return create(roomMessage, false, false, false);
    }

    public static MessageObject create(RealmRoomMessage roomMessage, boolean fromShareMedia, boolean isGap, boolean createForForward) {
        if (roomMessage == null) {
            return null;
        }

        MessageObject messageObject = new MessageObject();
        boolean isForwardOrReplay = roomMessage.replyTo != null || roomMessage.forwardMessage != null;

        if (fromShareMedia) {
            messageObject.previousMessageId = roomMessage.getMessageId();
            messageObject.futureMessageId = roomMessage.getMessageId();
        }
        messageObject.roomId = roomMessage.getRoomId();
        if (roomMessage.getUserId() != 0) {
            messageObject.userId = roomMessage.getUserId();
        } else {
            messageObject.roomId = roomMessage.getAuthorRoomId();
            if (isForwardOrReplay) {// FIXME: 12/15/20
                RealmRoom.needGetRoom(roomMessage.getAuthorRoomId());
            }
        }

        if (!isForwardOrReplay) {
            messageObject.deleted = roomMessage.isDeleted();
        }

        messageObject.setMessageText(roomMessage.getMessage());

        messageObject.id = (createForForward && roomMessage.getMessageId() > 0) ? roomMessage.getMessageId() * (-1) : roomMessage.getMessageId();
        messageObject.forwardedMessage = create(roomMessage.getForwardMessage(), false, false, true);
        messageObject.replayToMessage = create(roomMessage.getReplyTo());
        messageObject.status = readStatus(roomMessage.getStatus());
        messageObject.authorHash = roomMessage.getAuthorHash();
        messageObject.edited = roomMessage.isEdited();

        if (roomMessage.getAttachment() != null) {
            messageObject.attachment = AttachmentObject.create(roomMessage.getAttachment());
        }
        messageObject.location = LocationObject.create(roomMessage.getLocation());
        messageObject.log = LogObject.create(roomMessage.getLogs());

        if (roomMessage.getRoomMessageWallet() != null) {
            messageObject.wallet = WalletObject.create(roomMessage.getRoomMessageWallet());
        }

        if (roomMessage.getRoomMessageContact() != null) {
            messageObject.contact = RoomContactObject.create(roomMessage.getRoomMessageContact());
        }

        if (roomMessage.getRealmAdditional() != null) {
            messageObject.additionalData = roomMessage.getRealmAdditional().getAdditionalData();
            messageObject.additionalType = roomMessage.getRealmAdditional().getAdditionalType();
            messageObject.additional = AdditionalObject.create(roomMessage);
        }

        if (roomMessage.getChannelExtra() != null) {
            messageObject.channelExtraObject = ChannelExtraObject.create(roomMessage.getChannelExtra());
        }

        if (roomMessage.getStoryReplyMessage() != null) {
            messageObject.storyObject = StoryObject.create(roomMessage.getStoryReplyMessage());
            messageObject.storyStatus = roomMessage.getStoryStatus();
        }
        messageObject.messageType = roomMessage.getMessageType().getNumber();
        messageObject.messageVersion = roomMessage.getMessageVersion();
        messageObject.statusVersion = roomMessage.getStatusVersion();
        messageObject.updateTime = roomMessage.getUpdateTime() == 0 ? roomMessage.getCreateTime() : roomMessage.getUpdateTime();
        messageObject.createTime = roomMessage.getCreateTime();
        messageObject.documentId = roomMessage.getDocumentId();
        messageObject.setTextConvertToVoice(roomMessage.getTextToVoicePath());
        messageObject.setMessageBeforeEdited(roomMessage.getMessageBeforeEdited());

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

    public boolean isFileExistWithCacheId(boolean forThumb) {
        AttachmentObject attachmentObject = forwardedMessage != null ? forwardedMessage.attachment : attachment;
        if (attachmentObject != null) {
            if (attachmentObject.cacheId != null) {
                String mimeType = DownloadObject.extractMime(attachmentObject.name);
                String path = AndroidUtils.suitableAppFilePath(forNumber(messageType));
                File cashFile = new File(path + "/" + attachmentObject.cacheId + "_" + mimeType);
                File tempFile = new File(G.DIR_TEMP + "/" + DownloadObject.createKey(forThumb ? attachmentObject.smallThumbnail.cacheId : attachmentObject.cacheId, forThumb ? 1 : 0));
                return cashFile.exists() && cashFile.length() == attachmentObject.size || tempFile.length() == (forThumb ? attachmentObject.smallThumbnail.size : attachmentObject.size);
            }
        }
        return false;
    }

    public String getCacheFile(boolean forThumb) {
        AttachmentObject attachmentObject = forwardedMessage != null ? forwardedMessage.attachment : (messageType != STORY_REPLY_VALUE ? attachment : storyObject.attachmentObject);
        if (attachmentObject != null) {
            if (attachmentObject.cacheId != null) {
                String mimeType = DownloadObject.extractMime(attachmentObject.name);
                String path = AndroidUtils.suitableAppFilePath(forNumber(messageType));
                if (forThumb) {
                    return new File(G.DIR_TEMP + "/" + DownloadObject.createKey(forThumb ? attachmentObject.smallThumbnail.cacheId : attachmentObject.cacheId, forThumb ? 1 : 0)).getAbsolutePath();
                }
                return new File(path + "/" + (forThumb ? attachmentObject.largeThumbnail.cacheId : attachmentObject.cacheId) + "_" + mimeType).getAbsolutePath();
            }
        }
        return null;
    }

    public void setMessageText(String messageText) {
        if (messageText != null) {
            message = messageText.replaceAll("[\\u2063]", "");

            String messageLink = HelperUrl.getLinkInfo(message);
            if (messageLink.length() > 0) {
                hasLink = true;
                linkInfo = messageLink;
            } else {
                hasLink = false;
            }
        }
    }

    private String getMessageLinksInfo() {
        LinkedList<String> components = new LinkedList<>(Arrays.asList(message.split("\\s")));
        char[] chars = message.toCharArray();

        int boldPlaces = 0;
        int startDoubleStar = 0;
        boolean starStarted = false;
        for (int i = 0; i < chars.length; i++) {
            if (message.charAt(i) == '*' && (i + 1) < message.length() && message.charAt(i + 1) == '*') {
                if (i > (startDoubleStar + 1) && message.charAt(i - (message.substring((startDoubleStar + 2), i).length() + 1)) == '*' && starStarted) {
                    boldPlaces += 4;
                    starStarted = false;
                } else {
                    starStarted = true;
                    startDoubleStar = i;
                }
            }
        }

        int lastIndex = 0;
        Iterator<String> iterator = components.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.length() == 0) {
                iterator.remove();
                continue;
            }
            int indexOfComponent = message.indexOf(s);
            if (indexOfComponent >= lastIndex) {
                if (AndroidUtils.emojiPattern.matcher(String.valueOf(message.charAt(indexOfComponent))).matches()) {
                    if (indexOfComponent + 1 < (message.length() - 1) && AndroidUtils.emojiPattern.matcher(String.valueOf(message.charAt(indexOfComponent + 1))).matches()) {
                        indexOfComponent += 2;
                        s = s.substring(2);
                    } else {
                        indexOfComponent++;
                        s = s.substring(1);
                    }
                }
                stringMap.put(indexOfComponent, s);
                lastIndex = indexOfComponent;
                iterator.remove();
            }
        }

        String link = "";
        for (Map.Entry<Integer, String> stringEntry : stringMap.entrySet()) {
            if (hashTagLink.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.hash.toString() + "@";
            } else if (atSignLink.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.atSighn.toString() + "@";
            } else if (igapLink.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.igapLink.toString() + "@";
            } else if (stringEntry.getValue().contains(igapResolve)) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.igapResolve.toString() + "@";
            } else if (botLink.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.bot.toString() + "@";
            } else if (webLink.matcher(stringEntry.getValue()).matches() || webLink_with_port.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.webLink.toString() + "@";
            } else if (digitLink.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.digitLink.toString() + "@";
            } else if (deepLink.matcher(stringEntry.getValue()).matches()) {
                link += stringEntry.getKey() - boldPlaces + "_" + (stringEntry.getKey() + stringEntry.getValue().length() - boldPlaces) + "_" + HelperUrl.linkType.igapDeepLink.toString() + "@";
            }
        }
        return link;
    }

    public boolean isForwarded() {
        return forwardedMessage != null;
    }

    public boolean isSenderMe() {
        return userId == AccountManager.getInstance().getCurrentUser().getId();
    }

    public static boolean canSharePublic(MessageObject message) {
        return message != null && message.attachment != null && (message.messageType != VOICE_VALUE || message.messageType != STICKER_VALUE);
    }

    public static boolean canSharePublic(RealmRoomMessage message) {
        return message != null && message.attachment != null && (!message.messageType.equals(VOICE.toString()) || !message.messageType.equals(STICKER.toString()));
    }

    public boolean allowToForward() {
        return status != MessageObject.STATUS_SENDING && status != MessageObject.STATUS_FAILED;
    }

    public boolean isGiftSticker() {
        return messageType == STICKER_VALUE && additionalData != null && additionalType == AdditionalType.GIFT_STICKER;
    }

    public boolean isTimeOrLogMessage() {
        return userId == -1L;
    }

    public long getUpdateOrCreateTime() {
        return Math.max(updateTime, createTime);
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "forwardedMessage=" + forwardedMessage +
                ", replayToMessage=" + replayToMessage +
                ", location=" + location +
                ", contact=" + contact +
                ", log=" + log +
                ", wallet=" + wallet +
                ", attachment=" + attachment +
                ", additional=" + additional +
                ", additionalData='" + additionalData + '\'' +
                ", additionalType=" + additionalType +
                ", id=" + id +
                ", message='" + message + '\'' +
                ", needToShow=" + needToShow +
                ", hasLink=" + hasLink +
                ", linkInfo='" + linkInfo + '\'' +
                ", status=" + status +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", authorHash='" + authorHash + '\'' +
                ", deleted=" + deleted +
                ", edited=" + edited +
                ", messageType=" + messageType +
                ", messageVersion=" + messageVersion +
                ", statusVersion=" + statusVersion +
                ", previousMessageId=" + previousMessageId +
                ", channelExtra=" + channelExtraObject +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                ", futureMessageId=" + futureMessageId +
                ", isSelected=" + isSelected +
                '}';
    }

    public AdditionalObject getAdditional() {
        if (additional != null) {
            return additional;
        } else if (forwardedMessage != null && forwardedMessage.getAdditional() != null) {
            return forwardedMessage.additional;
        }
        return null;
    }

    public String getTextConvertToVoice() {
        return textConvertToVoice;
    }

    public void setTextConvertToVoice(String textConvertToVoice) {
        this.textConvertToVoice = textConvertToVoice;
    }

    public String getMessageBeforeEdited() {
        return messageBeforeEdited;
    }

    public void setMessageBeforeEdited(String messageBeforeEdited) {
        this.messageBeforeEdited = messageBeforeEdited;
    }
}
