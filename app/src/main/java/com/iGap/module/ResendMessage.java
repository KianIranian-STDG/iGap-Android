package com.iGap.module;

import android.content.Context;
import com.iGap.G;
import com.iGap.activities.ActivityChat;
import com.iGap.interfaces.IResendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/30/2016.
 */

public class ResendMessage implements IResendMessage {
    private List<StructMessageInfo> mMessages;
    private IResendMessage mListener;
    private long mSelectedMessageID;

    public ResendMessage(Context context, IResendMessage listener, long selectedMessageID, List<StructMessageInfo> messages) {
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
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID)).findFirst();
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
                        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID)).findFirst();
                        if (roomMessage != null) {
                            roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                        }
                    } else {
                        if (message.messageID.equalsIgnoreCase(Long.toString(mSelectedMessageID))) {
                            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID)).findFirst();
                            if (roomMessage != null) {
                                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
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
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID)).findFirst();
                if (roomMessage != null) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                    if (realmRoom != null) {
                        if (roomMessage.getAttachment() == null) {
                            ProtoGlobal.Room.Type roomType = realmRoom.getType();
                            G.chatSendMessageUtil.build(roomType, roomMessage.getRoomId(), roomMessage);
                        } else {
                            new ActivityChat.UploadTask().execute(roomMessage.getAttachment().getLocalFilePath(), roomMessage.getMessageId(), roomMessage.getMessageType(), roomMessage.getRoomId(), roomMessage.getMessage());
                        }
                    }
                }
            } else {
                if (message.messageID.equalsIgnoreCase(Long.toString(mSelectedMessageID))) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID)).findFirst();
                    if (roomMessage != null) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                        if (realmRoom != null) {
                            ProtoGlobal.Room.Type roomType = realmRoom.getType();
                            if (roomMessage.getAttachment() == null) {
                                G.chatSendMessageUtil.build(roomType, roomMessage.getRoomId(), roomMessage);
                            } else {
                                new ActivityChat.UploadTask().execute(roomMessage.getAttachment().getLocalFilePath(), roomMessage.getMessageId(), roomMessage.getMessageType(), roomMessage.getRoomId(), roomMessage.getMessage());
                            }
                        }
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
