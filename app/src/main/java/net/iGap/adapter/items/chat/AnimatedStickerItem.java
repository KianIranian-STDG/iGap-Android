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

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.LayoutCreator;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class AnimatedStickerItem extends AbstractMessage<AnimatedStickerItem, AnimatedStickerItem.ViewHolder> {

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
        super.bindView(holder, payloads);

        holder.image.setOnLongClickListener(getLongClickPerform(holder));
        holder.progress.setVisibility(View.GONE);

        holder.stickerCell.setMessage(structMessage);

        holder.getChatBloke().setBackgroundResource(0);

        holder.image.setOnClickListener(v -> {

            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
            } else {
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(v, structMessage, holder.getAdapterPosition());
                } else {
                    messageClickListener.onOpenClick(v, structMessage, holder.getAdapterPosition());
                }
            }
        });


    }


    @Override
    public void onLoadThumbnailFromLocal(ViewHolder holder, String tag, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);
        Log.i("abbasiAnimation", "onLoadThumbnailFromLocal: " + localPath);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected ReserveSpaceRoundedImageView image;
        protected MessageProgress progress;

        private AnimatedStickerCell stickerCell;

        public ViewHolder(View view) {
            super(view);

            FrameLayout frameLayout = new FrameLayout(getContext());

            stickerCell = new AnimatedStickerCell(getContext());

//            frameLayout.setBackgroundColor(getResources().getColor(R.color.red));
            image = new ReserveSpaceRoundedImageView(getContext());

            progress = getProgressBar(view.getContext(), 0);

            stickerCell.setOnClickListener(v -> {
                if (stickerCell.animatedLoaded)
                    stickerCell.playAnimation();
            });

            frameLayout.addView(stickerCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

            getContentBloke().addView(frameLayout, LayoutCreator.createFrame(200, 200));

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
