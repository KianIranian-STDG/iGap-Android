package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AppUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

import io.github.meness.emoji.EmojiTextView;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VoiceItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_voice;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        // TODO: 9/28/2016 [Alireza]
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.duration.setText(AppUtils.humanReadableDuration(mMessage.forwardedFrom.getAttachment().getDuration()));
            }
            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                holder.duration.setText(AppUtils.humanReadableDuration(mMessage.attachment.duration));
            }
            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }

        AppUtils.rightFileThumbnailIcon(holder.thumbnail, mMessage.messageType, null);
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView thumbnail;
        protected TextView elapsedTime;
        protected TextView duration;
        protected MediaController mediaController;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            elapsedTime = (TextView) view.findViewById(R.id.elapsedTime);
            duration = (TextView) view.findViewById(R.id.duration);
            mediaController = (MediaController) view.findViewById(R.id.mediaController);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
        }
    }
}
