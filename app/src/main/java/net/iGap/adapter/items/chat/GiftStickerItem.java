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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.LayoutCreator;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.proto.ProtoGlobal;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.rx.IGSingleObserver;
import net.iGap.view.StickerView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

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

        try {
            StructIGSticker structIGSticker = holder.structIGSticker = new Gson().fromJson(structMessage.getAdditional().getAdditionalData(), StructIGSticker.class);
            if (structIGSticker != null) {
                holder.image.loadSticker(structIGSticker);

                if (structIGSticker.getGiftId() != null)
                    StickerRepository.getInstance().getCardStatus(structIGSticker.getGiftId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new IGSingleObserver<StructIGGiftSticker>(new CompositeDisposable()) {
                                @Override
                                public void onSuccess(StructIGGiftSticker giftSticker) {
                                    Log.i("GiftStickerItem", "onSuccess: " + giftSticker.isActive());
                                    holder.visitBtn.setVisibility(giftSticker.isActive() ? View.GONE : View.VISIBLE);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }
                            });
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }


        holder.image.setOnLongClickListener(getLongClickPerform(holder));
        holder.progress.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    protected class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected StickerView image;
        protected MessageProgress progress;
        private Button visitBtn;
        private Button addToArchiveBtn;
        private AppCompatTextView cardNumber1;
        private AppCompatTextView cardNumber2;
        private AppCompatTextView cardNumber3;
        private AppCompatTextView cardNumber4;
        private AppCompatTextView cvv2Tv;
        private AppCompatTextView expireTimeTv;
        private StructIGSticker structIGSticker;


        public ViewHolder(View view) {
            super(view);

            LinearLayout contentOne = new LinearLayout(getContext());
            contentOne.setVisibility(View.VISIBLE);
            contentOne.setOrientation(LinearLayout.VERTICAL);

            image = new StickerView(getContext()) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(heightMeasureSpec, heightMeasureSpec);
                }
            };

            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(LayoutCreator.dp(250), LayoutCreator.dp(250));
            imageLayoutParams.gravity = Gravity.CENTER;
            image.setLayoutParams(imageLayoutParams);
            image.setId(R.id.thumbnail);
            image.setPadding(LayoutCreator.dp(25), LayoutCreator.dp(20), LayoutCreator.dp(25), LayoutCreator.dp(20));
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

//            buttonLayouts.addView(addToArchiveBtn, LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER, 0, 6, 3, 4));
            buttonLayouts.addView(visitBtn, LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER, 3, 6, 0, 4));

            visitBtn.setBackgroundResource(theme.getCardToCardButtonBackground(visitBtn.getContext()));
            addToArchiveBtn.setBackgroundResource(theme.getCardToCardButtonBackground(addToArchiveBtn.getContext()));

            progress = getProgressBar(view.getContext(), 0);
            contentOne.addView(progress);

            LinearLayout contentTwo = new LinearLayout(getContext());
            contentTwo.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
            contentTwo.setOrientation(LinearLayout.VERTICAL);

            AppCompatTextView giftCardTv = new AppCompatTextView(getContext());
            LinearLayout.LayoutParams giftCardTvLayoutParams = new LinearLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT);
            giftCardTvLayoutParams.gravity = Gravity.CENTER;
            giftCardTv.setLayoutParams(giftCardTvLayoutParams);

            giftCardTv.setText(R.string.gift_cart_title);
            giftCardTv.setGravity(Gravity.CENTER);
            giftCardTv.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
            setTextSize(giftCardTv, R.dimen.largeTextSize);
            giftCardTv.setPadding(dpToPx(2), dpToPx(15), dpToPx(2), dpToPx(15));
            giftCardTv.setSingleLine(true);
            setTypeFace(giftCardTv);

            LinearLayout cardNumberLayout = new LinearLayout(getContext());
            buttonLayouts.setOrientation(LinearLayout.HORIZONTAL);

            cardNumber1 = generateCardNumberTv();
            cardNumber2 = generateCardNumberTv();
            cardNumber3 = generateCardNumberTv();
            cardNumber4 = generateCardNumberTv();

            cardNumberLayout.addView(cardNumber1);
            cardNumberLayout.addView(cardNumber2);
            cardNumberLayout.addView(cardNumber3);
            cardNumberLayout.addView(cardNumber4);

            LinearLayout bottomLayout = new LinearLayout(getContext());
            buttonLayouts.setOrientation(LinearLayout.HORIZONTAL);


            cvv2Tv = new AppCompatTextView(getContext());
            cvv2Tv.setLayoutParams(LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER));
            cvv2Tv.setText("----");
            cvv2Tv.setGravity(Gravity.LEFT);
            setTextSize(cvv2Tv, R.dimen.largeTextSize);
            cvv2Tv.setPadding(dpToPx(15), 0, dpToPx(2), 0);
            cvv2Tv.setSingleLine(true);
            setTypeFace(cvv2Tv);

            expireTimeTv = new AppCompatTextView(getContext());
            expireTimeTv.setLayoutParams(LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER));
            expireTimeTv.setText("----");
            expireTimeTv.setGravity(Gravity.RIGHT);
            setTextSize(expireTimeTv, R.dimen.largeTextSize);
            expireTimeTv.setPadding(dpToPx(2), 0, dpToPx(15), 0);
            expireTimeTv.setSingleLine(true);
            setTypeFace(expireTimeTv);

            bottomLayout.addView(cvv2Tv);
            bottomLayout.addView(expireTimeTv);

            contentTwo.addView(giftCardTv);
            contentTwo.addView(cardNumberLayout);
            contentTwo.addView(bottomLayout);
            contentTwo.setVisibility(View.GONE);

            contentTwo.setBackgroundResource(theme.getCardToCardButtonBackground(contentTwo.getContext()));

            visitBtn.setOnClickListener(v -> messageClickListener.onActiveGiftStickerClick(structIGSticker));

            getContentBloke().addView(contentTwo);
        }

        private AppCompatTextView generateCardNumberTv() {
            AppCompatTextView cardNumber = new AppCompatTextView(getContext());
            cardNumber.setLayoutParams(LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, 1, Gravity.CENTER));
            cardNumber.setText("----");
            cardNumber.setGravity(Gravity.CENTER);
            setTextSize(cardNumber, R.dimen.largeTextSize);
            cardNumber.setPadding(dpToPx(2), 0, dpToPx(2), 0);
            cardNumber.setSingleLine(true);
            setTypeFace(cardNumber);

            return cardNumber;
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