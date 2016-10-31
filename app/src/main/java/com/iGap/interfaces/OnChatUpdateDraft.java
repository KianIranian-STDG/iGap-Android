package com.iGap.interfaces;

public interface OnChatUpdateDraft {
    void onChatUpdateDraft(long roomId, String message, long replyToMessageId);
}
