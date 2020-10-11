package net.iGap.controllers;

import net.iGap.helper.DispatchQueue;
import net.iGap.helper.FileLog;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmOfflineDelete;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MessageDataStorage extends BaseController {

    private static volatile MessageDataStorage[] instance = new MessageDataStorage[AccountManager.MAX_ACCOUNT_COUNT];
    private DispatchQueue storageQueue = new DispatchQueue("MessageStorage");
    private Realm dataBase;
    private String TAG = getClass().getSimpleName();

    public MessageDataStorage(int currentAccount) {
        super(currentAccount);
        openDataBase();
    }

    public static MessageDataStorage getInstance(int account) {
        MessageDataStorage localInstance = instance[account];
        if (localInstance == null) {
            synchronized (MessageDataStorage.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new MessageDataStorage(account);
                }
            }
        }
        return localInstance;
    }

    private void openDataBase() {
        storageQueue.postRunnable(() -> {
            if (dataBase == null || dataBase.isClosed()) {
                dataBase = DbManager.getInstance().getRealm();
            }
        });
    }

    public void processDeleteMessage(long roomId, long messageId, long deleteVersion, boolean update) {
        storageQueue.postRunnable(() -> {
            try {
                dataBase.beginTransaction();

                RealmClientCondition realmClientCondition = dataBase.where(RealmClientCondition.class).equalTo("roomId", roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteVersion = deleteVersion;
                }

                RealmOfflineDelete offlineDelete = dataBase.where(RealmOfflineDelete.class).equalTo("offlineDelete", messageId).findFirst();
                if (offlineDelete != null) {
                    offlineDelete.deleteFromRealm();
                }

                RealmRoomMessage message = dataBase.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
                RealmRoom realmRoom = dataBase.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                if (realmRoom != null && message != null && !message.deleted
                        && !message.isSenderMe()
                        && !message.status.equals(ProtoGlobal.RoomMessageStatus.SEEN.toString())
                        && realmRoom.firstUnreadMessage != null
                        && realmRoom.firstUnreadMessage.messageId <= messageId
                        && realmRoom.unreadCount > 0) {

                    realmRoom.unreadCount = realmRoom.unreadCount - 1;

                    if (realmRoom.getUnreadCount() == 0) {
                        realmRoom.firstUnreadMessage = null;
                    }
                }

                if (update && message != null) {
                    message.deleted = true;
                }

                RealmResults<RealmRoomMessage> replayedMessages = dataBase.where(RealmRoomMessage.class).equalTo("replyTo.messageId", -messageId).findAll();

                if (replayedMessages != null) {
                    for (RealmRoomMessage roomMessage : replayedMessages) {
                        if (roomMessage != null) {
                            roomMessage.replyTo.deleted = true;
                        }
                    }
                }

                getEventManager().postEvent(EventManager.ON_MESSAGE_DELETE, roomId, messageId, update);

                if (realmRoom != null && realmRoom.lastMessage != null) {
                    if (realmRoom.lastMessage.messageId == messageId) {

                        Number newLastMessageId = dataBase.where(RealmRoomMessage.class)
                                .equalTo("roomId", roomId)
                                .notEqualTo("messageId", messageId)
                                .notEqualTo("deleted", true)
                                .lessThan("messageId", messageId)
                                .max("messageId");

                        if (newLastMessageId != null) {
                            realmRoom.lastMessage = dataBase.where(RealmRoomMessage.class)
                                    .equalTo("messageId", newLastMessageId.longValue())
                                    .findFirst();
                        } else {
                            realmRoom.lastMessage = null;
                        }
                    }
                }
                dataBase.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public void putUserAvatar(long roomId, ProtoGlobal.Avatar avatar) {
        storageQueue.postRunnable(() -> {
            try {
                dataBase.beginTransaction();
                RealmAvatar realmAvatar = dataBase.where(RealmAvatar.class).equalTo("id", avatar.getId()).findFirst();

                if (realmAvatar == null) {
                    realmAvatar = dataBase.createObject(RealmAvatar.class, avatar.getId());
                }

                realmAvatar.setOwnerId(roomId);
                realmAvatar.setFile(RealmAttachment.build(dataBase, avatar.getFile(), AttachmentFor.AVATAR, null));

                dataBase.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    private void cleanUpInternal() {
        try {
            dataBase.close();
            dataBase = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void cleanUp() {
        storageQueue.postRunnable(() -> {
            cleanUpInternal();
            openDataBase();
        });
    }

    public void deleteRoomFromStorage(long roomId) {
        storageQueue.postRunnable(() -> {
            try {
                dataBase.beginTransaction();

                RealmRoom realmRoom = dataBase.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }

                dataBase.where(RealmClientCondition.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();
                dataBase.where(RealmRoomMessage.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();

                dataBase.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public void setAttachmentFilePath(String cacheId, String absolutePath, boolean isThumb) {
        storageQueue.postRunnable(() -> {
            try {
                dataBase.beginTransaction();
                RealmResults<RealmAttachment> attachments = dataBase.where(RealmAttachment.class).equalTo("cacheId", cacheId).findAll();

                for (RealmAttachment attachment : attachments) {
                    if (isThumb) {
                        attachment.setLocalThumbnailPath(absolutePath);
                    } else {
                        attachment.setLocalFilePath(absolutePath);
                    }
                }

                dataBase.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    private void putLastMessageInternal(final long roomId, RealmRoomMessage lastMessage) {
        try {
            dataBase.beginTransaction();
            RealmRoom room = dataBase.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (room != null) {
                room.updatedTime = Math.max(lastMessage.updateTime, lastMessage.createTime);
                room.lastMessage = lastMessage;
            }
            dataBase.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resetRoomLastMessage(final long roomId, final long messageId) {
        storageQueue.postRunnable(() -> {
            try {
                if (messageId != 0) {
                    deleteMessage(roomId, messageId, false);
                }

                RealmRoomMessage newMessage = dataBase.where(RealmRoomMessage.class).equalTo("roomId", roomId)
                        .notEqualTo("deleted", true)
                        .sort(new String[]{"messageId"}, new Sort[]{Sort.DESCENDING})
                        .findFirst();

                if (newMessage != null) {
                    putLastMessageInternal(roomId, newMessage);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public void deleteMessage(final long roomId, final long messageId, final boolean useQueue) {
        if (useQueue) {
            storageQueue.postRunnable(() -> deleteMessageInternal(roomId, messageId));
        } else {
            deleteMessageInternal(roomId, messageId);
        }
    }

    private void deleteMessageInternal(long roomId, long messageId) {
        try {
            if (roomId == 0 || messageId == 0) {
                return;
            }

            dataBase.beginTransaction();

            RealmRoomMessage message = dataBase.where(RealmRoomMessage.class).equalTo("messageId", messageId).equalTo("roomId", roomId).findFirst();
            if (message != null) {
                message.deleteFromRealm();
            }

            dataBase.commitTransaction();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void putAttachmentToken(final long messageId, final String token) {
        storageQueue.postRunnable(() -> {
            try {
                dataBase.beginTransaction();
                RealmAttachment attachment = dataBase.where(RealmAttachment.class).equalTo("id", messageId).findFirst();

                if (attachment != null) {
                    attachment.token = token;
                }

                dataBase.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }
}
