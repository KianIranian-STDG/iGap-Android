package net.iGap.controllers;

import net.iGap.G;
import net.iGap.helper.upload.UploadTask;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.AbstractObject;
import net.iGap.request.IG_Objects;

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

        if (object instanceof IG_Objects.ChannelAvatar) {
            IG_Objects.ChannelAvatar channelAvatar = (IG_Objects.ChannelAvatar) object;
            updateChannelAvatarInternal(channelAvatar);
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
                IG_Objects.ChannelAddAvatar req = new IG_Objects.ChannelAddAvatar();
                req.attachment = fileToken;
                req.roomId = lastUploadedAvatarRoomId;

                getRequestManager().sendRequest(req, (response, error) -> {
                    if (error == null) {
                        IG_Objects.ChannelAvatar channelAvatar = (IG_Objects.ChannelAvatar) response;
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

    private void updateChannelAvatarInternal(IG_Objects.ChannelAvatar avatar) {
        getMessageDataStorage().putUserAvatar(avatar.roomId, avatar.avatar);
        G.runOnUiThread(() -> getEventManager().postEvent(EventManager.AVATAR_UPDATE, avatar.roomId));
    }
}
