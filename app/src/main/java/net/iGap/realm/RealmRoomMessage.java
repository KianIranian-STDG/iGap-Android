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

import android.os.Looper;
import android.text.format.DateUtils;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import net.iGap.G;
import net.iGap.R;
import net.iGap.controllers.MessageController;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTimeOut;
import net.iGap.helper.HelperUrl;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.SUID;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.module.enums.LocalFileType;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.net_iGap_realm_RealmRoomMessageRealmProxy;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;

@Parcel(implementations = {net_iGap_realm_RealmRoomMessageRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmRoomMessage.class})
public class RealmRoomMessage extends RealmObject {
    @PrimaryKey
    public long messageId;
    @Index
    public long roomId;
    public long messageVersion;
    public String status;
    public long statusVersion;
    public String messageType;
    public String message;
    public boolean hasMessageLink = false;
    public RealmAttachment attachment;
    public long userId;
    public RealmRoomMessageLocation location;
    public RealmRoomMessageContact roomMessageContact;
    public RealmRoomMessageWallet roomMessageWallet;
    public RealmAdditional realmAdditional;
    public boolean edited;
    public long createTime;
    public long updateTime;
    public boolean deleted = false;
    public RealmRoomMessage forwardMessage;
    public RealmRoomMessage replyTo;
    public boolean showMessage = true;
    public String authorHash;
    public boolean hasEmojiInText;
    public boolean showTime = false;
    //if it was needed in the future we can use RealmAuthor instead of author hash and also maybe authorRoomId
    public long authorRoomId;
    // for channel message should be exist in other rooms (forwarded message)
    public RealmChannelExtra channelExtra;
    public long previousMessageId;
    public long futureMessageId;
    public String linkInfo;
    public byte[] Logs;

    /**
     * if has forward return that otherwise return enter value
     */
    public static RealmRoomMessage getFinalMessage(RealmRoomMessage realmRoomMessage) {
        if (realmRoomMessage != null && realmRoomMessage.isValid()) {
            if (realmRoomMessage.getForwardMessage() != null && realmRoomMessage.getForwardMessage().isValid()) {
                return realmRoomMessage.getForwardMessage();
            }
        }
        return realmRoomMessage;
    }

    public static RealmRoomMessage findMessage(Realm realm, long messageId) {
        return realm.where(RealmRoomMessage.class).equalTo("deleted", false).equalTo("messageId", messageId).findFirst();
    }

    public static RealmRoomMessage findLastMessage(Realm realm, long roomId) {
        //TODO [Saeed Mozaffari] [2017-10-23 11:24 AM] - Can Write Better Code?
        RealmResults<RealmRoomMessage> realmRoomMessages = findDescending(realm, roomId);
        RealmRoomMessage realmRoomMessage = null;
        if (realmRoomMessages.size() > 0) {
            realmRoomMessage = realmRoomMessages.first();
        }
        return realmRoomMessage;
    }

    public static RealmResults<RealmRoomMessage> findDescending(Realm realm, long roomId) {
        return findSorted(realm, roomId, "messageId", Sort.DESCENDING);
    }

    public static RealmResults<RealmRoomMessage> findAscending(Realm realm, long roomId) {
        return findSorted(realm, roomId, "messageId", Sort.ASCENDING);
    }

    public static RealmResults<RealmRoomMessage> findSorted(Realm realm, long roomId, String fieldName, Sort sortOrder) {
        return realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).equalTo("deleted", false).findAll().sort(fieldName, sortOrder);
    }

    public static RealmResults<RealmRoomMessage> findNotificationMessage(Realm realm) {
        return realm.where(RealmRoomMessage.class)
                .in("status", new String[]{ProtoGlobal.RoomMessageStatus.SENT.toString(), ProtoGlobal.RoomMessageStatus.DELIVERED.toString()})
                .equalTo("deleted", false)
                .notEqualTo("authorHash", RealmUserInfo.getCurrentUserAuthorHash())
                .notEqualTo("userId", AccountManager.getInstance().getCurrentUser().getId())
                .notEqualTo("messageType", ProtoGlobal.RoomMessageType.LOG.toString())
                .findAll().sort("updateTime", Sort.DESCENDING);
    }

    public static RealmResults<RealmRoomMessage> filterMessage(Realm realm, long roomId, ProtoGlobal.RoomMessageType messageType) {
        RealmResults<RealmRoomMessage> results;
        if (messageType == ProtoGlobal.RoomMessageType.TEXT) {
            results = realm.where(RealmRoomMessage.class).
                    equalTo("roomId", roomId).
                    equalTo("messageType", messageType.toString()).
                    equalTo("deleted", false).
                    equalTo("hasMessageLink", true).
                    contains("linkInfo", HelperUrl.linkType.webLink.toString()).
                    findAll().sort("updateTime", Sort.DESCENDING);
        } else {
            //TODO [Saeed Mozaffari] [2017-10-28 9:59 AM] - Can Write Better Code?
            results = realm.where(RealmRoomMessage.class).
                    equalTo("roomId", roomId).
                    in("messageType", new String[]{messageType.toString(), messageType.toString() + "_TEXT"}).
                    equalTo("deleted", false).
                    findAll().sort("updateTime", Sort.DESCENDING);
        }
        return results;
    }

    /**
     * @param messageType if set null will be checked both VIDEO and IMAGE type
     */
    public static boolean isImageOrVideo(RealmRoomMessage roomMessage, @Nullable ProtoGlobal.RoomMessageType messageType) {
        boolean isImageOrVideo = false;
        if (messageType == null) {
            if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                isImageOrVideo = true;
            } else if (roomMessage.getForwardMessage() != null) {
                ProtoGlobal.RoomMessageType messageTypeForward = roomMessage.getForwardMessage().getMessageType();
                if (messageTypeForward == ProtoGlobal.RoomMessageType.IMAGE || messageTypeForward == ProtoGlobal.RoomMessageType.IMAGE_TEXT || messageTypeForward == ProtoGlobal.RoomMessageType.VIDEO || messageTypeForward == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                    isImageOrVideo = true;
                }
            }
        } else if (messageType == ProtoGlobal.RoomMessageType.VIDEO) {
            if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                isImageOrVideo = true;
            }
        } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE) {
            if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                isImageOrVideo = true;
            }
        }

        return isImageOrVideo;
    }

    public static long findCustomMessageId(long roomId, int count) {
        return DbManager.getInstance().doRealmTask(realm -> {
            long messageId = 0;
            if (count == 0) {
                messageId = 0;
            } else if (count == -1) {
                RealmRoomMessage realmRoomMessage = RealmRoomMessage.findLastMessage(realm, roomId);
                if (realmRoomMessage != null) {
                    messageId = realmRoomMessage.getMessageId();
                } else {
                    messageId = 0;
                }
            } else {
                if (count > 0) {
                    RealmResults<RealmRoomMessage> realmRoomMessages = RealmRoomMessage.findDescending(realm, roomId);
                    if (realmRoomMessages.size() <= count) {
                        messageId = 0;
                    } else {
                        RealmRoomMessage message = realmRoomMessages.get(count);
                        if (message != null) {
                            messageId = message.getMessageId();
                        }
                    }
                }
            }
            return messageId;
        });
    }

    public static void makeSeenAllMessageOfRoom(long roomId) {
        // use for chat or group
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (room != null && room.getType() != ProtoGlobal.Room.Type.CHANNEL) {
                    RealmResults<RealmRoomMessage> realmRoomMessages =
                            realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).notEqualTo("status", ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo("status", ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findAll().sort("messageId", Sort.ASCENDING);
                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", roomId).findFirst();
                    if (realmClientCondition != null) {
                        for (RealmRoomMessage roomMessage : realmRoomMessages) {
                            if (roomMessage != null) {
                                if (roomMessage.getUserId() != AccountManager.getInstance().getCurrentUser().getId() && !realmClientCondition.containsOfflineSeen(roomMessage.getMessageId())) {
                                    roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                                    RealmClientCondition.addOfflineSeen(realm, realmClientCondition, roomMessage.getMessageId());
                                    MessageController.getInstance(AccountManager.selectedAccount).sendUpdateStatus(room.getType(), room.getId(), roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                                   // G.chatUpdateStatusUtil.sendUpdateStatus(room.getType(), room.getId(), roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                                }
                            }
                        }
                    }
                }
            });
        }).start();
    }

    public static RealmRoomMessage putOrUpdate(Realm realm, long roomId, ProtoGlobal.RoomMessage input, StructMessageOption messageOption) {
        long messageId;
        if (messageOption.isForwardOrReply()) {
            /**
             * for forward and reply set new messageId for create new message if before not exist
             */
            messageId = input.getMessageId() * (-1);
        } else {
            messageId = input.getMessageId();
        }
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();

        if (message == null) {
            message = realm.createObject(RealmRoomMessage.class, messageId);
        }
        message.setRoomId(roomId);
        if (input.hasForwardFrom()) {
            message.setForwardMessage(putOrUpdate(realm, -1, input.getForwardFrom(), new StructMessageOption().setGap().setForwardOrReply()));
        }
        if (input.hasReplyTo()) {
            message.setReplyTo(putOrUpdate(realm, -1, input.getReplyTo(), new StructMessageOption().setGap().setForwardOrReply()));
        }
        message.setShowMessage(true);

        if (messageOption.isFromShareMedia()) {
            message.setPreviousMessageId(input.getMessageId());
            message.setFutureMessageId(input.getMessageId());
        }

        message.setMessage(input.getMessage());

        message.setStatus(input.getStatus().toString());

        if (input.getAuthor().hasUser()) {
            message.setUserId(input.getAuthor().getUser().getUserId());
        } else {
            message.setUserId(0);
            message.setAuthorRoomId(input.getAuthor().getRoom().getRoomId());
            /**
             * if message is forward or reply check room exist or not for get info for
             * that room (hint : reply not important for this subject)
             * if this message isn't forward client before got this info and now don't
             * need to get it again
             */
            if (messageOption.isForwardOrReply()) {
                RealmRoom.needGetRoom(input.getAuthor().getRoom().getRoomId());
            }
        }
        message.setAuthorHash(input.getAuthor().getHash());

        if (!messageOption.isForwardOrReply()) {
            message.setDeleted(input.getDeleted());
        }

        message.setEdited(input.getEdited());

        if (input.hasAttachment()) {
            message.setAttachment(RealmAttachment.build(realm, input.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT, input.getMessageType()));

            if (message.getAttachment().getSmallThumbnail() == null) {
                long smallId = SUID.id().get();
                RealmThumbnail smallThumbnail = RealmThumbnail.put(realm, smallId, message.getAttachment().getId(), input.getAttachment().getSmallThumbnail());
                message.getAttachment().setSmallThumbnail(smallThumbnail);
            }

            message.getAttachment().setDuration(input.getAttachment().getDuration());
            message.getAttachment().setSize(input.getAttachment().getSize());
            message.getAttachment().setCacheId(input.getAttachment().getCacheId());

            if (message.getAttachment().getName() == null) {
                message.getAttachment().setName(input.getAttachment().getName());
            }
        }
        if (input.hasLocation()) {

            Long id = null;
            if (message.getLocation() != null) id = message.getLocation().getId();

            message.setLocation(RealmRoomMessageLocation.put(realm, input.getLocation(), id));
        }
        if (input.hasLog()) {
            if (input.getReplyTo() != null) {
                message.setLogs(HelperLogMessage.serializeLog(roomId, input.getAuthor(), input.getLog(), input.getMessageId(), input.getReplyTo().getMessage(), input.getReplyTo().getMessageType()));
            } else {
                message.setLogs(HelperLogMessage.serializeLog(roomId, input.getAuthor(), input.getLog(), input.getMessageId(), "", null));
            }
        }
        if (input.hasContact()) {
            message.setRoomMessageContact(RealmRoomMessageContact.put(realm, input.getContact()));
        }

        if (input.hasWallet()) {
            message.setRoomMessageWallet(RealmRoomMessageWallet.put(realm, input.getWallet()));
        }


        if (input.getAdditionalType() != 0 && !input.getAdditionalData().isEmpty()) {
            if (message.getRealmAdditional() == null) {
                message.setRealmAdditional(RealmAdditional.put(realm, input));
            }
        }

        message.setMessageType(input.getMessageType());
        message.setMessageVersion(input.getMessageVersion());
        message.setStatusVersion(input.getStatusVersion());
        if (input.getUpdateTime() == 0) {
            message.setUpdateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);
        } else {
            message.setUpdateTime(input.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);
        }
        message.setCreateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);

        if (messageOption.isGap()) {
            message.setPreviousMessageId(input.getPreviousMessageId());
        }

        if (input.hasChannelExtra()) {
            message.channelExtra = RealmChannelExtra.putOrUpdate(realm, input.getMessageId(), input.getChannelExtra());
        }

//        addTimeIfNeed(message, realm);
//
//        isEmojiInText(message, input.getMessage());

        return message;
    }

    public static RealmRoomMessage getRealmRoomMessage(Realm realm, long messageId) {
        return realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
    }

    public static void ClearAllMessage(Realm realm) {
        RealmQuery<RealmRoomMessage> roomRealmQuery = realm.where(RealmRoomMessage.class);
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
            if (realmRoom.getLastMessage() != null && realmRoom.getLastMessage().getForwardMessage() == null && realmRoom.getLastMessage().getReplyTo() == null) {
                roomRealmQuery.notEqualTo("messageId", realmRoom.getLastMessage().getMessageId());
            }

            if (realmRoom.getFirstUnreadMessage() != null && realmRoom.getFirstUnreadMessage().getForwardMessage() == null && realmRoom.getFirstUnreadMessage().getReplyTo() == null) {
                roomRealmQuery.notEqualTo("messageId", realmRoom.getFirstUnreadMessage().getMessageId());
            }
        }
        roomRealmQuery.findAll().deleteAllFromRealm();
    }

    public static void ClearAllMessageRoomAsync(Realm realm, final long roomId, Realm.Transaction.OnSuccess onSuccess) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (realmRoom != null) {
                    if (realmRoom.getLastMessage() != null && realmRoom.getLastMessage().getForwardMessage() == null && realmRoom.getLastMessage().getReplyTo() == null) {
                        realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).notEqualTo("messageId", realmRoom.getLastMessage().getMessageId()).findAll().deleteAllFromRealm();
                    } else {
                        realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();
                    }
                }
            }
        }, onSuccess);
    }

    public static void addTimeIfNeed(RealmRoomMessage message, Realm realm) {

        if (!message.isShowMessage()) {
            return;
        }

        RealmRoomMessage nextMessage = realm.where(RealmRoomMessage.class).equalTo("roomId", message.getRoomId()).equalTo("showTime", true).equalTo("showMessage", true).equalTo("deleted", false).greaterThan("messageId", message.getMessageId()).findFirst();

        RealmRoomMessage lastMessage = null;

        RealmResults<RealmRoomMessage> list = realm.where(RealmRoomMessage.class).equalTo("roomId", message.getRoomId()).equalTo("showTime", true).equalTo("showMessage", true).equalTo("deleted", false).lessThan("messageId", message.getMessageId()).findAll();

        if (list.size() > 0) {
            lastMessage = list.last();
        }

        if (lastMessage == null) {
            message.setShowTime(true);
        } else {
            message.setShowTime(isTimeDayDifferent(message.getUpdateTime(), lastMessage.getUpdateTime()));
        }

        if (nextMessage != null && message.isShowTime()) {

            boolean difTime = isTimeDayDifferent(message.getUpdateTime(), nextMessage.getUpdateTime());
            nextMessage.setShowTime(difTime);
        }
    }

    public static boolean isTimeDayDifferent(long time, long nextTime) {

        Calendar date1 = Calendar.getInstance();
        date1.setTimeInMillis(time);

        Calendar date2 = Calendar.getInstance();
        date2.setTimeInMillis(nextTime);

        return date1.get(Calendar.YEAR) != date2.get(Calendar.YEAR) || date1.get(Calendar.DAY_OF_YEAR) != date2.get(Calendar.DAY_OF_YEAR);
    }

    public static void isEmojiInText(RealmRoomMessage roomMessage, String message) {
//        try {
//            if (EmojiUtils.emojisCount(message) > 0) {
//                roomMessage.setHasEmojiInText(true);
//            } else {
//                roomMessage.setHasEmojiInText(false);
//            }
//        } catch (Exception e) {
//            roomMessage.setHasEmojiInText(true);
//        }
    }

    public static long getReplyMessageId(RealmRoomMessage realmRoomMessage) {
        if (realmRoomMessage != null && realmRoomMessage.getReplyTo() != null) {
            if (realmRoomMessage.getReplyTo().getMessageId() < 0) {
                return (realmRoomMessage.getReplyTo().getMessageId() * (-1));
            } else {
                return realmRoomMessage.getReplyTo().getMessageId();
            }
        }
        return 0;
    }

    public void removeFromRealm(Realm realm) {
        if (realmAdditional != null && realmAdditional.isValid())
            realmAdditional.deleteFromRealm();

        if (attachment != null && attachment.isValid()) {
            long count = realm.where(RealmRoomMessage.class)
                    .equalTo("attachment.id", attachment.getId())
                    .count();

            if (count == 1) // 1 is for this message
                attachment.deleteFromRealm();
        }

        if (location != null && location.isValid())
            location.deleteFromRealm();

        if (roomMessageContact != null && roomMessageContact.isValid())
            roomMessageContact.deleteFromRealm();

        if (roomMessageWallet != null && roomMessageWallet.isValid())
            roomMessageWallet.deleteFromRealm();

        if (forwardMessage != null && forwardMessage.isValid()) {
            long count = realm.where(RealmRoomMessage.class)
                    .equalTo("forwardMessage.messageId", forwardMessage.getMessageId())
                    .or()
                    .equalTo("replyTo.messageId", forwardMessage.getMessageId())
                    .count();
            if (count == 1) // 1 is for this message
                forwardMessage.deleteFromRealm();
        }

        if (replyTo != null && replyTo.isValid()) {
            long count = realm.where(RealmRoomMessage.class)
                    .equalTo("forwardMessage.messageId", replyTo.getMessageId())
                    .or()
                    .equalTo("replyTo.messageId", replyTo.getMessageId())
                    .count();
            if (count == 1) // 1 is for this message
                replyTo.deleteFromRealm();
        }

        this.deleteFromRealm();
    }

    /**
     * make messages failed
     */
    public static void makeFailed(final long messageId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(() -> makeFailed(messageId)).start();
            return;
        }

        FragmentChat.removeResendList(messageId);
        DbManager.getInstance().doRealmTransaction(realm -> {
            setStatusFailedInChat(realm, messageId);
        });
    }

    /**
     * detect that message is exist in realm or no
     *
     * @param messageId messageId for checking
     */
    public static boolean existMessageInRoom(long messageId, long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId)
                    .equalTo("roomId", roomId)
                    .findFirst();
            return realmRoomMessage != null;
        });
    }


    public static RealmRoomMessage deleteMessage(Realm realm, long messageId, long roomId) {
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId)
                .equalTo("roomId", roomId).findFirst();
        if (message != null) {
            message.deleteFromRealm();
        }
        return message;
    }

    public static void deleteAllMessage(Realm realm, long roomId) {
        realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();
    }

    public static void deleteAllMessage(final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            deleteAllMessage(realm, roomId);
        });
    }

    public static void deleteAllMessageLessThan(final long roomId, final long lessThan) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).lessThanOrEqualTo("messageId", lessThan).findAll().deleteAllFromRealm();
        });
    }


    // TODO: 1/9/21 Delete this method after completing deleting messages (MESSAGE_REFACTOR)
/*    public static void deleteSelectedMessages(Realm realm, final long RoomId, final ArrayList<Long> list, final ArrayList<Long> bothDeleteMessageId, final ProtoGlobal.Room.Type roomType) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // get offline delete list , add new deleted list and update in
                // client condition , then send request for delete message to server
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", RoomId).findFirst();

                for (final Long messageId : list) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();

                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);

                        // stop download
                        if (roomMessage.getAttachment() != null) {
                            Downloader.getInstance(AccountManager.selectedAccount).cancelDownload(roomMessage.getAttachment().getCacheId());
                        }
                    }

                    boolean bothDelete = false;
                    if (bothDeleteMessageId != null && bothDeleteMessageId.contains(messageId)) {
                        bothDelete = true;
                    }

                    // check here with bothDeleteMessageId for send both or no

                    if (realmClientCondition != null) {
                        RealmClientCondition.addOfflineDelete(realm, realmClientCondition, messageId, roomType, bothDelete);
                    }

                    if (roomType == GROUP) {
                        new RequestGroupDeleteMessage().groupDeleteMessage(RoomId, messageId);
                    } else if (roomType == CHAT) {
                        new RequestChatDeleteMessage().chatDeleteMessage(RoomId, messageId, bothDelete);
                    } else if (roomType == CHANNEL) {
                        new RequestChannelDeleteMessage().channelDeleteMessage(RoomId, messageId);
                    }
                }
            }
        });
    }*/
    public static boolean isBothDelete(long messageTime) {
        long currentTime;
        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            currentTime = G.currentServerTime * DateUtils.SECOND_IN_MILLIS;
        } else {
            currentTime = System.currentTimeMillis();
        }

        return !HelperTimeOut.timeoutChecking(currentTime, messageTime, G.bothChatDeleteTime);
    }

    public static long getMessageTime(long messageId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            return RealmRoomMessage.getRealmRoomMessage(realm, messageId).getUpdateTime();
        });
    }

    public static void editMessageClient(final long roomId, final long messageId, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTransaction(realm -> {
                    final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", roomId).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.getOfflineEdited().add(RealmOfflineEdited.put(realm, messageId, message));
                    }
                });
            }
        }).start();
    }

    public static void editMessageServerResponse(final long messageId, final long messageVersion, final String message, final ProtoGlobal.RoomMessageType messageType) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
            if (roomMessage != null) {
                roomMessage.setMessage(message);
                roomMessage.setMessageVersion(messageVersion);
                roomMessage.setEdited(true);
                roomMessage.setMessageType(messageType);
            }
        });
    }

    /**
     * check SEEN and LISTENED for avoid from duplicate send status request in enter to chat because in
     * send status again enter ro chat fetchMessage method will be send status so client shouldn't
     */
    public static void setStatusSeenInChat(Realm realm, final long messageId) {
        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).notEqualTo("status", ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo("status", ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findFirst();
        if (realmRoomMessage != null) {
            if (!realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
            }
        }
    }

    public static void setStatusFailedInChat(Realm realm, long messageId) {
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).onMessageFailed(message.getRoomId(), message.getMessageId());
        }
    }

    public static void setStatus(final long messageId, final ProtoGlobal.RoomMessageStatus messageStatus) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            setStatus(realm, messageId, messageStatus);
        });
    }

    public static void setStatus(Realm realm, long messageId, ProtoGlobal.RoomMessageStatus messageStatus) {
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
        if (roomMessage != null) {
            roomMessage.setStatus(messageStatus.toString());
        }
    }

    public static RealmRoomMessage setStatusServerResponse(Realm realm, long messageId, long statusVersion, ProtoGlobal.RoomMessageStatus messageStatus) {
        RealmRoomMessage roomMessage;
        if (messageStatus != ProtoGlobal.RoomMessageStatus.LISTENED) {
            roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).notEqualTo("status", ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo("status", ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findFirst();
        } else {
            roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
        }

        if (roomMessage != null) {
            roomMessage.setStatus(messageStatus.toString());
            roomMessage.setStatusVersion(statusVersion);
            realm.copyToRealmOrUpdate(roomMessage);
        }
        return roomMessage;
    }

    /**
     * set new gap state for UP and DOWN for {@param messageId} (BothDirections)
     *
     * @param messageId message that want set gapMessageId to that
     */
    public static void setGap(final long messageId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            setGapInTransaction(realm, messageId);
        });
    }

    public static void setGapInTransaction(Realm realm, final long messageId) {
        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
        if (realmRoomMessage != null) {
            realmRoomMessage.setPreviousMessageId(messageId);
            realmRoomMessage.setFutureMessageId(messageId);
        }
    }

    /*public int getVoteUp() {
        return voteUp;
    }

    public void setVoteUp(int voteUp) {
        this.voteUp = voteUp;
    }

    public int getVoteDown() {
        return voteDown;
    }

    public void setVoteDown(int voteDown) {
        this.voteDown = voteDown;
    }

    public int getViewsLabel() {
        return seenCount;
    }

    public void setViewsLabel(int seenCount) {
        this.seenCount = seenCount;
    }*/

    public static RealmRoomMessage makeUnreadMessage(int countNewMessage) {
        RealmRoomMessage message = new RealmRoomMessage();
        message.setMessageId(TimeUtils.currentLocalTime());
        message.setUserId(-1); // -1 means time message or unread message
        message.setMessage(countNewMessage + " " + G.fragmentActivity.getResources().getString(R.string.unread_message));
        message.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
        return message;
    }

    public static RealmRoomMessage makeTimeMessage(long time, String message) {
        RealmRoomMessage timeMessage = new RealmRoomMessage();
        timeMessage.setMessageId(SUID.id().get());
        timeMessage.setUserId(-1); // -1 means time message or unread message
        timeMessage.setUpdateTime(time);
        timeMessage.setMessage(message);
        timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
        return timeMessage;
    }

    public static RealmRoomMessage makeTextMessage(final long roomId, final String message, final long replyMessageId, String additinalData, int additionalType) {
        if (message.isEmpty()) {
            return null;
        }

        return DbManager.getInstance().doRealmTask(realm -> {
            final long messageId = AppUtils.makeRandomId();
            final long currentTime = TimeUtils.currentLocalTime();
            RealmRoomMessage roomMessage = new RealmRoomMessage();
            roomMessage.setMessageId(messageId);
            roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
            roomMessage.setMessage(message);
            roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
            RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
            RealmRoomMessage.isEmojiInText(roomMessage, message);
            roomMessage.setRoomId(roomId);
            roomMessage.setShowMessage(true);
            roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
            roomMessage.setAuthorHash(RealmUserInfo.getCurrentUserAuthorHash());
            roomMessage.setCreateTime(currentTime);
            if (additinalData != null) {
                RealmAdditional realmAdditional = new RealmAdditional();
                realmAdditional.setId(AppUtils.makeRandomId());
                realmAdditional.setAdditionalData(additinalData);
                realmAdditional.setAdditionalType(additionalType);

                roomMessage.setRealmAdditional(realmAdditional);
            }

            /**
             *  user wants to replay to a message
             */
            if (replyMessageId > 0) {
                RealmRoomMessage messageToReplay = realm.where(RealmRoomMessage.class).equalTo("messageId", replyMessageId).findFirst();
                if (messageToReplay != null) {
                    roomMessage.setReplyTo(realm.copyFromRealm(messageToReplay));
                }
            }

            new Thread(() -> {
                DbManager.getInstance().doRealmTransaction(realm12 -> {
                    RealmRoomMessage managedRoomMessage = realm12.copyToRealmOrUpdate(roomMessage);
                    RealmRoom.setLastMessageWithRoomMessage(realm12, roomId, managedRoomMessage);
                    if (RealmRoom.detectType(roomId) == CHANNEL) {
                        RealmChannelExtra.putDefault(realm12, roomId, messageId);
                    }
                });
            }).start();

            return roomMessage;
        });
    }

    public static RealmRoomMessage makeTextMessage(final long roomId, final String message, final long replyMessageId) {
        return makeTextMessage(roomId, message, replyMessageId, null, 0);
    }

    public static RealmRoomMessage makeTextMessage(final long roomId, final long messageId, final String message) {
        RealmRoomMessage roomMessage = new RealmRoomMessage();
        roomMessage.setMessageId(messageId);
        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
        roomMessage.setRoomId(roomId);
        roomMessage.setMessage(message);
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
        roomMessage.setCreateTime(TimeUtils.currentLocalTime());

        return roomMessage;
    }

    public static RealmRoomMessage makeTextMessage(final long roomId, final String message) {
        return makeTextMessage(roomId, System.currentTimeMillis(), message);
    }

    public static RealmRoomMessage makeAdditionalData(final long roomId, final long messageId, final String message, String additionalData, int additionalTaype, Realm realm, ProtoGlobal.RoomMessageType messageType) {

        RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);
        roomMessage.setMessageType(messageType);
        roomMessage.setRoomId(roomId);
        roomMessage.setMessage(message);
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
        roomMessage.setCreateTime(TimeUtils.currentLocalTime());

        if (additionalData != null) {

            JsonObject rootObject = new JsonObject();
            rootObject.addProperty("label", "");
            rootObject.addProperty("imageUrl", "");
            rootObject.addProperty("actionType", "9");
            rootObject.addProperty("value", additionalData);

            roomMessage.setRealmAdditional(RealmAdditional.put(realm, rootObject.toString(), additionalTaype));
        }

        return roomMessage;
    }

    public static RealmRoomMessage makeVoiceMessage(final long roomId, final ProtoGlobal.Room.Type roomType, final String filepath, final String message) {
        final long messageId = AppUtils.makeRandomId();
        final long updateTime = TimeUtils.currentLocalTime();
        final long duration = AndroidUtils.getAudioDuration(G.fragmentActivity, filepath) / 1000;
        RealmRoomMessage roomMessage = new RealmRoomMessage();
        roomMessage.setMessageId(messageId);
        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE);
        roomMessage.setMessage(message);
        roomMessage.setRoomId(roomId);
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        RealmAttachment realmAttachment = new RealmAttachment();
        realmAttachment.setId(messageId);
        realmAttachment.setLocalFilePath(filepath);
        realmAttachment.setWidth(0);
        realmAttachment.setSize(0);
        realmAttachment.setHeight(0);
        realmAttachment.setName(null);
        realmAttachment.setDuration(duration);


        roomMessage.setAttachment(realmAttachment);
        roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
        roomMessage.setCreateTime(updateTime);
        if (roomType.equals(CHANNEL)) {
            RealmChannelExtra channelExtra = new RealmChannelExtra();
            channelExtra.setMessageId(messageId);
            channelExtra.setThumbsUp("0");
            channelExtra.setThumbsDown("0");
            channelExtra.setViewsLabel("1");

            if (RealmRoom.showSignature(roomId)) {
                channelExtra.setSignature(AccountManager.getInstance().getCurrentUser().getName());
            } else {
                channelExtra.setSignature("");
            }

            roomMessage.setChannelExtra(channelExtra);
        }

        return roomMessage;
    }

    public static void makeForwardMessage(Realm realm, long roomId, long messageId, RealmRoomMessage message, boolean isMessage) {
        if (isMessage && message.getForwardMessage() == null) {
            message.setMessageId(messageId);
            message.setCreateTime(TimeUtils.currentLocalTime());
            message.setRoomId(roomId);
            message.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
            message.setUserId(AccountManager.getInstance().getCurrentUser().getId());
            realm.copyToRealmOrUpdate(message);
        } else {
            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", message.getMessageId()).findFirst();
            if (roomMessage != null) {
                RealmRoomMessage forwardedMessage = realm.createObject(RealmRoomMessage.class, messageId);
                if (roomMessage.getForwardMessage() != null) {
                    forwardedMessage.setForwardMessage(roomMessage.getForwardMessage());
                    forwardedMessage.setHasMessageLink(roomMessage.getForwardMessage().getHasMessageLink());
                } else {
                    forwardedMessage.setForwardMessage(roomMessage);
                    forwardedMessage.setHasMessageLink(roomMessage.getHasMessageLink());
                }

                forwardedMessage.setCreateTime(TimeUtils.currentLocalTime());
                forwardedMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                forwardedMessage.setRoomId(roomId);
                forwardedMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                forwardedMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
                forwardedMessage.setShowMessage(true);
            }
        }
    }

    public long getUpdateOrCreateTime() {
        return updateTime >= createTime ? updateTime : createTime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(long messageVersion) {
        this.messageVersion = messageVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(long statusVersion) {
        this.statusVersion = statusVersion;
    }

    public ProtoGlobal.RoomMessageType getMessageType() {

        if (messageType == null) {
            return ProtoGlobal.RoomMessageType.UNRECOGNIZED;
        }
        try {
            return ProtoGlobal.RoomMessageType.valueOf(messageType);
        } catch (RuntimeException e) {
            return ProtoGlobal.RoomMessageType.UNRECOGNIZED;
        }

    }

    public void setMessageType(ProtoGlobal.RoomMessageType messageType) {
        this.messageType = messageType.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {

        message = message.replaceAll("[\\u2063]", "");


        try {
            this.message = message;
        } catch (Exception e) {
            this.message = HelperString.getUtf8String(message);
        }

        String linkInfo = HelperUrl.getLinkInfo(message);
        if (linkInfo.length() > 0) {
            setHasMessageLink(true);
            setLinkInfo(linkInfo);
        } else {
            setHasMessageLink(false);
        }
    }

    public boolean getHasMessageLink() {
        return hasMessageLink;
    }

    public void setHasMessageLink(boolean hasMessageLink) {
        this.hasMessageLink = hasMessageLink;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isHasEmojiInText() {
        return hasEmojiInText;
    }

    public void setHasEmojiInText(boolean hasEmojiInText) {
        this.hasEmojiInText = hasEmojiInText;
    }

    public RealmRoomMessageLocation getLocation() {
        return location;
    }

    public void setLocation(RealmRoomMessageLocation location) {
        this.location = location;
    }

    public byte[] getLogs() {
        return Logs;
    }

    public void setLogs(byte[] logs) {
        Logs = logs;
    }

    public RealmRoomMessageContact getRoomMessageContact() {
        return roomMessageContact;
    }

    public void setRoomMessageContact(RealmRoomMessageContact roomMessageContact) {
        this.roomMessageContact = roomMessageContact;
    }

    public RealmRoomMessageWallet getRoomMessageWallet() {
        return roomMessageWallet;
    }

    public void setRoomMessageWallet(RealmRoomMessageWallet roomMessageWallet) {
        this.roomMessageWallet = roomMessageWallet;
    }

    public RealmAdditional getRealmAdditional() {
        return realmAdditional;
    }

    public void setRealmAdditional(RealmAdditional realmAdditional) {
        this.realmAdditional = realmAdditional;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public RealmChannelExtra getChannelExtra() {
        return channelExtra;
    }

    public void setChannelExtra(RealmChannelExtra channelExtra) {
        this.channelExtra = channelExtra;
    }

    public RealmRoomMessage getForwardMessage() {
        return forwardMessage;
    }

    public void setForwardMessage(RealmRoomMessage forwardMessage) {
        this.forwardMessage = forwardMessage;
    }

    public RealmRoomMessage getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(RealmRoomMessage replyTo) {
        this.replyTo = replyTo;
    }

    public long getAuthorRoomId() {
        return authorRoomId;
    }

    public void setAuthorRoomId(long authorRoomId) {
        this.authorRoomId = authorRoomId;
    }

    public String getAuthorHash() {
        return authorHash;
    }

    public void setAuthorHash(String authorHash) {
        this.authorHash = authorHash;
    }

    public long getPreviousMessageId() {
        return previousMessageId;
    }

    public void setPreviousMessageId(long previousMessageId) {
        this.previousMessageId = previousMessageId;
    }

    public long getFutureMessageId() {
        return futureMessageId;
    }

    public void setFutureMessageId(long futureMessageId) {
        this.futureMessageId = futureMessageId;
    }

    public boolean isSenderMe() {
        return getUserId() == AccountManager.getInstance().getCurrentUser().getId();
    }

    public boolean isAuthorMe() {

        boolean output = false;
        if (getAuthorHash() != null) {
            output = getAuthorHash().equals(RealmUserInfo.getCurrentUserAuthorHash());
        }

        return output;
    }

    public String getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    public boolean isOnlyTime() {
        return userId == -1;
    }

    public RealmAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(RealmAttachment attachment) {
        this.attachment = attachment;
    }

    public void setAttachment(final long messageId, final String path, int width, int height, long size, String name, double duration, LocalFileType type) {
        if (path == null) {
            return;
        }
        DbManager.getInstance().doRealmTask(realm -> {
            if (attachment == null) {
                RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo("id", messageId).findFirst();
                if (realmAttachment == null) {
                    realmAttachment = realm.createObject(RealmAttachment.class, messageId);
                }
                if (type == LocalFileType.THUMBNAIL) {
                    realmAttachment.setLocalThumbnailPath(path);
                } else {
                    realmAttachment.setLocalFilePath(path);
                }
                realmAttachment.setWidth(width);
                realmAttachment.setSize(size);
                realmAttachment.setHeight(height);
                realmAttachment.setName(name);
                realmAttachment.setDuration(duration);
                attachment = realmAttachment;
            } else {
                if (attachment.isValid()) {
                    if (type == LocalFileType.THUMBNAIL) {
                        attachment.setLocalThumbnailPath(path);
                    } else {
                        attachment.setLocalFilePath(path);
                    }
                }
            }
        });

    }
}
