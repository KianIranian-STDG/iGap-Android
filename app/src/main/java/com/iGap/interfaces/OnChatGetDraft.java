package com.iGap.interfaces;

public interface OnChatGetDraft {
    void onChatGetDraft(long roomId, String message, long replyToMessageId);
}
