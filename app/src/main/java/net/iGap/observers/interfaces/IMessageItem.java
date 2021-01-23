/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.observers.interfaces;

import android.view.View;

import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.structs.MessageObject;

public interface IMessageItem {
    void onOpenClick(View view, MessageObject message, int pos);

    void onContainerClick(View view, MessageObject message, int pos);

    void onSenderAvatarClick(View view, MessageObject message, int pos);

    void onUploadOrCompressCancel(View view, MessageObject message, int pos);

    void onFailedMessageClick(View view, MessageObject message, int pos);

    void onReplyClick(MessageObject replyMessage);

    void onForwardClick(MessageObject message);

    void onForwardFromCloudClick(MessageObject message);

    void onDownloadAllEqualCashId(String token, String messageId);

    void onItemShowingMessageId(MessageObject messageInfo);

    void onPlayMusic(String messageId);

    boolean getShowVoteChannel();

    void sendFromBot(Object realmRoomMessage);

    void onOpenLinkDialog(String url);

    void onActiveGiftStickerClick(StructIGSticker structIGSticker, int mode, MessageObject structMessage);

    void onVoteClick(MessageObject messageObject, int reactionValue);
    void onGetVote(MessageObject messageObject);
}
