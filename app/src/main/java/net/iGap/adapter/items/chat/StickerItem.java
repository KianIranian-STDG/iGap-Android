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

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.repository.StickerRepository;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.List;

import static net.iGap.module.AndroidUtils.suitablePath;

public class StickerItem extends AbstractMessage<StickerItem, StickerItem.ViewHolder> {

    public StickerItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.sticker_layout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.image.setTag(attachment.token);
        super.bindView(holder, payloads);

        holder.getChatBloke().setBackgroundResource(0);

        holder.image.setOnClickListener(v -> {

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
            G.imageLoader.displayImage(suitablePath(path), holder.image);
        } else {
            EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                if (id == EventManager.STICKER_DOWNLOAD) {

                    String filePath = (String) message[0];
                    String fileToken = (String) message[1];

                    if (holder.image.getTag().equals(fileToken)) {
                        G.handler.post(() -> {
                            G.imageLoader.displayImage(suitablePath(filePath), holder.image);
                        });
                    }
                }
            });

            IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(attachment.cacheId, attachment.token, attachment.size, path));
        }

        holder.image.setOnLongClickListener(getLongClickPerform(holder));
        holder.progress.setVisibility(View.GONE);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected AppCompatImageView image;
        protected MessageProgress progress;

        public ViewHolder(View view) {
            super(view);

            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            image = new AppCompatImageView(getContext());
            image.setId(R.id.thumbnail);
            getContentBloke().addView(frameLayout);
            frameLayout.addView(image, LayoutCreator.createFrame(200, 200, Gravity.CENTER));

            progress = getProgressBar(view.getContext(), 0);
            frameLayout.addView(progress, new FrameLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp60), Gravity.CENTER));

        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return image;
        }
    }
}
