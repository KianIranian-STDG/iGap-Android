package net.iGap.controllers;

import net.iGap.helper.DispatchQueue;
import net.iGap.helper.FileLog;
import net.iGap.module.accountManager.AccountManager;
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
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MessageDataStorage extends BaseController {

    private static volatile MessageDataStorage[] instance = new MessageDataStorage[AccountManager.MAX_ACCOUNT_COUNT];
    private DispatchQueue storageQueue = new DispatchQueue("MessageStorage");
    private Realm dataBase;

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
                RealmConfiguration config = AccountManager.getInstance().getCurrentUser().getRealmConfiguration();
                dataBase = Realm.getInstance(config);
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
}
