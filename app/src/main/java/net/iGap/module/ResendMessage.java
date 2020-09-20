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
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.upload.UploadManager;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.network.RequestManager;
import net.iGap.observers.interfaces.IResendMessage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.util.List;

import io.realm.Realm;

public class ResendMessage implements IResendMessage {
    private List<StructMessageInfo> mMessages;
    private IResendMessage mListener;
    private long mSelectedMessageID;

    public ResendMessage(Context context, IResendMessage listener, long selectedMessageID, List<StructMessageInfo> messages) {
        this.mMessages = messages;
        this.mListener = listener;
        this.mSelectedMessageID = selectedMessageID;
        boolean hasTextForCopy = checkHasMessageForCopy(messages);
        if (!((Activity) context).isFinishing()) {
            AppUtils.buildResendDialog(context, messages.size(), hasTextForCopy, this).show();
        }

    }

    private boolean checkHasMessageForCopy(List<StructMessageInfo> messages) {
        for (StructMessageInfo message : messages) {
            if (message.realmRoomMessage == null) continue;
            String msgText = message.realmRoomMessage.getForwardMessage() != null ? message.realmRoomMessage.getForwardMessage().getMessage() : message.realmRoomMessage.getMessage();
            if (msgText != null && !msgText.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<StructMessageInfo> getMessages() {
        return mMessages;
    }

    @Override
    public void deleteMessage() {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(realm1 -> {
                for (StructMessageInfo message : mMessages) {
                    if (message.realmRoomMessage != null) {
                        if (mSelectedMessageID == message.realmRoomMessage.getMessageId()) {
                            RealmRoomMessage.deleteMessage(realm1, message.realmRoomMessage.getMessageId());
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
                    for (StructMessageInfo message : mMessages) {
                        if (all) {
                            RealmRoomMessage.setStatus(realm, message.realmRoomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SENDING);
                        } else {
                            if (message.realmRoomMessage.getMessageId() == mSelectedMessageID) {
                                RealmRoomMessage.setStatus(realm, message.realmRoomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SENDING);
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
                        if (FragmentChat.allowResendMessage(mMessages.get(j).realmRoomMessage.getMessageId())) {
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DbManager.getInstance().doRealmTask(realm1 -> {
                                        RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, mMessages.get(j).realmRoomMessage.getMessageId()).findFirst();
                                        if (roomMessage != null) {
                                            RealmRoom realmRoom = realm1.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                                            if (realmRoom != null) {
                                                if (roomMessage.getAttachment() == null) {
                                                    ProtoGlobal.Room.Type roomType = realmRoom.getType();
                                                    G.chatSendMessageUtil.build(roomType, roomMessage.getRoomId(), roomMessage);
                                                } else {
                                                    if (roomMessage.getRealmAdditional() != null && roomMessage.getRealmAdditional().getAdditionalType() == 4) {
                                                        new ChatSendMessageUtil().build(realmRoom.getType(), roomMessage.getRoomId(), roomMessage);
                                                    } else {
                                                        UploadManager.getInstance().uploadMessageAndSend(realmRoom.getType(), roomMessage);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }, 1000 * j);
                        }
                    } else {
                        if (mMessages.get(j).realmRoomMessage.getMessageId() == mSelectedMessageID) {
                            if (FragmentChat.allowResendMessage(mSelectedMessageID)) {
                                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, mMessages.get(j).realmRoomMessage.getMessageId()).findFirst();
                                if (roomMessage != null) {
                                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                                    if (realmRoom != null) {
                                        ProtoGlobal.Room.Type roomType = realmRoom.getType();
                                        if (roomMessage.getAttachment() == null) {
                                            G.chatSendMessageUtil.build(roomType, roomMessage.getRoomId(), roomMessage);
                                        } else {
                                            if (roomMessage.getRealmAdditional() != null && roomMessage.getRealmAdditional().getAdditionalType() == 4) {
                                                new ChatSendMessageUtil().build(realmRoom.getType(), roomMessage.getRoomId(), roomMessage);
                                            } else {
                                                UploadManager.getInstance().uploadMessageAndSend(realmRoom.getType(), roomMessage);
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
