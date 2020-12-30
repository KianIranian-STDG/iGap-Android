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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.giftStickers.giftCardDetail.MainGiftStickerCardFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.Theme;
import net.iGap.module.customView.ProgressButton;
import net.iGap.module.customView.StickerView;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.proto.ProtoGlobal;
import net.iGap.repository.StickerRepository;
import net.iGap.structs.MessageObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

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
                if (messageObject.status == MessageObject.STATUS_SENDING) {
                    return;
                }
                if (messageObject.status == MessageObject.STATUS_FAILED) {
                    messageClickListener.onFailedMessageClick(v, messageObject, holder.getAdapterPosition());
                }
            }
        });

        try {
            StructIGSticker structIGSticker = holder.structIGSticker = new Gson().fromJson(additional.data, StructIGSticker.class);
            if (structIGSticker != null && structIGSticker.getPath() != null) {
                holder.stickerView.loadSticker(structIGSticker);
            } else {
                String path = StickerRepository.getInstance().getStickerPath(attachment.token, attachment.name);
                holder.stickerView.loadSticker(attachment.token, path, attachment.name, String.valueOf(messageObject.id), attachment.size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (messageObject.isSenderMe() && !mAdapter.roomIsMyCloud()) {
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
                    StickerRepository.getInstance().getCardStatus(structIGSticker.getGiftId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new IGSingleObserver<StructIGGiftSticker>(mAdapter.getCompositeDisposable()) {
                                @Override
                                public void onSuccess(StructIGGiftSticker giftSticker) {

                                    if (giftSticker.isActive() && giftSticker.isCardOwner()) {
                                        messageClickListener.onActiveGiftStickerClick(structIGSticker, MainGiftStickerCardFragment.ACTIVE_BY_ME, messageObject);
                                    } else if (giftSticker.isForward()) {
                                        Toast.makeText(getContext(), R.string.gift_carde_sended, Toast.LENGTH_SHORT).show();
                                    } else if (giftSticker.isActive()) {
                                        Toast.makeText(getContext(), R.string.gift_card_already_in_use, Toast.LENGTH_SHORT).show();
                                    } else {
                                        messageClickListener.onActiveGiftStickerClick(structIGSticker, giftSticker.isForward() ? MainGiftStickerCardFragment.ACTIVE_CARD_WHIT_OUT_FORWARD : MainGiftStickerCardFragment.ACTIVE_CARD_WHIT_FORWARD, messageObject);
                                    }

                                    progressButton.changeProgressTo(View.GONE);
                                    progressButton.setText(itemView.getContext().getResources().getString(R.string.gift_sticker_visit));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    progressButton.changeProgressTo(View.GONE);
                                    progressButton.setText(itemView.getContext().getResources().getString(R.string.gift_sticker_visit));
                                }
                            });
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