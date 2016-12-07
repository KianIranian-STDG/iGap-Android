package com.iGap.response;

import android.os.Handler;
import android.os.Looper;

import com.iGap.G;
import com.iGap.helper.HelperSetAction;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

public class FileUploadStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUploadStatus.FileUploadStatusResponse.Builder builder = (ProtoFileUploadStatus.FileUploadStatusResponse.Builder) message;
        G.onFileUploadStatusResponse.onFileUploadStatus(builder.getStatus(), builder.getProgress(), builder.getRecheckDelayMs(), this.identity, builder.getResponse());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        HelperSetAction.sendCancel(Long.parseLong(this.identity));
        makeFailed();
    }

    /**
     * make messages failed
     */
    private void makeFailed() {
        // message failed
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                    }
                });
            }
        });
    }
}


