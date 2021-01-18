package net.iGap.controllers;

import android.text.format.DateUtils;

import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperTimeOut;
import net.iGap.helper.upload.UploadTask;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.ArrayList;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT_VALUE;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP_VALUE;

public class MessageController extends BaseController implements EventListener {

    private String lastUploadedAvatarId;
    private long lastUploadedAvatarRoomId;

    private static volatile MessageController[] instance = new MessageController[AccountManager.MAX_ACCOUNT_COUNT];
    private String TAG = getClass().getSimpleName() + " " + currentAccount + " ";

    public static MessageController getInstance(int account) {
        MessageController localInstance = instance[account];
        if (localInstance == null) {
            synchronized (MessageController.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new MessageController(account);
                }
            }
        }
        return localInstance;
    }

    public MessageController(int currentAccount) {
        super(currentAccount);

        G.runOnUiThread(() -> {
            EventManager.getInstance().addEventListener(EventManager.FILE_UPLOAD_FAILED, this);
            EventManager.getInstance().addEventListener(EventManager.FILE_UPLOAD_SUCCESS, this);
        });
    }


    public void onUpdate(AbstractObject object) {
        if (object == null) {
            return;
        }

        FileLog.i(TAG, "onUpdate " + object);

        if (object instanceof IG_RPC.Res_Channel_Avatar) {
            IG_RPC.Res_Channel_Avatar channelAvatar = (IG_RPC.Res_Channel_Avatar) object;
            updateChannelAvatarInternal(channelAvatar);
        } else if (object instanceof IG_RPC.Res_Group_Create) {
            IG_RPC.Res_Group_Create res = (IG_RPC.Res_Group_Create) object;
            new RequestClientGetRoom().clientGetRoom(res.roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
        } else if (object instanceof IG_RPC.Res_Channel_Create) {
            IG_RPC.Res_Channel_Create res = (IG_RPC.Res_Channel_Create) object;
            new RequestClientGetRoom().clientGetRoom(res.roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
        } else if (object instanceof IG_RPC.Res_Channel_Delete) {
            IG_RPC.Res_Channel_Delete res = (IG_RPC.Res_Channel_Delete) object;
            getMessageDataStorage().deleteRoomFromStorage(res.roomId);
        } else if (object instanceof IG_RPC.Res_Group_Edit_Message || object instanceof IG_RPC.Res_Chat_Edit_Message || object instanceof IG_RPC.Res_Channel_Edit_Message) {
            onMessageEditResponse(object, true);
        } else if (object instanceof IG_RPC.Group_pin_message_response || object instanceof IG_RPC.Channel_pin_message_response) {
            onPinMessageResponse(object);
        } else if (object instanceof IG_RPC.Res_Chat_Delete_Message) {
            IG_RPC.Res_Chat_Delete_Message res = (IG_RPC.Res_Chat_Delete_Message) object;
            onDeleteMessageResponse(res, true);
        } else if (object instanceof IG_RPC.Res_Group_Delete_Message) {
            IG_RPC.Res_Group_Delete_Message res = (IG_RPC.Res_Group_Delete_Message) object;
            onDeleteMessageResponse(res, true);
        } else if (object instanceof IG_RPC.Res_Channel_Delete_Message) {
            IG_RPC.Res_Channel_Delete_Message res = (IG_RPC.Res_Channel_Delete_Message) object;
            onDeleteMessageResponse(res, true);
        } else if (object instanceof IG_RPC.Res_Chat_Update_Status || object instanceof IG_RPC.Res_Group_Update_Status) {
            onMessageStatusResponse(object, true);
        }
    }

    public static boolean isBothDelete(long messageTime) {
        long currentTime;
        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            currentTime = G.currentServerTime * DateUtils.SECOND_IN_MILLIS;
        } else {
            currentTime = System.currentTimeMillis();
        }

        return !HelperTimeOut.timeoutChecking(currentTime, messageTime, G.bothChatDeleteTime);
    }

    public void sendUpdateStatus(int roomType, long roomId, long messageId, int roomMessageStatus) {
        AbstractObject req = null;
        if (roomType == CHAT_VALUE) {
            IG_RPC.Chat_Update_Status chatUpdateStatusReq = new IG_RPC.Chat_Update_Status();
            chatUpdateStatusReq.roomId = roomId;
            chatUpdateStatusReq.messageId = messageId;
            chatUpdateStatusReq.roomMessageStatus = ProtoGlobal.RoomMessageStatus.forNumber(roomMessageStatus);
            req = chatUpdateStatusReq;
        } else if (roomType == GROUP_VALUE) {
            IG_RPC.Group_Update_Status groupUpdateStatusReq = new IG_RPC.Group_Update_Status();
            groupUpdateStatusReq.roomId = roomId;
            groupUpdateStatusReq.messageId = messageId;
            groupUpdateStatusReq.roomMessageStatus = ProtoGlobal.RoomMessageStatus.forNumber(roomMessageStatus);
            req = groupUpdateStatusReq;
        }

        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                onMessageStatusResponse(response, false);
            }
        });
    }

    private void onMessageStatusResponse(AbstractObject response, boolean update) {
        if (response instanceof IG_RPC.Res_Chat_Update_Status) {
            IG_RPC.Res_Chat_Update_Status res = (IG_RPC.Res_Chat_Update_Status) response;
            getMessageDataStorage().updateMessageStatus(res.roomId, res.messageId, res.updaterAuthorHash, res.statusValue, res.statusVersion, update);
            if (res.statusValue == ProtoGlobal.RoomMessageStatus.SEEN) {
                HelperNotification.getInstance().cancelNotification(res.roomId);
            }
        } else if (response instanceof IG_RPC.Res_Group_Update_Status) {
            IG_RPC.Res_Group_Update_Status res = (IG_RPC.Res_Group_Update_Status) response;
            getMessageDataStorage().updateMessageStatus(res.roomId, res.messageId, res.updaterAuthorHash, res.statusValue, res.statusVersion, update);
        }
    }

    public void clearHistoryMessage(final long roomId) {
        if (roomId > 0) {
            RealmRoom realmRoom = getMessageDataStorage().getRoom(roomId);
            long clearMessageId;

            if (realmRoom == null || !realmRoom.isLoaded() || !realmRoom.isValid()) {
                return;
            }

            if (realmRoom.getLastMessage() != null) {
                clearMessageId = realmRoom.getLastMessage().getMessageId();
            } else {
                clearMessageId = getMessageDataStorage().getRoomClearId(roomId);
            }

            if (clearMessageId == 0) {
                return;
            }

            getMessageDataStorage().setRoomClearId(roomId, clearMessageId, true);

            AbstractObject req = null;

            if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT) {
                IG_RPC.Chat_Clear_History historyReq = new IG_RPC.Chat_Clear_History();
                historyReq.roomId = roomId;
                historyReq.lastMessageId = clearMessageId;
                req = historyReq;
            } else if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                IG_RPC.Group_Clear_History historyReq = new IG_RPC.Group_Clear_History();
                historyReq.roomId = roomId;
                historyReq.lastMessageId = clearMessageId;
                req = historyReq;
            }

            getRequestManager().sendRequest(req, (response, error) -> {
                if (error == null) {
                    long clearId = 0;
                    if (response instanceof IG_RPC.Res_Chat_Clear_History) {
                        IG_RPC.Res_Chat_Clear_History resClearMessage = (IG_RPC.Res_Chat_Clear_History) response;
                        clearId = resClearMessage.clearId;
                    } else if (response instanceof IG_RPC.Res_Group_Clear_History) {
                        IG_RPC.Res_Group_Clear_History resClearMessage = (IG_RPC.Res_Group_Clear_History) response;
                        clearId = resClearMessage.clearId;
                    }

                    getMessageDataStorage().clearRoomHistory(roomId, clearId);
                }
            });
        }
    }

    public String saveChannelAvatar(String path, long roomId) {
        lastUploadedAvatarId = String.valueOf(SUID.id().get());
        lastUploadedAvatarRoomId = roomId;

        getUploadManager().upload(new UploadTask(lastUploadedAvatarId, new File(path), ProtoGlobal.RoomMessageType.IMAGE));

        return lastUploadedAvatarId;
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.FILE_UPLOAD_SUCCESS) {
            String fileId = (String) message[0];
            String fileToken = (String) message[1];

            if (lastUploadedAvatarId != null && lastUploadedAvatarId.equals(fileId)) {
                IG_RPC.Channel_AddAvatar req = new IG_RPC.Channel_AddAvatar();
                req.attachment = fileToken;
                req.roomId = lastUploadedAvatarRoomId;

                getRequestManager().sendRequest(req, (response, error) -> {
                    if (error == null) {
                        IG_RPC.Res_Channel_Avatar channelAvatar = (IG_RPC.Res_Channel_Avatar) response;
                        updateChannelAvatarInternal(channelAvatar);
                    }
                });
            }
        } else if (id == EventManager.FILE_UPLOAD_FAILED) {
            String fileId = (String) message[0];

            if (lastUploadedAvatarId != null && lastUploadedAvatarId.equals(fileId)) {
                lastUploadedAvatarId = null;
                lastUploadedAvatarRoomId = -1;
            }
        }
    }

    private void updateChannelAvatarInternal(IG_RPC.Res_Channel_Avatar avatar) {
        getMessageDataStorage().putUserAvatar(avatar.roomId, avatar.avatar);
        G.runOnUiThread(() -> getEventManager().postEvent(EventManager.AVATAR_UPDATE, avatar.roomId));
    }

    public void deleteChannel(long roomId) {
        IG_RPC.Channel_Delete req = new IG_RPC.Channel_Delete();
        req.roomId = roomId;

        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Channel_Delete res = (IG_RPC.Res_Channel_Delete) response;
                getMessageDataStorage().deleteRoomFromStorage(res.roomId);
            }
        });
    }

    public void cancelUploadFile(long roomId, MessageObject message) {
        if (message == null || roomId == 0) {
            return;
        }

        getMessageDataStorage().resetRoomLastMessage(roomId, message.id);
    }

    public void editMessage(long messageId, long roomId, String newMessage, int chatType) {
        AbstractObject req = null;

        if (chatType == CHAT_VALUE) {
            IG_RPC.Chat_edit_message req_chat_edit = new IG_RPC.Chat_edit_message();
            req_chat_edit.message = newMessage;
            req_chat_edit.messageId = messageId;
            req_chat_edit.roomId = roomId;
            req = req_chat_edit;
        } else if (chatType == GROUP_VALUE) {
            IG_RPC.Group_edit_message req_group_edit = new IG_RPC.Group_edit_message();
            req_group_edit.message = newMessage;
            req_group_edit.messageId = messageId;
            req_group_edit.roomId = roomId;
            req = req_group_edit;
        } else if (chatType == ProtoGlobal.Room.Type.CHANNEL_VALUE) {
            IG_RPC.Channel_edit_message req_channel_edit = new IG_RPC.Channel_edit_message();
            req_channel_edit.message = newMessage;
            req_channel_edit.messageId = messageId;
            req_channel_edit.roomId = roomId;
            req = req_channel_edit;
        }

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                onMessageEditResponse(response, false);
            } else {
                IG_RPC.Error err = (IG_RPC.Error) error;
                FileLog.e("Edit message -> Major: " + err.minor + " Minor: " + err.minor);
            }
        });

    }

    private void onMessageEditResponse(AbstractObject response, boolean isUpdate) {
        if (response == null) {
            return;
        }

        long roomId = 0;
        long messageId = 0;
        int messageType = 0;
        long messageVersion = 0;
        String newMessage = null;

        if (response instanceof IG_RPC.Res_Chat_Edit_Message) {
            IG_RPC.Res_Chat_Edit_Message res = (IG_RPC.Res_Chat_Edit_Message) response;

            newMessage = res.newMessage;
            roomId = res.roomId;
            messageId = res.messageId;
            messageVersion = res.messageVersion;
            messageType = res.messageType;
        } else if (response instanceof IG_RPC.Res_Group_Edit_Message) {
            IG_RPC.Res_Group_Edit_Message res = (IG_RPC.Res_Group_Edit_Message) response;

            newMessage = res.newMessage;
            roomId = res.roomId;
            messageId = res.messageId;
            messageVersion = res.messageVersion;
            messageType = res.messageType;
        } else {

            IG_RPC.Res_Channel_Edit_Message res = (IG_RPC.Res_Channel_Edit_Message) response;

            newMessage = res.newMessage;
            roomId = res.roomId;
            messageId = res.messageId;
            messageVersion = res.messageVersion;
            messageType = res.messageType;
        }

        getMessageDataStorage().updateEditedMessage(roomId, messageId, messageVersion, messageType, newMessage, isUpdate);
        getEventManager().postEvent(EventManager.ON_EDIT_MESSAGE, roomId, messageId, newMessage);

    }

    public void pinMessage(long roomId, long messageId, int chatType) {
        AbstractObject req = null;

        if (chatType == GROUP_VALUE) {
            IG_RPC.Group_pin_message group_pin_message = new IG_RPC.Group_pin_message();
            group_pin_message.roomId = roomId;
            group_pin_message.messageId = messageId;
            req = group_pin_message;
        } else if (chatType == ProtoGlobal.Room.Type.CHANNEL_VALUE) {
            IG_RPC.Channel_pin_message channel_pin_message = new IG_RPC.Channel_pin_message();
            channel_pin_message.messageId = messageId;
            channel_pin_message.roomId = roomId;
            req = channel_pin_message;
        }

        getRequestManager().sendRequest(req, (response, error) -> {
            if (response != null) {
                onPinMessageResponse(response);
            } else {
                IG_RPC.Error err = (IG_RPC.Error) error;
                FileLog.e("Pin message -> Major: " + err.minor + " Minor: " + err.minor);
            }
        });
    }

    private void onPinMessageResponse(AbstractObject response) {
        if (response == null) {
            return;
        }
        long roomId = 0;
        long messageId = 0;

        if (response instanceof IG_RPC.Group_pin_message_response) {
            IG_RPC.Group_pin_message_response res = (IG_RPC.Group_pin_message_response) response;
            roomId = res.roomId;
            messageId = res.pinnedMessage.getMessageId();
        } else if (response instanceof IG_RPC.Channel_pin_message_response) {
            IG_RPC.Channel_pin_message_response res = (IG_RPC.Channel_pin_message_response) response;
            roomId = res.roomId;
            messageId = res.pinnedMessage.getMessageId();
        }

        getMessageDataStorage().updatePinnedMessage(roomId, messageId);
    }

    public void deleteSelectedMessage(int roomType, long roomId, ArrayList<Long> messageIds, ArrayList<Long> bothMessageIds) {
        AbstractObject req = null;
        boolean bothDelete = false;

        for (long messageId : messageIds) {

            if (bothMessageIds != null && bothMessageIds.contains(messageId)) {
                bothDelete = true;
            }

            if (roomType == CHAT_VALUE) {
                IG_RPC.Chat_Delete_Message chat_delete_message = new IG_RPC.Chat_Delete_Message();
                chat_delete_message.roomId = roomId;
                chat_delete_message.messageId = messageId;
                chat_delete_message.both = bothDelete;
                req = chat_delete_message;

            } else if (roomType == GROUP_VALUE) {
                IG_RPC.Group_Delete_Message group_delete_message = new IG_RPC.Group_Delete_Message();
                group_delete_message.roomId = roomId;
                group_delete_message.messageId = messageId;
                req = group_delete_message;

            } else if (roomType == ProtoGlobal.Room.Type.CHANNEL_VALUE) {
                IG_RPC.Channel_Delete_Message channel_delete_message = new IG_RPC.Channel_Delete_Message();
                channel_delete_message.roomId = roomId;
                channel_delete_message.messageId = messageId;
                req = channel_delete_message;

            }

            getRequestManager().sendRequest(req, (response, error) -> {
                if (response != null) {
                    onDeleteMessageResponse(response, false);
                } else {
                    IG_RPC.Error e = new IG_RPC.Error();
                    FileLog.e("Delete Message -> Major:" + e.major + "Minor:" + e.minor);
                }
            });
        }
    }

    public void onDeleteMessageResponse(AbstractObject response, boolean update) {

        long roomId = 0;
        long messageId = 0;
        long deleteVersion = 0;

        if (response instanceof IG_RPC.Res_Chat_Delete_Message) {
            IG_RPC.Res_Chat_Delete_Message res = (IG_RPC.Res_Chat_Delete_Message) response;
            roomId = res.roomId;
            messageId = res.messageId;
            deleteVersion = res.deleteVersion;

        } else if (response instanceof IG_RPC.Res_Group_Delete_Message) {
            IG_RPC.Res_Group_Delete_Message res = (IG_RPC.Res_Group_Delete_Message) response;
            roomId = res.roomId;
            messageId = res.messageId;
            deleteVersion = res.deleteVersion;

        } else if (response instanceof IG_RPC.Res_Channel_Delete_Message) {
            IG_RPC.Res_Channel_Delete_Message res = (IG_RPC.Res_Channel_Delete_Message) response;
            roomId = res.roomId;
            messageId = res.messageId;
            deleteVersion = res.deleteVersion;
        }

        getMessageDataStorage().processDeleteMessage(roomId, messageId, deleteVersion, update);
    }

}
