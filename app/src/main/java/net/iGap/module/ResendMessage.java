/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.app.Activity;
import android.content.Context;

import net.iGap.G;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.fragments.FragmentChat;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.network.RequestManager;
import net.iGap.observers.interfaces.IResendMessage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.structs.MessageObject;

import java.util.List;

import io.realm.Realm;

public class ResendMessage implements IResendMessage {
    private List<MessageObject> mMessages;
    private IResendMessage mListener;
    private long mSelectedMessageID;

    public ResendMessage(Context context, IResendMessage listener, long selectedMessageID, List<MessageObject> messages) {
        this.mMessages = messages;
        this.mListener = listener;
        this.mSelectedMessageID = selectedMessageID;
        boolean hasTextForCopy = checkHasMessageForCopy(messages);
        if (!((Activity) context).isFinishing()) {
            AppUtils.buildResendDialog(context, messages.size(), hasTextForCopy, this).show();
        }

    }

    private boolean checkHasMessageForCopy(List<MessageObject> messages) {
        for (MessageObject message : messages) {
            if (message == null) continue;
            String msgText = message.forwardedMessage != null ? message.forwardedMessage.message: message.message;
            if (msgText != null && !msgText.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<MessageObject> getMessages() {
        return mMessages;
    }

    @Override
    public void deleteMessage() {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                for (MessageObject message : mMessages) {
                    if (message != null) {
                        if (mSelectedMessageID == message.id) {
//                            RealmRoomMessage.deleteMessage(realm1, message.realmRoomMessage.getMessageId());
                            MessageDataStorage.getInstance(AccountManager.selectedAccount).deleteMessage(message.roomId, message.id, true);
                            break;
                        }
                    }
                }
            }, () -> mListener.deleteMessage());
        });
    }

    private void resend(final boolean all) {

        if (!RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            return;
        }
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (MessageObject message : mMessages) {
                        if (all) {
                            RealmRoomMessage.setStatus(realm, message.id, ProtoGlobal.RoomMessageStatus.SENDING);
                        } else {
                            if (message.id == mSelectedMessageID) {
                                RealmRoomMessage.setStatus(realm, message.id, ProtoGlobal.RoomMessageStatus.SENDING);
                                break;
                            }
                        }

                    }
                }
            }, () -> {
                if (all) {
                    mListener.resendAllMessages();
                } else {
                    mListener.resendMessage();
                }

                for (int i = 0; i < mMessages.size(); i++) {
                    final int j = i;
                    if (all) {
                        if (FragmentChat.allowResendMessage(mMessages.get(j).id)) {
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DbManager.getInstance().doRealmTask(realm1 -> {
                                        RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo("messageId", mMessages.get(j).id).findFirst();
                                        if (roomMessage != null) {
                                            RealmRoom realmRoom = realm1.where(RealmRoom.class).equalTo("id", roomMessage.getRoomId()).findFirst();
                                            if (realmRoom != null) {
                                                if (roomMessage.getAttachment() == null) {
                                                    ProtoGlobal.Room.Type roomType = realmRoom.getType();
                                                    ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(roomType, roomMessage.getRoomId(), roomMessage);
                                                } else {
                                                    if (roomMessage.getRealmAdditional() != null && roomMessage.getRealmAdditional().getAdditionalType() == 4) {
                                                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(realmRoom.getType(), roomMessage.getRoomId(), roomMessage);
                                                    } else {
                                                        UploadObject fileObject = UploadObject.createForMessage(roomMessage, realmRoom.getType());

                                                        if (fileObject != null) {
                                                            Uploader.getInstance().upload(fileObject);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }, 1000 * j);
                        }
                    } else {
                        if (mMessages.get(j).id == mSelectedMessageID) {
                            if (FragmentChat.allowResendMessage(mSelectedMessageID)) {
                                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", mMessages.get(j).id).findFirst();
                                if (roomMessage != null) {
                                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomMessage.getRoomId()).findFirst();
                                    if (realmRoom != null) {
                                        ProtoGlobal.Room.Type roomType = realmRoom.getType();
                                        if (roomMessage.getAttachment() == null) {
                                            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(roomType, roomMessage.getRoomId(), roomMessage);
                                        } else {
                                            if (roomMessage.getRealmAdditional() != null && roomMessage.getRealmAdditional().getAdditionalType() == 4) {
                                                ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(realmRoom.getType(), roomMessage.getRoomId(), roomMessage);
                                            } else {
                                                UploadObject fileObject = UploadObject.createForMessage(roomMessage, realmRoom.getType());

                                                if (fileObject != null) {
                                                    Uploader.getInstance().upload(fileObject);
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            });
        });
    }

    @Override
    public void resendMessage() {
        resend(false);
    }

    @Override
    public void resendAllMessages() {
        resend(true);
    }

    @Override
    public void copyMessage() {
        mListener.copyMessage();
    }
}
