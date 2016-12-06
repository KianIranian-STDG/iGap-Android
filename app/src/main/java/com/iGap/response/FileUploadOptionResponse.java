package com.iGap.response;

import android.os.Handler;
import android.os.Looper;

import com.iGap.G;
import com.iGap.helper.HelperSetAction;
import com.iGap.proto.ProtoFileUploadOption;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

public class FileUploadOptionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadOptionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUploadOption.FileUploadOptionResponse.Builder fileUploadOptionResponse =
                (ProtoFileUploadOption.FileUploadOptionResponse.Builder) message;

        G.uploaderUtil.OnFileUploadOption(fileUploadOptionResponse.getFirstBytesLimit(),
                fileUploadOptionResponse.getLastBytesLimit(),
                fileUploadOptionResponse.getMaxConnection(), this.identity,
                fileUploadOptionResponse.getResponse());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
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
                            HelperSetAction.sendCancel(Long.parseLong(FileUploadOptionResponse.this.identity));
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                            G.uploaderUtil.onFileUploadTimeOut(message, message.getRoomId());
                        }
                    }
                });
            }
        });
    }
}


