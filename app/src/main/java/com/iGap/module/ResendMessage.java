package com.iGap.module;

import android.content.Context;

import com.iGap.G;
import com.iGap.interfaces.IResendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/30/2016.
 */

public class ResendMessage implements IResendMessage {
    private List<StructMessageInfo> mMessages;
    private IResendMessage mListener;
    private long mSelectedMessageID;

    public ResendMessage(Context context, IResendMessage listener, long selectedMessageID,
                         List<StructMessageInfo> messages) {
        this.mMessages = messages;
        this.mListener = listener;
        this.mSelectedMessageID = selectedMessageID;
        AppUtils.buildResendDialog(context, messages.size(), this).show();
    }

    public List<StructMessageInfo> getMessages() {
        return mMessages;
    }

    @Override
    public void deleteMessage() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (StructMessageInfo message : mMessages) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                    Long.parseLong(message.messageID))
                            .findFirst();
                    if (roomMessage != null) {
                        roomMessage.deleteFromRealm();
                    }
                }
            }
        });
        realm.close();

        mListener.deleteMessage();
    }

    private void resend(final boolean all) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (StructMessageInfo message : mMessages) {
                    if (all) {
                        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                        Long.parseLong(message.messageID))
                                .findFirst();
                        if (roomMessage != null) {
                            roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                        }
                    } else {
                        if (message.messageID.equalsIgnoreCase(Long.toString(mSelectedMessageID))) {
                            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                                    .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                            Long.parseLong(message.messageID))
                                    .findFirst();
                            if (roomMessage != null) {
                                roomMessage.setStatus(
                                        ProtoGlobal.RoomMessageStatus.SENDING.toString());
                            }
                            break;
                        }
                    }
                }
            }
        });

        if (all) {
            mListener.resendAllMessages();
        } else {
            mListener.resendMessage();
        }

        for (StructMessageInfo message : mMessages) {
            if (all) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID))
                        .findFirst();
                if (roomMessage != null) {
                    RoomType roomType = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, roomMessage.getRoomId())
                            .findFirst()
                            .getType();
                    G.chatSendMessageUtil.build(RoomType.convert(roomType), roomMessage.getRoomId(),
                            roomMessage);
                }
            } else {
                if (message.messageID.equalsIgnoreCase(Long.toString(mSelectedMessageID))) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                    Long.parseLong(message.messageID))
                            .findFirst();
                    if (roomMessage != null) {
                        RoomType roomType = realm.where(RealmRoom.class)
                                .equalTo(RealmRoomFields.ID, roomMessage.getRoomId())
                                .findFirst()
                                .getType();
                        G.chatSendMessageUtil.build(RoomType.convert(roomType),
                                roomMessage.getRoomId(), roomMessage);
                    }
                    break;
                }
            }
        }

        realm.close();
    }

    @Override
    public void resendMessage() {
        resend(false);
    }

    @Override
    public void resendAllMessages() {
        resend(true);
    }
}
