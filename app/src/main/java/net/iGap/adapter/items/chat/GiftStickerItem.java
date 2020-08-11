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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.LayoutCreator;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.Theme;
import net.iGap.module.customView.ProgressButton;
import net.iGap.module.customView.StickerView;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.repository.StickerRepository;

import java.util.List;

public class GiftStickerItem extends AbstractMessage<GiftStickerItem, GiftStickerItem.ViewHolder> {

    public GiftStickerItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.sticker_gif_card_layout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.getChatBloke().setBackgroundResource(0);
        holder.stickerView.setOnClickListener(v -> {

            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
            } else {
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(v, structMessage, holder.getAdapterPosition());
                }
            }
        });

        try {
            StructIGSticker structIGSticker = holder.structIGSticker = new Gson().fromJson(structMessage.getAdditional().getAdditionalData(), StructIGSticker.class);
            if (structIGSticker != null && structIGSticker.getPath() != null) {
                holder.stickerView.loadSticker(structIGSticker);
            } else {
                String path = StickerRepository.getInstance().getStickerPath(structMessage.getAttachment().getToken(), structMessage.getAttachment().getName());
                holder.stickerView.loadSticker(structMessage.getAttachment().getToken(), path, structMessage.getAttachment().getName(), String.valueOf(structMessage.getAttachment().getId()), structMessage.getAttachment().getSize());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (structMessage.isSenderMe() && !mAdapter.roomIsMyCloud()) {
            holder.progressButton.setVisibility(View.GONE);
        } else {
            holder.progressButton.setVisibility(View.VISIBLE);
        }

        holder.progress.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    protected class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected StickerView stickerView;
        protected MessageProgress progress;
        private StructIGSticker structIGSticker;
        private ProgressButton progressButton;

        public ViewHolder(View view) {
            super(view);

            FrameLayout rootView = new FrameLayout(view.getContext());

            stickerView = new StickerView(getContext()) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(heightMeasureSpec, heightMeasureSpec);
                }
            };

            stickerView.setId(R.id.thumbnail);

            rootView.addView(stickerView, LayoutCreator.createFrame(220, 220, Gravity.TOP, 8, 0, 8, 50));

            progressButton = new ProgressButton(itemView.getContext());
            progressButton.setBackgroundColor(Theme.getInstance().getAccentColor(itemView.getContext()));
            progressButton.setText(itemView.getContext().getResources().getString(R.string.gift_sticker_visit));
            progressButton.setRadius(6);
            rootView.addView(progressButton, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 42, Gravity.BOTTOM, 0, 0, 0, 0));

            progress = getProgressBar(view.getContext(), 0);

            progressButton.setOnClickListener(v -> {
                if (FragmentChat.isInSelectionMode) {
                    itemView.performLongClick();
                } else if (structIGSticker != null) {
                    progressButton.changeProgressTo(View.VISIBLE);
//    private resource
                }
            });

            getContentBloke().addView(rootView);
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return new AppCompatImageView(getContext());
        }
    }
}