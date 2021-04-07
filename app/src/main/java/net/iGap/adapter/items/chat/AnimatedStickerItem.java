/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieListener;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.repository.StickerRepository;
import net.iGap.structs.MessageObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class AnimatedStickerItem extends AbstractMessage<AnimatedStickerItem, AnimatedStickerItem.ViewHolder> {

    private String TAG = "abbasiStickerItem";

    public AnimatedStickerItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.animated_sticker_layout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.stickerCell.setTag(attachment.token);
        super.bindView(holder, payloads);

        holder.stickerCell.setOnLongClickListener(getLongClickPerform(holder));
        holder.progress.setVisibility(View.GONE);

//        holder.stickerCell.setMessage(structMessage);

        holder.getChatBloke().setBackgroundResource(0);

        holder.stickerCell.setOnClickListener(v -> {

            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
            } else {
                if (messageObject.status == MessageObject.STATUS_SENDING) {
                    return;
                }
                if (messageObject.status == MessageObject.STATUS_FAILED) {
                    messageClickListener.onFailedMessageClick(v, messageObject, holder.getAdapterPosition());
                } else {
                    messageClickListener.onOpenClick(v, messageObject, holder.getAdapterPosition());
                }
            }
        });

        String path = StickerRepository.getInstance().getStickerPath(attachment.token, attachment.name);
        if (new File(path).exists()) {
            holder.stickerCell.playAnimation(path);
        } else {
            IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(attachment.cacheId, attachment.token, attachment.size, path));
        }
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected MessageProgress progress;
        private AnimatedStickerCell stickerCell;

        public ViewHolder(View view) {
            super(view);

            stickerCell = new AnimatedStickerCell(getContext());

            stickerCell.setFailureListener(new LottieListener<Throwable>() {
                @Override
                public void onResult(Throwable result) {

                }
            });

            progress = getProgressBar(view.getContext(), 0);

            getContentBloke().addView(stickerCell, LayoutCreator.createFrame(200, 200));
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return stickerCell;
        }
    }
}
