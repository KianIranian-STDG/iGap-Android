package net.iGap.controllers;

import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.helper.upload.UploadTask;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestClientGetRoom;

import java.io.File;

public class MessageController extends BaseController implements EventListener {

    private String lastUploadedAvatarId;
    private long lastUploadedAvatarRoomId;

    private static volatile MessageController[] instance = new MessageController[AccountManager.MAX_ACCOUNT_COUNT];
    private String TAG = getClass().getSimpleName();

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

        FileLog.e("MessageController onUpdate " + object);

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

    public void cancelUploadFile(long roomId, RealmRoomMessage message) {
        if (message == null || roomId == 0) {
            return;
        }

        getMessageDataStorage().resetRoomLastMessage(roomId, message.messageId);
    }
}
