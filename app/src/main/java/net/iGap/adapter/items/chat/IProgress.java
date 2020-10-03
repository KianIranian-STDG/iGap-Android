package net.iGap.adapter.items.chat;

import android.widget.TextView;

import net.iGap.messageprogress.MessageProgress;

public interface IProgress {

    MessageProgress getProgress();

    default TextView getProgressTextView() {
        return null;
    }

    default String getTempTextView() {
        return null;
    }
}
