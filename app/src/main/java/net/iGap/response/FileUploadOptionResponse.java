/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.response;

import android.os.Handler;
import android.os.Looper;

import net.iGap.G;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperUploadFile;
import net.iGap.proto.ProtoFileUploadOption;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

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
        ProtoFileUploadOption.FileUploadOptionResponse.Builder fp = (ProtoFileUploadOption.FileUploadOptionResponse.Builder) message;

        HelperUploadFile.onFileUpload.OnFileUploadOption(fp.getFirstBytesLimit(), fp.getLastBytesLimit(), fp.getMaxConnection(), this.identity, fp.getResponse());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        HelperUploadFile.onFileUpload.onFileUploadTimeOut(this.identity);
        HelperSetAction.sendCancel(Long.parseLong(this.identity));
        makeFailed();
    }

    /**
     * make messages failed
     */
    private void makeFailed() {
        // message failed
        long roomId = -1L;
        try (Realm realm = Realm.getDefaultInstance()) {
            final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (message != null) {
                        message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                    }
                }
            });
            if (message != null) {
                roomId = message.getRoomId();
            }
        }

        if (roomId != -1L) {
            long finalRoomId = roomId;
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    G.refreshRealmUi();
                    G.chatSendMessageUtil.onMessageFailed(finalRoomId, Long.parseLong(identity));
                }
            });
        }
    }
}


