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

import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.util.List;

import static net.iGap.module.AndroidUtils.suitablePath;

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
        holder.image.setTag(structMessage.getAttachment().getToken());
        super.bindView(holder, payloads);

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

        String path = HelperDownloadSticker.downloadStickerPath(structMessage.getAttachment().getToken(), structMessage.getAttachment().getName());
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

            IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(structMessage.getAttachment().getCacheId(),
                    structMessage.getAttachment().getToken(), structMessage.getAttachment().getSize(), path));
        }

        holder.image.setOnLongClickListener(getLongClickPerform(holder));
        holder.progress.setVisibility(View.GONE);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected AppCompatImageView image;
        protected MessageProgress progress;
        protected Button visitBtn;
        protected Button addToArchiveBtn;


        public ViewHolder(View view) {
            super(view);

            LinearLayout contentOne = new LinearLayout(getContext());
            contentOne.setOrientation(LinearLayout.VERTICAL);

            image = new AppCompatImageView(getContext());
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageLayoutParams.gravity = Gravity.CENTER;
            image.setLayoutParams(imageLayoutParams);
            image.setId(R.id.thumbnail);
            contentOne.addView(image);
            getContentBloke().addView(contentOne);

            LinearLayout buttonLayouts = new LinearLayout(getContext());
            buttonLayouts.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayouts.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentOne.addView(buttonLayouts);
            visitBtn = new Button(getContext());
            addToArchiveBtn = new Button(getContext());

            visitBtn.setId(R.id.cardToCard_button);
            visitBtn.setMaxLines(1);
            visitBtn.setText(getResources().getString(R.string.gift_sticker_visit));
            setTextSize(visitBtn, R.dimen.standardTextSize);
            setTypeFace(visitBtn);
            visitBtn.setTextColor(getColor(R.color.white));

            addToArchiveBtn.setId(R.id.cardToCard_button);
            addToArchiveBtn.setMaxLines(1);
            addToArchiveBtn.setText(getResources().getString(R.string.gift_sticker_add_archive));
            setTextSize(addToArchiveBtn, R.dimen.standardTextSize);
            setTypeFace(addToArchiveBtn);
            addToArchiveBtn.setTextColor(getColor(R.color.white));

            buttonLayouts.addView(addToArchiveBtn, LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT,1, Gravity.CENTER, 0, 5 , 5, 0));
            buttonLayouts.addView(visitBtn, LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT,1, Gravity.CENTER, 5, 5 , 5, 0));

            visitBtn.setBackgroundResource(theme.getCardToCardButtonBackground(visitBtn.getContext()));
            addToArchiveBtn.setBackgroundResource(theme.getCardToCardButtonBackground(addToArchiveBtn.getContext()));

            progress = getProgressBar(view.getContext(), 0);
            contentOne.addView(progress);


            LinearLayout contentTwo = new LinearLayout(getContext());
            contentTwo.setVisibility(View.GONE);
            contentTwo.setOrientation(LinearLayout.VERTICAL);

            AppCompatTextView giftCardTv = new AppCompatTextView(getContext());
            giftCardTv.setText(R.string.gift_cart_title);
            giftCardTv.setGravity(Gravity.CENTER);
            setTextSize(giftCardTv, R.dimen.largeTextSize);
            giftCardTv.setPadding(dpToPx(2), 0, dpToPx(2), 0);
            giftCardTv.setSingleLine(true);
            setTypeFace(giftCardTv);

            LinearLayout cardNumberLayout = new LinearLayout(getContext());
            buttonLayouts.setOrientation(LinearLayout.HORIZONTAL);

            contentTwo.addView(giftCardTv);
            cardNumberLayout.addView(cardNumberLayout);

            getContentBloke().addView(contentTwo);

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
