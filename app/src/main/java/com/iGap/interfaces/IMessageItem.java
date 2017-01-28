package com.iGap.interfaces;

import android.view.View;
import com.iGap.module.StructMessageInfo;
import com.iGap.realm.RealmRoomMessage;

public interface IMessageItem {
    /**
     * open means open for files and play for videos
     */
    void onOpenClick(View view, StructMessageInfo message, int pos);

    void onContainerClick(View view, StructMessageInfo message, int pos);

    void onSenderAvatarClick(View view, StructMessageInfo message, int pos);

    void onUploadCancel(View view, StructMessageInfo message, int pos);

    void onFailedMessageClick(View view, StructMessageInfo message, int pos);

    void onReplyClick(RealmRoomMessage replyMessage);

    void onDownloadAllEqualCashId(String token);

    //void onVoteClick(StructMessageInfo message, String vote, ProtoGlobal.RoomMessageReaction reaction);
}
