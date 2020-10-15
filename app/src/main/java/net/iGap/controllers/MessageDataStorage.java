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

import io.realm.RealmResults;
import io.realm.Sort;

public class MessageDataStorage extends BaseController {

    private static volatile MessageDataStorage[] instance = new MessageDataStorage[AccountManager.MAX_ACCOUNT_COUNT];
    private DispatchQueue storageQueue = new DispatchQueue("MessageStorage");
    //    private Realm database;
    private String TAG = getClass().getSimpleName();

    public MessageDataStorage(int currentAccount) {
        super(currentAccount);
        openDatabase();
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

    private void openDatabase() {
//        storageQueue.postRunnable(() -> {
//            if (database == null || database.isClosed()) {
//                database = DbManager.getInstance().getRealm();
//            }
//        });
    }

    public void processDeleteMessage(long roomId, long messageId, long deleteVersion, boolean update) {
        storageQueue.postRunnable(() -> DbManager.getInstance().doRealmTask(database -> {
            try {
                database.beginTransaction();

                RealmClientCondition realmClientCondition = database.where(RealmClientCondition.class).equalTo("roomId", roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteVersion = deleteVersion;
                }

                RealmOfflineDelete offlineDelete = database.where(RealmOfflineDelete.class).equalTo("offlineDelete", messageId).findFirst();
                if (offlineDelete != null) {
                    offlineDelete.deleteFromRealm();
                }

                RealmRoomMessage message = database.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst();
                RealmRoom realmRoom = database.where(RealmRoom.class).equalTo("id", roomId).findFirst();

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

                RealmResults<RealmRoomMessage> replayedMessages = database.where(RealmRoomMessage.class).equalTo("replyTo.messageId", -messageId).findAll();

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

                        Number newLastMessageId = database.where(RealmRoomMessage.class)
                                .equalTo("roomId", roomId)
                                .notEqualTo("messageId", messageId)
                                .notEqualTo("deleted", true)
                                .lessThan("messageId", messageId)
                                .max("messageId");

                        if (newLastMessageId != null) {
                            realmRoom.lastMessage = database.where(RealmRoomMessage.class)
                                    .equalTo("messageId", newLastMessageId.longValue())
                                    .findFirst();
                        } else {
                            realmRoom.lastMessage = null;
                        }
                    }
                }
                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }));
    }

    public void putUserAvatar(long roomId, ProtoGlobal.Avatar avatar) {
        storageQueue.postRunnable(() -> DbManager.getInstance().doRealmTask(database -> {
            try {
                database.beginTransaction();
                RealmAvatar realmAvatar = database.where(RealmAvatar.class).equalTo("id", avatar.getId()).findFirst();

                if (realmAvatar == null) {
                    realmAvatar = database.createObject(RealmAvatar.class, avatar.getId());
                }

                realmAvatar.setOwnerId(roomId);
                realmAvatar.setFile(RealmAttachment.build(database, avatar.getFile(), AttachmentFor.AVATAR, null));

                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }));
    }

    private void cleanUpInternal() {
//        try {
//            database.close();
//            database = null;
//        } catch (Exception e) {
//            FileLog.e(e);
//        }
    }

    public void cleanUp() {
//        storageQueue.postRunnable(() -> {
//            cleanUpInternal();
//            openDatabase();
//        });
        storageQueue.cleanupQueue();
    }

    public void deleteRoomFromStorage(long roomId) {
        storageQueue.postRunnable(() -> DbManager.getInstance().doRealmTask(database -> {
            try {
                database.beginTransaction();

                RealmRoom realmRoom = database.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }

                database.where(RealmClientCondition.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();
                database.where(RealmRoomMessage.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();

                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }));
    }

    public void setAttachmentFilePath(String cacheId, String absolutePath, boolean isThumb) {
        storageQueue.postRunnable(() -> DbManager.getInstance().doRealmTask(database -> {
            try {
                database.beginTransaction();
                RealmResults<RealmAttachment> attachments = database.where(RealmAttachment.class).equalTo("cacheId", cacheId).findAll();

                for (RealmAttachment attachment : attachments) {
                    if (isThumb) {
                        attachment.setLocalThumbnailPath(absolutePath);
                    } else {
                        attachment.setLocalFilePath(absolutePath);
                    }
                }

                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }));
    }

    private void putLastMessageInternal(final long roomId, RealmRoomMessage lastMessage) {
        DbManager.getInstance().doRealmTask(database -> {
            try {
                database.beginTransaction();
                RealmRoom room = database.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (room != null) {
                    room.updatedTime = Math.max(lastMessage.updateTime, lastMessage.createTime);
                    room.lastMessage = lastMessage;
                }
                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public void resetRoomLastMessage(final long roomId, final long messageId) {
        storageQueue.postRunnable(() -> DbManager.getInstance().doRealmTask(database -> {
            try {
                if (messageId != 0) {
                    deleteMessage(roomId, messageId, false);
                }

                RealmRoomMessage newMessage = database.where(RealmRoomMessage.class).equalTo("roomId", roomId)
                        .notEqualTo("deleted", true)
                        .sort(new String[]{"messageId"}, new Sort[]{Sort.DESCENDING})
                        .findFirst();

                if (newMessage != null) {
                    putLastMessageInternal(roomId, newMessage);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }));
    }

    public void deleteMessage(final long roomId, final long messageId, final boolean useQueue) {
        if (useQueue) {
            storageQueue.postRunnable(() -> deleteMessageInternal(roomId, messageId));
        } else {
            deleteMessageInternal(roomId, messageId);
        }
    }

    private void deleteMessageInternal(long roomId, long messageId) {
        DbManager.getInstance().doRealmTask(database -> {
            try {
                if (roomId == 0 || messageId == 0) {
                    return;
                }

                database.beginTransaction();

                RealmRoomMessage message = database.where(RealmRoomMessage.class).equalTo("messageId", messageId).equalTo("roomId", roomId).findFirst();
                if (message != null) {
                    message.deleteFromRealm();
                }

                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        });

    }

    public void putAttachmentToken(final long messageId, final String token) {
        storageQueue.postRunnable(() -> DbManager.getInstance().doRealmTask(database -> {
            try {
                database.beginTransaction();
                RealmAttachment attachment = database.where(RealmAttachment.class).equalTo("id", messageId).findFirst();

                if (attachment != null) {
                    attachment.token = token;
                }

                database.commitTransaction();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }));
    }
}
